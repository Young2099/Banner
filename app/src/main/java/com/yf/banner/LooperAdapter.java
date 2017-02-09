package com.yf.banner;

import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by ${yf} on 2017/2/8.
 */

public abstract class LooperAdapter extends PagerAdapter {

    private ArrayList<View> viewsList = new ArrayList<>();
    private LooperViewPager mViewPager;

    public LooperAdapter(LooperViewPager viewPager) {
        mViewPager = viewPager;
        viewPager.setViewDelegate(new LooperViewImpl() {
            @Override
            public void setCurrentPosition(int position, LooperView delegateView) {
                if (delegateView != null && getRealCount() > 0) {
                    delegateView.setCurrent(position % getRealCount());
                }
            }

            @Override
            public void initView(int length, LooperView delegateView) {
                if (delegateView != null) {
                    delegateView.initView(getRealCount());
                }
            }
        });
    }

    @Override
    public int getCount() {
        return getRealCount() < 0 ? getRealCount() : Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void notifyDataSetChanged() {
        viewsList.clear();
        initPosition();
        super.notifyDataSetChanged();
    }

    private void initPosition() {
        if (mViewPager.getViewPager().getCurrentItem() == 0 && getRealCount() > 0) {
            int half = Integer.MAX_VALUE / 2;
            int start = half - half % getRealCount();
            setCurrent(start);
        }
    }

    private void setCurrent(int start) {
        try {
            Field field = ViewPager.class.getDeclaredField("mCurItem");
            field.setAccessible(true);
            field.set(mViewPager.getViewPager(), start);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
        initPosition();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //显示的view的position
        int realPosition = position % getRealCount();
        //根据位置来添加显示的view
        View itemView = findViewByPosition(container, realPosition);
        container.addView(itemView);
        return itemView;
    }

    private View findViewByPosition(ViewGroup container, int realPosition) {
        for (View view : viewsList) {
            if (((int) view.getTag()) == realPosition && view.getParent() == null) {
                return view;
            }
        }
        //将方法暴露，实现这个抽象方法来添加各个位置上的view
        View view = getView(container, realPosition);
        view.setTag(realPosition);
        viewsList.add(view);
        return view;
    }

    protected abstract View getView(ViewGroup container, int realPosition);

    protected abstract int getRealCount();

}
