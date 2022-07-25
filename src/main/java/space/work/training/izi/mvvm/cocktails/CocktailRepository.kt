package space.work.training.izi.mvvm.cocktails

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import space.work.training.izi.api.CocktailRetrofit
import space.work.training.izi.model.Drink

class CocktailRepository {

    fun getCocktailByName(name: String): Flow<List<Drink>> = flow {
        val response = CocktailRetrofit.api.searchDrinkByName(name)
        emit(response)
    }.flowOn(Dispatchers.IO)

}