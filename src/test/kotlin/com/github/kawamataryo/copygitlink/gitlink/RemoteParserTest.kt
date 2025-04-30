package com.github.kawamataryo.copygitlink.gitlink

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class RemoteParserTest {

    @ParameterizedTest
    @CsvSource(
        "git@github.com:user/repo.git, github.com/user/repo, GITHUB",
        "https://github.com/user/repo.git, github.com/user/repo, GITHUB",
        "git@github.com:user/repo, github.com/user/repo, GITHUB",
        "https://github.com/user/repo, github.com/user/repo, GITHUB",
        "git@bitbucket.org:user/repo.git, bitbucket.org/user/repo, BITBUCKET",
        "https://bitbucket.org/user/repo.git, bitbucket.org/user/repo, BITBUCKET",
        "ssh://git@github.com:user/repo.git, github.com/user/repo, GITHUB",
        "git://github.com/user/repo.git, github.com/user/repo, GITHUB",
        "https://github.com/YandexClassifieds/projectname, github.com/YandexClassifieds/projectname, GITHUB",
        "https://gitlab.self-hosted.com:8443/projgroup/projectname, gitlab.self-hosted.com/projgroup/projectname, GITLAB",
        "https://dev.azure.com/org/project/_git/repo, dev.azure.com/org/project/_git/repo, AZURE_DEVOPS",
        "https://org@dev.azure.com/org/project/_git/repo, dev.azure.com/org/project/_git/repo, AZURE_DEVOPS",
        "git@ssh.dev.azure.com:v3/org/project/repo, dev.azure.com/v3/org/project/repo, AZURE_DEVOPS",
        "git@ssh.dev.azure.com:v3/org/project/repo.with.dots, dev.azure.com/v3/org/project/repo.with.dots, AZURE_DEVOPS"
    )
    fun testParseRemoteUrl(remoteUrl: String, expectedPath: String, expectedHostType: String) {
        val result = parseRemoteUrl(remoteUrl)
        assertEquals(expectedPath, result.path, "Path does not match for URL: $remoteUrl")
        assertEquals(HostType.valueOf(expectedHostType), result.hostType, "Host type does not match for URL: $remoteUrl")
    }

    @ParameterizedTest
    @CsvSource(
        "https://dev.azure.com/org/project/_git/repo, org, project, repo",
        "https://org@dev.azure.com/org/project/_git/repo, org, project, repo",
        "git@ssh.dev.azure.com:v3/org/project/repo, org, project, repo",
        "git@ssh.dev.azure.com:v3/org/project/repo.with.dots, org, project, repo.with.dots"
    )
    fun testParseAzureDevOpsFields(remoteUrl: String, expectedOrg: String, expectedProject: String, expectedRepo: String) {
        val result = parseRemoteUrl(remoteUrl)
        assertEquals(expectedOrg, result.organization, "Organization does not match for URL: $remoteUrl")
        assertEquals(expectedProject, result.project, "Project does not match for URL: $remoteUrl")
        assertEquals(expectedRepo, result.repository, "Repository does not match for URL: $remoteUrl")
    }
}
