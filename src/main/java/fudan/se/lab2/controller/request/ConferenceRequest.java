package fudan.se.lab2.controller.request;

import java.util.Set;

public class ConferenceRequest {
    private String shortName;

    private String fullName;
    private String time;
    private String position;
    private String ddlForSubmit;
    private String releaseDate;

    private String username;
    private Set<String> topics;

    public ConferenceRequest(){}
    public ConferenceRequest(String shortName, String fullName, String time,
                             String position, String ddlForSubmit, String releaseDate, Set<String> topics){
        this.shortName = shortName;
        this.fullName = fullName;
        this.time = time;
        this.position = position;
        this.ddlForSubmit = ddlForSubmit;
        this.releaseDate = releaseDate;
        this.topics = topics;
    }

    public void setTopics(Set<String> topics) {
        this.topics = topics;
    }

    public Set<String> getTopics() {
        return topics;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getDdlForSubmit() {
        return ddlForSubmit;
    }

    public void setDdlForSubmit(String ddlForSubmit) {
        this.ddlForSubmit = ddlForSubmit;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTime() {
        return time;
    }

    public String getPosition() {
        return position;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }
}
