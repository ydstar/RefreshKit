package com.example.refreshkit.demo2

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper

import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adapter.kit.AdapterKit
import com.adapter.kit.DataItem
import com.example.refreshkit.R
import com.refresh.kit.MagicRecyclerView

import com.refresh.kit.core.IRefresh
import com.refresh.kit.core.RefreshKitLayout

import com.refresh.kit.view.OverView
import com.refresh.kit.view.TextOverView
import kotlinx.android.synthetic.main.activity_i_asb_list.*

/**
 * 通用基类列表
 */
abstract class AbsListActivity : AppCompatActivity(), IRefresh.IRefreshListener {

    private lateinit var mRefreshHeaderView: TextOverView
    private lateinit var mAdapter: AdapterKit
    protected var mRefreshLayout: RefreshKitLayout? = null
    protected var mRecyclerView: MagicRecyclerView? = null

    private var mProgressBar: ContentLoadingProgressBar? = null
    protected var pageIndex = 1

    companion object {
        const val PREFETCH_SIZE = 5
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_i_asb_list)

        this.mRefreshLayout = refresh_layout
        this.mRecyclerView = recycler_view
        this.mProgressBar = progress_bar

        //下拉刷新视图,设置给IRefreshLayout上
        mRefreshHeaderView = TextOverView(this)
        mRefreshLayout?.setRefreshOverView(mRefreshHeaderView)
        mRefreshLayout?.setRefreshListener(this)

        //设置recyclerview
        val layoutManager = createLayoutManager()
        mAdapter = AdapterKit(this)
        mRecyclerView?.layoutManager = layoutManager
        mRecyclerView?.adapter = mAdapter

        mProgressBar?.visibility = View.VISIBLE
        pageIndex = 1
    }

    open fun createLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

/////////////下拉刷新的监听///////////////////////////////////////////
    /**
     * 开启下拉刷新,默认是开启的
     */
    override fun enableRefresh(): Boolean {
        return true
    }

    @CallSuper
    override fun onRefresh() {
        if (mRecyclerView?.isLoading() == true) {
            //正处于分页
            mRefreshLayout?.post {
                mRefreshLayout?.refreshFinished()
            }
            return
        }
        pageIndex = 1
    }

/////////////加载更多///////////////////////////////////////////
    /**
     * 开启加载更多
     */
    open fun enableLoadMore(callback: () -> Unit) {
        //为了防止 同时 下拉刷新 和上拉分页的请求，这里就需要处理一把
        mRecyclerView?.enableLoadMore({
            if (mRefreshHeaderView.state == OverView.IRefreshState.STATE_REFRESH) {
                //正处于刷新状态
                mRecyclerView?.loadFinished(false)
                return@enableLoadMore
            }
            pageIndex++
            callback()
        }, PREFETCH_SIZE)
    }

    /**
     * 禁用加载更多
     */
    open fun disableLoadMore() {
        mRecyclerView?.disableLoadMore()
    }

/////////////完成下拉刷新或加载更多///////////////////////////////////////////
    /**
     * 完成下拉刷新或加载更多
     */
    open fun finishRefresh(dataItemList: List<DataItem<*, out RecyclerView.ViewHolder>>?) {
        val success = dataItemList != null && dataItemList.isNotEmpty()
        val refresh = pageIndex == 1
        if (refresh) {//下拉刷新
            mProgressBar?.visibility = View.GONE
            mRefreshLayout?.refreshFinished()
            if (success) {
                mAdapter.clearItems()
                mAdapter.addItems(dataItemList!!, true)
                //todo 隐藏空页面
            } else {
                //此时就需要判断列表上是否已经有数据，如果么有，显示出空页面转态
                if (mAdapter.itemCount <= 0) {
                    //todo 可以展示空页面
                }
            }
        } else {//加载更多
            if (success) {
                mAdapter.addItems(dataItemList!!, true)
            }
            mRecyclerView?.loadFinished(success)
        }
    }
}