package com.github.rek655869.client_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.rek655869.client_service.entities.ClientAccount;

public interface ClientAccountRepository extends JpaRepository<ClientAccount, Long> {
}
