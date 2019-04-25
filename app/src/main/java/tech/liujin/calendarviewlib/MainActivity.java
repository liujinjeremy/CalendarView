package tech.liujin.calendarviewlib;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

      private static final String TAG = MainActivity.class.getSimpleName();

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_main );
      }

      public void functionTest ( View view ) {

            FunctionActivity.start( this );
      }

      public void behavior ( View view ) {

            BehaviorTestActivity.start( this );
      }

      public void strategy ( View view ) {

            StrategyActivity.start( this );
      }
}
