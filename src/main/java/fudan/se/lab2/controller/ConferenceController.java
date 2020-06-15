package fudan.se.lab2.controller;

import fudan.se.lab2.controller.request.ConferenceRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import fudan.se.lab2.service.ConferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ConferenceController {
    private final ConferenceService conferenceService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public ConferenceController(ConferenceService conferenceService, JwtTokenUtil jwtTokenUtil) {
        this.conferenceService = conferenceService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @GetMapping("/applyConference")
    public ResponseEntity<String> applyConferenceDoGet() {
        return ResponseEntity.ok().body("please use post method");
    }

    /**
     * mapping to apply for a conference
     */
    @PostMapping("/applyConference")
    public ResponseEntity<String> applyConference(@RequestBody ConferenceRequest request, @RequestHeader String jwt_token) {

        String username = jwtTokenUtil.getUsernameFromToken(jwt_token);
        request.setUsername(username);
        try {
            return ResponseEntity.ok().
                    //header("Access-Control-Allow-Origin", "*").
                            body(conferenceService.processApply(request));
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/auditConference")
    public ResponseEntity<String> auditConference(@RequestParam String conferenceShortName,
                                             @RequestParam String status, @RequestHeader String jwt_token) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt_token);

        try {
            return ResponseEntity.ok(conferenceService.auditConference(conferenceShortName, username, status));
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/findConferenceByChair")
    public ResponseEntity<ResponseObject<List<Conference>>> findConferenceByChair(@RequestHeader String jwt_token) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt_token);
        try {
            return ResponseEntity.ok(conferenceService.findConferenceByChairUser(username));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    @GetMapping("/findConferenceByParticipation")
    public ResponseEntity<ResponseObject<List<Conference>>> findConferenceByParticipation(@RequestHeader String jwt_token) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt_token);
        try {
            return ResponseEntity.ok(conferenceService.findConferenceByParticipation(username));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    @GetMapping("/findCharacterByConferenceAndUser")
    public ResponseEntity<ResponseObject<List<String>>> findCharacterByConferenceAndUser(@RequestParam String conferenceShortName,
                                                              @RequestHeader String jwt_token) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt_token);
        try {
            return ResponseEntity.ok(conferenceService.findCharacterByConferenceAndUser(username, conferenceShortName));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    @GetMapping("/setConferenceCanBeSubmitted")
    public ResponseEntity<String> setConferenceCanBeSubmitted(@RequestParam String conferenceShortName,
                                                         @RequestHeader String jwt_token) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt_token);
        try {
            return ResponseEntity.ok(conferenceService.setConferenceCanBeSubmitted(username, conferenceShortName));
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @RequestMapping("/findAllConferences")
    public ResponseEntity<ResponseObject<List<Conference>>> findAllConferences() {
        try {
            return ResponseEntity.ok(conferenceService.findAllConferences());
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    @RequestMapping("/findAllConferencesByPage")
    public ResponseEntity<ResponseObject<Page<Conference>>> findAllConferencesByPage(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                      @RequestParam(value = "size", defaultValue = "5") Integer size) {
        return ResponseEntity.ok(conferenceService.findAllConferenceByPage(page, size));
    }

    @RequestMapping("/findAllConferencesByUser")
    public ResponseEntity<ResponseObject<List<Conference>>> findAllConferencesByUser(@RequestHeader String jwt_token) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt_token);
        try {
            return ResponseEntity.ok(conferenceService.findAllConferenceByUser(username));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    @RequestMapping("/findAllConferencesNotAudited")
    public ResponseEntity<ResponseObject<List<Conference>>> findAllConferencesNotAudited() {
        try {
            return ResponseEntity.ok(conferenceService.findAllConferenceNotAudited());
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    @RequestMapping("/pcMemberChooseTopic")
    public ResponseEntity<ResponseObject<Conference>> pcMemberChooseTopic(@RequestParam String conferenceShortName,
                                                 @RequestHeader String jwt_token, @RequestParam String topicName) {
        String username = getUsernameFromToken(jwt_token);
        try {
            return ResponseEntity.ok(conferenceService.pcMemberChooseTopic(conferenceShortName, username, topicName));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    @GetMapping("/audit_DivideByTopics")
    public ResponseEntity<ResponseObject<Object>> audit_DivideByTopics(@RequestParam String conferenceShortName, @RequestHeader String jwt_token) {
        String username = getUsernameFromToken(jwt_token);
        try {
            return ResponseEntity.ok(conferenceService.audit_DivideByTopics(username, conferenceShortName));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    @GetMapping("/audit_DivideAverage")
    public ResponseEntity<ResponseObject<Object>> audit_DivideAverage(@RequestParam String conferenceShortName, @RequestHeader String jwt_token) {
        String username = getUsernameFromToken(jwt_token);
        try {
            return ResponseEntity.ok(conferenceService.audit_DivideAverage(username, conferenceShortName));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }


    private String getUsernameFromToken(String jwt_token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = authentication != null && authentication.isAuthenticated()
                ? (User) authentication.getPrincipal() : null;
        return user != null ? user.getUsername() : jwtTokenUtil.getUsernameFromToken(jwt_token);
    }
}
