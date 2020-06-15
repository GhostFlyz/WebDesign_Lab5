package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String topicName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Conference conference;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "topics")
    private Set<User> users;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Invitation> invitations;

    public Topic(){}
    public Topic(String topicName){this.topicName = topicName;}

    public Set<Invitation> getInvitations() {
        return invitations;
    }

    public void setInvitations(Set<Invitation> invitations) {
        this.invitations = invitations;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Long getId() {
        return id;
    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
    public void addToUsers(User user){
        this.users.add(user);
    }
    public void addToInvitations(Invitation invitation){
        if(this.invitations == null) invitations = new HashSet<>();
        invitations.add(invitation);
    }
}
