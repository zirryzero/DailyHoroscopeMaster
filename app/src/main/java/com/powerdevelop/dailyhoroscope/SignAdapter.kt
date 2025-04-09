package com.powerdevelop.dailyhoroscope

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.view.MotionEvent

class SignAdapter(private val context: Context, private val signs: List<Sign>) :
    RecyclerView.Adapter<SignAdapter.SignViewHolder>() {

    inner class SignViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.signImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sign, parent, false)
        return SignViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: SignViewHolder, position: Int) {
        val sign = signs[position]
        holder.image.setImageResource(sign.imageResId)

        val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        holder.itemView.startAnimation(animation)

        holder.itemView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> v.elevation = 12f
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> v.elevation = 4f
            }
            false
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, HoroscopeActivity::class.java)
            intent.putExtra("sign", sign.name.lowercase())
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = signs.size
}
