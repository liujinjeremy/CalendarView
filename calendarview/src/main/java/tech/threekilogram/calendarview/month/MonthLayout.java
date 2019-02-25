package tech.threekilogram.calendarview.month;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.Date;
import java.util.LinkedList;
import tech.threekilogram.calendarview.CalendarUtils;
import tech.threekilogram.calendarview.CalendarView;
import tech.threekilogram.calendarview.CalendarView.ViewComponent;

/**
 * @author Liujin 2019/2/21:13:00:25
 */
public class MonthLayout extends ViewPager implements ViewComponent {

      private static final String TAG = MonthLayout.class.getSimpleName();

      /**
       * parent
       */
      private CalendarView mCalendarView;
      /**
       * 基准日期
       */
      private Date         mBaseDate;
      private int          mSelectedDayOfMonth;
      /**
       * 页面是否是滚动
       */
      private boolean      isScroll;
      /**
       * 天的布局宽度
       */
      private int          mCellWidth  = -1;
      /**
       * 天的布局高度
       */
      private int          mCellHeight = -1;

      private int mLastX;
      private int mLastY;

      private VerticalExpendFoldHelper mHelper = new VerticalExpendFoldHelper();

      public MonthLayout ( @NonNull Context context ) {

            super( context );
      }

      /**
       * 获取该位置的日期
       */
      public Date getDate ( int position ) {

            int step = position - Integer.MAX_VALUE / 2;
            return CalendarUtils.getMonthByStep( mBaseDate, step );
      }

      @Override
      public View getView ( ) {

            return this;
      }

      public void updateSelectedDayOfMonth ( int newSelected ) {

            if( mSelectedDayOfMonth != newSelected ) {
                  mSelectedDayOfMonth = newSelected;
            }
      }

      @Override
      public void bindParent ( CalendarView calendarView ) {

            mCalendarView = calendarView;
            mBaseDate = calendarView.getDate();
            mSelectedDayOfMonth = CalendarUtils.getDayOfMonth( mBaseDate );

            setAdapter( new PagerMonthAdapter() );
            setCurrentItem( Integer.MAX_VALUE >> 1 );
            addOnPageChangeListener( new PagerChangeHeightScrollListener( this ) );
      }

      @Override
      public void notifyFirstDayIsMondayChanged ( boolean isFirstDayMonday ) {

            getAdapter().notifyDataSetChanged();
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            /* 滚动时,重设页面高度,不必重新测量,已经设置好,直接使用 */
            if( isScroll ) {
                  int widthSize = MeasureSpec.getSize( widthMeasureSpec );
                  setMeasuredDimension( widthSize, getLayoutParams().height );
                  return;
            }

            /* 计算基本尺寸,每个月最多7*6个子view */
            int widthSize = MeasureSpec.getSize( widthMeasureSpec );
            int heightSize = MeasureSpec.getSize( heightMeasureSpec );
            if( mCellWidth == -1 ) {
                  mCellWidth = widthSize / 7;
            }
            if( mCellHeight == -1 ) {
                  mCellHeight = heightSize / 6;
            }

            int childCount = getChildCount();
            for( int i = 0; i < childCount; i++ ) {
                  MonthPage view = (MonthPage) getChildAt( i );
                  /* 为每天的布局设置基本尺寸 */
                  view.setCellSize( mCellWidth, mCellHeight );
            }

            super.onMeasure( widthMeasureSpec, heightMeasureSpec );

            int currentItem = getCurrentItem();
            for( int i = 0; i < childCount; i++ ) {
                  MonthPage view = (MonthPage) getChildAt( i );
                  if( currentItem == view.getPosition() ) {
                        /* 将当前页面的高度设置为pager高度 */
                        setMeasuredDimension( widthSize, view.getMeasuredHeight() );
                        break;
                  }
            }
      }

      @Override
      protected void onLayout ( boolean changed, int l, int t, int r, int b ) {

            if( isScroll ) {
                  return;
            }
            super.onLayout( changed, l, t, r, b );
      }

      @Override
      public boolean onTouchEvent ( MotionEvent ev ) {

            return super.onTouchEvent( ev );
      }

      /**
       * 页面滑动时改变页面高度
       *
       * @param height 当前页面高度
       * @param nextHeight 下一个页面高度
       * @param offset 滑动进度
       */
      private void changeHeight ( int height, int nextHeight, float offset ) {

            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = (int) ( height + ( nextHeight - height ) * offset );
            requestLayout();
      }

      /**
       * adapter 设置页面
       */
      private class PagerMonthAdapter extends PagerAdapter {

            private LinkedList<View> mReUsed = new LinkedList<>();

            @Override
            public int getCount ( ) {

                  return Integer.MAX_VALUE;
            }

            @NonNull
            @Override
            public Object instantiateItem ( @NonNull ViewGroup container, int position ) {

                  MonthPage page;
                  if( mReUsed.isEmpty() ) {
                        page = new MonthPage( container.getContext() );
                  } else {
                        page = (MonthPage) mReUsed.pollFirst();
                  }

                  Date date = getDate( position );
                  page.setInfo( mCalendarView.isFirstDayMonday(), date, position, mSelectedDayOfMonth );

                  container.addView( page );
                  return page;
            }

            @Override
            public boolean isViewFromObject ( @NonNull View view, @NonNull Object object ) {

                  return object == view;
            }

            @Override
            public void destroyItem ( @NonNull ViewGroup container, int position, @NonNull Object object ) {

                  View view = (View) object;
                  container.removeView( view );
                  mReUsed.add( view );
            }

            @Override
            public int getItemPosition ( @NonNull Object object ) {

                  return POSITION_NONE;
            }
      }

      /**
       * 改变页面高度
       */
      private class PagerChangeHeightScrollListener extends ViewPagerScrollListener {

            /**
             * 创建
             *
             * @param pager pager
             */
            private PagerChangeHeightScrollListener ( ViewPager pager ) {

                  super( pager );
            }

            @Override
            protected void onScrolled ( int state, int current, float offset, int offsetPixels ) {

                  isScroll = ( offset != 1 ) && ( offset != -1 );

                  if( offset < 0 ) {

                        int currentHeight = 0;
                        int nextHeight = 0;
                        int childCount = getChildCount();
                        for( int i = 0; i < childCount; i++ ) {
                              MonthPage child = (MonthPage) getChildAt( i );
                              if( child.getPosition() == current ) {
                                    currentHeight = child.getMeasuredHeight();
                                    continue;
                              }
                              if( child.getPosition() == current + 1 ) {
                                    nextHeight = child.getMeasuredHeight();
                              }
                        }

                        changeHeight( currentHeight, nextHeight, -offset );
                  }

                  if( offset > 0 ) {

                        int currentHeight = 0;
                        int nextHeight = 0;
                        int childCount = getChildCount();
                        for( int i = 0; i < childCount; i++ ) {
                              MonthPage child = (MonthPage) getChildAt( i );
                              if( child.getPosition() == current ) {
                                    currentHeight = child.getMeasuredHeight();
                                    continue;
                              }
                              if( child.getPosition() == current - 1 ) {
                                    nextHeight = child.getMeasuredHeight();
                              }
                        }

                        changeHeight( currentHeight, nextHeight, offset );
                  }
            }
      }

      private class VerticalExpendFoldHelper {

            private int mTop;
            private int mBottom;
            private int mTargetTop;
            private int mTargetBottom;

            private void set ( int top, int bottom, int targetTop, int targetBottom ) {

                  mTop = top;
                  mBottom = bottom;
                  mTargetTop = targetTop;
                  mTargetBottom = targetBottom;
            }
      }
}
