package com.github.kawamataryo.copygitlink.gitlink

import com.intellij.openapi.ide.CopyPasteManager
import java.awt.datatransfer.StringSelection

/**
 * Copies text to the clipboard.
 */
fun copyToClipboard(text: String) {
    CopyPasteManager.getInstance().setContents(StringSelection(text))
}
