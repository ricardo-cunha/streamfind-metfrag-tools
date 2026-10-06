# streamfind-metfrag-tools

Standalone structural fragmentation tools for streamfind, beginning with a Java CLI around MetFragLib. The streamfind integration will remain a subprocess and JSON interface.

## Status

API investigation only. The public MetFragLib source has been inspected; the CLI has not yet been implemented or run. See [API_SPIKE.md](API_SPIKE.md) for verified capabilities, limitations, and licensing notes.

## Intended command

```text
java -jar streamfind-metfrag-fragmenter.jar --smiles "CCO" --depth 2
```

The tool will emit JSON to standard output and diagnostics to standard error. Distribution of bundled binaries is on hold pending clarification of MetFrag's redistribution terms.

## Upstream

- [MetFragRelaunched](https://github.com/ipb-halle/MetFragRelaunched)
- Inspected branch: `dev`
- Inspected tree: `2c8671b4d8d29b05ca9461e1238c2e4268ce9e21`

