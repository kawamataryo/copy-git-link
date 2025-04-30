package com.github.kawamataryo.copygitlink

import com.github.kawamataryo.copygitlink.gitlink.GitLink
import com.github.kawamataryo.copygitlink.gitlink.copyToClipboard
import com.github.kawamataryo.copygitlink.gitlink.showNotification
import com.github.kawamataryo.copygitlink.gitlink.truncateText
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

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
