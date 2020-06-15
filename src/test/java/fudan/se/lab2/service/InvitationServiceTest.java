package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.ConferenceRequest;
import fudan.se.lab2.controller.request.InvitationRequest;
import fudan.se.lab2.controller.request.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
@Rollback(value = true)
class InvitationServiceTest {
    @Autowired
    private InvitationService invitationService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ConferenceService conferenceService;

    private Random random = new Random();
    private String conferencename = "test" + random.nextInt(10000);
    private Set<String> topics = new HashSet<>();

    @Test
    void sendInvitation() throws Exception {
        String username1 = "usr111";
        String username2 = "usr222";
        String password = "aaaaa11111";
        String fullname = "a";
        String email = "a@b.com";
        String region = "a";
        String institution = "a";
        RegisterRequest registerRequest1 = new RegisterRequest(username1, password, fullname, email, region, institution);
        RegisterRequest registerRequest2 = new RegisterRequest(username2, password, fullname, email, region, institution);
        RegisterRequest registerRequest3 = new RegisterRequest("usr221", password, fullname, email, region, institution);
        authService.register(registerRequest1);
        authService.register(registerRequest2);
        authService.register(registerRequest3);
        InvitationRequest invitationRequest1 = new InvitationRequest("usr111", "usr222", conferencename);
        assertEquals("the conference does not exist", invitationService.sendInvitation(invitationRequest1));
        topics.add("a");
        ConferenceRequest conferenceRequest = new ConferenceRequest(conferencename, "test1_test1", "May 1-1,2020",
                "pos", "2020-01-01", "2020-02-02", topics);
        conferenceService.processApply(conferenceRequest);
        assertEquals("success", invitationService.sendInvitation(invitationRequest1));
        assertEquals("the user has already been invited", invitationService.sendInvitation(invitationRequest1));
        InvitationRequest invitationRequest2 = new InvitationRequest("usr111", "usr223", conferencename);
        assertEquals("the user does not exist", invitationService.sendInvitation(invitationRequest2));
    }

    @Test
    void respondInvitation() throws Exception {
        List<String> list = new LinkedList<>();
        String status1 = "receive";
        String status2 = "reject";
        topics.add("a");
        ConferenceRequest conferenceRequest = new ConferenceRequest(conferencename, "test1_test1", "May 1-1,2020", "pos", "2020-01-01", "2020-02-02", topics);
        conferenceService.processApply(conferenceRequest);
        InvitationRequest invitationRequest1 = new InvitationRequest("usr111", "usr221", conferencename);
        InvitationRequest invitationRequest2 = new InvitationRequest("usr111", "usr222", conferencename);
        invitationService.sendInvitation(invitationRequest1);
        invitationService.sendInvitation(invitationRequest2);
        assertEquals("success", invitationService.respondInvitation(conferencename, "usr221", status1, list));
        assertEquals("success", invitationService.respondInvitation(conferencename, "usr222", status2, list));
        assertEquals("the status should be either receive or reject", invitationService.respondInvitation(conferencename, "usr222", "status", list));
        assertEquals("the conference does not exists", invitationService.respondInvitation(conferencename + "'", "usr222", status1, list));
        assertEquals("the invitation does not exists", invitationService.respondInvitation(conferencename, "usr223", status1, list));
    }

    @Test
    void findInvitationsByInviter() {
        String inviterName = "usr111";
        assertEquals("success", invitationService.findInvitationsByInviter(inviterName).getMessage());
    }

    @Test
    void findInvitationsByInvitee() {
        String inviteeName = "usr111";
        assertEquals("success", invitationService.findInvitationsByInvitee(inviteeName).getMessage());
    }
}