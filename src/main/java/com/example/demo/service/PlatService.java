package com.example.demo.service;

import com.example.demo.model.Plat;
import com.example.demo.repository.PlatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatService {

    @Autowired
    private PlatRepository platRepository;

    public List<Plat> findAllPlats() {
        return platRepository.findAll();
    }

    public Plat savePlat(Plat plat) {
        return platRepository.save(plat);
    }

    public Plat findPlatById(Long id) {
        return platRepository.findById(id).orElse(null);
    }

    public void deletePlatById(Long id) {
        platRepository.deleteById(id);
    }
}
