package com.scu.ds.dfs.dfscoordinator.service;

import com.scu.ds.dfs.dfscoordinator.model.User;
import org.springframework.stereotype.Component;


@Component
public interface UserService {

    User createProfile(User user);

    User getProfile(String username);
}
