# Build troubleshooting

## Gradle: `PKIX path validation failed` on this machine

Avast's "Web/Mail Shield" intercepts HTTPS and re-signs it with a root CA that lives in the **Windows
certificate store** but not in any JDK's `cacerts`. `curl` works, every JVM fails, and Gradle swallows
the handshake error and reports it as a bogus resolution miss:

> Plugin [id: '...'] was not found in any of the following sources

Only `--debug` reveals the real cause (`Starting handshake` → `Shutdown connection`, and
`SSLHandshakeException: PKIX path validation failed`). Fix by pointing the daemon at the Windows store,
which already trusts that root:

```properties
org.gradle.jvmargs = -Xmx2G -Djavax.net.ssl.trustStoreType=WINDOWS-ROOT
```

The mod build doesn't hit this because its dependencies are already in `~/.gradle/caches` — it bites
**new** Gradle builds that must download something. `tools/mod-status-plugin/gradle.properties` carries
the flag for that reason. Don't chase repository URLs or plugin versions before ruling this out.

## Build/datagen/GameTest/run tasks that finish and then hang

Any `./gradlew build`/`runGameTest`/NeoForge `run*` task (datagen or otherwise) can finish all of its
real work and then hang indefinitely instead of exiting the JVM (unlike the client-side shutdown
watchdog crash, this does **not** self-terminate). **Check status every ~10 minutes, not every
20-30+** — the vast majority of these tasks finish well under that, so "still running" past ~10 minutes
means hung, not slow. Poll the expected *output* (log tail, generated-file timestamps, whether the
artifact already exists), not just whether the process reports completion. Full write-up (root cause,
confirmed instances, how to find + kill the stuck `java.exe`): [MC 26.2 notes](../../MC-26.2-NOTES.md).
