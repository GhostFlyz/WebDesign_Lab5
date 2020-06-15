package fudan.se.lab2.controller.request;

public class InvitationRequest {
    private String inviterName;
    private String inviteeName;
    private String conferenceName;

    public InvitationRequest(){}
    public InvitationRequest(String inviterName,String inviteeName,String conferenceName){
        this.inviterName = inviterName;
        this.inviteeName = inviteeName;
        this.conferenceName = conferenceName;
    }

    public void setInviterName(String inviterName) {
        this.inviterName = inviterName;
    }

    public String getInviterName(){
        return inviterName;
    }

    public void setInviteeName(String inviteeName) {
        this.inviteeName = inviteeName;
    }

    public String getInviteeName() {
        return inviteeName;
    }

    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }

    public String getConferenceName() {
        return conferenceName;
    }
}
