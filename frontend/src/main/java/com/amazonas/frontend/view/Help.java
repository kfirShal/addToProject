package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("Help")
public class Help extends Profile {

    public Help(AppController appController) {
        super(appController);
        returnToMainIfNotLogged();

        // Initialize layout
        VerticalLayout helpLayout = new VerticalLayout();

        // Add title
        Paragraph title = new Paragraph("Help Center");
        title.getElement().getStyle().set("font-size", "24px").set("font-weight", "bold");
        helpLayout.add(title);

        // Add FAQ section
        Accordion faqAccordion = new Accordion();

        // Sample FAQ items
        faqAccordion.add("How to create an account?", new Paragraph("To create an account, click on the 'Sign Up' button on the home page and fill in the required information."));
        faqAccordion.add("How to reset my password?", new Paragraph("To reset your password, click on 'Forgot Password' on the login page and follow the instructions."));
        faqAccordion.add("How to contact support?", new Paragraph("You can contact our support team by emailing support@amazonas.com or calling 1-800-123-4567."));

        helpLayout.add(faqAccordion);

        // Add contact information section
        Paragraph contactTitle = new Paragraph("Contact Us");
        contactTitle.getElement().getStyle().set("font-size", "20px").set("font-weight", "bold");
        Paragraph contactInfo = new Paragraph(
                "For further assistance, you can reach out to our support team:\n" +
                        "Email: support@amazonas.com\n" +
                        "Phone: 1-800-123-4567\n" +
                        "Address: 123 Amazonas Lane, Tech City, TC 12345"
        );

        helpLayout.add(contactTitle, contactInfo);

        // Add helpLayout to the main layout
        content.add(helpLayout);
    }
}
