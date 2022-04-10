package com.bondarenko.codenames.repository;

import com.bondarenko.codenames.domain.entity.Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends CrudRepository<Room, Integer> {
    Optional<Room> findByIdAndOwnerId(Integer id, Integer ownerId);
}
