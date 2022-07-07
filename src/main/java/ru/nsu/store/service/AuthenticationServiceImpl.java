package ru.nsu.store.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.nsu.store.dto.CaptchaResponse;
import ru.nsu.store.entity.AuthProvider;
import ru.nsu.store.entity.Role;
import ru.nsu.store.entity.User;
import ru.nsu.store.exception.ApiRequestException;
import ru.nsu.store.exception.EmailException;
import ru.nsu.store.exception.PasswordException;
import ru.nsu.store.repository.UserRepository;
import ru.nsu.store.security.JwtProvider;
import ru.nsu.store.service.email.MailSender;
import ru.nsu.store.service.interfaces.AuthenticationService;

import java.util.*;

@Service
@Setter
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final String EMAIL_NOT_FOUND_MESSAGE = "Email not found.";

    private final AuthenticationManager authenticationManager;
    private final RestTemplate restTemplate;
    private final JwtProvider jwtProvider;
    private final MailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Value("${hostname}")
    private String hostname;

    @Value("${recaptcha.secret}")
    private String secret;

    @Value("${recaptcha.url}")
    private String captchaUrl;

    @Override
    public Map<String, String> login(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ApiRequestException(EMAIL_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND));

            if(user.isActive()) {
                String userRole = user.getRoles().iterator().next().name();
                String token = jwtProvider.createToken(email, userRole);
                Map<String, String> response = new HashMap<>();
                response.put("email", email);
                response.put("token", token);
                response.put("userRole", userRole);
                return response;
            } else {
                throw new ApiRequestException("User is blocked", HttpStatus.FORBIDDEN);
            }
        } catch (AuthenticationException e) {
            throw new ApiRequestException("Incorrect password or email", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public String registerUser(User user, String captcha, String password2) {
        String url = String.format(captchaUrl, secret, captcha);
        restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponse.class);

        if (user.getPassword() != null && !user.getPassword().equals(password2)) {
            throw new PasswordException("Passwords do not match.");
        }
        Optional<User> userFromDbOptional = userRepository.findByEmail(user.getEmail());

        if (userFromDbOptional.isPresent()) {
            throw new EmailException("Email is already used.");
        }
        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setProvider(AuthProvider.LOCAL);
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        String subject = "Activation code";
        String template = "registration-template";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("firstName", user.getFirstName());
        attributes.put("registrationUrl", "http://" + hostname + "/activate/" + user.getActivationCode());
        mailSender.sendMessageHtml(user.getEmail(), subject, template, attributes);
        return "User successfully registered.";
    }



    @Override
    public User findByPasswordResetCode(String code) {
        return userRepository.findByPasswordResetCode(code)
                .orElseThrow(() -> new ApiRequestException("Password reset code is invalid!", HttpStatus.BAD_REQUEST));
    }

    @Override
    public String sendPasswordResetCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiRequestException(EMAIL_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND));
        user.setPasswordResetCode(UUID.randomUUID().toString());
        userRepository.save(user);

        String subject = "Password reset";
        String template = "password-reset-template";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("firstName", user.getFirstName());
        attributes.put("resetUrl", "http://" + hostname + "/reset/" + user.getPasswordResetCode());
        mailSender.sendMessageHtml(user.getEmail(), subject, template, attributes);
        return "Reset password code is send to your E-mail";
    }

    @Override
    public String passwordReset(String email, String password, String password2) {
        if (password2.isEmpty()) {
            throw new PasswordException("Password confirmation cannot be empty.");
        }
        if (password != null && !password.equals(password2)) {
            throw new PasswordException("Passwords do not match.");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiRequestException(EMAIL_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND));
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordResetCode(null);
        userRepository.save(user);
        return "Password successfully changed!";
    }

    @Override
    public String activateUser(String code) {
        User user = userRepository.findByActivationCode(code)
                .orElseThrow(() -> new ApiRequestException("Activation code not found.", HttpStatus.NOT_FOUND));
        user.setActivationCode(null);
        user.setActive(true);
        userRepository.save(user);
        return "User successfully activated.";
    }
}
