package com.github.kawamataryo.intellijpluginsandbox

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager
import java.awt.datatransfer.StringSelection

fun getLinePath(caret: Caret): String {
    val start = caret.selectionStartPosition.getLine() + 1
    val end =
        if (caret.selectionEndPosition.leansRight) caret.selectionEndPosition.line + 1 else caret.selectionEndPosition.line
    return if (start == end) "#L$start" else "#L$start-L$end"
}

fun getRepositoryPath(repo: GitRepository): String {
    val url = repo.remotes.first().firstUrl ?: ""
    val result = Regex(".*github\\.com/(.*)\\.git").matchEntire(url)
    return result?.groupValues?.get(1) ?: ""
}

fun getRelativePath(project: Project, file: VirtualFile): String {
    val path = file.path
    val basePath = project.basePath ?: ""
    return path.replace(basePath, "")
}

fun showNotification(project: Project, type: NotificationType, title: String, content: String) {
    NotificationGroupManager.getInstance()
        .getNotificationGroup("CopyRemoteFileUrl Notification Group")
        .createNotification(title, content, type, NotificationListener.URL_OPENING_LISTENER)
        .notify(project);
}


class CopyRemoteCodeLink : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor: Editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val file: VirtualFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE)
        val project = e.getRequiredData(CommonDataKeys.PROJECT)
        val repo = GitRepositoryManager.getInstance(project).repositories.first()
        val primaryCaret: Caret = editor.caretModel.primaryCaret

        if (repo != null) {
            val linePath = getLinePath(primaryCaret)
            val repositoryPath = getRepositoryPath(repo)
            val relativePath = getRelativePath(project, file)
            val currentRevision = repo.currentRevision
            val remoteCodeLink =
                "https://github.com/$repositoryPath/blob/$currentRevision$relativePath$linePath"

            // Copy to clipboard
            CopyPasteManager.getInstance().setContents(StringSelection(remoteCodeLink));
            primaryCaret.removeSelection()

            showNotification(
                project,
                NotificationType.INFORMATION,
                "Copied remote code link",
                "<a href='$remoteCodeLink'>$remoteCodeLink</a>."
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
