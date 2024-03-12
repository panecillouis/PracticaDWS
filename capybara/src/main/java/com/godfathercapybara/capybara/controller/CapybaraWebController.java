package com.godfathercapybara.capybara.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.godfathercapybara.capybara.model.Capybara;
import com.godfathercapybara.capybara.service.CapybaraService;
import com.godfathercapybara.capybara.service.ImageService;
import com.godfathercapybara.capybara.service.ValidateService;

@Controller
public class CapybaraWebController {
	@Autowired
	private CapybaraService capybaraService;

	@Autowired
	private ImageService imageService;
	@Autowired
	private ValidateService validateService;

	@GetMapping("/")
	public String showHome() {
		return "index";
	}

	@GetMapping("/capybaras")
	public String showCapybaras(Model model) {

		model.addAttribute("capybaras", capybaraService.findAll());

		return "capybaras";
	}

	@GetMapping("/capybaras/{id}")
	public String showCapybara(Model model, @PathVariable long id) {

		Optional<Capybara> capybara = capybaraService.findById(id);
		if (capybara.isPresent()) {
			model.addAttribute("capybara", capybara.get());
			return "capybara";
		} else {
			return "redirect:/capybaras";
		}

	}

	@GetMapping("/capybaras/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {

		Optional<Capybara> op = capybaraService.findById(id);

		if (op.isPresent()) {
			Capybara capybara = op.get();
			Resource image = imageService.getImage(capybara.getImage());
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(image);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
		}
	}

	@GetMapping("/newcapybara")
	public String newcapybara(Model model) {

		return "newCapybaraPage";
	}

	@PostMapping("/newcapybara")
	public String newcapybaraProcess(Model model, Capybara capybara, MultipartFile imageField) throws IOException {

		if (validateService.validateCapybara(capybara, imageField) != null) {
			model.addAttribute("error", validateService.validateCapybara(capybara, imageField));
			model.addAttribute("capybara", capybara);
			return "newCapybaraPage";
		} else {
			Capybara newCapybara = capybaraService.save(capybara, imageField);

			model.addAttribute("capybaraId", newCapybara.getId());

			return "redirect:/capybaras/" + newCapybara.getId();
		}

	}

	@GetMapping("/capybaras/{id}/delete")
	public String deleteCapybara(Model model, @PathVariable long id) {
		Optional<Capybara> capybara = capybaraService.findById(id);

		if (capybara.isPresent()) {
			Capybara existingCapybara = capybara.get();

			// Delete the image
			imageService.deleteImage(existingCapybara.getImage());
			// Delete the capybara
			capybaraService.delete(id);
		}
		model.addAttribute("name", capybara.get().getName());

		return "removedcapybara";
	}

	@GetMapping("/capybaras/{id}/edit")
	public String showEditCapybaraForm(@PathVariable("id") long id, Model model) {

		Capybara capybara = capybaraService.findCapybaraById(id);
		model.addAttribute("capybara", capybara);
		return "editCapybaraPage";
	}

	@PostMapping("/capybaras/{id}/edit")
	public String processEditCapybaraForm(Model model, @PathVariable("id") long id,
			@ModelAttribute Capybara updatedCapybara, MultipartFile imageField) throws IOException {
		if (validateService.validateUpdatedCapybara(updatedCapybara) != null) {
			model.addAttribute("error", validateService.validateUpdatedCapybara(updatedCapybara));
			return "editCapybaraPage";
		}
		// Update the capybara with the new data
		capybaraService.updateCapybara(updatedCapybara, id, imageField);
		// Redirect to the capybara's page
		return "redirect:/capybaras/" + id;
	}

	@PostMapping("/capybaras/{id}/sponsor")
	public String sponsorCapybara(@PathVariable("id") long id, @RequestParam boolean isSponsored) {
		capybaraService.sponsorCapybara(id, isSponsored);
		return "redirect:/capybaras/" + id;
	}

}
