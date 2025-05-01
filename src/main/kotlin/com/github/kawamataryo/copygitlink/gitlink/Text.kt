package com.github.kawamataryo.copygitlink.gitlink

/**
 * Truncates text to a maximum length and adds ellipsis if needed.
 */
fun truncateText(text: String, maxLength: Int): String {
    if (text.length < maxLength) {
        return text
    }
    return "${text.substring(0, maxLength)}..."
}
