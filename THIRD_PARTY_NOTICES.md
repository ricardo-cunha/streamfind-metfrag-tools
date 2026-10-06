# Third-party software notices

## Project code

Original wrapper source and project-authored documentation use GPL-3.0-only; see [LICENSE](LICENSE). This declaration does not grant a license to MetFragLib or its dependencies.

## Components and audit status

| Component | Version / source | Use | License / redistribution status |
|---|---|---|---|
| MetFragRelaunched / MetFragLib | 2.6.12-SNAPSHOT, commit `2c8671b4d8d29b05ca9461e1238c2e4268ce9e21` | Fragmentation engine, built from source in CI | No authoritative license grant found in repository metadata, source tree, or inspected POMs. Bioconda labels it LGPL-2.1-or-later, but that is not a grant from the copyright holder. Maintainer confirmation is still needed. |
| CDK and other MetFrag transitive libraries | Resolved versions recorded in the CI CycloneDX SBOM artifact `runtime-sbom-<runner OS>` | Runtime libraries in the shaded local build | The SBOM records Maven license metadata where published; each component's authoritative license and notice obligations still require review before binary redistribution. |
| Eclipse Temurin JDK | 21 in CI | Compile and runtime environment | CI toolchain; not copied into the JAR. |
| Apache Maven | 3.8+ per upstream build instructions | Build tool | Not copied into the JAR. |
| Maven Compiler Plugin | 3.13.0 | Compiles wrapper | Build-only plugin; not copied into the JAR. |
| Maven Shade Plugin | 3.6.0 | Creates local executable JAR | Build-only plugin; not copied into the JAR. |
| CycloneDX Maven Plugin | 2.9.3 | Produces runtime dependency SBOM in CI | Build-only plugin; not copied into the JAR. |
| GitHub Actions and actions | checkout v7, setup-java v6, setup-python v7, upload-artifact v7.0.1 | CI build, validation, and SBOM retention | CI services/actions; not runtime dependencies. |

The CycloneDX SBOM is generated after MetFragLib is installed and the wrapper is packaged, so it inventories the resolved runtime graph used by Maven for the wrapper. License expressions in dependency metadata are discovery aids, not legal conclusions. The current audit is incomplete while MetFragLib's terms and the exact component notice obligations remain unconfirmed.

## Distribution status

The shaded JAR is built for private CI validation only. It is not attached to a release or made available for download. Do not publish it until MetFrag's redistribution terms are confirmed and every bundled runtime component has been checked for compatible terms and preserved notices.
