package com.github.kr328.clash.design.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.databinding.AdapterProfileBinding
import com.github.kr328.clash.design.databinding.AdapterSubscriptionBinding
import com.github.kr328.clash.design.model.ProfilePageState
import com.github.kr328.clash.design.model.ProxyPageState
import com.github.kr328.clash.design.ui.ObservableCurrentTime
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.view.ActionLabel
import com.github.kr328.clash.design.view.LargeActionLabel
import com.github.kr328.clash.service.model.PaymentPlatform
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.service.model.Subscription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class PaymentPlatformAdapter(
    private val context: Context,
    private val onItemClick: (PaymentPlatform) -> Unit
) : RecyclerView.Adapter<PaymentPlatformAdapter.Holder>() {
    class Holder(val actionLabel: LargeActionLabel) : RecyclerView.ViewHolder(actionLabel)

    var platformList: List<PaymentPlatform> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LargeActionLabel(context)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = platformList[position]
        holder.actionLabel.text = current.name
        holder.actionLabel.icon = context.getDrawable(R.drawable.ic_image_placeholder)
        holder.actionLabel.setPadding(0, 40, 0, 40)
        holder.actionLabel.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        Glide.with(holder.itemView.context)
            .load(current.iconUrl)
            .placeholder(R.drawable.ic_image_placeholder)
            .into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    // 设置图片作为背景
                    holder.actionLabel.icon = resource
                }
            })
//        CoroutineScope(Dispatchers.Main).launch {
//            val drawable = loadDrawableFromUrl(current.iconUrl)
//            if (drawable != null) {
//                holder.actionLabel.icon = drawable
//            }
//        }
        holder.itemView.setOnClickListener {
            onItemClick(current)
        }
    }

    private suspend fun loadDrawableFromUrl(url: String): Drawable? = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.doInput = true
            connection.connect()

            val inputStream: InputStream = connection.inputStream
            return@withContext Drawable.createFromStream(inputStream, "src")
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    override fun getItemCount(): Int {
        return platformList.size
    }
}