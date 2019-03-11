package tech.threekilogram.calendar.month;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by LiuJin on 2017-12-31:8:45
 * viewPager滚动时的观察者,滚动时会通知 OnRecyclerPagerScrollListener 当前滚动的position,下个将要显示的position,以及进度值
 *
 * @author liujin
 */
public abstract class ViewPagerScrollListener implements ViewPager.OnPageChangeListener {

      /**
       * 用于获取当前pager状态
       */
      protected ViewPager mPager;
      /**
       * 当前滚动状态
       */
      protected int       mState = ViewPager.SCROLL_STATE_IDLE;
      /**
       * 按下时位置
       */
      protected int       mDragPosition;
      /**
       * 选中时位置
       */
      protected int       mSettPosition;

      /**
       * 创建
       *
       * @param pager pager
       */
      public ViewPagerScrollListener ( ViewPager pager ) {

            mPager = pager;
      }

      @Override
      public void onPageScrolled ( int position, float positionOffset, int positionOffsetPixels ) {

            if( mState == ViewPager.SCROLL_STATE_DRAGGING ) {

                  if( position == mDragPosition ) {
                        onScrolled( mState, mDragPosition, -positionOffset, positionOffsetPixels );
                  }

                  if( position == mDragPosition - 1 ) {
                        onScrolled( mState, mDragPosition, 1 - positionOffset,
                                    positionOffsetPixels
                        );
                  }
            }

            if( mState == ViewPager.SCROLL_STATE_SETTLING ) {

                  if( positionOffset == 0 ) {

                        if( mDragPosition + 1 == mSettPosition ) {
                              onScrolled( mState, mDragPosition, -1f, positionOffsetPixels );
                        }
                  }

                  if( position == mDragPosition ) {
                        onScrolled(
                            mState, mDragPosition, -positionOffset, positionOffsetPixels );
                  }
                  if( position == mDragPosition - 1 ) {
                        onScrolled( mState, mDragPosition, 1 - positionOffset,
                                    positionOffsetPixels
                        );
                  }
            }
      }

      protected abstract void onScrolled ( int state, int current, float offset, int offsetPixels );

      @Override
      public void onPageSelected ( int position ) { }

      @Override
      public void onPageScrollStateChanged ( int state ) {

            if( state == ViewPager.SCROLL_STATE_DRAGGING ) {
                  mDragPosition = mPager.getCurrentItem();
            }

            if( state == ViewPager.SCROLL_STATE_SETTLING ) {
                  mSettPosition = mPager.getCurrentItem();
            }

            mState = state;
      }
}
