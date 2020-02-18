package com.jesperbergstrom.lifttracker.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.jesperbergstrom.lifttracker.R;
import com.jesperbergstrom.lifttracker.io.FileManager;
import com.jesperbergstrom.lifttracker.model.Lift;
import com.jesperbergstrom.lifttracker.model.Set;
import com.jesperbergstrom.lifttracker.model.Workout;
import com.jesperbergstrom.lifttracker.view.fragment.LiftFragmentAdapter;
import com.jesperbergstrom.lifttracker.view.graphs.OpenGLView;

import java.util.ArrayList;

public class LiftActivity extends AppCompatActivity {

    private OpenGLView openGLView;
    private LinearLayout workoutList;
    private Button addWorkoutButton;
    private TextView liftText;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ArrayList<Lift> lifts;
    private FileManager fileManager;
    private int selectedIndex;
    private String liftName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_lift);

        fileManager = new FileManager(getFilesDir());

        addWorkoutButton = findViewById(R.id.addWorkoutBtn);
        liftText = findViewById(R.id.liftText);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Workouts"));
        tabLayout.addTab(tabLayout.newTab().setText("Stats"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final LiftFragmentAdapter adapter = new LiftFragmentAdapter(this, this.getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    workoutList = findViewById(R.id.workoutList);
                    loadWorkouts();
                } else if (tab.getPosition() == 1) {
                    openGLView = findViewById(R.id.openGLView);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        Intent intent = getIntent();
        liftName = intent.getStringExtra("name");
        liftText.setText(liftName);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        workoutList = findViewById(R.id.workoutList);
        loadWorkouts();

        addWorkoutButton.setOnClickListener((view) -> {
            addWorkoutDialog();
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        selectedIndex = workoutList.indexOfChild(v);
        getMenuInflater().inflate(R.menu.workout_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.removeWorkout:
                removeDialog();
                return true;
            case R.id.changeWorkout:
                changeDialog();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void changeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        DatePicker datePicker = new DatePicker(this);

        builder.setView(datePicker);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Workout w = getLift(liftName).getWorkouts().get(selectedIndex);
            w.setDate(dateToString(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth()));
            fileManager.updateAllLiftFiles(lifts);
            loadWorkouts();
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
        text.setText("Do you want to remove this workout?");

        LinearLayout l = new LinearLayout(this);
        l.setPadding(60, 60, 60, 0);
        l.addView(text);

        builder.setView(l);

        builder.setPositiveButton("YES", (dialog, which) -> {
            getLift(liftName).getWorkouts().remove(selectedIndex);
            fileManager.updateAllLiftFiles(lifts);
            loadWorkouts();
        });

        builder.setNegativeButton("NO", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void addWorkoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        DatePicker datePicker = new DatePicker(this);

        builder.setView(datePicker);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Workout w = new Workout();
            w.setDate(dateToString(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth()));
            getLift(liftName).getWorkouts().add(w);
            fileManager.updateAllLiftFiles(lifts);
            loadWorkouts();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void loadWorkouts() {
        if (workoutList == null) {
            return;
        }

        lifts = fileManager.loadAllLiftFiles();

        ArrayList<Workout> workouts = getLift(liftName).getWorkouts();
        workoutList.removeAllViews();

        for (Workout w : workouts) {
            // Container for the workout
            LinearLayout hbox = new LinearLayout(this);
            hbox.setOrientation(LinearLayout.HORIZONTAL);
            hbox.setPadding(30, 30, 30, 30);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 4);
            hbox.setLayoutParams(params);
            hbox.setBackgroundColor(Color.parseColor("#202020"));

            // Generate "thumbnail" string
            String thumbnail = "";
            if (w.getSets().size() > 0 && w.getDisplayIndex() == -1) {
                Set show = w.getSets().get(0);
                for (Set s : w.getSets()) {
                    if (s.getWeight() > show.getWeight()) {
                        show = s;
                    } else if (s.getWeight() == show.getWeight() && s.getReps() > show.getReps()) {
                        show = s;
                    }
                }
                thumbnail = "(" + show.getWeight() + "kg x " + show.getReps() + ")";
            } else if (w.getDisplayIndex() != -1) {
                Set show = w.getSets().get(w.getDisplayIndex());
                thumbnail = "(" + show.getWeight() + "kg x " + show.getReps() + ")";
            }

            // Text
            TextView tv = new TextView(this);
            tv.setText(w.getDate() + ": " + thumbnail);
            tv.setTextSize(20);
            tv.setTextColor(Color.WHITE);

            this.registerForContextMenu(hbox);

            hbox.setOnClickListener((view) -> {
                Intent intent = new Intent(this, WorkoutActivity.class);
                intent.putExtra("name", liftName);
                intent.putExtra("date", w.getDate());
                startActivity(intent);
            });

            hbox.addView(tv);
            workoutList.addView(hbox);
        }
    }

    private String dateToString(int year, int month, int day) {
        String sM = String.valueOf(month + 1);
        if (month < 9) {
            sM = "0" + (month + 1);
        }
        String sD = String.valueOf(day);
        if (day < 10) {
            sD = "0" + day;
        }
        return year + "-" + sM + "-" + sD;
    }

    private Lift getLift(String name) {
        for (Lift lift : lifts) {
            if (lift.getName().equals(name)) {
                return lift;
            }
        }
        return null;
    }
}
