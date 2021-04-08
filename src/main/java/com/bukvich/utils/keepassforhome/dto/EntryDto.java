/*
 * Copyright (c) 2021 Maxim Bukvich
 */

package com.bukvich.utils.keepassforhome.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class EntryDto {
    private UUID id;
    private String title;
    private String userName;
}
