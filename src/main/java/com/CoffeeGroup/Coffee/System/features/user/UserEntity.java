package com.CoffeeGroup.Coffee.System.features.user;

import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.role.RoleEntity;
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

    @Column(name = "password")
    private String password;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email_address", unique = true, nullable = false))
    private Email email;

    @Column(name = "state")
    private Boolean state;

    @Column(name = "creation_date")
    private Date creationDate;

    //Relacion Rol


    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @ManyToMany(mappedBy = "users")
    private List<CafeEntity> cafes;

    @OneToOne(mappedBy = "user")
    private ClientEntity client;
}
