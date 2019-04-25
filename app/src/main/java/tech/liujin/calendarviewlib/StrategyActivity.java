package tech.liujin.calendarviewlib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import tech.liujin.calendar.CalendarView;
import tech.liujin.calendar.month.MonthLayout;

public class StrategyActivity extends AppCompatActivity {

      private CalendarView      mCalendar;
      private TextView          mCover;
      private CoordinatorLayout mRoot;

      public static void start ( Context context ) {

            Intent starter = new Intent( context, StrategyActivity.class );
            context.startActivity( starter );
      }

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_strategy );
            initView();
      }

      private void initView ( ) {

            mCalendar = findViewById( R.id.calendar );
            mCalendar.setBackgroundColor( Color.LTGRAY );

            MonthLayout monthLayout = mCalendar.getMonthLayout();
      }
}
