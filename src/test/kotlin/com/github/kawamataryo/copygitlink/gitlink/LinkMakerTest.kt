package com.github.kawamataryo.copygitlink.gitlink

import com.intellij.openapi.editor.LogicalPosition
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LinkMakerTest {
    @Test
    fun testRepositoryPath() {
        val repoUrl = "https://github.com/user/repo.git"
        val repoRoot = "/home/user/project"
        val filePath = "/home/user/project/src/Main.kt"
        val logicalStartPosition = LogicalPosition(9, 0)
        val logicalEndPosition = LogicalPosition(9, 10)
        val linkMaker = LinkMaker(repoUrl, repoRoot, filePath, logicalStartPosition, logicalEndPosition)
        assertEquals("github.com/user/repo", linkMaker.repositoryPath)
    }

    @Test
    fun testRelativePath() {
        val repoUrl = "https://github.com/user/repo.git"
        val repoRoot = "/home/user/project"
        val filePath = "/home/user/project/src/Main.kt"
        val logicalStartPosition = LogicalPosition(9, 0)
        val logicalEndPosition = LogicalPosition(9, 10)
        val linkMaker = LinkMaker(repoUrl, repoRoot, filePath, logicalStartPosition, logicalEndPosition)
        assertEquals("/src/Main.kt", linkMaker.relativePath)
    }

    @Test
    fun testLinePathSingleLine() {
        val repoUrl = "https://github.com/user/repo.git"
        val repoRoot = "/home/user/project"
        val filePath = "/home/user/project/src/Main.kt"
        val logicalStartPosition = LogicalPosition(9, 0)
        val logicalEndPosition = LogicalPosition(9, 10)
        val linkMaker = LinkMaker(repoUrl, repoRoot, filePath, logicalStartPosition, logicalEndPosition)
        assertEquals("#L10", linkMaker.linePath)
    }

    @Test
    fun testLinePathRange() {
        val repoUrl = "https://github.com/user/repo.git"
        val repoRoot = "/home/user/project"
        val filePath = "/home/user/project/src/Main.kt"
        val logicalStartPosition = LogicalPosition(4, 0)
        val logicalEndPosition = LogicalPosition(6, 5) // Non-zero column
        val linkMaker = LinkMaker(repoUrl, repoRoot, filePath, logicalStartPosition, logicalEndPosition)
        assertEquals("#L5-L7", linkMaker.linePath)
    }

    @Test
    fun testLinePathRangeWithZeroColumn() {
        val repoUrl = "https://github.com/user/repo.git"
        val repoRoot = "/home/user/project"
        val filePath = "/home/user/project/src/Main.kt"
        val logicalStartPosition = LogicalPosition(4, 0)
        val logicalEndPosition = LogicalPosition(6, 0) // Zero column
        val linkMaker = LinkMaker(repoUrl, repoRoot, filePath, logicalStartPosition, logicalEndPosition)
        assertEquals("#L5-L6", linkMaker.linePath)
    }

    @Test
    fun testPermalinkGithub() {
        val repoUrl = "https://github.com/user/repo.git"
        val repoRoot = "/home/user/project"
        val filePath = "/home/user/project/src/Main.kt"
        val logicalStartPosition = LogicalPosition(1, 0)
        val logicalEndPosition = LogicalPosition(1, 10)
        val revision = "abc123"
        val linkMaker = LinkMaker(repoUrl, repoRoot, filePath, logicalStartPosition, logicalEndPosition, revision = revision)
        assertEquals("https://github.com/user/repo/blob/abc123/src/Main.kt#L2", linkMaker.permalink)
    }

    @Test
    fun testBranchLinkGithub() {
        val repoUrl = "https://github.com/user/repo.git"
        val repoRoot = "/home/user/project"
        val filePath = "/home/user/project/src/Main.kt"
        val logicalStartPosition = LogicalPosition(2, 0)
        val logicalEndPosition = LogicalPosition(2, 10)
        val branch = "main"
        val linkMaker = LinkMaker(repoUrl, repoRoot, filePath, logicalStartPosition, logicalEndPosition, branch = branch)
        assertEquals("https://github.com/user/repo/blob/main/src/Main.kt#L3", linkMaker.branchLink)
    }

    @Test
    fun testPermalinkBitbucket() {
        val repoUrl = "https://bitbucket.org/user/repo.git"
        val repoRoot = "/home/user/bitbucketproject"
        val filePath = "/home/user/bitbucketproject/src/BitbucketMain.kt"
        val logicalStartPosition = LogicalPosition(3, 0)
        val logicalEndPosition = LogicalPosition(3, 5)
        val revision = "deadbeef"
        val linkMaker = LinkMaker(repoUrl, repoRoot, filePath, logicalStartPosition, logicalEndPosition, revision = revision)
        assertEquals("https://bitbucket.org/user/repo/blob/deadbeef/src/BitbucketMain.kt#L4", linkMaker.permalink)
    }

    @Test
    fun testBranchLinkBitbucket() {
        val repoUrl = "https://bitbucket.org/user/repo.git"
        val repoRoot = "/home/user/bitbucketproject"
        val filePath = "/home/user/bitbucketproject/src/BitbucketMain.kt"
        val logicalStartPosition = LogicalPosition(7, 0)
        val logicalEndPosition = LogicalPosition(7, 0)
        val branch = "develop"
        val linkMaker = LinkMaker(repoUrl, repoRoot, filePath, logicalStartPosition, logicalEndPosition, branch = branch)
        assertEquals("https://bitbucket.org/user/repo/blob/develop/src/BitbucketMain.kt#L8", linkMaker.branchLink)
    }
}
