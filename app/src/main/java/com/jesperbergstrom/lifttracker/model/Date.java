package com.jesperbergstrom.lifttracker.model;

public class Date {

    private final String[] MONTHS = { "null", "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec" };

    private int year;
    private int month;
    private int day;

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public boolean isBefore(Date other) {
        if (year < other.getYear()) {
            return true;
        } else if (month < other.getMonth() && year == other.getYear()) {
            return true;
        } else if (day < other.getDay() && month == other.getMonth() && year == other.getYear()) {
            return true;
        }
        return false;
    }

    public String toShortString() {
        return day + "/" + month;
    }

    public String toString() {
        String sM = String.valueOf(month);
        if (month < 10) {
            sM = "0" + month;
        }
        String sD = String.valueOf(day);
        if (day < 10) {
            sD = "0" + day;
        }
        return year + "-" + sM + "-" + sD;
    }

    public static Date parseDate(String str) {
        String[] parts = str.split("-");

        int year = Integer.parseInt(parts[0]);

        if (parts[1].startsWith("0") && parts[1].length() == 2) {
            parts[1] = String.valueOf(parts[1].charAt(1));
        }
        int month = Integer.parseInt(parts[1]);

        if (parts[2].startsWith("0") && parts[2].length() > 2) {
            parts[2] = String.valueOf(parts[2].charAt(2));
        }
        int day = Integer.parseInt(parts[2]);

        return new Date(year, month, day);
    }
}
