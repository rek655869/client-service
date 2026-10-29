package com.github.rek655869.client_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.github.rek655869.client_service.controllers.ClientController;
import com.github.rek655869.client_service.dto.ClientAccountCreateDto;
import com.github.rek655869.client_service.dto.ClientAccountResponseDto;
import com.github.rek655869.client_service.dto.ClientAccountUpdateDto;
import com.github.rek655869.client_service.enums.ClientGender;
import com.github.rek655869.client_service.enums.ClientStatus;
import com.github.rek655869.client_service.services.ClientAccountService;
import com.github.rek655869.client_service.services.ClientNotFoundException;

@WebMvcTest(ClientController.class)
public class ClientControllerTest {
        private static final String URL = "/api/clients";
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ClientAccountService service;

        @Nested
        class CreateClient {

                @Test
                void shouldCreateClientWithActiveStatus() throws Exception {
                        var dateTime = OffsetDateTime.now();
                        var response = new ClientAccountResponseDto(1L, "Имя1",
                                        ClientGender.M, ClientStatus.ACTIVE, dateTime, dateTime);

                        when(service.createClient(any(ClientAccountCreateDto.class))).thenReturn(response);

                        mockMvc.perform(post(URL)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"fullName\": \"Имя1\",\"gender\":\"M\"}"))
                                        .andExpect(status().isCreated())
                                        .andExpect(jsonPath("$.id").value(response.id()))
                                        .andExpect(jsonPath("$.fullName").value(response.fullName()))
                                        .andExpect(jsonPath("$.gender").value(response.gender().toString()))
                                        .andExpect(jsonPath("$.status").value(response.status().toString()))
                                        .andExpect(jsonPath("$.createDttm")
                                                        .value(response.createDttm().format(FORMATTER)))
                                        .andExpect(jsonPath("$.modifyDttm")
                                                        .value(response.modifyDttm().format(FORMATTER)));

                        ArgumentCaptor<ClientAccountCreateDto> captor = ArgumentCaptor
                                        .forClass(ClientAccountCreateDto.class);
                        verify(service).createClient(captor.capture());

                        assertEquals(captor.getValue().fullName(), response.fullName());
                        assertEquals(captor.getValue().gender(), response.gender());
                }

                @ParameterizedTest
                @ValueSource(strings = { "", " " })
                void shouldReturn400WhenFullNameIsBlank(String fullName) throws Exception {
                        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"fullName\": \"" + fullName + "\",\"gender\":\"M\"}"))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @Test
                void shouldReturn400WhenFullNameIsMissing() throws Exception {
                        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"gender\":\"M\"}"))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @Test
                void shouldReturn400WhenFullNameIsNull() throws Exception {
                        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"fullName\": null, \"gender\":\"M\"}"))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @Test
                void shouldReturn400WhenGenderDouesNotExist() throws Exception {
                        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"fullName\": \"Имя1\",\"gender\":\"S\"}"))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @Test
                void shouldReturn400WhenGenderIsMissing() throws Exception {
                        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"fullName\":\"Имя1\"}"))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @Test
                void shouldReturn400WhenGenderIsNull() throws Exception {
                        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"fullName\": \"Имя1\", \"gender\":\"null\"}"))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @Test
                void shouldReturnAllErrorsWhenBothFieldsInvalid() throws Exception {
                        mockMvc.perform(post(URL)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{}"))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @Test
                void shouldReturn415WhenContentTypeUnsupported() throws Exception {
                        mockMvc.perform(post(URL)
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .content("Имя1"))
                                        .andExpect(status().isUnsupportedMediaType());

                        verifyNoInteractions(service);
                }

                @Test
                void shouldReturn400WhenBodyMissing() throws Exception {
                        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @Test
                void shouldReturn400WhenJsonIncorrect() throws Exception {
                        mockMvc.perform(post(URL)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{fullName: "))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

        }

        @Nested
        class GetClients {

                @Test
                void shouldReturnListOfClients() throws Exception {
                        var dateTime = OffsetDateTime.now();
                        var firstDto = new ClientAccountResponseDto(1L, "Имя1",
                                        ClientGender.M, ClientStatus.ACTIVE, dateTime, dateTime);
                        var secondDto = new ClientAccountResponseDto(2L, "Имя2",
                                        ClientGender.F, ClientStatus.INACTIVE, dateTime, dateTime);

                        when(service.getAllClients()).thenReturn(List.of(firstDto, secondDto));

                        mockMvc.perform(get(URL))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.length()").value(2))

                                        .andExpect(jsonPath("$[0].id").value(firstDto.id()))
                                        .andExpect(jsonPath("$[0].fullName").value(firstDto.fullName()))
                                        .andExpect(jsonPath("$[0].gender").value(firstDto.gender().toString()))
                                        .andExpect(jsonPath("$[0].status").value(firstDto.status().toString()))
                                        .andExpect(jsonPath("$[0].createDttm")
                                                        .value(firstDto.createDttm().format(FORMATTER)))
                                        .andExpect(jsonPath("$[0].modifyDttm")
                                                        .value(firstDto.modifyDttm().format(FORMATTER)))

                                        .andExpect(jsonPath("$[1].id").value(secondDto.id()))
                                        .andExpect(jsonPath("$[1].fullName").value(secondDto.fullName()))
                                        .andExpect(jsonPath("$[1].gender").value(secondDto.gender().toString()))
                                        .andExpect(jsonPath("$[1].status").value(secondDto.status().toString()))
                                        .andExpect(jsonPath("$[1].createDttm")
                                                        .value(secondDto.createDttm().format(FORMATTER)))
                                        .andExpect(jsonPath("$[1].modifyDttm")
                                                        .value(secondDto.modifyDttm().format(FORMATTER)));

                        verify(service, times(1)).getAllClients();
                }

                @Test
                void shouldReturnEmptyListWhenNoClients() throws Exception {
                        when(service.getAllClients()).thenReturn(List.of());

                        mockMvc.perform(get(URL))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$").isArray())
                                        .andExpect(jsonPath("$.length()").value(0));

                        verify(service).getAllClients();
                }

                @Test
                void shouldReturnSingleClient() throws Exception {
                        var client = new ClientAccountResponseDto(1L, "Имя1",
                                        ClientGender.M, ClientStatus.ACTIVE, OffsetDateTime.now(),
                                        OffsetDateTime.now());

                        when(service.getAllClients()).thenReturn(List.of(client));

                        mockMvc.perform(get(URL))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.length()").value(1))
                                        .andExpect(jsonPath("$[0].fullName").value(client.fullName()));
                }
        }

        @Nested
        class GetClientById {

                @Test
                void shouldReturnClientWhenFound() throws Exception {
                        var response = new ClientAccountResponseDto(1L, "Имя1",
                                        ClientGender.M, ClientStatus.ACTIVE, OffsetDateTime.now(),
                                        OffsetDateTime.now());

                        when(service.getClientById(1L)).thenReturn(response);

                        mockMvc.perform(get(URL + "/1"))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.id").value(response.id()))
                                        .andExpect(jsonPath("$.fullName").value(response.fullName()))
                                        .andExpect(jsonPath("$.gender").value(response.gender().toString()))
                                        .andExpect(jsonPath("$.status").value(response.status().toString()))
                                        .andExpect(jsonPath("$.createDttm")
                                                        .value(response.createDttm().format(FORMATTER)))
                                        .andExpect(jsonPath("$.modifyDttm")
                                                        .value(response.modifyDttm().format(FORMATTER)));
                        ;

                        verify(service).getClientById(1L);
                }

                @Test
                void shouldReturn404WhenClientNotFound() throws Exception {
                        when(service.getClientById(999L)).thenThrow(new ClientNotFoundException());

                        mockMvc.perform(get(URL + "/999"))
                                        .andExpect(status().isNotFound());

                        verify(service).getClientById(999L);
                }

                @ParameterizedTest
                @ValueSource(ints = { -1, 0 })
                void shouldReturn400WhenIdIsNegativeOrZero(int id) throws Exception {
                        mockMvc.perform(get(URL + "/" + id))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @ParameterizedTest
                @ValueSource(strings = { "abc", "1.5", "1a" })
                void shouldReturn400WhenIdIsNotANumber(String id) throws Exception {
                        mockMvc.perform(get(URL + "/" + id))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }
        }

        @Nested
        class UpdateClient {

                private static final String BODY = "{\"fullName\": \"Имя1\",\"gender\":\"M\",\"status\":\"ACTIVE\"}";

                @Test
                void shouldUpdateClient() throws Exception {
                        var response = new ClientAccountResponseDto(1L, "Имя1",
                                        ClientGender.M, ClientStatus.ACTIVE, OffsetDateTime.now(),
                                        OffsetDateTime.now());

                        when(service.updateClient(eq(1L), any(ClientAccountUpdateDto.class)))
                                        .thenReturn(response);

                        mockMvc.perform(put(URL + "/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(BODY))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.id").value(1))
                                        .andExpect(jsonPath("$.fullName").value(response.fullName()))
                                        .andExpect(jsonPath("$.gender").value(response.gender().toString()))
                                        .andExpect(jsonPath("$.status").value(response.status().toString()))
                                        .andExpect(jsonPath("$.createDttm")
                                                        .value(response.createDttm().format(FORMATTER)))
                                        .andExpect(jsonPath("$.modifyDttm")
                                                        .value(response.modifyDttm().format(FORMATTER)));

                        ArgumentCaptor<ClientAccountUpdateDto> captor = ArgumentCaptor
                                        .forClass(ClientAccountUpdateDto.class);
                        verify(service).updateClient(eq(1L), captor.capture());

                        assertEquals(captor.getValue().fullName(), response.fullName());
                        assertEquals(captor.getValue().gender(), response.gender());
                        assertEquals(captor.getValue().status(), response.status());
                }

                @Test
                void shouldReturn404WhenClientNotFound() throws Exception {
                        when(service.updateClient(eq(999L), any(ClientAccountUpdateDto.class)))
                                        .thenThrow(new ClientNotFoundException());

                        mockMvc.perform(put(URL + "/999")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(BODY))
                                        .andExpect(status().isNotFound());

                        verify(service).updateClient(eq(999L), any(ClientAccountUpdateDto.class));
                }

                @ParameterizedTest
                @ValueSource(strings = { "-1", "0" })
                void shouldReturn400WhenIdIsNotPositive(String id) throws Exception {
                        mockMvc.perform(put(URL + "/" + id)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(BODY))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @ParameterizedTest
                @ValueSource(strings = { "abc", "1.5", "1a", "99999999999999999999" })
                void shouldReturn400WhenIdIsInvalid(String id) throws Exception {
                        mockMvc.perform(put(URL + "/" + id)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(BODY))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @ParameterizedTest
                @ValueSource(strings = { "", " " })
                void shouldReturn400WhenFullNameIsBlank(String fullName) throws Exception {
                        mockMvc.perform(put(URL + "/1").contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"fullName\": \"" + fullName + "\",\"gender\":\"M\"}"))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @Test
                void shouldReturn400WhenGenderDoesNotExist() throws Exception {
                        mockMvc.perform(put(URL + "/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"fullName\": \"Имя1\",\"gender\":\"S\"}"))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @Test
                void shouldReturn400WhenStatusDoesNotExist() throws Exception {
                        mockMvc.perform(put(URL + "/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"fullName\": \"Имя1\",\"status\":\"UNKNOWN\"}"))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @Test
                void shouldReturn400WhenBodyMissing() throws Exception {
                        mockMvc.perform(put(URL + "/1").contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @Test
                void shouldReturn400WhenJsonIncorrect() throws Exception {
                        mockMvc.perform(put(URL + "/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{fullName: "))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @Test
                void shouldReturn415WhenContentTypeUnsupported() throws Exception {
                        mockMvc.perform(put(URL + "/1")
                                        .contentType(MediaType.TEXT_PLAIN)
                                        .content("Имя1"))
                                        .andExpect(status().isUnsupportedMediaType());

                        verifyNoInteractions(service);
                }
        }

        @Nested
        class DeleteClient {

                @Test
                void shouldDeleteClient() throws Exception {
                        mockMvc.perform(delete(URL + "/1"))
                                        .andExpect(status().isNoContent())
                                        .andExpect(content().string(""));

                        verify(service).deleteClient(1L);
                }

                @Test
                void shouldReturn404WhenClientNotFound() throws Exception {
                        doThrow(new ClientNotFoundException())
                                        .when(service)
                                        .deleteClient(999L);

                        mockMvc.perform(delete(URL + "/999"))
                                        .andExpect(status().isNotFound());

                        verify(service).deleteClient(999L);
                }

                @ParameterizedTest
                @ValueSource(strings = { "-1", "0" })
                void shouldReturn400WhenIdIsNotPositive(String id) throws Exception {
                        mockMvc.perform(delete(URL + "/" + id))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @ParameterizedTest
                @ValueSource(strings = { "abc", "1.5", "1a", "99999999999999999999" })
                void shouldReturn400WhenIdIsInvalid(String id) throws Exception {
                        mockMvc.perform(delete(URL + "/" + id))
                                        .andExpect(status().isBadRequest());

                        verifyNoInteractions(service);
                }

                @Test
                void shouldReturn405WhenDeletingWithoutId() throws Exception {
                        mockMvc.perform(delete(URL))
                                        .andExpect(status().isMethodNotAllowed());

                        verifyNoInteractions(service);
                }
        }

}
