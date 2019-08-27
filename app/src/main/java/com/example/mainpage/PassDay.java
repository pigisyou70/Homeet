package com.example.mainpage;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

public class PassDay implements DayViewDecorator {
    private final Calendar calendar = Calendar.getInstance();

    public PassDay(){

    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        CalendarDay today=CalendarDay.today();
        return day.isBefore(today);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setDaysDisabled(true);
    }
}
