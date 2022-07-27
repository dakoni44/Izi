package space.work.training.izi.nav_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import space.work.training.izi.R
import space.work.training.izi.databinding.FragmentFunBinding
import space.work.training.izi.mvvm.cocktails.CocktailViewModel

@AndroidEntryPoint
class FunFragment : Fragment() {

    private lateinit var binding: FragmentFunBinding
    private val cocktailViewModel: CocktailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_fun, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //1
        //cocktailViewModel.getRandomCocktail()

        //4
        /*cocktailViewModel.getRandomCocktail4().observe(viewLifecycleOwner) {
            binding.text.text = it.strDrink
        }*/

        //1
        /*   cocktailViewModel.cocktail.observe(viewLifecycleOwner) {
               binding.text.text = it.drinks.get(0).strDrink
           }*/

        //2
        /*  lifecycleScope.launch {
              repeatOnLifecycle(Lifecycle.State.STARTED) {
                  cocktailViewModel.getRandomCocktail2().collect {
                      binding.text.text = it!!.drinks.get(0).strDrink
                  }
              }
          }*/

        //3
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cocktailViewModel.getRandomCocktail3().collect {
                    binding.text.text = it!!.drinks.get(0).strDrink
                }
            }
        }
    }


}