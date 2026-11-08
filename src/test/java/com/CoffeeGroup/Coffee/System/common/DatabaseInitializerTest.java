package com.CoffeeGroup.Coffee.System.common;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.auth.permissions.PermitEntity;
import com.CoffeeGroup.Coffee.System.auth.permissions.PermitRepository;
import com.CoffeeGroup.Coffee.System.auth.permissions.RoleEntity;
import com.CoffeeGroup.Coffee.System.auth.permissions.RoleRepository;
import com.CoffeeGroup.Coffee.System.features.benefit.IBenefitRepository;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitFormatSeeder;
import com.CoffeeGroup.Coffee.System.features.benefit_type.IBenefitTypeRepository;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.category.ICategoryRepository;
import com.CoffeeGroup.Coffee.System.features.client.DemoClientSeeder;
import com.CoffeeGroup.Coffee.System.features.product.IProductRepository;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;
import com.CoffeeGroup.Coffee.System.features.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class DatabaseInitializerTest {

    @Mock private RoleRepository roleRepository;
    @Mock private PermitRepository permitRepository;
    @Mock private UserRepository userRepository;
    @Mock private CredentialsRepository credentialsRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TierRepository tierRepository;
    @Mock private ICategoryRepository categoryRepository;
    @Mock private CafeRepository cafeRepository;
    @Mock private IProductRepository productRepository;
    @Mock private IBenefitTypeRepository benefitTypeRepository;
    @Mock private IBenefitRepository benefitRepository;
    @Mock private BenefitFormatSeeder benefitFormatSeeder;
    @Mock private DemoClientSeeder demoClientSeeder;

    @InjectMocks
    private DatabaseInitializer initializer;

    @BeforeEach
    void setUp() {
        when(permitRepository.save(any(PermitEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(roleRepository.save(any(RoleEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(credentialsRepository.existsByUsername(any())).thenReturn(true);
    }

    @Test
    void doesNotSeedDemoAccountsByDefault() {
        initializer.run();

        verify(cafeRepository, never()).existsByEmail(any());
        verify(demoClientSeeder, never()).seed();
    }

    @Test
    void seedsDemoAccountsWhenDemoDataIsEnabled() {
        ReflectionTestUtils.setField(initializer, "demoDataEnabled", true);
        when(cafeRepository.existsByEmail(any())).thenReturn(true);

        initializer.run();

        verify(demoClientSeeder).seed();
    }
}
