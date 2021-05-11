package com.fullteaching.backend.security;

import com.fullteaching.backend.user.User;
import com.fullteaching.backend.user.UserComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@RunWith(MockitoJUnitRunner.class)
class AuthorizationServiceTest {

    @InjectMocks
    private AuthorizationService authorizationService;

    @Mock
    private UserComponent users;

    private User loggedUser;
    private Object object;


    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.loggedUser = new User("Flavia", "123", "Flavinha", "flavinha.jpg");
        this.object = new Object();
        Mockito.when(users.getLoggedUser()).thenReturn(loggedUser);
    }


    @Test
    void testCheckBackendLogged_NotLoggedUser() {
        Mockito.when(users.isLoggedUser()).thenReturn(true);
        var response = authorizationService.checkBackendLogged();

        assertNull(response);
    }

    @Test
    void testCheckBackendLogged_NotLogged() {
        Mockito.when(users.isLoggedUser()).thenReturn(false);
        var response = authorizationService.checkBackendLogged();

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertEquals(HttpHeaders.EMPTY, response.getHeaders());
        assertNull(response.getBody());
    }

    @Test
    void testCheckAuthorization_Authorized() {
        var response = authorizationService.checkAuthorization(object, loggedUser);

        assertNull(response);
    }

    @Test
    void testCheckAuthorization_NotAuthorized() {
        var user = new User("Victoria", "123", "Vic", "vic.jpg");

        var response = authorizationService.checkAuthorization(object, user);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertEquals(HttpHeaders.EMPTY, response.getHeaders());
        assertNull(response.getBody());
    }

    @Test
    void testCheckAuthorization_ObjectNullAuthorized() {
        var response = authorizationService.checkAuthorization(null, loggedUser);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals(HttpHeaders.EMPTY, response.getHeaders());
        assertNull(response.getBody());
    }

    @Test
    void testCheckAuthorization_ObjectNullNotAuthorized() {
        var user = new User("Victoria", "123", "Vic", "vic.jpg");

        var response = authorizationService.checkAuthorization(null, user);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals(HttpHeaders.EMPTY, response.getHeaders());
        assertNull(response.getBody());
    }

    @Test
    void testCheckAuthorization_UserNull() {
        var response = authorizationService.checkAuthorization(object, null);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertEquals(HttpHeaders.EMPTY, response.getHeaders());
        assertNull(response.getBody());
    }


    @Test
    void testCheckAuthorizationUsers_Authorized() {
        var user = new User("Victoria", "123", "Vic", "vic.jpg");

        var userList = new ArrayList<User>();
        userList.add(loggedUser);
        userList.add(user);

        var response = authorizationService.checkAuthorizationUsers(object, userList);

        assertNull(response);
    }

    @Test
    void testCheckAuthorizationUsers_AuthorizedObjectNull() {
        var user = new User("Victoria", "123", "Vic", "vic.jpg");

        var userList = new ArrayList<User>();
        userList.add(loggedUser);
        userList.add(user);

        var response = authorizationService.checkAuthorizationUsers(null, userList);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals(HttpHeaders.EMPTY, response.getHeaders());
        assertNull(response.getBody());
    }

    @Test
    void testCheckAuthorizationUsers_NotAuthorized() {
        var user = new User("Victoria", "123", "Vic", "vic.jpg");
        var user2 = new User("Miguel", "123", "Guel", "guel.jpg");


        var userList = new ArrayList<User>();
        userList.add(user);
        userList.add(user2);

        var response = authorizationService.checkAuthorizationUsers(object, userList);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertEquals(HttpHeaders.EMPTY, response.getHeaders());
        assertNull(response.getBody());
    }

    @Test
    void testCheckAuthorizationUsers_NotAuthorizedObjectNull() {
        var user = new User("Victoria", "123", "Vic", "vic.jpg");
        var user2 = new User("Miguel", "123", "Guel", "guel.jpg");


        var userList = new ArrayList<User>();
        userList.add(user);
        userList.add(user2);

        var response = authorizationService.checkAuthorizationUsers(null, userList);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals(HttpHeaders.EMPTY, response.getHeaders());
        assertNull(response.getBody());
    }

    @Test
    void testCheckAuthorizationUsers_EmptyList() {
        var userList = new ArrayList<User>();


        var response = authorizationService.checkAuthorizationUsers(object, userList);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertEquals(HttpHeaders.EMPTY, response.getHeaders());
        assertNull(response.getBody());
    }


    @Test
    void testCheckAuthorizationUsers_NullList() {
        assertThrows(NullPointerException.class, () -> {
            authorizationService.checkAuthorizationUsers(object, null);
        });
    }
}