package com.godfathercapybara.capybara.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Optional;

import javax.print.attribute.standard.PrinterName;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.godfathercapybara.capybara.model.Capybara;
import com.godfathercapybara.capybara.model.User;
import com.godfathercapybara.capybara.service.AnalyticsService;
import com.godfathercapybara.capybara.service.CapybaraService;
import com.godfathercapybara.capybara.service.ValidateService;
import com.godfathercapybara.capybara.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CapybaraWebController {
	@Autowired
	private CapybaraService capybaraService;
	@Autowired
	private AnalyticsService analyticsService;
	@Autowired
	private ValidateService validateService;
	@Autowired
	private UserService userService;
	
	

	@GetMapping("/capybaras")
	public String showCapybaras(Model model, @RequestParam(required = false) Boolean isSponsored, @RequestParam(required = false) Double price, @RequestParam(required = false) String sex) {

		model.addAttribute("capybaras", capybaraService.findAll(isSponsored, price, sex));

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
	@GetMapping("/capybaras/{id}/analytics")
	public ResponseEntity<Object> downloadAnalytics(@PathVariable long id) throws SQLException {
		
		Capybara capybara = capybaraService.findCapybaraById(id);
		Resource analytics = analyticsService.getAnalytics(capybara.getAnalytics());
			if (capybara.getAnalytics() != null) {
			String mimeType = "application/pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + analytics.getFilename() + "\"")
                .body(analytics);
		} else {
			return ResponseEntity.notFound().build();
		}
		
	}
	@GetMapping("/capybaras/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {

		Capybara capybara = capybaraService.findCapybaraById(id);
		if (capybara.getImageFile() != null) {
			@SuppressWarnings("null")
			Resource file = new InputStreamResource(
					capybara.getImageFile().getBinaryStream());
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
					.contentLength(capybara.getImageFile().length())
					.body(file);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	@GetMapping("/newcapybara")
	public String newcapybara(Model model) {

		return "newCapybaraPage";
	}

	@PostMapping("/newcapybara")
	public String newcapybaraProcess(Model model, Capybara capybara, MultipartFile imageField, MultipartFile analyticsField) throws IOException {
		
		capybara.setDescription(Jsoup.clean(capybara.getDescription(), Safelist.relaxed()));

		if (validateService.validateCapybara(capybara, imageField, analyticsField) != null) {
			model.addAttribute("error", validateService.validateCapybara(capybara, imageField, analyticsField));
			model.addAttribute("capybara", capybara);
			return "newCapybaraPage";
		} else {
			Capybara newCapybara = capybaraService.save(capybara, imageField, analyticsField);

			model.addAttribute("capybaraId", newCapybara.getId());

			return "redirect:/capybaras/" + newCapybara.getId();
		}

	}

	@GetMapping("/capybaras/{id}/delete")
	public String deleteCapybara(Model model, @PathVariable long id) {
		Optional<Capybara> capybara = capybaraService.findById(id);

		if (capybara.isPresent()){
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
			@ModelAttribute Capybara updatedCapybara, MultipartFile imageField, MultipartFile analyticsField) throws IOException {
		if (validateService.validateUpdatedCapybara(updatedCapybara) != null) {
			model.addAttribute("error", validateService.validateUpdatedCapybara(updatedCapybara));
			return "editCapybaraPage";
		}
		// Update the capybara with the new data
		capybaraService.updateCapybara(updatedCapybara, id, imageField, analyticsField);
		// Redirect to the capybara's page
		return "redirect:/capybaras/" + id;
	}
	
	@SuppressWarnings("rawtypes")
    @GetMapping("/capybaras/{id}/sponsor")
	public ResponseEntity<Object> sponsorCapybara(@PathVariable("id") long id, @RequestParam boolean isSponsored, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		String userName = principal.getName();
		Optional<User> user = userService.findByUsername(userName);
		capybaraService.sponsorCapybara(id, isSponsored);
		if(user.isPresent() && isSponsored==true)
		{
			userService.addCapybara(user.get().getId(), id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.badRequest().build();
		
	}
	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();

		if(principal != null) {
		
			model.addAttribute("logged", true);		
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));
			
		} else {
			model.addAttribute("logged", false);
		}
	}
}
