# MetFragLib API spike

Date: 2026-10-06

## Scope and source version

Inspected the upstream `ipb-halle/MetFragRelaunched` repository at branch `dev`, commit `2c8671b4d8d29b05ca9461e1238c2e4268ce9e21`. The root project POM reports version `2.6.12-SNAPSHOT`; the upstream README states Java 21 and Maven 3.8 for building the project.

The initial CLI source builds and runs successfully on Ubuntu 24.04 and Windows using Java 21. CI also exercises metoprolol free base (`CC(C)NCC(COC1=CC=C(C=C1)CCOC)O`); PubChem identifies this compound as C15H25NO3 (CID 4171). The build/test run is linked from the repository README.

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

## License scope

The original wrapper and project-owned source files in this repository are licensed under GPL-3.0-only; see [LICENSE](LICENSE). This license declaration does not relicense MetFragLib, CDK, or other upstream/transitive dependencies. The upstream MetFrag repository metadata reports no detected license, its inspected tree has no LICENSE/LICENCE/COPYING/NOTICE file, and its POMs inspected do not state redistribution terms. [Bioconda's recipe](https://bioconda.github.io/recipes/metfrag/README.html) labels MetFrag LGPL-2.1-or-later, but that third-party package metadata is not an authoritative grant from the copyright holder. Keep binary releases unpublished until the upstream maintainer confirms terms and the runtime dependency inventory is audited. See [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

## Software used

- MetFragRelaunched / MetFragLib 2.6.12-SNAPSHOT at commit `2c8671b4d8d29b05ca9461e1238c2e4268ce9e21`
- Java 21 (Temurin in CI) and Maven 3.8+ for source build
- CDK 2.11 modules and other runtime libraries pulled transitively by MetFragLib
- Maven Compiler Plugin 3.13.0 and Maven Shade Plugin 3.6.0
- GitHub Actions for Ubuntu and Windows build/run checks

The executable JAR is shaded locally for the CLI, but is not published. A complete bill of materials and license audit remains necessary before redistribution.

## Remaining validation

1. Validate broken-bond index mapping against precursor atom/bond ordering and test SMILES round trips on additional molecules.
2. Investigate whether parent edges can be inferred safely; never expose inferred edges as authoritative.
3. Audit all bundled dependency licenses and notices before publishing any binary.
4. Confirm MetFrag's redistribution terms with the upstream maintainers.

## Source references

- [TopDownFragmenter.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragmenter/TopDownFragmenter.java)
- [TopDownNeutralLossFragmenter.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragmenter/TopDownNeutralLossFragmenter.java)
- [IFragment.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/interfaces/IFragment.java)
- [AbstractTopDownBitArrayFragment.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragment/AbstractTopDownBitArrayFragment.java)
- [BitArrayNeutralLoss.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragment/BitArrayNeutralLoss.java)
- [MetFragLib POM](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/pom.xml)
- [MetFrag upstream README](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/README.md)
