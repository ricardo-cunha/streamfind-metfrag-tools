# MetFragLib API spike

Date: 2026-10-06

## Scope and source version

Inspected the upstream `ipb-halle/MetFragRelaunched` repository at branch `dev`, commit `2c8671b4d8d29b05ca9461e1238c2e4268ce9e21`. The root project POM reports version `2.6.12-SNAPSHOT`; the upstream README states Java 21 and Maven 3.8 for building.

The CLI builds and runs on Ubuntu 24.04 and Windows using Java 21. CI exercises metoprolol free base (`CC(C)NCC(COC1=CC=C(C=C1)CCOC)O`); the output is printed in the build log.

## What the API provides

- `TopDownFragmenter(Settings)` exposes `generateFragments()`, returning a `FragmentList`.
- Each `IFragment` provides SMILES, molecular formula, monoisotopic mass, tree depth, ID, and broken-bond indices.
- `DefaultBitArrayFragment` exposes atom, intact-bond, and broken-bond bit arrays. These appear sufficient to map provenance to precursor indices, subject to validating index mapping and canonicalization.
- The flat `FragmentList` is produced breadth-by-depth, with the root first.

## Tree links and neutral losses captured by the adapter

The flat `FragmentList` has no authoritative parent edges: in the pinned `TopDownFragmenter` source, calls to `setPrecursorFragment` and `addChild` are commented out. Both `TopDownFragmenter` and `TopDownNeutralLossFragmenter` expose `getFragmentsOfNextTreeDepth(parent)`, however. The CLI walks this API breadth-first and records the exact parent object that generated each batch as `parent_id`. These are MetFrag generation relationships, not measured MS/MS tree edges.

`TopDownNeutralLossFragmenter` retains matched `BitArrayNeutralLoss` patterns. The CLI emits a record only when a detached child exactly matches one of those masks and its sibling completes the parent's atom set. It reports the matched atom structure, formula, and mass separately from MetFrag's catalog loss mass and the survivor hydrogen adjustment. This is limited to built-in MetFrag pattern matches; it is not a general neutral-loss search.

CI checks parent references on ethanol, aspirin, vanillin, and metoprolol on Linux and Windows, and confirms at least one built-in neutral-loss match for ethanol. Optional database, spreadsheet, R, script, and test adapters are excluded from the shaded archive and checked absent.

## License scope

The original wrapper source and project-authored code in this repository use GPL-3.0-only; see [LICENSE](LICENSE). The [official MetFrag project page](https://ipb-halle.github.io/MetFrag/) states MetFrag is published under GNU LGPL version 2.1 or later. The inspected source snapshot has no LICENSE/LICENCE/COPYING/NOTICE file and its POMs do not state a license; the official page is now recorded as the project-level license statement. The [FSF compatibility FAQ](https://www.gnu.org/licenses/gpl-faq.en.html#AllCompatibility) lists LGPL-2.1-or-later as compatible with GPLv3.

That license statement resolves the basic MetFrag license identification. It does not complete binary packaging compliance. A shaded executable must retain notices and meet applicable LGPL source and relinking/library-replacement conditions. If MetFragLib is included in a distribution, provide its corresponding source and license notices. For a shaded executable, also provide wrapper object materials needed to relink against a modified library. A dynamically replaceable library arrangement can satisfy the replaceability condition, but does not remove the source requirement when the library itself is distributed. Also finish the component-by-component license and notice review for the runtime SBOM. See [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md), the [GNU LGPL 2.1 text](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html), and [FSF guidance on linking](https://www.gnu.org/licenses/gpl-faq.en.html#LGPLStaticVsDynamic).

## Software used

- MetFragRelaunched / MetFragLib 2.6.12-SNAPSHOT at commit `2c8671b4d8d29b05ca9461e1238c2e4268ce9e21`
- Java 21 (Temurin in CI), Maven 3.8+
- CDK 2.11 and other runtime dependencies resolved through MetFragLib
- Maven Compiler Plugin 3.13.0, Maven Shade Plugin 3.6.0
- CycloneDX Maven Plugin for the runtime SBOM
- GitHub Actions for Ubuntu and Windows build/run checks

A draft v0.1.0 release is attached in this repository and is not published. It includes the executable JAR, the pinned MetFragRelaunched source and wrapper build materials for relinking, collected dependency license/notice files, the runtime SBOM, and checksums. The full component-by-component dependency license/notice audit remains outstanding; review it before publishing.

## Remaining validation

1. Validate precursor-relative atom/bond indices and SMILES round trips on additional molecules.
2. Review the uploaded CycloneDX runtime SBOM against component license texts and preserve required notices.
3. Review the draft source/relink archive and validate its LGPL source and relinking materials; complete the component-level dependency license/notice audit before publication.

## Source references

- [TopDownFragmenter.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragmenter/TopDownFragmenter.java)
- [TopDownNeutralLossFragmenter.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragmenter/TopDownNeutralLossFragmenter.java)
- [IFragment.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/interfaces/IFragment.java)
- [AbstractTopDownBitArrayFragment.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragment/AbstractTopDownBitArrayFragment.java)
- [BitArrayNeutralLoss.java](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/src/main/java/de/ipbhalle/metfraglib/fragment/BitArrayNeutralLoss.java)
- [MetFragLib POM](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/MetFragLib/pom.xml)
- [MetFrag upstream README](https://github.com/ipb-halle/MetFragRelaunched/blob/2c8671b4d8d29b05ca9461e1238c2e4268ce9e21/README.md)