package com.immotef.imageloading

import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso


/**
 *
 */



interface ImageLoader {
    fun loadImageWithRoundedCorners(url: Uri?, image: ImageView, radius: Int = 10, placeholder: Int? = null)
    fun loadImage(uriToImage: Uri?, imageView: ImageView)
    fun loadImageCenterInside(uriToImage: Uri?, imageView: ImageView)
    fun loadImage(urlToImage: String?, imageView: ImageView, loaded: (Boolean) -> Unit = {})

}

internal class ImageLoaderImp(private val picasso: Picasso) : ImageLoader {

    override fun loadImage(uriToImage: Uri?, imageView: ImageView) {
        picasso.load(uriToImage).fit().centerCrop().into(imageView)
    }

    override fun loadImageCenterInside(uriToImage: Uri?, imageView: ImageView) {
        picasso.load(uriToImage).fit().centerInside().into(imageView)
    }

    override fun loadImageWithRoundedCorners(url: Uri?, image: ImageView, radius: Int, placeholder: Int?) {
            picasso.load(url).fit()
                .transform(RoundedCornersTransformation(radius, 2))
                .centerCrop().into(image)
    }

    override fun loadImage(urlToImage: String?, imageView: ImageView, loaded: (Boolean) -> Unit) {
        picasso.load(urlToImage).fit().centerInside().into(imageView)
    }
}