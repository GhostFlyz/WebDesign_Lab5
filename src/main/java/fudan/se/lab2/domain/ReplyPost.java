package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ReplyPost extends Post {
    @ManyToOne
    @JsonIgnore
    private ThemePost themePost;

    public ReplyPost(){}

    public ReplyPost(String title, String content, String type, User author, ThemePost themePost){
        super(title, content, type, author);
        this.themePost = themePost;
    }

    public ThemePost getThemePost() {
        return themePost;
    }

    public void setThemePost(ThemePost themePost) {
        this.themePost = themePost;
    }
}
