package fudan.se.lab2.controller;

import fudan.se.lab2.controller.request.PostRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.domain.Post;
import fudan.se.lab2.domain.ThemePost;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import fudan.se.lab2.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Transactional
public class PostController {
    private final JwtTokenUtil jwtTokenUtil;
    private final PostService postService;

    @Autowired
    public PostController(JwtTokenUtil jwtTokenUtil, PostService postService){
        this.jwtTokenUtil = jwtTokenUtil;
        this.postService = postService;
    }

    @RequestMapping("/createFirstDiscussPost")
    public ResponseEntity<ResponseObject<Post>> createFirstDiscussPost(@RequestBody PostRequest postRequest,
                                                           @RequestHeader String jwt_token){
        String username = getUsernameFromToken(jwt_token);
        postRequest.setType("AU");
        return ResponseEntity.ok(postService.createPost(postRequest, username));
    }

    @RequestMapping("/createSecondDiscussPost")
    public ResponseEntity<ResponseObject<Post>> createSecondDiscussPost(@RequestBody PostRequest postRequest,
                                                           @RequestHeader String jwt_token){
        String username = getUsernameFromToken(jwt_token);
        postRequest.setType("RE");
        return ResponseEntity.ok(postService.createPost(postRequest, username));
    }

    @RequestMapping("/getAllFirstDiscussPost")
    public ResponseEntity<ResponseObject<List<ThemePost>>> getAllFirstDiscussPost(@RequestParam Long paperId){
        return ResponseEntity.ok(postService.findPostsByPaperAndType(paperId, "AU"));
    }

    @RequestMapping("/getAllSecondDiscussPost")
    public ResponseEntity<ResponseObject<List<ThemePost>>> getAllSecondDiscussPost(@RequestParam Long paperId){
        return ResponseEntity.ok(postService.findPostsByPaperAndType(paperId, "RE"));
    }

    private String getUsernameFromToken(String jwt_token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = authentication != null && authentication.isAuthenticated()
                ? (User) authentication.getPrincipal() : null;
        return user != null ? user.getUsername() : jwtTokenUtil.getUsernameFromToken(jwt_token);
    }
}
