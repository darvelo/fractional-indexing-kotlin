package com.davidarvelo.fractionalindexing

import kotlin.math.floor
import kotlin.math.roundToInt

private const val BASE_62_DIGITS =
    "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

// License: CC0 (no rights reserved).

// This is based on https://observablehq.com/@dgreensp/implementing-fractional-indexing

object FractionalIndexing {

    // `a` may be empty string, `b` is null or non-empty string.
    // `a < b` lexicographically if `b` is non-null.
    // no trailing zeros allowed.
    // digits is a string such as '0123456789' for base 10.  Digits must be in
    // ascending character code order!
    /**
     * @param {string} a
     * @param {string | null | undefined} b
     * @param {string} digits
     * @returns {string}
     */
    private fun midpoint(a: String, b: String?, digits: String): String {
        val zero = digits[0]
        if (b != null && a >= b) {
            throw Exception("$a >= $b")
        }
        if (a.lastOrNull() == zero || (b != null && b.lastOrNull() == zero)) {
            throw Exception("trailing zero")
        }
        if (b != null) {
            // remove longest common prefix.  pad `a` with 0s as we
            // go.  note that we don't need to pad `b`, because it can't
            // end before `a` while traversing the common prefix.
            var n = 0
            while (true) {
                val aChar: Char = if (n >= a.length) zero else a[n]
                val bChar = if (n >= b.length) zero else b[n]
                if (aChar == bChar) {
                    n += 1
                } else {
                    break
                }
            }
            if (n > 0) {
                return b.substring(0 until n) + midpoint(a.drop(n), b.drop(n), digits)
            }
        }
        // first digits (or lack of digit) are different
        val digitA = if (a.isNotEmpty()) digits.indexOf(a[0]) else 0
        val digitB = if (!b.isNullOrEmpty()) digits.indexOf(b[0]) else digits.length
        if (digitB - digitA > 1) {
            val midDigit: Int = (0.5f * (digitA + digitB)).roundToInt()
            return digits[midDigit].toString()
        } else {
            // first digits are consecutive
            return if (b != null && b.length > 1) {
                b.take(1)
            } else {
                // `b` is null or has length 1 (a single digit).
                // the first digit of `a` is the previous digit to `b`,
                // or 9 if `b` is null.
                // given, for example, midpoint('49', '5'), return
                // '4' + midpoint('9', null), which will become
                // '4' + '9' + midpoint('', null), which is '495'
                digits[digitA] + midpoint(a.drop(1), null, digits)
            }
        }
    }

    /**
     * @param {string} int
     * @return {void}
     */
    private fun validateInteger(int: String) {
        if (int.length != getIntegerLength(int[0].toString())) {
            throw Exception("invalid integer part of order key: $int")
        }
    }

    /**
     * @param {string} head
     * @return {number}
     */
    private fun getIntegerLength(head: String): Int {
        return if (head >= "a" && head <= "z") {
            head[0].code - 'a'.code + 2
        } else if (head >= "A" && head <= "Z") {
            'Z'.code - head[0].code + 2
        } else {
            throw Exception("invalid order key head: $head")
        }
    }

    /**
     * @param {string} key
     * @return {string}
     */
    private fun getIntegerPart(key: String): String {
        val integerPartLength = getIntegerLength(key[0].toString())
        if (integerPartLength > key.length) {
            throw Exception("invalid order key: $key")
        }
        return key.take(integerPartLength)
    }

    /**
     * @param {string} key
     * @param {string} digits
     * @return {void}
     */
    private fun validateOrderKey(key: String, digits: String) {
        if (key == "A" + digits[0].toString().repeat(26)) {
            throw Exception("invalid order key: $key")
        }
        // getIntegerPart will throw if the first character is bad,
        // or the key is too short.  we'd call it to check these things
        // even if we didn't need the result
        val i = getIntegerPart(key)
        val f = key.drop(i.length)
        if (f.lastOrNull() == digits[0]) {
            throw Exception("invalid order key: $key")
        }
    }

    // note that this may return null, as there is a largest integer
    /**
     * @param {string} x
     * @param {string} digits
     * @return {string | null}
     */
    private fun incrementInteger(x: String, digits: String): String? {
        validateInteger(x)
        val splitX = x.split("").drop(1).dropLast(1)
        val head = splitX.first()
        val digs = splitX.drop(1).toMutableList()
        var carry = true
        for (i in digs.size - 1 downTo 0) {
            if (!carry) {
                break
            }
            val d = digits.indexOf(digs[i]) + 1
            if (d == digits.length) {
                digs[i] = digits[0].toString()
            } else {
                digs[i] = digits[d].toString()
                carry = false
            }
        }
        if (carry) {
            if (head == "Z") {
                return "a" + digits[0]
            }
            if (head == "z") {
                return null
            }
            val h = (head[0].code + 1).toChar().toString()
            if (h > "a") {
                digs.add(digits[0].toString())
            } else {
                digs.removeLast()
            }
            return h + digs.joinToString("")
        } else {
            return head + digs.joinToString("")
        }
    }

    // note that this may return null, as there is a smallest integer
    /**
     * @param {string} x
     * @param {string} digits
     * @return {string | null}
     */
    private fun decrementInteger(x: String, digits: String): String? {
        validateInteger(x)
        val splitX = x.split("").drop(1).dropLast(1)
        val head = splitX.first()
        val digs = splitX.drop(1).toMutableList()
        var borrow = true
        for (i in digs.size - 1 downTo 0) {
            if (!borrow) {
                break
            }
            val d = digits.indexOf(digs[i]) - 1
            if (d == -1) {
                digs[i] = digits.last().toString()
            } else {
                digs[i] = digits[d].toString()
                borrow = false
            }
        }
        if (borrow) {
            if (head == "a") {
                return "Z" + digits.last().toString()
            }
            if (head == "A") {
                return null
            }
            val h = (head[0].code - 1).toChar().toString()
            if (h < "Z") {
                digs.add(digits.last().toString())
            } else {
                digs.removeLast()
            }
            return h + digs.joinToString("")
        } else {
            return head + digs.joinToString("")
        }
    }

    // `a` is an order key or null (START).
    // `b` is an order key or null (END).
    // `a < b` lexicographically if both are non-null.
    // digits is a string such as '0123456789' for base 10.  Digits must be in
    // ascending character code order!
    /**
     * @param {string | null | undefined} a
     * @param {string | null | undefined} b
     * @param {string=} digits
     * @return {string}
     */
    fun generateFractionalIndexBetween(a: String?, b: String?, digits: String = BASE_62_DIGITS): String {
        if (a != null) {
            validateOrderKey(a, digits)
        }
        if (b != null) {
            validateOrderKey(b, digits)
        }
        if (a != null && b != null && a >= b) {
            throw Exception("$a >= $b")
        }
        if (a == null) {
            if (b == null) {
                return "a" + digits[0]
            }

            val ib = getIntegerPart(b)
            val fb = b.drop(ib.length)
            if (ib == "A" + digits[0].toString().repeat(26)) {
                return ib + midpoint("", fb, digits)
            }
            if (ib < b) {
                return ib
            }
            return decrementInteger(ib, digits) ?: throw Exception("cannot decrement any more")
        }

        if (b == null) {
            val ia = getIntegerPart(a)
            val fa = a.drop(ia.length)
            return incrementInteger(ia, digits) ?: (ia + midpoint(fa, null, digits))
        }

        val ia = getIntegerPart(a)
        val fa = a.drop(ia.length)
        val ib = getIntegerPart(b)
        val fb = b.drop(ib.length)
        if (ia == ib) {
            return ia + midpoint(fa, fb, digits)
        }
        val i = incrementInteger(ia, digits) ?: throw Exception("cannot increment any more")
        if (i < b) {
            return i
        }
        return ia + midpoint(fa, null, digits)
    }

    /**
     * same preconditions as generateKeysBetween.
     * n >= 0.
     * Returns an array of n distinct keys in sorted order.
     * If a and b are both null, returns [a0, a1, ...]
     * If one or the other is null, returns consecutive "integer"
     * keys.  Otherwise, returns relatively short keys between
     * a and b.
     * @param {string | null | undefined} a
     * @param {string | null | undefined} b
     * @param {number} n
     * @param {string} digits
     * @return {string[]}
     */
    fun generateNFractionalIndicesBetween(a: String?, b: String?, n: Int, digits: String = BASE_62_DIGITS): List<String> {
        if (n == 0) {
            return emptyList()
        }
        if (n == 1) {
            return listOf(generateFractionalIndexBetween(a, b, digits))
        }
        if (b == null) {
            var c = generateFractionalIndexBetween(a, b, digits)
            val result = mutableListOf(c)
            for (i in 0..n-2) {
                c = generateFractionalIndexBetween(c, b, digits)
                result.add(c)
            }
            return result
        }
        if (a == null) {
            var c = generateFractionalIndexBetween(a, b, digits)
            val result = mutableListOf(c)
            for (i in 0..n-2) {
                c = generateFractionalIndexBetween(a, c, digits)
                result.add(c)
            }
            result.reverse()
            return result
        }
        val mid = floor(n / 2f).toInt()
        val c = generateFractionalIndexBetween(a, b, digits)
        val result = mutableListOf<String>()
        result.addAll(generateNFractionalIndicesBetween(a, c, mid, digits))
        result.add(c)
        result.addAll(generateNFractionalIndicesBetween(c, b, n - mid - 1, digits))
        return result
    }
}