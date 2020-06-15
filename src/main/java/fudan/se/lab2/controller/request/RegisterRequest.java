package fudan.se.lab2.controller.request;

import fudan.se.lab2.domain.Authority;

import java.util.HashSet;
import java.util.Set;

/**
 * @author LBW
 */
public class RegisterRequest {
    private String username;
    private String password;
    private String fullname;
    private Set<String> authorities;
    /* 根据需求，添加了email及region字段 */
    private String email;
    private String region;
    private String institution;

    public RegisterRequest() {}

    public RegisterRequest(String username, String password, String fullname) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.authorities = new HashSet<>();
    }

    public RegisterRequest(String username, String password, String fullname,
                           String email, String region, String institution){
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.authorities = new HashSet<>();
        this.authorities.add("Contributor");
        this.email = email;
        this.region = region;
        this.institution = institution;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
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

    public void setRegion(String region) {
        this.region = region;
    }
}

