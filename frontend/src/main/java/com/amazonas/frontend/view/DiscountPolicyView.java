package com.amazonas.frontend.view;

import com.amazonas.common.DiscountDTOs.*;
import com.amazonas.common.PurchaseRuleDTO.PurchaseRuleDTO;
import com.amazonas.common.requests.stores.DiscountDTORequest;
import com.amazonas.common.requests.stores.PurchasePolicyRequest;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("discount-policy")
public class DiscountPolicyView extends BaseLayout implements BeforeEnterObserver {
    private final AppController appController;
    private TreeGrid<Object> treeGrid;
    private DiscountRuleTranslator translator;
    private String storeId;

    public DiscountPolicyView(AppController appController) {
        super(appController);
        this.appController = appController;
        //initializeView();
    }

    private void initializeView() {
        H2 title = new H2("Discount Policy");
        title.getStyle().set("text-align", "center");
        storeId = getParam("storeid");

        // Button for adding new discount policy
        Button addButton = new Button("Add New Discount Policy", new Icon(VaadinIcon.PLUS));
        addButton.addClickListener(event -> showAddPolicyDialog());

        // Layout for title and button
        HorizontalLayout header = new HorizontalLayout(title, addButton);
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(true);
        header.setSpacing(true);

        treeGrid = new TreeGrid<>(Object.class);


        List<DiscountComponentDTO> rules;
        try{
            rules = appController.postByEndpoint(Endpoints.GET_DISCOUNT_RULE_DTO, storeId);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
            return;
        }
        List<DiscountComponentDTO> exampleData = createExampleData();
        translator = new DiscountRuleTranslator();
        //treeGrid = translator.translateToTreeGrid(exampleData);
        treeGrid = translator.translateToTreeGrid(rules);

        // Button for deleting discount policy
        Button deleteButton = new Button("Delete Discount Policy", new Icon(VaadinIcon.TRASH));
        deleteButton.addClickListener(event ->
            {
                treeGrid = translator.translateToTreeGrid(new ArrayList<>());
                try{
                    appController.postByEndpoint(Endpoints.REMOVE_DISCOUNT_RULE, storeId);
                } catch (ApplicationException e) {
                    openErrorDialog(e.getMessage());
                }
            }
        );

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
        NewDiscountPolicyView dialog = new NewDiscountPolicyView(appController, this);
        dialog.open();
    }

    public void setRules(List<DiscountComponentDTO> rules) {
        treeGrid = translator.translateToTreeGrid(rules);
        DiscountDTORequest request = new DiscountDTORequest(storeId, rules.getFirst());
        try{
            appController.postByEndpoint(Endpoints.ADD_PURCHASE_POLICY, request);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
    }

    private List<DiscountComponentDTO> createExampleData() {
        List<DiscountComponentDTO> exampleData = new ArrayList<>();

        // Example discount data creation
        SimpleDiscountDTO simpleDiscount = new SimpleDiscountDTO(HierarchyLevel.ProductLevel, "Product123", 10);
        UnaryConditionDTO condition1 = new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_SOME_PRODUCT, 3, "Product123");
        UnaryConditionDTO condition2 = new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, 50, "Product123");
        MultipleConditionDTO orConditions = new MultipleConditionDTO( MultipleConditionType.OR, List.of(condition1, condition2));
        ComplexDiscountDTO complexDiscount = new ComplexDiscountDTO(orConditions, simpleDiscount);
        SimpleDiscountDTO simpleDiscount2 = new SimpleDiscountDTO(HierarchyLevel.CategoryLevel, "Category123", 20);
        List<DiscountComponentDTO> multipleDiscounts = new ArrayList<>();
        multipleDiscounts.add(complexDiscount);
        multipleDiscounts.add(simpleDiscount2);
        MultipleDiscountDTO andDiscount = new MultipleDiscountDTO(MultipleDiscountType.MAXIMUM_PRICE,multipleDiscounts);

        exampleData.add(andDiscount);

        return exampleData;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(!beforeEnterEvent.getNavigationTarget().equals(DiscountPolicyView.class)){
            return;
        }
        storeId = beforeEnterEvent.getLocation().getQueryParameters().getParameters().get("storeid").getFirst();
        initializeView();
    }
}
