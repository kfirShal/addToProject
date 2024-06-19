package com.amazonas.frontend.control;

import com.amazonas.common.requests.RequestBuilder;
import com.amazonas.common.requests.auth.AuthenticationRequest;
import com.amazonas.common.requests.users.LoginRequest;
import com.amazonas.common.requests.users.RegisterRequest;
import com.amazonas.common.utils.APIFetcher;
import com.amazonas.common.utils.Response;
import com.amazonas.frontend.exceptions.ApplicationException;
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

    // ==================================================================================== |
    // ============================= AUTHENTICATION METHODS =============================== |
    // ==================================================================================== |

    public boolean enterAsGuest() {
        if(isUserLoggedIn() || isGuestLoggedIn()) {
            return false;
        }

        String token, userId;
        try {
            userId = (String) getByEndpoint(Endpoints.ENTER_AS_GUEST).getFirst();
            AuthenticationRequest request =  new AuthenticationRequest(userId, null);
            token = (String) postByEndpoint(Endpoints.AUTHENTICATE_GUEST, request).getFirst();
        } catch (ApplicationException e) {
            return false;
        }
        // ---------> logged in as guest
        setCurrentUserId(userId);
        setGuestLoggedIn(true);
        setToken(token);
        return true;
    }

    public boolean login(String userId, String password) {
        if(isUserLoggedIn()){
            return false;
        }

        String credentialsString = "%s:%s".formatted(userId, password);
        String auth = "Basic " + new String(Base64.getEncoder().encode(credentialsString.getBytes()));

        String body,token;
        Response authResponse, loginResponse;
        try {
            body = RequestBuilder.create()
                    .withPayload(new AuthenticationRequest(userId, password))
                    .build()
                    .toJson();
            String fetched = APIFetcher.create()
                    .withUri(BACKEND_URI + Endpoints.AUTHENTICATE_USER.location())
                    .withHeader("Authorization", auth)
                    .withBody(body)
                    .withPost()
                    .fetch();
            authResponse = Response.fromJson(fetched);
            if(authResponse == null || ! authResponse.success()){
                return false;
            }
            // ---------> passed authentication
            token = authResponse.<String>payload(String.class).getFirst();
            String guestId = getCurrentUserId();
            body = RequestBuilder.create()
                    .withUserId(guestId)
                    .withToken(token)
                    .withPayload(new LoginRequest(guestId, userId))
                    .build()
                    .toJson();
            fetched = APIFetcher.create()
                    .withUri(BACKEND_URI + Endpoints.LOGIN_TO_REGISTERED.location())
                    .withHeader("Authorization", getBearerAuth())
                    .withBody(body)
                    .withPost()
                    .fetch();
            loginResponse = Response.fromJson(fetched);
            if(loginResponse == null || ! loginResponse.success()){
                return false;
            }
            // ---------> logged in
        } catch (IOException | InterruptedException | JsonSyntaxException e) {
            return false;
        }
        setCurrentUserId(userId);
        setToken(token);
        setUserLoggedIn(true);
        setGuestLoggedIn(false);
        return true;
    }

    public boolean register(String email, String username, String password, String confirmPassword) {
        if(isUserLoggedIn()){
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            return false;
        }

        RegisterRequest request = new RegisterRequest(email, username, password);
        try {
            postByEndpoint(Endpoints.REGISTER_USER, request);
        } catch (ApplicationException e) {
            return false;
        }
        return true;
    }

    // ==================================================================================== |
    // ============================= API FETCHING METHODS ================================= |
    // ==================================================================================== |

    public <T> List<T> getByEndpoint(Endpoints endpoint) throws ApplicationException {
        return get(endpoint.location(), endpoint.clazz());
    }

    public <T> List<T> postByEndpoint(Endpoints endpoint, Object payload) throws ApplicationException {
        return post(endpoint.location(),endpoint.clazz() ,payload);
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

    private <T> List<T> post(String location, Type t, Object payload) throws ApplicationException {
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
    // ===========================  SESSION FUNCTIONS ===================================== |
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
