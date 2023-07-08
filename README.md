![Maven Central](https://img.shields.io/maven-central/v/com.davidarvelo/fractional-indexing)

# Fractional Indexing in Kotlin

Based on [Implementing Fractional Indexing](https://observablehq.com/@dgreensp/implementing-fractional-indexing) by [David Greenspan](https://github.com/dgreensp).

This port is based on the [rocicorp/fractional-indexing](https://github.com/rocicorp/fractional-indexing) repo and is meant to give the same output for any given input, but no guarantees are made that this port is 100% accurate to the original JavaScript source code it was ported from.

This is compatible with Kotlin Multiplatform as it makes use of only the Kotlin standard library (no JVM-specific code).

# Installation

You can add this library as a dependency in Gradle in a Kotlin or Kotlin Multiplatform project:

```gradle
dependencies {
    // ...
    implementation("com.davidarvelo:fractional-indexing:${latestVersion}")
}
```

# Example Usage

```kotlin
class Usages {

    fun generateFractionalIndexBetween() {
        val first = FractionalIndexing.generateFractionalIndexBetween(null, null); // "a0"
        // Insert after 1st
        val second = FractionalIndexing.generateFractionalIndexBetween(first, null); // "a1"
        // Insert after 2nd
        val third = FractionalIndexing.generateFractionalIndexBetween(second, null); // "a2"
        // Insert before 1st
        val zeroth = FractionalIndexing.generateFractionalIndexBetween(null, first); // "Zz"
        // Insert in between 2nd and 3rd (midpoint)
        val secondAndHalf = FractionalIndexing.generateFractionalIndexBetween(second, third); // "a1V"

        val beforeZeroth = FractionalIndexing.generateFractionalIndexBetween(null, zeroth)
        println(first)
        println(second)
        println(third)
        println(zeroth)
        println(secondAndHalf)
        println(beforeZeroth)
    }

    fun generateNFractionalIndicesBetween() {
        val (first, second) = FractionalIndexing.generateNFractionalIndicesBetween(null, null, 2); // ['a0', 'a1']
        // Insert after 2nd
        val nextSet = FractionalIndexing.generateFractionalIndexBetween(second, null); // "a2"
        // Insert two keys after 2nd
        val (secondAgain, third) = FractionalIndexing.generateNFractionalIndicesBetween(second, null, 2); // ['a2', 'a3']
        // Insert two keys before 1st
        val twoMore = FractionalIndexing.generateNFractionalIndicesBetween(null, first, 2); // ['Zy', 'Zz']
        // Insert two keys in between 1st and 2nd (midpoints)
        val betweenKeys = FractionalIndexing.generateNFractionalIndicesBetween(first, second, 2); // ['a0G', 'a0V']

        println("$first, $second")
        println(nextSet)
        println("$secondAgain, $third")
        println(twoMore)
        println(betweenKeys)
    }
}

fun main() {
    Usages().generateFractionalIndexBetween()
    Usages().generateNFractionalIndicesBetween()
}
```

# Acknowledgements

[rocicorp](https://github.com/rocicorp) for the original code implemented in JavaScript, located at https://github.com/rocicorp/fractional-indexing, and ported to Kotlin in this repo.
