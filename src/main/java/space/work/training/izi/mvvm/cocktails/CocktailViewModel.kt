package space.work.training.izi.mvvm.cocktails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import space.work.training.izi.model.DrinkList
import javax.inject.Inject

@HiltViewModel
class CocktailViewModel @Inject constructor(private var cocktailRepository: CocktailRepository) :
    ViewModel() {

    //1
    val cocktail = MutableLiveData<DrinkList>()

    fun getCocktailByName(name: String): Flow<DrinkList?> = flow {
        delay(1000)
        emit(cocktailRepository.getCocktailByName(name))
    }

    //3
    fun getRandomCocktail3(): Flow<DrinkList?> = flow {
        emit(cocktailRepository.getRandomCocktail3())
    }

    //1
    /*  fun getRandomCocktail() {
          viewModelScope.launch {
              cocktailRepository.getRandomCocktail().collect {
                  cocktail.postValue(it)
              }
          }
      }*/

    //2
    /*fun getRandomCocktail2(): Flow<DrinkList?> {
        return cocktailRepository.getRandomCocktail2()
    }*/

    //4
    init {
        //  cocktailRepository.getRandomCocktail4()
    }

    //4
    /* fun getRandomCocktail4(): MutableLiveData<Drink> {
         return cocktailRepository.cocktail
     }*/
}