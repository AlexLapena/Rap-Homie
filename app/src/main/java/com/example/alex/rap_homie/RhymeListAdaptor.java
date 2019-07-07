package com.example.alex.rap_homie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import java.util.ArrayList;

//https://stackoverflow.com/questions/40862154/how-to-create-listview-items-button-in-each-row/40862637

public class RhymeListAdaptor extends BaseAdapter {
    private ArrayList<String> wordList;
    private Context context;
    
    public RhymeListAdaptor( Context context, ArrayList wordList) {
        super();
        this.context = context;
        this.wordList = wordList;
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
                // Logic goes here
            }
        });

        return view;
    }
}

