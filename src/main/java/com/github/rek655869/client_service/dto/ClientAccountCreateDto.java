package com.github.rek655869.client_service.dto;

import com.github.rek655869.client_service.enums.ClientGender;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Модель данных для создания клиента")
public record ClientAccountCreateDto(

        @Schema(
            description = "ФИО", 
            example = "Иванов Иван Иванович", 
            requiredMode = Schema.RequiredMode.REQUIRED
        ) 
        @NotBlank(message = "ФИО не может быть пустым") 
        String fullName,

        @Schema(
            description = "Пол", 
            example = "M", 
            requiredMode = Schema.RequiredMode.REQUIRED
        ) 
        @NotNull(message = "Пол должен быть указан") 
        ClientGender gender

) {
}