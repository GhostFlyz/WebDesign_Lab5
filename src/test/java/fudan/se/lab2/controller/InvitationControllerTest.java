package fudan.se.lab2.controller;

import fudan.se.lab2.controller.request.InvitationRequest;
import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.request.ResponseInvitationRequest;
import fudan.se.lab2.controller.response.ResponseObject;
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
class InvitationControllerTest {
    @Autowired
    private InvitationController invitationController;
    static private String jwt_token;
    private String username = "tsttt";
    private String password = "aaaaa11111";
    @Autowired
    private AuthController authController;

    void login() {
        jwt_token =
                ((ResponseObject<String>)
                        Objects.requireNonNull
                                (authController.login(new LoginRequest(username, password))
                                        .getBody())).getContent();
    }

    @Test
    void findInvitationsByInviter() {
        login();
        assertEquals(200, invitationController.findInvitationsByInviter(jwt_token).getStatusCode().value());
    }

    @Test
    void findInvitationsByInvitee() {
        login();
        assertEquals(200, invitationController.findInvitationsByInvitee(jwt_token).getStatusCode().value());
    }

    @Test
    void sendInvitation() {
        InvitationRequest request = new InvitationRequest("tsttt","Raccoon","test");
        login();
        assertEquals(200, invitationController.sendInvitation(request,jwt_token).getStatusCode().value());
    }

    @Test
    void responseInvitation() {
        this.username = "Raccoon";
        this.password = "asdf1234";
        login();
        ResponseInvitationRequest request = new ResponseInvitationRequest();
        assertEquals(200, invitationController.responseInvitation(jwt_token,request).getStatusCode().value());
    }
}