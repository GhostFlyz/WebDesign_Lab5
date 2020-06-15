package fudan.se.lab2;

import fudan.se.lab2.controller.AuthController;
import fudan.se.lab2.controller.ConferenceController;
import fudan.se.lab2.controller.PaperController;
import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Rollback(value = true)
public class PaperAuditTest {
    @Autowired
    private ConferenceController conferenceController;
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

    void divideMethod() {
        String confShort = "test1587618879125";
        login();
        assertEquals(200, conferenceController.audit_DivideAverage(confShort, jwt_token).getStatusCode().value());
        assertEquals(200, conferenceController.audit_DivideByTopics(confShort, jwt_token).getStatusCode().value());
    }

    void submitPaperAuditInfo() {
        login();
        String grade = "-2";
        String comment = "test_comment";
        String confidence = "high";
        String title = "test_paper";
        assertEquals(200, paperController.submitPaperAuditInfo(jwt_token, grade, comment, confidence, title).getStatusCode().value());
    }

    void modifyPaperAuditInfo() {
        login();
        String grade = "-1";
        String comment = "test_comment";
        String confidence = "high";
        String title = "test_paper";
        Long id = (long) 1;
        assertEquals(200, paperController.firstModifyPaperAuditInfo(jwt_token, grade, comment, confidence, title, id).getStatusCode().value());
        assertEquals(200, paperController.secondModifyPaperAuditInfo(jwt_token, grade, comment, confidence, title, id).getStatusCode().value());
    }

    void addRebuttal() {
        String title = "test1587633322070";
        String rebuttal = "test_rebuttal";
        login();
        assertEquals(200, paperController.addRebuttalForPaper(jwt_token, rebuttal, title).getStatusCode().value());
    }

    void publishResult() {
        login();
        String title = "test1587633322070";
        assertEquals(200, paperController.publishResult(title, jwt_token).getStatusCode().value());
    }

    @Test
    void paperAuditTest(){
        divideMethod();
        submitPaperAuditInfo();
        modifyPaperAuditInfo();
        addRebuttal();
        publishResult();
    }
}
