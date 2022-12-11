package com.github.kawamataryo.copygitlink

import GitLink
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import copyToClipboard
import showNotification
import truncateText

class CopyBranchLink : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val gitLink = GitLink(e)

        if (gitLink.hasRepository) {
            val branchLink = gitLink.branchLink

            // Copy to clipboard
            copyToClipboard(branchLink)

            showNotification(
                gitLink.project,
                NotificationType.INFORMATION,
                "Copied branch link.",
                "<a href='$branchLink'>${truncateText(branchLink, 45)}</a>."
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
