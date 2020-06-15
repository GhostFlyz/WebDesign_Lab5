package fudan.se.lab2.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Inviter")
    private String inviter;
    @Column(name = "Invitee")
    private String invitee;
    @Column(name = "Conference")
    private String conference;
    @Column(name = "Status", columnDefinition = "varchar(255) default 'wait'")
    private String status;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "invitations")
    private Set<Topic> topics;

    public Invitation() {
    }

    public Invitation(String inviter, String invitee, String conference, Set<Topic> topics) {
        this.inviter = inviter;
        this.invitee = invitee;
        this.conference = conference;
        this.status = "wait";
        this.topics = topics;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    public String getInvitee() {
        return invitee;
    }

    public void setInvitee(String invitee) {
        this.invitee = invitee;
    }

    public String getConference() {
        return conference;
    }

    public void setConference(String conference) {
        this.conference = conference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }
}
