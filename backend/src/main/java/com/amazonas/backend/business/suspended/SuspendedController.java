package com.amazonas.backend.business.suspended;

import com.amazonas.common.dtos.Suspend;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("suspendedController")
public class SuspendedController {

    private static SuspendedController instance;

    private final Map<String, Suspend> suspendList;

    public SuspendedController() {
        this.suspendList = new HashMap<>();
        Suspend suspend1 = new Suspend("1", "09/09/24", "15/09/24");
        Suspend suspend2 = new Suspend("2", "09/09/24", "always");
        Suspend suspendU2 = new Suspend("u2", "08/09/24", "always");
        this.addSuspend(suspend1);
        this.addSuspend(suspend2);
        this.addSuspend(suspendU2);
    }


    public List<Suspend> getSuspendList() {
        return List.copyOf(suspendList.values());
    }

    public void addSuspend(Suspend suspend) {
        suspendList.put(suspend.getSuspendId(), suspend);
    }

    public Suspend removeSuspend(String id) {
        return suspendList.remove(id);
    }

    //The user is suspended iff date of now is between begin and finish
    public boolean isSuspended(String id) {
        Suspend suspend = suspendList.get(id);
        if (suspend != null) {
            LocalDate beginDate = stringToLocalDate(suspend.getBeginDate());
            LocalDate now = LocalDate.now();
            if (beginDate.isBefore(now)) {
                if (suspend.getFinishDate().equals("always"))
                    return true;
                LocalDate finishDate = stringToLocalDate(suspend.getFinishDate());
                return finishDate.isAfter(now);
            }
        }
        return false;
    }

    //Auxiliary function for convert the field of time String to Local date
    private LocalDate stringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        return LocalDate.parse(date, formatter);
    }
}
