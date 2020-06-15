package fudan.se.lab2.domain;

import javax.persistence.*;

@MappedSuperclass
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String content;
    private String type;

    @ManyToOne
    private User author;

    public Post(){}
    public Post(String title, String content, String type, User author){
        this.title = title;
        this.content = content;
        this.type = type;
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Long getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
