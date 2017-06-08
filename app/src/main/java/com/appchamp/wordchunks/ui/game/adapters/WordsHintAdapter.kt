package com.appchamp.wordchunks.ui.game.adapters

import android.graphics.drawable.GradientDrawable
import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.RelativeLayout
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.extensions.color
import com.appchamp.wordchunks.extensions.gone
import com.appchamp.wordchunks.extensions.visible
import com.appchamp.wordchunks.models.realm.Word
import com.appchamp.wordchunks.util.Constants
import io.realm.RealmList
import kotlinx.android.synthetic.main.item_word.view.*


class WordsHintAdapter(private val words: RealmList<Word>, @ColorInt private val packColor: Int,
                       private val wordClick: (Word) -> Unit) :
        RecyclerView.Adapter<WordsHintAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word_hint, parent, false)
        return ViewHolder(view, wordClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setAnimation(holder.itemView, position)

        holder.bind(words[position], packColor, position)
    }

    override fun getItemCount() = words.size

    class ViewHolder(view: View, val wordClick: (Word) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bind(word: Word, @ColorInt packColor: Int, pos: Int) = with(itemView) {
            val wordState = word.state
            val drawable = imgRectBg.drawable as GradientDrawable
            when (wordState) {
                Constants.WORD_STATE_NOT_SOLVED -> {
                    val wordLength = word.word.length
                    tvWord.text = resources.getString(R.string.number_of_letters, wordLength)
                    tvWordNum.text = (pos + 1).toString()
                    drawable.setColor(context.color(R.color.word_rect_bg))
                }
                else -> {
                    tvWord.text = word.word
                    tvWord.setTextColor(packColor)
                    icon.visible()
                    tvWordNum.gone()
                    drawable.setColor(packColor)
                    itemView.alpha = 0.4f
                }
            }

            // left column
            setItemLayout(RelativeLayout.ALIGN_PARENT_START, Gravity.END)

            itemView.setOnClickListener { wordClick(word) }
        }

        private fun setItemLayout(alignParent: Int, gravity: Int) {
            var params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT)

            params.addRule(alignParent, RelativeLayout.TRUE)

            itemView.icon.layoutParams = params
            itemView.imgRectBg.layoutParams = params
            itemView.tvWord.gravity = gravity or Gravity.CENTER_VERTICAL

            params = RelativeLayout.LayoutParams(
                    TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            42f,
                            itemView.resources.displayMetrics).toInt(),
                    RelativeLayout.LayoutParams.MATCH_PARENT)

            params.addRule(alignParent, RelativeLayout.TRUE)
            itemView.tvWordNum.layoutParams = params
        }
    }

    private var mLastPosition = -1

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > mLastPosition) {
            val anim = ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            // to make duration random number between [0,501)
            anim.duration = position * 100L + 100
            viewToAnimate.startAnimation(anim)
            mLastPosition = position
        }
    }
}