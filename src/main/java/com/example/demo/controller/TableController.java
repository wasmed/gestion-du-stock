package com.example.demo.controller;

import com.example.demo.model.StatutTable;
import com.example.demo.model.TableRestaurant;
import com.example.demo.repository.TableRestaurantRepository;
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
    public String saveTable(@ModelAttribute TableRestaurant table, RedirectAttributes redirectAttributes) {
        tableRepository.save(table);
        redirectAttributes.addFlashAttribute("successMessage", "Table enregistrée avec succès !");
        return "redirect:/admin/tables";
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
        tableRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Table supprimée avec succès !");
        return "redirect:/admin/tables";
    }
}
