package com.example.demo.controller;

import com.example.demo.model.StatutTable;
import com.example.demo.model.TableRestaurant;
import com.example.demo.repository.TableRestaurantRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/tables")
@PreAuthorize("hasRole('ADMIN')")
public class TableController {

    private final TableRestaurantRepository tableRepository;

    public TableController(TableRestaurantRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @GetMapping
    public String listTables(Model model) {
        model.addAttribute("tables", tableRepository.findAll());
        return "admin/table-list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("table", new TableRestaurant());
        model.addAttribute("statuts", StatutTable.values());
        return "admin/table-form";
    }

    @PostMapping("/save")
    public String saveTable(@ModelAttribute TableRestaurant table, Model model, RedirectAttributes redirectAttributes) {
        try {
            tableRepository.save(table);
            redirectAttributes.addFlashAttribute("successMessage", "Table enregistrée avec succès !");
            return "redirect:/admin/tables";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("errorMessage", "Erreur : Une table avec ce numéro existe peut-être déjà ou les données sont invalides.");
            model.addAttribute("table", table);
            model.addAttribute("statuts", StatutTable.values());
            return "admin/table-form";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Une erreur inattendue est survenue : " + e.getMessage());
            model.addAttribute("table", table);
            model.addAttribute("statuts", StatutTable.values());
            return "admin/table-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        TableRestaurant table = tableRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid table Id:" + id));
        model.addAttribute("table", table);
        model.addAttribute("statuts", StatutTable.values());
        return "admin/table-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteTable(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tableRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Table supprimée avec succès !");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de supprimer cette table car elle est liée à des commandes existantes.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/admin/tables";
    }
}
