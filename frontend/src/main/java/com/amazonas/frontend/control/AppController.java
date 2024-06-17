package com.amazonas.frontend.control;

import com.amazonas.common.utils.APIFetcher;
import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Response;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.amazonas.frontend.model.User;
import com.google.gson.JsonSyntaxException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.List;

@Component("appController")
public class AppController {

    private static final String API_URI = "http://localhost:8080/";

    public static User currentUser;
    public static boolean isUserLoggedIn = false;


    public String getWelcomeMessage() {
        return "Welcome to my app!";
    }

    public String getExampleMessage(int num) {
        return "Example " + num;
    }

    public boolean login(String username, String password) {
        isUserLoggedIn = true;
        currentUser = new User(username, password);
        return true;
//        List<Boolean> fetched;
//        try {
//            fetched = fetchByEndpoint(Endpoints.LOGIN);
//        } catch (ApplicationException e) {
//            return false;
//        }
//        Boolean success = fetched.getFirst();
//        if (success) {
//            isUserLoggedIn = true;
//            currentUser = new User(username, password);
//        }
//        return success;
    }

    public <T> List<T> fetchByEndpoint(Endpoints endpoint) throws ApplicationException {
        return fetch(endpoint.location(), endpoint.clazz());
    }

    public void postByEndpoint(Endpoints endpoint, Object entity) throws ApplicationException {
        post(endpoint.location(), entity);
    }

    private <T> List<T> fetch(String location, Type t) throws ApplicationException {
        ApplicationException fetchFailed = new ApplicationException("Failed to fetch data");

        String auth = getAuth();
        Response response;
        try {
            String fetched = APIFetcher.create()
                    .withUri(API_URI + location)
                    .withHeader("Authorization", auth)
                    .fetch();
            response = Response.fromJson(fetched);
        } catch (IOException | InterruptedException | JsonSyntaxException e) {
            throw fetchFailed;
        }
        if (response == null) {
            throw fetchFailed;
        }
        if (response.success()) {
            return response.payload(t);
        } else {
            throw new ApplicationException(response.message());
        }
    }

    private void post(String location, Object entity) throws ApplicationException {
        ApplicationException postFailed = new ApplicationException("Failed to send data");

        String auth = getAuth();
        Response response;
        try {
            String body = JsonUtils.serialize(entity);
            String fetched = APIFetcher.create()
                    .withUri(API_URI + location)
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
    }

    private String getAuth() {
        User user = currentUser;
        String credentialsString = "%s:%s".formatted(user.username(), user.password());
        return "Basic " + new String(Base64.getEncoder().encode(credentialsString.getBytes()));
    }
}
