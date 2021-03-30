package com.example.refreshkit.demo2

import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.adapter.kit.DataItem
import com.example.refreshkit.R


/**
 * Author: 信仰年轻
 * Date: 2021-01-04 16:11
 * Email: hydznsqk@163.com
 * Des:
 */
class MyDataItem(var data: String): DataItem<String, MyDataItem.ActivityHolder>(data) {


    override fun getItemLayoutRes(): Int {
        return R.layout.item_layout
    }

    override fun onBindData(holder: ActivityHolder, position: Int) {
        val textView = holder.itemView.findViewById<TextView>(R.id.tv_title)
        textView.text=data;

        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "position:$position", Toast.LENGTH_SHORT)
                .show()
        }
    }

    class ActivityHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    }

}