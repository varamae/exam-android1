package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.GoalModel

class GoalAdapters(var context: Context, var arrayList: ArrayList<GoalModel>) : RecyclerView.Adapter<GoalAdapters.ItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {

        val itemHolder = LayoutInflater.from(parent.context).inflate(R.layout.grid_layout_list_item, parent, false)
        return ItemHolder(itemHolder)

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {

        var nameGoal: GoalModel = arrayList.get(position)

        holder.icons.setImageResource(nameGoal.iconsGoal!!)
        holder.names.text = nameGoal.nameGoal

        holder.names.setOnClickListener {
            Toast.makeText(context, nameGoal.nameGoal, Toast.LENGTH_LONG).show()
        }

    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var icons = itemView.findViewById<ImageView>(R.id.goal_image)
        var names = itemView.findViewById<TextView>(R.id.goal_text)
    }
}