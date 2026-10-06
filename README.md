# streamfind-metfrag-tools

Standalone Java CLI tools built around MetFragLib. The first tool generates structural fragment candidates from a SMILES string and writes machine-readable JSON.

## Install the released CLI

The public [MetFrag Fragmenter v0.1.0 release](https://github.com/ricardo-cunha/streamfind-metfrag-tools/releases/tag/v0.1.0) provides a self-contained executable JAR, a companion source/relink archive, third-party notices, and SHA-256 checksums.

Requirements: Java 21 or later. The JAR bundles its Java library dependencies; it does not bundle a Java runtime.

Download the JAR:

```sh
curl -fL -o streamfind-metfrag-fragmenter.jar https://github.com/ricardo-cunha/streamfind-metfrag-tools/releases/download/v0.1.0/streamfind-metfrag-fragmenter-0.1.0.jar
```

On Windows PowerShell:

```powershell
Invoke-WebRequest -Uri "https://github.com/ricardo-cunha/streamfind-metfrag-tools/releases/download/v0.1.0/streamfind-metfrag-fragmenter-0.1.0.jar" -OutFile "streamfind-metfrag-fragmenter.jar"
```

Run it directly:

```sh
java -jar streamfind-metfrag-fragmenter.jar --smiles "CCO" --depth 2
```

For StreamFind, copy the downloaded file to:

```text
.streamfind/tools/metfrag/streamfind-metfrag-fragmenter.jar
```

You can instead set `STREAMFIND_METFRAG_FRAGMENTER_JAR` to the JAR path. StreamFind also needs its managed Java 21 runtime or another supported Java 21 installation. Verify the downloaded file against `SHA256SUMS` from the release.

## Output

The CLI writes one JSON object to standard output. It includes fragment SMILES, formula, neutral monoisotopic exact mass, depth, atom and broken-bond provenance, parent links captured during MetFrag's per-depth traversal, and neutral-loss records only when they match MetFrag's built-in atom-mask patterns. Parent links describe generator traversal, not experimentally observed MS/MS transitions.

For example, metoprolol can be fragmented with:

```sh
java -jar streamfind-metfrag-fragmenter.jar --smiles "CC(C)NCC(COC1=CC=C(C=C1)CCOC)O" --depth 1
```

See [API_SPIKE.md](API_SPIKE.md) for the API investigation and output limitations.

## Build and validation

Build instructions and the JSON field description are in [metfrag-tools/README.md](metfrag-tools/README.md). CI builds and smoke-tests on Ubuntu and Windows with Java 21, including ethanol, aspirin, vanillin, and metoprolol. The latest successful workflow run is available under [Actions](https://github.com/ricardo-cunha/streamfind-metfrag-tools/actions).

## License and bundled software

Original wrapper code and project-authored documentation use GPL-3.0-only; see [LICENSE](LICENSE). MetFragLib and runtime dependencies retain their own licenses. The release's source/relink archive includes the pinned MetFragRelaunched source, wrapper build materials, runtime SBOM, collected dependency license/notice files, and LGPL-2.1 text. See [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) for the component inventory and known limits of the license metadata review.
