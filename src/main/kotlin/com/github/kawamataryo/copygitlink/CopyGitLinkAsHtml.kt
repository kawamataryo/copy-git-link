package com.github.kawamataryo.copygitlink

import GitLink
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import copyToClipboard
import showNotification
import truncateText

class CopyPermalinkAsHtml : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val gitLink = GitLink(e)

        if (gitLink.hasRepository) {
            val permalink = gitLink.permalink
            val linkText = gitLink.relativePath + gitLink.linePath

            // Copy to clipboard
            copyToClipboard("<a href='$permalink'>$linkText</a>")

            showNotification(
                gitLink.project,
                NotificationType.INFORMATION,
                "Copied permalink.",
                "<a href='$permalink'>${truncateText(permalink, 45)}</a>."
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
