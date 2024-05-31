package com.example.lab7_20211755.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    int userId;

    @Column(name = "name")
    String name;

    @Column(name = "type")
    String type;

    @Column(name = "authorizedResource")
    int authorizedResource;

    @Column(name = "active")
    boolean active;

}
