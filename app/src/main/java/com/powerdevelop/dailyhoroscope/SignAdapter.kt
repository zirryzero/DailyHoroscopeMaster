package com.powerdevelop.dailyhoroscope

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.powerdevelop.dailyhoroscope.databinding.ItemSignBinding

class SignAdapter(
    private val context: Context,
    private val signs: List<Sign>
) : RecyclerView.Adapter<SignAdapter.SignViewHolder>() {

    inner class SignViewHolder(val binding: ItemSignBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignViewHolder {
        val binding = ItemSignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SignViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SignViewHolder, position: Int) {
        val sign = signs[position]
        with(holder.binding) {
            signImage.setImageResource(sign.imageResId)
            signName.setText(sign.nameResId)
            signDate.setText(sign.dateRangeResId)

            val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
            root.startAnimation(animation)

            root.setOnClickListener {
                val intent = Intent(context, HoroscopeActivity::class.java).apply {
                    putExtra(HoroscopeActivity.EXTRA_SIGN_ID, sign.id)
                    putExtra(HoroscopeActivity.EXTRA_SIGN_NAME_RES, sign.nameResId)
                    putExtra(HoroscopeActivity.EXTRA_BACKGROUND_RES, sign.backgroundResId)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = signs.size
}
