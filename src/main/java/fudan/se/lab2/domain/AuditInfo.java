package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class AuditInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String grade;

    private String comment;

    private String confidence;

    @ManyToOne
    @JsonIgnore
    private Paper paper;

    @ManyToOne(fetch = FetchType.LAZY)
    private User inspector;

    @ManyToOne
    @JsonIgnore
    private Conference conference;

    @Column(name = "first_change", columnDefinition = "boolean default FALSE")
    private boolean firstchange;

    @Column(name = "second_change", columnDefinition = "boolean default FALSE")
    private boolean secondchange;


    public AuditInfo() {
    }

    public AuditInfo(String grade, String comment, String confidence, Paper paper, User inspector, Conference conference) {
        this.grade = grade;
        this.comment = comment;
        this.confidence = confidence;
        this.paper = paper;
        this.inspector = inspector;
        this.conference = conference;
        this.firstchange = false;
        this.secondchange = false;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getGrade() {
        return grade;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public Paper getPaper() {
        return paper;
    }

    public void setInspector(User inspector) {
        this.inspector = inspector;
    }

    public User getInspector() {
        return inspector;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

    public Conference getConference() {
        return conference;
    }

    public boolean isFirstchange() {
        return this.firstchange;
    }

    public void setFirstchange(boolean firstchange) {
        this.firstchange = firstchange;
    }

    public boolean isSecondchange() {
        return this.secondchange;
    }

    public void setSecondchange(boolean secondchange) {
        this.secondchange = secondchange;
    }
}
