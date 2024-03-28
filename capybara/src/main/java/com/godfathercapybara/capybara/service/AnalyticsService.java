
package com.godfathercapybara.capybara.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AnalyticsService {

    private static final Path PDFS_FOLDER = Paths.get(System.getProperty("user.dir"), "analytics");

    public String createAnalytics(MultipartFile multiPartFile) {

        String originalName = multiPartFile.getOriginalFilename();
    
        if (!originalName.matches(".*\\.pdf")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The URL does not represent a PDF resource");
        }
    
        Path pdfPath = PDFS_FOLDER.resolve(originalName);
    
        try {
            multiPartFile.transferTo(pdfPath);
        } catch (Exception ex) {
            System.err.println(ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't save PDF locally", ex);
        }
    
        return originalName;  
    }
    

    public Resource getAnalytics(String pdfName) {
        Path pdfPath = PDFS_FOLDER.resolve(pdfName);
        try {
            return new UrlResource(pdfPath.toUri());
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't get local PDF");
        }
    }

    public void deleteAnalytics(String pdfName) {
        try {
            PDFS_FOLDER.resolve(pdfName).toFile().delete();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't delete local PDF");
        }
    }



}
