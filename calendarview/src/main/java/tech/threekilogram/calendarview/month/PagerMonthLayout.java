package tech.threekilogram.calendarview.month;

import android.content.Context;
import android.graphics.Color;
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
public class PagerMonthLayout extends ViewPager implements ViewComponent {

      private static final String TAG = PagerMonthLayout.class.getSimpleName();

      private CalendarView mCalendarView;

      private int mBaseYear;
      private int mBaseMonth;

      private boolean isScroll;

      private int mCellWidth  = -1;
      private int mCellHeight = -1;

      public PagerMonthLayout ( @NonNull Context context ) {

            super( context );
            init();
      }

      private void init ( ) {

            setAdapter( new PagerMonthAdapter() );
            setCurrentItem( Integer.MAX_VALUE >> 1 );
            setBackgroundColor( Color.GRAY );
            addOnPageChangeListener( new PagerChangeHeightScrollListener( this ) );
      }

      private Date getDate ( int position ) {

            int step = position - Integer.MAX_VALUE / 2;
            return CalendarUtils.getMonthByStep( mBaseYear, mBaseMonth, step );
      }

      @Override
      public View getView ( ) {

            return this;
      }

      @Override
      public void bindParent ( CalendarView calendarView ) {

            mCalendarView = calendarView;
      }

      @Override
      public void notifyFirstDayIsMondayChanged ( boolean isFirstDayMonday ) {

      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            if( isScroll ) {
                  int widthSize = MeasureSpec.getSize( widthMeasureSpec );
                  setMeasuredDimension( widthSize, getLayoutParams().height );
                  return;
            }

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
                  view.setCellSize( mCellWidth, mCellHeight );
            }
            super.onMeasure( widthMeasureSpec, heightMeasureSpec );

            int currentItem = getCurrentItem();
            for( int i = 0; i < childCount; i++ ) {
                  MonthPage view = (MonthPage) getChildAt( i );
                  if( currentItem == view.mPosition ) {
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

      public Date getCurrentMonth ( ) {

            int item = getCurrentItem();
            return getDate( item );
      }

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
                  page.setInfo( CalendarUtils.getYear( date ), CalendarUtils.getMonth( date ), position );

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
      }

      private void changeHeight ( int height, int nextHeight, float offset ) {

            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = (int) ( height + ( nextHeight - height ) * offset );
            requestLayout();
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
                              if( child.mPosition == current ) {
                                    currentHeight = child.getMeasuredHeight();
                                    continue;
                              }
                              if( child.mPosition == current + 1 ) {
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
                              if( child.mPosition == current ) {
                                    currentHeight = child.getMeasuredHeight();
                                    continue;
                              }
                              if( child.mPosition == current - 1 ) {
                                    nextHeight = child.getMeasuredHeight();
                              }
                        }

                        changeHeight( currentHeight, nextHeight, offset );
                  }
            }
      }
}
