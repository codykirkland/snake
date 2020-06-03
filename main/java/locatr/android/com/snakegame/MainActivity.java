package locatr.android.com.snakegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
/*
this is the  start screen of the game
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    /**
     * this method will start the next activity.
     * @param view
     */
    public void play(View view){
        //starts the next activity
        startActivity(new Intent(this,snake.class));

    }
}
