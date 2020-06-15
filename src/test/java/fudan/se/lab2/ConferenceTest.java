package fudan.se.lab2;

import fudan.se.lab2.controller.AuthController;
import fudan.se.lab2.controller.ConferenceController;
import fudan.se.lab2.controller.request.ConferenceRequest;
import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.domain.Conference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = true)
public class ConferenceTest {
    @Autowired
    ConferenceController conferenceController;
    @Autowired
    AuthController authController;

    String shortName;
    static private String jwt_token;
    private String username = "tsttt";
    private String password = "aaaaa11111";

    void login(){
        jwt_token =
                ((ResponseObject<String>)
                        Objects.requireNonNull
                                (authController.login(new LoginRequest(username, password))
                                        .getBody())).getContent();
    }

    void applyConference() {
        login();
        long randByTime = System.currentTimeMillis();
        Set<String> topics = new HashSet<>();
        shortName = "test" + randByTime;
        topics.add("a");
        ConferenceRequest request = new ConferenceRequest(shortName
                , "test1_test1", "May 1-1,2020",
                "pos", "2020-01-01", "2020-02-02", topics);
        assertEquals(200, conferenceController.applyConference(request, jwt_token).getStatusCode().value());
    }

    void auditConference() {
        login();
        assertEquals(200,
                conferenceController.auditConference(shortName, "pass", jwt_token).getStatusCode().value());
    }

    void getAllConference(){
        ResponseEntity<ResponseObject<List<Conference>>> responseEntity = conferenceController.findAllConferences();
        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(responseEntity.getBody());
        boolean have = false;
        for(Conference conference : responseEntity.getBody().getContent()){
            if(shortName.equals(conference.getShortName())){
                have = true;
                assertEquals("pass", conference.getAuditStatus());
            }
        }
        assertTrue(have);
    }

    @Test
    void applyAndAudit(){
        applyConference();
        auditConference();
        getAllConference();
    }
}
