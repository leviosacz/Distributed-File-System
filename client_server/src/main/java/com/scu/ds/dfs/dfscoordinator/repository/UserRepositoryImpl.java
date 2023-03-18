package com.scu.ds.dfs.dfscoordinator.repository;

import com.scu.ds.dfs.dfscoordinator.model.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UserRepositoryImpl implements UserRepository {

    private Map<String, User> userDB = new HashMap<>();
    private AtomicInteger idSequence = new AtomicInteger(0);

    @Override
    public User createUser(User user) {

        if (userDB.containsKey(user.getUsername())) {
            throw new RuntimeException("Username not available");
        }
        user.setId(String.valueOf(idSequence.incrementAndGet()));
        userDB.put(user.getUsername(), user);
        return user;
    }

    @Override
    public User getUser(String username) {
        if (!userDB.containsKey(username)) {
            throw new RuntimeException("User not found");
        }
        return userDB.get(username);
    }
}
