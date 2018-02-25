package dk.easj.anbo.restjsonserviceexample;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ReadJSONFeedTask task = new ReadJSONFeedTask();
        // task.execute("http://extjs.org.cn/extjs/examples/grid/survey.html");
        task.execute("http://date.jsontest.com/");
        // from http://www.jsontest.com/#date
    }

    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return readJSonFeed(urls[0]);
            } catch (IOException ex) {
                Log.e("SHIT", ex.toString());
                cancel(true);
                return ex.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            final TextView textView = findViewById(R.id.mainResultTextView);
            try {
                JSONObject jsonObject = new JSONObject(result);
                final String date = jsonObject.getString("date");
                final String time = jsonObject.getString("time");
                textView.append(date + ": " + time + "\n");

                long msec = jsonObject.getLong("milliseconds_since_epoch");
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(msec);
                DateFormat formatter = DateFormat.getDateTimeInstance();
                String dateString = formatter.format(cal.getTime());
                formatter.format(msec);
                textView.append(dateString);
            } catch (JSONException ex) {
                textView.append(ex.toString());
            }
        }

        @Override
        protected void onCancelled(String message) {
            super.onCancelled(message);
            final TextView textView = findViewById(R.id.mainResultTextView);
            textView.setText(message);
        }
    }

    private String readJSonFeed(String urlString) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        final InputStream content = openHttpConnection(urlString);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        while (true) {
            final String line = reader.readLine();
            if (line == null)
                break;
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    private InputStream openHttpConnection(final String urlString)
            throws IOException {
        final URL url = new URL(urlString);
        final URLConnection conn = url.openConnection();
        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        final HttpURLConnection httpConn = (HttpURLConnection) conn;
        httpConn.setAllowUserInteraction(false);
        // No user interaction like dialog boxes, etc.
        httpConn.setInstanceFollowRedirects(true);
        // follow redirects, response code 3xx
        httpConn.setRequestMethod("GET");
        httpConn.connect();
        final int response = httpConn.getResponseCode();
        if (response == HttpURLConnection.HTTP_OK) {
            return httpConn.getInputStream();
        } else {
            throw new IOException("HTTP response not OK");
        }
    }
}
