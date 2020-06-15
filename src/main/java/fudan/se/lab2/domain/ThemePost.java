package fudan.se.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;

@Entity
public class ThemePost extends Post {
    @OneToMany
    private List<ReplyPost> replyPosts;
    @ManyToOne
    @JsonIgnore
    private Paper paper;

    public ThemePost(){}
    public ThemePost(String title, String content, String type, User user, Paper paper){
        super(title, content, type, user);
        this.paper = paper;
    }

    public List<ReplyPost> getReplyPosts() {
        return replyPosts;
    }

    public void setReplyPosts(List<ReplyPost> replyPosts) {
        this.replyPosts = replyPosts;
    }
    public void addToReplyPosts(ReplyPost post){
        if(replyPosts == null) replyPosts = new LinkedList<>();
        replyPosts.add(post);
    }

    public Paper getPaper() {
        return paper;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }
}
