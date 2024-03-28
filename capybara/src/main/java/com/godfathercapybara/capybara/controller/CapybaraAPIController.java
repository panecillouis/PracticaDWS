package com.godfathercapybara.capybara.controller;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.*;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import com.godfathercapybara.capybara.model.Capybara;
import com.godfathercapybara.capybara.service.AnalyticsService;
import com.godfathercapybara.capybara.service.CapybaraService;
import com.godfathercapybara.capybara.service.ValidateService;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

@RequestMapping("/api/capybaras")
@RestController
public class CapybaraAPIController {
    @Autowired
    private CapybaraService capybaraService;
    @Autowired
    private ValidateService validateService;
    @Autowired
    private AnalyticsService analyticsService;

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
    public List<Capybara> getAllCapybaras(@RequestParam(required = false) Boolean isSponsored,
            @RequestParam(required = false) Double price, @RequestParam(required = false) String sex) {

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
    public ResponseEntity<?> createCapybara(@RequestBody Capybara capybara, MultipartFile imageField,
            MultipartFile analyticsField) throws IOException {
        String error = validateService.validateCapybara(capybara, imageField, analyticsField);
        if (error != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", error);
            response.put("capybara", capybara);
            return ResponseEntity.badRequest().body(response);
        } else {
            capybaraService.save(capybara, imageField, analyticsField);
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(capybara.getId()).toUri();
            return ResponseEntity.created(location).body(capybara);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCapybara(@PathVariable long id, @RequestBody Capybara newcapybara,
            MultipartFile imageField, MultipartFile analyticsField) throws IOException {
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
                capybaraService.updateCapybara(newcapybara, id, imageField, analyticsField);
                return ResponseEntity.ok(newcapybara);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @PostMapping("/{id}/analytics")
    public ResponseEntity<Object> uploadAnalytics(@PathVariable long id, @RequestParam MultipartFile analytics)
            throws IOException {

        Capybara capybara = capybaraService.findCapybaraById(id);
        URI location = fromCurrentRequest().build().toUri();
        String path = analyticsService.createAnalytics(analytics);
        capybara.setAnalytics(location.toString());
        capybaraService.updateCapybara(capybara, id, null, analytics);
        return ResponseEntity.created(location).build();

    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Object> uploadImage(@PathVariable long id, @RequestParam MultipartFile image)
            throws IOException {

        Capybara capybara = capybaraService.findCapybaraById(id);
        URI location = fromCurrentRequest().build().toUri();
        capybara.setImage(location.toString());
        capybara.setImageFile(BlobProxy.generateProxy(image.getInputStream(), image.getSize()));
        capybaraService.updateCapybara(capybara, id, image, null);
        return ResponseEntity.created(location).build();

    }

    @DeleteMapping("/{id}/analytics")
    public ResponseEntity<Object> deleteAnalytics(@PathVariable long id) throws IOException {
        Capybara capybara = capybaraService.findCapybaraById(id);

        analyticsService.deleteAnalytics(capybara.getAnalytics());
        capybara.setAnalytics("no-analytics.pdf");
        capybaraService.updateCapybara(capybara, id, null, null);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<Object> deleteImage(@PathVariable long id) throws IOException {
        Capybara capybara = capybaraService.findCapybaraById(id);

        capybara.setImage("no-image.png");
        capybara.setImageFile(null);
        capybaraService.updateCapybara(capybara, id, null, null);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/analytics")
    public ResponseEntity<Object> downloadPDF(@PathVariable long id) {

        Capybara capybara = capybaraService.findCapybaraById(id);
        Resource analytics = analyticsService.getAnalytics(capybara.getAnalytics());

        if (analytics == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PDF not found");
        }

        String mimeType = "application/pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + analytics.getFilename() + "\"")
                .body(analytics);
    }

    @SuppressWarnings("null")
    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> downloadImage(@PathVariable long id) {
        try {
            Capybara capybara = capybaraService.findCapybaraById(id);
            if (capybara.getImageFile() == null) {
                return ResponseEntity.notFound().build();
            }

            Resource file = new InputStreamResource(capybara.getImageFile().getBinaryStream());
            String mimeType = MimeTypeUtils.IMAGE_JPEG_VALUE;

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + capybara.getImage() + "\"")
                    .contentType(MediaType.parseMediaType(mimeType))
                    .contentLength(capybara.getImageFile().length())
                    .body(file);

        } catch (SQLException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't retrieve capybara image", ex);
        }
    }

}
