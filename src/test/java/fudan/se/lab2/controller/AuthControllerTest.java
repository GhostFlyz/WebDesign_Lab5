package fudan.se.lab2.controller;

import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = true)
class AuthControllerTest {
    @Autowired
    private AuthController authController;
    static private String jwt_token;
    private String username = "tsttt";
    private String password = "aaaaa11111";

    void preLogin() {
        jwt_token =
                ((ResponseObject<String>)
                        Objects.requireNonNull
                                (authController.login(new LoginRequest(username, password))
                                        .getBody())).getContent();
    }

    @Test
    void registerDoGet() {
        assertEquals(200, authController.registerDoGet().getStatusCode().value());
    }

    @Test
    void loginDoGet() {
        assertEquals(200, authController.loginDoGet().getStatusCode().value());
    }

    @Test
    void checkUsername() {
        assertEquals(200, authController.checkUsername("aaaaa").getStatusCode().value());
    }

    @Test
    void register() {
        long rand = System.currentTimeMillis() % 100000000;
        String username = "usr" + rand;
        String password = "aaaaa11111";
        String fullname = "a";
        Set<String> authorities = new HashSet<>();
        authorities.add("Contributor");
        String email = "a@b.com";
        String region = "a";
        String institution = "a";
        RegisterRequest registerRequest = new RegisterRequest(username, password, fullname, email, region, institution);
        assertEquals(200, authController.register(registerRequest).getStatusCode().value());
    }

    @Test
    void login() {
        LoginRequest request = new LoginRequest("tsttt", "aaaaa11111");
        assertEquals(200, authController.login(request).getStatusCode().value());
    }

    @Test
    void findAuthorityOfUser() {
        preLogin();
        assertEquals(200, authController.findAuthorityOfUser(jwt_token).getStatusCode().value());
    }

    @Test
    void findUsersByUsernameContaining() {
        preLogin();
        assertEquals(200, authController.findUsersByUsernameContaining("a", jwt_token).getStatusCode().value());
    }

    @Test
    void getCurrentUser() {
        preLogin();
        assertEquals(200, authController.getCurrentUser(jwt_token).getStatusCode().value());
    }

    @Test
    void welcome() {
        assertEquals(200, authController.welcome().getStatusCode().value());
    }

    @Test
    void findUserByFullName() {
        String fullName = "test_fullName";
        login();
        assertEquals(200, authController.findUserByFullName(fullName, jwt_token).getStatusCode().value());
    }
}