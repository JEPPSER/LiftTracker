package com.jesperbergstrom.lifttracker.io;

import com.jesperbergstrom.lifttracker.model.Lift;
import com.jesperbergstrom.lifttracker.model.Set;
import com.jesperbergstrom.lifttracker.model.Workout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManager {

    private File dir;

    public FileManager(File dir) {
        File temp = new File(dir.getAbsolutePath() + "/lifts");
        if (!temp.exists()) {
            temp.mkdir();
        }
        this.dir = temp;
    }

    public void updateAllLiftFiles(ArrayList<Lift> lifts) {
        for (File f : dir.listFiles()) {
            f.delete();
        }

        for (Lift l : lifts) {
            try {
                FileWriter fw = new FileWriter(dir.getAbsolutePath() + "/" + l.getName() + ".txt");
                BufferedWriter bw = new BufferedWriter(fw);

                for (Workout w : l.getWorkouts()) {
                    bw.write(w.getDate() + ":" + w.getDisplayIndex() + ":");
                    for (Set s : w.getSets()) {
                        bw.write(s.getWeight() + "x" + s.getReps() + ",");
                    }
                    bw.newLine();
                }

                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Lift> loadAllLiftFiles() {
        File[] files = dir.listFiles();
        ArrayList<Lift> lifts = new ArrayList<Lift>();

        for (File f : files) {
            lifts.add(loadLiftFile(f));
        }

        return lifts;
    }

    private Lift loadLiftFile(File file) {
        try {
            Scanner scan = new Scanner(file, "UTF-8");
            ArrayList<Workout> workouts = new ArrayList<Workout>();

            while (scan.hasNext()) {
                String[] line = scan.nextLine().split(":");
                String date = line[0];

                Workout w = new Workout();
                w.setDate(date);
                w.setDisplayIndex(Integer.parseInt(line[1]));

                if (line.length > 2) {
                    String[] sets = line[2].split(",");
                    System.out.println(line[2]);
                    for (String s : sets) {
                        String[] parts = s.split("x");
                        Set set = new Set(Float.parseFloat(parts[0]), Integer.parseInt(parts[1]));
                        w.getSets().add(set);
                    }
                }

                workouts.add(w);
            }

            Lift lift = new Lift(file.getName().replace(".txt", ""));
            lift.setWorkouts(workouts);

            return lift;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
