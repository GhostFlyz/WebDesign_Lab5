package fudan.se.lab2.security.jwt;

import fudan.se.lab2.domain.User;
import fudan.se.lab2.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Write your code to make this filter works.
 *
 * @author LBW
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private JwtUserDetailsService jwtUserDetailsService;
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtRequestFilter(JwtUserDetailsService jwtUserDetailsService, JwtTokenUtil jwtTokenUtil){
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenUtil =  jwtTokenUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /*
         * 我瞎掰的实现方法：在header里面加一个jwt_token字段
         * 这个地方是验证有没有那个字段并且字段合法的
         */
        try{
            if(request.getHeader("jwt_token") != null){
                String token = request.getHeader("jwt_token");
                String username = jwtTokenUtil.getUsernameFromToken(token);
                User user = (User) jwtUserDetailsService.loadUserByUsername(username);
                if(jwtTokenUtil.validateToken(token, user)){
                    setSecurityContext(new WebAuthenticationDetailsSource().buildDetails(request), token);
                }
            }
        }catch (IllegalArgumentException e){
            logger.warn(e.getLocalizedMessage());
        }
        filterChain.doFilter(request, response);
    }

    private void setSecurityContext(WebAuthenticationDetails authDetails, String token) {
        final String username = jwtTokenUtil.getUsernameFromToken(token);
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        authentication.setDetails(authDetails);
        // After setting the Authentication in the context, we specify
        // that the current user is authenticated. So it passes the
        // Spring Security Configurations successfully.
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
