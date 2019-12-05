package service.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import service.client.entities.User;
import service.client.repositories.UserRepository;

/**
 * Created By: Prashant Chaubey
 * Created On: 26-10-2019 01:57
 * Purpose: Implementation of UserDetailsService for this project. Provide interface to load a user from the database
 * by username.
 **/
@Service
public class ApiUserDetailsService implements UserDetailsService {

    private UserRepository repository;

    @Autowired
    public ApiUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }
}