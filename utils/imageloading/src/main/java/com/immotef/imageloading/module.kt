package com.immotef.imageloading

import com.immotef.imageloading.ImageLoader
import com.immotef.imageloading.ImageLoaderImp
import com.squareup.picasso.Picasso
import org.koin.dsl.module

/**
 *
 */


val imageModule = module{
    single<ImageLoader>{ ImageLoaderImp(Picasso.get()) }
}