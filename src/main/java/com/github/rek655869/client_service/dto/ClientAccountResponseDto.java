package com.github.rek655869.client_service.dto;

import java.time.OffsetDateTime;

import com.github.rek655869.client_service.enums.ClientGender;
import com.github.rek655869.client_service.enums.ClientStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Модель данных с полной информацией о клиенте")
public record ClientAccountResponseDto(

        @Schema(
            description = "ID", 
            example = "324"
        ) 
        Long id,

        @Schema(
            description = "ФИО", 
            example = "Иванов Иван Иванович"
        ) 
        String fullName,

        @Schema(
            description = "Пол", 
            example = "M"
        ) 
        ClientGender gender,

        @Schema(
            description = "Статус", 
            example = "ACTIVE"
        ) 
        ClientStatus status,

        @Schema(
            description = "Дата создания", 
            example = "2026-04-01T13:58:49Z"
        ) 
        OffsetDateTime createDttm,

        @Schema(
            description = "Дата изменения", 
            example = "2026-04-01T13:58:49Z"
        ) 
        OffsetDateTime modifyDttm
) {
}