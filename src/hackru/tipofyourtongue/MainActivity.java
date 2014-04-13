package hackru.tipofyourtongue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

public class MainActivity extends Activity {

	private static final int SPEECH_REQUEST = 0;
	private ArrayList<Card> cards;
	private CardScrollView cardScrollView;
	private CardScrollViewAdapter cardScrollAdapter;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		cards = new ArrayList<Card>();
		cardScrollAdapter = new CardScrollViewAdapter();
		cardScrollView = new CardScrollView(this);
		cardScrollView.setAdapter(cardScrollAdapter);
		cardScrollView.activate();

		// Starts up a speech request
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		startActivityForResult(intent, SPEECH_REQUEST);

		setContentView(cardScrollView);
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
	
	private class CardScrollViewAdapter extends CardScrollAdapter {
		
		@Override
		public int findIdPosition(Object id) {
			return -1;
		}

		@Override
		public int findItemPosition(Object item) {
			return cards.indexOf(item);
		}

		@Override
		public int getCount() {
			return cards.size();
		}

		@Override
		public Object getItem(int position) {
			return cards.get(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return cards.get(position).toView();
		}
		
	}

	private class ReverseDictionaryTask extends AsyncTask<String, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(String... input) {
			LinkedHashMap<String, String> words = new LinkedHashMap<String, String>();
			JSONObject json;
			try {
				json = readJsonFromUrl("http://api.wordnik.com:80/v4/words.json/reverseDictionary?query="+input[0]+"&minCorpusCount=5&maxCorpusCount=-1&minLength=1&maxLength=-1&includeTags=false&skip=0&limit=10&api_key=a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5");
				
				int numOfResults = json.getInt("totalResults");
				for (int i = 0; i < numOfResults; i++){
					words.put(
						json.getJSONArray("results").getJSONObject(i).getString("word"),
						json.getJSONArray("results").getJSONObject(i).getString("text")
					);
				}
			} catch (Exception e) {
				Log.e("TipOfYourTongue", e.getMessage());
				Log.e("TipOfYourTongue", e.getStackTrace().toString());
			}
			
			return words;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			// Sets the card text
			if(result.entrySet().size() > 0) {
				for(Entry<String, String> entry : result.entrySet()) {
					Card card = new Card(context);
					card.setText(entry.getKey());
					card.setFootnote(entry.getValue().replaceAll("<strong>", "").replaceAll("/strong>", ": "));
					
					cards.add(card);
					cardScrollAdapter.notifyDataSetChanged();
				}
			} else {
				Card card = new Card(context);
				card.setText("Could not find any words");
				
				cards.add(card);
				cardScrollAdapter.notifyDataSetChanged();
			}
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