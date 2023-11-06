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

}
