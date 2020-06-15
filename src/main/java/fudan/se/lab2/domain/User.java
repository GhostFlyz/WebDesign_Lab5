package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author LBW
 */
@Entity
@Table(name = "user", indexes = {
        @Index(columnList = "username")
})
public class User implements UserDetails {

    private static final long serialVersionUID = -6140085056226164016L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;
    private String fullname;
    /* 根据需求，添加了email及region字段 */
    private String email;
    private String region;
    private String institution;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Conference> pcMemberConfs;
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Conference> authorConfs;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Authority> authorities = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Topic> topics;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    @JsonIgnore
    private Set<Paper> papers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Paper> paperToAudited;

    public User() {
    }

    public User(String username, String password, String fullname, Set<Authority> authorities) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.authorities = authorities;
    }

    public User(String username, String password, String fullname, Set<Authority> authorities,
                String email, String region, String institution) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.authorities = authorities;
        this.email = email;
        this.region = region;
        this.institution = institution;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) return false;
        return this.id.equals(((User) obj).id);
    }

    @Override
    public int hashCode() {
        return this.id == null ? this.username.hashCode() : (int) (this.id % Integer.MAX_VALUE);
    }

    public Set<Conference> getAuthorConfs() {
        return authorConfs;
    }

    public void setAuthorConfs(Set<Conference> authorConfs) {
        this.authorConfs = authorConfs;
    }

    public Set<Conference> getPcMemberConfs() {
        return pcMemberConfs;
    }

    public void setPcMemberConfs(Set<Conference> pcMemberConfs) {
        this.pcMemberConfs = pcMemberConfs;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    public void addToTopics(Topic topic) {
        this.topics.add(topic);
    }

    public Set<Paper> getPapers() {
        return papers;
    }

    public void setPapers(Set<Paper> papers) {
        this.papers = papers;
    }

    public void addToPaper(Paper paper) {
        if (this.papers == null) this.papers = new HashSet<>();
        this.papers.add(paper);
    }

    public Set<Paper> getPaperToAudited() {
        return paperToAudited;
    }

    public void setPaperToAudited(Set<Paper> paperToAudited) {
        this.paperToAudited = paperToAudited;
    }

    public void addToPaperToAudited(Paper paper) {
        if (this.paperToAudited == null) this.paperToAudited = new HashSet<>();
        this.paperToAudited.add(paper);
    }

    public void addToPcMemberConfs(Conference conference) {
        if (this.pcMemberConfs == null) pcMemberConfs = new HashSet<>();
        pcMemberConfs.add(conference);
    }

    public void addToAuthorConfs(Conference conference) {
        if (this.authorConfs == null) authorConfs = new HashSet<>();
        authorConfs.add(conference);
    }
}
