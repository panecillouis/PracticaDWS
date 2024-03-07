package com.godfathercapybara.capybara.controller;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.godfathercapybara.capybara.model.Capybara;
import com.godfathercapybara.capybara.service.CapybaraService;

@RequestMapping("/api/capybaras")
@RestController
public class CapybaraAPIController {
    @Autowired
    private CapybaraService capybaraService;
    

    @DeleteMapping("/{id}")
    public ResponseEntity<Capybara> deleteCapybara(@PathVariable long id) {
        Optional<Capybara> capybaraOptional = capybaraService.findById(id);
        if (capybaraOptional.isPresent()) {
            Capybara capybara = capybaraOptional.get();
            capybaraService.delete(capybara.getId());
            return ResponseEntity.ok(capybara);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/")
    public List<Capybara> getAllCapybaras() {
       List<Capybara> capybaras = capybaraService.findAll();
         return capybaras;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Capybara> getCapybaraById(@PathVariable long id) {
        Optional<Capybara> capybara = capybaraService.findById(id);
        if (capybara.isPresent()) {
            return ResponseEntity.ok(capybara.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<Capybara> createCapybara(@RequestBody Capybara capybara, MultipartFile imageField) {
         capybaraService.save(capybara, imageField);
         URI location = fromCurrentRequest().path("/{id}").buildAndExpand(capybara.getId()).toUri();

         return ResponseEntity.created(location).body(capybara);
    }
   
    

    @PutMapping("/{id}")
    public ResponseEntity<Capybara> replaceCapybara(@PathVariable long id, @RequestBody Capybara newcapybara, MultipartFile imageField) {
       Capybara capybara = capybaraService.findCapybaraById(id);
        if (capybara != null) {
            newcapybara.setId(id);
            capybaraService.updateCapybara(newcapybara, id,imageField);
            return ResponseEntity.ok(capybara);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}