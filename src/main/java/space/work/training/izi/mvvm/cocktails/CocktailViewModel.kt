package space.work.training.izi.mvvm.cocktails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import space.work.training.izi.model.Drink
import space.work.training.izi.model.DrinkList
import javax.inject.Inject

@HiltViewModel
class CocktailViewModel @Inject constructor(private var cocktailRepository: CocktailRepository) :
    ViewModel() {

    private val cocktails = MutableLiveData<DrinkList>()
    //1
    val cocktail = MutableLiveData<DrinkList>()

    fun getCocktailByName(name: String) {
        viewModelScope.launch {
            cocktailRepository.getCocktailByName(name).collect {
                cocktails.postValue(it)
            }
        }
    }

    //4
    init {
      //  cocktailRepository.getRandomCocktail4()
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

    fun getRandomCocktail3(): Flow<DrinkList?> = flow {
        emit(cocktailRepository.getRandomCocktail3())
    }

    //4
   /* fun getRandomCocktail4(): MutableLiveData<Drink> {
        return cocktailRepository.cocktail
    }*/


}