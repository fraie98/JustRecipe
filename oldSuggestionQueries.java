 // Vecchie query delle suggested recipes (ora sono state unite)

 /**
     * It gives the suggested recipes of the first level
     * @param username  Username of the user who is the receiver of the suggestions
     * @param howManyToSkip  how many recipe to skip
     * @param howManyToGet  how many recipe to get
     * @return  The list of suggested recipes of the first level
     */
    public List<Recipe> getFirstLevelSuggestedRecipe(final String username, final int howManyToSkip, final int howManyToGet)
    {
        List<Recipe> recipes = new ArrayList<>();
        try(Session session = driver.session()) {
            recipes = session.readTransaction((TransactionWork<List<Recipe>>)  tx -> {
                Result result = tx.run("MATCH path = (recipe:Recipe)<-[a:ADDS]-(owner:User)<-[:FOLLOWS*2..3]-(me:User{username:$u}) " +
                                "RETURN recipe.title, recipe.calories, recipe.carbs, recipe.protein, recipe.fat," +
                                " recipe.picture, a.when, owner.username " +
                                "ORDER BY length(path) ASC, a.when DESC " +
                                "SKIP $s " +
                                "LIMIT $l ",
                        parameters("u",username, "s", howManyToSkip, "l", howManyToGet));

                List<Recipe> r = new ArrayList<>();

                while(result.hasNext())
                {
                    Record rec = result.next();
                    String title = rec.get("recipe.title").asString();
                    int calories = 0;
                    int protein = 0;
                    int fat = 0;
                    int carbs = 0;
                    String picture = null;
                    String authorUsername = rec.get("owner.username").asString();
                    if(rec.get("recipe.calories") != NULL)
                        calories = rec.get("recipe.calories").asInt();
                    if(rec.get("recipe.fat") != NULL)
                        fat = rec.get("recipe.fat").asInt();
                    if(rec.get("recipe.protein") != NULL)
                        protein = rec.get("recipe.protein").asInt();
                    if(rec.get("recipe.carbs") != NULL)
                        carbs = rec.get("recipe.carbs").asInt();
                    if (rec.get("recipe.picture") != NULL)
                    {
                        picture = rec.get("recipe.picture").asString();
                    }
                    Recipe recipe = new Recipe(title, fat, calories, protein, carbs, picture);
                    recipe.setAuthorUsername(authorUsername);
                    r.add(recipe);
                }
                return r;
            });
        }
        return recipes;
    }


 /**
     * Function that returns the second level of recipes suggestion
     * @param username          Username of the user
     * @param threshold         Threshold on the number of likes
     * @param howManySkip       How many recipes to skip
     * @param howMany           How many recipes to obtain
     * @return                  List of recipes
     */
    public List<Recipe> getSecondLevelSuggestedRecipe (final String username, final int threshold,  final int howManySkip, final int howMany)
    {
        List<Recipe> recipes = new ArrayList<>();
        try(Session session = driver.session()) {
            session.readTransaction(tx -> {
                Result result = tx.run("MATCH (:User {username: $username})-[l:LIKES]->(:Recipe)<-[:ADDS]-(u:User) "+
                                // Count the likes for every distinct user, so the likes that the user has added at the recipe of this user
                                "WITH DISTINCT(u) AS u, COUNT(DISTINCT l) AS numLikes " +
                                "WHERE numLikes > $threshold " +
                                "MATCH (u)-[a:ADDS]->(r:Recipe) " +
                                "RETURN r.title as title, r.calories as calories, r.fat as fat, r.protein as protein, " +
                                "r.carbs AS carbs, r.picture as picture, u.username as authorUsername " +
                                "ORDER BY a.when DESC " +
                                "SKIP $skip LIMIT $limit",
                        parameters("username",username, "threshold", threshold, "skip", howManySkip, "limit", howMany));

                while(result.hasNext()){
                    Record r = result.next();
                    String title = r.get("title").asString();
                    int calories = 0;
                    int protein = 0;
                    int fat = 0;
                    int carbs = 0;
                    String picture = null;
                    String authorUsername = r.get("authorUsername").asString();
                    if(r.get("calories") != NULL)
                        calories = r.get("calories").asInt();
                    if(r.get("fat") != NULL)
                        fat = r.get("fat").asInt();
                    if(r.get("protein") != NULL)
                        protein = r.get("protein").asInt();
                    if(r.get("carbs") != NULL)
                        carbs = r.get("carbs").asInt();
                    if (r.get("picture") != NULL)
                    {
                        picture = r.get("picture").asString();
                    }
                    Recipe recipe = new Recipe(title, fat, calories, protein, carbs, picture);
                    recipe.setAuthorUsername(authorUsername);
                    recipes.add(recipe);
                }
                return null;
            });
        }
        return recipes;
    }