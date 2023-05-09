package com.github.kawamataryo.copygitlink.utils

import getRepositoryPathFromRemoteUrl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class UtilsKtTest {

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
    )
    fun testGetRepositoryPathFromRemoteUrl(remoteUrl: String, expectedPath: String) {
        val actualPath = getRepositoryPathFromRemoteUrl(remoteUrl)
        assertEquals(expectedPath, actualPath)
    }
}
