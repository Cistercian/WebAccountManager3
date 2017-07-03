package ru.hd.olaf.util.json;

import java.math.BigDecimal;
import java.util.Date;

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

    public CalendarEntity(Number sum, Date date) {
        this();
        this.title = sum.toString();
        this.date = date.toString();

        isNegativeSum(sum.toString());
    }

    public CalendarEntity(String sum, String date) {
        this();
        this.title = sum;
        this.date = date;

        //Если 1, то имеем дело с расходом
        isNegativeSum(sum);
    }

    private void isNegativeSum(String sum) {
        if (new BigDecimal(sum.toString()).compareTo(new BigDecimal("0")) < 0) {
            this.color = "#da8b7e";
            this.textColor = "#631712";
        }
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarEntity that = (CalendarEntity) o;

        return date != null ? date.equals(that.date) : that.date == null;

    }

    @Override
    public int hashCode() {
        return date != null ? date.hashCode() : 0;
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
