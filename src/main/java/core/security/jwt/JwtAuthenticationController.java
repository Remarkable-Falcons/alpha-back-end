package core.security.jwt;

import core.entity.User;
import core.security.PortalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    private String login;


    public ResponseEntity<?> createAuthenticationToken(JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        if (userDetails == null) {
            //throw new Exception("INVALID_CREDENTIALS: Usuario ou senha incorretos.");
            return ResponseEntity.status(401).build();
        }
        final String token = jwtTokenUtil.generateToken(userDetails);
        setCurrentuser();
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @RequestMapping(value = "/auth/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody User user) throws Exception {
        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setUsername(user.getLogin());
        jwtRequest.setPassword(user.getPassword());
        ResponseEntity<?> auth = createAuthenticationToken(jwtRequest);
        return auth;
        //return createAuthenticationToken(jwtRequest);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        }
        catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        }
        catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    private void setCurrentuser()
    {
        User currentUser = userDetailsService.findByLogin(this.login);
        PortalUtil.setCurrentUser(currentUser);
    }
}
