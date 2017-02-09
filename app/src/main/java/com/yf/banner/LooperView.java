package com.yf.banner;

/**
 * Created by ${yf} on 2017/2/9.
 */

public interface LooperView {
    //为了显示到圆点的位置
    void setCurrent(int position);

    //实例化圆点的个数
    void initView(int length);
}
