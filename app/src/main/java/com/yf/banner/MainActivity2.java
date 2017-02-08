package com.yf.banner;

import android.os.Bundle;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.demo.panguso.banner.R;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    private LooperViewPager mViewPager;
    int[] imgs = new int[]{
            R.mipmap.m1,
            R.mipmap.m2,
            R.mipmap.m3,
            R.mipmap.m4,
            R.mipmap.m5,
    };
    private ArrayList<View> views = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mViewPager = (LooperViewPager) findViewById(R.id.view_pager);
        for(int i = 0;i<imgs.length;i++){
            views.add();
        }
        mViewPager.initData(views);
    }
}
