package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.repository.AuthorityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = true)
class AuthServiceTest {
    @Autowired
    AuthService authService;

    @Test
    void checkUsername() {
        long rand = System.currentTimeMillis();
        assertEquals("valid", authService.checkUsername("xxx"+rand));
        assertEquals("username exists", authService.checkUsername("aaaaa"));
    }

    @Test
    void register() {
        long rand = System.currentTimeMillis() % 100000000;
        String username = "usr" + rand;
        String invalidUsername = "a";
        String password = "aaaaa11111";
        String invalidPwd = "a";
        String fullname = "a";
        Set<String> authorities = new HashSet<>();
        authorities.add("Contributor");
        String email = "a@b.com";
        String invalidEmail = "xxx";
        String region = "a";
        String institution = "a";
        RegisterRequest registerRequest = new RegisterRequest(username, password, fullname, email, region, institution);
        assertEquals(username, authService.register(registerRequest).getUsername());
        assertEquals("username exists!", authService.register(registerRequest).getUsername());
        registerRequest.setUsername(invalidUsername);
        assertEquals("the length of username should between 5 and 32",
                authService.register(registerRequest).getUsername());
        registerRequest.setUsername("usr2" + rand);
        registerRequest.setEmail(invalidEmail);
        assertEquals("invalid email", authService.register(registerRequest).getUsername());
        registerRequest.setEmail(email);
        registerRequest.setPassword(invalidPwd);
        assertEquals("password should have 2 of number, letter, and special symbol, and 6 <= password length <= 32",
                authService.register(registerRequest).getUsername());
    }

    @Test
    void login() {
        String username = "tsttt";
        String notExistUsr = "notexist";
        String password = "aaaaa11111";
        String wrongPwd = "a";
        assertEquals("success", authService.login(username, password));
        assertEquals("wrong password", authService.login(username, wrongPwd));
        assertEquals("user does not exist", authService.login(notExistUsr, password));
    }

    @Test
    void findAuthOfUser() throws Exception{
        String username = "tsttt";
        assertEquals("Admin", authService.findAuthoritiesOfUser(username).getContent().get(0));
    }

    @Test
    void findUser() throws Exception{
        String username = "tsttt";
        assertEquals("tsttt", authService.findUserByUsername(username).getContent().getUsername());
    }
}