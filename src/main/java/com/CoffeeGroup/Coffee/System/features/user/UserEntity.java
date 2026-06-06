package com.CoffeeGroup.Coffee.System.features.user;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsEntity;
import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true, updatable = false)
    private UUID externalId;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "email_address", unique = true, nullable = false)
    )
    private Email email;

    @Column(name = "state", nullable = false)
    private Boolean state;

    @Column(name = "creation_date", nullable = false)
    private Date creationDate;

    @OneToOne(mappedBy = "usuario")
    private CredentialsEntity credentials;

    @OneToOne(mappedBy = "user")
    private CafeEntity cafe;

    @OneToOne(mappedBy = "user")
    private ClientEntity client;
}
