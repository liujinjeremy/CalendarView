package tech.threekilogram.calendarview.month;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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
      /**
       * 页面是否是滚动
       */
      private boolean      isScroll;

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

            super.onMeasure( widthMeasureSpec, heightMeasureSpec );

            int widthSize = MeasureSpec.getSize( widthMeasureSpec );
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

            if( isScroll ) {
                  return;
            }

            super.onLayout( changed, l, t, r, b );
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
      private class VerticalMoveHelper {

            final int state_scroll  = 10;
            final int state_release = 11;

            private float mDownX;
            private float mDownY;
            private float mLastX;
            private float mLastY;

            private int mState;

            private int mTopDy;
            private int mBottomDy;

            private int mReleaseDy;

            private boolean handleMotionEvent ( MotionEvent ev ) {

                  float x = ev.getX();
                  float y = ev.getY();
                  switch( ev.getAction() ) {
                        case MotionEvent.ACTION_DOWN:
                              mDownX = x;
                              mDownY = y;
                              mLastX = x;
                              mLastY = y;
                              break;
                        case MotionEvent.ACTION_MOVE:

                              float dx = x - mLastX;
                              float dy = y - mLastY;
                              mLastX = x;
                              mLastY = y;

                              if( verticalMove( dx, dy ) ) {
                                    mState = state_scroll;
                                    calculateDy( dy );
                                    return true;
                              }

                              break;
                        default:
                              if( mState == state_scroll ) {
                                    Log.i( TAG, "handleMotionEvent: 手势释放" );
                                    mState = state_release;
                                    if( y > mDownY ) {
                                          mReleaseDy = 50;
                                    } else {
                                          mReleaseDy = -50;
                                    }
                                    requestLayout();
                                    return true;
                              }
                              break;
                  }
                  return false;
            }

            private MonthPage findCurrentItem ( ) {

                  int childCount = getChildCount();
                  int currentItem = getCurrentItem();
                  for( int i = 0; i < childCount; i++ ) {
                        MonthPage page = (MonthPage) getChildAt( i );
                        if( page.getPosition() == currentItem ) {
                              return page;
                        }
                  }
                  return null;
            }

            private boolean verticalMove ( float dx, float dy ) {

                  return Math.abs( dy ) > Math.abs( dx ) * 2;
            }

            private void calculateDy ( float dy ) {

                  MonthPage page = findCurrentItem();

                  int top = page.getTop();
                  int bottom = page.getBottom();
                  int pageMeasuredHeight = page.getMeasuredHeight();

                  View child = null;//page.getSelectedChild();
                  int childTop = child.getTop();
                  int childBottom = child.getBottom();
                  int childMeasuredHeight = child.getMeasuredHeight();

                  int topDis = childTop;
                  int bottomDis = pageMeasuredHeight - childBottom;

                  float rate = topDis * 1f / ( bottomDis + topDis );
                  mTopDy = (int) ( dy * rate );
                  mBottomDy = (int) ( dy * ( 1 - rate ) );

                  int finalTop = top + mTopDy;
                  if( finalTop > 0 ) {
                        mTopDy = -top;
                  }
                  if( finalTop < -childTop ) {
                        mTopDy = -childTop - top;
                  }

                  int finalBottom = bottom + mBottomDy;
                  if( finalBottom < childMeasuredHeight ) {
                        mBottomDy = childMeasuredHeight - bottom;
                  }
                  if( finalBottom > pageMeasuredHeight ) {
                        mBottomDy = pageMeasuredHeight - bottom;
                  }

                  getLayoutParams().height = bottom + mTopDy + mBottomDy;
                  requestLayout();
            }

            private void reLayout ( ) {

                  int childCount = getChildCount();
                  int currentItem = getCurrentItem();
                  for( int i = 0; i < childCount; i++ ) {
                        MonthPage page = (MonthPage) getChildAt( i );
                        if( page.getPosition() == currentItem ) {
                              int top = page.getTop() + mTopDy;
                              int bottom = page.getBottom() + mTopDy + mBottomDy;
                              page.layout(
                                  page.getLeft(),
                                  top,
                                  page.getRight(),
                                  bottom
                              );
                        } else {
                              page.layout(
                                  page.getLeft(),
                                  page.getTop(),
                                  page.getRight(),
                                  page.getBottom()
                              );
                        }
                  }
            }

            private boolean isLayoutToFinal ( ) {

                  int childCount = getChildCount();
                  int currentItem = getCurrentItem();
                  for( int i = 0; i < childCount; i++ ) {
                        MonthPage page = (MonthPage) getChildAt( i );
                        if( page.getPosition() == currentItem ) {
                              int top = page.getTop();
                              View selectedChild = null;//page.getSelectedChild();
                              int childTop = selectedChild.getTop();
                              int bottom = page.getBottom();
                              int height = selectedChild.getMeasuredHeight();
                              return top == -childTop && bottom == height;
                        }
                  }
                  return false;
            }

            private void reset ( ) {

                  mDownX = 0;
                  mDownY = 0;
                  mLastX = 0;
                  mLastY = 0;
                  mState = 0;

                  mTopDy = 0;
                  mBottomDy = 0;

                  mReleaseDy = 0;
            }
      }
}
