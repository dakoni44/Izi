package space.work.training.izi.mvvm.cocktails

import androidx.lifecycle.MutableLiveData
import space.work.training.izi.api.CocktailRetrofit
import space.work.training.izi.model.Drink
import space.work.training.izi.model.DrinkList
import javax.inject.Inject

class CocktailRepository @Inject constructor() {

    //4
    val cocktail = MutableLiveData<Drink>()

    suspend fun getCocktailByName(name: String): DrinkList? {
        val response = CocktailRetrofit.api.searchDrinkByName(name)
        if(response.isSuccessful){
            return response.body()
        }else{
            return null
        }
    }

    suspend fun getCocktailByIngredient(ingredient: String): DrinkList? {
        val response = CocktailRetrofit.api.searchDrinkByIngredient(ingredient)
        if(response.isSuccessful){
            return response.body()
        }else{
            return null
        }
    }

    suspend fun getIngredients(): DrinkList? {
        val response = CocktailRetrofit.api.findIngredients()
        if(response.isSuccessful){
            return response.body()
        }else{
            return null
        }
    }


    //1
/*    fun getRandomCocktail(): Flow<DrinkList?> = flow {
        val response = CocktailRetrofit.api.getRandomCocktail()
        emit(response.body())
    }.flowOn(Dispatchers.IO)*/

    //2
    /*  fun getRandomCocktail2(): Flow<DrinkList?> = flow {
          val response = CocktailRetrofit.api.getRandomCocktail()
          emit(response.body())
      }.flowOn(Dispatchers.IO)*/

    //3
    suspend fun getRandomCocktail3(): DrinkList? {
        val response = CocktailRetrofit.api.getRandomCocktail()
        return response.body()
    }

    //4
    /* fun getRandomCocktail4() {
         CoroutineScope(Dispatchers.IO).launch {
             val response = CocktailRetrofit.api.getRandomCocktail()
             cocktail.postValue(response.body()!!.drinks.get(0))
         }
     }*/


}