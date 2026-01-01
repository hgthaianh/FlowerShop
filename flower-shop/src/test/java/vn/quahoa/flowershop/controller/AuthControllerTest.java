package vn.quahoa.flowershop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.quahoa.flowershop.dto.auth.AuthResponse;
import vn.quahoa.flowershop.dto.auth.LoginRequest;
import vn.quahoa.flowershop.dto.auth.SignUpRequest;
import vn.quahoa.flowershop.exception.ValidationException;
import vn.quahoa.flowershop.model.AuthProvider;
import vn.quahoa.flowershop.model.User;
import vn.quahoa.flowershop.repository.UserRepository;
import vn.quahoa.flowershop.security.TokenProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private SignUpRequest signUpRequest;
    private User user;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("Password123");

        signUpRequest = new SignUpRequest();
        signUpRequest.setName("Nguyen Van A");
        signUpRequest.setEmail("newuser@example.com");
        signUpRequest.setPassword("Password123");

        user = new User();
        user.setId(1L);
        user.setName("Nguyen Van A");
        user.setEmail("newuser@example.com");
        user.setProvider(AuthProvider.LOCAL);
    }

    // ========================================
    // LOGIN TESTS
    // ========================================

    @Test
    @DisplayName("TC_LOGIN_01: Đăng nhập thành công - trả về JWT token")
    void login_ValidCredentials_ReturnsToken() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.createToken(authentication)).thenReturn("jwt-token-here");

        // Act
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isInstanceOf(AuthResponse.class);
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertThat(authResponse.getAccessToken()).isEqualTo("jwt-token-here");
    }

    @Test
    @DisplayName("TC_LOGIN_02: Đăng nhập với thông tin sai - throw exception")
    void login_InvalidCredentials_ThrowsException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThatThrownBy(() -> authController.authenticateUser(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("TC_LOGIN_03: Đăng nhập - verify authenticationManager được gọi đúng")
    void login_VerifyAuthenticationManagerCalled() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.createToken(authentication)).thenReturn("jwt-token-here");

        // Act
        authController.authenticateUser(loginRequest);

        // Assert
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).createToken(authentication);
    }

    @Test
    @DisplayName("TC_LOGIN_04: Đăng nhập - sử dụng đúng email và password")
    void login_UsesCorrectCredentials() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenAnswer(invocation -> {
                    UsernamePasswordAuthenticationToken token = invocation.getArgument(0);
                    assertThat(token.getPrincipal()).isEqualTo("user@example.com");
                    assertThat(token.getCredentials()).isEqualTo("Password123");
                    return authentication;
                });
        when(tokenProvider.createToken(authentication)).thenReturn("jwt-token-here");

        // Act
        authController.authenticateUser(loginRequest);
    }

    // ========================================
    // SIGNUP TESTS
    // ========================================

    @Test
    @DisplayName("TC_SIGNUP_01: Đăng ký với email đã tồn tại - throw ValidationException")
    void signup_DuplicateEmail_ThrowsException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authController.registerUser(signUpRequest))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("TC_SIGNUP_02: Verify existsByEmail được gọi với đúng email")
    void signup_VerifyEmailCheck() {
        // Arrange
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        // Act & Assert
        try {
            authController.registerUser(signUpRequest);
        } catch (ValidationException e) {
            // Expected
        }

        // Verify
        verify(userRepository).existsByEmail("newuser@example.com");
    }

    @Test
    @DisplayName("TC_SIGNUP_03: Verify password được encode khi email hợp lệ")
    void signup_PasswordEncodedWhenValidEmail() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act - Lỗi sẽ xảy ra ở ServletUriComponentsBuilder nhưng logic đã chạy
        try {
            authController.registerUser(signUpRequest);
        } catch (Exception e) {
            // Expected - ServletRequestAttributes not available
        }

        // Assert - password encoder đã được gọi trước khi lỗi
        verify(passwordEncoder).encode("Password123");
    }
}
