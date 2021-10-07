package com.github.kawamataryo.copygitlink

import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.vfs.VirtualFile
import copyToClipboard
import getLinePath
import getPermalink
import getRelativePath
import getRepositoryPath
import git4idea.repo.GitRepositoryManager
import showNotification
import truncateText


class CopyPermalink : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val file: VirtualFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE)
        val project = e.getRequiredData(CommonDataKeys.PROJECT)
        val repo = GitRepositoryManager.getInstance(project).getRepositoryForFileQuick(file)
        val primaryCaret: Caret = e.getRequiredData(CommonDataKeys.EDITOR).caretModel.primaryCaret

        if (repo != null) {
            val permalink = getPermalink(
                getRepositoryPath(repo),
                repo.currentRevision!!,
                getRelativePath(project, file),
                getLinePath(primaryCaret)
            )

            // Copy to clipboard
            copyToClipboard(permalink)
            primaryCaret.removeSelection()

            showNotification(
                project,
                NotificationType.INFORMATION,
                "Copied permalink.",
                "<a href='$permalink'>${truncateText(permalink, 45)}</a>."
            )
        } else {
            showNotification(
                project,
                NotificationType.ERROR,
                "Error",
                "Copy failed.\n You will need to set up a GitHub remote repository to run it."
            )
        }
    }
}
