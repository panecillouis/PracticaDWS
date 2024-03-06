package com.godfathercapybara.capybara.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.godfathercapybara.capybara.model.Capybara; 
@Service
public class CapybaraService {
    
	@Autowired
	private ImageService imageService;

	private AtomicLong nextId = new AtomicLong(1L);
	private ConcurrentHashMap<Long, Capybara> capybaras = new ConcurrentHashMap<>();

	public Optional<Capybara> findById(long id) {
		if (this.capybaras.containsKey(id)) {
			return Optional.of(this.capybaras.get(id));
		}
		return Optional.empty();
	}

	public boolean exist(long id) {
		return this.capybaras.containsKey(id);
	}

	public List<Capybara> findAll() {
		return this.capybaras.values().stream().toList();
	}

	public Capybara save(Capybara capybara, MultipartFile imageField) {

		if (imageField != null && !imageField.isEmpty()){
			String path = imageService.createImage(imageField);
			capybara.setImage(path);
		}

		if(capybara.getImage() == null || capybara.getImage().isEmpty()) capybara.setImage("no-image.png");

		long id = nextId.getAndIncrement();
		capybara.setId(id);
		capybaras.put(id, capybara);
		return capybara;
	}
	
	public void delete(long id) {
		capybaras.remove(id);
	}
	public Capybara findCapybaraById(long id) {
		return capybaras.get(id);
	}
	public void updateCapybara(Capybara capybara, long id, MultipartFile imageField) {

		if (imageField != null && !imageField.isEmpty()){
			String path = imageService.createImage(imageField);
			capybara.setImage(path);
		}
		
		if(capybara.getImage() == null || capybara.getImage().isEmpty()) {
			Capybara existingCapybara = capybaras.get(id);
			if (existingCapybara != null) {
				capybara.setImage(existingCapybara.getImage());
			}
		}

		capybaras.put(id, capybara);
	}
	
}