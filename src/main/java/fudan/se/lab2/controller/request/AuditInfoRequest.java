package fudan.se.lab2.controller.request;

public class AuditInfoRequest {
    private Long id;
    private String grade;
    private String comment;
    private String confidence;
    private String paperTitle;

    public AuditInfoRequest() {
    }

    public AuditInfoRequest(Long id, String grade, String confidence, String comment, String paperTitle) {
        this.id = id;
        this.grade = grade;
        this.comment = comment;
        this.confidence = confidence;
        this.paperTitle = paperTitle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getPaperTitle(){
        return paperTitle;
    }

    public void setPaperTitle(String paperTitle){
        this.paperTitle = paperTitle;
    }
}
