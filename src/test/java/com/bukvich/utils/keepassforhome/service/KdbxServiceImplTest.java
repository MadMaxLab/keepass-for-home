/*
 * Copyright (c) 2021 Maxim Bukvich
 */

package com.bukvich.utils.keepassforhome.service;

import com.bukvich.utils.keepassforhome.configuration.DatabasesConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.linguafranca.pwdb.kdbx.KdbxCreds;
import org.linguafranca.pwdb.kdbx.simple.SimpleDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KdbxServiceImplTest {

    private static final String TEST_PASSWORD = "password";
    private static final String EXISTED_DB_NAME = "TestDbName";

    private DatabasesConfig dbConfig;

    private KdbxService kdbxService;

    @TempDir
    File tempDir;

    @BeforeEach
    void init() throws IOException {
        String dbFullPath = tempDir.getAbsolutePath() + "/test.kdbx";
        Map<String, String> availableDatabases = Map.of(EXISTED_DB_NAME, dbFullPath);
        dbConfig = new DatabasesConfig();
        dbConfig.setAvailableDatabases(availableDatabases);
        kdbxService = new KdbxServiceImpl(dbConfig);

        SimpleDatabase db = new SimpleDatabase();
        db.setName(EXISTED_DB_NAME);
        try (FileOutputStream fos = new FileOutputStream(dbFullPath)) {
            db.save(new KdbxCreds(TEST_PASSWORD.getBytes(StandardCharsets.UTF_8)), fos);
        }
    }

    @Test
    void openCorrectDbShouldNotThrow() {
        assertThatCode(() -> kdbxService.openDb(EXISTED_DB_NAME, TEST_PASSWORD))
                .doesNotThrowAnyException();
    }

    @Test
    void openDbWithIncorrectPasswordShouldThrow() {
        assertThatThrownBy(() -> kdbxService.openDb(EXISTED_DB_NAME, "InvalidPassword"))
                .isInstanceOf(Exception.class);
    }

}