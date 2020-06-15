package fudan.se.lab2.controller;

import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import fudan.se.lab2.service.AuthService;
import fudan.se.lab2.service.InvitationService;
import fudan.se.lab2.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBW
 */
@RestController
public class AuthController {

    private final AuthService authService;
    private final InvitationService invitationService;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    public AuthController(AuthService authService, InvitationService invitationService, JwtTokenUtil jwtTokenUtil
            , JwtUserDetailsService jwtUserDetailsService) {
        this.authService = authService;
        this.invitationService = invitationService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    @GetMapping("/register")
    public ResponseEntity<String> registerDoGet() {
        return ResponseEntity.ok().
                body("please use post method");
    }

    @GetMapping("/login")
    public ResponseEntity<String> loginDoGet() {
        return ResponseEntity.ok().
                body("please use post method");
    }

    @GetMapping("/checkUsername")
    public ResponseEntity<String> checkUsername(@RequestParam String username) {
        return ResponseEntity.ok().
                body(authService.checkUsername(username));
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {

        return ResponseEntity.ok().
                body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject<String>> login(@RequestBody LoginRequest request) {
        /* @RequestBody主要用来接收前端传递给后端的json字符串中的数据 */

        String token;
        try {
            token = jwtTokenUtil.generateToken((User) jwtUserDetailsService.loadUserByUsername(request.getUsername()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.ok(new ResponseObject<>(200, "user not exists", null));
        }
        return ResponseEntity.ok().
                body(new ResponseObject<>(200,
                        authService.login(request.getUsername(), request.getPassword()),
                        token));
    }

    @GetMapping("/findAuthorityOfUser")
    public ResponseEntity<ResponseObject<List<String>>> findAuthorityOfUser(@RequestHeader String jwt_token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = authentication != null && authentication.isAuthenticated()
                ? (User) authentication.getPrincipal() : null;
        String username = user != null ? user.getUsername() : jwtTokenUtil.getUsernameFromToken(jwt_token);
        try {
            return ResponseEntity.ok(authService.findAuthoritiesOfUser(username));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    @GetMapping("/findUsersByUsernameContaining")
    public ResponseEntity<ResponseObject<List<User>>> findUsersByUsernameContaining(@RequestParam String field, @RequestHeader String jwt_token) {
        String username = jwtTokenUtil.getUsernameFromToken(jwt_token);
        return ResponseEntity.ok(invitationService.findUsersByUsernameContaining(field, username));
    }

    @GetMapping("/findUserByFullName")
    public ResponseEntity<ResponseObject<List<User>>> findUserByFullName(@RequestParam String fullName, @RequestHeader String jwt_token){
        String username = jwtTokenUtil.getUsernameFromToken(jwt_token);
        return ResponseEntity.ok(invitationService.findUsersByFullname(fullName,username));
    }

    @RequestMapping("getCurrentUser")
    public ResponseEntity<ResponseObject<User>> getCurrentUser(@RequestHeader String jwt_token) {
        String username = getUsernameFromToken(jwt_token);
        try {
            return ResponseEntity.ok(authService.findUserByUsername(username));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseObject<>(500, e.getMessage(), null));
        }
    }

    private String getUsernameFromToken(String jwt_token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = authentication != null && authentication.isAuthenticated()
                ? (User) authentication.getPrincipal() : null;
        return user != null ? user.getUsername() : jwtTokenUtil.getUsernameFromToken(jwt_token);
    }

    /**
     * This is a function to test your connectivity. (健康测试时，可能会用到它）.
     */
    @GetMapping("/welcome")
    public ResponseEntity<Map<String, String>> welcome() {
        Map<String, String> response = new HashMap<>();
        String message = "Welcome to 2020 Software Engineering Lab2. ";
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

}



