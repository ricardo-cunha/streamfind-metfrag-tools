# streamfind-metfrag-tools

Standalone structural fragmentation tools for streamfind. The first tool is a Java CLI around MetFragLib; streamfind will integrate through a subprocess and JSON interface.

## License

The wrapper code in this repository is licensed under the GNU General Public License v3.0 only; see [LICENSE](LICENSE). This applies to original project code and does not replace licenses for MetFragLib or any other dependency. See [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) for the software inventory and distribution audit.

## Status

A source-level MetFragLib API investigation is documented in [API_SPIKE.md](API_SPIKE.md). The initial CLI adapter and Maven build are in [metfrag-tools](metfrag-tools/README.md).

The [official MetFrag project page](https://ipb-halle.github.io/MetFrag/) states GNU LGPL version 2.1 or later. The CLI builds and runs on Ubuntu 24.04 and Windows with Java 21; CI includes ethanol, aspirin, vanillin, and metoprolol. Binary distribution is pending LGPL packaging requirements and the transitive dependency license/notice audit.

## Intended command

```text
java -jar streamfind-metfrag-fragmenter.jar --smiles "CCO" --depth 2
```

The CLI emits JSON on standard output with fragment SMILES, formula, neutral monoisotopic mass, depth, atom/bond provenance, parent links captured during per-depth traversal, and only those neutral losses that match MetFrag's own atom-mask patterns. A CycloneDX SBOM for resolved runtime dependencies is generated in CI. The shaded JAR is built in CI for validation but is not published while distribution compliance work remains.

## Upstream

- [MetFragRelaunched](https://github.com/ipb-halle/MetFragRelaunched)
- [Official license statement](https://ipb-halle.github.io/MetFrag/)
- Inspected branch: `dev`
- Inspected commit: `2c8671b4d8d29b05ca9461e1238c2e4268ce9e21`
- [Linux and Windows smoke workflow](https://github.com/ricardo-cunha/streamfind-metfrag-tools/actions)