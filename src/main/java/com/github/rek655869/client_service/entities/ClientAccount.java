package com.github.rek655869.client_service.entities;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.github.rek655869.client_service.enums.ClientGender;
import com.github.rek655869.client_service.enums.ClientStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(comment = "Идентификатор")
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false, comment = "ФИО")
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(length = 1, comment = "Пол")
    private ClientGender gender;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, comment = "Статус")
    private ClientStatus status;

    @CreationTimestamp
    @Column(nullable = false, comment = "Дата создания")
    private OffsetDateTime createDttm;

    @UpdateTimestamp
    @Column(nullable = false, comment = "Дата обновления")
    private OffsetDateTime modifyDttm;
}
