package com.github.kawamataryo.copygitlink.gitlink

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class RepositoryPathUtilsTest {

    @ParameterizedTest
    @CsvSource(
        "git@github.com:user/repo.git, github.com/user/repo",
        "https://github.com/user/repo.git, github.com/user/repo",
        "git@github.com:user/repo, github.com/user/repo",
        "https://github.com/user/repo, github.com/user/repo",
        "git@bitbucket.org:user/repo.git, bitbucket.org/user/repo",
        "https://bitbucket.org/user/repo.git, bitbucket.org/user/repo",
        "ssh://git@github.com:user/repo.git, github.com/user/repo",
        "git://github.com/user/repo.git, github.com/user/repo",
        "https://github.com/YandexClassifieds/projectname, github.com/YandexClassifieds/projectname",
        "https://gitlab.self-hosted.com:8443/projgroup/projectname, gitlab.self-hosted.com/projgroup/projectname",
        "https://dev.azure.com/org/project/_git/repo, dev.azure.com/org/project/_git/repo",
        "https://org@dev.azure.com/org/project/_git/repo, dev.azure.com/org/project/_git/repo",
        "git@ssh.dev.azure.com:v3/org/project/repo, dev.azure.com/v3/org/project/repo",
        "git@ssh.dev.azure.com:v3/org/project/repo.with.dots, dev.azure.com/v3/org/project/repo.with.dots"
    )
    fun testGetRepositoryPathFromRemoteUrl(remoteUrl: String, expected: String) {
        assertEquals(expected, getRepositoryPathFromRemoteUrl(remoteUrl))
    }
}
