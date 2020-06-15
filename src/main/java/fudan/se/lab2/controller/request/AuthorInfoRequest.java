package fudan.se.lab2.controller.request;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthorInfoRequest {
    private String fullName;
    private String institution;
    private String region;
    private String email;

    public AuthorInfoRequest(){}
    public AuthorInfoRequest(String fullName, String institution, String region, String email){
        this.fullName = fullName;
        this.institution = institution;
        this.region = region;
        this.email = email;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getInstitution() {
        return institution;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegion() {
        return region;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        }catch (Exception e) {
            return "{" +
                    "\"fullName\": \"" + fullName + '\"' +
                    ", \"institution\":\"" + institution + '\"' +
                    ", \"region\":\"" + region + '\"' +
                    ", \"email\":\"" + email + '\"' +
                    '}';
        }
    }
}
