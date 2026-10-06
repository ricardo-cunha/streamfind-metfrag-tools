# MetFragLib API spike

Date: 2026-10-06

## Scope and source version

Inspected the upstream `ipb-halle/MetFragRelaunched` repository at branch `dev`, commit `2c8671b4d8d29b05ca9461e1238c2e4268ce9e21`. The root project POM reports version `2.6.12-SNAPSHOT`; the upstream README states Java 21 and Maven 3.8 for building the project.

The initial CLI source is now present, but it has not yet been compiled or exercised against molecules. A Linux and Windows GitHub Actions build/smoke workflow was added and is running.

## What the API provides

- `TopDownFragmenter(Settings)` exposes `generateFragments()`, returning a `FragmentList`.
- Each `IFragment` provides `getSmiles(precursor)`, `getMolecularFormula(precursor)`, `getMonoisotopicMass(precursor)`, `getTreeDepth()`, `getID()`, and `getBrokenBondIndeces()`.
- `DefaultBitArrayFragment` exposes atom, intact-bond, and broken-bond bit arrays. The top-down precursor tracks atoms and bonds from the parsed precursor structure. This appears sufficient to map atom/bond provenance to indices in the exact input molecule, subject to validating indexing and canonicalization.
- The returned flat `FragmentList` is produced breadth-by-depth: the root is added first and generation loops from depth 1 through configured maximum depth.

## What is not reliably exposed

### Parent-child relationships

The public fragment interface contains ID and tree depth but no parent/child API. In `TopDownFragmenter`, the lines that would attach children to their precursor fragment are commented out. Therefore a flat result cannot be treated as an authoritative fragmentation tree. Parent edges might be inferred from atom/bond containment and depth in some cases, but ambiguity and duplicate fragments make these edges inferred data. The first JSON contract marks parent links unavailable rather than implying a true tree.

### Neutral-loss structures

`TopDownNeutralLossFragmenter` internally detects matching neutral-loss atom masks and adapts molecular formulas during fragmentation. `BitArrayNeutralLoss` exposes a neutral-loss type, mass difference, hydrogen difference, and atom masks. However, these arrays are held in protected/internal state on the fragmenter and are not included in the returned `FragmentList`. The inspected public `IFragment` result does not expose a neutral-loss association. A later implementation could use this specialized fragmenter and an adapter, but loss provenance is not available from the ordinary returned fragment objects. The initial JSON therefore marks per-fragment neutral losses unavailable.

## Licensing and redistribution

The GitHub repository metadata reports no detected license, the inspected branch tree has no license/LICENCE/COPYING/NOTICE file, and the POMs inspected do not state redistribution terms. [Bioconda's recipe](https://bioconda.github.io/recipes/metfrag/README.html) describes the upstream as LGPL-2.1-or-later, but that third-party package metadata is not an authoritative grant from the copyright holder. Do not publish a shaded/fat JAR containing MetFragLib or its dependencies until the upstream maintainers clarify licensing and redistribution obligations. Keep the repository private until then.

## Remaining validation

1. Review the Linux and Windows workflow results and repair compile/runtime issues.
2. Validate formulas, exact masses, depths, broken-bond index mapping, duplicate behavior, and SMILES round trips for representative acyclic, ring, aromatic, and heteroatom-containing molecules.
3. Test any parent-edge inference independently and label provenance/confidence in JSON; do not expose inferred edges as authoritative.
4. Audit all bundled dependency licenses and notices before publishing any binary.
5. Confirm MetFrag's redistribution terms with the upstream maintainers.

## Source references

- [TopDownFragmenter.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragmenter/TopDownFragmenter.java)
- [TopDownNeutralLossFragmenter.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragmenter/TopDownNeutralLossFragmenter.java)
- [IFragment.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/interfaces/IFragment.java)
- [AbstractTopDownBitArrayFragment.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragment/AbstractTopDownBitArrayFragment.java)
- [BitArrayNeutralLoss.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragment/BitArrayNeutralLoss.java)
- [MetFragLib POM](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/pom.xml)
- [MetFrag upstream README](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/README.md)
