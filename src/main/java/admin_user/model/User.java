package admin_user.model;

import jakarta.persistence.*;

import lombok.Data;


@Entity
@Data

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String roles;

    private String passwordToken;

    private int otp;


    private boolean status;











}