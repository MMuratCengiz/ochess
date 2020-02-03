package com.mcp.ochess.dao;

import com.mcp.ochess.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPlayerRepository extends CrudRepository<Player, Integer> {
}
