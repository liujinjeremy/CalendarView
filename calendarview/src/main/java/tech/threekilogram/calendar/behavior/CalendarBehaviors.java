package tech.threekilogram.calendar.behavior;

import android.graphics.Color;
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
import tech.threekilogram.calendar.month.MonthLayout;
import tech.threekilogram.calendar.month.MonthLayout.PageHeightChangeStrategy;

/**
 * @author Liujin 2019/3/11:15:37:33
 */
public class CalendarBehaviors {

      private static final String TAG = CalendarBehaviors.class.getSimpleName();

      private CalendarView           mCalendarView;
      private MonthLayout            mMonthLayout;
      private RecyclerView           mRecyclerView;
      private RecyclerScrollListener mScrollListener;
      private CalendarBehavior       mCalendarBehavior;
      private RecyclerBehavior       mRecyclerBehavior;

      private int mCalendarBottomMargin;
      private int mRecyclerTopMargin;

      private boolean mFlagMeasure;
      private boolean mFlagLayout;

      /**
       * 为{@link CoordinatorLayout}的直接子view辅助创建{@link Behavior}
       *
       * @param calendarView 直接子view之一,当recycler竖直嵌套滑动时,会响应该滑动
       * @param recyclerView 直接子view之一
       */
      public void setUpWith ( CalendarView calendarView, RecyclerView recyclerView ) {

            mCalendarView = calendarView;
            mCalendarView.setBackgroundColor( Color.LTGRAY );
            mRecyclerView = recyclerView;


            /* calendar set behavior */
            LayoutParams layoutParams = ( (LayoutParams) calendarView.getLayoutParams() );
            mCalendarBehavior = new CalendarBehavior();
            layoutParams.setBehavior( mCalendarBehavior );

            /* set recycler behavior */
            layoutParams = (LayoutParams) recyclerView.getLayoutParams();
            mRecyclerBehavior = new RecyclerBehavior();
            layoutParams.setBehavior( mRecyclerBehavior );


            /* monitor recycler */
            if( mScrollListener != null ) {
                  mRecyclerView.removeOnScrollListener( mScrollListener );
            } else {
                  mScrollListener = new RecyclerScrollListener();
            }
            mRecyclerView.addOnScrollListener( mScrollListener );

            /* recycler follow calendar */
            mMonthLayout = mCalendarView.getMonthLayout();
            mMonthLayout.setPageHeightChangeStrategy( new HeightChangeStrategy() );
      }

      private void measureRecycler ( int parentWidthMeasureSpec, int parentHeightMeasureSpec ) {

            MarginLayoutParams layoutParams = (MarginLayoutParams) mRecyclerView.getLayoutParams();
            mRecyclerTopMargin = layoutParams.topMargin;

            int width;
            if( layoutParams.width > 0 ) {
                  width = layoutParams.width;
            } else {
                  width = MeasureSpec.getSize( parentWidthMeasureSpec )
                      - layoutParams.leftMargin - layoutParams.rightMargin;
            }
            int height;
            if( layoutParams.height > 0 ) {
                  height = layoutParams.height;
            } else {
                  MarginLayoutParams params = (MarginLayoutParams) mCalendarView.getLayoutParams();
                  int margin = params.topMargin + params.bottomMargin + layoutParams.topMargin + layoutParams.bottomMargin;
                  height = MeasureSpec.getSize( parentHeightMeasureSpec ) - mCalendarView.getMinimumHeight() - margin;
            }

            int childWidthSpec = MeasureSpec
                .makeMeasureSpec( width, MeasureSpec.EXACTLY );
            int childHeightSpec = MeasureSpec
                .makeMeasureSpec( height, MeasureSpec.EXACTLY );

            mRecyclerView.measure( childWidthSpec, childHeightSpec );
      }

      private void measureCalendar ( int parentWidthMeasureSpec, int parentHeightMeasureSpec ) {

            MarginLayoutParams layoutParams = (MarginLayoutParams) mCalendarView.getLayoutParams();
            mCalendarBottomMargin = layoutParams.bottomMargin;

            int width;
            if( layoutParams.width > 0 ) {
                  width = layoutParams.width;
            } else {
                  width =
                      MeasureSpec.getSize( parentWidthMeasureSpec ) - layoutParams.leftMargin - layoutParams.rightMargin;
            }
            int height;
            if( layoutParams.height > 0 ) {
                  height = layoutParams.height;
            } else {
                  height =
                      MeasureSpec.getSize( parentHeightMeasureSpec ) - layoutParams.topMargin - layoutParams.bottomMargin;
            }

            int childWidthSpec = MeasureSpec
                .makeMeasureSpec( width, MeasureSpec.EXACTLY );
            int childHeightSpec = MeasureSpec
                .makeMeasureSpec( height, MeasureSpec.AT_MOST );

            mCalendarView.measure( childWidthSpec, childHeightSpec );
      }

      private void layoutRecycler ( ) {

            MarginLayoutParams layoutParams = (MarginLayoutParams) mRecyclerView.getLayoutParams();

            int l = layoutParams.leftMargin;
            int t = mCalendarView.getBottom()
                + ( (MarginLayoutParams) mCalendarView.getLayoutParams() ).bottomMargin
                + layoutParams.topMargin;
            int r = l + mRecyclerView.getMeasuredWidth();
            int b = t + mRecyclerView.getMeasuredHeight();

            mRecyclerView.layout( l, t, r, b );
      }

      private void layoutCalendar ( ) {

            MarginLayoutParams layoutParams = (MarginLayoutParams) mCalendarView.getLayoutParams();
            mCalendarView.layout(
                layoutParams.leftMargin,
                layoutParams.topMargin,
                layoutParams.leftMargin + mCalendarView.getMeasuredWidth(),
                layoutParams.topMargin + mCalendarView.getMeasuredHeight()
            );
      }

      private void onMeasure ( int parentWidthMeasureSpec, int parentHeightMeasureSpec ) {

            if( !mFlagMeasure ) {
                  mFlagMeasure = true;
                  measureCalendar( parentWidthMeasureSpec, parentHeightMeasureSpec );
                  measureRecycler( parentWidthMeasureSpec, parentHeightMeasureSpec );
            }
      }

      private void onLayout ( ) {

            if( !mFlagLayout ) {
                  mFlagLayout = true;
                  layoutCalendar();
                  layoutRecycler();
            }
      }

      /**
       * {@link #mRecyclerView}的{@link Behavior}
       */
      private class RecyclerBehavior extends Behavior<RecyclerView> {

            @Override
            public boolean onMeasureChild (
                @NonNull CoordinatorLayout parent, @NonNull RecyclerView child, int parentWidthMeasureSpec, int widthUsed,
                int parentHeightMeasureSpec, int heightUsed ) {

                  onMeasure( parentWidthMeasureSpec, parentHeightMeasureSpec );
                  return true;
            }

            @Override
            public boolean onLayoutChild ( @NonNull CoordinatorLayout parent, @NonNull RecyclerView child, int layoutDirection ) {

                  onLayout();
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

                  if( mMonthLayout.isScrolling() ) {
                        return;
                  }

                  if( mMonthLayout.dispatchMoveToCurrentPage( -dy ) ) {
                        consumed[ 1 ] = dy;
                  }
            }

            @Override
            public boolean onNestedPreFling (
                @NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View target, float velocityX,
                float velocityY ) {

                  return super.onNestedPreFling( coordinatorLayout, child, target, velocityX, velocityY );
            }

            @Override
            public void onStopNestedScroll (
                @NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View target, int type ) {

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

                  onMeasure( parentWidthMeasureSpec, parentHeightMeasureSpec );
                  return true;
            }

            @Override
            public boolean onLayoutChild ( @NonNull CoordinatorLayout parent, @NonNull CalendarView child, int layoutDirection ) {

                  onLayout();
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

      private class HeightChangeStrategy implements PageHeightChangeStrategy {

            @Override
            public void onHeightChanging ( int currentHeight, int which ) {

                  mMonthLayout.reLayoutToPageHeight( currentHeight );
                  int bottom = mCalendarView.getBottom();
                  mRecyclerView.setY( bottom + mCalendarBottomMargin + mRecyclerTopMargin );
            }

            @Override
            public void onScrollFinished ( ) {

                  int bottom = mCalendarView.getBottom();
                  int y = bottom + mCalendarBottomMargin + mRecyclerTopMargin;
                  mRecyclerView.setY( y );
            }

            @Override
            public void onExpanded ( ) { }

            @Override
            public void onFolded ( ) { }
      }
}
