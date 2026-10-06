# Third-party software notices

## Project code

The wrapper source and project-authored documentation are licensed under GPL-3.0-only; see [LICENSE](LICENSE). SPDX identifier: `GPL-3.0-only`.

## Build and runtime components

| Component | Version / source | Use | License status |
|---|---|---|---|
| MetFragRelaunched / MetFragLib | 2.6.12-SNAPSHOT, commit `2c8671b4d8d29b05ca9461e1238c2e4268ce9e21` | Structural fragmentation engine; source build installed into the Maven local repository in CI | No license grant detected in the upstream repository metadata/tree at the inspected commit. A third-party Bioconda recipe labels it LGPL-2.1-or-later; this is not authoritative. Confirmation required before redistribution. |
| Chemistry Development Kit (CDK) | 2.11, pulled transitively by MetFragLib | SMILES parsing, molecular structures, and chemistry routines used by MetFragLib | License and notices must be verified from the exact resolved artifacts before any bundled binary release. |
| Eclipse Temurin JDK | 21 in CI | Compiles and runs the CLI | CI toolchain only; not copied into the JAR. |
| Apache Maven | 3.8+ in upstream build instructions | Builds MetFragLib and the wrapper | Build tool only; not copied into the JAR. |
| Maven Compiler Plugin | 3.13.0 | Compiles the wrapper | Build plugin. |
| Maven Shade Plugin | 3.6.0 | Assembles the local executable JAR with runtime dependencies | Build plugin; shaded artifact is not published. |
| GitHub Actions | checkout v7, setup-java v6, setup-python v7 | Linux/Windows build and smoke workflow | CI service/actions; not runtime dependencies. |

MetFragLib declares multiple direct and transitive components beyond CDK. The exact shaded dependency set is produced by Maven for the pinned upstream commit and can change when the upstream revision changes. This table is an initial inventory, not a complete bill of materials or legal review.

## Distribution status

No executable JAR is attached to a release or otherwise published. Before publishing, confirm MetFragLib redistribution terms with its maintainers, generate the resolved dependency bill of materials, preserve required notices, and verify that the combined distribution terms permit the planned redistribution.
