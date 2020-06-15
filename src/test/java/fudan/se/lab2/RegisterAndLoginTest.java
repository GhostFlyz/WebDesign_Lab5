package fudan.se.lab2;

import fudan.se.lab2.controller.AuthController;
import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.request.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Rollback(value = true)
public class RegisterAndLoginTest {
    @Autowired
    AuthController authController;

    String username;

    void register(){
        long rand = System.currentTimeMillis() % 100000000;
        username = "usr" + rand;
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

    void login(){
        LoginRequest request = new LoginRequest(username, "aaaaa11111");
        assertEquals(200, authController.login(request).getStatusCode().value());
    }

    @Test
    void registerAndLogin(){
        register();
        login();
    }
}
