package com.myretail.pricing.auth;

import com.myretail.pricing.dao.UserDao;
import com.myretail.pricing.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Sunith Soman
 */
@Service
public class PricingUserDetailsService implements UserDetailsService {

    private UserDao userDao;

    @Autowired
    public PricingUserDetailsService(UserDao userDao){
        super();
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO user = userDao.getUserByUserName(username);
        if(user==null){
            throw new UsernameNotFoundException("Invalid user");
        }
        return new PricingUserDetails(user);
    }
}
