package space.work.training.izi.api


import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import space.work.training.izi.model.Drink
import space.work.training.izi.model.DrinkList

interface CocktailApi {

    @GET("search.php")
    suspend fun searchDrinkByName(@Query("s") name: String): DrinkList

    @GET("random.php")
    suspend fun getRandomCocktail(): Response<DrinkList>

    @GET("filter.php")
    suspend fun searchDrinkByIngredient(@Query("i") ingredient: String): Response<DrinkList>
}