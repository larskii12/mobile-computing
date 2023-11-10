package com.comp90018.uninooks.service.time;

import java.sql.Time;
import java.time.LocalTime;

public interface TimeService {

    Time getAEDTTime();

    LocalTime getAEDTLocalTime();

    int getAEDTTimeHour();

    int getAEDTTimeMinute();

    int getAEDTTimeSecond();

    int getWeekDate();

}
