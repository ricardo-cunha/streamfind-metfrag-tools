# streamfind-metfrag-tools

Standalone structural fragmentation tools for streamfind. The first tool is a Java CLI around MetFragLib; streamfind will integrate through a subprocess and JSON interface.

## Status

A source-level MetFragLib API investigation is documented in [API_SPIKE.md](API_SPIKE.md). The initial CLI adapter and Maven build are in [metfrag-tools](metfrag-tools/README.md).

The source has not yet been compiled or executed against example molecules. Local binary redistribution is on hold because the upstream repository has no detectable license grant; see the API spike before publishing artifacts.

## Intended command

```text
java -jar streamfind-metfrag-fragmenter.jar --smiles "CCO" --depth 2
```

The CLI emits JSON on standard output, including fragment SMILES, formula, neutral monoisotopic mass, depth, and precursor-relative atom/broken-bond indices. Parent links and neutral-loss structures are marked unavailable because the public fragment results do not preserve those associations.

## Upstream

- [MetFragRelaunched](https://github.com/ipb-halle/MetFragRelaunched)
- Inspected branch: `dev`
- Inspected tree: `2c8671b4d8d29b05ca9461e1238c2e4268ce9e21`
