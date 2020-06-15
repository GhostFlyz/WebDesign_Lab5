package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.AuditInfoRequest;
import fudan.se.lab2.controller.request.AuthorInfoRequest;
import fudan.se.lab2.controller.request.PaperSubmitRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.domain.*;
import fudan.se.lab2.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@Service
public class PaperService {
    private PaperRepository paperRepository;
    private UserRepository userRepository;
    private ConferenceRepository conferenceRepository;
    private TopicRepository topicRepository;
    private AuthorInfoRepository authorInfoRepository;
    private AuditInfoRepository auditInfoRepository;

    private static final String SUCCESS = "success";
    private static final String NOT_CHAIR = "user is not the conference's chair";

    @Value("${file.uploadFolder}")
    private String uploadFolder;
    @Value("${file.staticAccessPath}")
    private String staticAccessPath;

    @Autowired
    public PaperService(PaperRepository paperRepository, UserRepository userRepository,
                        ConferenceRepository conferenceRepository, TopicRepository topicRepository,
                        AuthorInfoRepository authorInfoRepository, AuditInfoRepository auditInfoRepository) {
        this.paperRepository = paperRepository;
        this.userRepository = userRepository;
        this.conferenceRepository = conferenceRepository;
        this.topicRepository = topicRepository;
        this.authorInfoRepository = authorInfoRepository;
        this.auditInfoRepository = auditInfoRepository;
    }

    public String submitPaperForConference(PaperSubmitRequest request,
                                           String username, boolean change) {
        List<User> users = new LinkedList<>();
        Set<Topic> topics = new HashSet<>();
        List<Conference> conferences = new LinkedList<>();
        Conference conference = null;
        String check = checkPaper(request, username, change, users, topics, conferences);

        if (!"valid".equals(check)) {
            return check;
        }
        User user = users.get(0);
        List<AuthorInfo> authorInfos = new LinkedList<>();
        for (AuthorInfoRequest authorInfoRequest : request.getAuthorInfos()) {
            String fullName = authorInfoRequest.getFullName();
            String email = authorInfoRequest.getEmail();
            String region = authorInfoRequest.getRegion();
            String ins = authorInfoRequest.getInstitution();
            if (!AuthService.EMAIL_PATTERN.matcher(email).matches()) {
                return "author " + fullName + "'s email is invalid";
            }
            AuthorInfo authorInfo;
            List<AuthorInfo> finds = authorInfoRepository.findAuthorInfosByFullNameAndEmail(fullName, email);

            if (!finds.isEmpty()) authorInfo = finds.get(0);
            else authorInfo = new AuthorInfo(fullName, ins, region, email);

            authorInfoRepository.save(authorInfo);
            authorInfo = authorInfoRepository.findAuthorInfosByFullNameAndEmail(fullName, email).get(0);
            authorInfos.add(authorInfo);
        }
        conference = conferences.get(0);
        String title = request.getTitle();
        String abstractContent = request.getAbstractContent();
        String path;
        String fileName = UUID.randomUUID().toString() + ".pdf";
        String save = savePaper(request, fileName);
        if (!save.equals(SUCCESS)) return save;
        //String title, String abstractContent, User author, Conference conference, String filePath
        path = "http://114.115.151.236:8080" + "/upload/" + fileName;
        Paper paper;
        if (change) {
            paper = paperRepository.findPaperById(request.getId());
            paper.setTitle(title);
            paper.setAbstractContent(abstractContent);
            paper.setAuthor(user);
            paper.setConference(conference);
            paper.setFilePath(path);
        } else {
            paper = new Paper(title, abstractContent, user, conference, path);
        }
        paper.setAuthorInfos(authorInfos);
        paper.setTopics(topics);
        paperRepository.save(paper);
        conference.addToAuthors(users);
        conferenceRepository.save(conference);
        user.getAuthorConfs().add(conference);
        user.addToPaper(paper);
        userRepository.save(user);
        for (AuthorInfo authorInfo : authorInfos) {
            authorInfo.addToPapers(paper);
            authorInfoRepository.save(authorInfo);
        }
        return SUCCESS;
    }

    private String savePaper(PaperSubmitRequest request, String fileName) {
        String title = request.getTitle();
        String abstractContent = request.getAbstractContent();
        String path;
        MultipartFile file = request.getFile();
        if (fileName == null) {
            return "file does not have valid name";
        }
        if (fileName.length() == 0) fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf('.') + 1);
        if (!"pdf".equals(suffix)) {
            return fileName + " file is not of valid type(only pdf is allowed)";
        }
        if (title.length() > 50) {
            return "title's length should less than 50";
        }
        if (abstractContent.length() > 800) {
            return "abstract's length should less than 800";
        }
        try {
            String absolutePath = uploadFolder;
            File upload = new File(absolutePath);
            if (!upload.exists()) upload.mkdirs();
            path = absolutePath + fileName;
            File dest = new File(path);
            file.transferTo(dest);
        } catch (Exception e) {
            return (e.getMessage() + ": paper file transfer failed");
        }
        return SUCCESS;
    }

    public ResponseObject<AuditInfo> submitPaperAuditInfo(AuditInfoRequest request, String username, int num) {
        String comment = request.getComment();
        String grade = request.getGrade();
        String confidence = request.getConfidence();
        String paperTitle = request.getPaperTitle();
        Paper paper = paperRepository.findPaperByTitle(paperTitle);
        User inspector = userRepository.findByUsername(username);
        Conference conference = paper.getConference();
        if (comment.length() > 800) {
            return new ResponseObject<>(404, "comment's length should less than 800", null);
        }
        AuditInfo auditInfo;
        if (num == 1) {
            auditInfo = auditInfoRepository.findAuditInfoById(request.getId());
            if (auditInfo.isFirstchange()) {
                return new ResponseObject<>(404, "you have already submit your first change", null);
            }
            auditInfo.setComment(request.getComment());
            auditInfo.setConfidence(request.getConfidence());
            auditInfo.setGrade(request.getGrade());
            auditInfo.setFirstchange(true);
        } else if (num == 2) {
            auditInfo = auditInfoRepository.findAuditInfoById(request.getId());
            if (auditInfo.isSecondchange()) {
                return new ResponseObject<>(404, "you have already submit your second change", null);
            }
            auditInfo.setComment(request.getComment());
            auditInfo.setConfidence(request.getConfidence());
            auditInfo.setGrade(request.getGrade());
            auditInfo.setSecondchange(true);
        } else {
            auditInfo = new AuditInfo(grade, comment, confidence, paper, inspector, conference);
            paper.addToAuditInfo(auditInfo);
        }
        paperRepository.save(paper);
        auditInfoRepository.save(auditInfo);

        return new ResponseObject<>(200, SUCCESS, auditInfo);
    }

    private String checkPaper(PaperSubmitRequest request,
                              String username, boolean change, List<User> users,
                              Set<Topic> topics, List<Conference> conferences) {
        if (!change && paperRepository.findPaperByTitle(request.getTitle()) != null) {
            return "paper has existed";
        }
        if (change && paperRepository.findPaperById(request.getId()) == null) {
            return "paper does not existed";
        }
        List<Conference> conferences1 =
                conferenceRepository.findConferencesByShortName(request.getConferenceShortName());
        if (conferences1 == null || conferences1.isEmpty()) {
            return "conference not exist";
        }
        Conference conference = conferences1.get(0);
        conferences.add(conference);
        if (!conference.isCanBeSubmitted()) {
            return "the conference has not been able to submitted";
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "user " + username + " does not exist";
        }
        if (user.equals(conference.getChair())) {
            return "the conference's chair can not submit paper to this conference";
        }
        users.add(user);

        for (String topicName : request.getTopics()) {
            List<Topic> list = topicRepository.findTopicsByTopicNameAndAndConference(topicName, conference);
            if (list.isEmpty()) return "this conference does not have topic " + topicName;
            topics.add(list.get(0));
        }
        return "valid";
    }

    public ResponseObject<List<Paper>> getPapersByUser(String username) {
        User author = userRepository.findByUsername(username);
        if (author == null) return new ResponseObject<>(404, "user does not exist", null);
        List<Paper> papers = paperRepository.findPapersByAuthor(author);
        return new ResponseObject<>(200, SUCCESS, papers);
    }

    public ResponseObject<List<Paper>> getPapersByInspectorContains(String username) {
        User inspector = userRepository.findByUsername(username);
        if (inspector == null) return new ResponseObject<>(404, "user does not exist", null);
        List<Paper> papers = paperRepository.findPapersByInspectorsContains(inspector);
        return new ResponseObject<>(200, SUCCESS, papers);
    }

    public ResponseObject<List<Paper>> getPapersByConference(String conferenceShortName) {
        List<Conference> conferences = conferenceRepository.findConferencesByShortName(conferenceShortName);
        if (conferences.size() == 0) return new ResponseObject<>(404, "conference does not exist", null);
        Conference conference = conferences.get(0);
        List<Paper> papers = paperRepository.findPapersByConference(conference);
        return new ResponseObject<>(200, SUCCESS, papers);
    }

    public ResponseObject<List<Paper>> getMyAuditedPaper(String username) {
        User author = userRepository.findByUsername(username);
        List<Paper> papers = paperRepository.findPapersByAuthor(author);
        List<Paper> auditedPaper = new LinkedList<>();
        for (Paper paper : papers) {
            if (paper.getAuditInfos().size() == 3 && paper.isResultPublished()) {
                auditedPaper.add(paper);
            }
        }
        return new ResponseObject<>(200, SUCCESS, auditedPaper);
    }

    public ResponseObject<String> addRebuttalForPaper(String title, String rebuttal) {
        Paper paper = paperRepository.findPaperByTitle(title);
        if (paper.getRebuttal() != null) {
            return new ResponseObject<>(404, "Already rebuttal", null);
        }
        paper.setRebuttal(rebuttal);
        paperRepository.save(paper);
        return new ResponseObject<>(200, SUCCESS, rebuttal);
    }

    public ResponseObject<Object> publishResult(String title, String username) {
        User user = userRepository.findByUsername(username);
        Paper paper = paperRepository.findPaperByTitle(title);
        List<AuditInfo> firstResults = new LinkedList<>();
        List<AuditInfo> finalResults = new LinkedList<>();
        if (!user.equals(paper.getConference().getChair())) {
            return new ResponseObject<>(404, NOT_CHAIR, null);
        }
        paper.setResultPublished(true);
        for (AuditInfo auditInfo : paper.getAuditInfos()) {
            if (auditInfo.isFirstchange())
                firstResults.add(auditInfo);
            if (auditInfo.isSecondchange())
                finalResults.add(auditInfo);
        }
        if (paper.getFirstAccepted().equals("waiting for publication"))
            paper.setFirstAccepted(getResult(firstResults));
        paper.setFinalAccepted(getResult(finalResults));
        paperRepository.save(paper);
        return new ResponseObject<>(200, SUCCESS, null);
    }

    private String getResult(List<AuditInfo> results) {
        if (results.size() == 3) {
            for (AuditInfo auditInfo : results) {
                if (auditInfo.getGrade().length() != 1)
                    return "rejected";
            }
            return "accepted";
        } else
            return "waiting for publication";
    }
}
