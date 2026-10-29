package com.github.rek655869.client_service.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.github.rek655869.client_service.dto.ClientAccountCreateDto;
import com.github.rek655869.client_service.dto.ClientAccountResponseDto;
import com.github.rek655869.client_service.dto.ClientAccountUpdateDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RequestMapping("/api/clients")
@Validated
public interface ClientApi {

    
    @Operation(summary = "Создание записи о клиенте", description = "Принимает JSON с полями fullName, gender. " +
            "По умолчанию клиент создается со статусом ACTIVE(активный пользователь). " +
            "В ответе возвращается объект созданного клиента.")
    @PostMapping
    ResponseEntity<ClientAccountResponseDto> createClient(
        @Valid @RequestBody
        ClientAccountCreateDto clientDto);


    @Operation(summary = "Получение списка всех пользователей", description = "Возвращает JSON со списком " +
            "всех клиентов")
    @GetMapping
    List<ClientAccountResponseDto> getClients();


    @Operation(summary = "Получение клиента по идентификатору", description = "Возвращает информацию " +
            "о пользователе, если она найдена.")
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Клиент найден"),
        @ApiResponse(responseCode = "404", description = "Клиент не найден")
    })
    @GetMapping("/{id}")
    ClientAccountResponseDto getClientById(
        @Parameter(description = "Идентификатор клиента")
        @PathVariable("id") @Positive(message = "Идентификатор должен быть положительным числом")
        Long clientId);


    @Operation(summary = "Обновление информации о клиенте", description = "Принимает JSON с обновляемыми " +
            "полями. Обновляет информацию о существующем клиенте.")
    @PutMapping("/{id}")
    ClientAccountResponseDto updateClient(
        @Parameter(description = "Идентификатор клиента") 
        @PathVariable("id") @Positive(message = "Идентификатор должен быть положительным числом")
        Long clientId,

        @Valid @RequestBody 
        ClientAccountUpdateDto clientDto);


    @Operation(summary = "Удаление информации о клиенте", description = "Полностью удаляет информацию о клиенте, если он существовал.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteClient(
        @Parameter(description = "Идентификатор клиента") 
        @PathVariable("id") @Positive(message = "Идентификатор должен быть положительным числом")
        Long clientId);
}