package com.amazonas.frontend.view;

import com.amazonas.common.PurchaseRuleDTO.*;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.stores.PurchasePolicyRequest;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.amazonas.frontend.control.AppController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Route("purchase-policy")
public class PurchasePolicyView extends BaseLayout implements BeforeEnterObserver {
    private final AppController appController;
    private TreeGrid<PurchaseRuleDTO> treeGrid;
    private PurchaseRuleTranslator translator;
    private String storeId;

    public PurchasePolicyView(AppController appController) {
        super(appController);
        this.appController = appController;
        //initializeView();
    }

    private void initializeView() {
        H2 title = new H2("Purchase Policy");
        title.getStyle().set("text-align", "center");
        storeId = getParam("storeid");

        // Button for adding new purchase policy
        Button addButton = new Button("Add New Purchase Policy", new Icon(VaadinIcon.PLUS));
        addButton.addClickListener(event -> showAddPolicyDialog());

        // Layout for title and button
        HorizontalLayout header = new HorizontalLayout(title, addButton);
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(true);
        header.setSpacing(true);


        treeGrid = new TreeGrid<>(PurchaseRuleDTO.class);

        List<PurchaseRuleDTO> rules;
        try{
            rules = appController.postByEndpoint(Endpoints.GET_PURCHASE_POLICY, storeId);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
            return;
        }
        List<PurchaseRuleDTO> exampleData = createExampleData();
        translator = new PurchaseRuleTranslator();
        treeGrid = translator.translateToTreeGrid(exampleData);


        // Button for deleting purchase policy
        Button deleteButton = new Button("Delete Purchase Policy", new Icon(VaadinIcon.TRASH));
        deleteButton.addClickListener(event -> {
            treeGrid = translator.translateToTreeGrid(new ArrayList<>());
            try{
                appController.postByEndpoint(Endpoints.REMOVE_PURCHASE_POLICY, storeId);
            } catch (ApplicationException e) {
                openErrorDialog(e.getMessage());
            }
        });
        // Layout for TreeGrid and Delete Button
        HorizontalLayout footer = new HorizontalLayout(deleteButton);
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.setPadding(true);
        footer.setSpacing(true);

        VerticalLayout layout = new VerticalLayout(header, treeGrid, footer);
        layout.setSizeFull();
        layout.setPadding(true);
        layout.setSpacing(true);

        content.add(layout);

    }

    private void showAddPolicyDialog() {
        NewPolicyView dialog = new NewPolicyView(appController,this);
        dialog.open();
    }
    public void setRules(List<PurchaseRuleDTO> rules) {
        treeGrid = translator.translateToTreeGrid(rules);
        PurchasePolicyRequest request = new PurchasePolicyRequest(storeId, rules.getFirst());
        try{
            appController.postByEndpoint(Endpoints.ADD_PURCHASE_POLICY, request);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
    }


    private List<PurchaseRuleDTO> createExampleData() {
        List<PurchaseRuleDTO> exampleData = new ArrayList<>();

        // Example rule data creation
        NumericalPurchaseRuleDTO ageRestrictionRule = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.AGE_RESTRICTION, 18);
        DatePurchaseRuleDTO dayRestrictionRule = new DatePurchaseRuleDTO(DatePurchaseRuleType.DAY_RESTRICTION, LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 7));
        ConditionLevelDTO conditionLevelDTO = new ConditionLevelDTO(ConditionLevelType.PRODUCT_LEVEL, "Product123", 5);
        ConditionalPurchaseRuleDTO conditionalPurchaseRule = new ConditionalPurchaseRuleDTO(conditionLevelDTO, ageRestrictionRule);

        List<PurchaseRuleDTO> multipleRules = new ArrayList<>();
        multipleRules.add(dayRestrictionRule);
        multipleRules.add(conditionalPurchaseRule);
        MultiplePurchaseRuleDTO andRule = new MultiplePurchaseRuleDTO(MultiplePurchaseRuleType.AND, multipleRules);

        exampleData.add(andRule);

        return exampleData;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        params = beforeEnterEvent.getLocation().getQueryParameters();
        initializeView();
    }
}

