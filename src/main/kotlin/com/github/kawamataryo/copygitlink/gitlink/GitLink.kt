import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Caret
import git4idea.repo.GitRepositoryManager

class GitLink(actionEvent: AnActionEvent) {
    val project = actionEvent.getRequiredData(CommonDataKeys.PROJECT)
    val caret: Caret =
        actionEvent.getRequiredData(CommonDataKeys.EDITOR).caretModel.primaryCaret
    private val virtualFile = actionEvent.getRequiredData(CommonDataKeys.VIRTUAL_FILE)
    private val repo =
        GitRepositoryManager.getInstance(project).getRepositoryForFileQuick(virtualFile)

    val hasRepository = repo != null

    val linePath: String
        get() {
            val start = caret.selectionStartPosition.getLine() + 1
            val end =
                if (caret.selectionEndPosition.leansRight) caret.selectionEndPosition.line + 1 else caret.selectionEndPosition.line
            return if (start == end) "#L$start" else "#L$start-L$end"
        }

    val repositoryPath: String
        get() {
            val url = repo?.remotes?.first()?.firstUrl ?: ""
            val result =
                Regex(".*(github|gitlab|bitbucket)\\.(com|org).([^/]+/[^/]+).*\\.git").matchEntire(
                    url
                )
            return result?.groupValues?.get(3) ?: ""
        }

    val relativePath: String
        get() {
            val path = virtualFile.path
            val basePath = project.basePath ?: ""
            return path.replace(basePath, "")
        }

    val permalink: String
        get() {
            return "https://github.com/$repositoryPath/blob/${repo?.currentRevision}$relativePath$linePath"
        }
}


