package com.serhankhan.animals.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.serhankhan.animals.R
import com.serhankhan.animals.databinding.ItemAnimalBinding
import com.serhankhan.animals.models.Animal
import com.serhankhan.animals.util.getProgresDrawable
import com.serhankhan.animals.util.loadImage
import kotlinx.android.synthetic.main.item_animal.view.*

class AnimalListAdapter(private val animalList: ArrayList<Animal>) :
    RecyclerView.Adapter<AnimalListAdapter.AnimalViewHolder>() {


    fun updateAnimalList(newAnimalList: List<Animal>) {
        animalList.clear()
        animalList.addAll(newAnimalList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemAnimalBinding>(
            inflater,
            R.layout.item_animal,
            parent,
            false
        )
        return AnimalViewHolder(view)

    }

    override fun getItemCount(): Int = animalList.size

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        holder.bindData(animalList[position])
    }


    class AnimalViewHolder(var view: ItemAnimalBinding) : RecyclerView.ViewHolder(view.root),
        AnimalClickListener {

        private var animalItem: Animal? = null

        fun bindData(item: Animal) {
            view.animal = item
            view.listener = this
            animalItem = item
        }

        override fun onClick(view: View) {
            animalItem?.let { animal ->
                    val action = ListFragmentDirections.actionDetail(animal)
                    Navigation.findNavController(view).navigate(action)
            }

        }

    }

}