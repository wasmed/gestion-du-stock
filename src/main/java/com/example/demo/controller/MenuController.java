package com.example.demo.controller;

import com.example.demo.model.Menu;
import com.example.demo.model.Plat;
import com.example.demo.service.MenuService;
import com.example.demo.service.PlatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/menus")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private PlatService platService;

    @GetMapping
    public String listMenus(Model model) {
        model.addAttribute("menus", menuService.findAllMenus());
        return "menu/list";
    }

    @GetMapping("/form")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_CUISINIER')")
    public String showMenuForm(@RequestParam(required = false) Long id, Model model) {
        Menu menu = (id != null) ? menuService.findMenuById(id) : new Menu();
        model.addAttribute("menu", menu);
        model.addAttribute("allPlats", platService.findAllPlats());
        return "menu/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_CUISINIER')")
    public String saveMenu(@ModelAttribute Menu menu) {
        menuService.saveMenu(menu);
        return "redirect:/menus";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteMenu(@PathVariable Long id) {
        menuService.deleteMenuById(id);
        return "redirect:/menus";
    }
}
