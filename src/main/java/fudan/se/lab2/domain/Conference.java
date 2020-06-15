package fudan.se.lab2.domain;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "conference", indexes = {@Index(columnList = "shortName"), @Index(columnList = "audit_status")})
//@Cacheable
//@CacheConfig(cacheNames = "conference")
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Conference {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String shortName;

    private String fullName;
    private String time;
    private String position;
    private String ddlForSubmit;
    private String releaseDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "conference")
    private Set<Topic> topics;

    @OneToOne
    private User chair;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "pcMemberConfs")
    private Set<User> pcMember;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "authorConfs")
    private Set<User> authors;

    @Column(name = "audit_status", columnDefinition = "varchar(255) default 'wait'")
    private String auditStatus;

    @Column(name = "can_be_submitted", columnDefinition = "boolean default FALSE")
    private boolean canBeSubmitted;

    public Conference(String shortName, String fullName, String time,
                      String position, String ddlForSubmit, String releaseDate, User chair) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.time = time;
        this.position = position;
        this.ddlForSubmit = ddlForSubmit;
        this.releaseDate = releaseDate;
        this.auditStatus = "wait";
        this.chair = chair;
        this.pcMember = new HashSet<>();
        this.authors = new HashSet<>();
        this.canBeSubmitted = false;
    }

    public Conference(String shortName, String fullName, String time,
                      String position, String ddlForSubmit, String releaseDate, User chair, Set<User> pcMember, Set<User> authors) {
        this(shortName, fullName, time, position, ddlForSubmit, releaseDate, chair);
        this.pcMember = pcMember;
        this.authors = authors;
    }

    public Conference() {
        this.auditStatus = "wait";
    }

    public boolean isCanBeSubmitted() {
        return canBeSubmitted;
    }

    public void setCanBeSubmitted(boolean canBeSubmitted) {
        this.canBeSubmitted = canBeSubmitted;
    }

    public Set<User> getPcMember() {
        return pcMember;
    }

    public void setPcMember(Set<User> pcMember) {
        this.pcMember = pcMember;
    }

    public Set<User> getAuthors() {
        return authors;
    }

    public void addToAuthors(Collection<User> authors){
        if(this.authors == null) this.authors = new HashSet<>();
        this.authors.addAll(authors);
    }

    public void setAuthors(Set<User> authors) {
        this.authors = authors;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public User getChair() {
        return chair;
    }

    public void setChair(User chair) {
        this.chair = chair;
    }

    public String getDdlForSubmit() {
        return ddlForSubmit;
    }

    public void setDdlForSubmit(String ddlForSubmit) {
        this.ddlForSubmit = ddlForSubmit;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void addToPCMember(User user) {
        this.pcMember.add(user);
    }

    public void addToAuthor(User user) {
        this.authors.add(user);
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }
}
