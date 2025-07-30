package com.henningstorck.kptncookdb.images;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;

@Service
public class ImageService {
	public static final String BASE_DIR = "./images/";

	private final Logger logger = LoggerFactory.getLogger(ImageService.class);

	public void saveImages(JsonNode jsonRecipe) {
		JsonNode jsonImages = jsonRecipe.get("imageList");

		for (int i = 0; i < jsonImages.size(); i++) {
			JsonNode jsonImage = jsonImages.get(i);
			saveImage(jsonImage);
		}
	}

	private void saveImage(JsonNode jsonImage) {
		URI uri = URI.create(jsonImage.get("url").textValue());
		String fileName = uri.toString().substring(uri.toString().lastIndexOf('/') + 1);
		File file = new File(BASE_DIR + fileName);

		try {
			FileUtils.copyURLToFile(uri.toURL(), file);
			logger.info("Image '{}' was saved.", fileName);
		} catch (IOException e) {
			logger.error("There was an error when saving image '{}'.", uri, e);
		}
	}
}
