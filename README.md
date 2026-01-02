[![.github/workflows/build.yml](https://github.com/MinnDevelopment/jdave/actions/workflows/build.yml/badge.svg)](https://github.com/MinnDevelopment/jdave/actions/workflows/build.yml)

# Java Discord Audio & Video Encryption (JDAVE)

This library provides java bindings for [libdave](https://github.com/discord/libdave).

To implement these bindings, this library uses Java **Foreign Function & Memory API** (FFM).

## Requirements

- Java 25

## Supported Platforms

- Linux x64

## Example: JDA

To use this library with [JDA](https://github.com/discord-jda/JDA), you can use the [JDaveSessionFactory](src/main/java/club/minnced/discord/jdave/interop/JDaveSessionFactory.java) to configure the audio module:

```java
JDABuilder.createLight(TOKEN)
  .setAudioModuleConfig(new AudioModuleConfig()
    .withDaveSessionFactory(new JDaveSessionFactory()))
  .build()
```

## Why Java 25?

This library uses the [Foreign Function & Memory (FFM) API](https://docs.oracle.com/en/java/javase/22/core/foreign-function-and-memory-api.html) which has been stabilized in Java 22.
Since Java 22 is not a Long-Term-Support release, this library instead uses Java 25.

Other alternatives have downsides compared to the newer FFM API:

- JNI breaks relocation, since it requires the package to match the C symbols. To relocate a JNI native binding, you must rename the native symbols, which is quite cumbersome.
- JNI requires compiling the library for each platform, we can't just call C libraries directly without some C glue code that needs to be compiled for every target platform.
- JNA can be relocated and does not require extra C glue code, but has significant overhead, which is a problem in performance critical hot paths, like with audio encryption.
- JNA is also a massive library, which is a dependency we can avoid by using Java's standard library entirely.

For this reason, I've chosen to use the new FFM API instead. The optimal solution would be a full Java implementation, instead of relying on native libraries. But that requires a lot more time, which we don't have right now.

---

# ⚠️ This implementation is work-in-progress and not completed.
