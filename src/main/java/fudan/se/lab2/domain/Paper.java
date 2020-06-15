package fudan.se.lab2.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "paper", indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "author_id")
})
public class Paper {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String title; // 长度限制50字符

    private String abstractContent; // 长度限制800字符

    private String rebuttal;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "papers")
    private List<AuthorInfo> authorInfos;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "paperToAudited")
    private List<User> inspectors;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Topic> topics;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "paper")
    private List<AuditInfo> auditInfos;

    @ManyToOne
    @JoinColumn(name = "conference_id", referencedColumnName = "id")
    private Conference conference; // 该论文投稿的会议
    private String filePath; // 暂定，论文pdf文件存储的路径

    @Column(name = "result_published", columnDefinition = "boolean default FALSE")
    private boolean resultPublished;

    private String firstAccepted;//首次录用结果

    private String finalAccepted;//最终录用结果

    public Paper() {
    }

    public Paper(String title, String abstractContent, User author, Conference conference, String filePath) {
        this.title = title;
        this.abstractContent = abstractContent;
        this.author = author;
        this.conference = conference;
        this.filePath = filePath;
        this.rebuttal = null;
        this.resultPublished = false;
        this.firstAccepted = "waiting for publication";
        this.finalAccepted = "waiting for publication";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

    public String getAbstractContent() {
        return abstractContent;
    }

    public void setAbstractContent(String abstractContent) {
        this.abstractContent = abstractContent;
    }

    public String getRebuttal() {
        return rebuttal;
    }

    public void setRebuttal(String rebuttal) {
        this.rebuttal = rebuttal;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    public void addToTopics(Topic topic) {
        if (this.topics == null) this.topics = new HashSet<>();
        topics.add(topic);
    }

    public List<User> getInspectors() {
        return inspectors;
    }

    public void setInspectors(List<User> inspectors) {
        this.inspectors = inspectors;
    }

    public void addToInspectors(User inspector) {
        if (this.inspectors == null) inspectors = new LinkedList<>();
        inspectors.add(inspector);
    }

    public List<AuditInfo> getAuditInfos() {
        return auditInfos;
    }

    public void setAuditInfos(List<AuditInfo> auditInfos) {
        this.auditInfos = auditInfos;
    }

    public void addToAuditInfo(AuditInfo auditInfo) {
        if (auditInfos == null) auditInfos = new LinkedList<>();
        auditInfos.add(auditInfo);
    }

    public List<AuthorInfo> getAuthorInfos() {
        return authorInfos;
    }

    public void setAuthorInfos(List<AuthorInfo> authorInfos) {
        this.authorInfos = authorInfos;
    }

    public void addToAuthorInfo(AuthorInfo authorInfo) {
        if (authorInfos == null) authorInfos = new LinkedList<>();
        authorInfos.add(authorInfo);
    }

    public boolean isResultPublished() {
        return this.resultPublished;
    }

    public void setResultPublished(boolean resultPublished) {
        this.resultPublished = resultPublished;
    }

    public String getFirstAccepted() {
        return this.firstAccepted;
    }

    public void setFirstAccepted(String firstAccepted) {
        this.firstAccepted = firstAccepted;
    }

    public String getFinalAccepted() {
        return this.finalAccepted;
    }

    public void setFinalAccepted(String finalAccepted) {
        this.finalAccepted = finalAccepted;
    }
}
