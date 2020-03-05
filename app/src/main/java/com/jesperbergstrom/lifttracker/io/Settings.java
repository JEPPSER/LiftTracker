package com.jesperbergstrom.lifttracker.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Settings {

    private File file;
    private String weightUnit;

    public Settings(File dir) {
        file = new File(dir.getAbsolutePath() + "/settings.txt");
        if (!file.exists()) {
            weightUnit = "kg";
            updateSettingsFile();
        }
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
        updateSettingsFile();
    }

    public String getWeightUnit() {
        loadSettingsFile();
        return weightUnit;
    }

    private void updateSettingsFile() {
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(weightUnit);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSettingsFile() {
        try {
            Scanner scan = new Scanner(file, "UTF-8");
            weightUnit = scan.nextLine();
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
