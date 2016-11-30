# Banner
广告轮播图的实现.

项目中要实现广告的轮播，之前实现的老是有问题，综合网上大神的做法，写的这个轮播图流畅度还可以。

重点1： 设置初始化的轮播图的值是：

     int currentPosition = Short.MAX_VALUE / 2 - (Short.MAX_VALUE / 2) % mViewList.size();//设置显示的item在中间值，Short跟Integer类似
     
    都是取的position认为无限大，一般滑不到界限。
    
重点2：在viewpager的OnpagerChangerListener里面设置。解释代码中有：

      private class ViewPagerListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
        @Override
        public void onPageSelected(int position) {
            if (isCycle()) {
                if (position == 0) { //假设向左滑动如果position是0
                    mViewPager.setCurrentItem(mViewList.size(), false);
                } else if (position == Short.MAX_VALUE - 1) {//假设向右滑到最后一个位置
                    mViewPager.setCurrentItem(mViewList.size() - 1, false);
                }
            }
            mIndicatorLayout.getChildAt(prePosition).findViewById(R.id.radius)
                    .setBackgroundResource(R.drawable.indicator_normal);//设置没被选中的指示灯的样式
            mIndicatorLayout.getChildAt(position % mViewList.size()).findViewById(R.id.radius)
                    .setBackgroundResource(R.drawable.indicator_select);//当前指示灯的样式
            prePosition = position % mViewList.size();//求与得到页面位置。实际上只有所传数据的大小
        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
    
重点3：是根据在屏幕上左右是否有滑动，判断dx，来区分手势滑动和循环滑动，不是根据手势滑动的时间间隔来判断的

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
    
重点4：viewpager的adpater：一个是原视图的移除（不再显示的视图），另一个是新增显示视图（即将显示的视图）：
 
     @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = data.get(position % data.size());
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent != null) {
                container.removeView(view);//移除原来的视图，
            }
        }
        container.addView(view);//增加新的视图显示并且返回这个页面的Key也就是view，destoryItem里面会将改view从viewpager中删除。
        return view;
    }
    
   
其他详细见代码。
