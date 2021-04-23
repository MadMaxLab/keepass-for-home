package com.bukvich.utils.keepassforhome.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KeepassExceptionDto {
    private String message;
    private String cause;
}
