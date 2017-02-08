package com.yf.banner;

import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;

/**
 * Created by ${yf} on 2017/2/8.
 */

public class LooperAdapter extends PagerAdapter {
    private ArrayList<View> mViewList = new ArrayList<>();
    private LooperViewPager mViewPager;

    public LooperAdapter(ArrayList<View> views, LooperViewPager viewPager) {
        mViewList = views;
        this.mViewPager = viewPager;
        mViewPager.setHintViewDelegate(new LoopHintViewDelegate());
    }

    @Override
    public int getCount() {
        if (mViewList.size() > 1) {
            return Short.MAX_VALUE;
        }
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViewList.get(position % mViewList.size());
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent != null) {
                container.removeView(view);
            }
        }
        container.addView(view);
        return view;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
        if (getCount() == 0) return;
        int half = Integer.MAX_VALUE / 2;
        int start = half - half % mViewList.size();
        mViewPager.getViewPager().setCurrentItem(start, false);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
    }
    private class LoopHintViewDelegate implements LooperViewPager.ViewDelegate{
        @Override
        public void setCurrentPosition(int position, HintView hintView) {
            if (hintView!=null)
                hintView.setCurrent(position% mViewList.size());
        }

    }
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mViewList.clear();
    }

}
