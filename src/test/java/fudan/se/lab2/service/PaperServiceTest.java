package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.AuditInfoRequest;
import fudan.se.lab2.controller.request.AuthorInfoRequest;
import fudan.se.lab2.controller.request.PaperSubmitRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.domain.Paper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = true)
class PaperServiceTest {
    @Autowired
    private PaperService paperService;

    private String basePath = "http://localhost:8080";

    @Test
    void submitPaperForConference() throws Exception {
        long randByTime = System.currentTimeMillis();
        String title = "test" + randByTime;
        String username = "aaaaa";
        String username2 = "Raccoon";
        List<String> users = new LinkedList<>();
        users.add(username);
        users.add(username2);
        String chairUser = "tsttt";
        String doesNotExistUser = "xxx";
        String abstractContent = "aaaa";
        String conferenceShortName = "test1587618879125";
        String anotherConference = "L5";
        String doesNotExistConference = "xxx";
        AuthorInfoRequest request1 = new AuthorInfoRequest("a", "a", "a", "a@a.com");
        AuthorInfoRequest request2 = new AuthorInfoRequest("b", "b", "b", "b@b.com");
        List<AuthorInfoRequest> requests = new LinkedList<>();
        requests.add(request1);
        requests.add(request2);
        MultipartFile file = new MultipartFile() {
            @Override
            public String getName() {
                return "test2.pdf";
            }

            @Override
            public String getOriginalFilename() {
                return "test2.pdf";
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }
        };
        PaperSubmitRequest request = new PaperSubmitRequest();
        request.setFile(file);
        request.setTitle(title);
        request.setAbstractContent(abstractContent);
        request.setConferenceShortName(conferenceShortName);
        List<String> topics = new LinkedList<>();
        topics.add("a");
        request.setTopics(topics);
        request.setAuthorInfos(requests);
        assertEquals("success", paperService.submitPaperForConference(request, username, false));
        assertEquals("paper has existed", paperService.submitPaperForConference(request, username, false));
        request.setTitle("xxx");
        users.add(doesNotExistUser);
        assertEquals("user " + doesNotExistUser + " does not exist",
                paperService.submitPaperForConference(request, doesNotExistUser, false));
        users.remove(doesNotExistUser);
        users.add(chairUser);
        assertEquals("the conference's chair can not submit paper to this conference",
                paperService.submitPaperForConference(request, chairUser, false));
        users.remove(chairUser);
        request.setConferenceShortName(doesNotExistConference);
        assertEquals("conference not exist", paperService.submitPaperForConference(request, username, false));
        request.setConferenceShortName(anotherConference);
        assertEquals("the conference has not been able to submitted",
                paperService.submitPaperForConference(request, username, false));
        request.setConferenceShortName(conferenceShortName);
        topics.add("b");
        assertEquals("this conference does not have topic b",
                paperService.submitPaperForConference(request, username, false));
    }

    @Test
    void getPapersByUser() throws Exception {
        String username = "aaaaa";
        ResponseObject response = paperService.getPapersByUser(username);
        List<Paper> list = (List<Paper>) response.getContent();
        assertEquals("success", response.getMessage());
        for (Paper p : list) {
            assertEquals("aaaaa", p.getAuthor().getUsername());
            System.out.println(p.getTitle() + ":" + p.getFilePath());
        }
    }

    @Test
    void getPaperByInspectorContains() throws Exception {
        String username = "aaaaa";
        assertEquals(200, paperService.getPapersByInspectorContains(username).getStatusCode());
    }

    @Test
    void testAddRebuttal() throws Exception {
        String title = createPaper();
        String rebuttal = "test_rebuttal";
        assertEquals("success", paperService.addRebuttalForPaper(title, rebuttal).getMessage());
        assertEquals("Already rebuttal", paperService.addRebuttalForPaper(title, rebuttal).getMessage());
    }

    @Test
    void testSubmitAuditInfo() throws Exception {
        AuditInfoRequest request = new AuditInfoRequest();
        String title = createPaper();
        request.setPaperTitle(title);
        StringBuilder toLongComment = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            toLongComment.append("LongComment");
        }
        request.setConfidence("high");
        request.setGrade("-1");
        String username = "aaaaa";
        request.setComment(toLongComment.toString());
        assertEquals("comment's length should less than 800", paperService.submitPaperAuditInfo(request, username, 0).getMessage());
        request.setComment("test_comment");
        assertEquals("success", paperService.submitPaperAuditInfo(request, username, 0).getMessage());
        request.setId(paperService.submitPaperAuditInfo(request, username, 0).getContent().getId());
        assertEquals("success", paperService.submitPaperAuditInfo(request, username, 1).getMessage());
        assertEquals("you have already submit your first change", paperService.submitPaperAuditInfo(request, username, 1).getMessage());
        assertEquals("success", paperService.submitPaperAuditInfo(request, username, 2).getMessage());
        assertEquals("you have already submit your second change", paperService.submitPaperAuditInfo(request, username, 2).getMessage());
    }

    private String createPaper() {
        long randByTime = System.currentTimeMillis();
        String title = "test" + randByTime;
        String username = "aaaaa";
        String username2 = "Raccoon";
        List<String> users = new LinkedList<>();
        users.add(username);
        users.add(username2);
        String abstractContent = "aaaa";
        String conferenceShortName = "test1587618879125";
        AuthorInfoRequest request1 = new AuthorInfoRequest("a", "a", "a", "a@a.com");
        AuthorInfoRequest request2 = new AuthorInfoRequest("b", "b", "b", "b@b.com");
        List<AuthorInfoRequest> requests = new LinkedList<>();
        requests.add(request1);
        requests.add(request2);
        MultipartFile file = new MultipartFile() {
            @Override
            public String getName() {
                return "test2.pdf";
            }

            @Override
            public String getOriginalFilename() {
                return "test2.pdf";
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }
        };
        PaperSubmitRequest request = new PaperSubmitRequest();
        request.setFile(file);
        request.setTitle(title);
        request.setAbstractContent(abstractContent);
        request.setConferenceShortName(conferenceShortName);
        List<String> topics = new LinkedList<>();
        topics.add("a");
        request.setTopics(topics);
        request.setAuthorInfos(requests);
        paperService.submitPaperForConference(request, username, false);
        return title;
    }

    @Test
    void testPublishResult() throws Exception {
        String title = createPaper();
        String username = "tsttt";
        String anotherusername = "aaaaa";
        AuditInfoRequest request = new AuditInfoRequest();
        request.setPaperTitle(title);
        request.setConfidence("high");
        request.setGrade("-1");
        request.setComment("test_comment");
        paperService.submitPaperAuditInfo(request, username, 0);
        assertEquals("success", paperService.publishResult(title, username).getMessage());
        assertEquals("user is not the conference's chair", paperService.publishResult(title, anotherusername).getMessage());
    }

    @Test
    void testFindPaperByConference() throws Exception {
        createPaper();
        String conferenceShortName = "test1587618879125";
        assertEquals("success", paperService.getPapersByConference(conferenceShortName).getMessage());
        assertEquals("conference does not exist", paperService.getPapersByConference("test0816").getMessage());
    }

    @Test
    void testgGtMyAuditedPaper() throws Exception{
        assertEquals("success", paperService.getMyAuditedPaper("aaaaa").getMessage());
    }
}