package space.work.training.izi.api


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import space.work.training.izi.model.DrinkList

interface CocktailApi {

    @GET("search.php")
    suspend fun searchDrinkByName(@Query("s") name: String): Response<DrinkList>

    @GET("lookup.php")
    suspend fun searchDrinkById(@Query("i") name: String): Response<DrinkList>

    @GET("random.php")
    suspend fun getRandomCocktail(): Response<DrinkList>

    @GET("filter.php")
    suspend fun searchDrinkByIngredient(@Query("i") ingredient: String): Response<DrinkList>

    @GET("list.php?i=list")
    suspend fun findIngredients(): Response<DrinkList>
}