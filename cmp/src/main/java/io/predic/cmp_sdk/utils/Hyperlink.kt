package io.predic.cmp_sdk.utils

import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import io.predic.cmp_sdk.R


internal class Hyperlink {
    companion object {
        /**
         * Use this method to set clickable highlighted a text in TextView
         *
         * @param tv              TextView or Edittext or Button or child of TextView class
         * @param textToHighlight Text to highlight
         */
        fun setClickableHighLightedText(
            tv: TextView,
            textToHighlight: String,
            color: Int = tv.context.resources.getColor(R.color.buttonColor),
            onClickListener: View.OnClickListener?
        ) {
            val tvt = tv.text.toString()
            var ofe = tvt.indexOf(textToHighlight, 0)
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    onClickListener?.onClick(textView)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = color
                    ds.isUnderlineText = false
                }
            }
            val wordToSpan = SpannableString(tv.text)
            var ofs = 0
            while (ofs < tvt.length && ofe != -1) {
                ofe = tvt.indexOf(textToHighlight, ofs)
                if (ofe == -1)
                    break
                else {
                    wordToSpan.setSpan(
                        clickableSpan,
                        ofe,
                        ofe + textToHighlight.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    tv.setText(wordToSpan, TextView.BufferType.SPANNABLE)
                    tv.movementMethod = LinkMovementMethod.getInstance()
                }
                ofs = ofe + 1
            }
        }
    }
}