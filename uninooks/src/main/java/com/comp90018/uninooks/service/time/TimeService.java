package com.comp90018.uninooks.service.time;

import java.sql.Time;
import java.time.LocalTime;

public interface TimeService {

    public Time getAEDTTime();

    public LocalTime getAEDTLocalTime();

    public int getAEDTTimeHour();
    public int getAEDTTimeMinute();

    public int getAEDTTimeSecond();

    public int getWeekDate();

    public int getTestWeekDate(int weekDate);

    public Time getTestAEDTTime(int hour, int minute, int second);

    public LocalTime getTestAEDTLocalTime(int hour, int minute, int second);

}
