package fudan.se.lab2.service;

import fudan.se.lab2.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author LBW
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    public JwtUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (userRepository.findByUsername(username) == null)
            throw new UsernameNotFoundException("User: '" + username + "' not found.");
        return userRepository.findByUsername(username);
    }
}
