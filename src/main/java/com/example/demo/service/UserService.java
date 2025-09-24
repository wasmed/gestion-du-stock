package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user) {

            // Hacher le mot de passe avant de le sauvegarder
        user.setPassword(passwordEncoder.encode(user.getPassword()));
            // On définit un rôle par défaut pour les nouveaux utilisateurs
            // user.setRole(ROLE.CLIENT); // Par exemple, si tu as une classe ROLE

            return userRepository.save(user);
        }
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    public List<User> findAllUsers() {return userRepository.findAll();}
    public List<User> findAllClients() {
        return userRepository.findByRole(Role.CLIENT);
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

}



