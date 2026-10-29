package com.github.rek655869.client_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.rek655869.client_service.dto.ClientAccountCreateDto;
import com.github.rek655869.client_service.dto.ClientAccountResponseDto;
import com.github.rek655869.client_service.dto.ClientAccountUpdateDto;
import com.github.rek655869.client_service.entities.ClientAccount;
import com.github.rek655869.client_service.enums.ClientGender;
import com.github.rek655869.client_service.enums.ClientStatus;
import com.github.rek655869.client_service.repositories.ClientAccountRepository;
import com.github.rek655869.client_service.services.ClientAccountService;
import com.github.rek655869.client_service.services.ClientNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ClientAccountServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-28T10:00:00Z");

    @Mock
    private ClientAccountRepository repository;

    @InjectMocks
    private ClientAccountService service;

    @Nested
    class CreateClient {
        @Test
        void shouldCreateClientWithCurrentDateTime() throws Exception {
            var dto = new ClientAccountCreateDto("Имя1", ClientGender.M);

            when(repository.save(any(ClientAccount.class))).then(returnsFirstArg());
            var result = service.createClient(dto);

            assertEquals(result.fullName(), dto.fullName());
            assertEquals(result.gender(), dto.gender());
            assertEquals(result.status(), ClientStatus.ACTIVE);
        }
    }

    @Nested
    class GetAllClients {
        @Test
        void shouldReturnAllClientsMappedToDto() {
            var dateTime = OffsetDateTime.ofInstant(NOW, ZoneOffset.UTC);
            var firstEntity = new ClientAccount(1L, "Имя1", ClientGender.M, ClientStatus.ACTIVE,
                    dateTime,
                    dateTime);
            var secondEntity = new ClientAccount(2L, "Имя2", ClientGender.F, ClientStatus.INACTIVE,
                    dateTime,
                    dateTime);

            when(repository.findAll()).thenReturn(List.of(firstEntity, secondEntity));

            List<ClientAccountResponseDto> result = service.getAllClients();

            assertEquals(result.size(), 2);

            assertEquals(result.get(0).id(), firstEntity.getId());
            assertEquals(result.get(0).fullName(), firstEntity.getFullName());
            assertEquals(result.get(0).gender(), firstEntity.getGender());
            assertEquals(result.get(0).status(), firstEntity.getStatus());

            assertEquals(result.get(1).id(), secondEntity.getId());
            assertEquals(result.get(1).fullName(), secondEntity.getFullName());
            assertEquals(result.get(1).gender(), secondEntity.getGender());
            assertEquals(result.get(1).status(), secondEntity.getStatus());
        }

        @Test
        void shouldReturnEmptyListWhenNoClients() {
            when(repository.findAll()).thenReturn(List.of());

            var result = service.getAllClients();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class GetClientById {
        @Test
        void shouldReturnClientDtoWhenClientExists() {
            var dateTime = OffsetDateTime.ofInstant(NOW, ZoneOffset.UTC);
            var entity = new ClientAccount(1L, "Имя1", ClientGender.M, ClientStatus.ACTIVE, dateTime,
                    dateTime);

            when(repository.findById(1L)).thenReturn(Optional.of(entity));

            ClientAccountResponseDto result = service.getClientById(1L);

            assertEquals(result.id(), entity.getId());
            assertEquals(result.fullName(), entity.getFullName());
            assertEquals(result.gender(), entity.getGender());
            assertEquals(result.status(), entity.getStatus());
        }

        @Test
        void shouldThrowExceptionWhenClientNotFound() {
            when(repository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ClientNotFoundException.class, () -> {
                service.getClientById(999L);
            });

            verify(repository).findById(999L);
        }
    }

    @Nested
    class UpdateClient {
        private static final OffsetDateTime OLD_DTTM = OffsetDateTime.ofInstant(Instant.parse("2026-01-01T00:00:00Z"),
                ZoneOffset.UTC);
        private ClientAccount oldEntity;

        @BeforeEach
        void setUp() {
            oldEntity = new ClientAccount(1L, "Имя1", ClientGender.M,
                    ClientStatus.ACTIVE, OLD_DTTM,
                    OLD_DTTM);
        }

        @Test
        void shouldUpdateAllFieldsAndModifyDttm() {
            var dto = new ClientAccountUpdateDto("Имя2", ClientGender.F,
                    ClientStatus.INACTIVE);
            when(repository.findById(1L)).thenReturn(Optional.of(oldEntity));

            var result = service.updateClient(1L, dto);

            assertEquals(result.id(), 1L);
            assertEquals(result.fullName(), dto.fullName());
            assertEquals(result.gender(), dto.gender());
            assertEquals(result.status(), dto.status());
        }

        @Test
        void shouldUpdateOnlyFullName() {
            var dto = new ClientAccountUpdateDto("Имя2", null, null);
            when(repository.findById(1L)).thenReturn(Optional.of(oldEntity));

            var result = service.updateClient(1L, dto);

            assertEquals(result.fullName(), dto.fullName());
            assertEquals(result.gender(), oldEntity.getGender());
            assertEquals(result.status(), oldEntity.getStatus());
        }

        @Test
        void shouldUpdateOnlyGender() {
            var dto = new ClientAccountUpdateDto(null, ClientGender.F, null);
            when(repository.findById(1L)).thenReturn(Optional.of(oldEntity));

            var result = service.updateClient(1L, dto);

            assertEquals(result.fullName(), oldEntity.getFullName());
            assertEquals(result.gender(), dto.gender());
            assertEquals(result.status(), oldEntity.getStatus());
        }

        @Test
        void shouldUpdateOnlyStatus() {
            var dto = new ClientAccountUpdateDto(null, null, ClientStatus.INACTIVE);
            when(repository.findById(1L)).thenReturn(Optional.of(oldEntity));

            var result = service.updateClient(1L, dto);

            assertEquals(result.fullName(), oldEntity.getFullName());
            assertEquals(result.gender(), oldEntity.getGender());
            assertEquals(result.status(), dto.status());
        }

        @Test
        void shouldNotChangeModifyDttmWhenNothingToUpdate() {
            when(repository.findById(1L)).thenReturn(Optional.of(oldEntity));

            var result = service.updateClient(1L, new ClientAccountUpdateDto(null, null, null));

            assertEquals(result.fullName(), oldEntity.getFullName());
            assertEquals(result.gender(), oldEntity.getGender());
            assertEquals(result.status(), oldEntity.getStatus());
        }

        @Test
        void shouldSaveUpdatedEntity() {
            var dto = new ClientAccountUpdateDto("Имя2", ClientGender.F,
                    ClientStatus.INACTIVE);
            when(repository.findById(1L)).thenReturn(Optional.of(oldEntity));

            service.updateClient(1L, dto);

            verify(repository, never()).save(any(ClientAccount.class));
        }

        @Test
        void shouldReturnEmptyOptional() {
            var dto = new ClientAccountUpdateDto("Имя2", ClientGender.F,
                    ClientStatus.INACTIVE);
            when(repository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ClientNotFoundException.class, () -> {
                service.updateClient(999L, dto);
            });

            verify(repository, never()).save(any(ClientAccount.class));
        }
    }

    @Nested
    class DeleteClient {
        @Test
        void shouldDeleteClient() {
            when(repository.existsById(1L)).thenReturn(true);

            service.deleteClient(1L);

            verify(repository).existsById(1L);
            verify(repository).deleteById(1L);
        }

        @Test
        void shouldReturnFalseAndNotDeleteWhenClientNotFound() {
            when(repository.existsById(999L)).thenReturn(false);

            assertThrows(ClientNotFoundException.class, () -> {
                service.deleteClient(999L);
            });
            verify(repository, never()).deleteById(any());
        }
    }

}
