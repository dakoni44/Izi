package space.work.training.izi.api


import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import space.work.training.izi.model.Drink
import space.work.training.izi.model.DrinkList

interface CocktailApi {

    @GET("search.php")
    fun searchDrinkByName(@Query("s") name: String): List<Drink>

    @GET("random.php")
    fun getRandomCocktail(): Call<List<Drink>>

    @GET("filter.php")
    fun searchDrinkByIngredient(@Query("i") ingredient: String): Call<List<Drink>>
}