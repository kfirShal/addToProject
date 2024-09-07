package com.amazonas.common.dtos;

public class Suspend {
    private final String suspendId;
    private final String beginDate;
    private final String duration;

    public Suspend(String suspendId, String beginDate, String duration){
        this.suspendId = suspendId;
        this.beginDate = beginDate;
        this.duration = duration;
    }

    public String getSuspendId() {
        return suspendId;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public String getDuration() {
        return duration;
    }

    public String finishDate(String beginDate, String duration){
        if (duration.equals("No Limit"))
        {
            return "There is no end date";
        }
        else
            return new String(duration);
    }
}
