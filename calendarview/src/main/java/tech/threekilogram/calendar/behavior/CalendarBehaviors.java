package tech.threekilogram.calendar.behavior;

import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams;
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
      private RecyclerBehavior       mRecyclerBehavior;
      private RecyclerScrollListener mRecyclerScrollListener;

      private CalendarView     mCalendarView;
      private CalendarBehavior mCalendarBehavior;

      /**
       * 为{@link CoordinatorLayout}的直接子view辅助创建{@link Behavior}
       *
       * @param calendarView 直接子view之一,当recycler竖直嵌套滑动时,会响应该滑动
       * @param recyclerView 直接子view之一
       */
      public void setUp ( CalendarView calendarView, RecyclerView recyclerView ) {

            mCalendarView = calendarView;
            mRecyclerView = recyclerView;

            /* set recycler behavior */
            LayoutParams layoutParams = (LayoutParams) recyclerView.getLayoutParams();
            mRecyclerBehavior = new RecyclerBehavior();
            layoutParams.setBehavior( mRecyclerBehavior );

            /* add  OnScrollListener for recycler to get scrolled total y */
            if( mRecyclerScrollListener != null ) {
                  recyclerView.removeOnScrollListener( mRecyclerScrollListener );
            } else {
                  mRecyclerScrollListener = new RecyclerScrollListener();
            }
            recyclerView.addOnScrollListener( mRecyclerScrollListener );

            /* calendar set behavior */
            layoutParams = ( (LayoutParams) calendarView.getLayoutParams() );
            mCalendarBehavior = new CalendarBehavior();
            layoutParams.setBehavior( mCalendarBehavior );
      }

      /**
       * {@link #mRecyclerView}的{@link Behavior}
       */
      private class RecyclerBehavior extends Behavior<RecyclerView> {

            @Override
            public boolean layoutDependsOn (
                @NonNull CoordinatorLayout parent, @NonNull RecyclerView child, @NonNull View dependency ) {

                  return dependency == mCalendarView;
            }

            @Override
            public boolean onDependentViewChanged (
                @NonNull CoordinatorLayout parent, @NonNull RecyclerView child, @NonNull View dependency ) {

                  int bottom = mCalendarView.getBottom();
                  MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                  float y = child.getY();

                  if( y - layoutParams.topMargin != bottom ) {
                        child.setY( bottom + layoutParams.topMargin );
                        return true;
                  }
                  return super.onDependentViewChanged( parent, child, dependency );
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

      /**
       * {@link #mCalendarView}的behavior
       */
      private class CalendarBehavior extends Behavior<CalendarView> {

            private float mLastDy = -1;

            @Override
            public boolean onStartNestedScroll (
                @NonNull CoordinatorLayout coordinatorLayout,
                @NonNull CalendarView child,
                @NonNull View directTargetChild,
                @NonNull View target,
                int axes, int type ) {

                  /* 当竖直滑动时捕获滑动 */
                  return true;
            }

            @Override
            public void onNestedScrollAccepted (
                @NonNull CoordinatorLayout coordinatorLayout, @NonNull CalendarView child, @NonNull View directTargetChild,
                @NonNull View target, int axes, int type ) {

            }

            @Override
            public void onNestedPreScroll (
                @NonNull CoordinatorLayout coordinatorLayout, @NonNull CalendarView child, @NonNull View target, int dx, int dy,
                @NonNull int[] consumed, int type ) {

            }

            @Override
            public void onStopNestedScroll (
                @NonNull CoordinatorLayout coordinatorLayout, @NonNull CalendarView child, @NonNull View target, int type ) {

            }

            private MonthPage getCurrentPage ( CalendarView child ) {

                  return child.getMonthLayout().getCurrentPage();
            }
      }
}
