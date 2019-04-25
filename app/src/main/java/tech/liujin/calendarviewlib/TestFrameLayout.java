package tech.liujin.calendarviewlib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Liujin 2019/2/21:13:21:31
 */
public class TestFrameLayout extends FrameLayout {

      private static final String TAG = TestFrameLayout.class.getSimpleName();

      public TestFrameLayout ( @NonNull Context context ) {

            super( context );
      }

      public TestFrameLayout ( @NonNull Context context, @Nullable AttributeSet attrs ) {

            super( context, attrs );
      }

      public TestFrameLayout ( @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {

            super( context, attrs, defStyleAttr );
      }

      @Override
      protected void onLayout ( boolean changed, int left, int top, int right, int bottom ) {

            super.onLayout( changed, left, top, right, bottom );
            Log.i( TAG, "onLayout: " + changed + " " + left + " " + top + " " + right + " " + bottom );
      }
}
