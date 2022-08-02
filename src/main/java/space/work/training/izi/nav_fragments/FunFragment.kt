package space.work.training.izi.nav_fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
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
    private var cocktailAdapter2: CocktailAdapter? = null
    private var listView: ListView? = null
    private var adapter: ArrayAdapter<String>? = null
    private var builder: AlertDialog? = null
    private var ingredients = ArrayList<String>()

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

        binding.rvIngredient.setHasFixedSize(true)
        cocktailAdapter2 = CocktailAdapter(requireContext(), this)
        binding.rvIngredient.adapter = cocktailAdapter2

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cocktailViewModel.getIngredients().collect {
                    it?.drinks?.let {
                        fillList(it)
                    }
                }
            }
        }

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

        binding.listIng.setOnClickListener {
            openAlert()
        }

        binding.ivFind2.setOnClickListener {
            if (!binding.etFind2.text.toString().equals("Find by ingredient"))
                findCocktail2(binding.etFind2.text.toString())
        }

        binding.random.setOnClickListener {
            if (!binding.instructions.text.equals("")) {
                binding.random.visibility = View.INVISIBLE
                binding.random2.visibility = View.VISIBLE
                binding.instructions.visibility = View.VISIBLE
                binding.ingredients.visibility = View.VISIBLE
            }
        }

        binding.random2.setOnClickListener {
            if (!binding.instructions.text.equals("")) {
                binding.random.visibility = View.VISIBLE
                binding.random2.visibility = View.INVISIBLE
                binding.instructions.visibility = View.INVISIBLE
                binding.ingredients.visibility = View.INVISIBLE
            }
        }

    }

    private fun fillList(drinks: List<Drink>) {
        for (drink in drinks) {
            ingredients.add(drink.strIngredient1)
        }
        listView = ListView(requireContext())
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, ingredients)
        listView!!.adapter = adapter
        builder = AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setView(listView)
            .create()
    }


    private fun openAlert() {
        builder!!.show()
        listView!!.setOnItemClickListener { adapterView, view, i, l ->
            binding.etFind2.text = adapter!!.getItem(i)
            builder!!.dismiss()
        }
    }

    private fun findCocktail(s: String) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cocktailViewModel.getCocktailByName(s).collect {
                    it?.drinks?.let {
                        binding.rvName.visibility = View.VISIBLE
                        binding.cocktail1.visibility = View.GONE
                        cocktailAdapter!!.setData(it)
                    }
//                        binding.rvName.visibility = View.INVISIBLE
//                        binding.cocktail1.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun findCocktail2(s: String) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cocktailViewModel.getCocktailByIngredient(s).collect {
                    it?.drinks?.let {
                        binding.rvIngredient.visibility = View.VISIBLE
                        binding.cocktail2.visibility = View.GONE
                        cocktailAdapter2!!.setData(it)
                    }
//                        binding.rvName.visibility = View.INVISIBLE
//                        binding.cocktail1.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setUpRandom(drink: Drink) {
        binding.tvName.text = drink.strDrink
        binding.tvCategory.text = drink.strCategory
        binding.tvAlcohol.text = drink.strAlcoholic
        binding.instructions.text = drink.strInstructions
        var ings = "Ingredients: " +
                checkString(drink.strIngredient1) + checkString2(drink.strMeasure1) + checkString(
            drink.strIngredient2
        ) + checkString2(
            drink.strMeasure2
        ) +
                checkString(drink.strIngredient3) + checkString2(drink.strMeasure3) +
                checkString(drink.strIngredient4) + checkString2(drink.strMeasure4) + checkString(
            drink.strIngredient5
        ) + checkString2(drink.strMeasure5) +
                checkString(drink.strIngredient6) + checkString2(drink.strMeasure6) +
                checkString(drink.strIngredient7) + checkString2(drink.strMeasure7) + checkString(
            drink.strIngredient8
        ) + checkString2(drink.strMeasure8) +
                checkString(drink.strIngredient9) + checkString2(drink.strMeasure9) +
                checkString(drink.strIngredient10) + checkString2(drink.strMeasure10) + checkString(
            drink.strIngredient11
        ) + checkString2(drink.strMeasure11) +
                checkString(drink.strIngredient12) + checkString2(drink.strMeasure12) +
                checkString(drink.strIngredient13) + checkString2(drink.strMeasure13) + checkString(
            drink.strIngredient14
        ) + checkString2(drink.strMeasure14) +
                checkString(drink.strIngredient15) + checkString2(drink.strMeasure15)

        binding.ingredients.text = ings

        if (drink.strDrinkThumb == null) {
            Glide.with(requireContext()).load(R.drawable.cocktail_black)
                .into(binding.random)
            Glide.with(requireContext()).load(R.drawable.cocktail_black)
                .into(binding.random2)
        } else {
            Glide.with(requireContext()).load(drink.strDrinkThumb).into(binding.random)
            Glide.with(requireContext()).load(drink.strDrinkThumb).into(binding.random2)
        }
    }

    fun checkString(s: String?): String {
        return if (s.isNullOrEmpty()) "" else s
    }

    fun checkString2(s: String?): String {
        return if (s.isNullOrEmpty()) "" else " (" + s.trim() + ") "
    }

    override fun onItemClick(position: Int) {
    }


}