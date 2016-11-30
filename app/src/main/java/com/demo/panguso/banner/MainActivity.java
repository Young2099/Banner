package com.demo.panguso.banner;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int MOVE_PAGER = 1;
    //模拟图片
    private int[] Image = {R.mipmap.m1, R.mipmap.m2, R.mipmap.m5, R.mipmap.m3, R.mipmap.m4};
    private CycleViewPager mViewPager;
    List<View> viewList = new ArrayList<>();
    private Handler mHandler;

    private class MyHandler extends WeakReferenceHandler<MainActivity> {

        public MyHandler(MainActivity reference) {
            super(reference);
        }

        @Override
        protected void handleMessage(MainActivity reference, Message msg) {

            if (msg.what == MOVE_PAGER) {
                reference.mViewPager.setCurrentItem();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new MyHandler(this);
        mViewPager = (CycleViewPager) findViewById(R.id.cycle_viewpager);
        initViewList();
        mViewPager.initData(viewList, mHandler);
    }

    private void initViewList() {
        for (int list : Image) {
            View view = getImage(list);
            viewList.add(view);
        }
    }

    /**
     * 设置viewpager页面的展示的view（项目中用到了gridview的轮播图）
     *
     * @param list
     * @return
     */
    private View getImage(int list) {
        RelativeLayout rl = new RelativeLayout(this);
        ImageView imageView = new ImageView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(list);
        rl.addView(imageView);
        return rl;
    }
}
