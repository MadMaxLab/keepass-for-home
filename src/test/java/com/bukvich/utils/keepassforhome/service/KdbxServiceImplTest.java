/*
 * Copyright (c) 2021 Maxim Bukvich
 */

package com.bukvich.utils.keepassforhome.service;

import com.bukvich.utils.keepassforhome.configuration.DatabasesConfig;
import com.bukvich.utils.keepassforhome.dto.EntryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.linguafranca.pwdb.kdbx.KdbxCreds;
import org.linguafranca.pwdb.kdbx.simple.SimpleDatabase;
import org.linguafranca.pwdb.kdbx.simple.SimpleEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KdbxServiceImplTest {

    private static final String EXISTED_DB_PASSWORD = "password";
    private static final String EXISTED_DB_NAME = "TestDbName";
    private static final String EXISTED_ENTRY_NAME = "TestEntryName";
    private static final String EXISTED_ENTRY_USER_NAME = "TestUserName";
    private static final String EXISTED_ENTRY_PASSWORD = "TestEntryPassword";

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
        SimpleEntry entry = db.newEntry(EXISTED_ENTRY_NAME);
        entry.setUsername(EXISTED_ENTRY_USER_NAME);
        entry.setPassword(EXISTED_ENTRY_PASSWORD);
        db.getRootGroup().addEntry(entry);
        try (FileOutputStream fos = new FileOutputStream(dbFullPath)) {
            db.save(new KdbxCreds(EXISTED_DB_PASSWORD.getBytes(StandardCharsets.UTF_8)), fos);
        }
    }

    @Test
    void openCorrectDbShouldNotThrow() {
        assertThatCode(() -> kdbxService.openDb(EXISTED_DB_NAME, EXISTED_DB_PASSWORD))
                .doesNotThrowAnyException();
    }

    @Test
    void openDbWithIncorrectPasswordShouldThrow() {
        assertThatThrownBy(() -> kdbxService.openDb(EXISTED_DB_NAME, "InvalidPassword"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void findExistedEntryShouldReturnEntry() throws Exception {
        kdbxService.openDb(EXISTED_DB_NAME, EXISTED_DB_PASSWORD);
        List<EntryDto> actualResult = kdbxService.findEntry(EXISTED_DB_NAME, EXISTED_ENTRY_NAME);

        assertThat(actualResult)
                .hasSize(1);
        EntryDto actualDto = actualResult.get(0);
        assertThat(actualDto.getTitle())
                .isEqualTo(EXISTED_ENTRY_NAME);
        assertThat(actualDto.getUserName())
                .isEqualTo(EXISTED_ENTRY_USER_NAME);
    }

    @Test
    void findNonExistedEntryShouldReturnEmptyList() throws Exception {
        String nonExistedEntryName = "Some non existed entry name";
        kdbxService.openDb(EXISTED_DB_NAME, EXISTED_DB_PASSWORD);
        List<EntryDto> actualResult = kdbxService.findEntry(EXISTED_DB_NAME, nonExistedEntryName);

        assertThat(actualResult)
                .isEmpty();
    }

    @Test
    void findEntryShouldThrowIfDbIsNotOpened() {
        assertThatThrownBy(() -> kdbxService.findEntry(EXISTED_DB_NAME, EXISTED_ENTRY_NAME))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void findEntryShouldThrowIfDbIsNotExisted() {
        String nonExistedDbName = "Some non existed db name";
        assertThatThrownBy(() -> kdbxService.findEntry(nonExistedDbName, EXISTED_ENTRY_NAME))
                .isInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void findEntryShouldThrowIfDbNameNullOrEmpty(String dbName) {
        assertThatThrownBy(() -> kdbxService.findEntry(dbName, EXISTED_ENTRY_NAME))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void findEntryShouldThrowIfEntryNameNullOrEmpty(String entryName) {
        assertThatThrownBy(() -> kdbxService.findEntry(EXISTED_DB_NAME, entryName))
                .isInstanceOf(IllegalArgumentException.class);
    }
}