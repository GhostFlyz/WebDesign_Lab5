package fudan.se.lab2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fudan.se.lab2.controller.request.AuditInfoRequest;
import fudan.se.lab2.controller.request.AuthorInfoRequest;
import fudan.se.lab2.controller.request.PaperSubmitRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.domain.AuditInfo;
import fudan.se.lab2.domain.Paper;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import fudan.se.lab2.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;

@RestController
@Transactional(rollbackFor = Exception.class)
public class PaperController {
    private final JwtTokenUtil jwtTokenUtil;
    private final PaperService paperService;
    @Value("${file.baseUrl}")
    private String basePath;

    @Autowired
    public PaperController(JwtTokenUtil jwtTokenUtil, PaperService paperService) {
        this.paperService = paperService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/submitPaper")
    public ResponseEntity<String> submitPaper(@RequestParam MultipartFile file,
                                              @RequestParam String title,
                                              @RequestParam String abstractContent,
                                              @RequestParam String conferenceShortName,
                                              @RequestParam List<String> topics,
                                              @RequestParam List<String> authorInfos,
                                              @RequestHeader String jwt_token) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt_token);
        List<AuthorInfoRequest> authorInfoRequests = processAuthorInfoRequest(authorInfos);
        PaperSubmitRequest request = new PaperSubmitRequest(file, title, (long) -1, abstractContent, conferenceShortName, topics, authorInfoRequests);
        try {
            return ResponseEntity.ok(paperService.submitPaperForConference(request, username, false));
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @PostMapping("/modifyPaper")
    public ResponseEntity<String> modifyPaper(@RequestParam MultipartFile file,
                                              @RequestParam String title,
                                              @RequestParam String abstractContent,
                                              @RequestParam String conferenceShortName,
                                              @RequestParam List<String> topics,
                                              @RequestParam List<String> authorInfos,
                                              @RequestParam Long id,
                                              @RequestHeader String jwt_token) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt_token);
        List<AuthorInfoRequest> authorInfoRequests = processAuthorInfoRequest(authorInfos);
        PaperSubmitRequest request = new PaperSubmitRequest(file, title, id, abstractContent,
                conferenceShortName, topics, authorInfoRequests);
        try {
            return ResponseEntity.ok(paperService.submitPaperForConference(request, username, true));
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @PostMapping("/submitPaperAuditInfo")
    public ResponseEntity<ResponseObject<AuditInfo>> submitPaperAuditInfo(@RequestHeader String jwt_token,
                                                                       @RequestParam(required = false) String grade,
                                                                       @RequestParam String comment,
                                                                       @RequestParam String confidence,
                                                                       @RequestParam String paperTitle) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt_token);
        AuditInfoRequest auditInfoRequest = new AuditInfoRequest((long) -1, grade, confidence, comment, paperTitle);
        try {
            return ResponseEntity.ok(paperService.submitPaperAuditInfo(auditInfoRequest, username, 0));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    @PostMapping("/firstModifyPaperAuditInfo")
    public ResponseEntity<ResponseObject<AuditInfo>> firstModifyPaperAuditInfo(@RequestHeader String jwt_token,
                                                                               @RequestParam(required = false) String grade,
                                                                               @RequestParam String comment,
                                                                               @RequestParam String confidence,
                                                                               @RequestParam String paperTitle,
                                                                               @RequestParam Long id) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt_token);
        AuditInfoRequest auditInfoRequest = new AuditInfoRequest(id, grade, confidence, comment, paperTitle);
        try {
            return ResponseEntity.ok(paperService.submitPaperAuditInfo(auditInfoRequest, username, 1));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    @PostMapping("/secondModifyPaperAuditInfo")
    public ResponseEntity<ResponseObject<AuditInfo>> secondModifyPaperAuditInfo(@RequestHeader String jwt_token,
                                                                             @RequestParam(required = false) String grade,
                                                                             @RequestParam String comment,
                                                                             @RequestParam String confidence,
                                                                             @RequestParam String paperTitle,
                                                                             @RequestParam Long id) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt_token);
        AuditInfoRequest auditInfoRequest = new AuditInfoRequest(id, grade, confidence, comment, paperTitle);
        try {
            return ResponseEntity.ok(paperService.submitPaperAuditInfo(auditInfoRequest, username, 2));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    @GetMapping("/publishResult")
    public ResponseEntity<ResponseObject<Object>> publishResult(@RequestParam String title, @RequestHeader String jwt_token) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt_token);
        try {
            return ResponseEntity.ok(paperService.publishResult(title, username));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    private List<AuthorInfoRequest> processAuthorInfoRequest(List<String> authorInfos) {
        List<AuthorInfoRequest> authorInfoRequests = new LinkedList<>();
        ObjectMapper mapper = new ObjectMapper();
        StringBuilder temp = new StringBuilder();
        for (String infoString : authorInfos) {
            if (infoString.contains("{")) {
                temp = new StringBuilder();
            } else {
                temp.append(",");
            }
            temp.append(infoString);
            if (infoString.contains("}")) {
                try {
                    AuthorInfoRequest request = (mapper.readValue(temp.toString(), AuthorInfoRequest.class));
                    authorInfoRequests.add(request);
                } catch (Exception e) {
                    return new LinkedList<>();
                }
            }
        }
        return authorInfoRequests;
    }

    @GetMapping("/getPapersByUser")
    public ResponseEntity<ResponseObject<List<Paper>>> getPapersByUser(@RequestHeader String jwt_token) {
        String username = getUsernameFromToken(jwt_token);
        try {
            return ResponseEntity.ok(paperService.getPapersByUser(username));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    @GetMapping("/getPapersByInspector")
    public ResponseEntity<ResponseObject<List<Paper>>> getPapersByInspector(@RequestHeader String jwt_token) {
        String username = getUsernameFromToken(jwt_token);
        try {
            return ResponseEntity.ok(paperService.getPapersByInspectorContains(username));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    @GetMapping("/getMyAuditedPaper")
    public ResponseEntity<ResponseObject<List<Paper>>> getAuditResultsOfMyPaper(@RequestHeader String jwt_token) {
        String username = getUsernameFromToken(jwt_token);
        try {
            return ResponseEntity.ok(paperService.getMyAuditedPaper(username));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    @GetMapping("/getPapersByConference")
    public ResponseEntity<ResponseObject<List<Paper>>> getAuditResults(@RequestParam String conferenceShortName) {
        try {
            return ResponseEntity.ok(paperService.getPapersByConference(conferenceShortName));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    @PostMapping("/addRebuttalForPaper")
    public ResponseEntity<ResponseObject<String>> addRebuttalForPaper(@RequestHeader String jwt_token, @RequestParam String rebuttal, @RequestParam String title) {
        String username = getUsernameFromToken(jwt_token);
        try {
            return ResponseEntity.ok(paperService.addRebuttalForPaper(title, rebuttal));
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
