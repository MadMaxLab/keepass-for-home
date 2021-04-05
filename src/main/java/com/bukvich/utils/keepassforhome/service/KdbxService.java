/*
 * Copyright (c) 2021 Maxim Bukvich
 */

package com.bukvich.utils.keepassforhome.service;

/**
 * Service to work with kdbx database
 *
 * @author Maxim Bukvich
 */
public interface KdbxService {

    void openDb(String dbName, String password) throws Exception;

}
