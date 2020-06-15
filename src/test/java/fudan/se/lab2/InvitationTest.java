package fudan.se.lab2;

import fudan.se.lab2.controller.AuthController;
import fudan.se.lab2.controller.InvitationController;
import fudan.se.lab2.controller.request.InvitationRequest;
import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.request.ResponseInvitationRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.service.InvitationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Rollback(value = true)
public class InvitationTest {
    @Autowired
    AuthController authController;
    @Autowired
    InvitationController invitationController;


    static private String jwt_token;
    private String username1 = "tsttt";
    private String password1 = "aaaaa11111";
    private String username2 = "Raccoon";
    private String password2 = "1234asdf";
    String conferenceShortName = "test1587618879125";

    void login(String username, String password) {
        jwt_token =
                ((ResponseObject<String>)
                        Objects.requireNonNull
                                (authController.login(new LoginRequest(username, password))
                                        .getBody())).getContent();
    }

    void sendInvitation() {
        login(username1, password1);
        InvitationRequest request = new InvitationRequest();
        request.setInviterName(username1);
        request.setInviteeName(username2);
        request.setConferenceName(conferenceShortName);
        assertEquals(200,invitationController.sendInvitation(request,jwt_token).getStatusCode().value());
    }

    void respondInvitation(){
        List<String> topics = new LinkedList<>();
        topics.add("a");
        login(username2,password2);
        ResponseInvitationRequest request = new ResponseInvitationRequest();
        request.setConferenceName(conferenceShortName);
        request.setStatus("receive");
        request.setTopics(topics);
        assertEquals(200,invitationController.responseInvitation(jwt_token,request).getStatusCode().value());
    }

    @Test
    void invitationTest() {
        sendInvitation();
        respondInvitation();
    }
}
