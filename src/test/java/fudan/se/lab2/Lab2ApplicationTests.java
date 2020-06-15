package fudan.se.lab2;

import fudan.se.lab2.controller.request.ConferenceRequest;
import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


@SpringBootTest
class Lab2ApplicationTests {
    private RestTemplate template;

    void contextLoads() {

    }

   // @Test
    void testAllConf() throws Exception{
        template = new RestTemplate();
        ResponseObject loginMsg = testLogin("tsttt", "aaaaa11111");
        String token = (String) loginMsg.getContent();
        HttpHeaders headers = new HttpHeaders();
        headers.add("jwt_token", token);
        HttpEntity<ConferenceRequest> requestEntity = new HttpEntity<>(headers);
        String msg =
                template.exchange(
                        "http://localhost:8080/findAllConferences",
                        HttpMethod.GET, requestEntity, String.class).getBody();
        System.out.println(msg);
    }

    //@Test
    void testAuditConference() throws Exception {
        template = new RestTemplate();
        Set<String> auths = new HashSet<>();
        auths.add("Admin");
        testRegister("tsttt", "aaaaa11111", "a", auths, "a@b.com", "a", "a");
        ResponseObject loginMsg = testLogin("tsttt", "aaaaa11111");
        String token = (String) loginMsg.getContent();
        HttpHeaders headers = new HttpHeaders();
        headers.add("jwt_token", token);
        testApplyForConf("st2", headers);
        HttpEntity<ConferenceRequest> requestEntity = new HttpEntity<>(headers);
        String msg =
                template.exchange(
                        "http://localhost:8080/auditConference?conferenceShortName=st2&status=pass",
                        HttpMethod.GET, requestEntity, String.class).getBody();
        System.out.println(msg);
        Assert.isTrue(msg != null && msg.contains("success"));
    }

    //@Test
    void testFindConferenceByChair() throws Exception {
        template = new RestTemplate();
        ResponseObject loginMsg = testLogin("tsttt", "aaaaa11111");
        String token = (String) loginMsg.getContent();
        HttpHeaders headers = new HttpHeaders();
        headers.add("jwt_token", token);
        HttpEntity<ConferenceRequest> requestEntity = new HttpEntity<>(headers);
        String msg =
                template.exchange(
                        "http://localhost:8080/findConferenceByChair",
                        HttpMethod.GET, requestEntity, String.class).getBody();
        System.out.println(msg);
        Assert.isTrue(msg != null && msg.contains("success"));
    }

    //@Test
    void testFindConferenceByParticipation() throws Exception {
        template = new RestTemplate();
        ResponseObject loginMsg = testLogin("tsttt", "aaaaa11111");
        String token = (String) loginMsg.getContent();
        HttpHeaders headers = new HttpHeaders();
        headers.add("jwt_token", token);
        HttpEntity<ConferenceRequest> requestEntity = new HttpEntity<>(headers);
        String msg =
                template.exchange(
                        "http://localhost:8080/findConferenceByParticipation",
                        HttpMethod.GET, requestEntity, String.class).getBody();
        System.out.println(msg);
        Assert.isTrue(msg != null && msg.contains("success"));
    }

   // @Test
    void testFindCharacterByConferenceAndUser() throws Exception {
        template = new RestTemplate();
        ResponseObject loginMsg = testLogin("tsttt", "aaaaa11111");
        String token = (String) loginMsg.getContent();
        HttpHeaders headers = new HttpHeaders();
        headers.add("jwt_token", token);
        HttpEntity<ConferenceRequest> requestEntity = new HttpEntity<>(headers);
        String msg =
                template.exchange(
                        "http://localhost:8080/findCharacterByConferenceAndUser?conferenceShortName=st2",
                        HttpMethod.GET, requestEntity, String.class).getBody();
        System.out.println(msg);
        Assert.isTrue(msg != null && msg.contains("success"));
    }

  //  @Test
    void testSetConferenceCanBeSubmitted() throws Exception {
        template = new RestTemplate();
        ResponseObject loginMsg = testLogin("tsttt", "aaaaa11111");
        String token = (String) loginMsg.getContent();
        HttpHeaders headers = new HttpHeaders();
        headers.add("jwt_token", token);
        HttpEntity<ConferenceRequest> requestEntity = new HttpEntity<>(headers);
        String msg =
                template.exchange(
                        "http://localhost:8080/setConferenceCanBeSubmitted?conferenceShortName=st2",
                        HttpMethod.GET, requestEntity, String.class).getBody();
        System.out.println(msg);
        Assert.isTrue(msg != null && msg.contains("success"));
    }


    //@Test
    void testSubmitPaper() throws Exception {
        template = new RestTemplate();
        ResponseObject loginMsg = testLogin("aaaaa", "a111111111");
        String token = (String) loginMsg.getContent();
        HttpHeaders headers = new HttpHeaders();
        headers.add("jwt_token", token);
        //headers.setContentType(MediaType.APPLICATION_JSON);
        ByteArrayResource contentsAsResource = new ByteArrayResource("test".getBytes("UTF-8")) {
            @Override
            public String getFilename() {
                return "test.pdf";
            }
        };
        String fileLocal = "D:\\test.pdf";
        FileSystemResource resource = new FileSystemResource(new File(fileLocal));
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("file", contentsAsResource);
        param.add("title", "aaaaaa");
        param.add("abstractContent", "a");
        param.add("conferenceShortName", "st2");
        HttpEntity<MultiValueMap> entity = new HttpEntity<>(param, headers);
        String msg = (template.postForObject("http://localhost:8080/submitPaper",
                entity, String.class));
        System.out.println(msg);
        Assert.isTrue(msg != null && msg.contains("success"));
    }

    //@Test
    void testRegisterAndLogin() throws Exception {
        template = new RestTemplate();
        Set<String> auths = new HashSet<>();
        auths.add("Admin");
        testRegister("aaaaa", "a111111111", "a", auths, "a@b.com", "a", "a");
        ResponseObject loginMsg = testLogin("aaaaa", "a111111111");
        System.out.println(loginMsg);
        testCheckUsername("aaaaa");
        if (loginMsg.getMessage().equals("success")) {
            //int idx1 = loginMsg.indexOf(':');
            //int idx2 = loginMsg.indexOf('|', idx1 + 1);
            //String token = loginMsg.substring(idx1 + 1, idx2);
            String token = (String) loginMsg.getContent();

            System.out.println("token:" + token);
            HttpHeaders headers = new HttpHeaders();
            headers.add("jwt_token", token);
            template = new RestTemplate();
            testApplyForConf("bccc", headers);
            testApplyForConf("cac", headers);
        }
    }

    void testCheckUsername(String username) {
        System.out.println(template.getForEntity("http://localhost:8080/checkUsername?username=" + username, String.class));
    }

    void testRegister(String username, String password, String fullname, Set<String> auths,
                      String email, String region, String institution) throws Exception {

        RegisterRequest registerRequest = new RegisterRequest(username, password, fullname, email, region, institution);
        System.out.println(template.postForEntity("http://localhost:8080/register", registerRequest, String.class));
    }

    ResponseObject testLogin(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);
        return (template.postForEntity("http://localhost:8080/login", loginRequest, ResponseObject.class)).getBody();
    }

  //  @Test
    void applyConf() throws Exception{
        template = new RestTemplate();
        ResponseObject loginMsg = testLogin("tsttt", "aaaaa11111");
        String token = (String) loginMsg.getContent();
        HttpHeaders headers = new HttpHeaders();
        headers.add("jwt_token", token);
        String shortName = "ttt";
        testApplyForConf(shortName, headers);
    }

    void testApplyForConf(String shortName, HttpHeaders headers) throws Exception {
        // template = new RestTemplate();
        String fullName = "a a";
        String time = "October 5-11,2020";
        String position = "a";
        String ddlForSub = "2020-01-01";
        String relDate = "2020-01-01";
        testApplyForConfHelper(headers, shortName, fullName, time, position, ddlForSub, relDate);
    }

    void testApplyForConfHelper(HttpHeaders httpHeaders, String shortName,
                                String fullName, String time, String position, String ddlForSubmit, String releaseDate) {
        Set<String> topics = new HashSet<>();
        ConferenceRequest conferenceRequest =
                new ConferenceRequest(shortName, fullName, time, position, ddlForSubmit, releaseDate, topics);
        HttpEntity<ConferenceRequest> requestEntity = new HttpEntity<>(conferenceRequest, httpHeaders);
        System.out.println(template.exchange("http://localhost:8080/applyConference", HttpMethod.POST,
                requestEntity, String.class));
    }
}
