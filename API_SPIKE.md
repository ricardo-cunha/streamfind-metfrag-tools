# MetFragLib API spike

Date: 2026-10-06

## Scope and source version

Inspected the upstream `ipb-halle/MetFragRelaunched` repository, branch `dev`, tree SHA `2c8671b4d8d29b05ca9461e1238c2e4268ce9e21`. The root project POM reports version `2.6.12-SNAPSHOT`; the README states Java 21 and Maven 3.8 for building the project.

This is a source-level API inspection. No wrapper has been built or exercised against molecules yet.

## What the API provides

- `TopDownFragmenter(Settings)` exposes `generateFragments()`, returning a `FragmentList`.
- Each `IFragment` provides `getSmiles(precursor)`, `getMolecularFormula(precursor)`, `getMonoisotopicMass(precursor)`, `getTreeDepth()`, `getID()`, and `getBrokenBondIndeces()`.
- `DefaultBitArrayFragment` exposes atom, intact-bond, and broken-bond bit arrays. The top-down precursor tracks atoms and bonds from the parsed precursor structure. This appears sufficient to map atom/bond provenance to indices in the exact input molecule, subject to validating indexing and canonicalization.
- The returned flat `FragmentList` is produced breadth-by-depth: the root is added first and generation loops from depth 1 through configured maximum depth.

## What is not reliably exposed

### Parent-child relationships

The public fragment interface contains ID and tree depth but no parent/child API. In `TopDownFragmenter`, the lines that would attach children to their precursor fragment are commented out. Therefore a flat result cannot be treated as an authoritative fragmentation tree. Parent edges might be inferred from atom/bond containment and depth in some cases, but ambiguity and duplicate fragments make these edges inferred data. The first JSON contract should label any such edges as inferred or omit them until validated.

### Neutral-loss structures

`TopDownNeutralLossFragmenter` internally detects matching neutral-loss atom masks and adapts molecular formulas during fragmentation. `BitArrayNeutralLoss` exposes a neutral-loss type, mass difference, hydrogen difference, and atom masks. However, these arrays are held in protected/internal state on the fragmenter and are not included in the returned `FragmentList`. The inspected public `IFragment` result does not expose a neutral-loss association. A later implementation could use this specialized fragmenter and an adapter, but loss provenance is not available from the ordinary returned fragment objects. Do not claim neutral-loss structures as reliable output until the exact execution path proves they can be associated with each generated fragment.

## Licensing and redistribution

The GitHub repository metadata reports no detected license, the inspected branch tree has no license/LICENCE/COPYING/NOTICE file, and the POMs inspected do not state redistribution terms. A third-party package index describes the upstream as LGPL-2.1-or-later, but that is not an authoritative grant from the copyright holder. Do not publish a shaded/fat JAR containing MetFragLib or its dependencies until the upstream maintainers clarify licensing and redistribution obligations. Keep the repository private until then.

## Next implementation checks

1. Confirm the permitted MetFrag dependency route/version and redistribution terms with the upstream maintainers.
2. Build an API adapter using a parsed SMILES, `TopDownPrecursorCandidate`, `TopDownBitArrayPrecursor`, `Settings`, and `TopDownFragmenter`; verify required settings and constructor path from source before committing to that pipeline.
3. Run representative acyclic, ring, aromatic, and heteroatom-containing molecules; validate formulas, exact masses, depths, broken-bond index mapping, duplicate behavior, and SMILES round trips.
4. Test parent-edge inference independently and report confidence/provenance in JSON.
5. Only bundle dependencies after auditing each dependency license and notices.
6. Build and execute on Linux first; validate Windows when a Java 21 environment is available.

## Source references

- [TopDownFragmenter.java](https://github.com/ipb-halle/MetFragRelaunched/blob/dev/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragmenter/TopDownFragmenter.java)
- [TopDownNeutralLossFragmenter.java](https://github.com/ipb-halle/MetFragRelaunched/blob/dev/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragmenter/TopDownNeutralLossFragmenter.java)
- [IFragment.java](https://github.com/ipb-halle/MetFragRelaunched/blob/dev/MetFragLib/src/main/java/de/ipbhalle/metfraglib/interfaces/IFragment.java)
- [AbstractTopDownBitArrayFragment.java](https://github.com/ipb-halle/MetFragRelaunched/blob/dev/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragment/AbstractTopDownBitArrayFragment.java)
- [BitArrayNeutralLoss.java](https://github.com/ipb-halle/MetFragRelaunched/blob/dev/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragment/BitArrayNeutralLoss.java)
- [MetFragLib POM](https://github.com/ipb-halle/MetFragRelaunched/blob/dev/MetFragLib/pom.xml)
- [MetFrag upstream README](https://github.com/ipb-halle/MetFragRelaunched/blob/dev/README.md)
