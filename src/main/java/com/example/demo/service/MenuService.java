package com.example.demo.service;

import com.example.demo.model.Menu;
import com.example.demo.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    public List<Menu> findAllMenus() {
        return menuRepository.findAll();
    }

    public List<Menu> findAllActiveMenus() {
        return menuRepository.findActiveMenus(LocalDate.now());
    }

    public Menu findMenuById(Long id) {
        return menuRepository.findById(id).orElse(null);
    }

    public Menu saveMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    public void deleteMenuById(Long id) {
        menuRepository.deleteById(id);
    }
}
