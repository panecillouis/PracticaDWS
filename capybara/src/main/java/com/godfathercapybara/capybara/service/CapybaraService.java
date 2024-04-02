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

import jakarta.persistence.EntityManager;

@Service
public class CapybaraService {

	
	@Autowired
	private EntityManager entityManager;

	@Autowired
	private CapybaraRepository capybaraRepository;

	@Autowired
	private AnalyticsService analyticsService;

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
	@SuppressWarnings("unchecked")
	public List<Capybara> findAll(Boolean isSponsored, Double price, String sex) {
		String query = "SELECT * FROM capybara";
		if( isSponsored!=null || price!=null || isNotEmptyField(sex)) {
			query+=" WHERE";
		}
		if(isSponsored!=null) {
			query+=" is_sponsored= "+isSponsored + " AND";
		}
		if(price!=null) {
			query+=" price <="+price + " AND";
		}
		
		if(isNotEmptyField(sex)) {
			query+=" sex='"+sex +"'"+ " AND";
		}
		if (query.endsWith("AND")) {
			query = query.substring(0, query.length() - 4);
		}
		if (!query.startsWith("SELECT")) {
			query = query.substring(5);
		}
		return (List<Capybara>) entityManager.createNativeQuery(query, Capybara.class).getResultList();
	}

	private boolean isNotEmptyField(String field) {
		return field != null && !field.isEmpty();
	}
	

	public Capybara save(Capybara capybara, MultipartFile imageField, MultipartFile analyticsField) throws IOException {

		if (imageField != null  && !imageField.isEmpty()) {
			capybara.setImage(imageField.getOriginalFilename());
			
			capybara.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
		}
		if(analyticsField != null && !analyticsField.isEmpty()) {
			capybara.setAnalytics(analyticsField.getOriginalFilename());
			String path =  analyticsService.createAnalytics(analyticsField);
		}

		else if(capybara.getImage() == null || capybara.getImage().isEmpty())
		{	capybara.setImage("no-image.png");
			capybara.setImageFile(null);
		}
		else if(capybara.getAnalytics() == null || capybara.getAnalytics().isEmpty())
		{	capybara.setAnalytics("no-analytics.pdf");
		}

		capybara.setId(nextId.getAndIncrement());
		capybaraRepository.save(capybara);
		return capybara;
	}

	public void delete(long id) {
		Capybara existingCapybara = this.findCapybaraById(id);
		String analyticsPdfName = existingCapybara.getAnalytics();
    
		if (analyticsPdfName != null && !analyticsPdfName.isEmpty()) {
			analyticsService.deleteAnalytics(analyticsPdfName);
		}
		capybaraRepository.deleteById(id);
	}

	public Capybara findCapybaraById(long id) {
		return capybaraRepository.findById(id).orElseThrow();
	}

	public void updateCapybara(Capybara capybara, long id, MultipartFile imageField, MultipartFile analyticsField) throws IOException {

		if (imageField != null && !imageField.isEmpty() ) {
			capybara.setImage(imageField.getOriginalFilename());
        	capybara.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
		}
		if(analyticsField != null && !analyticsField.isEmpty()) {
			capybara.setAnalytics(analyticsField.getOriginalFilename());
			String path =  analyticsService.createAnalytics(analyticsField);
		}
		else if (capybara.getImage() == null || capybara.getImage().isEmpty() ) {
			Capybara existingCapybara = capybaraRepository.findById(id).orElseThrow();
			capybara.setImageFile(existingCapybara.getImageFile());
			capybara.setImage(existingCapybara.getImage());
		
		}
		else if(capybara.getAnalytics() == null || capybara.getAnalytics().isEmpty())
		{	
			Capybara existingCapybara = capybaraRepository.findById(id).orElseThrow();
			capybara.setAnalytics(existingCapybara.getAnalytics());
				
		}
		capybaraRepository.save(capybara);

	}

	public void sponsorCapybara(long id, boolean isSponsored) {
		Capybara capybara = capybaraRepository.findById(id).orElse(null);
		capybara.setIsSponsored(isSponsored);
		capybaraRepository.save(capybara);
	}

}