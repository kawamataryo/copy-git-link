package com.github.kawamataryo.copygitlink

import GitLink
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import copyToClipboard
import showNotification
import truncateText


class CopyAsMarkdownWithCode : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val gitLink = GitLink(e)

        if (gitLink.hasRepository) {
            val permalink = gitLink.permalink
            val linkText = gitLink.relativePath + gitLink.linePath
            val markdownLink = "[$linkText]($permalink)"
            val source = gitLink.source;
            val editor: Editor =
                e.getRequiredData(CommonDataKeys.EDITOR)

            var vf:VirtualFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE)


            val content = """$markdownLink
                |```${vf.name.split(".").last()}
                |$source
                |```
            """.trimMargin()

            // Copy to clipboard
            copyToClipboard(content)

            showNotification(
                    gitLink.project,
                    NotificationType.INFORMATION,
                    "Copied permalink as Markdown.",
                    "<a href='$permalink'>${truncateText(linkText, 45)}</a>."
            )
        } else {
            showNotification(
                    gitLink.project,
                    NotificationType.ERROR,
                    "Error",
                    "Copy failed.\n You will need to set up a GitHub remote repository to run it."
            )
        }
    }
}
