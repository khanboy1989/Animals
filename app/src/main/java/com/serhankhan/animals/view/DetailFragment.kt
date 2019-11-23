package com.serhankhan.animals.view


import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.serhankhan.animals.R
import com.serhankhan.animals.databinding.FragmentDetailBinding
import com.serhankhan.animals.models.Animal
import com.serhankhan.animals.models.AnimalPalette


class DetailFragment : Fragment() {

    var animal:Animal? = null
    private lateinit var dataBinding:FragmentDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_detail,container,false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAnimal()
    }

    private fun getAnimal(){
        arguments?.let {
            animal = DetailFragmentArgs.fromBundle(it).animal
        }

        animal?.imageUrl?.let{
            setupBackgroundColor(it)
        }
        dataBinding.animal = animal
    }


    private fun setupBackgroundColor(url:String){

        Glide.with(this).
            asBitmap()
            .load(url)
            .into(object:CustomTarget<Bitmap>(){
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource).generate(){palette ->
                        val intColor = palette?.lightMutedSwatch?.rgb ?:0
                        dataBinding.palette = AnimalPalette(intColor)
                    }
                }

            })
    }
}
