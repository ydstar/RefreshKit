package com.refresh.kit

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.adapter.kit.AdapterKit
import com.refresh.kit.util.DisplayUtil

/**
 * 风骚实现分页预加载
 */
open class MagicRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {


    private var mLoadMoreScrollListener: OnScrollListener? = null
    private var mFooterView: View? = null
    private var mIsLoadingMore: Boolean = false

    inner class LoadMoreScrollListener(val prefetchSize: Int, val callback: () -> Unit) :
        OnScrollListener() {

        //这里的强转，因为前面 会有前置检查
        val iAdapter = adapter as AdapterKit

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            //需要根据当前的滑动状态  已决定要不要添加footer view ，要不执行上拉加载分页的动作
            if (mIsLoadingMore) {
                return
            }
            //咱们需要判断当前类表上 已经显示的 item的个数 ，如果列表上已显示的item的数量小于0
            val totalItemCount = iAdapter.itemCount
            if (totalItemCount <= 0)
                return

            //此时，需要在滑动状态为 拖动状态时，就要判断要不要添加footer
            //目的就是为了防止列表滑动到底部了但是 footerview 还没显示出来，
            //1. 依旧需要判断列表是否能够滑动,那么问题来了，如何判断RecyclerView ，是否可以继续向下滑动
            val canScrollVertical = recyclerView.canScrollVertically(1)

            //还有一种情况,canScrollVertical 咱们是检查他能不能欧股继续向下滑动，
            //特殊情况，列表已经滑动到底部了，但是分页失败了。
            val lastVisibleItem = findLastVisibleItem(recyclerView)
            val firstVisibleItem = findFirstVisibleItem(recyclerView)
            if (lastVisibleItem <= 0)
                return
            //列表不可滑动,但列表没有撑满屏幕,此时lastVisibleItem就等于最后一条item,为了避免这种能情况，还需要加firstVisibleItem!=0
            val arriveBottom =
                lastVisibleItem >= totalItemCount - 1 && firstVisibleItem > 0
            //可以向下滑动，或者当前已经滑动到最底下了，此时在拖动列表，那也是允许分页的
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING && (canScrollVertical || arriveBottom)) {
                addFooterView()
            }

            //不能在 滑动停止了，才去添加footer view
            if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                return
            }

            //预加载,就是 不需要等待 滑动到最后一个item的时候，就出发下一页的加载动作
            val arrivePrefetchPosition = totalItemCount - lastVisibleItem <= prefetchSize
            if (!arrivePrefetchPosition) {
                return
            }
            mIsLoadingMore = true
            callback()
        }

        private fun addFooterView() {
            val footerView = getFooterView()
            //主要是为了避免 removeFooterView 不及时，在边界场景下可能会出现，footerView还没从recyclervIEW上移除掉，但我们又调用了addFooterView，
            //造成的重复添加的情况，此时会抛出 add view must call removeview form it parent first exception
            if (footerView.parent != null) {
                footerView.post {
                    addFooterView()
                }
            } else {
                iAdapter.addFooterView(footerView)
            }
        }

        private fun getFooterView(): View {
            if (mFooterView == null) {
                mFooterView = LayoutInflater.from(context)
                    .inflate(R.layout.layout_footer_loading, this@MagicRecyclerView, false)

            }
            resizeFootView()
            return mFooterView!!
        }

        private fun findLastVisibleItem(recyclerView: RecyclerView): Int {
            when (val layoutManager = recyclerView.layoutManager) {
                //layoutManager is GridLayoutManager
                is LinearLayoutManager -> {
                    return layoutManager.findLastVisibleItemPosition()
                }
                is StaggeredGridLayoutManager -> {
                    return layoutManager.findLastVisibleItemPositions(null)[0]
                }
            }
            return -1
        }

        private fun findFirstVisibleItem(recyclerView: RecyclerView): Int {
            when (val layoutManager = recyclerView.layoutManager) {
                //layoutManager is GridLayoutManager
                is LinearLayoutManager -> {
                    return layoutManager.findFirstVisibleItemPosition()
                }
                is StaggeredGridLayoutManager -> {
                    return layoutManager.findFirstVisibleItemPositions(null)[0]
                }
            }
            return -1
        }
    }

    /**
     * 开启加载更多
     */
    fun enableLoadMore(callback: () -> Unit, prefetchSize: Int) {
        if (adapter !is AdapterKit) {
            throw RuntimeException("enableLoadMore must use AdapterKit")
        }
        mLoadMoreScrollListener = LoadMoreScrollListener(prefetchSize, callback)
        addOnScrollListener(mLoadMoreScrollListener!!)
    }

    /**
     * 禁用加载更多
     */
    fun disableLoadMore() {
        if (adapter !is AdapterKit) {
            throw RuntimeException("disableLoadMore must use AdapterKit")
        }
        val iAdapter = adapter as AdapterKit
        mFooterView?.let {
            if (mFooterView!!.parent != null) {
                iAdapter.removeFooterView(mFooterView!!)
            }
        }
        mLoadMoreScrollListener?.let {
            removeOnScrollListener(mLoadMoreScrollListener!!)
            mLoadMoreScrollListener = null
            mFooterView = null
            mIsLoadingMore = false
        }
    }

    /**
     * 是否正在加载更多
     */
    fun isLoading(): Boolean {
        return mIsLoadingMore
    }

    /**
     * 加载更多成功
     */
    fun loadFinished(success: Boolean) {
        if (adapter !is AdapterKit) {
            throw RuntimeException("loadFinished must use AdapterKit")
        }
        mIsLoadingMore = false
        val iAdapter = adapter as AdapterKit
        if (!success) {
            mFooterView?.let {
                if (mFooterView!!.parent != null) {
                    iAdapter.removeFooterView(mFooterView!!)
                }
            }
        }
    }

    /**
     * 适配折叠屏,重新计算footView的宽和高
     */
    private fun resizeFootView() {
        val width: Int = DisplayUtil.getDisplayWidthInPx(context)
        val layoutParams = mFooterView?.layoutParams
        layoutParams?.width = width
        mFooterView?.layoutParams = layoutParams
    }
}