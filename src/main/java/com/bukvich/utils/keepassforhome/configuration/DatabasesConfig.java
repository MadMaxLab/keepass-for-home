/*
 * Copyright (c) 2021 Maxim Bukvich
 */

package com.bukvich.utils.keepassforhome.configuration;


import lombok.Data;

import java.util.Map;

@Data
public class DatabasesConfig {

    private Map<String, String> availableDatabases;
}
