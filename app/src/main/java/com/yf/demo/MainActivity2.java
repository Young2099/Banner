package com.yf.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.demo.panguso.banner.R;
import com.yf.banner.LooperAdapter;
import com.yf.banner.LooperViewPager;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    private LooperViewPager mViewPager;
    private int[] Image = {R.mipmap.m1, R.mipmap.m2, R.mipmap.m5, R.mipmap.m3, R.mipmap.m4};
    private ArrayList<View> views = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mViewPager = (LooperViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new ImageLooperAdapter(mViewPager));
        mViewPager.setDelay(4000);
    }

    private class ImageLooperAdapter extends LooperAdapter {
        int[] imgs = new int[]{
                R.mipmap.img1,
                R.mipmap.img2,
                R.mipmap.img3,
                R.mipmap.img4,
                R.mipmap.img5,
        };

        public ImageLooperAdapter(LooperViewPager viewPager) {
            super(viewPager);
        }

        @Override
        protected View getView(ViewGroup container, int realPosition) {
            ImageView view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setImageResource(imgs[realPosition]);
            return view;
        }

        @Override
        protected int getRealCount() {
            return imgs.length;
        }
    }
}
