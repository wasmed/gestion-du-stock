package com.example.demo.model;

import jakarta.persistence.*;



import java.util.Set;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true)
    private String email;

    private String password;


    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "client")
    private Set<Commande> commandesClient;

    @OneToMany(mappedBy = "serveur")
    private Set<Commande> commandesServeur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<Commande> getCommandesClient() {
        return commandesClient;
    }

    public void setCommandesClient(Set<Commande> commandesClient) {
        this.commandesClient = commandesClient;
    }

    public Set<Commande> getCommandesServeur() {
        return commandesServeur;
    }

    public void setCommandesServeur(Set<Commande> commandesServeur) {
        this.commandesServeur = commandesServeur;
    }
}
