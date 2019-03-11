package tech.threekilogram.calendar.behavior;

import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams;
import androidx.recyclerview.widget.RecyclerView;
import tech.threekilogram.calendar.CalendarView;

/**
 * @author Liujin 2019/3/11:15:37:33
 */
public class CalendarBehaviors {

      private CalendarView     mCalendarView;
      private RecyclerView     mRecyclerView;
      private RecyclerBehavior mRecyclerBehavior;

      public void setUp ( CalendarView calendarView, RecyclerView recyclerView ) {

            LayoutParams layoutParams = (LayoutParams) recyclerView.getLayoutParams();
            mRecyclerBehavior = new RecyclerBehavior();
            layoutParams.setBehavior( mRecyclerBehavior );
      }

      private class RecyclerBehavior extends Behavior<RecyclerView> {

            @Override
            public boolean layoutDependsOn (
                @NonNull CoordinatorLayout parent, @NonNull RecyclerView child, @NonNull View dependency ) {

                  return dependency == mCalendarView;
            }

            @Override
            public boolean onDependentViewChanged (
                @NonNull CoordinatorLayout parent, @NonNull RecyclerView child, @NonNull View dependency ) {

                  int bottom = dependency.getBottom();
                  int top = child.getTop();
                  MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                  if( top - layoutParams.topMargin != bottom ) {
                        child.setY( bottom + layoutParams.topMargin );
                        return true;
                  }
                  return super.onDependentViewChanged( parent, child, dependency );
            }
      }
}
