package com.amazonas.backend.service;

import com.amazonas.backend.business.payment.PaymentService;
import com.amazonas.backend.business.shipping.ShippingService;
import com.amazonas.backend.service.requests.payment.PaymentServiceManagementRequest;
import com.amazonas.backend.service.requests.shipping.ShippingServiceManagementRequest;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.notifications.NotificationRequest;
import com.amazonas.common.requests.shipping.ShipmentRequest;
import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Response;

import java.util.LinkedList;
import java.util.List;

public class InitialRunFileExecutor {
    public String requestUserId;
    public String requestToken;
    private final AuthenticationService authenticationService;
    private final ExternalServicesService externalServicesService;
    private final MarketService marketService;
    private final NotificationsService notificationsService;
    private final StoresService storesService;
    private final UserProfilesService userProfilesService;
    private final PermissionsService permissionsService;

    public InitialRunFileExecutor(AuthenticationService authenticationService,
                                  ExternalServicesService externalServicesService,
                                  MarketService marketService,
                                  NotificationsService notificationsService,
                                  StoresService storesService,
                                  UserProfilesService userProfilesService, PermissionsService permissionsService) {
        this.requestUserId = "";
        this.requestToken = "";
        this.authenticationService = authenticationService;
        this.externalServicesService = externalServicesService;
        this.marketService = marketService;
        this.notificationsService = notificationsService;
        this.storesService = storesService;
        this.userProfilesService = userProfilesService;
        this.permissionsService = permissionsService;
    }

    public Response runCode(String code){
        List<String[]> operations;
        try {
            operations = parser(code);
        }
        catch (Exception e) {
            return new Response(e.getMessage(), false, "");
        }
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

    private String executeOperation(String[] operation) {
        if (operation == null || operation.length == 0) {
            throw new IllegalArgumentException("Operation is empty");
        }
        switch (operation[0]) {

            //====================================================================== |
            //======================== External Services =========================== |
            //====================================================================== |

            case "sendShipment" -> {
                numOfArgumentsChecker(operation, 3);
                externalServicesService.sendShipment(JsonUtils.serialize(new Request(requestUserId,
                                                                                     requestToken,
                                                                                     JsonUtils.serialize(new ShipmentRequest(operation[1],
                                                                                                                             operation[2],
                                                                                                                             operation[3])))));
            }
            case "addShippingService" -> {
                numOfArgumentsChecker(operation, 1);
                externalServicesService.addShippingService(JsonUtils.serialize(new Request(requestUserId,
                                                                                           requestToken,
                                                                                           JsonUtils.serialize(new ShippingServiceManagementRequest(operation[1],
                                                                                                                                                    new ShippingService())))));
            }
            case "removeShippingService" -> {
                numOfArgumentsChecker(operation, 1);
                externalServicesService.removeShippingService(JsonUtils.serialize(new Request(requestUserId,
                                                                                              requestToken,
                                                                                              JsonUtils.serialize(new ShippingServiceManagementRequest(operation[1],
                                                                                                                                                       new ShippingService())))));
            }
            /*
            case "updateShippingService" -> {
                numOfArgumentsChecker(operation, 1);
                externalServicesService.updateShippingService(JsonUtils.serialize(new Request(requestUserId,
                                                                                              requestToken,
                                                                                              JsonUtils.serialize(new ShippingServiceManagementRequest(operation[1],
                                                                                                                                                       new ShippingService())))));
            }
             */
            case "addPaymentService" -> {
                numOfArgumentsChecker(operation, 1);
                externalServicesService.addPaymentService(JsonUtils.serialize(new Request(requestUserId,
                                                                                          requestToken,
                                                                                          JsonUtils.serialize(new PaymentServiceManagementRequest(operation[1],
                                                                                                                                                  new PaymentService())))));
            }
            case "removePaymentService" -> {
                numOfArgumentsChecker(operation, 1);
                externalServicesService.removePaymentService(JsonUtils.serialize(new Request(requestUserId,
                                                                                             requestToken,
                                                                                             JsonUtils.serialize(new PaymentServiceManagementRequest(operation[1],
                                                                                                                                                     new PaymentService())))));
            }
            /*
            case "updatePaymentService" -> {
                numOfArgumentsChecker(operation, 1);
                externalServicesService.updatePaymentService(JsonUtils.serialize(new Request(requestUserId,
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
                marketService.startMarket(JsonUtils.serialize(new Request(requestUserId,
                                                                          requestToken,
                                                                          "")));
            }
            case "shutdown" -> {
                numOfArgumentsChecker(operation, 0);
                marketService.shutdown(JsonUtils.serialize(new Request(requestUserId,
                                                                       requestToken,
                                                                       "")));
            }

            //====================================================================== |
            //============================ Notification ============================ |
            //====================================================================== |

            case "sendNotification" -> {
                numOfArgumentsChecker(operation, 4);
                notificationsService.sendNotification(JsonUtils.serialize(new Request(requestUserId,
                                                                                      requestToken,
                                                                                      JsonUtils.serialize(new NotificationRequest(operation[1],
                                                                                                                                  operation[2],
                                                                                                                                  operation[3],
                                                                                                                                  operation[4])))));
            }
            case "setReadValue" -> {
                numOfArgumentsChecker(operation, 2);
                boolean readValue = false;
                if (operation[2].equals("true")) {
                    readValue = true;
                } else if (operation[2].equals("false")) {
                    readValue = false;
                } else {
                    throw new IllegalArgumentException("setReadValue second parameter is boolean - must be \"true\" or \"false\"");
                }
                notificationsService.setReadValue(JsonUtils.serialize(new Request(requestUserId,
                                                                                  requestToken,
                                                                                  JsonUtils.serialize(new NotificationRequest(operation[1],
                                                                                                                              readValue)))));
            }
            case "deleteNotification" -> {
                numOfArgumentsChecker(operation, 1);
                notificationsService.deleteNotification(JsonUtils.serialize(new Request(requestUserId,
                                                                                  requestToken,
                                                                                  JsonUtils.serialize(new NotificationRequest(operation[1],
                                                                                                                              false)))));
            }

            default -> throw new IllegalArgumentException("Invalid operation: \"" + operation[0] + "\"");
        }
        throw new IllegalArgumentException("Invalid operation: \"" + operation[0] + "\"");
    }

    private void numOfArgumentsChecker(String[] operation, int expectedNumOfArguments) {
        if (operation.length != expectedNumOfArguments + 1) {
            throw new IllegalArgumentException(operation[0] + " is required " + expectedNumOfArguments + " arguments");
        }
    }

    private static List<String[]> parser(String file) {
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
            for(int i = 1; i <= arguments.length; i++) {
                operation[i] = removeWhiteSpaces(arguments[i-1]);

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
}
