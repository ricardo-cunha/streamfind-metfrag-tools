# Third-party software notices

## Project code

Original wrapper source and project-authored documentation use GPL-3.0-only; see [LICENSE](LICENSE). This license applies to original project code and does not replace third-party licenses.

## Runtime software inventory

The v0.1.0 executable is a shaded Java archive built on 2026-10-06 from the pinned MetFragRelaunched source revision below. The release's companion source/relink archive contains the CycloneDX runtime SBOM (`bom.json`), LGPL-2.1 text, the wrapper sources and build file, pinned MetFrag source, and license/notice files collected from runtime dependency archives.

| Component group | Version(s) in the release | License information recorded by the source project or runtime SBOM |
|---|---|---|
| MetFragRelaunched / MetFragLib | 2.6.12-SNAPSHOT, commit `2c8671b4d8d29b05ca9461e1238c2e4268ce9e21` | SBOM metadata says `unspecified`. The [official MetFrag project page](https://ipb-halle.github.io/MetFrag/) states GNU LGPL version 2.1 or later. |
| Chemistry Development Kit (CDK) | 2.11 | GNU LGPL 2.1 or later, as recorded in Maven metadata. |
| JNA and JNA-InChI | 5.10.0 and 1.3.1 | LGPL-2.1-or-later; JNA also reports Apache-2.0. |
| JGraphT and XOM | 0.6.0 and 1.3.9 | LGPL-2.1-only / LGPL 2.1, as recorded in Maven metadata. |
| Apache HttpComponents, Commons CSV/Codec/IO, json-simple, hierarchical-clustering, Log4j | Resolved versions are listed in `bom.json` | Apache-2.0, as recorded in Maven metadata. |
| SLF4J and signatures | 1.7.36 and 1.1 | MIT, as recorded in Maven metadata. |
| BEAM and dom4j | 1.3.8 and 2.2.0 | BSD-2-Clause and BSD-3-Clause, as recorded in Maven metadata. |
| vecmath | 1.5.2 | GPL-2.0-with-classpath-exception, as recorded in Maven metadata. |
| JAMA | 1.0.3 | CC-PDDC, as recorded in Maven metadata. |

The complete resolved component list, versions, and declared metadata are in the release archive's `bom.json`. CI also prints this metadata in the Linux and Windows workflow logs. Maven license declarations are discovery information, not independent legal conclusions. MetFragLib itself has no Maven license identifier in the inspected source snapshot; the project-level license statement is linked above.

## Redistribution notes

The published release includes a shaded executable JAR. The companion source/relink archive provides the pinned MetFragRelaunched source and wrapper build materials so users can rebuild after modifying MetFragLib. It also includes the LGPL-2.1 text and dependency license/notice files found in the runtime JARs. The release contains SHA-256 checksums for the JAR and source archive.

The runtime SBOM and archived license files have been reviewed against the build's declared dependency metadata for this release. Some metadata is absent or depends on upstream statements; the archive's collected notices should therefore be checked against the actual component licenses before further redistribution. This inventory describes the available materials and upstream statements; it is not legal advice. The applicable LGPL terms remain in force. See the [GNU LGPL 2.1 text](https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html) and [FSF guidance on static and dynamic linking](https://www.gnu.org/licenses/gpl-faq.en.html#LGPLStaticVsDynamic).

## Build tools

Java 21 (Temurin in CI), Apache Maven, Maven Compiler Plugin 3.13.0, Maven Shade Plugin 3.6.0, and CycloneDX Maven Plugin 2.9.3 are used to build and describe the runtime artifact. GitHub Actions builds and tests the CLI on Ubuntu and Windows. These build tools and CI services are not included in the executable JAR.
