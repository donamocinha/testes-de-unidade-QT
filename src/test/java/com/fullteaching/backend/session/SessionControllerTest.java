package com.fullteaching.backend.session;

import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.course.CourseRepository;
import com.fullteaching.backend.security.AuthorizationService;
import com.fullteaching.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class SessionControllerTest {

    @InjectMocks
    private SessionController sessionController;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private AuthorizationService authorizationService;

    private User loggedUser;


    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        loggedUser = new User("Flavia", "123", "Flavinha", "flavinha.jpg");
        loggedUser.setId(1L);
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);
    }

    @Test
    void testNewSession_Success() {
        var session = new Session("Session", "New session", 2020L);

        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));
        Mockito.when(authorizationService.checkAuthorization(course, loggedUser)).thenReturn(null);

        var resp = sessionController.newSession(session, String.valueOf(course.getId()));

        Mockito.verify(courseRepository, Mockito.times(1)).save(course);

        var sessions = new HashSet<Session>();
        sessions.add(session);
        course.setSessions(sessions);

        var savedCourse = (Course) resp.getBody();
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertNotNull(savedCourse);
        assertEquals(course.getSessions(), savedCourse.getSessions());
    }

    @Test
    void testModifySession_Success() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var session = new Session("Session", "New session", 2020L);
        session.setId(1L);
        session.setCourse(course);

        var modifiedSession = new Session("M Session", "Modified session", 2021L);
        modifiedSession.setId(session.getId());

        Mockito.when(sessionRepository.findById(modifiedSession.getId())).thenReturn(java.util.Optional.of(session));
        Mockito.when(authorizationService.checkAuthorization(course, loggedUser)).thenReturn(null);

        var resp = sessionController.modifySession(modifiedSession);

        Mockito.verify(sessionRepository, Mockito.times(1)).save(modifiedSession);

        var savedSession = (Session) resp.getBody();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(savedSession);
        assertEquals(modifiedSession.getId(), savedSession.getId());
        assertEquals(modifiedSession.getTitle(), savedSession.getTitle());
        assertEquals(modifiedSession.getDescription(), savedSession.getDescription());
    }

    @Test
    void testDeleteSession_Success() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var session = new Session("Session", "New session", 2020L);
        session.setId(1L);
        session.setCourse(course);

        Mockito.when(authorizationService.checkAuthorization(course, loggedUser)).thenReturn(null);
        Mockito.when(sessionRepository.findById(session.getId())).thenReturn(java.util.Optional.of(session));
        Mockito.when(courseRepository.findById(session.getCourse().getId())).thenReturn(java.util.Optional.of(course));

        var resp = sessionController.deleteSession(String.valueOf(session.getId()));

        Mockito.verify(sessionRepository, Mockito.times(1)).deleteById(session.getId());

        var savedSession = (Session) resp.getBody();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(savedSession);
        assertEquals(session.getTitle(), savedSession.getTitle());
        assertEquals(session.getDescription(), savedSession.getDescription());
    }
}