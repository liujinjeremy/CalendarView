package tech.threekilogram.calendarviewlib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import tech.threekilogram.calendar.CalendarView;

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
            mCover = findViewById( R.id.cover );
            mRoot = findViewById( R.id.root );

            CalendarViewCoverBehavior<TextView> behavior = new CalendarViewCoverBehavior<>( mRoot, mCalendar, mCover );
      }
}
