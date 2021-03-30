package com.refresh.kit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.refresh.kit.util.DisplayUtil;


/**
 * Author: 信仰年轻
 * Date: 2020-09-21 15:34
 * Email: hydznsqk@163.com
 * Des:下拉刷新的Overlay视图,可以重载这个类来定义自己的Overlay
 */
public abstract class OverView extends FrameLayout {


    protected IRefreshState mState = IRefreshState.STATE_INIT;
    /**
     * 触发下拉刷新 需要的最小高度
     */
    public int mPullRefreshHeight;
    /**
     * 最小阻尼
     */
    public float minDamp = 1.6f;
    /**
     * 最大阻尼
     */
    public float maxDamp = 2.2f;

    public OverView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        preInit();
    }

    public OverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        preInit();
    }

    public OverView(Context context) {
        super(context);
        preInit();
    }

    protected void preInit() {
        mPullRefreshHeight = DisplayUtil.dp2px(66, getResources());
        init();
    }

    /**
     * 初始化
     */
    public abstract void init();

    public abstract void onScroll(int scrollY, int pullRefreshHeight);

    /**
     * 显示Overlay
     */
    public abstract void onVisible();

    /**
     * 超过Overlay，释放就会加载
     */
    public abstract void onOver();

    /**
     * 开始加载
     */
    public abstract void onRefresh();

    /**
     * 加载完成
     */
    public abstract void onFinish();

    /**
     * 设置状态
     *
     * @param state 状态
     */
    public void setState(IRefreshState state) {
        mState = state;
    }

    /**
     * 获取状态
     *
     * @return 状态
     */
    public IRefreshState getState() {
        return mState;
    }



    public enum IRefreshState {
        /**
         * 初始态
         */
        STATE_INIT,
        /**
         * Header展示的状态
         */
        STATE_VISIBLE,
        /**
         * 超出可刷新距离的状态
         */
        STATE_OVER,
        /**
         * 刷新中的状态
         */
        STATE_REFRESH,
        /**
         * 超出刷新位置松开手后的状态
         */
        STATE_OVER_RELEASE
    }
}
