package com.CoffeeGroup.Coffee.System.common;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsEntity;
import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.auth.permissions.*;
import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermitRepository permitRepository;
    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;

    /*con esto lo que hago es iniciar con un super admin, en la env variable agregan un email y una pass ficticia para el login,
    asi se loguean, cuando hacemos un /api/auth/register solo puede registrarse un usuario, la cafeteria la registramos nosotros con otro endpoint que falta,
    el login sirve para todos, hay un preautorize de get users para testear que anden los roles,
     que si ponen el token del usuario no los deja usar ese endpoint, pero si ponen
    el token de admin, si los deja

    pongan la env
    SUPERADMIN_EMAIL
    SUPERADMIN_PASSWORD

    mañana agregare que cuando iniciamos un usuario se cree tambien todo lo de cliente
    y que cuando iniciamos un usuario con rol cafeteria se inicie la cafeteria con sus respectivos datos*/


    @Value("${app.superadmin.email}")
    private String superAdminEmail;

    @Value("${app.superadmin.password}")
    private String superAdminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        Map<Permits, PermitEntity> permits = createPermits();

        RoleEntity clienteRole = createRole(
                Roles.ROLE_CLIENTE,
                Set.of(
                        Permits.VER_PRODUCTOS,
                        Permits.CREAR_COMPRA,
                        Permits.VER_COMPRAS_PROPIAS
                ),
                permits
        );

        RoleEntity cafeteriaRole = createRole(
                Roles.ROLE_CAFETERIA,
                Set.of(
                        Permits.VER_PRODUCTOS,
                        Permits.CREAR_PRODUCTOS,
                        Permits.ACTUALIZAR_PRODUCTOS,
                        Permits.VER_COMPRAS_CAFETERIA
                ),
                permits
        );

        RoleEntity superAdminRole = createRole(
                Roles.ROLE_SUPERADMIN,
                Set.of(Permits.values()),
                permits
        );

        createSuperAdmin(superAdminRole);
    }

    private Map<Permits, PermitEntity> createPermits() {
        Map<Permits, PermitEntity> permitsMap = new HashMap<>();

        for (Permits permit : Permits.values()) {
            PermitEntity permitEntity = permitRepository.findByPermit(permit)
                    .orElseGet(() -> permitRepository.save(
                            PermitEntity.builder()
                                    .permit(permit)
                                    .build()
                    ));

            permitsMap.put(permit, permitEntity);
        }

        return permitsMap;
    }

    private RoleEntity createRole(
            Roles roleName,
            Set<Permits> permits,
            Map<Permits, PermitEntity> permitsMap
    ) {
        RoleEntity role = roleRepository.findByRole(roleName)
                .orElseGet(() -> roleRepository.save(new RoleEntity(roleName)));

        for (Permits permit : permits) {
            role.addPermit(permitsMap.get(permit));
        }

        return roleRepository.save(role);
    }

    private void createSuperAdmin(RoleEntity superAdminRole) {
        if (credentialsRepository.existsByUsername(superAdminEmail)) {
            return;
        }

        Email email = new Email(superAdminEmail);

        UserEntity user = UserEntity.builder()
                .externalId(UUID.randomUUID())
                .email(email)
                .state(true)
                .creationDate(new Date())
                .build();

        UserEntity savedUser = userRepository.save(user);

        CredentialsEntity credentials = CredentialsEntity.builder()
                .username(superAdminEmail)
                .password(passwordEncoder.encode(superAdminPassword))
                .enabled(true)
                .usuario(savedUser)
                .roles(Set.of(superAdminRole))
                .build();

        credentialsRepository.save(credentials);
    }
}