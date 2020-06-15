package fudan.se.lab2.controller.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class PaperSubmitRequest {
    private MultipartFile file;
    private Long id;
    private String title;
    private String abstractContent;
    private String conferenceShortName;
    private List<String> topics;
    private List<AuthorInfoRequest> authorInfos;

    public PaperSubmitRequest(){}
    public PaperSubmitRequest(MultipartFile file, String title, Long id, String abstractContent, String conferenceShortName,
                              List<String> topics, List<AuthorInfoRequest> authorInfos){
        this.file = file;
        this.title = title;
        this.id = id;
        this.abstractContent = abstractContent;
        this.conferenceShortName = conferenceShortName;
        this.topics = topics;
        this.authorInfos = authorInfos;
    }

    public void setAuthorInfos(List<AuthorInfoRequest> authorInfos) {
        this.authorInfos = authorInfos;
    }

    public List<AuthorInfoRequest> getAuthorInfos() {
        return authorInfos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAbstractContent(String abstractContent) {
        this.abstractContent = abstractContent;
    }

    public String getAbstractContent() {
        return abstractContent;
    }

    public MultipartFile getFile() {
        return file;
    }

    public String getConferenceShortName() {
        return conferenceShortName;
    }

    public void setConferenceShortName(String conferenceShortName) {
        this.conferenceShortName = conferenceShortName;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}
