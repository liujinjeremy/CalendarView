package tech.threekilogram.calendarviewlib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import tech.threekilogram.calendarview.CalendarView;

public class FunctionActivity extends AppCompatActivity {

      private static final String TAG = FunctionActivity.class.getSimpleName();

      private CalendarView mCalendar;

      public static void start ( Context context ) {

            Intent starter = new Intent( context, FunctionActivity.class );
            context.startActivity( starter );
      }

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_function );
            initView();
      }

      private void initView ( ) {

            mCalendar = findViewById( R.id.calendar );
            mCalendar.setBackgroundColor( Color.LTGRAY );
      }

      public void changeWeekStart ( View view ) {

            mCalendar.setFirstDayMonday( !mCalendar.isFirstDayMonday() );
      }
}
