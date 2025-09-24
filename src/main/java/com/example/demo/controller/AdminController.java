package com.example.demo.controller;

import com.example.demo.model.Feedback;
import com.example.demo.model.Role;
import com.example.demo.model.StockProduit;
import com.example.demo.model.User;
import com.example.demo.repository.CommandeRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FeedbackService;
import com.example.demo.service.StockService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired private CommandeRepository commandeRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired
    private StockService stockService;

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        model.addAttribute("totalClients", userRepository.countByRole(Role.CLIENT));
        model.addAttribute("commandesAujourdhui", commandeRepository.countByDateHeureAfter(LocalDate.now().atStartOfDay()));

        // Pour le revenu, il faudrait une requête SQL plus complexe. On met une valeur statique pour l'exemple.
        Double revenu = commandeRepository.sumTotalForToday(LocalDate.now().atStartOfDay()); // Méthode à créer
        model.addAttribute("revenuAujourdhui", revenu != null ? revenu : 0.0);

        return "admin/dashboard";
    }

    @GetMapping("/feedbacks")
    public String listFeedbacks(Model model) {
        List<Feedback> feedbacks = feedbackService.findAllFeedbacks();
        model.addAttribute("feedbacks", feedbacks);
        return "admin/feedback-list"; // Pointeur vers une nouvelle vue
    }

    /**
     * Affiche la liste de tous les utilisateurs (personnel et clients).
     */
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/user-list";
    }

    /**
     * Affiche le formulaire pour créer un nouvel utilisateur (personnel).
     */
    @GetMapping("/users/new")
    public String showNewUserForm(Model model) {
        model.addAttribute("user", new User());
        // On ne peut créer que du personnel, pas des clients
        model.addAttribute("roles", List.of(Role.SERVEUR, Role.CHEF_CUISINIER, Role.ADMIN));
        return "admin/user-form";
    }

    /**
     * Affiche le formulaire pour modifier un utilisateur existant.
     */
    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        model.addAttribute("roles", List.of(Role.SERVEUR, Role.CHEF_CUISINIER, Role.ADMIN));
        return "admin/user-form";
    }

    /**
     * Traite la création ou la mise à jour d'un utilisateur.
     */
    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        // Si l'ID n'est pas nul, c'est une mise à jour. Sinon, c'est une création.
        boolean isNewUser = user.getId() == null;

        // Pour un nouvel utilisateur, on encode son mot de passe.
        // Pour une mise à jour, on ne touche pas au mot de passe s'il n'est pas changé.
        if (isNewUser) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Utilisateur " + (isNewUser ? "créé" : "mis à jour") + " avec succès !");
        return "redirect:/admin/users";
    }

    /**
     * Supprime un utilisateur.
     */
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}
