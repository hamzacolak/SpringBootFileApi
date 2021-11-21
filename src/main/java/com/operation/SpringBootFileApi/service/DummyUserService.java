package com.operation.SpringBootFileApi.service;


import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class DummyUserService {

    private Map<String, User> users = new HashMap<>();

    @PostConstruct
    public void initialize() {
        users.put("hamza", new User("hamza", "hamza123",new ArrayList<>()));
    }

    public User getUserByUsername(String username) {
        return users.get(username);
    }
}