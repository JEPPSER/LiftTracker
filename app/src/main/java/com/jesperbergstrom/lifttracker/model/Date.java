package com.jesperbergstrom.lifttracker.model;

public class Date {

    private final String[] MONTHS = { "null", "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec" };
    private final int[] NUMBER_OF_DAYS = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    private int year;
    private int month;
    private int day;

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() { return year; }

    public int getMonth() {
        return month;
    }

    public int getDay() { return day; }

    public boolean equals(Date other) {
        if (this.year == other.year && this.month == other.month && this.day == other.day) {
            return true;
        }
        return false;
    }

    public int daysTo(Date other) {
        if (!this.isBefore(other)) {
            return 0;
        }

        Date temp = new Date(other.year, other.month, other.day);

        int count = temp.getDay();

        while (!(temp.getMonth() == month && temp.getYear() == year)) {
            temp.setMonth(temp.getMonth() - 1);
            if (temp.getMonth() == 0) {
                temp.setMonth(12);
                temp.setYear(temp.getYear() - 1);
            }

            if (temp.getMonth() == 2 && temp.getYear() % 4 == 0) { // Leap year
                temp.setDay(29);
            } else {
                temp.setDay(NUMBER_OF_DAYS[temp.getMonth()]);
            }

            count += temp.getDay();
        }

        count -= day;

        return count;
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
