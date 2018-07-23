package com.brian.todolist;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // used to retrieve the to do list items from storage
    private static final String TO_DO_ITEMS = "to_do_items.txt";

    // used to restore list if user wants to undo changes
    private String mPreviousItem; // single change
    private int mPreviousItemPos;

    private ArrayList<String> mItems;
    private ArrayAdapter<String> mArrayAdapter;
    private ListView mListViewItems;
    private EditText mEditTextInput;
    private Button mButtonAdd;
    private Button mButtonUndo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreviousItem = "";
        mPreviousItemPos = -1;

        // Read from storage if there is data
        readItems();
        mArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mItems);
        mListViewItems = (ListView) findViewById(R.id.list_view_items);
        mListViewItems.setAdapter(mArrayAdapter);

        mEditTextInput = findViewById(R.id.edit_text_new_item);
        mButtonAdd = findViewById(R.id.button_add);
        mButtonUndo = findViewById(R.id.button_undo);

        // delete items by long pressing
        mListViewItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                deleteItem(i);
                return true;
            }
        });

        // Add items to the list after typing in edit text
        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        mButtonUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoItems();
            }
        });
    }

    private void addItem() {
        String input = mEditTextInput.getText().toString();
        if (input.length() == 0) {
            Toast.makeText(MainActivity.this,
                    "Field cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        mArrayAdapter.add(input);
        mEditTextInput.getText().clear();
        writeItems();
    }

    private void deleteItem(int i) {
        // save the item in case user wants to undo the delete
        mPreviousItem = mItems.get(i);
        mPreviousItemPos = i;
        showUndoButton();
        mItems.remove(i);
        mArrayAdapter.notifyDataSetChanged();
        writeItems();
    }

    private void undoItems() {
        if (!mPreviousItem.equals("") && mPreviousItemPos != -1) {
            mItems.add(mPreviousItemPos, mPreviousItem);
            mArrayAdapter.notifyDataSetChanged();
            writeItems();
        }
        mButtonUndo.setVisibility(View.INVISIBLE);
        mPreviousItem = "";
        mPreviousItemPos = -1;
    }

    private void showUndoButton() {
        mButtonUndo.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mButtonUndo.setVisibility(View.INVISIBLE);
            }
        }, 3500);
    }

    // read and write methods to save our data
    private void readItems() {
        File list = new File(getFilesDir(), TO_DO_ITEMS);
        try {
            mItems = new ArrayList<String>(FileUtils.readLines(list));
        }
        catch (IOException e) {
            mItems = new ArrayList<String>();
        }
    }

    private void writeItems() {
        File list = new File(getFilesDir(), TO_DO_ITEMS);
        try {
            FileUtils.writeLines(list, mItems);
        }
        catch (IOException e){ // do nothing
        }
    }
}
