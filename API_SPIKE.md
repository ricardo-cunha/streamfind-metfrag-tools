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

## Tree links and neutral losses now captured by the adapter

The returned flat `FragmentList` still has no authoritative parent edges: in the pinned `TopDownFragmenter` source, calls to `setPrecursorFragment` and `addChild` are commented out. However, both `TopDownFragmenter` and `TopDownNeutralLossFragmenter` expose `getFragmentsOfNextTreeDepth(parent)`. The CLI now performs the breadth-first walk itself and records which parent object produced each returned batch. This reports the actual generation call as `parent_id`, rather than inferring ancestry from atom overlap or mass. It is a MetFrag generation tree at the configured traversal depth, not a measured MS/MS tree.

The specialized `TopDownNeutralLossFragmenter` holds matched `BitArrayNeutralLoss` patterns in protected state. Each pattern exposes its atom mask and neutral-loss type; the pinned `NeutralLosses` catalogue provides its SMARTS and configured mass. The CLI subclasses the fragmenter only to read that state and emits a neutral-loss record when a detached child exactly equals one of those masks and its sibling completes the parent's atom set. The record includes the matched atom set's SMILES, formula, and mass; MetFrag's catalogue neutral-loss mass; and the hydrogen adjustment applied to the surviving fragment. The matched-atom structure is not necessarily the complete hydrogen-adjusted neutral species. This is deliberately limited to MetFrag's built-in pattern matches; it is not a complete chemical neutral-loss search.

The source confirms these capabilities, but runtime assertions and representative examples must still pass on Linux and Windows before calling the fields validated.

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

1. Validate precursor-relative atom/bond indices and SMILES round trips on additional molecules.
2. Review the uploaded CycloneDX runtime SBOM against component license texts and preserve required notices.
3. Obtain authoritative MetFrag redistribution terms from the upstream maintainers before publishing any binary.

## Source references

- [TopDownFragmenter.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragmenter/TopDownFragmenter.java)
- [TopDownNeutralLossFragmenter.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragmenter/TopDownNeutralLossFragmenter.java)
- [IFragment.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/interfaces/IFragment.java)
- [AbstractTopDownBitArrayFragment.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragment/AbstractTopDownBitArrayFragment.java)
- [BitArrayNeutralLoss.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragment/BitArrayNeutralLoss.java)
- [MetFragLib POM](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/pom.xml)
- [MetFrag upstream README](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/README.md)
