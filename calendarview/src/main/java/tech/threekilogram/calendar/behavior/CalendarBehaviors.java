package tech.threekilogram.calendar.behavior;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import tech.threekilogram.calendar.CalendarView;
import tech.threekilogram.calendar.month.MonthPage;

/**
 * @author Liujin 2019/3/11:15:37:33
 */
public class CalendarBehaviors {

      private static final String TAG = CalendarBehaviors.class.getSimpleName();

      private RecyclerView           mRecyclerView;
      private CalendarView           mCalendarView;
      private RecyclerBehavior       mRecyclerBehavior;
      private CalendarBehavior       mCalendarBehavior;
      private RecyclerScrollListener mScrollListener;

      /**
       * 为{@link CoordinatorLayout}的直接子view辅助创建{@link Behavior}
       *
       * @param calendarView 直接子view之一,当recycler竖直嵌套滑动时,会响应该滑动
       * @param recyclerView 直接子view之一
       */
      public void setUp ( CalendarView calendarView, RecyclerView recyclerView ) {

            mCalendarView = calendarView;
            mCalendarView.setBackgroundColor( Color.LTGRAY );
            mRecyclerView = recyclerView;

            /* set recycler behavior */
            LayoutParams layoutParams = (LayoutParams) recyclerView.getLayoutParams();
            mRecyclerBehavior = new RecyclerBehavior();
            layoutParams.setBehavior( mRecyclerBehavior );

            /* calendar set behavior */
            layoutParams = ( (LayoutParams) calendarView.getLayoutParams() );
            mCalendarBehavior = new CalendarBehavior();
            layoutParams.setBehavior( mCalendarBehavior );

            if( mScrollListener != null ) {
                  mRecyclerView.removeOnScrollListener( mScrollListener );
            } else {
                  mScrollListener = new RecyclerScrollListener();
            }
            mRecyclerView.addOnScrollListener( mScrollListener );
      }

      /**
       * {@link #mRecyclerView}的{@link Behavior}
       */
      private class RecyclerBehavior extends Behavior<RecyclerView> {

            @Override
            public boolean onMeasureChild (
                @NonNull CoordinatorLayout parent, @NonNull RecyclerView child, int parentWidthMeasureSpec, int widthUsed,
                int parentHeightMeasureSpec, int heightUsed ) {

                  MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                  int width;
                  if( layoutParams.width > 0 ) {
                        width = layoutParams.width;
                  } else {
                        width = MeasureSpec.getSize( parentWidthMeasureSpec );
                  }
                  int height;
                  if( layoutParams.height > 0 ) {
                        height = layoutParams.height;
                  } else {
                        int topMargin = ( (MarginLayoutParams) mCalendarView.getLayoutParams() ).topMargin;
                        height = MeasureSpec.getSize( parentHeightMeasureSpec ) - mCalendarView.getMinimumHeight() - topMargin;
                  }

                  int childWidthSpec = MeasureSpec
                      .makeMeasureSpec( width - layoutParams.leftMargin - layoutParams.rightMargin, MeasureSpec.EXACTLY );
                  int childHeightSpec = MeasureSpec
                      .makeMeasureSpec( height - layoutParams.topMargin - layoutParams.bottomMargin, MeasureSpec.EXACTLY );

                  child.measure( childWidthSpec, childHeightSpec );
                  return true;
            }

            @Override
            public boolean onLayoutChild ( @NonNull CoordinatorLayout parent, @NonNull RecyclerView child, int layoutDirection ) {

                  MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();

                  int l = layoutParams.leftMargin;
                  int t = mCalendarView.getBottom() + ( (MarginLayoutParams) mCalendarView.getLayoutParams() ).topMargin;
                  ;
                  int r = l + child.getMeasuredWidth();
                  int b = t + child.getMeasuredHeight();

                  child.layout( l, t, r, b );
                  return true;
            }

            @Override
            public boolean onStartNestedScroll (
                @NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View directTargetChild,
                @NonNull View target, int axes, int type ) {

                  return ( axes & ViewCompat.SCROLL_AXIS_VERTICAL ) != 0;
            }

            @Override
            public void onNestedPreScroll (
                @NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View target, int dx, int dy,
                @NonNull int[] consumed, int type ) {

                  if( mScrollListener.mMoved != 0 ) {
                        return;
                  }

                  MonthPage currentPage = mCalendarView.getMonthLayout().getCurrentPage();
                  if( currentPage.calculateMovedBy( -dy ) ) {
                        int left = mCalendarView.getLeft();
                        int top = mCalendarView.getTop();
                        int right = mCalendarView.getRight();
                        int weekBarHeight = mCalendarView.getWeekBar().getMeasuredHeight();
                        int pageHeight = currentPage.getMovedMeasureHeight();

                        mCalendarView.layout( left, top, right, top + weekBarHeight + pageHeight );
                        currentPage.layout(
                            currentPage.getLeft(),
                            currentPage.getTop(),
                            currentPage.getRight(),
                            currentPage.getTop() + currentPage.getMovedMeasureHeight()
                        );

                        mRecyclerView.layout(
                            mRecyclerView.getLeft(),
                            mCalendarView.getBottom(),
                            mRecyclerView.getRight(),
                            mCalendarView.getBottom() + mRecyclerView.getMeasuredHeight()
                        );

                        consumed[ 1 ] = dy;
                  }
            }

            @Override
            public boolean onNestedPreFling (
                @NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View target, float velocityX,
                float velocityY ) {

                  Log.i( TAG, "onNestedPreFling: " );
                  return super.onNestedPreFling( coordinatorLayout, child, target, velocityX, velocityY );
            }

            @Override
            public void onStopNestedScroll (
                @NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View target, int type ) {

                  Log.i( TAG, "onStopNestedScroll: " );
                  super.onStopNestedScroll( coordinatorLayout, child, target, type );
            }
      }

      /**
       * {@link #mCalendarView}的behavior
       */
      private class CalendarBehavior extends Behavior<CalendarView> {

            @Override
            public boolean onMeasureChild (
                @NonNull CoordinatorLayout parent, @NonNull CalendarView child, int parentWidthMeasureSpec, int widthUsed,
                int parentHeightMeasureSpec, int heightUsed ) {

                  MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                  int width;
                  if( layoutParams.width > 0 ) {
                        width = layoutParams.width;
                  } else {
                        width = MeasureSpec.getSize( parentWidthMeasureSpec );
                  }
                  int height;
                  if( layoutParams.height > 0 ) {
                        height = layoutParams.height;
                  } else {
                        height = MeasureSpec.getSize( parentHeightMeasureSpec );
                  }

                  int childWidthSpec = MeasureSpec
                      .makeMeasureSpec( width - layoutParams.leftMargin - layoutParams.rightMargin, MeasureSpec.EXACTLY );
                  int childHeightSpec = MeasureSpec
                      .makeMeasureSpec( height - layoutParams.topMargin - layoutParams.bottomMargin, MeasureSpec.EXACTLY );

                  child.measure( childWidthSpec, childHeightSpec );
                  return true;
            }

            @Override
            public boolean onLayoutChild ( @NonNull CoordinatorLayout parent, @NonNull CalendarView child, int layoutDirection ) {

                  MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                  child.layout( layoutParams.leftMargin, layoutParams.topMargin,
                                layoutParams.leftMargin + child.getMeasuredWidth(),
                                layoutParams.topMargin + child.getMeasuredHeight()
                  );
                  return true;
            }
      }

      /**
       * {@link #mRecyclerView}的滚动监听,用于记录一共滚动了多少距离
       */
      private class RecyclerScrollListener extends OnScrollListener {

            private int mState;
            private int mMoved;

            @Override
            public void onScrollStateChanged ( @NonNull RecyclerView recyclerView, int newState ) {

                  mState = newState;
            }

            @Override
            public void onScrolled ( @NonNull RecyclerView recyclerView, int dx, int dy ) {

                  mMoved += dy;
            }
      }
}
