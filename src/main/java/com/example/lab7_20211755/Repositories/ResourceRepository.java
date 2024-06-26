package com.example.lab7_20211755.Repositories;

import com.example.lab7_20211755.Entities.Resource;
import com.example.lab7_20211755.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Integer>  {
}
