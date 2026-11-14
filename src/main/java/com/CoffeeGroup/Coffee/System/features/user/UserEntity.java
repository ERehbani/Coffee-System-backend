package com.CoffeeGroup.Coffee.System.features.user;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsEntity;
import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.common.persistence.AuditableEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UserEntity extends AuditableEntity {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email_address", unique = true, nullable = false))
    private Email email;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @OneToOne(mappedBy = "user")
    private CredentialsEntity credentials;

    @OneToOne(mappedBy = "user")
    private CafeEntity cafe;

    @OneToOne(mappedBy = "user")
    private ClientEntity client;

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public void deactivate() {
        active = false;
    }

    public String emailAddress() {
        return email == null ? null : email.value();
    }
}
