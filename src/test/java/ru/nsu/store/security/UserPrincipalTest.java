package ru.nsu.store.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nsu.store.entity.Role;
import ru.nsu.store.entity.User;

import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserPrincipalTest {

    @InjectMocks
    private UserPrincipal userPrincipal;

    @Test
    void isAccountNonLocked() {
        assertTrue(userPrincipal.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired() {
        assertTrue(userPrincipal.isCredentialsNonExpired());
    }

    @Test
    void isEnabled() {
        assertTrue(userPrincipal.isEnabled());
    }

    @Test
    void isAccountNonExpired() {
        assertTrue(userPrincipal.isAccountNonExpired());
    }

    @Test
    void getEmail() {
        User user = new User();
        user.setRoles(Set.of(Role.ADMIN));
        user.setEmail("enail");
        UserPrincipal up = UserPrincipal.create(user);

        assertEquals("enail", up.getUsername());
    }

    @Test
    void getName() {
        User user = new User();
        user.setRoles(Set.of(Role.ADMIN));
        user.setEmail("enail");
        UserPrincipal up = UserPrincipal.create(user);

        assertEquals("enail", up.getName());
    }

    @Test
    void create(){
        User user = new User();
        user.setRoles(Set.of(Role.ADMIN));
        user.setEmail("enail");

        assertNotNull(UserPrincipal.create(user, new HashMap<>()));
    }
}