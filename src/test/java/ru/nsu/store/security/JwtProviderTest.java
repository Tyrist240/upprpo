package ru.nsu.store.security;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJws;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.nsu.store.entity.Role;
import ru.nsu.store.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.PcmVIPbcZl9j7qFzXRAeSyhtuBnHQNMuLHsaG5l804A";
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjozNTE2MjM5MDIyfQ.JrL7KOV6NWuUs5ADrX4MGVwUWq5lvYq-AmDBpQPY1HQ";

    private static final String USER_NAME = "username";

    @InjectMocks
    private JwtProvider jwtProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private UserDetailsService userDetailsService;


    @BeforeEach
    private void init() {
        jwtProvider.setSecretKey("secret");
    }

    @Test
    void createToken() {
        String result = jwtProvider.createToken("name", "role");
        assertNotNull(result);
    }

    @Test
    void validateTokenThrowsJwtAuthenticationException() {
        assertThrows(JwtAuthenticationException.class, () -> jwtProvider.validateToken(INVALID_TOKEN));
    }

    @Test
    void validateToken() {
        try (MockedStatic<Jwts> jwtsMockedStatic = Mockito.mockStatic(Jwts.class)) {
            JwtParser parserMock = mock(JwtParser.class);
            DefaultJws mockJws = mock(DefaultJws.class);
            Date mockDate = mock(Date.class);
            DefaultClaims mockClaims = mock(DefaultClaims.class);
            jwtsMockedStatic.when(Jwts::parser).thenReturn(parserMock);

            when(parserMock.setSigningKey(anyString())).thenReturn(parserMock);
            when(parserMock.parseClaimsJws(anyString())).thenReturn(mockJws);

            when(mockJws.getBody()).thenReturn(mockClaims);
            when(mockClaims.getExpiration()).thenReturn(mockDate);

            assertTrue(jwtProvider.validateToken(VALID_TOKEN));
        }
    }

    @Test
    void getAuthentication() {
        try (MockedStatic<Jwts> jwtsMockedStatic = Mockito.mockStatic(Jwts.class)) {
            User user = new User();
            user.setRoles(Set.of(Role.ADMIN));
            UserPrincipal userPrincipal = UserPrincipal.create(user);

            when(userDetailsService.loadUserByUsername(any()))
                    .thenReturn(userPrincipal);

            JwtParser parserMock = mock(JwtParser.class);
            DefaultJws mockJws = mock(DefaultJws.class);
            DefaultClaims mockClaims = mock(DefaultClaims.class);
            jwtsMockedStatic.when(Jwts::parser).thenReturn(parserMock);

            when(parserMock.setSigningKey(anyString())).thenReturn(parserMock);
            when(parserMock.parseClaimsJws(anyString())).thenReturn(mockJws);
            when(mockJws.getBody()).thenReturn(mockClaims);
            when(mockClaims.getSubject()).thenReturn(USER_NAME);

            Authentication expected = new UsernamePasswordAuthenticationToken(userPrincipal, "");
            Authentication result = jwtProvider.getAuthentication(INVALID_TOKEN);

            assertEquals(expected.getCredentials(), result.getCredentials());
            assertEquals(expected.getDetails(), result.getDetails());
            assertEquals(expected.getPrincipal(), result.getPrincipal());
            assertEquals(expected.getName(), result.getName());
        }
    }

    @Test
    void getUsername() {
        try (MockedStatic<Jwts> jwtsMockedStatic = Mockito.mockStatic(Jwts.class)) {
            JwtParser parserMock = mock(JwtParser.class);
            DefaultJws mockJws = mock(DefaultJws.class);
            DefaultClaims mockClaims = mock(DefaultClaims.class);
            jwtsMockedStatic.when(Jwts::parser).thenReturn(parserMock);

            when(parserMock.setSigningKey(anyString())).thenReturn(parserMock);
            when(parserMock.parseClaimsJws(anyString())).thenReturn(mockJws);
            when(mockJws.getBody()).thenReturn(mockClaims);
            when(mockClaims.getSubject()).thenReturn(USER_NAME);

            assertEquals(USER_NAME, jwtProvider.getUsername(VALID_TOKEN));
        }
    }

    @Test
    void resolveToken() {
        when(request.getHeader(any())).thenReturn("token");

        assertEquals("token", jwtProvider.resolveToken(request));
    }
}
