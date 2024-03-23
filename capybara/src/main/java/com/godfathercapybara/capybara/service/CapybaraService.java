package com.godfathercapybara.capybara.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.godfathercapybara.capybara.model.Capybara;
import com.godfathercapybara.capybara.repository.CapybaraRepository;

@Service
public class CapybaraService {

	
	@Autowired
	private CapybaraRepository capybaraRepository;

	private AtomicLong nextId = new AtomicLong(1L);

	public Optional<Capybara> findById(long id) {
		if (this.exist(id)) {
			return Optional.of(this.findCapybaraById(id));
		}
		return Optional.empty();
	}

	public boolean exist(long id) {
		return capybaraRepository.existsById(id);
	}

	public List<Capybara> findAll() {
		return capybaraRepository.findAll();
	}

	public Capybara save(Capybara capybara, MultipartFile imageField) throws IOException {

		if (imageField != null && !imageField.isEmpty()) {
			capybara.setImage(imageField.getOriginalFilename());
			capybara.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
		}

		else if(capybara.getImage() == null || capybara.getImage().isEmpty())
		{	capybara.setImage("no-image.png");
			capybara.setImageFile(null);
		}
		capybara.setId(nextId.getAndIncrement());
		capybaraRepository.save(capybara);
		return capybara;
	}

	public void delete(long id) {
		capybaraRepository.deleteById(id);
	}

	public Capybara findCapybaraById(long id) {
		return capybaraRepository.findById(id).orElseThrow();
	}

	public void updateCapybara(Capybara capybara, long id, MultipartFile imageField) throws IOException {

		if (imageField != null && !imageField.isEmpty()) {
			capybara.setImage(imageField.getOriginalFilename());
        	capybara.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
		}
		
		else if (capybara.getImage() == null || capybara.getImage().isEmpty()) {
			Capybara existingCapybara = capybaraRepository.findById(id).orElseThrow();
			capybara.setImageFile(existingCapybara.getImageFile());
			capybara.setImage(existingCapybara.getImage());
		}
		
		capybaraRepository.save(capybara);

	}

	public void sponsorCapybara(long id, boolean isSponsored) {
		Capybara capybara = capybaraRepository.findById(id).orElse(null);
		capybara.setIsSponsored(isSponsored);
		capybaraRepository.save(capybara);
	}

}