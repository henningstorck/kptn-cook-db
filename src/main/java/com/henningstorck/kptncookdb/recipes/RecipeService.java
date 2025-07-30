package com.henningstorck.kptncookdb.recipes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class RecipeService {
	public static final String URL = "https://mobile.kptncook.com/recipes/%s/%d?kptnkey=%s";
	public static final String API_KEY = "6q7QNKy-oIgk-IMuWisJ-jfN7s6";
	public static final String LANGUAGE = "de";

	private final Logger logger = LoggerFactory.getLogger(RecipeService.class);
	private final RecipeRepository recipeRepository;

	public RecipeService(RecipeRepository recipeRepository) {
		this.recipeRepository = recipeRepository;
	}

	public List<Recipe> listRecipes() {
		return recipeRepository.findAll();
	}

	@EventListener(ApplicationReadyEvent.class)
	@Scheduled(cron = "0 0 12 * * ?")
	public void saveDailyRecipes() {
		try {
			logger.info("Saving daily recipes...");

			long unixTime = System.currentTimeMillis() / 1000;
			RestClient restClient = RestClient.create();

			String fullJson = restClient.get()
				.uri(URL.formatted(LANGUAGE, unixTime, API_KEY))
				.retrieve()
				.body(String.class);

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonRecipes = objectMapper.readTree(fullJson);

			for (int i = 0; i < jsonRecipes.size(); i++) {
				JsonNode jsonRecipe = jsonRecipes.get(i);
				saveRecipe(jsonRecipe);
			}

			logger.info("Total recipes: {}", recipeRepository.count());
		} catch (JsonProcessingException e) {
			logger.error("There was an error when saving daily recipes.", e);
		}
	}

	private void saveRecipe(JsonNode jsonRecipe) {
		String id = jsonRecipe.get("_id").get("$oid").textValue();
		String title = jsonRecipe.get("localizedTitle").get(LANGUAGE).textValue();
		String json = jsonRecipe.toString();

		if (recipeRepository.existsById(id)) {
			logger.info("Receipt '{}' was skipped, because it already exists.", title);
			return;
		}

		Recipe recipe = new Recipe();
		recipe.setId(id);
		recipe.setTitle(title);
		recipe.setJson(json);
		recipeRepository.save(recipe);
		logger.info("Receipt '{}' was saved.", title);
	}
}
