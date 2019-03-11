package tech.threekilogram.calendarviewlib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Locale;
import tech.threekilogram.calendar.CalendarView;

public class BehaviorTestActivity extends AppCompatActivity {

      private CalendarView mCalendar;
      private RecyclerView mRecycler;

      public static void start ( Context context ) {

            Intent starter = new Intent( context, BehaviorTestActivity.class );
            context.startActivity( starter );
      }

      @Override
      protected void onCreate ( Bundle savedInstanceState ) {

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_behavior_test );
            initView();
      }

      private void initView ( ) {

            mCalendar = findViewById( R.id.calendar );
            mRecycler = findViewById( R.id.recycler );
            mRecycler.setLayoutManager( new LinearLayoutManager( this ) );
            mRecycler.setAdapter( new Adapter() );
      }

      private class Holder extends RecyclerView.ViewHolder {

            TextView mTextView;

            private Holder ( @NonNull View itemView ) {

                  super( itemView );
                  mTextView = itemView.findViewById( R.id.text );
            }

            private void bind ( int position ) {

                  mTextView.setText( String.format( Locale.CHINA, "item %d", position ) );
            }
      }

      private class Adapter extends RecyclerView.Adapter<Holder> {

            @NonNull
            @Override
            public Holder onCreateViewHolder ( @NonNull ViewGroup parent, int viewType ) {

                  View view = getLayoutInflater().inflate( R.layout.item_recycler, parent, false );
                  return new Holder( view );
            }

            @Override
            public void onBindViewHolder ( @NonNull Holder holder, int position ) {

                  holder.bind( position );
            }

            @Override
            public int getItemCount ( ) {

                  return 20;
            }
      }
}
