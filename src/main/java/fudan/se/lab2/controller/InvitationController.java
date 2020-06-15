package fudan.se.lab2.controller;

import fudan.se.lab2.controller.request.InvitationRequest;
import fudan.se.lab2.controller.request.ResponseInvitationRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.domain.Invitation;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import fudan.se.lab2.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InvitationController {
    private final JwtTokenUtil jwtTokenUtil;
    private final InvitationService invitationService;

    @Autowired
    public InvitationController(JwtTokenUtil jwtTokenUtil, InvitationService invitationService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.invitationService = invitationService;
    }

    @GetMapping("/findInvitationsByInviter")
    public ResponseEntity<ResponseObject<List<Invitation>>> findInvitationsByInviter(@RequestHeader String jwt_token) {
        String inviter = jwtTokenUtil.getUsernameFromToken(jwt_token);
        return ResponseEntity.ok(invitationService.findInvitationsByInviter(inviter));
    }

    @GetMapping("/findInvitationsByInvitee")
    public ResponseEntity<ResponseObject<List<Invitation>>> findInvitationsByInvitee(@RequestHeader String jwt_token) {
        String invitee = jwtTokenUtil.getUsernameFromToken(jwt_token);
        return ResponseEntity.ok(invitationService.findInvitationsByInvitee(invitee));
    }

    @PostMapping("/sendInvitation")
    public ResponseEntity<String> sendInvitation(@RequestBody InvitationRequest request, @RequestHeader String jwt_token) {
        String inviter = jwtTokenUtil.getUsernameFromToken(jwt_token);
        request.setInviterName(inviter);
        return ResponseEntity.ok().body(invitationService.sendInvitation(request));
    }

    @PostMapping("/respondInvitation")
    public ResponseEntity<String> responseInvitation(@RequestHeader String jwt_token, @RequestBody ResponseInvitationRequest responseInvitationRequest) {
        String invitee = jwtTokenUtil.getUsernameFromToken(jwt_token);
        List<String> topicList = responseInvitationRequest.getTopics();
        String conferenceName = responseInvitationRequest.getConferenceName();
        String status = responseInvitationRequest.getStatus();
        return ResponseEntity.ok(invitationService.respondInvitation(conferenceName, invitee, status, topicList));
    }
}
