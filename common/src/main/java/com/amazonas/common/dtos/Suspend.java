package com.amazonas.common.dtos;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Suspend {
    private final String suspendId;
    private final String beginDate;
    private final String finishDate;

    public Suspend(String suspendId, String beginDate, String finishDate){
        this.suspendId = suspendId;
        this.beginDate = beginDate;
        this.finishDate = finishDate;
    }

    public String getSuspendId() {
        return suspendId;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public String getFinishDate() {
        return finishDate;
    }


    public String getDuration() {
        LocalDate begin = stringToLocalDate(this.beginDate);
        if (this.finishDate.equals("always"))
            return "always";
        LocalDate finish = stringToLocalDate(this.finishDate);
        Period duration = Period.between(begin, finish);
        return " " + duration.getYears() + " years, " + duration.getMonths() + " months, " + duration.getDays() + " days. ";
    }

    private LocalDate stringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        return LocalDate.parse(date, formatter);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Suspend that = (Suspend) o;
        return Objects.equals(suspendId, that.suspendId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(suspendId);
    }
}
