package com.amazonas.frontend.view;

import com.amazonas.common.DiscountDTOs.*;
import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.List;

@Route("new-discount-policy")
public class NewDiscountPolicyView extends Dialog {
    private final AppController appController;
    private final DiscountPolicyView discountPolicyView;
    private VirtualList<DiscountComponentDTO> rulesList;
    private VirtualList<DiscountConditionDTO> conditionsList;
    private ComboBox<String> products;
    private ComboBox<String> categories;
    private TextField percent;
    private TextField quantity;
    private List<DiscountConditionDTO> conditions = new ArrayList<>();
    private List<DiscountComponentDTO> rules = new ArrayList<>();
    private Button minimumItems;
    private Button minimumPrice;
    private Button storeLevel;
    private TextField price;


    public NewDiscountPolicyView(AppController appController, DiscountPolicyView discountPolicyView) {
        this.appController = appController;
        this.discountPolicyView = discountPolicyView;
        setCloseOnOutsideClick(false); // Prevent closing dialog on outside click
        setResizable(true); // Allow resizing of the dialog
        setWidth("800px"); // Set initial width
        setHeight("600px"); // Set initial height
        initializeView();
    }
    private void initializeView() {
        HorizontalLayout header = new HorizontalLayout();
        H2 title = new H2("Compose Discount Policy");
        title.getStyle().set("text-align", "center");
        header.add(title);


        HorizontalLayout actions = new HorizontalLayout();
        Button addButton = new Button("Add", event-> addRuleToList());
        Button makeMultipleConditions = new Button("Make Multiple Conditions", event->{
            if(conditions.size()>1){
                openConditionsDialog();
            } else {
                Notification.show("You need to have at list two rules in the list", 5000, Notification.Position.MIDDLE);
            }
        });
        Button makeMultipleRules = new Button("Make Multiple Rules", event->{   
            if(rules.size()>1){
            openRulesDialog();
        } else {
            Notification.show("You need to have at list two rules in the list", 5000, Notification.Position.MIDDLE);
        }});
        Button saveButton = new Button("Save", event->{
            saveRules();
            close();
        });
        Button cancelButton = new Button("Cancel", event->close());

        actions.add(addButton, makeMultipleConditions, makeMultipleRules, saveButton, cancelButton);
        actions.setSpacing(true);
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        conditionsList = new VirtualList<>();
        conditionsList.setHeight("300px");
        rulesList = new VirtualList<>();
        rulesList.setHeight("300px");

        VerticalLayout layout = new VerticalLayout(header,createButtonsLayout(), new H6("Conditions list"),conditionsList,new H6("Rules list"), rulesList,actions);
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        add(layout);
        setSizeFull();// Set dialog to take up full available space
        setUpButtonsListeners();


    }


    private void setUpButtonsListeners() {
        storeLevel.addClickListener(event -> {
            if(validateFieldsEmpty(List.of(products, categories,quantity,price))){
                if(isNotEmpty(percent)){
                    SimpleDiscountDTO rule = new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", Integer.parseInt(percent.getValue()));
                    rules.add(rule);
                }
            }
        });



        minimumItems.addClickListener(event -> {
            if(products.getValue() != null && categories.getValue() != null && validateFieldsEmpty(List.of(percent,price))){
                Notification.show("You need to choose between products and categories", 5000, Notification.Position.MIDDLE);
            }
            else if(validateFieldsEmpty(List.of(categories,percent,price))){
                if(isNotEmpty(quantity)){
                    UnaryConditionDTO condition = new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_SOME_PRODUCT, Integer.parseInt(quantity.getValue()),products.getValue());
                    conditions.add(condition);
                }
            }
            else if(validateFieldsEmpty(List.of(products,percent,price))){
                if(isNotEmpty(quantity)){
                    UnaryConditionDTO condition = new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_ITEMS_FROM_CATEGORY, Integer.parseInt(quantity.getValue()),categories.getValue());
                    conditions.add(condition);
                }
            }
            else if (validateFieldsEmpty(List.of(products,categories,price))){

                Notification.show("You need to choose between products and categories", 5000, Notification.Position.MIDDLE);

            }
            else if(validateFieldsEmpty(List.of(categories,price))){
                if(isNotEmpty(quantity) && isNotEmpty(percent)){
                    ComplexDiscountDTO rule = new ComplexDiscountDTO(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_SOME_PRODUCT, Integer.parseInt(quantity.getValue()),products.getValue()),new SimpleDiscountDTO(HierarchyLevel.ProductLevel, products.getValue(), Integer.parseInt(percent.getValue())));
                    rules.add(rule);
                }
            }
            else if (validateFieldsEmpty(List.of(products,price))){
                if(isNotEmpty(quantity) && isNotEmpty(percent)){
                    ComplexDiscountDTO rule = new ComplexDiscountDTO(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_ITEMS_FROM_CATEGORY, Integer.parseInt(quantity.getValue()),categories.getValue()),new SimpleDiscountDTO(HierarchyLevel.CategoryLevel, categories.getValue(), Integer.parseInt(percent.getValue())));
                    rules.add(rule);
                }
            }
            else{
                Notification.show("You fill in too many fields, please fill only the quantity", 5000, Notification.Position.MIDDLE);
            }
        });

        minimumPrice.addClickListener(event -> {

            if(validateFieldsEmpty(List.of(products,categories,percent,quantity))){
                if(isNotEmpty(price)){
                    UnaryConditionDTO condition = new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, Integer.parseInt(price.getValue()),"");
                    conditions.add(condition);
                }
            }
            else if(validateFieldsEmpty(List.of(products,quantity,percent))){
                if(isNotEmpty(price)){
                    UnaryConditionDTO condition = new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, Integer.parseInt(price.getValue()),categories.getValue());
                    conditions.add(condition);
                }
            }
            else if(validateFieldsEmpty((List.of(categories,quantity,percent)))){
                if(isNotEmpty(price)){
                    UnaryConditionDTO condition = new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, Integer.parseInt(price.getValue()),products.getValue());
                    conditions.add(condition);
                }
            }
            else if(validateFieldsEmpty(List.of(products,categories,quantity))){
                if(isNotEmpty(price) && isNotEmpty(percent)){
                    ComplexDiscountDTO rule = new ComplexDiscountDTO(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, Integer.parseInt(price.getValue()),""),new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", Integer.parseInt(percent.getValue())));
                    rules.add(rule);
                }
            }
            else if(validateFieldsEmpty(List.of(categories,quantity))){
                if(isNotEmpty(price) && isNotEmpty(percent)){
                    ComplexDiscountDTO rule = new ComplexDiscountDTO(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, Integer.parseInt(price.getValue()),""),new SimpleDiscountDTO(HierarchyLevel.ProductLevel, products.getValue(), Integer.parseInt(percent.getValue())));
                    rules.add(rule);
                }
            }
            else if(validateFieldsEmpty(List.of(products,quantity))){
                if(isNotEmpty(price) && isNotEmpty(percent)){
                    ComplexDiscountDTO rule = new ComplexDiscountDTO(new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, Integer.parseInt(price.getValue()),""),new SimpleDiscountDTO(HierarchyLevel.CategoryLevel, categories.getValue(), Integer.parseInt(percent.getValue())));
                    rules.add(rule);
                }
            }
            else{
                Notification.show("You fill in too many fields, please fill only the price", 5000, Notification.Position.MIDDLE);
            }
        });

    }
    private void updateComboBoxes() {
        if(products.getValue() != null && validateFieldsEmpty(List.of(categories,quantity,price))){
            if(isNotEmpty(percent)){
                SimpleDiscountDTO rule = new SimpleDiscountDTO(HierarchyLevel.ProductLevel, products.getValue(), Integer.parseInt(percent.getValue()));
                rules.add(rule);
            }
        }

        if(categories.getValue() != null && validateFieldsEmpty(List.of(products,quantity,price))){
            if(isNotEmpty(percent)){
                SimpleDiscountDTO rule = new SimpleDiscountDTO(HierarchyLevel.CategoryLevel, categories.getValue(), Integer.parseInt(percent.getValue()));
                rules.add(rule);
            }
        }
    }


    private void openRulesDialog() {
        Dialog choiceDialog = new Dialog();
        choiceDialog.setCloseOnEsc(false);
        choiceDialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);

        H3 message = new H3("Please choose between the following:");
        message.getStyle().set("text-align", "center");

        Button minimumButton = new Button("Minimum price", event -> {
            handleRulesDialogSelection(MultipleDiscountType.MINIMUM_PRICE);
            choiceDialog.close();
        });

        Button maximumButton = new Button("Maximum price", event -> {
            handleRulesDialogSelection(MultipleDiscountType.MAXIMUM_PRICE);
            choiceDialog.close();
        });

        Button additionButton = new Button("Addition", event -> {
            handleRulesDialogSelection(MultipleDiscountType.ADDITION);
            choiceDialog.close();
        });
        HorizontalLayout buttonLayout = new HorizontalLayout(minimumButton, maximumButton, additionButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        dialogLayout.add(message, buttonLayout);

        choiceDialog.add(dialogLayout);
        choiceDialog.open();
    }

    private void handleRulesDialogSelection(MultipleDiscountType multipleDiscountType) {
        MultipleDiscountDTO multipleDiscountDTO = new MultipleDiscountDTO(multipleDiscountType, rules);
        rules = new ArrayList<>();
        rules.add(multipleDiscountDTO);
        rulesList.setItems(rules);
    }


    private void openConditionsDialog() {
        Dialog choiceDialog = new Dialog();
        choiceDialog.setCloseOnEsc(false);
        choiceDialog.setCloseOnOutsideClick(false);

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);

        H3 message = new H3("Please choose between the following:");
        message.getStyle().set("text-align", "center");

        Button orButton = new Button("Or", event -> {
            handleConditionsDialogSelection(MultipleConditionType.OR);
            choiceDialog.close();
        });

        Button andButton = new Button("And", event -> {
            handleConditionsDialogSelection(MultipleConditionType.AND);
            choiceDialog.close();
        });

        Button xorButton = new Button("Xor", event -> {
            handleConditionsDialogSelection(MultipleConditionType.XOR);
            choiceDialog.close();
        });
        HorizontalLayout buttonLayout = new HorizontalLayout(orButton, andButton, xorButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        dialogLayout.add(message, buttonLayout);

        choiceDialog.add(dialogLayout);
        choiceDialog.open();
    }

    private void handleConditionsDialogSelection(MultipleConditionType multipleConditionType) {
        MultipleConditionDTO multipleConditionDTO = new MultipleConditionDTO(multipleConditionType, conditions);
        conditions = new ArrayList<>();
        conditions.add(multipleConditionDTO);
        conditionsList.setItems(conditions);
    }


    private VerticalLayout createButtonsLayout() {
        storeLevel = new Button("Store Level");

        products = new ComboBox<>("Products");
        products.setItems("Product 1", "Product 2", "Product 3", "Product 4");

        categories = new ComboBox<>("Categories");
        categories.setItems("Computers", "Smartphones", "Tablets","Headphones");

        percent = new TextField("% Percent");
        percent.setClearButtonVisible(true);

        minimumItems = new Button("Minimum Items");
        minimumPrice = new Button("Minimum Price");

        quantity = new TextField("Quantity");
        quantity.setClearButtonVisible(true);

        price = new TextField("Price");
        price.setClearButtonVisible(true);

        VerticalLayout typesLayout = new VerticalLayout();
        HorizontalLayout typesLayout2 = new HorizontalLayout(storeLevel, percent, products, categories);
        typesLayout2.setAlignItems(FlexComponent.Alignment.BASELINE);
        typesLayout.add(new H3("Types"),typesLayout2);
        typesLayout.setSpacing(true);

        VerticalLayout conditionsLayout = new VerticalLayout();
        HorizontalLayout conditionsLayout2 = new HorizontalLayout(minimumItems, minimumPrice, quantity, price);
        conditionsLayout2.setAlignItems(FlexComponent.Alignment.BASELINE);
        conditionsLayout.add(new H3("Conditions"),conditionsLayout2);
        conditionsLayout.setSpacing(true);



        VerticalLayout buttonsLayout = new VerticalLayout(typesLayout, conditionsLayout);
        buttonsLayout.setSpacing(true);
        return buttonsLayout;

    }



    private void addRuleToList() {
        updateComboBoxes();
        if(!rules.isEmpty()){
            rulesList.setItems(rules);
        }
        if(!conditions.isEmpty()){
            conditionsList.setItems(conditions);
        }
        clearFields();

    }


    private void saveRules() {
        if(conditions.size() >1){
            handleConditionsDialogSelection(MultipleConditionType.OR);
        }
        if(rules.size() >1){
            handleRulesDialogSelection(MultipleDiscountType.MINIMUM_PRICE);
        }
        if(conditions.size()==1){
            ComplexDiscountDTO rule = new ComplexDiscountDTO(conditions.getFirst(),rules.getFirst());
            rules = new ArrayList<>();
            rules.add(rule);

        }
        discountPolicyView.setRules(rules);
    }



    private Boolean isNotEmpty(TextField field){
        if(field.getValue() != null && !field.isEmpty() && !field.getValue().isEmpty()){
            return true;
        }
        return false;
    }

    private Boolean validateFieldsEmpty(List<Component> fields) {
        for(Component field : fields){
            if(field instanceof TextField){
                if(!((TextField) field).isEmpty()){
                    return false;
                }
            }
            if(field instanceof ComboBox){
                if(!((ComboBox<?>) field).isEmpty()){
                    return false;
                }
            }
        }
        return true;
    }


    private void clearFields() {
        products.clear();
        categories.clear();
        percent.clear();
        quantity.clear();
        price.clear();
    }
}
