package ru.hd.olaf.util.json;

/**
 * Created by Olaf on 29.04.2017.
 */
public class CalendarEntity {
    private String title;
    private String date;
    private boolean allDay;
    private String color;
    private String textColor;

    public CalendarEntity() {
        this.allDay = true;
        this.color = "#aedb97";
        this.textColor = "#3d641b";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    @Override
    public String toString() {
        return "CalendarEntity{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", allDay=" + allDay +
                ", color='" + color + '\'' +
                ", textColor='" + textColor + '\'' +
                '}';
    }
}
