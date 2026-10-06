# MetFrag structural fragmentation CLI

This Java command line adapter accepts a SMILES and maximum top-down fragmentation depth and emits one JSON object on standard output.

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
- `parent_id` is `null` and `parent_link_status` reports unavailable because the public fragment-list API does not preserve parent links
- `neutral_losses` is `null` and the top-level `neutral_loss_status` explains that the result API does not expose per-fragment loss provenance

Errors are JSON on standard output and return exit code 1; invalid CLI arguments return exit code 2. `--help` prints usage.

## Validation

The latest [Linux and Windows workflow](https://github.com/ricardo-cunha/streamfind-metfrag-tools/actions/runs/37425049329) built the JAR and ran three representative SMILES successfully on Ubuntu 24.04 and Windows latest. This workstation itself has no Java or Maven installation and WSL is inaccessible.
