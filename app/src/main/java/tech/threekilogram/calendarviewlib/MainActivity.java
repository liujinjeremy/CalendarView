package tech.threekilogram.calendarviewlib;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import tech.threekilogram.calendarview.CalendarView;

public class MainActivity extends AppCompatActivity {

      private static final String TAG = MainActivity.class.getSimpleName();

      private TextView     mTitle;
      private CalendarView mCalendarView;
      private FrameLayout  mRoot;

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_main );
            initView();
      }

      private void initView ( ) {

            mRoot = findViewById( R.id.root );
            mTitle = findViewById( R.id.title );
            mCalendarView = findViewById( R.id.calendarView );
            mCalendarView.setFirstDayMonday( false );
      }
}
