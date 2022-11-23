package com.app.wildtreasure.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.wildtreasure.R


class RVAdapter() :
    RecyclerView.Adapter<RVAdapter.RVHolder>() {
    val itemList: MutableList<DTItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVHolder {
        val itemView =
            LayoutInflater.from(parent?.context).inflate(R.layout.list_item_view, parent, false)
        return RVHolder(itemView)
    }

    override fun onBindViewHolder(holder: RVHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = itemList.size

    inner class RVHolder(private val item: View) : RecyclerView.ViewHolder(item) {
        val imViewFirst = item.findViewById<ImageView>(R.id.imViewFirst)
        val imViewSecond = item.findViewById<ImageView>(R.id.imViewSecond)
        val imViewTherd = item.findViewById<ImageView>(R.id.imViewTherd)
        val imViewFour = item.findViewById<ImageView>(R.id.imViewFour)


        fun bind(item: DTItem) {
            imViewFirst.setImageResource(item.resIdfirst)
            imViewSecond.setImageResource(item.resIdSecond)
            imViewTherd.setImageResource(item.resIdTherd)
            imViewFour.setImageResource(item.resIdFour)

        }

    }
}