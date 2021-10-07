import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.repo.GitRepository
import java.awt.datatransfer.StringSelection

fun getLinePath(caret: Caret): String {
    val start = caret.selectionStartPosition.getLine() + 1
    val end =
        if (caret.selectionEndPosition.leansRight) caret.selectionEndPosition.line + 1 else caret.selectionEndPosition.line
    return if (start == end) "#L$start" else "#L$start-L$end"
}

fun getRepositoryPath(repo: GitRepository): String {
    val url = repo.remotes.first().firstUrl ?: ""
    val result = Regex(".*(github|gitlab|bitbucket)\\.(com|org).([^/]+/[^/]+).*\\.git").matchEntire(url)
    return result?.groupValues?.get(3) ?: ""
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

fun copyToClipboard(text: String) {
    CopyPasteManager.getInstance().setContents(StringSelection(text))
}

fun truncateText(text: String, maxLength: Int): String {
    if(text.length < maxLength) {
        return text
    }
    return "${text.substring(0, maxLength)}..."
}

fun getPermalink(repositoryPath: String, revision: String, filePath: String, linePath: String): String {
    return "https://github.com/$repositoryPath/blob/$revision$filePath$linePath"
}
