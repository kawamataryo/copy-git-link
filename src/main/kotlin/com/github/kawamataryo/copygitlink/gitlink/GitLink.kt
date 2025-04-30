import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.ProperTextRange
import git4idea.repo.GitRepositoryManager
import git4idea.GitUtil


class GitLink(actionEvent: AnActionEvent) {
    val project = actionEvent.getRequiredData(CommonDataKeys.PROJECT)
    val caret: Caret =
        actionEvent.getRequiredData(CommonDataKeys.EDITOR).caretModel.primaryCaret
    private val virtualFile = actionEvent.getRequiredData(CommonDataKeys.VIRTUAL_FILE)
    private val repo =
        GitRepositoryManager.getInstance(project).getRepositoryForFileQuick(virtualFile)
    private val editor: Editor =
        actionEvent.getRequiredData(CommonDataKeys.EDITOR)

    val hasRepository = repo != null

    val linePath: String
        get() {
            val logicalStartPosition = editor.visualToLogicalPosition(caret.selectionStartPosition)
            val logicalEndPosition = editor.visualToLogicalPosition(caret.selectionEndPosition)
            val start = logicalStartPosition.line + 1
            val end = if(logicalEndPosition.column == 0 && logicalStartPosition.line != logicalEndPosition.line) logicalEndPosition.line else logicalEndPosition.line + 1
            return if (start == end) "#L$start" else "#L$start-L$end"
        }

    val repositoryPath: String
        get() {
            val url = repo?.remotes?.first()?.firstUrl ?: ""
            return getRepositoryPathFromRemoteUrl(url)
        }

    val relativePath: String
        get() {
            val path = virtualFile.path
            return path.replace(repo?.root?.path ?: "", "")
        }

    val permalink: String
        get() {
            return makeUrl(repo?.currentRevision ?: "")
        }
    val branchLink: String
        get() {
            return makeUrl(repo?.currentBranch ?: "")
        }

    val source:String
        get(){
            val logicalStartPosition = editor.visualToLogicalPosition(caret.selectionStartPosition)
            val logicalEndPosition = editor.visualToLogicalPosition(caret.selectionEndPosition)

            val startOffset = editor.document.getLineStartOffset(logicalStartPosition.line)
            val endOffset = editor.document.getLineEndOffset(logicalEndPosition.line)
            return editor.document.getText(ProperTextRange(startOffset, endOffset))
        }

    private fun makeUrl(ref: String): String {
        return "https://$repositoryPath/blob/$ref$relativePath$linePath"
    }
}
