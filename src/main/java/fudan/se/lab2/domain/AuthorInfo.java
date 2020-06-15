package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class AuthorInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;
    private String institution;
    private String region;
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Paper> papers;

    public AuthorInfo() {
    }

    public AuthorInfo(String fullName, String institution,
                      String region, String email) {
        this.fullName = fullName;
        this.institution = institution;
        this.region = region;
        this.email = email;
    }

    public Set<Paper> getPapers() {
        return papers;
    }

    public void setPapers(Set<Paper> papers) {
        this.papers = papers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public void addToPapers(Paper paper) {
        if (this.papers == null) this.papers = new HashSet<>();
        this.papers.add(paper);
    }
}
