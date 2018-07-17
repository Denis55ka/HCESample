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

fun Int.toHexString(): String = Integer.toHexString(this)

fun buildTemplate(tag: String, vararg tags: Pair<String, String>): String {
    val value = tags.joinToString("") { (tag, value) -> buildTag(tag, value) }
    return "$tag${getTagLength(value)}$value"
}

fun buildTag(tag: String, value: String): String = "$tag${getTagLength(value)}${value.replace(" ", "")}"

fun getTagLength(value: String): String {
    val length = (value.replace(" ", "").length / 2).toHexString()
    return (if (length.length == 1) "0" else "") + length
}
