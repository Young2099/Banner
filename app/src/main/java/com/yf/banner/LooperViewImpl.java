package com.yf.banner;

/**
 * Created by ${yf} on 2017/2/9.
 */

public interface LooperViewImpl {
    void setCurrentPosition(int position, LooperView delegateView);

    void initView(int length, LooperView delegateView);
}
