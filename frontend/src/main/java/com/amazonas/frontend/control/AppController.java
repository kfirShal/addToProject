package com.amazonas.frontend.control;


import com.amazonas.common.permissions.profiles.AdminPermissionsProfile;
import com.amazonas.common.permissions.profiles.DefaultPermissionsProfile;
import com.amazonas.common.permissions.profiles.PermissionsProfile;
import com.amazonas.common.permissions.profiles.UserPermissionsProfile;

import com.amazonas.common.requests.RequestBuilder;
import com.amazonas.common.requests.auth.AuthenticationRequest;
import com.amazonas.common.requests.users.LoginRequest;
import com.amazonas.common.requests.users.RegisterRequest;
import com.amazonas.common.utils.APIFetcher;
import com.amazonas.common.utils.Response;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.google.gson.JsonSyntaxException;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.apache.catalina.session.StandardSession;
import org.apache.catalina.session.StandardSessionFacade;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component("appController")
public class AppController {

    private static final int SESSION_TIMEOUT_INTERVAL = 60 * 60 * 24 ; // one day
    private final ConcurrentMap<String,SessionDetails> sessions;

    private static final String BACKEND_URI = "https://localhost:8443/";

    public AppController() {
        sessions = new ConcurrentHashMap<>();
        new Thread(this::sessionCleaner).start();
    }

    public String getWelcomeMessage() {
        return "Welcome to my app!";
    }

    public String getNotificationsMessage() {
        return "Notifications";
    }
    public String getPurchasePolicyMessage() {
        return "Purchase Policy";
    }
    public String getPreviousOrdersMessage() {
        return "Previous Orders";
    }

    public String getExampleMessage(int num) {
        return "Example " + num;
    }

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
        String sid = getSessionId();
        SessionDetails sessionDetails = sessions.get(sid);
        sessionDetails.setUserId(userId);
        sessionDetails.setToken(token);
        sessionDetails.setGuest(true);
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
        String sid = getSessionId();
        SessionDetails sessionDetails = sessions.get(sid);
        sessionDetails.setUserId(userId);
        sessionDetails.setToken(token);
        sessionDetails.setGuest(false);
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

    public static boolean isUserLoggedIn() {
        Boolean isUserLoggedIn = getSessionAttribute("isUserLoggedIn");
        return isUserLoggedIn != null && isUserLoggedIn;
    }

    public static void setUserLoggedIn(boolean value) {
        getSession().setAttribute("isUserLoggedIn", value);
    }

    public static boolean isGuestLoggedIn() {
        Boolean isGuestLoggedIn = getSessionAttribute("isGuestLoggedIn");
        return isGuestLoggedIn != null && isGuestLoggedIn;
    }

    public static PermissionsProfile getPermissionsProfile() {
        return getSessionAttribute("permissionsProfile");
    }

    public static void setPermissionsProfile(PermissionsProfile profile) {
        setSessionsAttribute("permissionsProfile", profile);
    }

    public static void setGuestLoggedIn(boolean value) {
        getSession().setAttribute("isGuestLoggedIn", value);
    }

    public static String getCurrentUserId() {
        return getSessionAttribute("userId");
    }

    public static void setCurrentUserId(String userId) {
        setSessionsAttribute("userId", userId);
    }

    public static String getToken() {
        return getSessionAttribute("token");
    }

    public static void setToken(String token) {
        setSessionsAttribute("token", token);
    }

    public static void clearSession() {
        getSession().invalidate();
    }

    public static void setSessionsAttribute(String key, Object value) {
        getSession().setAttribute(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getSessionAttribute(String key) {
        return (T) getSession().getAttribute(key);
    }

    private static WrappedSession getSession() {
        return VaadinSession.getCurrent().getSession();
    }

    public void addSession() {
        if (!sessions.containsKey(getSessionId())) {
            StandardSession stdSession = extractStandardSession();
            SessionDetails sessionDetails = new SessionDetails(stdSession);
            stdSession.setMaxInactiveInterval(SESSION_TIMEOUT_INTERVAL);
            sessions.put(stdSession.getId(),sessionDetails);
            stdSession.setAttribute("sessionRegistered", true);
        }
    }

    private String getSessionId() {
        return getSession().getId();
    }

    private StandardSession extractStandardSession() {
        try{
            WrappedSession ws = VaadinSession.getCurrent().getSession();
            Field field = ws.getClass().getDeclaredField("session");
            field.setAccessible(true);
            StandardSessionFacade ssf = (StandardSessionFacade) field.get(ws);
            field = ssf.getClass().getDeclaredField("session");
            field.setAccessible(true);
            return (StandardSession) field.get(ssf);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void sessionCleaner() {
        while (true) {
            for (var entry : sessions.entrySet()) {
                SessionDetails s = entry.getValue();
                if (!s.session().isValid()) {
                    String request = RequestBuilder.create()
                            .withUserId(s.userId())
                            .withToken(s.token())
                            .build()
                            .toJson();
                    APIFetcher fetcher = APIFetcher.create()
                            .withHeader("Authorization", "Bearer " + s.token())
                            .withBody(request)
                            .withPost();
                    String fetched = "";
                    try{
                        if(s.isGuest()){
                            fetched = fetcher.withUri(BACKEND_URI + Endpoints.LOGOUT_AS_GUEST.location()).fetch();
                        } else {
                            fetched = fetcher.withUri(BACKEND_URI + Endpoints.LOGOUT.location()).fetch();
                        }
                        Response response = Response.fromJson(fetched);
                        if(response == null || !response.success()){
                            continue;
                        }
                    } catch (Exception ignored){
                        continue;
                    }
                    sessions.remove(entry.getKey());
                }
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException _) {}
        }
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
