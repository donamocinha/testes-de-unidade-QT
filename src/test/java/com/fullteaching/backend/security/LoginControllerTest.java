package com.fullteaching.backend.security;

import javax.servlet.http.HttpSession;

import com.fullteaching.backend.user.User;
import com.fullteaching.backend.user.UserComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(MockitoJUnitRunner.class)
class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @Mock
    private UserComponent userComponent;

    @Mock
    private HttpSession session;


    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testLogIn_Success() {
        var user = new User("Flavia", "123", "Flavinha", "flavinha.jpg");
        user.setId(1L);

        Mockito.when(userComponent.isLoggedUser()).thenReturn(true);
        Mockito.when(userComponent.getLoggedUser()).thenReturn(user);

        var resp = loginController.logIn();

        Mockito.verify(userComponent, Mockito.never()).setLoggedUser(user);
        Mockito.verify(userComponent, Mockito.times(1)).getLoggedUser();
        Mockito.verify(userComponent, Mockito.times(1)).isLoggedUser();


        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(user, resp.getBody());
    }

    @Test
    void testLogOut_Success() {
        var user = new User("Flavia", "123", "Flavinha", "flavinha.jpg");
        user.setId(1L);

        Mockito.when(userComponent.getLoggedUser()).thenReturn(user);
        Mockito.when(userComponent.isLoggedUser()).thenReturn(true);

        var resp = loginController.logOut(session);

        Mockito.verify(session, Mockito.times(1)).invalidate();
        Mockito.verify(userComponent, Mockito.times(1)).getLoggedUser();
        Mockito.verify(userComponent, Mockito.times(1)).isLoggedUser();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(true, resp.getBody());
    }
}