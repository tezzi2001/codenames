package com.bondarenko.codenames.repository;

import com.bondarenko.codenames.domain.model.common.TeamType;
import com.bondarenko.codenames.domain.entity.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends CrudRepository<Team, Integer> {
    Optional<Team> findByTeamTypeAndRoomId(TeamType teamType, Integer roomId);
    @Query(value = "SELECT * FROM Team LEFT JOIN Player ON Team.id = Player.team_id WHERE Team.room_id = :roomId", nativeQuery = true)
    List<Team> findAllByRoomId(Integer roomId);
}
