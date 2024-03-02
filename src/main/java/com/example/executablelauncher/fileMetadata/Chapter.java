package com.example.executablelauncher.fileMetadata;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Chapter {
    @JsonIgnore
    public long id;
    @JsonIgnore
    public String time_base;
    public long start;
    @JsonIgnore
    public String start_time;
    @JsonIgnore
    public long end;
    @JsonIgnore
    public String end_time;
    public Tags tags;
}
