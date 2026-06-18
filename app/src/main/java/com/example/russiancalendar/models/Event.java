package com.example.russiancalendar.models;

import java.io.Serializable;

public class Event implements Serializable {
    private long id;
    private String time;      // "HH:MM"
    private String name;
    private String date;      // "yyyy-MM-dd"

    public Event() {}

    public Event(long id, String time, String name, String date) {
        this.id = id;
        this.time = time;
        this.name = name;
        this.date = date;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
