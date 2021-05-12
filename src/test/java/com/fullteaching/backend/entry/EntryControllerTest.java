package com.fullteaching.backend.entry;

import com.fullteaching.backend.comment.Comment;
import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.coursedetails.CourseDetailsRepository;
import com.fullteaching.backend.forum.Forum;
import com.fullteaching.backend.forum.ForumRepository;
import com.fullteaching.backend.security.AuthorizationService;
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
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class EntryControllerTest {

    @InjectMocks
    private EntryController entryController;

    @Mock
    private ForumRepository forumRepository;

    @Mock
    private CourseDetailsRepository courseDetailsRepository;

    @Mock
    private UserComponent user;

    @Mock
    private AuthorizationService authorizationService;

    private User loggedUser;


    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        loggedUser = new User("Flavia", "123", "Flavinha", "flavinha.jpg");
        loggedUser.setId(1L);
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);
        Mockito.when(user.getLoggedUser()).thenReturn(loggedUser);
    }

    @Test
    void testNewEntry_Success() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var forum = new Forum();
        forum.setEntries(new ArrayList<Entry>());

        var courseDetails = new CourseDetails();
        courseDetails.setId(1L);
        courseDetails.setCourse(course);
        courseDetails.setForum(forum);

        var comment = new Comment();
        var comments = new ArrayList<Comment>();
        comments.add(comment);

        var user1 = new User("Victoria", "123", "Vic", "vic.jpg");

        var entry = new Entry("Meu trabalho", 2021L, user1);
        entry.setId(1L);
        entry.setComments(comments);

        Mockito.when(authorizationService.checkAuthorization(course, course.getTeacher())).thenReturn(null);
        Mockito.when(courseDetailsRepository.findById(courseDetails.getId())).thenReturn(java.util.Optional.of(courseDetails));

        var resp = entryController.newEntry(entry, String.valueOf(courseDetails.getId()));

        Mockito.verify(forumRepository, Mockito.times(1)).save(forum);

        var newForum = (Forum) resp.getBody();

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertNotNull(newForum);
        assertEquals(forum, newForum);
        assertEquals(1, newForum.getEntries().size());

        assertEquals(entry.getId(), newForum.getEntries().get(0).getId());
        assertEquals(entry.getTitle(), newForum.getEntries().get(0).getTitle());
        assertEquals(entry.getDate(), newForum.getEntries().get(0).getDate());

        assertEquals(loggedUser, newForum.getEntries().get(0).getUser());
        assertNotEquals(0L, newForum.getEntries().get(0).getDate());
        assertEquals(1, newForum.getEntries().get(0).getComments().size());
        assertEquals(loggedUser, newForum.getEntries().get(0).getComments().get(0).getUser());
        assertNotEquals(0L, newForum.getEntries().get(0).getComments().get(0).getDate());
    }

    @Test
    void testNewEntry_Unauthorized1() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var forum = new Forum();
        forum.setEntries(new ArrayList<Entry>());

        var courseDetails = new CourseDetails();
        courseDetails.setId(1L);
        courseDetails.setCourse(course);
        courseDetails.setForum(forum);

        var comment = new Comment();
        var comments = new ArrayList<Comment>();
        comments.add(comment);

        var entry = new Entry("Meu trabalho", 2021L, loggedUser);
        entry.setId(1L);
        entry.setComments(comments);

        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        Mockito.when(authorizationService.checkAuthorization(course, course.getTeacher())).thenReturn(null);
        Mockito.when(courseDetailsRepository.findById(courseDetails.getId())).thenReturn(java.util.Optional.of(courseDetails));

        var resp = entryController.newEntry(entry, String.valueOf(courseDetails.getId()));


        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    void testNewEntry_Unauthorized2() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var forum = new Forum();
        forum.setEntries(new ArrayList<Entry>());

        var courseDetails = new CourseDetails();
        courseDetails.setId(1L);
        courseDetails.setCourse(course);
        courseDetails.setForum(forum);

        var comment = new Comment();
        var comments = new ArrayList<Comment>();
        comments.add(comment);

        var entry = new Entry("Meu trabalho", 2021L, loggedUser);
        entry.setId(1L);
        entry.setComments(comments);

        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);
        Mockito.when(authorizationService.checkAuthorizationUsers(Mockito.any(), Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        Mockito.when(courseDetailsRepository.findById(courseDetails.getId())).thenReturn(java.util.Optional.of(courseDetails));

        var resp = entryController.newEntry(entry, String.valueOf(courseDetails.getId()));


        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    void testNewEntry_InvalidNumber() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var forum = new Forum();
        forum.setEntries(new ArrayList<Entry>());

        var courseDetails = new CourseDetails();
        courseDetails.setId(1L);
        courseDetails.setCourse(course);
        courseDetails.setForum(forum);

        var comment = new Comment();
        var comments = new ArrayList<Comment>();
        comments.add(comment);

        var entry = new Entry("Meu trabalho", 2021L, loggedUser);
        entry.setId(1L);
        entry.setComments(comments);

        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);
        Mockito.when(authorizationService.checkAuthorization(course, course.getTeacher())).thenReturn(null);
        Mockito.when(courseDetailsRepository.findById(courseDetails.getId())).thenReturn(java.util.Optional.of(courseDetails));

        var resp = entryController.newEntry(entry, "aa");

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }
}