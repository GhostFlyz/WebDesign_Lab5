package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.PostRequest;
import fudan.se.lab2.domain.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = true)
class PostServiceTest {

    @Autowired
    PostService postService;

    @Test
    void createPost() {
        String title = "test";
        String content = "test";
        Long paperId = 391L;
        PostRequest request = new PostRequest();
        request.setType("AU");
        request.setTitle(title);
        request.setTorR("T");
        request.setContent(content);
        request.setPaperId(paperId);
        String username = "tsttt";
        Post newTheme = postService.createPost(request, username).getContent();
        assertNotNull(newTheme);
        request.setTorR("R");
        request.setThemeId(newTheme.getId() + 1000);
        assertEquals("theme post does not exist", postService.createPost(request, username).getMessage());
        request.setTorR("T");
        request.setPaperId(0L);
        assertEquals("paper does not exist", postService.createPost(request, username).getMessage());
        request.setTorR("X");
        assertEquals("the post should be either theme(T) or reply(R), now X",
                postService.createPost(request, username).getMessage());
        request.setTorR("R");
        request.setThemeId(newTheme.getId());
        assertNotNull(postService.createPost(request, username).getContent());
    }

    @Test
    void findPostsByPaperAndType() {
        Long paperId = 391L;
        assertEquals(200, postService.findPostsByPaperAndType(paperId, "AU").getStatusCode());
        assertEquals(400, postService.findPostsByPaperAndType(-1L, "AU").getStatusCode());
    }
}