package com.amazonas.backend.service;

import com.amazonas.backend.business.payment.PaymentService;
import com.amazonas.backend.business.shipping.ShippingService;
import com.amazonas.backend.service.requests.payment.PaymentServiceManagementRequest;
import com.amazonas.backend.service.requests.shipping.ShippingServiceManagementRequest;
import com.amazonas.common.DiscountDTOs.DiscountComponentDTO;
import com.amazonas.common.PurchaseRuleDTO.PurchaseRuleDTO;
import com.amazonas.common.dtos.*;
import com.amazonas.common.permissions.profiles.AdminPermissionsProfile;
import com.amazonas.common.permissions.profiles.DefaultPermissionsProfile;
import com.amazonas.common.permissions.profiles.PermissionsProfile;
import com.amazonas.common.permissions.profiles.UserPermissionsProfile;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.RequestBuilder;
import com.amazonas.common.requests.auth.AuthenticationRequest;
import com.amazonas.common.requests.notifications.NotificationRequest;
import com.amazonas.common.requests.shipping.ShipmentRequest;
import com.amazonas.common.requests.stores.*;
import com.amazonas.common.requests.users.CartRequest;
import com.amazonas.common.requests.users.LoginRequest;
import com.amazonas.common.requests.users.RegisterRequest;
import com.amazonas.common.utils.APIFetcher;
import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Rating;
import com.amazonas.common.utils.Response;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.io.FileUtils;

@Component("initialRunFileExecutor")
public class InitialRunFileExecutor {
    private final AuthenticationService authenticationService;
    private final ExternalServicesService externalServicesService;
    private final MarketService marketService;
    private final NotificationsService notificationsService;
    private final StoresService storesService;
    private final UserProfilesService userProfilesService;
    private final PermissionsService permissionsService;
    private final AppController appController;

    @EventListener
    public void handleApplicationReadyEvent(ApplicationReadyEvent event) {
        String initialRunCode;
        try {
            File file = new File("C:\\Users\\yuval\\git\\sadna\\backend\\src\\main\\resources\\InitialRunFile.txt");
            try(BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))){
                initialRunCode = new String(stream.readAllBytes());
            }
        }
        catch (Exception e) {
            System.out.println("Cannot find the initial run file");
            return;
        }
        try{
            runCode(initialRunCode);
        } catch(Exception e) {
            System.out.println("Error in initialRunFile.txt: " + e.getMessage());
            System.exit(1);
        }
    }

    private InputStream getFileAsIOStream(final String fileName)
    {
        InputStream ioStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(fileName);

        if (ioStream == null) {
            throw new IllegalArgumentException(fileName + " is not found");
        }
        return ioStream;
    }

    public InitialRunFileExecutor(AuthenticationService authenticationService,
                                  ExternalServicesService externalServicesService,
                                  MarketService marketService,
                                  NotificationsService notificationsService,
                                  StoresService storesService,
                                  UserProfilesService userProfilesService, PermissionsService permissionsService) {
        this.authenticationService = authenticationService;
        this.externalServicesService = externalServicesService;
        this.marketService = marketService;
        this.notificationsService = notificationsService;
        this.storesService = storesService;
        this.userProfilesService = userProfilesService;
        this.permissionsService = permissionsService;
        this.appController = new AppController();
    }

    public Response runCode(String code){
        List<String[]> operations;
        try {
            operations = parser(code);
        }
        catch (Exception e) {
            return new Response(e.getMessage(), false, "");
        }
        appController.enterAsGuest();
        for (String[] operation : operations) {
            try {
                String result= executeOperation(operation);
                Response response = JsonUtils.deserialize(result, Response.class);
                if(!response.success()) {
                    return new Response("The operation: \"" +
                                        operationToString(operation) +
                                        "\" failed with the error: \"" +
                                        response.message() + "\"",
                                        false, "");
                }
            }
            catch (Exception e) {
                return new Response(e.getMessage(), false, "");
            }
        }
        return new Response(true);
    }

    public String executeOperation(String[] operation) {
        if (operation == null || operation.length == 0) {
            throw new IllegalArgumentException("Operation is empty");
        }
        switch (operation[0]) {

            //====================================================================== |
            //========================== Authentication ============================ |
            //====================================================================== |

            case "register" -> {
                numOfArgumentsChecker(operation, 4);
                boolean res = appController.register(operation[1],
                                                    operation[2],
                                                    operation[3],
                                                    operation[3],
                                                    getDate("register",
                                                            operation[4],
                                                            4));
                if(res) {
                    return Response.getOk();
                }
                else {
                    return Response.getError("Can't execute the registration");
                }
            }
            case "login" -> {
                numOfArgumentsChecker(operation, 4);
                boolean res = appController.login(operation[1],
                                                  operation[2]);
                if(res) {
                    return Response.getOk();
                }
                else {
                    return Response.getError("Can't execute the logging in");
                }
            }
            case "logout" -> {
                numOfArgumentsChecker(operation, 4);
                boolean res = appController.logout();
                if(res) {
                    return Response.getOk();
                }
                else {
                    return Response.getError("Can't execute the logging in");
                }
            }

            //====================================================================== |
            //======================== External Services =========================== |
            //====================================================================== |

            case "sendShipment" -> {
                numOfArgumentsChecker(operation, 3);
                return externalServicesService.sendShipment(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                                                                                            appController.getToken(),
                                                                                            JsonUtils.serialize(new ShipmentRequest(operation[1],
                                                                                                                                    operation[2],
                                                                                                                                    operation[3])))));
            }
            case "addShippingService" -> {
                numOfArgumentsChecker(operation, 1);
                return externalServicesService.addShippingService(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                                                                                                  appController.getToken(),
                                                                                                  JsonUtils.serialize(new ShippingServiceManagementRequest(operation[1],
                                                                                                                                                    new ShippingService())))));
            }
            case "removeShippingService" -> {
                numOfArgumentsChecker(operation, 1);
                return externalServicesService.removeShippingService(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                                                                                                     appController.getToken(),
                                                                                                     JsonUtils.serialize(new ShippingServiceManagementRequest(operation[1],
                                                                                                                                                       new ShippingService())))));
            }
            /*
            case "updateShippingService" -> {
                numOfArgumentsChecker(operation, 1);
                return externalServicesService.updateShippingService(JsonUtils.serialize(new Request(requestUserId,
                                                                                                     requestToken,
                                                                                                     JsonUtils.serialize(new ShippingServiceManagementRequest(operation[1],
                                                                                                                                                       new ShippingService())))));
            }
             */
            case "addPaymentService" -> {
                numOfArgumentsChecker(operation, 1);
                return externalServicesService.addPaymentService(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                                 JsonUtils.serialize(new PaymentServiceManagementRequest(operation[1],
                                                                                                                                                  new PaymentService())))));
            }
            case "removePaymentService" -> {
                numOfArgumentsChecker(operation, 1);
                return externalServicesService.removePaymentService(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                                    JsonUtils.serialize(new PaymentServiceManagementRequest(operation[1],
                                                                                                                                                     new PaymentService())))));
            }
            /*
            case "updatePaymentService" -> {
                numOfArgumentsChecker(operation, 1);
                return externalServicesService.updatePaymentService(JsonUtils.serialize(new Request(requestUserId,
                                                                                                    requestToken,
                                                                                                    JsonUtils.serialize(new PaymentServiceManagementRequest(operation[1],
                                                                                                                                                     new PaymentService())))));
            }
             */

            //====================================================================== |
            //=============================== Market =============================== |
            //====================================================================== |

            case "startMarket" -> {
                numOfArgumentsChecker(operation, 0);
                return marketService.startMarket(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                 "")));
            }
            case "shutdown" -> {
                numOfArgumentsChecker(operation, 0);
                return marketService.shutdown(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                              "")));
            }

            //====================================================================== |
            //============================ Notification ============================ |
            //====================================================================== |

            case "sendNotification" -> {
                numOfArgumentsChecker(operation, 4);
                return notificationsService.sendNotification(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                             JsonUtils.serialize(new NotificationRequest(operation[1],
                                                                                                                                         operation[2],
                                                                                                                                         operation[3],
                                                                                                                                         operation[4])))));
            }
            case "setReadValue" -> {
                numOfArgumentsChecker(operation, 2);
                boolean readValue = getBoolean("setReadValue", operation[2], 2);
                return notificationsService.setReadValue(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                         JsonUtils.serialize(new NotificationRequest(operation[1],
                                                                                                                                     readValue)))));
            }
            case "deleteNotification" -> {
                numOfArgumentsChecker(operation, 1);
                return notificationsService.deleteNotification(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                               JsonUtils.serialize(new NotificationRequest(operation[1],
                                                                                                                                           false)))));
            }

            //====================================================================== |
            //=============================== Store ================================ |
            //====================================================================== |

            case "addStore" -> {
                numOfArgumentsChecker(operation, 3);
                return storesService.addStore(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                              JsonUtils.serialize(new StoreCreationRequest(operation[2],
                                                                                                                           operation[3],
                                                                                                                           operation[1])))));
            }
            case "openStore" -> {
                numOfArgumentsChecker(operation, 1);
                return storesService.openStore(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                               operation[1])));
            }
            case "closeStore" -> {
                numOfArgumentsChecker(operation, 1);
                return storesService.closeStore(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                operation[1])));
            }
            case "addProduct" -> {
                numOfArgumentsChecker(operation, 7);
                double price = getDouble("addProduct", operation[4], 4);
                Rating rating = getRating("addProduct", operation[7], 7);
                return storesService.addProduct(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new ProductRequest(operation[1],
                                                                                                                       new Product(operation[2],
                                                                                                                                   operation[3],
                                                                                                                                   price,
                                                                                                                                   operation[5],
                                                                                                                                   operation[6],
                                                                                                                                   rating))))));
            }
            case "updateProduct" -> {
                numOfArgumentsChecker(operation, 7);
                double price = getDouble("updateProduct", operation[4], 4);
                Rating rating = getRating("updateProduct", operation[7], 7);
                return storesService.updateProduct(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new ProductRequest(operation[1],
                                                                                                                       new Product(operation[2],
                                                                                                                                   operation[3],
                                                                                                                                   price,
                                                                                                                                   operation[5],
                                                                                                                                   operation[6],
                                                                                                                                   rating))))));
            }
            case "removeProduct" -> {
                numOfArgumentsChecker(operation, 2);
                return storesService.removeProduct(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new ProductRequest(operation[1],
                                                                                                                       new Product(operation[2]))))));
            }
            case "disableProduct" -> {
                numOfArgumentsChecker(operation, 2);
                return storesService.disableProduct(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new ProductRequest(operation[1],
                                                                                                                       new Product(operation[2]))))));
            }
            case "enableProduct" -> {
                numOfArgumentsChecker(operation, 2);
                return storesService.enableProduct(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new ProductRequest(operation[1],
                                                                                                                       new Product(operation[2]))))));
            }
            case "setProductQuantity" -> {
                numOfArgumentsChecker(operation, 3);
                int quantity = getInteger("setProductQuantity", operation[3], 3);
                return storesService.setProductQuantity(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new ProductRequest(operation[1],
                                                                                                                       new Product(operation[2]),
                                                                                                                       quantity)))));
            }
            case "addOwner" -> {
                numOfArgumentsChecker(operation, 2);
                return storesService.addOwner(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new StoreStaffRequest(operation[1],
                                                                                        appController.getCurrentUserId(),
                                                                                                                          operation[2])))));
            }
            case "removeOwner" -> {
                numOfArgumentsChecker(operation, 2);
                return storesService.removeOwner(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new StoreStaffRequest(operation[1],
                                                                                        appController.getCurrentUserId(),
                                                                                                                          operation[2])))));
            }
            case "addManager" -> {
                numOfArgumentsChecker(operation, 2);
                return storesService.addManager(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new StoreStaffRequest(operation[1],
                                                                                        appController.getCurrentUserId(),
                                                                                                                          operation[2])))));
            }
            case "removeManager" -> {
                numOfArgumentsChecker(operation, 2);
                return storesService.removeManager(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new StoreStaffRequest(operation[1],
                                                                                        appController.getCurrentUserId(),
                                                                                                                          operation[2])))));
            }
            case "addPermissionToManager" -> {
                numOfArgumentsChecker(operation, 3);
                return storesService.addPermissionToManager(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new StorePermissionRequest(operation[1],
                                                                                                                               operation[2],
                                                                                                                               operation[3])))));
            }
            case "removePermissionFromManager" -> {
                numOfArgumentsChecker(operation, 3);
                return storesService.removePermissionFromManager(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new StorePermissionRequest(operation[1],
                                                                                                                               operation[2],
                                                                                                                               operation[3])))));
            }
            case "addDiscountRuleByCFG" -> {
                numOfArgumentsChecker(operation, 2);
                return storesService.addDiscountRuleByCFG(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new DiscountCFGRequest(operation[1],
                                                                                                                           operation[2])))));
            }
            case "deleteAllDiscounts" -> {
                numOfArgumentsChecker(operation, 1);
                return storesService.deleteAllDiscounts(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new DiscountCFGRequest(operation[1],
                                                                                                                           "")))));
            }

            //====================================================================== |
            //=========================== User Profiles ============================ |
            //====================================================================== |

            case "addProductToCart" -> {
                numOfArgumentsChecker(operation, 3);
                int quantity = getInteger("addProductToCart", operation[3], 3);
                return userProfilesService.addProductToCart(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new CartRequest(operation[1],
                                                                                                                    operation[2],
                                                                                                                    quantity)))));
            }
            case "removeProductFromCart" -> {
                numOfArgumentsChecker(operation, 2);
                return userProfilesService.removeProductFromCart(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new CartRequest(operation[1],
                                                                                                                    operation[2],
                                                                                                                    0)))));
            }
            case "changeProductQuantityAtCart" -> {
                numOfArgumentsChecker(operation, 3);
                int quantity = getInteger("addProductToCart", operation[3], 3);
                return userProfilesService.changeProductQuantity(JsonUtils.serialize(new Request(appController.getCurrentUserId(),
                        appController.getToken(),
                                                                                JsonUtils.serialize(new CartRequest(operation[1],
                                                                                                                    operation[2],
                                                                                                                    quantity)))));
            }

            default -> throw new IllegalArgumentException("Invalid operation: \"" + operation[0] + "\"");
        }
    }

    private void numOfArgumentsChecker(String[] operation, int expectedNumOfArguments) {
        if (operation.length != expectedNumOfArguments + 1) {
            throw new IllegalArgumentException(operation[0] + " is required " + expectedNumOfArguments + " arguments");
        }
    }

    public static List<String[]> parser(String file) {
        String[] operations = (file+" ").split(";");
        List<String[]> ret = new LinkedList<>();
        if (!removeWhiteSpaces(operations[operations.length - 1]).isEmpty()) {
            throw new IllegalArgumentException("After the last operation there is more text: \"" + operations[operations.length - 1] + "\"");
        }
        for (int index = 0; index < operations.length - 1; index++) {
            String op = operations[index];
            String[] words = op.split("[(]");
            if(words.length != 2) {
                throw new IllegalArgumentException("missing '(' at operation: \"" + op + "\"");
            }
            String[] operator = words[0].split("\\W");
            if(operator.length != 1) {
                boolean found = false;
                for (String splited : operator) {
                    String str = removeWhiteSpaces(splited);
                    if (!str.isEmpty()) {
                        if (!found) {
                            found = true;
                            operator[0] = str;

                        }
                        else {
                            throw new IllegalArgumentException("Illegal operator at operation: \"" + op + "\" = " + operator.length);

                        }
                    }
                }
                if (!found) {
                    throw new IllegalArgumentException("no operator found at operation: \"" + op + "\" = " + operator.length);
                }
            }
            String[] argumentsWord = (words[1]+" ").split("[)]");
            if(argumentsWord.length != 2 || !removeWhiteSpaces(argumentsWord[1]).isEmpty()) {
                throw new IllegalArgumentException("no continue after aoeration at: \"" + op + "\"");
            }
            String[] arguments = argumentsWord[0].split(",");
            String[] operation = new String[arguments.length + 1];
            operation[0] = operator[0];
            if (arguments.length == 1 && removeWhiteSpaces(arguments[0]).isEmpty()) {
                operation = new String[1];
                operation[0] = operator[0];
            }
            else {
                for (int i = 1; i <= arguments.length; i++) {
                    operation[i] = removeWhiteSpaces(arguments[i - 1]);

                }
            }
            ret.add(operation);
        }
        return ret;
    }

    private static String removeWhiteSpaces(String input){
        int first = -1;
        int end = -1;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) > 32) {
                if (first == -1) {
                    first = i;
                }
                else {
                    end = i+1;
                }
            }
        }
        if (first == -1) {
            return "";
        }
        if (end == -1) {
            end = first + 1;
        }
        return input.substring(first, end);
    }

    private static String operationToString(String[] operation) {
        if (operation == null || operation.length == 0) {
           return "";
        }
        String ret = operation[0] + " ( ";
        for (int i = 0; i < operation.length; i++) {
            ret += operation[i] + " ";
        }
        ret = ret + ")";
        return ret;
    }

    private static int getInteger(String operator, String input, int argIndex) {
        try {
            return Integer.parseInt(input);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(operator + " " + argIndex + " parameter must be integer");
        }
    }

    private static double getDouble(String operator, String input, int argIndex) {
        try {
            return Double.parseDouble(input);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(operator + " " + argIndex + " parameter is double - must be decimal number");
        }
    }

    private static Rating getRating(String operator, String input, int argIndex) {
        Rating rating = null;
        try {
            int rate = Integer.parseInt(input);
            if(rate < 1 || rate > 5) {
                throw new IllegalArgumentException(""); //anyway will be caught
            }
            switch (rate) {
                case 1 -> rating = Rating.ONE_STAR;
                case 2 -> rating = Rating.TWO_STARS;
                case 3 -> rating = Rating.THREE_STARS;
                case 4 -> rating = Rating.FOUR_STARS;
                case 5 -> rating = Rating.FIVE_STARS;
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException(operator + " " + argIndex + " parameter is rating - must be integral between 1-5");
        }
        return rating;
    }

    private static boolean getBoolean(String operator, String input, int argIndex) {
        if (input.equals("true")) {
            return true;
        } else if (input.equals("false")) {
            return false;
        } else {
            throw new IllegalArgumentException(operator + " " + argIndex + " parameter is boolean - must be \"true\" or \"false\"");
        }
    }

    private static LocalDate getDate(String operator, String input, int argIndex) {
        if (input == null) {
            throw new IllegalArgumentException(operator + " " + argIndex + " parameter is date, and it cannot be empty");
        }
        if (input.length() != 8) {
            throw new IllegalArgumentException(operator + " " + argIndex + " parameter is date, and it must be from the form DDMMYYYY");
        }
        int day = Integer.parseInt(input.substring(0,2));
        int month = Integer.parseInt(input.substring(2,4));
        int year = Integer.parseInt(input.substring(4,8));
        try {
            return LocalDate.of(year, month, day);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(operator + " " + argIndex + " parameter is date - the date must be valid and exist");
        }
    }

    private static class AppController {

        public static class ApplicationException extends Exception{

            public ApplicationException() {
                super(null, null, true, false);
            }

            public ApplicationException(String message) {
                super(message,null,true,false);
            }

            public ApplicationException(String message, Throwable cause) {
                super(message, cause, true, false);
            }

            public ApplicationException(Throwable cause) {
                super(null,cause,true,false);
            }
        }

        public enum Endpoints {

            //TODO: REPLACE 'Void.class' WITH THE CORRECT RETURN TYPE

            // User Profiles Endpoints
            ENTER_AS_GUEST("userprofiles/enterasguest", String.class),
            REGISTER_USER("userprofiles/register", Boolean.class),
            LOGIN_TO_REGISTERED("userprofiles/logintoregistered", Boolean.class),
            LOGOUT("userprofiles/logout", Boolean.class),
            LOGOUT_AS_GUEST("userprofiles/logoutasguest", Boolean.class),
            ADD_PRODUCT_TO_CART("userprofiles/addproducttocart", Boolean.class),
            REMOVE_PRODUCT_FROM_CART("userprofiles/removeproductfromcart", Void.class),
            CHANGE_PRODUCT_QUANTITY("userprofiles/changeproductquantity", Void.class),
            VIEW_CART("userprofiles/viewcart", ShoppingCart.class),
            START_PURCHASE("userprofiles/startpurchase", Void.class),
            PAY_FOR_PURCHASE("userprofiles/payforpurchase", Void.class),
            CANCEL_PURCHASE("userprofiles/cancelpurchase", Void.class),
            GET_USER_TRANSACTION_HISTORY("userprofiles/getusertransactionhistory", Transaction.class),

            // Authentication Endpoints
            AUTHENTICATE_USER("auth/user", String.class),
            AUTHENTICATE_GUEST("auth/guest", String.class),

            // External Services Endpoints
            SEND_SHIPMENT("external/sendshipment", Void.class),
            ADD_SHIPPING_SERVICE("external/addshippingservice", Void.class),
            REMOVE_SHIPPING_SERVICE("external/removeshippingservice", Void.class),
            UPDATE_SHIPPING_SERVICE("external/updateshippingservice", Void.class),
            ADD_PAYMENT_SERVICE("external/addpaymentservice", Void.class),
            REMOVE_PAYMENT_SERVICE("external/removepaymentservice", Void.class),
            UPDATE_PAYMENT_SERVICE("external/updatepaymentservice", Void.class),

            // Market Endpoints
            START_MARKET("market/start", Void.class),
            SHUTDOWN_MARKET("market/shutdown", Void.class),

            // Notifications Endpoints
            SEND_NOTIFICATION("notifications/sendnotification", Void.class),
            SET_READ_VALUE("notifications/setreadvalue", Void.class),
            GET_UNREAD_NOTIFICATIONS("notifications/getunreadnotifications", Notification.class),
            GET_NOTIFICATIONS("notifications/getnotifications", Notification.class),
            DELETE_NOTIFICATION("notifications/deletenotification", Void.class),

            // Stores Endpoints
            SEARCH_PRODUCTS_GLOBALLY("stores/searchproductsglobally", Product.class),
            SEARCH_PRODUCTS_BY_KEYWORD("stores/searchproductsbykeyword", Product.class),
            SEARCH_PRODUCTS_IN_STORE("stores/searchproductsinstore", Void.class),
            SEARCH_STORES_GLOBALLY("stores/searchstoresglobally", StoreDetails.class),
            ADD_STORE("stores/addstore", Void.class),
            OPEN_STORE("stores/openstore", Void.class),
            CLOSE_STORE("stores/closestore", Void.class),
            ADD_PRODUCT("stores/addproduct", Void.class),
            UPDATE_PRODUCT("stores/updateproduct", Void.class),
            REMOVE_PRODUCT("stores/removeproduct", Void.class),
            DISABLE_PRODUCT("stores/disableproduct", Void.class),
            ENABLE_PRODUCT("stores/enableproduct", Void.class),
            ADD_OWNER("stores/addowner", Void.class),
            ADD_MANAGER("stores/addmanager", Void.class),
            REMOVE_OWNER("stores/removeowner", Void.class),
            REMOVE_MANAGER("stores/removemanager", Void.class),
            ADD_PERMISSION_TO_MANAGER("stores/addpermissiontomanager", Void.class),
            REMOVE_PERMISSION_FROM_MANAGER("stores/removepermissionfrommanager", Void.class),
            GET_STORE_ROLES_INFORMATION("stores/getstorerolesinformation", Void.class),
            GET_STORE_TRANSACTION_HISTORY("stores/getstoretransactionhistory", Void.class),
            SET_PRODUCT_QUANTITY("stores/setproductquantity", Void.class),
            GET_STORE_PRODUCTS("stores/getstoreproducts", Types.GET_STORE_PRODUCTS_TYPE),
            GET_PRODUCT_QUANTITY("stores/getproductquantity", Integer.class),
            GET_STORE_DETAILS("stores/getstoredetails", StoreDetails.class),
            GET_PRODUCT("stores/getproduct", Product.class),
            ADD_DISCOUNT_RULE_CFG("stores/adddiscountrulebycfg", String.class),
            GET_DISCOUNT_RULE_CFG("stores/getcfgdiscountrule", String.class),
            ADD_DISCOUNT_RULE_DTO("stores/adddiscountrulebydto", String.class),
            GET_DISCOUNT_RULE_DTO("stores/getdtodiscountrule", DiscountComponentDTO.class),
            REMOVE_DISCOUNT_RULE("stores/removediscountrule", Boolean.class),
            ADD_PURCHASE_POLICY("stores/addpuchasepolicy", Void.class),
            GET_PURCHASE_POLICY("stores/getpurchasepolicy", PurchaseRuleDTO.class),
            REMOVE_PURCHASE_POLICY("stores/removepuchasepolicy", Boolean.class),

            //Permissions Endpoints
            GET_USER_PERMISSIONS("permissions/getuserpermissions", UserPermissionsProfile.class),
            GET_GUEST_PERMISSIONS("permissions/getguestpermissions", DefaultPermissionsProfile.class),
            IS_ADMIN("permissions/isadmin", Boolean.class);

            private final String location;
            private final Type returnType;

            Endpoints(String location, Type returnType) {
                this.location = location;
                this.returnType = returnType;
            }

            public String location() {
                return location;
            }

            public Type returnType() {
                return returnType;
            }

            private static class Types {
                private static final Type GET_STORE_PRODUCTS_TYPE = new TypeToken<Map<Boolean, List<Product>>>() {}.getType();
            }
        }

        private final ConcurrentMap<String, Object> sessionAttributes = new ConcurrentHashMap<>();

        private static final String BACKEND_URI = "https://localhost:8443/";

        // ==================================================================================== |
        // ============================= API FETCHING METHODS ================================= |
        // ==================================================================================== |

        public <T> List<T> getByEndpoint(Endpoints endpoint) throws ApplicationException {
            return get(endpoint.location(), endpoint.returnType());
        }

        public <T> List<T> postByEndpoint(Endpoints endpoint, Object payload) throws ApplicationException {
            return post(endpoint.location(), endpoint.returnType(), payload);
        }

        private <T> List<T> get(String location, Type clazz) throws ApplicationException {
            ApplicationException fetchFailed = new ApplicationException("Failed to fetch data");

            Response response;
            try {
                String fetched = APIFetcher.create()
                        .withUri(BACKEND_URI + location)
                        .withHeader("Authorization", getBearerAuth())
                        .fetch();
                response = Response.fromJson(fetched);
            } catch (IOException | InterruptedException | JsonSyntaxException e) {
                throw fetchFailed;
            }

            if (response == null) {
                throw fetchFailed;
            }
            if (!response.success()) {
                throw new ApplicationException(response.message());
            }
            return response.payload(clazz);
        }

        private <T> List<T> post(String location, Type clazz, Object payload) throws ApplicationException {
            ApplicationException postFailed = new ApplicationException("Failed to send data");

            String body = RequestBuilder.create()
                    .withUserId(getCurrentUserId())
                    .withToken(getToken())
                    .withPayload(payload)
                    .build()
                    .toJson();

            Response response;
            try {
                String fetched = APIFetcher.create()
                        .withUri(BACKEND_URI + location)
                        .withHeader("Authorization", getBearerAuth())
                        .withBody(body)
                        .withPost()
                        .fetch();
                response = Response.fromJson(fetched);
            } catch (IOException | InterruptedException | JsonSyntaxException e) {
                throw postFailed;
            }

            if (response == null) {
                throw postFailed;
            }
            if (!response.success()) {
                throw new ApplicationException(response.message());
            }
            return response.payload(clazz);
        }

        private String getBearerAuth() {
            String token = getToken();
            if (token == null) {
                return "";
            }
            return "Bearer " + token;
        }

        // ==================================================================================== |
        // ============================= AUTHENTICATION METHODS =============================== |
        // ==================================================================================== |

        public boolean enterAsGuest() {
            if (isUserLoggedIn() || isGuestLoggedIn()) {
                return false;
            }

            String token, userId;
            PermissionsProfile profile;
            try {
                userId = (String) getByEndpoint(Endpoints.ENTER_AS_GUEST).getFirst();
                AuthenticationRequest request = new AuthenticationRequest(userId, null);
                token = (String) postByEndpoint(Endpoints.AUTHENTICATE_GUEST, request).getFirst();
            } catch (ApplicationException e) {
                return false;
            }
            // ---------> logged in as guest
            // get permissions profile
            try {
                List<PermissionsProfile> fetched = postCustom(Endpoints.GET_GUEST_PERMISSIONS, userId, token, "Bearer "+token, null);
                profile = fetched.getFirst();
            } catch (ApplicationException e) {
                return false;
            }
            setCurrentUserId(userId);
            setGuestLoggedIn(true);
            setToken(token);
            setPermissionsProfile(profile);
            return true;
        }

        public boolean login(String userId, String password) {
            userId = userId.toLowerCase();
            if (isUserLoggedIn()) {
                return false;
            }

            String credentialsString = "%s:%s".formatted(userId, password);
            String auth = "Basic " + new String(Base64.getEncoder().encode(credentialsString.getBytes()));

            String token, guestId = getCurrentUserId();
            PermissionsProfile profile;
            boolean isAdmin;

            try {
                AuthenticationRequest authRequest = new AuthenticationRequest(userId, password);
                List<String> fetched1 = postCustom(Endpoints.AUTHENTICATE_USER, null, null,auth, authRequest);
                token = fetched1.getFirst();

                // ---------> passed authentication
                // send login request

                LoginRequest loginRequest = new LoginRequest(guestId, userId);
                postCustom(Endpoints.LOGIN_TO_REGISTERED, userId, token, "Bearer "+token, loginRequest);

                // ---------> logged in
                // Check if the user is an admin

                List<Boolean> fetched3 = postCustom(Endpoints.IS_ADMIN, userId, token, "Bearer "+token, null);
                isAdmin = fetched3.getFirst();

                // ---------> checked if admin
                // get permissions profile

                if(isAdmin){
                    profile = new AdminPermissionsProfile();
                } else {
                    List<UserPermissionsProfile> fetched4 = postCustom(Endpoints.GET_USER_PERMISSIONS, userId, token, "Bearer "+token, null);
                    profile = fetched4.getFirst();
                }
            } catch (ApplicationException e) {
                return false;
            }

            setCurrentUserId(userId);
            setToken(token);
            setUserLoggedIn(true);
            setGuestLoggedIn(false);
            setPermissionsProfile(profile);
            return true;
        }

        public boolean register(String email, String username, String password, String confirmPassword, LocalDate birthDate) {
            if (isUserLoggedIn()) {
                return false;
            }

            if (!password.equals(confirmPassword)) {
                return false;
            }

            RegisterRequest request = new RegisterRequest(email, username, password, birthDate);
            try {
                postByEndpoint(Endpoints.REGISTER_USER, request);
            } catch (ApplicationException e) {
                return false;
            }
            return true;
        }

        public boolean logout() {
            if (!isUserLoggedIn()) {
                return false;
            }
            try {
                postByEndpoint(Endpoints.LOGOUT, null);
            } catch (ApplicationException e) {
                return false;
            }
            return true;
        }

        public boolean logoutAsGuest() {
            if (!isGuestLoggedIn()) {
                return false;
            }
            try {
                postByEndpoint(Endpoints.LOGOUT_AS_GUEST, null);
            } catch (ApplicationException e) {
                return false;
            }
            return true;
        }

        // ==================================================================================== |
        // ===========================  SESSION FUNCTIONS ===================================== |
        // ==================================================================================== |

        public boolean isUserLoggedIn() {
            Boolean isUserLoggedIn = getSessionAttribute("isUserLoggedIn");
            return isUserLoggedIn != null && isUserLoggedIn;
        }

        public void setUserLoggedIn(boolean value) {
            setSessionsAttribute("isUserLoggedIn", value);
        }

        public boolean isGuestLoggedIn() {
            Boolean isGuestLoggedIn = getSessionAttribute("isGuestLoggedIn");
            return isGuestLoggedIn != null && isGuestLoggedIn;
        }

        public PermissionsProfile getPermissionsProfile() {
            return getSessionAttribute("permissionsProfile");
        }

        public void setPermissionsProfile(PermissionsProfile profile) {
            setSessionsAttribute("permissionsProfile", profile);
        }

        public void setGuestLoggedIn(boolean value) {
            setSessionsAttribute("isGuestLoggedIn", value);
        }

        public String getCurrentUserId() {
            return getSessionAttribute("userId");
        }

        public void setCurrentUserId(String userId) {
            setSessionsAttribute("userId", userId);
        }

        public String getToken() {
            return getSessionAttribute("token");
        }

        public void setToken(String token) {
            setSessionsAttribute("token", token);
        }

        public void setSessionsAttribute(String key, Object value) {
            sessionAttributes.put(key, value);
        }

        @SuppressWarnings("unchecked")
        public <T> T getSessionAttribute(String key) {
            return (T) sessionAttributes.get(key);
        }

        // ==================================================================================== |
        // =============================  UTILITY METHODS ===================================== |
        // ==================================================================================== |

        private <T> List<T> postCustom(Endpoints endpoint,String userId,String token, String auth, Object payload) throws ApplicationException {
            ApplicationException postFailed = new ApplicationException("Failed to send data");

            String body = RequestBuilder.create()
                    .withUserId(userId)
                    .withToken(token)
                    .withPayload(payload)
                    .build()
                    .toJson();

            Response response;
            try {
                String fetched = APIFetcher.create()
                        .withUri(BACKEND_URI + endpoint.location())
                        .withHeader("Authorization", auth)
                        .withBody(body)
                        .withPost()
                        .fetch();
                response = Response.fromJson(fetched);
            } catch (IOException | InterruptedException | JsonSyntaxException e) {
                throw postFailed;
            }
            if (response == null) {
                throw postFailed;
            }
            if (!response.success()) {
                throw new ApplicationException(response.message());
            }
            return response.payload(endpoint.returnType());
        }
    }
}
