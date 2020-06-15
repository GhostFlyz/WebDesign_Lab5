package fudan.se.lab2.controller;

import fudan.se.lab2.controller.request.ConferenceRequest;
import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = true)
class ConferenceControllerTest {
    @Autowired
    private ConferenceController conferenceController;
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
    void applyConferenceDoGet() {
        assertEquals("please use post method", conferenceController.applyConferenceDoGet().getBody());
    }

    @Test
    void applyConference() {
        login();
        long randByTime = System.currentTimeMillis();
        Set<String> topics = new HashSet<>();
        topics.add("a");
        ConferenceRequest request = new ConferenceRequest("test" + randByTime
                , "test1_test1", "May 1-1,2020",
                "pos", "2020-01-01", "2020-02-02", topics);
        assertEquals(200, conferenceController.applyConference(request, jwt_token).getStatusCode().value());
    }

    @Test
    void auditConference() {
        login();
        assertEquals(200,
                conferenceController.auditConference("st2", "pass", jwt_token).getStatusCode().value());
    }

    @Test
    void findConferenceByChair() {
        login();
        List<Conference> list = ((ResponseObject<List<Conference>>)
                Objects.requireNonNull(conferenceController.findConferenceByChair(jwt_token).getBody())).getContent();
        for (Conference conf : list)
            assertEquals(username, conf.getChair().getUsername());
    }

    @Test
    void findConferenceByParticipation() {
        login();
        assertEquals(200, conferenceController.findConferenceByParticipation(jwt_token).getStatusCode().value());
    }

    @Test
    void findCharacterByConferenceAndUser() {
        login();
        assertEquals(200,
                conferenceController.findCharacterByConferenceAndUser("st2", jwt_token).getStatusCode().value());
    }

    @Test
    void setConferenceCanBeSubmitted() {
        login();
        assertEquals(200,
                conferenceController.setConferenceCanBeSubmitted("st2", jwt_token).getStatusCode().value());
    }

    @Test
    void findAllConferences() {
        assertEquals(200, conferenceController.findAllConferences().getStatusCode().value());
    }

    @Test
    void findAllConferencesByPage() {
        assertEquals(200, conferenceController.findAllConferencesByPage(1, 10).getStatusCode().value());
    }

    @Test
    void findAllConferencesByUser() {
        login();
        assertEquals(200, conferenceController.findAllConferencesByUser(jwt_token).getStatusCode().value());
    }

    @Test
    void findAllNotAudited() {
        assertEquals(200, conferenceController.findAllConferencesNotAudited().getStatusCode().value());
    }

    @Test
    void pcMemberChooseTopic() {
        String confShort = "test1587618879125";
        this.username = "Raccoon";
        this.password = "asdf1234";
        login();
        assertEquals(200,
                conferenceController.pcMemberChooseTopic(confShort, jwt_token, "a").getStatusCode().value());
        username = "tsttt";
        password = "aaaaa11111";
    }

    @Test
    void audit_DivideByTopics() {
        String confShort = "test1587618879125";
        login();
        assertEquals(200, conferenceController.audit_DivideByTopics(confShort, jwt_token).getStatusCode().value());
    }

    @Test
    void audit_DivideAverage() {
        String confShort = "test1587618879125";
        login();
        assertEquals(200, conferenceController.audit_DivideAverage(confShort, jwt_token).getStatusCode().value());
    }
}