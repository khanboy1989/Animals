package com.serhankhan.animals.util

import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.serhankhan.animals.R


fun getProgresDrawable(context: Context):CircularProgressDrawable{
    return  CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 50f
        start()
    }
}

fun ImageView.loadImage(uri:String?,progressDrawable: CircularProgressDrawable){

    val options = RequestOptions.placeholderOf(progressDrawable)
        .error(R.mipmap.ic_launcher_round)

    Glide.with(context).setDefaultRequestOptions(options)
        .load(uri)
        .into(this)
}


@BindingAdapter("android:imageUrl")
fun loadImage(view:ImageView,url:String?){
    view.loadImage(url, getProgresDrawable(view.context))
}

