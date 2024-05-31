package com.example.lab7_20211755.Repositories;

import com.example.lab7_20211755.Entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>  {

    List<Object> findAllByTypeEquals(String type);

    @Query(nativeQuery = true, value = "UPDATE users SET type = ?2, authorizedResource = ?3 WHERE userId = ?1")
    @Modifying
    @Transactional
    void cambiarTipoPorId(int userId, String type, int resource);

    @Query(nativeQuery = true, value = "UPDATE users SET active = false")
    @Modifying
    @Transactional
    void apagar();

}
