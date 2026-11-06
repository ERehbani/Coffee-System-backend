package com.CoffeeGroup.Coffee.System.auth;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.CoffeeGroup.Coffee.System.auth.cookie.AuthCookieService;
import com.CoffeeGroup.Coffee.System.auth.jwt.JwtService;
import com.CoffeeGroup.Coffee.System.features.user.UserService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    private static final long ONE_HOUR_IN_MILLIS = 3_600_000L;

    @Mock
    private AuthService authService;
    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuthCookieService authCookieService = new AuthCookieService(true, "Strict", ONE_HOUR_IN_MILLIS);
        AuthController controller = new AuthController(authService, userService, jwtService, authCookieService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void loginSetsTheSessionCookieAndReturnsTheUserWithoutTheToken() throws Exception {
        when(authService.authenticate(any())).thenReturn(
                User.withUsername("cliente@test.com").password("hashed").authorities("ROLE_CLIENTE").build());
        when(jwtService.generateToken(any())).thenReturn("jwt-value");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"cliente@test.com\",\"password\":\"Cliente123\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("auth_token=jwt-value")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=Strict")))
                .andExpect(jsonPath("$.email").value("cliente@test.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_CLIENTE"))
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    void logoutExpiresTheSessionCookie() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("auth_token=")))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("Max-Age=0")));
    }

    @Test
    void meReturnsTheEmailAndRolesOfTheSession() throws Exception {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "cafe.central@test.com", null, List.of(new SimpleGrantedAuthority("ROLE_CAFETERIA")));

        mockMvc.perform(get("/auth/me").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("cafe.central@test.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_CAFETERIA"));
    }
}
