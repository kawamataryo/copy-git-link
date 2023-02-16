import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.ProperTextRange
import git4idea.repo.GitRepositoryManager

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
            val result =
                Regex(".*(?:@|\\/\\/)(.[^:\\/]*)(?::[0-9]{1,4}).([^\\.]+)\\.git").matchEntire(
                    url
                )
            return result?.groupValues?.get(1) + "/" + result?.groupValues?.get(2) ?: ""
        }

    val relativePath: String
        get() {
            val path = virtualFile.path
            return path.replace(repo?.root?.path ?: "", "")
        }

    val permalink: String
        get() {
            return "https://$repositoryPath/blob/${repo?.currentRevision}$relativePath$linePath"
        }
    val branchLink: String
        get() {
            return "https://$repositoryPath/blob/${repo?.currentBranch}$relativePath$linePath"
        }

    val source:String
        get(){
            val logicalStartPosition = editor.visualToLogicalPosition(caret.selectionStartPosition)
            val logicalEndPosition = editor.visualToLogicalPosition(caret.selectionEndPosition)

            val startOffset = editor.document.getLineStartOffset(logicalStartPosition.line)
            val endOffset = editor.document.getLineEndOffset(logicalEndPosition.line)
            return editor.document.getText(ProperTextRange(startOffset, endOffset))
        }
}


