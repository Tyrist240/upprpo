package ru.nsu.store.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.nsu.store.entity.Role;
import ru.nsu.store.entity.User;
import ru.nsu.store.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    void loadUserByUsernameThrowsUsernameNotFoundException() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("email"));
    }

    @Test
    void loadUserByUsernameThrowsLockedException() {
        User user = new User();
        user.setActivationCode("test");
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        assertThrows(LockedException.class, () -> userDetailsService.loadUserByUsername("email"));
    }

    @Test
    void loadUserByUsername() {
        User user = new User();
        user.setRoles(Set.of(Role.ADMIN));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        assertEquals(UserPrincipal.create(user), userDetailsService.loadUserByUsername("email"));
    }
}