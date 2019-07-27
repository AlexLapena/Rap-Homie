package com.example.alex.rap_homie;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;

//https://stackoverflow.com/questions/40862154/how-to-create-listview-items-button-in-each-row/40862637

public class RhymeListAdaptor extends BaseAdapter {
    private final PopupWindow popupWindow;
    private ArrayList<String> wordList;
    private Context context;
    private EditText songText;
    
    public RhymeListAdaptor(Context context, ArrayList wordList, PopupWindow popupWindow, EditText songText) {
        super();
        this.context = context;
        this.wordList = wordList;
        this.popupWindow = popupWindow;
        this.songText = songText;
    }

    @Override
    public int getCount() {
        return wordList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_listview_row, parent, false);

        // Set button text
        Button button = view.findViewById(R.id.word_btn);
        button.setText(wordList.get(position));

        // Click listener of button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add rhyme word to clipboard
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE); //Might crash, haven't tested
                ClipData clip = ClipData.newPlainText("RhymeClip", wordList.get(position));
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Selected rhyme: " + wordList.get(position), Toast.LENGTH_LONG).show();

                //Close popupw window
                popupWindow.dismiss();

                //Wait for user to select editText location, then paste
                songText.setOnClickListener(v -> {
                    //Paste clipboard item, then wipe from clipboard
                    ClipData clipData = clipboard.getPrimaryClip();
                    ClipData.Item item = clipData.getItemAt(0);
                    String pasteText = item.getText().toString();

                    int cursorPosition = songText.getSelectionStart();
                    songText.getText().insert(cursorPosition, pasteText);
                    v.setOnClickListener(null);
                });
            }
        });

        return view;
    }
}

