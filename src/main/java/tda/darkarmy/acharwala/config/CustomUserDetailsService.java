package tda.darkarmy.acharwala.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tda.darkarmy.acharwala.model.User;
import tda.darkarmy.acharwala.repository.UserRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService{

    private UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        System.out.println("Username: "+ userName);
        User user = userRepository.findByEmail(userName).orElseThrow();
        return new CustomUser(user);
    }
}
