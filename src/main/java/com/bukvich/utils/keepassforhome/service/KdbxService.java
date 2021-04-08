/*
 * Copyright (c) 2021 Maxim Bukvich
 */

package com.bukvich.utils.keepassforhome.service;

import com.bukvich.utils.keepassforhome.dto.EntryDto;

import java.util.List;

/**
 * Service to work with kdbx database
 *
 * @author Maxim Bukvich
 */
public interface KdbxService {

    void openDb(String dbName, String password) throws Exception;

    List<EntryDto> findEntry(String dbName, String title);
}
