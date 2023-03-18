package com.scu.ds.dfs.dfscoordinator.repository;

import com.scu.ds.dfs.dfscoordinator.model.User;

public interface UserRepository {

    User createUser(User user);

    User getUser(String username);
}
