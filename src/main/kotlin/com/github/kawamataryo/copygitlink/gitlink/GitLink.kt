package com.github.kawamataryo.copygitlink.gitlink

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.ProperTextRange
import git4idea.repo.GitRepositoryManager
import git4idea.GitUtil

class GitLink(actionEvent: AnActionEvent) {
    public val project = actionEvent.getRequiredData(CommonDataKeys.PROJECT)
    private val caret: Caret = actionEvent.getRequiredData(CommonDataKeys.EDITOR).caretModel.primaryCaret
    private val virtualFile = actionEvent.getRequiredData(CommonDataKeys.VIRTUAL_FILE)
    private val repo = GitRepositoryManager.getInstance(project).getRepositoryForFileQuick(virtualFile)
    private val editor: Editor = actionEvent.getRequiredData(CommonDataKeys.EDITOR)

    val hasRepository = repo != null

    val linePath: String
        get() = makeLinkMaker().linePath

    val repositoryPath: String
        get() = makeLinkMaker().repositoryPath

    val relativePath: String
        get() = makeLinkMaker().relativePath

    val permalink: String
        get() = makeLinkMaker().permalink

    val branchLink: String
        get() = makeLinkMaker().branchLink

    val source:String
        get(){
            val logicalStartPosition = editor.visualToLogicalPosition(caret.selectionStartPosition)
            val logicalEndPosition = editor.visualToLogicalPosition(caret.selectionEndPosition)
            val startOffset = editor.document.getLineStartOffset(logicalStartPosition.line)
            val endOffset = editor.document.getLineEndOffset(logicalEndPosition.line)
            return editor.document.getText(ProperTextRange(startOffset, endOffset))
        }

    private fun makeLinkMaker(): LinkMaker {
        val repoUrl = repo?.remotes?.first()?.firstUrl ?: ""
        val repoRoot = repo?.root?.path ?: ""
        val path = virtualFile.path
        val logicalStartPosition = editor.visualToLogicalPosition(caret.selectionStartPosition)
        val logicalEndPosition = editor.visualToLogicalPosition(caret.selectionEndPosition)
        val revision = repo?.currentRevision?.toString() ?: ""
        val branch = repo?.currentBranch?.toString() ?: ""
        return LinkMaker(repoUrl, repoRoot, path, logicalStartPosition, logicalEndPosition, revision, branch)
    }
}
