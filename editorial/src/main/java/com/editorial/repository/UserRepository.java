package com.editorial.repository;

import com.editorial.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u from User u JOIN FETCH u.userDetails ud " +
            "JOIN FETCH u.authority ua WHERE u.username = :username")
    User findUserByName(@Param("username") String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u JOIN u.userDetails ud " +
            "WHERE ud.email = :email")
    boolean existsUsersByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u " +
            "WHERE u.username = :username")
    boolean existsUsersByUsername(@Param("username") String username);

    @Modifying
    @Query("DELETE FROM User u WHERE u.id =:id")
    void deleteUserById(Long id);

    @Query("SELECT u FROM User u JOIN FETCH u.userDetails ud " +
            "JOIN FETCH u.authority ua WHERE u.id = :id")
    User findUserById(@Param("id") Long id);
}