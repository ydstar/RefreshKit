package com.refresh.kit.core;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.refresh.kit.util.MyGestureDetector;
import com.refresh.kit.util.ScrollUtil;
import com.refresh.kit.view.OverView;


/**
 * Author: 信仰年轻
 * Date: 2020-09-21 15:43
 * Email: hydznsqk@163.com
 * Des: 下拉刷新View
 * 本身是个FrameLayout,然后里面可以包裹RecyclerView或者ScrollView
 */
public class RefreshKitLayout extends FrameLayout implements IRefresh {

    private static final String TAG = RefreshKitLayout.class.getSimpleName();
    private OverView.IRefreshState mState;
    private android.view.GestureDetector mGestureDetector;
    private AutoScroller mAutoScroller;
    private IRefresh.IRefreshListener mRefreshListener;
    protected OverView mOverView;
    private int mLastY;
    //刷新时是否禁止滚动
    private boolean disableRefreshScroll;

    public RefreshKitLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshKitLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RefreshKitLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        mGestureDetector = new android.view.GestureDetector(getContext(), mIGestureDetector);
        mAutoScroller = new AutoScroller();
    }

    @Override
    public void setDisableRefreshScroll(boolean disableRefreshScroll) {
        this.disableRefreshScroll = disableRefreshScroll;
    }

    @Override
    public void refreshFinished() {
        final View head = getChildAt(0);
        mOverView.onFinish();
        mOverView.setState(OverView.IRefreshState.STATE_INIT);
        final int bottom = head.getBottom();
        if (bottom > 0) {
            //下over pull 200，height 100
            //  bottom  =100 ,height 100
            recover(bottom);
        }
        mState = OverView.IRefreshState.STATE_INIT;

    }

    @Override
    public void setRefreshListener(IRefresh.IRefreshListener refreshListener) {
        mRefreshListener = refreshListener;
    }

    /**
     * 设置下拉刷新的视图
     *
     * @param iOverView
     */
    @Override
    public void setRefreshOverView(OverView iOverView) {
        if (this.mOverView != null) {
            removeView(mOverView);
        }
        this.mOverView = iOverView;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mOverView, 0, params);
    }

    MyGestureDetector mIGestureDetector = new MyGestureDetector() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float disX, float disY) {
            if (Math.abs(disX) > Math.abs(disY) || mRefreshListener != null && !mRefreshListener.enableRefresh()) {
                //横向滑动，或刷新被禁止则不处理
                return false;
            }
            if (disableRefreshScroll && mState == OverView.IRefreshState.STATE_REFRESH) {//刷新时是否禁止滑动
                return true;
            }

            View head = getChildAt(0);
            View child = ScrollUtil.findScrollableChild(RefreshKitLayout.this);
            if (ScrollUtil.childScrolled(child)) {
                //如果列表发生了滚动则不处理
                return false;
            }
            //没有刷新或没有达到可以刷新的距离，且头部已经划出或下拉
            if ((mState != OverView.IRefreshState.STATE_REFRESH || head.getBottom() <= mOverView.mPullRefreshHeight) && (head.getBottom() > 0 || disY <= 0.0F)) {
                //还在滑动中
                if (mState != OverView.IRefreshState.STATE_OVER_RELEASE) {
                    int speed;
                    //阻尼计算
                    if (child.getTop() < mOverView.mPullRefreshHeight) {
                        speed = (int) (mLastY / mOverView.minDamp);
                    } else {
                        speed = (int) (mLastY / mOverView.maxDamp);
                    }
                    //如果是正在刷新状态，则不允许在滑动的时候改变状态
                    boolean bool = moveDown(speed, true);
                    mLastY = (int) (-disY);
                    return bool;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //事件分发处理
        if (!mAutoScroller.isFinished()) {
            return false;
        }

        View head = getChildAt(0);
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL
                || ev.getAction() == MotionEvent.ACTION_POINTER_INDEX_MASK) {//松开手
            if (head.getBottom() > 0) {
                if (mState != OverView.IRefreshState.STATE_REFRESH) {//非正在刷新
                    recover(head.getBottom());
                    return false;
                }
            }
            mLastY = 0;
        }
        boolean consumed = mGestureDetector.onTouchEvent(ev);

        if ((consumed || (mState != OverView.IRefreshState.STATE_INIT && mState != OverView.IRefreshState.STATE_REFRESH)) && head.getBottom() != 0) {
            ev.setAction(MotionEvent.ACTION_CANCEL);//让父类接受不到真实的事件
            return super.dispatchTouchEvent(ev);
        }

        if (consumed) {
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //定义head和child的排列位置
        View head = getChildAt(0);
        View child = getChildAt(1);
        if (head != null && child != null) {
            int childTop = child.getTop();
            if (mState == OverView.IRefreshState.STATE_REFRESH) {
                head.layout(0, mOverView.mPullRefreshHeight - head.getMeasuredHeight(), right, mOverView.mPullRefreshHeight);
                child.layout(0, mOverView.mPullRefreshHeight, right, mOverView.mPullRefreshHeight + child.getMeasuredHeight());
            } else {
                //left,top,right,bottom
                head.layout(0, childTop - head.getMeasuredHeight(), right, childTop);
                child.layout(0, childTop, right, childTop + child.getMeasuredHeight());
            }

            View other;
            for (int i = 2; i < getChildCount(); ++i) {
                other = getChildAt(i);
                other.layout(0, top, right, bottom);
            }
        }
    }

    private void recover(int dis) {//dis =200  200-100
        if (mRefreshListener != null && dis > mOverView.mPullRefreshHeight) {
            mAutoScroller.recover(dis - mOverView.mPullRefreshHeight);
            mState = OverView.IRefreshState.STATE_OVER_RELEASE;
        } else {
            mAutoScroller.recover(dis);
        }
    }

    /**
     * 根据偏移量移动header与child
     *
     * @param offsetY 偏移量
     * @param nonAuto 是否非自动滚动触发
     * @return
     */
    private boolean moveDown(int offsetY, boolean nonAuto) {
        View head = getChildAt(0);
        View child = getChildAt(1);
        int childTop = child.getTop() + offsetY;

        if (childTop <= 0) {//异常情况的补充
            offsetY = -child.getTop();
            //移动head与child的位置，到原始位置
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            if (mState != OverView.IRefreshState.STATE_REFRESH) {
                mState = OverView.IRefreshState.STATE_INIT;
            }
        } else if (mState == OverView.IRefreshState.STATE_REFRESH && childTop > mOverView.mPullRefreshHeight) {
            //如果正在下拉刷新中，禁止继续下拉
            return false;
        } else if (childTop <= mOverView.mPullRefreshHeight) {//还没超出设定的刷新距离
            if (mOverView.getState() != OverView.IRefreshState.STATE_VISIBLE && nonAuto) {//头部开始显示
                mOverView.onVisible();
                mOverView.setState(OverView.IRefreshState.STATE_VISIBLE);
                mState = OverView.IRefreshState.STATE_VISIBLE;
            }
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            if (childTop == mOverView.mPullRefreshHeight && mState == OverView.IRefreshState.STATE_OVER_RELEASE) {
                refresh();
            }
        } else {
            if (mOverView.getState() != OverView.IRefreshState.STATE_OVER && nonAuto) {
                //超出刷新位置
                mOverView.onOver();
                mOverView.setState(OverView.IRefreshState.STATE_OVER);
            }
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
        }
        if (mOverView != null) {
            mOverView.onScroll(head.getBottom(), mOverView.mPullRefreshHeight);
        }
        return true;
    }


    /**
     * 刷新
     */
    private void refresh() {
        if (mRefreshListener != null) {
            mState = OverView.IRefreshState.STATE_REFRESH;

            mOverView.onRefresh();
            mOverView.setState(OverView.IRefreshState.STATE_REFRESH);
            mRefreshListener.onRefresh();
        }
    }


    /**
     * 借助Scroller实现视图的自动滚动
     * https://juejin.im/post/5c7f4f0351882562ed516ab6
     */
    private class AutoScroller implements Runnable {
        private Scroller mScroller;
        private int mLastY;
        private boolean mIsFinished;

        AutoScroller() {
            mScroller = new Scroller(getContext(), new LinearInterpolator());
            mIsFinished = true;
        }

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {//还未滚动完成
                moveDown(mLastY - mScroller.getCurrY(), false);
                mLastY = mScroller.getCurrY();
                post(this);
            } else {
                removeCallbacks(this);
                mIsFinished = true;
            }
        }

        void recover(int dis) {
            if (dis <= 0) {
                return;
            }
            removeCallbacks(this);
            mLastY = 0;
            mIsFinished = false;
            mScroller.startScroll(0, 0, 0, dis, 300);
            post(this);
        }

        boolean isFinished() {
            return mIsFinished;
        }

    }

}
