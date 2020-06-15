package fudan.se.lab2;

import com.fasterxml.jackson.databind.ObjectMapper;
import fudan.se.lab2.controller.AuthController;
import fudan.se.lab2.controller.PaperController;
import fudan.se.lab2.controller.request.AuthorInfoRequest;
import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.request.PaperSubmitRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.domain.Paper;
import fudan.se.lab2.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PaperUploadTest {
    @Autowired
    AuthController authController;
    @Autowired
    PaperController paperController;

    static private String jwt_token;
    private String username = "Raccoon";
    private String password = "1234asdf";
    private String title;

    void login(){
        jwt_token =
                ((ResponseObject<String>)
                        Objects.requireNonNull
                                (authController.login(new LoginRequest(username, password))
                                        .getBody())).getContent();
    }


    void submitPaper() throws Exception {
        login();
        String abs = "aaaa";
        AuthorInfoRequest request1 = new AuthorInfoRequest("a", "a", "a", "a@a.com");
        AuthorInfoRequest request2 = new AuthorInfoRequest("b", "b","b", "b@b.com");
        List<String> requests = new LinkedList<>();
        requests.add(new ObjectMapper().writeValueAsString(request1));
        requests.add(new ObjectMapper().writeValueAsString(request2));


        long randByTime = System.currentTimeMillis();
        title = "test" + randByTime;
        String abstractContent = "aaaa";
        String conferenceShortName = "test1587618879125";
        MultipartFile file = new MultipartFile() {
            @Override
            public String getName() {
                return "test2.pdf";
            }

            @Override
            public String getOriginalFilename() {
                return "test2.pdf";
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }
        };
        PaperSubmitRequest request = new PaperSubmitRequest();
        request.setFile(file);
        request.setTitle(title);
        request.setAbstractContent(abstractContent);
        request.setConferenceShortName(conferenceShortName);
        List<String> topics = new LinkedList<>();
        topics.add("a");
        request.setTopics(topics);

        ResponseEntity<?> entity = paperController.submitPaper(file,
                title, abs, conferenceShortName, topics, requests, jwt_token);
        System.out.println(new ObjectMapper().writeValueAsString(entity.getBody()));

        assertEquals("success", entity.getBody());
    }

    void getPapersByUser() {
        login();
        ResponseEntity<ResponseObject<List<Paper>>> responseEntity = paperController.getPapersByUser(jwt_token);
        assertEquals(200, responseEntity.getStatusCode().value());
        boolean have = false;
        assertNotNull(responseEntity.getBody());
        for(Paper paper : responseEntity.getBody().getContent()){
            if(title.equals(paper.getTitle())){
                have = true;
                assertEquals(username, paper.getAuthor().getUsername());
            }
        }
        assertTrue(have);
    }

    void modifyPaper() throws Exception{
        login();
        long id = 394;
        String abs = "bbbb";
        String conferenceShortName = "m8";
        MultipartFile file = new MockMultipartFile("test3.pdf", new byte[0]);
        List<String> topics = new LinkedList<>();
        topics.add("a");
        AuthorInfoRequest request1 = new AuthorInfoRequest("a", "a", "a", "a@a.com");
        AuthorInfoRequest request2 = new AuthorInfoRequest("b", "b","b", "b@b.com");
        List<String> requests = new LinkedList<>();
        requests.add(new ObjectMapper().writeValueAsString(request1));
        requests.add(new ObjectMapper().writeValueAsString(request2));
        assertEquals(200, paperController.modifyPaper(file,
                title, abs, conferenceShortName, topics, requests, id, jwt_token).getStatusCode().value());
    }

    @Test
    void uploadAndModifyAndGet() throws Exception{
        submitPaper();
        modifyPaper();
        getPapersByUser();
    }


}
