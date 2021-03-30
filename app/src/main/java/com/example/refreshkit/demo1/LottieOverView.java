package com.example.refreshkit.demo1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.airbnb.lottie.LottieAnimationView;
import com.example.refreshkit.R;
import com.refresh.kit.view.OverView;


/**
 * Author: 信仰年轻
 * Date: 2020-09-21 16:21
 * Email: hydznsqk@163.com
 * Des: 自定义下拉头
 */
public class LottieOverView extends OverView {
    private LottieAnimationView pullAnimationView;

    public LottieOverView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LottieOverView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LottieOverView(Context context) {
        super(context);
    }

    @Override
    public void init() {

        LayoutInflater.from(getContext()).inflate(R.layout.lottie_item_layout, this, true);

        pullAnimationView = findViewById(R.id.pull_animation);
        pullAnimationView.setAnimation("loading_wave.json");
    }

    @Override
    public void onScroll(int scrollY, int pullRefreshHeight) {

    }

    @Override
    public void onVisible() {

    }

    @Override
    public void onOver() {

    }

    @Override
    public void onRefresh() {
        pullAnimationView.setSpeed(2);
        pullAnimationView.playAnimation();
    }

    @Override
    public void onFinish() {
        pullAnimationView.setProgress(0f);
        pullAnimationView.cancelAnimation();
    }
}
