package com.mcp.ochess.dao;

import com.mcp.ochess.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserRepository extends CrudRepository<User, Integer> {
    List<User> findByName(String username);
}
