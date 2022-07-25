package space.work.training.izi.mvvm.cocktails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import space.work.training.izi.model.Drink
import javax.inject.Inject

@HiltViewModel
class CocktailViewModel @Inject constructor(private var cocktailRepository: CocktailRepository) :
    ViewModel() {

    private val cocktail = MutableLiveData<List<Drink>>()

    fun getCocktailByName(name: String) {
        viewModelScope.launch {
            cocktailRepository.getCocktailByName(name).collect {
                cocktail.postValue(it)
            }
        }
    }

}