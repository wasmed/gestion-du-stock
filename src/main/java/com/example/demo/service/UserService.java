package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user) {

            // Hacher le mot de passe avant de le sauvegarder
        user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
            // On définit un rôle par défaut pour les nouveaux utilisateurs
            // user.setRole(ROLE.CLIENT); // Par exemple, si tu as une classe ROLE

            return userRepository.save(user);
        }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}

    // Ajoute d'autres méthodes de service ici...

