package com.yf.banner;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ${yf} on 2017/2/8.
 * 设置循环的ViewPager
 */

public class LooperViewPager extends LinearLayout implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private View mIndicatorView;
    private ArrayList<View> views;
    private PagerAdapter mAdapter;
    //播放延迟
    private int delay;
    //异步操作
    private Timer timer;
    //滑动触屏的时间
    private long mRecentTouchTime;

    public LooperViewPager(Context context) {
        this(context, null);
    }

    public LooperViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LooperViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    public void initData(ArrayList<View> view) {
        views = view;
        initIndicatorView();
        setAdapter(new LooperAdapter(views, this));
    }

    //设置接口，将HintView的方法引用到这里，设置要显示指示器的position
    public interface ViewDelegate {
        void setCurrentPosition(int position, HintView hintView);
    }

    private ViewDelegate mViewDelegate = new ViewDelegate() {
        @Override
        public void setCurrentPosition(int position, HintView hintView) {
            if (hintView != null) {
                hintView.setCurrent(position);
            }
        }
    };

    /**
     * 初始化ViewPager
     */
    private void initView(AttributeSet attrs) {
        if (mViewPager != null) {
            removeView(mViewPager);
        }
        //设置viewPager的style,将它加入到LooperViewPager这个view里面去
        mViewPager = new ViewPager(getContext(), attrs);
        mViewPager.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        addView(mViewPager);
        mViewPager.setOnTouchListener(new ViewPagerTounch());
    }

    /**
     * 设置圆点View，在这里是根据传入的图片的view设置圆点的个数
     */
    private void initIndicatorView() {
        IndicatorView indicatorView = new IndicatorView(getContext());
        indicatorView.setColorAndSize(views.size(), Color.parseColor("#E3AC42"), Color.parseColor("#88ffffff"));
        if (mIndicatorView != null) {
            removeView(mIndicatorView);
        }
        if (mIndicatorView == null || !(mIndicatorView instanceof HintView)) {
            return;
        }
        mIndicatorView = indicatorView;
        loadIndicatorView();
    }

    /**
     * 将indicator的view加入到viewpager
     */
    private void loadIndicatorView() {
        addView(mIndicatorView);
        mIndicatorView.setPadding(0, 0, 0, 10);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //将圆点指示器设置到viewpager的底部
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mIndicatorView.setLayoutParams(lp);
    }

    public void setAdapter(PagerAdapter adapter) {
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(this);
        mAdapter = adapter;
        dataSetChanged();
        adapter.registerDataSetObserver(new PagerObserver());
    }

    private void dataSetChanged() {
        startPlay();
    }

    private class ViewPagerTounch implements OnTouchListener {
        float x = 0;
        float y = 0;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = motionEvent.getX();
                    y = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = motionEvent.getX() - x;
                    float dy = motionEvent.getY() - y;
                    if (dx != 0) {
                        stopPlay();
                    }
                    if (dy > 0 && dx == 0) {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    startPlay();
                    break;

            }
            return false;
        }
    }

    private void stopPlay() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 开始轮播
     */
    private void startPlay() {
        if (delay <= 0 || mAdapter == null || mAdapter.getCount() <= 1) {
            return;
        }
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new WeakTimerTask(this), delay, delay);
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    private static class TimerTaskHandler extends Handler {
        private WeakReference<LooperViewPager> mLoopViewPagerWeakReference;

        public TimerTaskHandler(LooperViewPager loopViewPager) {
            this.mLoopViewPagerWeakReference = new WeakReference<>(loopViewPager);
        }

        @Override
        public void handleMessage(Message msg) {
            LooperViewPager loopViewPager = mLoopViewPagerWeakReference.get();
            int current = loopViewPager.getViewPager().getCurrentItem() + 1;
            if (current >= loopViewPager.mAdapter.getCount()) {
                current = 0;
            }
            loopViewPager.getViewPager().setCurrentItem(current);
            //这里也要圆点进行相应的滑动
            loopViewPager.mViewDelegate.setCurrentPosition(current, (HintView) loopViewPager.mIndicatorView);
        }
    }

    private TimerTaskHandler mHandler = new TimerTaskHandler(this);

    /**
     * 延迟多少执行scheduled,创建软引用的task
     */
    private static class WeakTimerTask extends TimerTask {
        private WeakReference<LooperViewPager> mLoopViewPagerWeakReference;

        public WeakTimerTask(LooperViewPager loopViewPager) {
            this.mLoopViewPagerWeakReference = new WeakReference<>(loopViewPager);
        }

        @Override
        public void run() {
            LooperViewPager loopViewPager = mLoopViewPagerWeakReference.get();
            if (loopViewPager != null) {
                if (loopViewPager.isShown() && System.currentTimeMillis() - loopViewPager.mRecentTouchTime > loopViewPager.delay) {
                    //要发出消息进行循环
                    loopViewPager.mHandler.sendEmptyMessage(0);
                }
            } else {
                cancel();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mRecentTouchTime = System.currentTimeMillis();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mViewDelegate.setCurrentPosition(position, (HintView) mIndicatorView);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public void setHintViewDelegate(ViewDelegate delegate) {
        this.mViewDelegate = delegate;
    }


    /**
     * 用来实现adapter的notifyDataSetChanged通知position变化
     */
    private class PagerObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            dataSetChanged();
        }

        @Override
        public void onInvalidated() {
            dataSetChanged();
        }
    }

}
