package com.godfathercapybara.capybara.controller;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.*;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.godfathercapybara.capybara.model.Capybara;
import com.godfathercapybara.capybara.service.CapybaraService;
import com.godfathercapybara.capybara.service.ValidateService;

@RequestMapping("/api/capybaras")
@RestController
public class CapybaraAPIController {
    @Autowired
    private CapybaraService capybaraService;
    @Autowired
    private ValidateService validateService;
    

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

    @GetMapping("")
    public List<Capybara> getAllCapybaras(@RequestParam(required = false) Boolean isSponsored, @RequestParam(required = false) Double price, @RequestParam(required = false) String sex) {

        return capybaraService.findAll(isSponsored, price, sex);
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
    public ResponseEntity<?> createCapybara(@RequestBody Capybara capybara, MultipartFile imageField) throws IOException {
        String error = validateService.validateCapybara(capybara, imageField);
        if (error != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", error);
            response.put("capybara", capybara);
            return ResponseEntity.badRequest().body(response);
        } else {
            capybaraService.save(capybara, imageField);
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(capybara.getId()).toUri();
            return ResponseEntity.created(location).body(capybara);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCapybara(@PathVariable long id, @RequestBody Capybara newcapybara,
            MultipartFile imageField) throws IOException {
        String error = validateService.validateUpdatedCapybara(newcapybara);
        if (error != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", error);
            response.put("capybara", newcapybara);
            return ResponseEntity.badRequest().body(response);
        } else {
            Optional<Capybara> capybaraOptional = capybaraService.findById(id);
            if (capybaraOptional.isPresent()) {
                newcapybara.setId(id);
                capybaraService.updateCapybara(newcapybara, id, imageField);
                return ResponseEntity.ok(newcapybara);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Object> uploadImage(@PathVariable long id, @RequestParam MultipartFile image)
            throws IOException {

        Capybara capybara = capybaraService.findCapybaraById(id);
        URI location = fromCurrentRequest().build().toUri();
        capybara.setImage(location.toString());
        capybara.setImageFile(BlobProxy.generateProxy(image.getInputStream(), image.getSize()));
        capybaraService.updateCapybara(capybara, id, image);
        return ResponseEntity.created(location).build();

    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<Object> deleteImage(@PathVariable long id) throws IOException {
        Capybara capybara = capybaraService.findCapybaraById(id);

        capybara.setImage("no-image.png");
        capybara.setImageFile(null);
        capybaraService.updateCapybara(capybara, id, null);
        return ResponseEntity.noContent().build();
    }
}
