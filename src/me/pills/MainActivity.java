package me.pills;

import me.pills.R;
import com.google.android.glass.app.Card;
import com.google.android.glass.timeline.TimelineManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Card card = new Card(this);
        card.setText("Meds");
        card.setFootnote("Take yo pills");
        card.addImage(R.drawable.peels);
        
        setContentView(card.toView());
    }
}
