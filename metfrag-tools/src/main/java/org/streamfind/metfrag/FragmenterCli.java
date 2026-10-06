// SPDX-License-Identifier: GPL-3.0-only
package org.streamfind.metfrag;

import de.ipbhalle.metfraglib.candidate.TopDownPrecursorCandidate;
import de.ipbhalle.metfraglib.fragment.DefaultBitArrayFragment;
import de.ipbhalle.metfraglib.fragmenter.TopDownFragmenter;
import de.ipbhalle.metfraglib.interfaces.IFragment;
import de.ipbhalle.metfraglib.interfaces.IMolecularFormula;
import de.ipbhalle.metfraglib.list.FragmentList;
import de.ipbhalle.metfraglib.parameter.Constants;
import de.ipbhalle.metfraglib.parameter.VariableNames;
import de.ipbhalle.metfraglib.settings.Settings;

import java.util.ArrayList;
import java.util.List;

/** Small, line-oriented JSON CLI for MetFragLib top-down structural fragmentation. */
public final class FragmenterCli {
    private FragmenterCli() {}

    public static void main(String[] args) {
        try {
            Arguments arguments = Arguments.parse(args);
            System.out.println(run(arguments.smiles(), arguments.depth()));
        } catch (IllegalArgumentException e) {
            System.out.println(errorJson(e.getMessage()));
            System.exit(2);
        } catch (Exception e) {
            System.out.println(errorJson(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
            System.exit(1);
        }
    }

    static String run(String smiles, int depth) throws Exception {
        TopDownPrecursorCandidate candidate = new TopDownPrecursorCandidate("", "streamfind-input", smiles);
        candidate.setUseSmiles(true);
        candidate.initialisePrecursorCandidate();

        Settings settings = new Settings();
        settings.set(VariableNames.CANDIDATE_NAME, candidate);
        settings.set(VariableNames.MAXIMUM_TREE_DEPTH_NAME, (byte) depth);
        settings.set(VariableNames.MINIMUM_FRAGMENT_MASS_LIMIT_NAME, Constants.DEFAULT_MINIMUM_FRAGMENT_MASS_LIMIT);
        settings.set(VariableNames.MAXIMUM_NUMBER_OF_TOPDOWN_FRAGMENT_ADDED_TO_QUEUE,
                Constants.DEFAULT_MAXIMUM_NUMBER_OF_TOPDOWN_FRAGMENT_ADDED_TO_QUEUE);

        FragmentList fragments = new TopDownFragmenter(settings).generateFragments();
        StringBuilder json = new StringBuilder(256 + fragments.getNumberElements() * 192);
        json.append("{\"schema_version\":1,\"input_smiles\":").append(quote(smiles))
                .append(",\"requested_depth\":").append(depth)
                .append(",\"fragmentation_method\":\"metfrag_top_down\"")
                .append(",\"relationship_status\":\"unavailable_from_metfrag_fragment_list\"")
                .append(",\"neutral_loss_status\":\"not_exposed_by_public_fragment_result\"")
                .append(",\"fragments\":[");

        for (int i = 0; i < fragments.getNumberElements(); i++) {
            if (i > 0) json.append(',');
            IFragment fragment = fragments.getElement(i);
            IMolecularFormula formula = fragment.getMolecularFormula(candidate.getPrecursorMolecule());
            DefaultBitArrayFragment bitArrayFragment = (DefaultBitArrayFragment) fragment;
            json.append("{\"id\":").append(fragment.getID())
                    .append(",\"smiles\":").append(quote(fragment.getSmiles(candidate.getPrecursorMolecule())))
                    .append(",\"formula\":").append(quote(formula.toString()))
                    .append(",\"exact_mass\":").append(Double.toString(fragment.getMonoisotopicMass(candidate.getPrecursorMolecule())))
                    .append(",\"depth\":").append(fragment.getTreeDepth())
                    .append(",\"parent_id\":null,\"parent_link_status\":\"unavailable\"")
                    .append(",\"atom_indices\":").append(indices(bitArrayFragment.getAtomsFastBitArray()))
                    .append(",\"broken_bond_indices\":").append(intArray(fragment.getBrokenBondIndeces()))
                    .append(",\"neutral_losses\":null");
            json.append('}');
        }
        return json.append("]}").toString();
    }

    private static String indices(de.ipbhalle.metfraglib.FastBitArray bits) {
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < bits.getSize(); i++) if (bits.get(i)) values.add(i);
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) result.append(',');
            result.append(values.get(i));
        }
        return result.append(']').toString();
    }

    private static String intArray(int[] values) {
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) result.append(',');
            result.append(values[i]);
        }
        return result.append(']').toString();
    }

    private static String quote(String value) {
        if (value == null) return "null";
        StringBuilder result = new StringBuilder(value.length() + 2).append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"' -> result.append("\\\"");
                case '\\' -> result.append("\\\\");
                case '\b' -> result.append("\\b");
                case '\f' -> result.append("\\f");
                case '\n' -> result.append("\\n");
                case '\r' -> result.append("\\r");
                case '\t' -> result.append("\\t");
                default -> {
                    if (c < 0x20) result.append(String.format("\\u%04x", (int) c));
                    else result.append(c);
                }
            }
        }
        return result.append('"').toString();
    }

    private static String errorJson(String message) {
        return "{\"schema_version\":1,\"error\":" + quote(message == null ? "Unknown error" : message) + "}";
    }

    private record Arguments(String smiles, int depth) {
        static Arguments parse(String[] args) {
            if (args.length == 1 && (args[0].equals("--help") || args[0].equals("-h"))) {
                System.out.println("Usage: java -jar streamfind-metfrag-fragmenter.jar --smiles <SMILES> --depth <1..127>");
                System.exit(0);
            }
            String smiles = null;
            Integer depth = null;
            for (int i = 0; i < args.length; i++) {
                String key = args[i];
                if (i + 1 >= args.length) throw new IllegalArgumentException("Missing value for " + key);
                String value = args[++i];
                switch (key) {
                    case "--smiles" -> smiles = value;
                    case "--depth" -> {
                        try { depth = Integer.parseInt(value); }
                        catch (NumberFormatException e) { throw new IllegalArgumentException("Depth must be an integer"); }
                    }
                    default -> throw new IllegalArgumentException("Unknown option: " + key);
                }
            }
            if (smiles == null || smiles.isBlank()) throw new IllegalArgumentException("--smiles is required");
            if (depth == null || depth < 1 || depth > Byte.MAX_VALUE)
                throw new IllegalArgumentException("--depth must be between 1 and 127");
            return new Arguments(smiles, depth);
        }
    }
}

