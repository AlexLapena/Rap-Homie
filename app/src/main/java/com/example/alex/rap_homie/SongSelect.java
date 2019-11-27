package com.example.alex.rap_homie;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main Page - Displays all available songs
 */
public class SongSelect extends AppCompatActivity {

    private List<NotesBuilder> notesList = new ArrayList<>();
    private ArrayList<String> titleArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent( SongSelect.this, NotepadActivity.class);
                SongSelect.this.startActivity(myIntent);
            }
        });

        prepareNotes();

        // FIXME - Better way to do this, such as array copy.
        for (NotesBuilder title : notesList) {
            titleArray.add(title.getTitle());
        }

        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.list_row, titleArray);
        ListView listView = (ListView) findViewById(R.id.song_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String[] titleAsArray = Arrays.copyOf(titleArray.toArray(), titleArray.toArray().length, String[].class);
                Intent intent = new Intent(getApplicationContext(),NotepadActivity.class);
                intent.putExtra("SONG_TITLE_SELECTED", titleAsArray[i]);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private void prepareNotes() {
        File directory;
        directory = getFilesDir();
        File[] files = directory.listFiles();

        for (File songFile : files) {
            String songTitle = songFile.getName();
            NotesBuilder note = new NotesBuilder(songTitle);
            notesList.add(note);
        }
    }

    public String Open(String fileName) {
        String content = "";
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
        return content;
    }
}
