package space.work.training.izi.nav_fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import space.work.training.izi.R
import space.work.training.izi.adapters.CocktailAdapter
import space.work.training.izi.databinding.FragmentFunBinding
import space.work.training.izi.model.Drink
import space.work.training.izi.mvvm.cocktails.CocktailViewModel
import java.util.*


@AndroidEntryPoint
class FunFragment : Fragment(), CocktailAdapter.OnItemClickListener {

    private lateinit var binding: FragmentFunBinding
    private val cocktailViewModel: CocktailViewModel by viewModels()
    private var cocktailAdapter: CocktailAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

        binding.rvName.setHasFixedSize(true)
        cocktailAdapter = CocktailAdapter(requireContext(), this)
        binding.rvName.adapter = cocktailAdapter

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
        binding.tvRandom.setOnClickListener {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    cocktailViewModel.getRandomCocktail3().collect {
                        setUpRandom(it!!.drinks.get(0))
                    }
                }
            }
        }

        binding.etFind.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                findCocktail(s.toString().trim().lowercase(Locale.getDefault()))
            }

            override fun afterTextChanged(s: Editable) {}
        })

    }

    private fun findCocktail(s:String){
        if(!s.equals("")){
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    cocktailViewModel.getCocktailByName(s).collect {
                        it?.drinks?.let {
                            binding.rvName.visibility=View.VISIBLE
                            binding.cocktail1.visibility = View.GONE
                            cocktailAdapter!!.setData(it)
                        }

                    }
                }
            }
        }else{
            binding.rvName.visibility=View.INVISIBLE
            binding.cocktail1.visibility = View.VISIBLE
        }

    }

    private fun setUpRandom(drink: Drink) {
        binding.tvName.text = drink.strDrink
        binding.tvCategory.text = drink.strCategory
        binding.tvAlcohol.text = drink.strAlcoholic
        if (drink.strDrinkThumb == null) {
            Glide.with(requireContext()).load(R.drawable.cocktail_black).into(binding.random)
        } else {
            Glide.with(requireContext()).load(drink.strDrinkThumb).into(binding.random)
        }

    }

    override fun onItemClick(position: Int) {
    }


}