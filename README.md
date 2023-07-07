# Fractional Indexing in Kotlin

Based on [Implementing Fractional Indexing](https://observablehq.com/@dgreensp/implementing-fractional-indexing) by [David Greenspan](https://github.com/dgreensp).

This port is based on the [rocicorp/fractional-indexing](https://github.com/rocicorp/fractional-indexing) repo and is meant to give the same output for any given input, but no guarantees are made that this port is 100% accurate to the original JavaScript source code it was ported from.

This is compatible with Kotlin Multiplatform as it makes use of only the Kotlin standard library (no JVM-specific code).

# Installation

You can add this library as a dependency in Gradle in a Kotlin or Kotlin Multiplatform project:

```
dependencies {
    // ...
    implementation("com.davidarvelo:fractional-indexing:${latestVersion}")
}
```

# Example Usage

```
class Usages {

    fun generateKeys() {
        val first = generateKeyBetween(null, null); // "a0"
        // Insert after 1st
        val second = generateKeyBetween(first, null); // "a1"
        // Insert after 2nd
        val third = generateKeyBetween(second, null); // "a2"
        // Insert before 1st
        val zeroth = generateKeyBetween(null, first); // "Zz"
        // Insert in between 2nd and 3rd (midpoint)
        val secondAndHalf = generateKeyBetween(second, third); // "a1V"

        val beforeZeroth = generateKeyBetween(null, zeroth)
        println(first)
        println(second)
        println(third)
        println(zeroth)
        println(secondAndHalf)
        println(beforeZeroth)
    }

    fun generateNKeys() {
        val (first, second) = generateNKeysBetween(null, null, 2); // ['a0', 'a1']
        // Insert after 2nd
        val nextSet = generateKeyBetween(second, null); // "a2"
        // Insert two keys after 2nd
        val (secondAgain, third) = generateNKeysBetween(second, null, 2); // ['a2', 'a3']
        // Insert two keys before 1st
        val twoMore = generateNKeysBetween(null, first, 2); // ['Zy', 'Zz']
        // Insert two keys in between 1st and 2nd (midpoints)
        val betweenKeys = generateNKeysBetween(first, second, 2); // ['a0G', 'a0V']

        println("$first, $second")
        println(nextSet)
        println("$secondAgain, $third")
        println(twoMore)
        println(betweenKeys)
    }
}

fun main() {
    Usages().generateKeys()
    Usages().generateNKeys()
}
```

# Acknowledgements

[rocicorp](https://github.com/rocicorp) for the original code implemented in JavaScript, located at https://github.com/rocicorp/fractional-indexing, and ported to Kotlin in this repo.
