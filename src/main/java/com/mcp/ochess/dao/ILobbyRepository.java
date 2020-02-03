package com.mcp.ochess.dao;

import com.mcp.ochess.model.Lobby;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILobbyRepository extends CrudRepository<Lobby, Integer> {
    List<Lobby> findAllByNameContaining(@Param("name") String name, Pageable pageable);
}
