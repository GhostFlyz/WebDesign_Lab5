package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.ConferenceRequest;
import fudan.se.lab2.controller.request.PaperSubmitRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.Paper;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = true)
class ConferenceServiceTest {
    @Autowired
    private ConferenceService conferenceService;
    @Autowired
    private UserRepository userRepository;
    private String randName;

    @Test
    void pcMemberChooseTopic() throws Exception{
        String confShort = "test1587618879125";
        String username = "Raccoon";
        conferenceService.helper();
        assertEquals(200, conferenceService.pcMemberChooseTopic(confShort, username, "a").getStatusCode());
    }

    @Test
    void processApply() throws Exception{
        //String shortName, String fullName, String time,
        //String position, String ddlForSubmit, String releaseDate
        long randByTime = System.currentTimeMillis();
        Set<String> topics = new HashSet<>();
        topics.add("a");
        randName = "test" + randByTime;
        ConferenceRequest request = new ConferenceRequest(randName
                , "test1_test1", "May 1-1,2020",
                "pos", "2020-01-01", "2020-02-02", topics);
        request.setUsername("tsttt");
        assertEquals("success", conferenceService.processApply(request));
        request.setShortName("notexist");
        request.setReleaseDate("xxx");
        assertEquals("release date is not a date", conferenceService.processApply(request));
        request.setReleaseDate("2019-12-31");
        assertEquals("the deadline for submit should be earlier than release", conferenceService.processApply(request));
        request.setReleaseDate("2020-02-02");
        request.setTime("January 3-10,2020");
        assertEquals("the release time should be earlier than hold time", conferenceService.processApply(request));
        request.setDdlForSubmit("xxx");
        assertEquals("ddl for submit is not a date", conferenceService.processApply(request));
        request.setShortName("test" + randByTime);
        assertTrue(conferenceService.processApply(request).contains("conference already exists"));
    }

    @Test
    void auditConference() throws Exception{
        //String conferenceShortName, String username, String status
        String conferenceShortName = "st2";
        String invalidConferenceShortName = "notexist";
        String username = "tsttt";
        String invalidUsername = "notexists";
        String status = "pass";
        String invalidStatus = "xxx";
        long randByTime = System.currentTimeMillis();
        Set<String> topics = new HashSet<>();
        topics.add("a");
        randName = "test" + randByTime;
        ConferenceRequest request = new ConferenceRequest(randName
                , "test1_test1", "May 1-1,2020",
                "pos", "2020-01-01", "2020-02-02", topics);
        request.setUsername(username);
        assertEquals("success", conferenceService.processApply(request));
        assertEquals("success", conferenceService.auditConference(randName, username, status));

        assertEquals("the conference does not exists", conferenceService.auditConference(invalidConferenceShortName, username, status));
        assertEquals("the user does not exists", conferenceService.auditConference(conferenceShortName, invalidUsername, status));
        assertEquals("the status should be either pass or reject", conferenceService.auditConference(conferenceShortName, username, invalidStatus));
    }

    @Test
    void findConferenceByChairUser()throws Exception {
        String username = "tsttt";
        assertEquals("success", conferenceService.findConferenceByChairUser(username).getMessage());
    }

    @Test
    void findConferenceByParticipation()throws Exception {
        String username = "tsttt";
        assertEquals("success", conferenceService.findConferenceByParticipation(username).getMessage());
    }

    @Test
    void findCharacterByConferenceAndUser()throws Exception {
        String username = "tsttt";
        String conferenceShortName = "st2";
        assertEquals("success",
                conferenceService.findCharacterByConferenceAndUser(username, conferenceShortName).getMessage());
        conferenceShortName = "notexist";
        assertEquals("conference not exist",
                conferenceService.findCharacterByConferenceAndUser(username, conferenceShortName).getMessage());
    }

    @Test
    void setConferenceCanBeSubmitted() throws Exception{
        String username = "tsttt";
        String anotherUser = "aaaaa";
        String conferenceShortName = "st2";
        String anotherConference = "rac_pcm";
        assertEquals("success", conferenceService.setConferenceCanBeSubmitted(username, conferenceShortName));
        assertEquals("user is not the conference's chair",
                conferenceService.setConferenceCanBeSubmitted(anotherUser, conferenceShortName));
        assertEquals("the conference has not be audited",
                conferenceService.setConferenceCanBeSubmitted(username, anotherConference));
    }

    @Test
    void findAllConferences()throws Exception {
        assertEquals("success", conferenceService.findAllConferences().getMessage());
    }

    @Test
    void testFindAllConferencesByPage(){
        Integer page = 0;
        Integer size = 5;
        ResponseObject<Page<Conference>> resp = conferenceService.findAllConferenceByPage(page, size);
        assertEquals("success", resp.getMessage());
        Page<Conference> pages = resp.getContent();
        for(Conference conference : pages.getContent()){
            assertNotNull(conference.getShortName());
        }
    }

    @Test
    void testFindAllConferencesByUser() throws Exception{
        String username = "Raccoon";
        ResponseObject<List<Conference>> resp = conferenceService.findAllConferenceByUser(username);
        assertEquals("success", resp.getMessage());
        for(Conference conference : resp.getContent()){
            System.out.println(conference.getShortName());
        }
    }

    @Test
    void testFindAllNotAudited() throws Exception{
        ResponseObject<List<Conference>> resp = conferenceService.findAllConferenceNotAudited();
        assertEquals("success", resp.getMessage());
        for(Conference conference : resp.getContent()){
            assertEquals("wait", conference.getAuditStatus());
        }
    }

    @Test
    void testDivideMethod() throws Exception{
        String username = "tsttt";
        String anotherUser = "aaaaa";
        String conferenceShortName = "st2";
        assertEquals("this conference has less than 3 PCMembers",conferenceService.audit_DivideByTopics(username,conferenceShortName).getMessage());
        assertEquals("this conference has less than 3 PCMembers",conferenceService.audit_DivideAverage(username,conferenceShortName).getMessage());
        assertEquals("user is not the conference's chair",conferenceService.audit_DivideByTopics(anotherUser,conferenceShortName).getMessage());
        assertEquals("user is not the conference's chair",conferenceService.audit_DivideAverage(anotherUser,conferenceShortName).getMessage());
        assertEquals("success",conferenceService.audit_DivideByTopics("testChair","L5").getMessage());
        assertEquals("success",conferenceService.audit_DivideAverage("testChair","L5").getMessage());
        assertEquals("no proper division method",conferenceService.audit_DivideByTopics("testChair","L5-2").getMessage());
        assertEquals("no proper division method",conferenceService.audit_DivideAverage("testChair","L5-2").getMessage());
    }
}