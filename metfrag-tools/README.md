# MetFrag structural fragmentation CLI

This Java command line adapter accepts a SMILES and maximum top-down fragmentation depth and emits one JSON object on standard output.

## License

Original wrapper code is licensed under [GPL-3.0-only](../LICENSE). MetFragLib and transitive dependencies retain their respective licenses; see [THIRD_PARTY_NOTICES.md](../THIRD_PARTY_NOTICES.md).

## Build prerequisites

- Java 21 JDK
- Maven 3.8 or later
- MetFragRelaunched source built and installed locally

MetFragLib currently appears as `2.6.12-SNAPSHOT` on the inspected upstream branch and is not declared here as a released Maven Central dependency. First build its source from the upstream repository:

```sh
git clone https://github.com/ipb-halle/MetFragRelaunched.git
cd MetFragRelaunched
mvn clean install -pl MetFragLib -am -DskipTests
```

Then, from this directory:

```sh
mvn clean package
java -jar target/streamfind-metfrag-fragmenter-0.1.0-SNAPSHOT.jar --smiles "CCO" --depth 2
```

The shaded jar is a local build output only. Do not publish it until upstream redistribution terms and every bundled dependency's license obligations are confirmed.

## JSON fields

- `schema_version`, `input_smiles`, `requested_depth`, and `fragmentation_method`
- `fragments[]`: MetFrag fragment `id`, `smiles`, `formula`, neutral monoisotopic `exact_mass`, and `depth`
- `atom_indices` and `broken_bond_indices`: precursor-relative MetFrag indices; atom and bond index mapping still needs validation against input ordering
- `parent_id` is captured from the exact parent object whose per-depth API call generated the fragment
- `neutral_losses` contains a matched atom set only when its mask exactly matches a MetFrag pattern, alongside the catalogue loss mass and survivor hydrogen adjustment; the match structure is not necessarily the complete neutral species

Errors are JSON on standard output and return exit code 1; invalid CLI arguments return exit code 2. `--help` prints usage.

## Validation

The [Linux and Windows workflow](https://github.com/ricardo-cunha/streamfind-metfrag-tools/actions) builds/runs the CLI, checks parent references and JSON schema for ethanol, aspirin, vanillin, and metoprolol, and uploads a CycloneDX SBOM for runtime dependencies. The JAR is not distributed while licensing review remains incomplete. The latest run is linked from the repository README.
