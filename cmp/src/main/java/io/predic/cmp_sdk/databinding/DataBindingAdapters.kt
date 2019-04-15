package io.predic.cmp_sdk.databinding

import android.databinding.BindingAdapter
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

/**
 * Contains all DataBindingAdapters.
 */
internal class DataBindingAdapters {
    companion object {

        @BindingAdapter("android:src")
        @JvmStatic
        fun setImageUri(view: ImageView, imageUri: Uri) {
            view.setImageURI(imageUri)
        }

        @BindingAdapter("android:src")
        @JvmStatic
        fun setImageDrawable(view: ImageView, drawable: Drawable) {
            view.setImageDrawable(drawable)
        }

        @BindingAdapter("android:src")
        @JvmStatic
        fun setImageResource(imageView: ImageView, resource: Int) {
            imageView.setImageResource(resource)
        }

        @BindingAdapter("android:textSize")
        @JvmStatic
        fun setTextSize(view: TextView, textSize: Int) {
            view.textSize = textSize.toFloat()
        }

        @BindingAdapter("android:textColor")
        @JvmStatic
        fun setTextColor(view: TextView, textColor: String) {
            view.setTextColor(Color.parseColor(textColor))
        }

        @BindingAdapter("android:textColor")
        @JvmStatic
        fun setTextColor(view: TextView, textColor: Int) {
            view.setTextColor(textColor)
        }

        @BindingAdapter("android:textColor")
        @JvmStatic
        fun setTextColor(view: Button, textColor: String) {
            view.setTextColor(Color.parseColor(textColor))
        }

        @BindingAdapter("android:textColor")
        @JvmStatic
        fun setTextColor(view: Button, textColor: Int) {
            view.setTextColor(textColor)
        }

        @BindingAdapter("android:background")
        @JvmStatic
        fun setBackgroundColor(view: View, color: Int) {
            view.setBackgroundColor(color)
        }
    }
}