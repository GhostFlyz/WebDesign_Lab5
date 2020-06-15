package fudan.se.lab2;

import fudan.se.lab2.controller.AuthController;
import fudan.se.lab2.controller.PostController;
import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.request.PostRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.domain.Post;
import fudan.se.lab2.domain.ThemePost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = true)
public class PostTest {
    static private String jwt_token;
    @Autowired
    AuthController authController;
    @Autowired
    PostController postController;
    private final String username = "tsttt";
    private final String password = "aaaaa11111";

    String title = "test_post_20200525";
    String content = "test_post_20200525";
    private Long paperId = 391L;

    void login() {
        jwt_token =
                Objects.requireNonNull
                        (authController.login(new LoginRequest(username, password))
                                .getBody()).getContent();
    }

    void createFirstDiscussPost() {
        login();
        PostRequest request = new PostRequest();
        request.setType("AU");
        request.setTitle(title);
        request.setTorR("T");
        request.setContent(content);
        request.setPaperId(paperId);
        assertEquals(200, postController.createFirstDiscussPost(request, jwt_token).getStatusCode().value());
    }

    void createSecondDiscussPost() {
        login();
        PostRequest request = new PostRequest();
        request.setType("AU");
        request.setTitle(title);
        request.setTorR("T");
        request.setContent(content);
        request.setPaperId(paperId);
        assertEquals(200, postController.createSecondDiscussPost(request, jwt_token).getStatusCode().value());
    }

    void getAllFirstDiscussPost() {
        ResponseEntity<ResponseObject<List<ThemePost>>> responseEntity = postController.getAllFirstDiscussPost(paperId);
        assertEquals(200, responseEntity.getStatusCode().value());
        boolean have = false;
        assertNotNull(responseEntity.getBody());
        for(ThemePost post : responseEntity.getBody().getContent()){
            if(title.equals(post.getTitle())){
                have = true;
                break;
            }
        }
        assertTrue(have);
    }

    void getAllSecondDiscussPost() {
        ResponseEntity<ResponseObject<List<ThemePost>>> responseEntity = postController.getAllSecondDiscussPost(paperId);
        assertEquals(200, responseEntity.getStatusCode().value());
        boolean have = false;
        assertNotNull(responseEntity.getBody());
        for(ThemePost post : responseEntity.getBody().getContent()){
            if(title.equals(post.getTitle())){
                have = true;
                break;
            }
        }
        assertTrue(have);
    }

    @Test
    void postTest(){
        createFirstDiscussPost();
        createSecondDiscussPost();
        getAllFirstDiscussPost();
        getAllSecondDiscussPost();
    }
}
