package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Notification;
import com.amazonas.common.dtos.Suspend;
import com.amazonas.common.requests.notifications.NotificationRequest;
import com.amazonas.common.requests.suspends.SuspendedRequest;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("suspends")
public class SuspendsView extends BaseLayout {

    private final AppController appController;
    private Grid<Suspend> suspendsGrid;
    private List<Suspend> suspends;

    public SuspendsView(AppController appController) {
        super(appController);
        this.appController = appController;

        String message = appController.getSuspendsMessage();
        H2 h2 = new H2(message);
        h2.getStyle().setAlignSelf(Style.AlignSelf.CENTER);
        content.add(h2);

        //init list
        suspends = new ArrayList<>();

        // Get suspends from the backend
        SuspendedRequest request = new SuspendedRequest();


        try {
            suspends = appController.postByEndpoint(Endpoints.SUSPENDS_LIST, null);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }

        suspendsGrid = new Grid<>(Suspend.class, false);
        suspendsGrid.addColumn(Suspend::getSuspendId).setHeader("Suspend ID");
        suspendsGrid.addColumn(Suspend::getBeginDate).setHeader("Begin Date");
        suspendsGrid.addColumn(Suspend::getFinishDate).setHeader("Finish Date");
        suspendsGrid.addColumn(Suspend::getDuration).setHeader("Duration");

        if (suspends == null || suspends.isEmpty()) {
            VerticalLayout noSuspendsLayout = new VerticalLayout();
            noSuspendsLayout.setWidthFull();
            noSuspendsLayout.setHeightFull();
            noSuspendsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            noSuspendsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

            Span noSuspendsMessage = new Span("There are no new suspends");
            noSuspendsMessage.getStyle().set("font-size", "24px");
            noSuspendsMessage.getStyle().set("color", "var(--lumo-secondary-text-color)");

            noSuspendsLayout.add(noSuspendsMessage);
            content.add(noSuspendsLayout);
            return;

        }

        suspendsGrid.setItems(suspends);
        content.add(suspendsGrid);


    }
}
