package com.amazonas.frontend.control;

import com.amazonas.common.utils.APIFetcher;
import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Response;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.amazonas.frontend.model.User;
import com.google.gson.JsonSyntaxException;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.WrappedSession;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.List;

@Component("appController")
public class AppController {

    private static final String BACKEND_URI = "https://localhost:8443/";

    public String getWelcomeMessage() {
        return "Welcome to my app!";
    }

    public String getExampleMessage(int num) {
        return "Example " + num;
    }

    public boolean enterAsGuest() {
        if(getCurrentUserId() != null) {
            return false;
        }

        List<String> fetched;
        try {
            fetched = getByEndpoint(Endpoints.ENTER_AS_GUEST);
        } catch (ApplicationException e) {
            return false;
        }
        if(fetched == null || fetched.isEmpty()) {
            return false;
        }

        setCurrentUserId(fetched.getFirst());
        return true;
    }

    public boolean authenticateAsGuest() {
        if(isUserLoggedIn() || isGuestLoggedIn()) {
            return false;
        }

        List<String> fetched;
        try {
            User entity = new User(null, getCurrentUserId(),null);
            fetched = postByEndpoint(Endpoints.LOGIN_GUEST, entity);
        } catch (ApplicationException e) {
            return false;
        }
        if(fetched == null || fetched.isEmpty()) {
            return false;
        }
        setGuestLoggedIn(true);
        setToken(fetched.getFirst());
        return true;
    }

    public boolean login(String userId, String password) {
        String credentialsString = "%s:%s".formatted(userId, password);
        String auth = "Basic " + new String(Base64.getEncoder().encode(credentialsString.getBytes()));

        Response response;
        try {
            String fetched = APIFetcher.create()
                    .withUri(BACKEND_URI + Endpoints.LOGIN_USER.location())
                    .withHeader("Authorization", auth)
                    .withBody(JsonUtils.serialize(new User(null, userId, password)))
                    .withPost()
                    .fetch();
            response = Response.fromJson(fetched);
        } catch (IOException | InterruptedException | JsonSyntaxException e) {
            return false;
        }

        boolean success = response.success();
        if (success) {
            setCurrentUserId(userId);
            setToken(response.<String>payload(String.class).getFirst());
            setUserLoggedIn(true);
            setGuestLoggedIn(false);
        }
        return success;
    }

    public <T> List<T> getByEndpoint(Endpoints endpoint) throws ApplicationException {
        return get(endpoint.location(), endpoint.clazz());
    }

    public <T> List<T> postByEndpoint(Endpoints endpoint, Object entity) throws ApplicationException {
        return post(endpoint.location(),endpoint.clazz() ,entity);
    }

    private <T> List<T> get(String location, Type t) throws ApplicationException {
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
        return response.payload(t);
    }

    private <T> List<T> post(String location, Type t, Object entity) throws ApplicationException {
        ApplicationException postFailed = new ApplicationException("Failed to send data");

        Response response;
        try {
            String body = JsonUtils.serialize(entity);
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
        return response.payload(t);
    }

    private String getBearerAuth() {
        String token = getToken();
        if (token == null){
            return "";
        }
        return "Bearer " + token;
    }

    // ==================================================================================== |
    // ============================= STATIC SESSION METHODS =============================== |
    // ==================================================================================== |

    public static boolean isUserLoggedIn() {
        return getSession().getAttribute("isUserLoggedIn") != null;
    }

    public static void setUserLoggedIn(boolean value) {
        getSession().setAttribute("isUserLoggedIn", value);
    }

    public static boolean isGuestLoggedIn() {
        return getSession().getAttribute("isGuestLoggedIn") != null;
    }

    public static void setGuestLoggedIn(boolean value) {
        getSession().setAttribute("isGuestLoggedIn", value);
    }

    public static String getCurrentUserId(){
        return (String) getSession().getAttribute("userId");
    }

    public static void setCurrentUserId(String userId){
        getSession().setAttribute("userId", userId);
    }

    public static String getToken(){
        return (String) getSession().getAttribute("token");
    }

    public static void setToken(String token){
        getSession().setAttribute("token", token);
    }

    public static void clearSession(){
        getSession().invalidate();
    }

    private static WrappedSession getSession() {
        return VaadinService.getCurrentRequest().getWrappedSession();
    }

}
