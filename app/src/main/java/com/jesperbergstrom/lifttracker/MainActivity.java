package com.jesperbergstrom.lifttracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.jesperbergstrom.lifttracker.io.FileManager;
import com.jesperbergstrom.lifttracker.model.Lift;
import com.jesperbergstrom.lifttracker.view.LiftActivity;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private Button addLiftBtn;
    private LinearLayout liftList;

    private ArrayList<Lift> lifts;
    private FileManager fileManager;
    private int selectedIndex;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileManager = new FileManager(getFilesDir());

        addLiftBtn = findViewById(R.id.addLiftBtn);
        liftList = findViewById(R.id.liftList);

        intent = new Intent(this, LiftActivity.class);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.removeLift:
                removeDialog();
                return true;
            case R.id.changeLift:
                changeDialog();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        loadLifts();

        addLiftBtn.setOnClickListener((view) -> {
            addLiftDialog();
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        selectedIndex = liftList.indexOfChild(v);
        getMenuInflater().inflate(R.menu.lift_menu, menu);
    }

    private void changeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New name:");

        final EditText input = new EditText(this);
        input.setWidth(400);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        LinearLayout l = new LinearLayout(this);
        l.setPadding(60, 0, 60, 0);
        l.addView(input);

        builder.setView(l);

        builder.setPositiveButton("OK", (dialog, which) -> {
            lifts.get(selectedIndex).setName(input.getText().toString());
            fileManager.updateAllLiftFiles(lifts);
            loadLifts();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void addLiftDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lift name:");

        final EditText input = new EditText(this);
        input.setWidth(400);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        LinearLayout l = new LinearLayout(this);
        l.setPadding(60, 0, 60, 0);
        l.addView(input);

        builder.setView(l);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Lift lift = new Lift(input.getText().toString());
            lifts.add(lift);
            fileManager.updateAllLiftFiles(lifts);
            loadLifts();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void removeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning!");
        TextView text = new TextView(this);
        text.setText("Do you want to remove this lift?");

        LinearLayout l = new LinearLayout(this);
        l.setPadding(60, 60, 60, 0);
        l.addView(text);

        builder.setView(l);

        builder.setPositiveButton("YES", (dialog, which) -> {
            lifts.remove(selectedIndex);
            fileManager.updateAllLiftFiles(lifts);
            loadLifts();
        });

        builder.setNegativeButton("NO", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void loadLifts() {
        lifts = fileManager.loadAllLiftFiles();
        liftList.removeAllViews();

        // Add UI elements
        for (Lift l : lifts) {

            // Container for the lift
            LinearLayout hbox = new LinearLayout(this);
            hbox.setOrientation(LinearLayout.HORIZONTAL);
            hbox.setPadding(30, 30, 30, 30);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 4);
            hbox.setLayoutParams(params);
            hbox.setBackgroundColor(Color.parseColor("#202020"));

            // Text
            TextView tv = new TextView(this);
            tv.setText(l.getName());
            tv.setTextSize(30);
            tv.setTextColor(Color.WHITE);

            this.registerForContextMenu(hbox);

            hbox.setOnClickListener((view) -> {
                intent.putExtra("name", l.getName());
                startActivity(intent);
            });

            hbox.addView(tv);
            liftList.addView(hbox);
        }
    }
}
