package com.amazonas.frontend.view;

import com.amazonas.common.PurchaseRuleDTO.*;
import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("new-policy-dialog")
public class NewPolicyView extends Dialog {
    private final AppController appController;
    private final PurchasePolicyView purchasePolicyView;
    private ComboBox<Integer> ageRestrictionComboBox;
    private ComboBox<Integer> maxUniqueProductsComboBox;
    private ComboBox<Integer> minUniqueProductsComboBox;
    private DatePicker dayRestrictionStart;
    private DatePicker dayRestrictionEnd;
    private TimePicker hourRestrictionStart;
    private TimePicker hourRestrictionEnd;
    private Button categoryLevelButton;
    private Button productLevelButton;
    private HorizontalLayout changingLayout;
    private List<PurchaseRuleDTO> rules = new ArrayList<>();
    private ConditionLevelDTO condRule;
    private VirtualList<PurchaseRuleDTO> rulesList;
    private TextField quantity;
    private ComboBox<String> category;
    private ConditionLevelDTO conditionLevelDTO;
    private ComboBox<String> products;


    public NewPolicyView(AppController appController, PurchasePolicyView purchasePolicyView) {
        this.appController = appController;
        this.purchasePolicyView = purchasePolicyView;
        setCloseOnOutsideClick(false); // Prevent closing dialog on outside click
        setResizable(true); // Allow resizing of the dialog
        setWidth("800px"); // Set initial width
        setHeight("600px"); // Set initial height
        initializeView();
    }


    private void initializeView() {
        HorizontalLayout headers = new HorizontalLayout();
        H2 title = new H2("Compose New Policy");
        title.getStyle().set("text-align", "center");
        headers.add(title);

        HorizontalLayout actionButtons = new HorizontalLayout();
        Button addButton = new Button("Add", event ->{
            addRuleToList();
        });
        Button makeMultipleButton = new Button("Make multiple", event ->{
            if(rules.size()>1){
                openChoiceDialog();
            } else {
                Notification.show("You need to have at list two rules in the list", 5000, Notification.Position.MIDDLE);
            }
        });


        Button saveButton = new Button("Save", event -> {
            saveRules();
            close(); // Close dialog after saving
        });
        Button cancelButton = new Button("Cancel", event -> close());
        actionButtons.add(addButton,makeMultipleButton, saveButton, cancelButton);
        actionButtons.setSpacing(true);
        actionButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        rulesList = new VirtualList<>();
        rulesList.setHeight("200px");
        //rulesList.setRenderer(r -> createRuleRenderer(r));
        
        VerticalLayout layout = new VerticalLayout(headers, createButtonLayout(),  rulesList, actionButtons);
        layout.setSizeFull();
        layout.setPadding(true);
        layout.setSpacing(true);

        add(layout);
        setSizeFull(); // Set dialog to take up full available space
        setupConditions();

    }

    private ConditionLevelDTO createConditionLevelDTO() {
//        if (quantity.getValue() == null || quantity.getValue().isEmpty()) {
//            Notification.show("Quantity is required", 3000, Notification.Position.MIDDLE);
//        }

        int qty;
        try {
            qty = Integer.parseInt(quantity.getValue());
        } catch (NumberFormatException e) {
            Notification.show("Quantity must be a number", 3000, Notification.Position.MIDDLE);
            return null;
        }

        if (category != null && category.getValue() != null) {
            return new ConditionLevelDTO(ConditionLevelType.CATEGORY_LEVEL, category.getValue(), qty);
        } else if (products != null && products.getValue() != null) {
            return new ConditionLevelDTO(ConditionLevelType.PRODUCT_LEVEL, products.getValue(), qty);
        } else {
            Notification.show("Category or Product must be selected", 3000, Notification.Position.MIDDLE);
            return null;
        }
    }

    private void setupConditions() {

        quantity = new TextField();
        quantity.setLabel("Quantity");
        quantity.setClearButtonVisible(true);

        category = new ComboBox<>("Categories");
        category.setItems("Books", "Electronics", "Clothing", "Food");

        products = new ComboBox<>("Products");
        products.setItems("product 1", "Product 2", "Product 3", "Product 4");

        categoryLevelButton.addClickListener(event -> {
            if (changingLayout.getChildren().noneMatch(component -> component.equals(category))) {
                changingLayout.add(category, quantity);
                changingLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
            }

        });

        productLevelButton.addClickListener(event -> {
            if (changingLayout.getChildren().noneMatch(component -> component.equals(products))) {
                changingLayout.add(products, quantity);
                changingLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
            }

        });
    }


    private void saveRules() {

        if(rules.size()>1){
            handleChoiceDialogSelection(MultiplePurchaseRuleType.OR);
        }

        //TODO : send the rules list to PurchasePolicyView and close the dialog
        purchasePolicyView.setRules(rules);
        close();
    }

    private void addRuleToList() {
        createTypeRule();
        if (ageRestrictionComboBox.isEmpty() && maxUniqueProductsComboBox.isEmpty() && minUniqueProductsComboBox.isEmpty() &&
                dayRestrictionStart.isEmpty() && hourRestrictionStart.isEmpty()) {
            Notification.show("Please fill in at least one type rule", 5000, Notification.Position.MIDDLE);
            return;
        }
        if(condRule!=null){
            PurchaseRuleDTO purchaseRule = rules.removeLast();
            ConditionalPurchaseRuleDTO conditionalPurchaseRuleDTO = new ConditionalPurchaseRuleDTO(condRule, purchaseRule);
            rules.add(conditionalPurchaseRuleDTO);
            condRule = null;
        }
        if (!rules.isEmpty()) {
            rulesList.setItems(rules);
            clearFields();
        }

    }

    private void clearFields() {
        ageRestrictionComboBox.clear();
        maxUniqueProductsComboBox.clear();
        minUniqueProductsComboBox.clear();
        dayRestrictionStart.clear();
        dayRestrictionEnd.clear();
        hourRestrictionStart.clear();
        hourRestrictionEnd.clear();
        category.clear();
        products.clear();
        quantity.clear();
    }

    private void createTypeRule() {
        if(ageRestrictionComboBox.getValue() != null){
            PurchaseRuleDTO ageRestrictionRule = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.AGE_RESTRICTION, ageRestrictionComboBox.getValue());
            rules.add(ageRestrictionRule);
        }
        if(minUniqueProductsComboBox.getValue() != null){
            PurchaseRuleDTO minUniqueProductsRule = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS, minUniqueProductsComboBox.getValue());
            rules.add(minUniqueProductsRule);
        }
        if(maxUniqueProductsComboBox.getValue() != null){
            PurchaseRuleDTO maxUniqueProductsRule = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MAX_UNIQUE_PRODUCTS, maxUniqueProductsComboBox.getValue());
            rules.add(maxUniqueProductsRule);
        }
        if(dayRestrictionStart.getValue() != null){
            PurchaseRuleDTO dayRestrictionRule = new DatePurchaseRuleDTO(DatePurchaseRuleType.DAY_RESTRICTION, dayRestrictionStart.getValue(), dayRestrictionEnd.getValue());
            rules.add(dayRestrictionRule);
        }
        if(hourRestrictionStart.getValue() != null){
            PurchaseRuleDTO hourRestrictionRule = new DatePurchaseRuleDTO(DatePurchaseRuleType.HOUR_RESTRICTION, hourRestrictionStart.getValue(), hourRestrictionEnd.getValue());
            rules.add(hourRestrictionRule);
        }

        if(quantity.getValue()!=null){
            if(category.getValue()!=null){
                condRule =  new ConditionLevelDTO(ConditionLevelType.CATEGORY_LEVEL, category.getValue(), Integer.parseInt(quantity.getValue()));

            }
            if(products.getValue()!=null){
                condRule = new ConditionLevelDTO(ConditionLevelType.PRODUCT_LEVEL, products.getValue(), Integer.parseInt(quantity.getValue()));

            }
        }


    }

    private void openChoiceDialog() {
    Dialog choiceDialog = new Dialog();
    choiceDialog.setCloseOnEsc(false);
    choiceDialog.setCloseOnOutsideClick(false);

    VerticalLayout dialogLayout = new VerticalLayout();
    dialogLayout.setPadding(true);
    dialogLayout.setSpacing(true);

    H3 message = new H3("Please choose between the following:");
    message.getStyle().set("text-align", "center");

    Button orButton = new Button("Or", event -> {
        handleChoiceDialogSelection(MultiplePurchaseRuleType.OR);
        choiceDialog.close();
    });

    Button andButton = new Button("And", event -> {
        handleChoiceDialogSelection(MultiplePurchaseRuleType.AND);
        choiceDialog.close();
    });

    HorizontalLayout buttonLayout = new HorizontalLayout(orButton, andButton);
    buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);

    dialogLayout.add(message, buttonLayout);

    choiceDialog.add(dialogLayout);
    choiceDialog.open();
}

    private void handleChoiceDialogSelection(MultiplePurchaseRuleType multiplePurchaseRuleType) {
        MultiplePurchaseRuleDTO multiplePurchaseRuleDTO = new MultiplePurchaseRuleDTO(multiplePurchaseRuleType, rules);
        rules = new ArrayList<>();
        rules.add(multiplePurchaseRuleDTO);
        rulesList.setItems(rules);
    }

    private VerticalLayout createButtonLayout() {
        // ComboBox for Age Restriction
        ageRestrictionComboBox = new ComboBox<>("Age Restriction");
        ageRestrictionComboBox.setItems(generateRange(5, 99));

        ageRestrictionComboBox.getStyle().setWidth("120px");


        // ComboBox for Max Unique Products
        maxUniqueProductsComboBox = new ComboBox<>("Max Unique Products");
        maxUniqueProductsComboBox.setItems(generateRange(1, 100));



        // ComboBox for Min Unique Products
        minUniqueProductsComboBox = new ComboBox<>("Min Unique Products");
        minUniqueProductsComboBox.setItems(generateRange(1, 100));


        dayRestrictionStart = new DatePicker();
        dayRestrictionStart.setLabel("From day");
        dayRestrictionStart.setPlaceholder("Day restriction");
        dayRestrictionStart.setClearButtonVisible(true);


        dayRestrictionEnd = new DatePicker();
        dayRestrictionEnd.setLabel("To day");
        dayRestrictionEnd.setPlaceholder("Day restriction");
        dayRestrictionEnd.setClearButtonVisible(true);


        hourRestrictionStart = new TimePicker();
        hourRestrictionStart.setLabel("From hour");
        hourRestrictionStart.setPlaceholder("Hour restriction");
        hourRestrictionStart.setClearButtonVisible(true);


        hourRestrictionEnd = new TimePicker();
        hourRestrictionEnd.setLabel("To hour");
        hourRestrictionEnd.setPlaceholder("Hour restriction");
        hourRestrictionEnd.setClearButtonVisible(true);


        // Buttons for conditions
        categoryLevelButton = new Button("Category Level");
        productLevelButton = new Button("Product Level");

        // Layout for types buttons
        VerticalLayout typesLayout = new VerticalLayout();
        typesLayout.add(new H3("Types"),
                new HorizontalLayout(ageRestrictionComboBox, maxUniqueProductsComboBox, minUniqueProductsComboBox,
                        dayRestrictionStart, dayRestrictionEnd, hourRestrictionStart, hourRestrictionEnd));
        typesLayout.setSpacing(true);

        // Layout for conditions buttons
        VerticalLayout conditionsLayout = new VerticalLayout();
        changingLayout = new HorizontalLayout(categoryLevelButton, productLevelButton);
        conditionsLayout.add(new H3("Conditions"), changingLayout
                );
        conditionsLayout.setSpacing(true);

        // Main button layout
        VerticalLayout buttonBox = new VerticalLayout(typesLayout, conditionsLayout);
        buttonBox.setSpacing(true);

        return buttonBox;
    }

    private List<Integer> generateRange(int start, int end) {
        List<Integer> range = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            range.add(i);
        }
        return range;
    }

}

