package hackru.tipofyourtongue;

import java.util.List;

import com.google.android.glass.app.Card;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

	private static final int SPEECH_REQUEST = 0;
	private Card card;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		card = new Card(this);

		// Starts up a speech request
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		startActivityForResult(intent, SPEECH_REQUEST);

		setContentView(card.toView());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
			// Gets the spoken text
			List<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String spokenText = results.get(0);
			spokenText = spokenText.replaceAll("\\s+","%20");
			new ReverseDictionaryTask().execute(spokenText);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class ReverseDictionaryTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... input) {
			String result = "Didn't work";
			JSONObject json;
			try {
				json = readJsonFromUrl("http://api.wordnik.com:80/v4/words.json/reverseDictionary?query="+input[0]+"&minCorpusCount=5&maxCorpusCount=-1&minLength=1&maxLength=-1&includeTags=false&skip=0&limit=10&api_key=a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5");
				result = json.getJSONArray("results").getJSONObject(0).getString("word");
			} catch (Exception e) {
				Log.e("TipOfYourTongue", e.getMessage());
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// Sets the card text
			card.setText(result);	// Only gets first result; implement scrolling cards later
			setContentView(card.toView());
		}
		
		private String readAll(Reader rd) throws IOException {
		    StringBuilder sb = new StringBuilder();
		    int cp;
		    while ((cp = rd.read()) != -1) {
		      sb.append((char) cp);
		    }
		    return sb.toString();
		  }
		
		public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		    InputStream is = new URL(url).openStream();
		    try {
		      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		      String jsonText = readAll(rd);
		      JSONObject json = new JSONObject(jsonText);
		      return json;
		    } finally {
		      is.close();
		    }
		  }
	}
}