package jdave.gradle

import org.gradle.api.Project

fun Project.getPlatform(triplet: String = targetPlatform) = Platform.parse(triplet)

val Project.targetPlatform: String
    get() = findProperty("target") as? String ?: run {
        val osName = System.getProperty("os.name").lowercase()
        val os = when {
            "windows" in osName -> "windows"
            "linux" in osName -> "linux"
            "mac" in osName || "darwin" in osName -> "darwin"
            else -> error("Unsupported OS: $osName")
        }

        val archName = System.getProperty("os.arch").lowercase()
        val arch = when {
            archName in listOf("x86_64", "amd64") -> "x86_64"
            archName in listOf("x86", "i386", "i486", "i586", "i686") -> "x86"
            archName == "aarch64" -> "aarch64"
            archName.startsWith("arm") -> "arm"
            else -> error("Unsupported architecture: $archName")
        }

        val musl = if (os == "linux" && "musl" in System.getProperty("java.vm.name").lowercase()) "-musl" else ""

        "$arch-unknown-$os$musl"
    }

data class Platform(val operatingSystem: OperatingSystem, val arch: Architecture, val musl: Boolean) {
    companion object {
        fun parse(triplet: String): Platform {
            val normalized = triplet.lowercase()
            return Platform(
                operatingSystem = OperatingSystem.parse(normalized),
                arch = Architecture.parse(normalized),
                musl = "musl" in normalized
            )
        }
    }

    override fun toString(): String =
        listOfNotNull(operatingSystem.key, if (musl) "musl" else null, arch.key).joinToString("-")
}

enum class OperatingSystem(val key: String, val libraryPattern: Regex) {
    Linux("linux", Regex("lib(\\w+)\\.so")),
    MacOS("darwin", Regex("lib(\\w+)\\.dylib")),
    Windows("win", Regex("(\\w+)\\.dll"));

    companion object {
        fun parse(triplet: String) = when {
            "linux" in triplet -> Linux
            "darwin" in triplet -> MacOS
            "windows" in triplet -> Windows
            else -> error("Unknown operating system: $triplet")
        }
    }
}

enum class Architecture(val key: String?) {
    X86("x86"),
    X86_64("x86-64"),
    AARCH64("aarch64"),
    ARM("arm"),
    DARWIN(null);

    companion object {
        fun parse(triplet: String) = when {
            "x86_64" in triplet -> X86_64
            listOf("x86", "i386", "i486", "i586", "i686").any { it in triplet } -> X86
            "aarch64" in triplet -> AARCH64
            "arm" in triplet -> ARM
            "darwin" in triplet -> DARWIN
            else -> error("Unknown architecture: $triplet")
        }
    }
}
