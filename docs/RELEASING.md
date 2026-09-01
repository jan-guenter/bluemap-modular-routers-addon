# Release procedure

Releases are promoted only from an owner-accepted, independently audited
commit on `main`. The accepted candidate identity is recorded in
`provenance/release.json`.

## Clean gate

Use Java 21, a complete Gradle distribution, the exact sibling BlueMap
checkout, and the exact local Modular Routers and Glassential add-on artifacts:

```bash
git submodule update --init --recursive -- \
  tooling/bluemap-addon-toolkit modules/bluemap-addon-adapter-api
gradle --no-daemon \
  -PbluemapSourcePath=/absolute/path/to/BlueMap-at-7e07f4e7 \
  -PmodularRoutersJar=/absolute/path/modular-routers-13.2.7+mc1.21.1.jar \
  -PglassentialAddonJar=/absolute/path/bluemap-glassential-addon-0.1.0-alpha.2.jar \
  -PreleaseTag=v0.1.0-alpha.2 \
  clean check build generatePomFileForAddonPublication \
  generateMetadataFileForAddonPublication verifyPublicationArtifacts \
  verifyReleaseCandidate
```

Inspect the production and sources JARs. Reject NeoForge metadata, nested
JARs, third-party classes/assets, gallery output, tests, research data, or
unexpanded metadata. Require exactly the four shared Adapter API source/class
paths once and reject the displaced local helper types.

## Runtime and publication

Run the deterministic [gallery](../gallery/README.md) against that exact JAR,
open the intended BlueMap link for the required lightweight sanity check, and
obtain explicit owner acceptance for the exact candidate JAR. Rebuild twice
with the exact inputs and require byte-identical artifacts before sealing the
release identity.

Before tagging, merge the independently audited release pull request. Create
and push an annotated `v<addon_version>` tag at that reviewed `main` commit.
The release workflow reproduces every accepted byte, creates a draft
prerelease, uploads and attests the assets, publishes the Maven package,
verifies the draft assets, and only then makes the prerelease public.

Never reuse or move a release tag. A failed prepublication run may be resumed
with the workflow's exact immutable tag input while its GitHub release remains
a draft. Publication deploys nothing to the Minecraft server.
