package com.demo.panguso.banner;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by ${yangfang} on 2016/11/29.
 */

public abstract class WeakReferenceHandler<T> extends Handler {
    public WeakReference<T> mReference;

    public WeakReferenceHandler(T reference) {
        mReference = new WeakReference<T>(reference);
    }

    @Override
    public void handleMessage(Message msg) {
        if (mReference.get() == null) {
            return;
        }
        handleMessage(mReference.get(), msg);
    }

    protected abstract void handleMessage(T reference, Message msg);
}
