package fudan.se.lab2.controller;

import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.request.PostRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = true)
class PostControllerTest {
    static private String jwt_token;
    @Autowired
    AuthController authController;
    @Autowired
    PostController postController;
    private final String username = "tsttt";
    private final String password = "aaaaa11111";

    void login() {
        jwt_token =
                Objects.requireNonNull
                        (authController.login(new LoginRequest(username, password))
                                .getBody()).getContent();
    }

    @Test
    void createFirstDiscussPost() {
        login();
        String title = "test";
        String content = "test";
        Long paperId = 391L;
        PostRequest request = new PostRequest();
        request.setType("AU");
        request.setTitle(title);
        request.setTorR("T");
        request.setContent(content);
        request.setPaperId(paperId);
        assertEquals(200, postController.createFirstDiscussPost(request, jwt_token).getStatusCode().value());
    }

    @Test
    void createSecondDiscussPost() {
        login();
        String title = "test";
        String content = "test";
        Long paperId = 391L;
        PostRequest request = new PostRequest();
        request.setType("AU");
        request.setTitle(title);
        request.setTorR("T");
        request.setContent(content);
        request.setPaperId(paperId);
        assertEquals(200, postController.createSecondDiscussPost(request, jwt_token).getStatusCode().value());
    }

    @Test
    void getAllFirstDiscussPost() {
        assertEquals(200, postController.getAllFirstDiscussPost(391L).getStatusCode().value());
    }

    @Test
    void getAllSecondDiscussPost() {
        assertEquals(200, postController.getAllSecondDiscussPost(391L).getStatusCode().value());
    }
}