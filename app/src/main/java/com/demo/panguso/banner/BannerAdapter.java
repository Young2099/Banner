package com.demo.panguso.banner;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${yangfang} on 2016/11/18.
 */
public class BannerAdapter extends PagerAdapter {
    private List<View> data = new ArrayList<>();

    public BannerAdapter(List<View> views) {
        data = views;
    }

    @Override
    public int getCount() {
        if (data.size() > 1) {
            return Short.MAX_VALUE;
        }
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = data.get(position % data.size());
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
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
    }
}
