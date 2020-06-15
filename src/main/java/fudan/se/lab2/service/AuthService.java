package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.controller.response.ResponseObject;
import fudan.se.lab2.domain.Authority;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.AuthorityRepository;
import fudan.se.lab2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author LBW
 */
@Service
public class AuthService {
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private static final String EMAIL_REGEX = "^\\s*?(.+)@(.+?)\\s*$";
    public static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final String PW_REGEX = "^(?![\\d]+$)(?![a-zA-Z]+$)(?![^\\da-zA-Z]+$).{6,32}$";
    private static final Pattern PWD_PATTERN = Pattern.compile(PW_REGEX);
    private static final String SUCCESS = "success";

    @Autowired
    public AuthService(UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    public String checkUsername(String username) {
        if (userRepository.findByUsername(username) == null) return "valid";
        return "username exists";
    }

    public User register(RegisterRequest request) {
        String username = request.getUsername();
        /*
            因为这个接口只能返回User类型，所以报错通过将报错信息包装进User来实现。
            id = -1: 用户名已存在
            id = -2: 密码强度不够
            id = -3: 邮箱不合法
            id = -4: 用户名长度不合法
            id = -5: 密码含有用户名
            报错信息设置在了User的username属性中
            前端此处原始判断方法为hasOwnProperty(id)，不再适用，可以修改为根据返回的user对象的id来判断
        */
        Set<String> authorityNames = request.getAuthorities();
        if (authorityNames == null) {
            authorityNames = new HashSet<>();
            authorityNames.add("Contributor");
        }
        String fullName = request.getFullname();
        String password = request.getPassword();
        String region = request.getRegion();
        String email = request.getEmail();
        String institution = request.getInstitution();
        User invalid = validate(username, password, email);
        if (invalid != null) return invalid;
        Set<Authority> authorities = new HashSet<>();
        for (String authorityName : authorityNames) {
            Authority authority = authorityRepository.findByAuthority(authorityName);
            if (authority == null) {
                authority = new Authority(authorityName);
                authorityRepository.save(authority);
            }
            authorities.add(authority);
        }
        User user = new User(username, password, fullName, authorities, email, region, institution);
        for (Authority authority : authorities) authority.getUsers().add(user);
        userRepository.save(user);
        return user;
    }

    private User validate(String username, String password, String email) {
        User user = null;
        if (userRepository.findByUsername(username) != null) {
            user = createInvalidUser(-1, "username exists!");
        } else if (!PWD_PATTERN.matcher(password).matches()) {
            user = createInvalidUser(-2,
                    "password should have 2 of number, letter, and special symbol, and 6 <= password length <= 32");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            user = createInvalidUser(-3, "invalid email");
        } else if (username.length() < 5 || username.length() > 32) {
            user = createInvalidUser(-4, "the length of username should between 5 and 32");
        } else if (password.contains(username)) {
            user = createInvalidUser(-5, "password should not contains username");
        }
        return user;
    }

    private User createInvalidUser(long id, String errorMessage) {
        User user = new User();
        user.setId(id);
        user.setUsername(errorMessage);
        return user;
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || user.getFullname() == null || "".equals(user.getFullname())) {
            return "user does not exist";
        }
        if (user.getPassword().equals(password)) {
            return SUCCESS;
        }
        return "wrong password";
    }

    public ResponseObject<List<String>> findAuthoritiesOfUser(String username){
        User user = userRepository.findByUsername(username);
        List<String> res = new LinkedList<>();
        Set<Authority> authorities = (Set<Authority>) user.getAuthorities();
        for (Authority authority : authorities)
            res.add(authority.getAuthority());
        return new ResponseObject<>(200, SUCCESS, res);
    }

    public ResponseObject<User> findUserByUsername(String username){
        return new ResponseObject<>(200, SUCCESS, userRepository.findByUsername(username));
    }
}
