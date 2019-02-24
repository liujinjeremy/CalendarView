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

      private CalendarView mParent;

      private int mBaseYear;
      private int mBaseMonth;

      private int mLeftHeight;
      private int mCurrentHeight;
      private int mRightHeight;

      public PagerMonthLayout ( @NonNull Context context, CalendarView parent ) {

            super( context );
            mParent = parent;
            init();
      }

      private void init ( ) {

            setAdapter( new PagerMonthAdapter() );
            setCurrentItem( Integer.MAX_VALUE >> 1 );

            Date date = new Date();
            mBaseYear = CalendarUtils.getYear( date );
            mBaseMonth = CalendarUtils.getMonth( date );

            setBackgroundColor( Color.GRAY );
      }

      public void setBaseDate ( int year, int month ) {

            mBaseYear = year;
            mBaseMonth = month;

            setAdapter( new PagerMonthAdapter() );
            setCurrentItem( Integer.MAX_VALUE / 2 );
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
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            int widthSize = MeasureSpec.getSize( widthMeasureSpec );
            int heightSize = MeasureSpec.getSize( heightMeasureSpec );

            int cellWidth = widthSize / 7;
            int cellHeight = heightSize / 6;

            int childCount = getChildCount();

            for( int i = 0; i < childCount; i++ ) {
                  MonthPage view = (MonthPage) getChildAt( i );
                  view.setCellSize( cellWidth, cellHeight );
            }
            super.onMeasure( widthMeasureSpec, heightMeasureSpec );

            int currentItem = getCurrentItem();
            for( int i = 0; i < childCount; i++ ) {
                  MonthPage view = (MonthPage) getChildAt( i );
                  if( currentItem == view.mPosition ) {
                        mCurrentHeight = view.getMeasuredHeight();
                        continue;
                  }
                  if( currentItem == view.mPosition - 1 ) {
                        mLeftHeight = view.getMeasuredHeight();
                        continue;
                  }
                  if( currentItem == view.mPosition + 1 ) {
                        mRightHeight = view.getMeasuredHeight();
                  }
            }

            setMeasuredDimension( widthSize, mCurrentHeight );
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
}
