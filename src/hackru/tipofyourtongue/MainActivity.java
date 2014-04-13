package hackru.tipofyourtongue;

import java.util.List;

import com.google.android.glass.app.Card;
import com.google.android.glass.timeline.TimelineManager;
import com.temboo.Library.Wordnik.Words.ReverseDictionary;
import com.temboo.Library.Wordnik.Words.ReverseDictionary.ReverseDictionaryInputSet;
import com.temboo.Library.Wordnik.Words.ReverseDictionary.ReverseDictionaryResultSet;
import com.temboo.core.TembooException;
import com.temboo.core.TembooSession;

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
        
        // Starts up a speech request
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        startActivityForResult(intent, SPEECH_REQUEST);
        
        setContentView(card.toView());
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
        	// Gets the spoken text
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            
            // Gets the reverse dictionary output
            TembooSession session;
			try {
				// Instantiate the Choreo, using a previously instantiated TembooSession object
				session = new TembooSession("RichardChu", "Tip Of Your Tongue", "f00de67c02a503b0850080e3e090b9e53bb68fa621377b3dd");
				ReverseDictionary reverseDictionaryChoreo = new ReverseDictionary(session);
				
				// Get an InputSet object for the choreo
				ReverseDictionaryInputSet reverseDictionaryInputs = reverseDictionaryChoreo.newInputSet();
				
				// Set inputs
				reverseDictionaryInputs.set_APIKey("f00de67c02a503b0850080e3e090b9e53bb68fa621377b3dd");
				reverseDictionaryInputs.set_Query(spokenText);
				
				// Execute Choreo
				ReverseDictionaryResultSet reverseDictionaryResults = reverseDictionaryChoreo.execute(reverseDictionaryInputs);
				
				// Sets the card text
				card.setText(reverseDictionaryResults.toString());
			} catch (TembooException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}