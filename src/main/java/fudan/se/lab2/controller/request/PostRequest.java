package fudan.se.lab2.controller.request;

public class PostRequest {
    private String title;
    private String content;
    private String type;
    private String torR;
    private Long themeId;
    private Long paperId;
    
    public PostRequest(){
        // Empty Construct
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTorR() {
        return torR;
    }

    public void setTorR(String torR) {
        this.torR = torR;
    }

    public Long getThemeId() {
        return themeId;
    }

    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }
}
