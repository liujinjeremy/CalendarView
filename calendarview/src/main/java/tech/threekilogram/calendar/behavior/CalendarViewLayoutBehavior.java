package tech.threekilogram.calendar.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior;
import tech.threekilogram.calendar.CalendarView;

/**
 * @author Liujin 2019/3/11:11:17:22
 */
public class CalendarViewLayoutBehavior extends Behavior {

      private static final String TAG = CalendarViewLayoutBehavior.class.getSimpleName();

      private CalendarView mDependency;

      public CalendarViewLayoutBehavior ( ) { }

      public CalendarViewLayoutBehavior ( Context context, AttributeSet attrs ) {

            super( context, attrs );
      }

      @Override
      public boolean layoutDependsOn ( @NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency ) {

            Log.i( TAG, "layoutDependsOn: " );
            boolean b = dependency instanceof CalendarView;
            if( b ) {
                  mDependency = (CalendarView) dependency;
            }
            return b;
      }

      @Override
      public boolean onDependentViewChanged (
          @NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency ) {

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
