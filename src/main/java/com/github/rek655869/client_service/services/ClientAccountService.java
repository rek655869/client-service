package com.github.rek655869.client_service.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.rek655869.client_service.dto.ClientAccountCreateDto;
import com.github.rek655869.client_service.dto.ClientAccountResponseDto;
import com.github.rek655869.client_service.dto.ClientAccountUpdateDto;
import com.github.rek655869.client_service.entities.ClientAccount;
import com.github.rek655869.client_service.enums.ClientStatus;
import com.github.rek655869.client_service.repositories.ClientAccountRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientAccountService {
    private final ClientAccountRepository repository;

    @Transactional
    public ClientAccountResponseDto createClient(ClientAccountCreateDto dto) {
        var client = new ClientAccount();
        client.setFullName(dto.fullName());
        client.setGender(dto.gender());
        client.setStatus(ClientStatus.ACTIVE);

        var response = convertEntityToDto(repository.save(client));
        log.info("Добавлена сущность c ID = {}", client.getId());
        return response;
    }

    @Transactional(readOnly = true)
    public List<ClientAccountResponseDto> getAllClients() {
        List<ClientAccount> allClients = repository.findAll();
        return allClients.stream().map(ClientAccountService::convertEntityToDto).toList();
    }

    @Transactional(readOnly = true)
    public ClientAccountResponseDto getClientById(
            Long id) {
        return repository.findById(id).map(ClientAccountService::convertEntityToDto)
                .orElseThrow(() -> new ClientNotFoundException());
    }

    @Transactional
    public ClientAccountResponseDto updateClient(
            Long id,
            ClientAccountUpdateDto dto) {
        return repository.findById(id).map(client -> {
            if (dto.fullName() != null) {
                client.setFullName(dto.fullName());
            }
            if (dto.gender() != null) {
                client.setGender(dto.gender());
            }
            if (dto.status() != null) {
                client.setStatus(dto.status());
            }
            repository.flush(); // чтобы обновилось modifyDttm
            var response = convertEntityToDto(client);
            log.info("Обновлена сущность c ID = {}", client.getId());
            return response;
        }).orElseThrow(() -> new ClientNotFoundException());
    }

    @Transactional
    public void deleteClient(
            Long id) {
        if (!repository.existsById(id)) {
            throw new ClientNotFoundException();
        }
        repository.deleteById(id);
        log.info("Удалена сущность с ID = {}", id);
    }

    private static ClientAccountResponseDto convertEntityToDto(ClientAccount entity) {
        return new ClientAccountResponseDto(
                entity.getId(),
                entity.getFullName(),
                entity.getGender(),
                entity.getStatus(),
                entity.getCreateDttm(),
                entity.getModifyDttm());
    }
}
