package com.example.alex.rap_homie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Editable notepad for creating song lyrics
 */
public class NotepadActivity extends AppCompatActivity {
    EditText songText;
    EditText titleText;
    String myResponse;
    JSONArray jsonArray;
    ArrayList<String> rhymeList;
    RhymeListAdaptor rhymeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notepad_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String sessionId = getIntent().getStringExtra("SONG_TITLE_SELECTED");
        titleText = (EditText) findViewById(R.id.titleText);
        titleText.setText(sessionId ,TextView.BufferType.EDITABLE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Save(titleText.getText().toString()));

        final Button button = findViewById(R.id.rhyme_button);
        button.setOnClickListener(v -> {
            //onButtonShowPopupWindowClick(v, "egg"); // FIXME - Remove
        });

        songText = (EditText) findViewById(R.id.EditText1);
        songText.setText(Open(titleText.getText().toString()));

        //Long click triggers rhymes (double tap would be nicer)
        songText.setOnLongClickListener(v -> {
            int selectionStart = songText.getSelectionStart();
            int selectionEnd = songText.getSelectionEnd();

            String rhymeString = songText.getText().toString().substring(selectionStart, selectionEnd);
            try {
                onButtonShowPopupWindowClick(v, rhymeString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onButtonShowPopupWindowClick(View view, String rhymeString) throws JSONException {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // FIXME Update layout to a fixed size
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        ListView listView = popupView.findViewById(R.id.rhymeListView);

        // Add Rhyme word buttons
        jsonArray = getRhymeWords(rhymeString);
        rhymeList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObject = jsonArray.getJSONObject(i);
                rhymeList.add(jObject.getString("word"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        rhymeAdapter = new RhymeListAdaptor(popupWindow.getContentView().getContext(), rhymeList);
        listView.setAdapter(rhymeAdapter);
        rhymeAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean  onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void Save(String fileName) {
        try {
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(fileName, 0));
            out.write(songText.getText().toString());
            out.close();
            Toast.makeText(this, "Song saved!", Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public String Open(String fileName) {
        String content = "";
        if (FileExists(fileName)) {
            try {
                InputStream in = openFileInput(fileName);
                if ( in != null) {
                    InputStreamReader tmp = new InputStreamReader( in );
                    BufferedReader reader = new BufferedReader(tmp);
                    String str;
                    StringBuilder buf = new StringBuilder();
                    while ((str = reader.readLine()) != null) {
                        buf.append(str + "\n");
                    } in .close();
                    content = buf.toString();
                }
            } catch (java.io.FileNotFoundException e) {} catch (Throwable t) {
                Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        }
        return content;
    }

    public boolean FileExists(String fname) {
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(NotepadActivity.this, SongSelect.class);
            NotepadActivity.this.startActivity(myIntent);
        }
        else if (id == R.id.delete_song) {
            File dir = getFilesDir();
            File file = new File(dir, titleText.getText().toString());
            if (file.exists()) {
                file.delete();
                Toast.makeText(this, titleText.getText().toString() + " deleted.", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(NotepadActivity.this, SongSelect.class);
                NotepadActivity.this.startActivity(myIntent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private JSONArray getRhymeWords(String rhymeInput) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.datamuse.com/words?rel_rhy=" + rhymeInput;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    myResponse = response.body().string();
                    System.out.println(myResponse);
                    try {
                        jsonArray = new JSONArray(myResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        // Fixme - first response is null
        return jsonArray;
    }
}
