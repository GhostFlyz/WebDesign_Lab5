package fudan.se.lab2.controller.request;

import java.util.List;

public class ResponseInvitationRequest {
    String conferenceName;
    String status;
    List<String> topics;

    public ResponseInvitationRequest(){
        // Empty Construct
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public List<String> getTopics() {
        return topics;
    }

    public String getConferenceName() {
        return conferenceName;
    }

    public String getStatus() {
        return status;
    }

    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
