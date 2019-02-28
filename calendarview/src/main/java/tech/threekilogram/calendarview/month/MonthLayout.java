package tech.threekilogram.calendarview.month;

import android.content.Context;
import android.graphics.Color;
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
      private CalendarView         mCalendarView;
      /**
       * 基准日期
       */
      private Date                 mBaseDate;
      /**
       * 页面滚动时改变高度
       */
      private ChangeHeightScroller mListener;
      /**
       * 展开折叠页面
       */
      private ExpandFoldPage       mExpandFoldPage;

      public MonthLayout ( @NonNull Context context ) {

            super( context );
            setBackgroundColor( Color.LTGRAY );
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

      @Override
      public void bindParent ( CalendarView calendarView ) {

            mCalendarView = calendarView;
            mBaseDate = calendarView.getDate();

            setAdapter( new PagerMonthAdapter() );
            setCurrentItem( Integer.MAX_VALUE >> 1 );
            mListener = new ChangeHeightScroller( this );
            addOnPageChangeListener( mListener );
            mExpandFoldPage = new ExpandFoldPage();
      }

      @Override
      public void notifyFirstDayIsMondayChanged ( boolean isFirstDayMonday ) {

            getAdapter().notifyDataSetChanged();
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            int widthSize = MeasureSpec.getSize( widthMeasureSpec );

            /* 滚动时,重设页面高度,不必重新测量,已经设置好,直接使用 */
            if( mListener.isScrolling ) {
                  setMeasuredDimension( widthSize, getLayoutParams().height );
                  return;
            }

            /* 当前页面展开折叠时改变高度 */
            if( mExpandFoldPage.isVerticalMoving ) {
                  setMeasuredDimension( widthSize, getLayoutParams().height );
                  return;
            }

            super.onMeasure( widthMeasureSpec, heightMeasureSpec );

            /* 将当前页面的高度设置为pager高度 */
            int childCount = getChildCount();
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

            if( mListener.isScrolling ) {
                  return;
            }
            super.onLayout( changed, l, t, r, b );
      }

      @Override
      public boolean dispatchTouchEvent ( MotionEvent ev ) {

            if( mExpandFoldPage.handleMotionEvent( ev ) ) {
                  return true;
            }

            return super.dispatchTouchEvent( ev );
      }

      private MonthPage getCurrentPage ( ) {

            int currentItem = getCurrentItem();
            int count = getChildCount();
            for( int i = 0; i < count; i++ ) {
                  MonthPage child = (MonthPage) getChildAt( i );
                  if( child.getPosition() == currentItem ) {
                        return child;
                  }
            }
            return null;
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
                  page.setInfo( mCalendarView.isFirstDayMonday(), date, position );

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
      private class ChangeHeightScroller extends ViewPagerScrollListener {

            /**
             * 页面是否是滚动
             */
            private boolean isScrolling;

            /**
             * 创建
             *
             * @param pager pager
             */
            private ChangeHeightScroller ( ViewPager pager ) {

                  super( pager );
            }

            @Override
            protected void onScrolled ( int state, int current, float offset, int offsetPixels ) {

                  isScrolling = ( offset != 1 ) && ( offset != -1 );

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
      }

      /**
       * 用于竖直滑动时展开折叠布局
       */
      private class ExpandFoldPage {

            private float   mDownX;
            private float   mDownY;
            private boolean isVerticalMoving;
            private boolean isHorizontalMoving;

            private boolean handleMotionEvent ( MotionEvent ev ) {

                  float x = ev.getX();
                  float y = ev.getY();
                  switch( ev.getAction() ) {
                        case MotionEvent.ACTION_DOWN:
                              mDownX = x;
                              mDownY = y;
                              break;
                        case MotionEvent.ACTION_MOVE:

                              float dx = x - mDownX;
                              float dy = y - mDownY;

                              if( !isHorizontalMoving && !isVerticalMoving ) {

                                    float aDy = Math.abs( dy );
                                    float aDx = Math.abs( dx );

                                    if( aDx > 2 * aDy ) {
                                          isHorizontalMoving = true;
                                          isVerticalMoving = false;
                                    }
                                    if( aDy > 2 * aDx ) {
                                          isVerticalMoving = true;
                                          isHorizontalMoving = false;
                                    }
                              }

                              if( isHorizontalMoving ) {
                                    return false;
                              }
                              if( isVerticalMoving ) {
                                    return true;
                              }

                              break;
                        default:
                              if( isVerticalMoving ) {
                                    isVerticalMoving = isHorizontalMoving = false;
                                    return true;
                              }
                              break;
                  }
                  return false;
            }
      }
}