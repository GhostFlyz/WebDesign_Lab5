package fudan.se.lab2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fudan.se.lab2.controller.request.AuthorInfoRequest;
import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = true)
class PaperControllerTest {
    @Autowired
    private PaperController paperController;
    @Autowired
    private AuthController authController;
    static private String jwt_token;
    private String username = "tsttt";
    private String password = "aaaaa11111";

    void login() {
        jwt_token =
                ((ResponseObject<String>)
                        Objects.requireNonNull
                                (authController.login(new LoginRequest(username, password))
                                        .getBody())).getContent();
    }

    @Test
    void submitPaper() throws Exception {
        login();
        long randByTime = System.currentTimeMillis();
        String title = "test" + randByTime;
        String abs = "aaaa";
        String conferenceShortName = "m8";
        MultipartFile file = new MockMultipartFile("test3.pdf", new byte[0]);
        List<String> users = new LinkedList<>();
        List<String> topics = new LinkedList<>();
        users.add("aaaaa");
        topics.add("ABC");
        AuthorInfoRequest request1 = new AuthorInfoRequest("a", "a", "a", "a@a.com");
        AuthorInfoRequest request2 = new AuthorInfoRequest("b", "b", "b", "b@b.com");
        List<String> requests = new LinkedList<>();
        System.out.println(new ObjectMapper().writeValueAsString(request1));
        requests.add(new ObjectMapper().writeValueAsString(request1));
        requests.add(new ObjectMapper().writeValueAsString(request2));
        ResponseEntity entity = paperController.submitPaper(file,
                title, abs, conferenceShortName, topics, requests, jwt_token);
        System.out.println(new ObjectMapper().writeValueAsString(entity.getBody()));
        assertEquals(200, entity.getStatusCode().value());
    }

    @Test
    void modifyPaper() throws Exception {
        login();
        String title = "test1587633322070";
        long id = 394;
        String abs = "bbbb";
        String conferenceShortName = "test1587618879125";
        MultipartFile file = new MockMultipartFile("test3.pdf", new byte[0]);
        List<String> users = new LinkedList<>();
        List<String> topics = new LinkedList<>();
        users.add("Raccoon");
        topics.add("a");
        AuthorInfoRequest request1 = new AuthorInfoRequest("a", "a", "a", "a@a.com");
        AuthorInfoRequest request2 = new AuthorInfoRequest("b", "b", "b", "b@b.com");
        List<String> requests = new LinkedList<>();
        requests.add(new ObjectMapper().writeValueAsString(request1));
        requests.add(new ObjectMapper().writeValueAsString(request2));
        assertEquals(200, paperController.modifyPaper(file,
                title, abs, conferenceShortName, topics, requests, id, jwt_token).getStatusCode().value());
    }

    @Test
    void getPapersByUser() {
        login();
        assertEquals(200, paperController.getPapersByUser(jwt_token).getStatusCode().value());
    }

    @Test
    void getPapersByInspector() {
        login();
        assertEquals(200, paperController.getPapersByInspector(jwt_token).getStatusCode().value());
    }

    @Test
    void submitPaperAuditInfo() {
        login();
        String grade = "-1";
        String comment = "test_comment";
        String confidence = "high";
        String title = "test_paper";
        assertEquals(200, paperController.submitPaperAuditInfo(jwt_token, grade, comment, confidence, title).getStatusCode().value());
    }

    @Test
    void firstModifyPaperAuditInfo() {
        login();
        String grade = "-1";
        String comment = "test_comment";
        String confidence = "high";
        String title = "test_paper";
        Long id = (long) -1;
        assertEquals(200, paperController.firstModifyPaperAuditInfo(jwt_token, grade, comment, confidence, title, id).getStatusCode().value());
    }

    @Test
    void secondModifyPaperAuditInfo() {
        login();
        String grade = "-1";
        String comment = "test_comment";
        String confidence = "high";
        String title = "test_paper";
        Long id = (long) -1;
        assertEquals(200, paperController.secondModifyPaperAuditInfo(jwt_token, grade, comment, confidence, title, id).getStatusCode().value());
    }

    @Test
    void publishResult() {
        login();
        String title = "test1587633322070";
        assertEquals(200, paperController.publishResult(title, jwt_token).getStatusCode().value());
    }

    @Test
    void getAuditResultsOfMyPaper() {
        login();
        assertEquals(200, paperController.getAuditResultsOfMyPaper(jwt_token).getStatusCode().value());
    }

    @Test
    void getAuditResults() {
        String conferenceShortName = "test1587618879125";
        assertEquals(200, paperController.getAuditResults(conferenceShortName).getStatusCode().value());
    }

    @Test
    void addRebuttalForPaper() {
        String title = "test1587633322070";
        String rebuttal = "test_rebuttal";
        login();
        assertEquals(200, paperController.addRebuttalForPaper(jwt_token, rebuttal, title).getStatusCode().value());
    }
}