package com.godfathercapybara.capybara.service;

import java.net.MalformedURLException;
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
    private static final Path ANALYTICS_FOLDER = Paths.get(System.getProperty("user.dir"), "analytics");

    public String createAnalytics(MultipartFile multiPartFile) {

        String originalName = multiPartFile.getOriginalFilename();

        if (!originalName.matches(".*\\.pdf")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The url is not an analytics resource");
        }

        String fileName = "analytics_" + UUID.randomUUID() + "_" + originalName;

        Path analyticsPath = ANALYTICS_FOLDER.resolve(fileName);
        try {
            multiPartFile.transferTo(analyticsPath);
        } catch (Exception ex) {
            System.err.println(ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't save analytics locally", ex);
        }

        return fileName;
    }

    public Resource getAnalytics(String analyticsName) {
        Path analyticsPath = ANALYTICS_FOLDER.resolve(analyticsName);
        try {
            return new UrlResource(analyticsPath.toUri());
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't get local analytics");
        }
    }

    public void deleteAnalytics(String analytics_url) {
        String[] tokens = analytics_url.split("/");
        String analytics_name = tokens[tokens.length - 1];

        try {
            ANALYTICS_FOLDER.resolve(analytics_name).toFile().delete();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't delete local analytics");
        }
    }
}
