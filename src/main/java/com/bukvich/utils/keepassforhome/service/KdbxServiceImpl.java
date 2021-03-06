/*
 * Copyright (c) 2021 Maxim Bukvich
 */

package com.bukvich.utils.keepassforhome.service;

import com.bukvich.utils.keepassforhome.configuration.DatabasesConfig;
import com.bukvich.utils.keepassforhome.dto.EntryDto;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.linguafranca.pwdb.kdbx.KdbxCreds;
import org.linguafranca.pwdb.kdbx.simple.SimpleDatabase;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A standard implementation of KdbxService interface
 *
 * @author Maxim Bukvich
 */
@Service
@RequiredArgsConstructor
public class KdbxServiceImpl implements KdbxService {

    private final DatabasesConfig databasesConfig;

    private final Map<String, SimpleDatabase> dbCache = new ConcurrentHashMap<>();

    @Override
    public void openDb(String dbName, String password) throws Exception {
        if (Strings.isNullOrEmpty(dbName) || Strings.isNullOrEmpty(password)) {
            throw new IllegalArgumentException("Input arguments must be not null and not empty!");
        }
        String dbPath = databasesConfig.getAvailableDatabases().get(dbName);
        if (Strings.isNullOrEmpty(dbPath)) {
            throw new IllegalArgumentException("There is no valid config for DB: " + dbName);
        }
        SimpleDatabase db = SimpleDatabase.load(new KdbxCreds(password.getBytes(StandardCharsets.UTF_8)),
                new FileInputStream(dbPath));
        dbCache.put(dbName, db);
    }

    @Override
    public List<EntryDto> findEntry(String dbName, String title) {
        if (Strings.isNullOrEmpty(dbName) || Strings.isNullOrEmpty(title)) {
            throw new IllegalArgumentException("DbName and Title should be not null or empty!");
        }
        SimpleDatabase db = dbCache.get(dbName);
        if (db == null) {
            throw new IllegalStateException("Db " + dbName + " is not opened. Please open db before searching.");
        }
        return db.findEntries(title).stream()
                .map(simpleEntry -> new EntryDto(simpleEntry.getUuid(), simpleEntry.getTitle(), simpleEntry.getUsername()))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<String> showDbList() {
        return databasesConfig.getAvailableDatabases().keySet();
    }
}
