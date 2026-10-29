package com.github.rek655869.client_service.services;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ClientNotFoundException extends ResponseStatusException {
    
    public ClientNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Клиент не найден");
    }
}
