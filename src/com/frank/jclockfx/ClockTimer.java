package com.frank.jclockfx;

import java.time.LocalTime;

public class ClockTimer {

    public int getHour() {
        return LocalTime.now().getHour();
    }

    public int getMinute() {
        return LocalTime.now().getMinute();
    }

    public int getSecond() {
        return LocalTime.now().getSecond();
    }
}
