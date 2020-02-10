package com.jesperbergstrom.lifttracker.io;

import com.jesperbergstrom.lifttracker.model.Lift;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileManager {

    private File dir;

    public FileManager(File dir) {
        File temp = new File(dir.getAbsolutePath() + "/lifts");
        if (!temp.exists()) {
            temp.mkdir();
        }
        this.dir = temp;
    }

    public void updateLiftFiles(ArrayList<Lift> lifts) {
        for (Lift l : lifts) {
            try {
                FileWriter fw = new FileWriter(dir.getAbsolutePath() + "/" + l.getName() + ".txt");
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("temp");
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Lift> loadLiftFiles() {
        File[] files = dir.listFiles();
        ArrayList<Lift> lifts = new ArrayList<Lift>();

        for (File f : files) {
            Lift lift = new Lift(f.getName().replace(".txt", ""));
            lifts.add(lift);
        }

        return lifts;
    }
}
