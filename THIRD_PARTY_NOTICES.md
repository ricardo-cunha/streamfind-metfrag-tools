# Third-party software notices

## Project code

Original wrapper source and project-authored documentation use GPL-3.0-only; see [LICENSE](LICENSE). This license applies to original project code and does not replace third-party licenses.

## Components and audit status

| Component | Version / source | Use | License / redistribution status |
|---|---|---|---|
| MetFragRelaunched / MetFragLib | 2.6.12-SNAPSHOT, commit `2c8671b4d8d29b05ca9461e1238c2e4268ce9e21` | Fragmentation engine, built from source in CI | The [official MetFrag project page](https://ipb-halle.github.io/MetFrag/) states GNU LGPL version 2.1 or later. The inspected source snapshot has no LICENSE file or POM license entry, so preserve the project page as the license reference and include LGPL notices with any distribution. LGPL-2.1-or-later is compatible with GPLv3 according to the [FSF compatibility FAQ](https://www.gnu.org/licenses/gpl-faq.en.html#AllCompatibility). Applicable LGPL redistribution conditions still apply to the shaded executable. |
| CDK and other MetFrag transitive libraries | Resolved versions in CI CycloneDX SBOM `runtime-sbom-<runner OS>` | Runtime libraries in shaded local build | Maven license metadata is recorded in the SBOM. Each component's actual license text, notices, and binary redistribution obligations still need review before distribution. |
| Eclipse Temurin JDK | 21 in CI | Compile and runtime environment | CI toolchain; not copied into the JAR. |
| Apache Maven | 3.8+ per upstream build instructions | Build tool | Not copied into the JAR. |
| Maven Compiler Plugin | 3.13.0 | Compiles wrapper | Build-only plugin; not copied into the JAR. |
| Maven Shade Plugin | 3.6.0 | Creates local executable JAR | Build-only plugin; not copied into the JAR. |
| CycloneDX Maven Plugin | 2.9.3 | Produces runtime dependency SBOM in CI | Build-only plugin; not copied into the JAR. |
| GitHub Actions and actions | checkout v7, setup-java v6, setup-python v7, upload-artifact v7.0.1 | CI build, validation, and SBOM retention | CI services/actions; not runtime dependencies. |

The wrapper excludes optional database, spreadsheet, R-server, JavaScript-engine, and test libraries unused by this CLI; CI verifies they are absent from the shaded archive. The CycloneDX SBOM inventories the resolved runtime dependency graph and is retained with Maven-declared license metadata. Those declarations are discovery aids, not legal conclusions.

The official project page resolves MetFrag's stated license; a maintainer issue is not needed for basic license identification. Before distributing the shaded executable, complete the component-level SBOM/license/notice review and satisfy LGPL conditions, including making corresponding MetFragLib source and the wrapper materials needed to relink available, or using a suitable replaceable-library mechanism. See the [GNU LGPL 2.1 text](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html) and [FSF guidance on static and dynamic linking](https://www.gnu.org/licenses/gpl-faq.en.html#LGPLStaticVsDynamic).

## Distribution status

The shaded JAR is built for private CI validation only and is not attached to a release. MetFrag's stated license is documented; binary distribution remains pending the LGPL packaging work and full dependency notice audit.