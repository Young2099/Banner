package com.demo.panguso.banner;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${yangfang} on 2016/11/18.
 */

public class CycleViewPager extends FrameLayout {

    public static final int MOVE_PAGER = 1;
    private Context mContext;
    private LayoutInflater inflater;
    private ViewPager mViewPager;
    private LinearLayout mIndicatorLayout;
    private List<View> mViewList = new ArrayList<>();

    //当前viewpager展示的页面
    private int prePosition = 0;
    //设置轮播
    private boolean isCycle = true;
    //设置是否循环
    private boolean isWheel;
    //延播时间
    private long delayTime = 4000;

    private Handler handler;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(MOVE_PAGER);
        }
    };

    public CycleViewPager(Context context) {
        super(context);
    }

    public CycleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.view_pager, this, true);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mIndicatorLayout = (LinearLayout) view.findViewById(R.id.ll_indicator);
    }

    public void initData(List<View> view, Handler mHandler) {
        handler = mHandler;
        initViewPager(view);
    }

    private void initViewPager(List<View> views) {
        if (views == null || views.size() == 0) {
            return;
        }
        mViewList = views;
        //这是设置图片只有一张，不轮播的判断
        if (isCycle()) {
            for (int i = 0; i < mViewList.size(); i++) {
                mIndicatorLayout.addView(inflater.inflate(R.layout.item_radius, mViewPager, false));
            }
        } else {
            //处理一张图片的情况
            mIndicatorLayout.addView(inflater.inflate(R.layout.item_radius, mViewPager, false));
            isCycle = false;
        }
        mIndicatorLayout.getChildAt(0).findViewById(R.id.radius)
                .setBackgroundResource(R.drawable.indicator_select);
        int currentPosition = Short.MAX_VALUE / 2 - (Short.MAX_VALUE / 2) % mViewList.size();//设置显示的item在中间值，Short跟Integer类似
        mViewPager.setOnPageChangeListener(new ViewPagerListener());
        mViewPager.setCurrentItem(currentPosition);
        mViewPager.setAdapter(new BannerAdapter(mViewList));
        mViewPager.setOnTouchListener(new OnTouchListener());
        setWheelView(true);//设置循环轮播
    }

    /**
     * 在activity中控制是否循环
     *
     * @param wheelView
     */
    public void setWheelView(boolean wheelView) {
        this.isWheel = wheelView;
        setIsCycle(true);
        if (isWheel) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, delayTime);
        } else if(runnable != null){
            handler.removeCallbacks(runnable);
        }

    }

    /**
     * 扩展是否轮播
     *
     * @param isCycle
     */
    public void setIsCycle(boolean isCycle) {
        this.isCycle = isCycle;
    }

    private boolean isCycle() {
        return isCycle;
    }

    public void setCurrentItem() {
        int currentItem = (mViewPager.getCurrentItem() + 1);
        mViewPager.setCurrentItem(currentItem, true);
        handler.postDelayed(runnable, delayTime);

    }

    private class ViewPagerListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (isCycle()) {
                if (position == 0) { //向左滑动如果position是0，则设置到最后一个位置
                    mViewPager.setCurrentItem(mViewList.size(), false);
                } else if (position == Short.MAX_VALUE - 1) {//假设滑到最后一个位置
                    mViewPager.setCurrentItem(mViewList.size() - 1, false);
                }
            }
            mIndicatorLayout.getChildAt(prePosition).findViewById(R.id.radius)
                    .setBackgroundResource(R.drawable.indicator_normal);
            mIndicatorLayout.getChildAt(position % mViewList.size()).findViewById(R.id.radius)
                    .setBackgroundResource(R.drawable.indicator_select);
            prePosition = position % mViewList.size();//求与得到页面位置。实际上只有所传数据的大小
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private class OnTouchListener implements View.OnTouchListener {
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
                    //根据手势滑动的距离做处理是否循环
                    if (dx != 0) {
                        handler.removeCallbacks(runnable);
                    } else {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    handler.postDelayed(runnable, delayTime);
                    break;
            }
            return false;
        }
    }
}
