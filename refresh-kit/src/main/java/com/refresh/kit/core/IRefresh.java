package com.refresh.kit.core;

import com.refresh.kit.view.OverView;

/**
 * Author: 信仰年轻
 * Date: 2020-09-21 15:36
 * Email: hydznsqk@163.com
 * Des: 下拉刷新顶层接口
 */
public interface IRefresh {

    /**
     * 刷新时是否禁止滚动
     *
     * @param disableRefreshScroll 否禁止滚动
     */
    void setDisableRefreshScroll(boolean disableRefreshScroll);

    /**
     * 刷新完成
     */
    void refreshFinished();


    /**
     * 设置下拉刷新的视图
     *
     * @param iOverView 下拉刷新的视图
     */
    void setRefreshOverView(OverView iOverView);


    /**
     * 设置下拉刷新的监听器
     *
     * @param iRefreshListener 刷新的监听器
     */
    void setRefreshListener(IRefreshListener iRefreshListener);

    interface IRefreshListener {

        void onRefresh();

        boolean enableRefresh();
    }
}
