package hackru.tipofyourtongue;

import java.util.List;

import me.pills.R;

import com.google.android.glass.app.Card;
import com.google.android.glass.timeline.TimelineManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;

public class MainActivity extends Activity {
	
	private static final int SPEECH_REQUEST = 0;
	private Card card = new Card(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        startActivityForResult(intent, SPEECH_REQUEST);
        
        setContentView(card.toView());
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText.
            card.setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}