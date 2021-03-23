package com.example.refreshkit.demo2


import android.os.Bundle
import android.os.Handler

/**
 * 下拉刷新和加载更多
 */
class RefreshDemo2Activity : AbsListActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 刷新时是否禁止滚动,默认是false
        mRefreshLayout!!.setDisableRefreshScroll(true)
        //开启加载更多
        enableLoadMore { requestData() }
        //刚进来请求数据
        requestData()
    }

    //下拉刷新默认是开启的,直接重写该方法就好
    override fun onRefresh() {
        super.onRefresh()
        requestData()
    }

    fun requestData() {
        Handler().postDelayed({ getData() }, 1000)
    }

    fun getData(){
        val list = ArrayList<String>()
        list.add("iRefresh&LoadMore")
        list.add("iRefresh&LoadMore")
        list.add("iRefresh&LoadMore")
        list.add("iRefresh&LoadMore")
        list.add("iRefresh&LoadMore")
        list.add("iRefresh&LoadMore")
        list.add("iRefresh&LoadMore")
        list.add("iRefresh&LoadMore")
        requestDataSuccess(list)
    }

    /**
     * 成功拿到数据
     */
    private fun requestDataSuccess(list: ArrayList<String>) {
        val dataItems = mutableListOf<DataItem>()
        for (str in list) {
            val goodsItem = DataItem(str)
            dataItems.add(goodsItem)
        }
        //完成下拉刷新或加载更多
        finishRefresh(dataItems)
    }

}