package com.denis55ka.hcesample

fun String.toByteArray(): ByteArray {
    val digits = replace(" ", "")
    val result = ByteArray(digits.length / 2) // One byte represent as two char: FF
    for (i in 0..digits.lastIndex step 2) {
        result[i / 2] = Integer.parseInt(digits.substring(i, i + 2), 16).toByte()
    }
    return result
}

fun ByteArray.toHexString(): String = joinToString(" ") { String.format("%02X", it) }
