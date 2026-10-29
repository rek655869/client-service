package com.github.rek655869.client_service.dto;

import com.github.rek655869.client_service.enums.ClientGender;
import com.github.rek655869.client_service.enums.ClientStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Модель данных для обновления информации о клиенте")
public record ClientAccountUpdateDto(

        @Schema(
            description = "ФИО", 
            example = "Иванов Иван Иванович"
        ) 
        @Pattern(regexp = ".*\\S.*", message = "ФИО не может быть пустым") 
        String fullName,

        @Schema(
            description = "Пол", 
            example = "M"
        ) 
        ClientGender gender,

        @Schema(
            description = "Статус", 
            example = "ACTIVE"
        ) ClientStatus status
) {
}