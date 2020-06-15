package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.PostRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.domain.*;
import fudan.se.lab2.repository.PaperRepository;
import fudan.se.lab2.repository.ReplyPostRepository;
import fudan.se.lab2.repository.ThemePostRepository;
import fudan.se.lab2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private UserRepository userRepository;
    private ReplyPostRepository replyPostRepository;
    private ThemePostRepository themePostRepository;
    private PaperRepository paperRepository;
    private static final String THEME = "T";
    private static final String REPLY = "R";

    @Autowired
    public PostService(UserRepository userRepository, ReplyPostRepository replyPostRepository,
                       ThemePostRepository themePostRepository, PaperRepository paperRepository){
        this.userRepository = userRepository;
        this.replyPostRepository = replyPostRepository;
        this.themePostRepository = themePostRepository;
        this.paperRepository = paperRepository;
    }

    public ResponseObject<Post> createPost(PostRequest request, String username){
        User user = userRepository.findByUsername(username);
        Post res;
        if(THEME.equals(request.getTorR())){
            Paper paper = paperRepository.findPaperById(request.getPaperId());
            if(paper == null){
                return new ResponseObject<>(400, "paper does not exist", null);
            }
            ThemePost post = new ThemePost(request.getTitle(), request.getContent(), request.getType(), user, paper);
            themePostRepository.save(post);
            res = post;
        }else if(REPLY.equals(request.getTorR())){
            ThemePost theme = themePostRepository.getThemePostsById(request.getThemeId());
            if(theme == null){
                return new ResponseObject<>(400, "theme post does not exist", null);
            }
            ReplyPost post = new ReplyPost(request.getTitle(), request.getContent(), request.getType(), user, theme);
            theme.addToReplyPosts(post);
            replyPostRepository.save(post);
            themePostRepository.save(theme);
            res = post;
        }else{
            return new ResponseObject<>(400,
                    "the post should be either theme(T) or reply(R), now " + request.getTorR(), null);
        }
        return new ResponseObject<>(200, "success", res);
    }

    public ResponseObject<List<ThemePost>> findPostsByPaperAndType(Long paperId, String type){
        Paper paper = paperRepository.findPaperById(paperId);
        if(paper == null){
            return new ResponseObject<>(400, "paper does not exist", null);
        }
        return new ResponseObject<>(200, "success",
                themePostRepository.findThemePostsByPaperAndType(paper, type));
    }
}
