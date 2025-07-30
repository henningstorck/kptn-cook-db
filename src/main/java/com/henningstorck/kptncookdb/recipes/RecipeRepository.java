package com.henningstorck.kptncookdb.recipes;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends ListCrudRepository<Recipe, String>, ListPagingAndSortingRepository<Recipe, String> {
}
