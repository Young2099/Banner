package com.yf.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.demo.panguso.banner.R;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ${yf} on 2017/2/8.
 * 设置循环的ViewPager
 */

public class LooperViewPager extends RelativeLayout implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private ShapeView mIndicatorView;
    private PagerAdapter mAdapter;
    //播放延迟
    private int delay;
    //异步操作
    private Timer timer;
    //滑动触屏的时间

    private int focusColor = Color.BLACK;
    private int normalColor = Color.WHITE;
    private long mRecentTouchTime;

    private int paddingLeft = 0;
    private int paddingTop = 0;
    private int paddingRight = 0;
    private int paddingBottom = 0;

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

    //初始化viewpager
    private void initView(AttributeSet attrs) {
        if (mViewPager != null) {
            removeView(mViewPager);
        }
        //可以在布局文件设置style;
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LooperViewPager);
        paddingBottom = (int) typedArray.getDimension(R.styleable.LooperViewPager_LooperViewPager_hint_paddingBottom, Util.dip2px(getContext(), 4));
        mViewPager = new ViewPager(getContext());
        mViewPager.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        mViewPager.setId(R.id.viewpager_inner);
        addView(mViewPager);
        initIndicatorView();
    }

    /**
     * 初始化指示器的view
     */
    private void initIndicatorView() {
        if (mIndicatorView != null) {
            removeView(mIndicatorView);
        }
        mIndicatorView = new ShapeView(getContext());
        mIndicatorView.setColor(focusColor, normalColor);
        mIndicatorView.setPadding(paddingLeft, paddingRight, paddingTop, paddingBottom);
        LayoutParams layoutParams = new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mIndicatorView.setLayoutParams(layoutParams);
        addView(mIndicatorView);
        mLooperViewDelegate.initView(mAdapter == null ? 0 : mAdapter.getCount(), mIndicatorView);
    }

    private LooperViewImpl mLooperViewDelegate = new LooperViewImpl() {
        @Override
        public void setCurrentPosition(int position, LooperView delegateView) {
            if (delegateView != null) {
                delegateView.setCurrent(position);
            }
        }

        @Override
        public void initView(int length, LooperView delegateView) {
            if (delegateView != null) {
                delegateView.initView(length);
            }
        }
    };

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

    public void setAdapter(PagerAdapter adapter) {
        adapter.registerDataSetObserver(new JPagerObserver());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOnTouchListener(new ViewPagerTouch());
        mAdapter = adapter;
        dataSetChanged();
    }

    /**
     * 用来实现adapter的notifyDataSetChanged通知HintView变化
     */
    private class JPagerObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            dataSetChanged();
        }

        @Override
        public void onInvalidated() {
            dataSetChanged();
        }
    }

    private void dataSetChanged() {
        if (mIndicatorView != null) {
            mLooperViewDelegate.initView(mAdapter.getCount(), mIndicatorView);
            mLooperViewDelegate.setCurrentPosition(mViewPager.getCurrentItem(), mIndicatorView);
        }
        //开始循环
        startPlay();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mLooperViewDelegate.setCurrentPosition(position, mIndicatorView);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void setViewDelegate(LooperViewImpl looperViewDelegate) {
        mLooperViewDelegate = looperViewDelegate;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    /**
     * 异步任务，循环播放
     */
    private static class WeakTimerTask extends TimerTask {
        private WeakReference<LooperViewPager> mLooperViewPagerWeakReference;

        public WeakTimerTask(LooperViewPager looperViewPager) {
            this.mLooperViewPagerWeakReference = new WeakReference<>(looperViewPager);
        }

        @Override
        public void run() {
            LooperViewPager looperViewPager = mLooperViewPagerWeakReference.get();
            if (looperViewPager != null) {
                if (looperViewPager.isShown() && System.currentTimeMillis() - looperViewPager.mRecentTouchTime > looperViewPager.delay) {
                    looperViewPager.mHandler.sendEmptyMessage(0);
                }
            } else {
                cancel();
            }
        }
    }

    private TimerTaskHandler mHandler = new TimerTaskHandler(this);

    /**
     * 软引用的handler
     */
    private static class TimerTaskHandler extends Handler {
        private WeakReference<LooperViewPager> mLooperViewPagerWeakReference;

        public TimerTaskHandler(LooperViewPager looperViewPager) {
            this.mLooperViewPagerWeakReference = new WeakReference<>(looperViewPager);
        }

        @Override
        public void handleMessage(Message msg) {
            LooperViewPager looperViewPager = mLooperViewPagerWeakReference.get();
            int cur = looperViewPager.mViewPager.getCurrentItem() + 1;
            if (cur >= looperViewPager.mAdapter.getCount()) {
                cur = 0;
            }
            looperViewPager.mViewPager.setCurrentItem(cur);
            looperViewPager.mLooperViewDelegate.setCurrentPosition(cur, looperViewPager.mIndicatorView);
            if (looperViewPager.mViewPager.getCurrentItem() <= 1) {
                looperViewPager.stopPlay();
            }
        }
    }

    /**
     * 停止播放
     */
    private void stopPlay() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private class ViewPagerTouch implements OnTouchListener {
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

    /**
     * 实现触摸时和过后一定时间内不滑动，在动作分发的时候拦截
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mRecentTouchTime = System.currentTimeMillis();
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 设置圆点颜色
     *
     * @return
     */
    private int setFoucsColor() {
        return focusColor;
    }

    private int setNormalColor() {
        return normalColor;
    }

    public void onStop() {
        stopPlay();
    }

    public void onResume() {
        startPlay();
    }

    public void setDelay(int delay) {
        this.delay = delay;
        startPlay();
    }
}
