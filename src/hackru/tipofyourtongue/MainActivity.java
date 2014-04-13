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
import android.util.Log;
import android.view.LayoutInflater;

public class MainActivity extends Activity {

	private boolean mReturningWithResult = false;
	private String text;
	private Intent data;

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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
			this.data = data;
			mReturningWithResult = true;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void onPostResume() {
		super.onPostResume();

		if (mReturningWithResult) {
			// Gets the spoken text
			List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String spokenText = results.get(0);
			text = spokenText;

			// Gets the reverse dictionary output
			TembooSession session = null;
			ReverseDictionaryResultSet reverseDictionaryResults = null;
			try {
				session = new TembooSession("RichardChu", "GlassApp",
						"f00de67c02a503b0850080e3e090b9e53bb68fa621377b3dd");

				// Instantiate the Choreo, using a previously instantiated
				// TembooSession object
				ReverseDictionary reverseDictionaryChoreo = new ReverseDictionary(
						session);

				// Get an InputSet object for the choreo
				ReverseDictionaryInputSet reverseDictionaryInputs = reverseDictionaryChoreo
						.newInputSet();

				// Set inputs
				reverseDictionaryInputs
						.set_APIKey("f00de67c02a503b0850080e3e090b9e53bb68fa621377b3dd");
				reverseDictionaryInputs.set_Query(text);

				// Execute Choreo
				reverseDictionaryResults = reverseDictionaryChoreo
						.execute(reverseDictionaryInputs);

				// Sets the card text
				card.setText(reverseDictionaryResults.get_Response());
			} catch (Exception e) {
				Log.e("ERROR", e.getMessage());
				card.setText("Doesn't work");
			}
		}
		mReturningWithResult = false;
	}
}