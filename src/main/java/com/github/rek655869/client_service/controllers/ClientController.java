package com.github.rek655869.client_service.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.github.rek655869.client_service.dto.ClientAccountCreateDto;
import com.github.rek655869.client_service.dto.ClientAccountResponseDto;
import com.github.rek655869.client_service.dto.ClientAccountUpdateDto;
import com.github.rek655869.client_service.services.ClientAccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ClientController implements ClientApi {
    private final ClientAccountService service;

    @Override
    public ResponseEntity<ClientAccountResponseDto> createClient(ClientAccountCreateDto clientDto) {
        var client = service.createClient(clientDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(client.id())
                .toUri();

        return ResponseEntity.created(location).body(client);
    }

    @Override
    public List<ClientAccountResponseDto> getClients() {
        return service.getAllClients();
    }

    @Override
    public ClientAccountResponseDto getClientById(
            Long clientId) {
        return service.getClientById(clientId);

    }

    @Override
    public ClientAccountResponseDto updateClient(
            Long clientId,
            ClientAccountUpdateDto clientDto) {
        return service.updateClient(clientId, clientDto);
    }

    @Override
    public void deleteClient(Long clientId) {
        service.deleteClient(clientId);
    }
}
