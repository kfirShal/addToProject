package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Suspend;
import com.amazonas.common.requests.suspends.SuspendedRequest;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        //SuspendedRequest request = new SuspendedRequest();

        try {
            suspends = appController.postByEndpoint(Endpoints.SUSPENDS_LIST, null);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }

        //From vaadin - Grid
        suspendsGrid = new Grid<>(Suspend.class, false);
        suspendsGrid.addColumn(Suspend::getSuspendId).setHeader("Suspend ID");
        suspendsGrid.addColumn(Suspend::getBeginDate).setHeader("Begin Date");
        suspendsGrid.addColumn(Suspend::getFinishDate).setHeader("Finish Date");
        suspendsGrid.addColumn(Suspend::getDuration).setHeader("Duration");
        //from vaadin Grid button of remove
        suspendsGrid.addColumn(
                new ComponentRenderer<>(Button::new, (button, suspend) -> {
                    button.addClickListener(e -> {

                        SuspendedRequest removeRequest = new SuspendedRequest(suspend.getSuspendId());

                        try {
                            appController.postByEndpoint(Endpoints.REMOVE_SUSPEND, removeRequest);
                            suspends = appController.postByEndpoint(Endpoints.SUSPENDS_LIST, null);
                            suspendsGrid.setItems(suspends);

                        } catch (ApplicationException error) {
                            openErrorDialog(error.getMessage());
                        }
                    });
                    button.setIcon(new Icon(VaadinIcon.TRASH));
                })).setHeader("Remove");
        //Empty state
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
        }
        //from vaadin Grid, in case it is not empty state
        else {
            suspendsGrid.setItems(suspends);
        }

        content.add(suspendsGrid);
        //from vaadin Form and Date Picker
        TextField idField = new TextField("ID");
        DatePicker beginDate = new DatePicker("Begin Date");
        DatePicker finishDate = new DatePicker("Finish Date");
        Checkbox always = new Checkbox("Always", event -> {
            boolean value = event.getValue();
            finishDate.setEnabled(!value);
        });


        Button addSuspend = new Button("Add", event -> {
            String idValue = idField.getValue();
            LocalDate begin = beginDate.getValue();
            LocalDate finish = finishDate.getValue();
            boolean isAlways = always.getValue();

            if (idValue.isEmpty() || begin == null || (!isAlways && (finish == null || finish.isBefore(begin)))) {
                openErrorDialog("Form is not valid");
                return;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
            SuspendedRequest addRequest = new SuspendedRequest(idValue, begin.format(formatter), isAlways ? "always" : finish.format(formatter));

            try {
                appController.postByEndpoint(Endpoints.ADD_SUSPEND, addRequest);
                suspends = appController.postByEndpoint(Endpoints.SUSPENDS_LIST, null);
                suspendsGrid.setItems(suspends);

            } catch (ApplicationException e) {
                openErrorDialog(e.getMessage());
            }

        });
        FormLayout formLayout = new FormLayout();
        formLayout.add(idField, beginDate, finishDate, always, addSuspend);
        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2),
                new FormLayout.ResponsiveStep("750px", 3));

        content.add(formLayout);

    }
}
