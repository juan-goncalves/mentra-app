package me.juangoncalves.mentra.ui.common

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.*
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.asImageAsset
import androidx.ui.layout.fillMaxSize
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import me.juangoncalves.mentra.R

@Composable
fun NetworkImage(
    url: String,
    modifier: Modifier = Modifier.fillMaxSize(),
    placeholder: @Composable() () -> Unit = {}
) {
    var image by state<ImageAsset?> { null }
    val context = ContextAmbient.current
    onCommit(url) {
        val glide = Glide.with(context)
        val target = object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {
                image = null
            }

            override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                image = bitmap.asImageAsset()
            }
        }
        glide
            .asBitmap()
            .load(url)
            .error(R.drawable.app_icon)
            .into(target)

        onDispose {
            image = null
            glide.clear(target)
        }
    }

    val safeImage = image
    if (safeImage != null) {
        Image(
            modifier = modifier,
            asset = safeImage,
            alignment = Alignment.Center
        )
    } else {
        placeholder()
    }
}