package com.example.refreshkit.demo1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.refreshkit.R
import com.refresh.kit.core.IRefresh
import com.refresh.kit.core.RefreshKitLayout


/**
 * 下拉刷新
 */
class RefreshDemo1Activity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null

    var myDataset =
        arrayOf(
            "iRefresh",
            "iRefresh",
            "iRefresh",
            "iRefresh",
            "iRefresh",
            "iRefresh",
            "iRefresh"
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_i_refresh_demo)
        val refreshLayout = findViewById<RefreshKitLayout>(R.id.refresh_layout)

        val lottieOverView = LottieOverView(this)
        refreshLayout.setRefreshOverView(lottieOverView)

        refreshLayout.setRefreshListener(object : IRefresh.IRefreshListener {
            //下拉刷新的时候会调用下面这个方法,一般情况下都是下拉刷新之后去请求数据,然后刷新列表
            override fun onRefresh() {
                //1秒后完成下拉刷新
                Handler().postDelayed({ refreshLayout.refreshFinished() }, 1000)
            }

            override fun enableRefresh(): Boolean {
                return true
            }
        })
        // 刷新时是否禁止滚动,默认是false
        refreshLayout.setDisableRefreshScroll(true)
        initRecycleView()
    }


    private fun initRecycleView() {
        recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView

        recyclerView!!.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this)
        recyclerView!!.setLayoutManager(layoutManager)

        val mAdapter =
            MyAdapter(
                myDataset
            )
        recyclerView!!.setAdapter(mAdapter)
    }


    class MyAdapter (private val mDataset: Array<String>) :RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var textView: TextView
            init {
                textView = v.findViewById(R.id.tv_title)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
            return MyViewHolder(
                v
            )
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.textView.text = mDataset[position]
            holder.itemView.setOnClickListener {
                Toast.makeText(holder.itemView.context, "position:$position", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        override fun getItemCount(): Int {
            return mDataset.size
        }

    }
}