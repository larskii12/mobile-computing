package com.comp90018.uninooks.service.time;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeServiceImpl implements TimeService{
    @Override
    public Time getAEDTTime() {
        LocalTime localTimeAEDT = LocalTime.now(ZoneId.of("Australia/Melbourne"));
        int hour = localTimeAEDT.getHour();
        int minute = localTimeAEDT.getMinute();
        int second = localTimeAEDT.getSecond();
        String currentTimeStr = hour + ":" + minute + ":" + second;

        return Time.valueOf(currentTimeStr);
    }

    @Override
    public LocalTime getAEDTLocalTime() {

        LocalTime localTimeAEDT = LocalTime.now(ZoneId.of("Australia/Melbourne"));
        int hour = localTimeAEDT.getHour();
        int minute = localTimeAEDT.getMinute();
        int second = localTimeAEDT.getSecond();
        String currentTimeStr = hour + ":" + minute + ":" + second;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss");

        return LocalTime.parse(currentTimeStr, formatter);
    }

    @Override
    public int getAEDTTimeHour() {
        LocalTime localTimeAEDT = LocalTime.now(ZoneId.of("Australia/Melbourne"));
        return localTimeAEDT.getHour();
    }

    @Override
    public int getAEDTTimeMinute() {
        LocalTime localTimeAEDT = LocalTime.now(ZoneId.of("Australia/Melbourne"));
        return localTimeAEDT.getMinute();
    }

    @Override
    public int getAEDTTimeSecond() {
        LocalTime localTimeAEDT = LocalTime.now(ZoneId.of("Australia/Melbourne"));
        return localTimeAEDT.getSecond();
    }

    @Override
    public int getWeekDate() {
        LocalDate currentDate = LocalDate.now(ZoneId.of("Australia/Melbourne"));
        return currentDate.getDayOfWeek().getValue();
    }
}
