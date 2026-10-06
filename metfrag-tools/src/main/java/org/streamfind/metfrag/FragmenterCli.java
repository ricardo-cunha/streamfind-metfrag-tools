// SPDX-License-Identifier: GPL-3.0-only
package org.streamfind.metfrag;

import de.ipbhalle.metfraglib.candidate.TopDownPrecursorCandidate;
import de.ipbhalle.metfraglib.FastBitArray;
import de.ipbhalle.metfraglib.additionals.NeutralLosses;
import de.ipbhalle.metfraglib.fragment.AbstractTopDownBitArrayFragment;
import de.ipbhalle.metfraglib.fragment.BitArrayNeutralLoss;
import de.ipbhalle.metfraglib.fragment.DefaultBitArrayFragment;
import de.ipbhalle.metfraglib.fragmenter.TopDownNeutralLossFragmenter;
import de.ipbhalle.metfraglib.interfaces.IFragment;
import de.ipbhalle.metfraglib.interfaces.IMolecularFormula;
import de.ipbhalle.metfraglib.parameter.Constants;
import de.ipbhalle.metfraglib.parameter.VariableNames;
import de.ipbhalle.metfraglib.precursor.AbstractTopDownBitArrayPrecursor;
import de.ipbhalle.metfraglib.settings.Settings;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

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

        ExposedNeutralLossFragmenter fragmenter = new ExposedNeutralLossFragmenter(settings);
        AbstractTopDownBitArrayFragment root = ((AbstractTopDownBitArrayPrecursor)
                candidate.getPrecursorMolecule()).toFragment();
        fragmenter.registerRoot(root);

        List<AbstractTopDownBitArrayFragment> fragments = new ArrayList<>();
        fragments.add(root);
        Map<AbstractTopDownBitArrayFragment, Integer> parents = new IdentityHashMap<>();
        Map<AbstractTopDownBitArrayFragment, List<NeutralLossMatch>> losses = new IdentityHashMap<>();
        List<AbstractTopDownBitArrayFragment> frontier = List.of(root);
        BitArrayNeutralLoss[] knownLosses = fragmenter.neutralLossPatterns();
        for (int level = 1; level <= depth && !frontier.isEmpty(); level++) {
            List<AbstractTopDownBitArrayFragment> next = new ArrayList<>();
            for (AbstractTopDownBitArrayFragment parent : frontier) {
                ArrayList<AbstractTopDownBitArrayFragment> children = fragmenter.getFragmentsOfNextTreeDepth(parent);
                next.addAll(children);
                for (AbstractTopDownBitArrayFragment child : children) parents.put(child, parent.getID());
                recordNeutralLosses(parent, children, knownLosses, losses, candidate.getPrecursorMolecule());
            }
            fragments.addAll(next);
            frontier = next;
        }

        StringBuilder json = new StringBuilder(256 + fragments.size() * 192);
        json.append("{\"schema_version\":1,\"input_smiles\":").append(quote(smiles))
                .append(",\"requested_depth\":").append(depth)
                .append(",\"fragmentation_method\":\"metfrag_top_down_neutral_loss\"")
                .append(",\"relationship_status\":\"captured_during_depth_traversal\"")
                .append(",\"neutral_loss_status\":\"metfrag_pattern_and_atom_mask_matches\"")
                .append(",\"fragments\":[");

        for (int i = 0; i < fragments.size(); i++) {
            if (i > 0) json.append(',');
            IFragment fragment = fragments.get(i);
            IMolecularFormula formula = fragment.getMolecularFormula(candidate.getPrecursorMolecule());
            DefaultBitArrayFragment bitArrayFragment = (DefaultBitArrayFragment) fragment;
            json.append("{\"id\":").append(fragment.getID())
                    .append(",\"smiles\":").append(quote(fragment.getSmiles(candidate.getPrecursorMolecule())))
                    .append(",\"formula\":").append(quote(formula.toString()))
                    .append(",\"exact_mass\":").append(Double.toString(fragment.getMonoisotopicMass(candidate.getPrecursorMolecule())))
                    .append(",\"depth\":").append(fragment.getTreeDepth())
                    .append(",\"parent_id\":").append(parents.containsKey((AbstractTopDownBitArrayFragment) fragment)
                            ? parents.get((AbstractTopDownBitArrayFragment) fragment) : "null")
                    .append(",\"atom_indices\":").append(indices(bitArrayFragment.getAtomsFastBitArray()))
                    .append(",\"broken_bond_indices\":").append(intArray(fragment.getBrokenBondIndeces()))
                    .append(",\"neutral_losses\":");
            List<NeutralLossMatch> fragmentLosses = losses.get((AbstractTopDownBitArrayFragment) fragment);
            if (fragmentLosses == null || fragmentLosses.isEmpty()) json.append("[]");
            else {
                json.append('[');
                for (int j = 0; j < fragmentLosses.size(); j++) {
                    if (j > 0) json.append(',');
                    NeutralLossMatch loss = fragmentLosses.get(j);
                    json.append("{\"smarts_pattern\":").append(quote(loss.smarts()))
                            .append(",\"matched_atom_smiles\":").append(quote(loss.matchedAtomSmiles()))
                            .append(",\"matched_atom_formula\":").append(quote(loss.matchedAtomFormula()))
                            .append(",\"matched_atom_exact_mass\":").append(Double.toString(loss.matchedAtomMass()))
                            .append(",\"metfrag_neutral_loss_mass\":").append(Double.toString(loss.metfragMass()))
                            .append(",\"survivor_hydrogen_delta\":").append(loss.survivorHydrogenDelta()).append('}');
                }
                json.append(']');
            }
            json.append('}');
        }
        return json.append("]}").toString();
    }

    private static void recordNeutralLosses(AbstractTopDownBitArrayFragment parent,
            List<AbstractTopDownBitArrayFragment> children, BitArrayNeutralLoss[] knownLosses,
            Map<AbstractTopDownBitArrayFragment, List<NeutralLossMatch>> output,
            de.ipbhalle.metfraglib.interfaces.IMolecularStructure precursor) {
        for (AbstractTopDownBitArrayFragment child : children) {
            for (AbstractTopDownBitArrayFragment detached : children) {
                if (child == detached || !disjoint(child.getAtomsFastBitArray(), detached.getAtomsFastBitArray())
                        || !sameBits(parent.getAtomsFastBitArray(), union(child.getAtomsFastBitArray(), detached.getAtomsFastBitArray()))) continue;
                for (BitArrayNeutralLoss lossType : knownLosses) {
                    for (int i = 0; i < lossType.getNumberNeutralLosses(); i++) {
                        FastBitArray pattern = lossType.getNeutralLossAtomFastBitArray(i);
                        if (sameBits(pattern, detached.getAtomsFastBitArray())) {
                            NeutralLosses catalogue = new NeutralLosses();
                            NeutralLossMatch match = new NeutralLossMatch(catalogue.getSmartsPattern(lossType.getNeutralLossType()),
                                    detached.getSmiles(precursor),
                                    detached.getMolecularFormula(precursor).toString(),
                                    detached.getMonoisotopicMass(precursor),
                                    catalogue.getMonoisotopicMass(lossType.getNeutralLossType()),
                                    lossType.getHydrogenDifference());
                            List<NeutralLossMatch> matches = output.computeIfAbsent(child, ignored -> new ArrayList<>());
                            if (!matches.contains(match)) matches.add(match);
                        }
                    }
                }
            }
        }
    }

    private static FastBitArray union(FastBitArray left, FastBitArray right) {
        FastBitArray result = left.clone();
        for (int i = 0; i < right.getSize(); i++) if (right.get(i)) result.set(i);
        return result;
    }

    private static boolean sameBits(FastBitArray left, FastBitArray right) {
        if (left.getSize() != right.getSize()) return false;
        for (int i = 0; i < left.getSize(); i++) if (left.get(i) != right.get(i)) return false;
        return true;
    }

    private static boolean disjoint(FastBitArray left, FastBitArray right) {
        if (left.getSize() != right.getSize()) return false;
        for (int i = 0; i < left.getSize(); i++) if (left.get(i) && right.get(i)) return false;
        return true;
    }

    private record NeutralLossMatch(String smarts, String matchedAtomSmiles, String matchedAtomFormula,
            double matchedAtomMass, double metfragMass, byte survivorHydrogenDelta) {}

    private static final class ExposedNeutralLossFragmenter extends TopDownNeutralLossFragmenter {
        ExposedNeutralLossFragmenter(Settings settings) throws Exception { super(settings); }
        void registerRoot(AbstractTopDownBitArrayFragment root) { processGeneratedFragments(new AbstractTopDownBitArrayFragment[]{root}); }
        BitArrayNeutralLoss[] neutralLossPatterns() { return detectedNeutralLosses; }
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

