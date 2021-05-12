package com.fullteaching.backend.comment;

import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.coursedetails.CourseDetailsRepository;
import com.fullteaching.backend.entry.Entry;
import com.fullteaching.backend.entry.EntryRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class CommentControllerTest {
    @InjectMocks
    private CommentController commentController;

    @Mock
    private EntryRepository entryRepository;

    @Mock
    private CommentRepository commentRepository;

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
    }


    @Test
    void testNewComment_saveSuccess() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var courseDetails = new CourseDetails();
        courseDetails.setId(1L);
        courseDetails.setCourse(course);

        var comment = new Comment();

        var entry = new Entry("Meu trabalho", 2021L, loggedUser);
        entry.setId(1L);

        Mockito.when(courseDetailsRepository.findById(courseDetails.getId())).thenReturn(java.util.Optional.of(courseDetails));
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);
        Mockito.when(authorizationService.checkAuthorizationUsers(courseDetails, course.getAttenders())).thenReturn(null);
        Mockito.when(entryRepository.findById(entry.getId())).thenReturn(Optional.of(entry));
        Mockito.when(user.getLoggedUser()).thenReturn(loggedUser);

        var resp = commentController.newComment(comment, String.valueOf(entry.getId()), String.valueOf(courseDetails.getId()));

        Mockito.verify(entryRepository, Mockito.times(1)).save(entry);

        var newEntry = (Entry) resp.getBody();


        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertNotNull(newEntry);
        assertEquals(entry, newEntry);

        var newComments = newEntry.getComments();

        assertEquals(1, newComments.size());


        assertEquals(loggedUser, newComments.get(0).getUser());
        assertNotEquals(0L, newComments.get(0).getDate());
    }

    @Test
    void testNewComment_Unauthorized1() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var courseDetails = new CourseDetails();
        courseDetails.setId(1L);
        courseDetails.setCourse(course);

        var comment = new Comment();
        var comments = new ArrayList<Comment>();
        comments.add(comment);

        var entry = new Entry("Meu trabalho", 2021L, loggedUser);
        entry.setId(1L);
        entry.setComments(comments);

        Mockito.when(courseDetailsRepository.findById(courseDetails.getId())).thenReturn(java.util.Optional.of(courseDetails));
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        Mockito.when(authorizationService.checkAuthorizationUsers(courseDetails, course.getAttenders())).thenReturn(null);
        Mockito.when(entryRepository.findById(entry.getId())).thenReturn(Optional.of(entry));

        var resp = commentController.newComment(comment, String.valueOf(entry.getId()), String.valueOf(courseDetails.getId()));

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    void testNewComment_Unauthorized2() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var courseDetails = new CourseDetails();
        courseDetails.setId(1L);
        courseDetails.setCourse(course);

        var comment = new Comment();
        var comments = new ArrayList<Comment>();
        comments.add(comment);

        var entry = new Entry("Meu trabalho", 2021L, loggedUser);
        entry.setId(1L);
        entry.setComments(comments);

        Mockito.when(courseDetailsRepository.findById(courseDetails.getId())).thenReturn(java.util.Optional.of(courseDetails));
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);
        Mockito.when(authorizationService.checkAuthorizationUsers(courseDetails, course.getAttenders())).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        Mockito.when(entryRepository.findById(entry.getId())).thenReturn(Optional.of(entry));

        var resp = commentController.newComment(comment, String.valueOf(entry.getId()), String.valueOf(courseDetails.getId()));

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }


    @Test
    void testNewComment_InvalidNumber1() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var courseDetails = new CourseDetails();
        courseDetails.setId(1L);
        courseDetails.setCourse(course);

        var comment = new Comment();
        var comments = new ArrayList<Comment>();
        comments.add(comment);

        var entry = new Entry("Meu trabalho", 2021L, loggedUser);
        entry.setId(1L);
        entry.setComments(comments);

        Mockito.when(courseDetailsRepository.findById(courseDetails.getId())).thenReturn(java.util.Optional.of(courseDetails));
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);
        Mockito.when(authorizationService.checkAuthorizationUsers(courseDetails, course.getAttenders())).thenReturn(null);
        Mockito.when(entryRepository.findById(entry.getId())).thenReturn(Optional.of(entry));

        var resp = commentController.newComment(comment, "s", String.valueOf(courseDetails.getId()));

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void testNewComment_InvalidNumber2() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var courseDetails = new CourseDetails();
        courseDetails.setId(1L);
        courseDetails.setCourse(course);

        var comment = new Comment();
        var comments = new ArrayList<Comment>();
        comments.add(comment);

        var entry = new Entry("Meu trabalho", 2021L, loggedUser);
        entry.setId(1L);
        entry.setComments(comments);

        Mockito.when(courseDetailsRepository.findById(courseDetails.getId())).thenReturn(java.util.Optional.of(courseDetails));
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);
        Mockito.when(authorizationService.checkAuthorizationUsers(courseDetails, course.getAttenders())).thenReturn(null);
        Mockito.when(entryRepository.findById(entry.getId())).thenReturn(Optional.of(entry));

        var resp = commentController.newComment(comment, String.valueOf(entry.getId()), "aa");

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void testNewComment_NullEntry() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var courseDetails = new CourseDetails();
        courseDetails.setId(1L);
        courseDetails.setCourse(course);

        var comment = new Comment();
        var comments = new ArrayList<Comment>();
        comments.add(comment);

        var entry = new Entry("Meu trabalho", 2021L, loggedUser);
        entry.setId(1L);
        entry.setComments(comments);

        Mockito.when(courseDetailsRepository.findById(courseDetails.getId())).thenReturn(java.util.Optional.of(courseDetails));
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);
        Mockito.when(authorizationService.checkAuthorizationUsers(courseDetails, course.getAttenders())).thenReturn(null);
        Mockito.when(entryRepository.findById(entry.getId())).thenReturn(Optional.empty());

        var resp = commentController.newComment(comment, String.valueOf(entry.getId()), String.valueOf(courseDetails.getId()));

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void testNewComment_WithParent() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var courseDetails = new CourseDetails();
        courseDetails.setId(1L);
        courseDetails.setCourse(course);

        var parentComment = new Comment();
        parentComment.setId(1L);
        parentComment.setReplies(new ArrayList<>());

        var comment = new Comment();
        comment.setId(2L);
        comment.setCommentParent(parentComment);
        var comments = new ArrayList<Comment>();
        comments.add(comment);

        var entry = new Entry("Meu trabalho", 2021L, loggedUser);
        entry.setId(1L);
        entry.setComments(comments);

        Mockito.when(courseDetailsRepository.findById(courseDetails.getId())).thenReturn(java.util.Optional.of(courseDetails));
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);
        Mockito.when(authorizationService.checkAuthorizationUsers(courseDetails, course.getAttenders())).thenReturn(null);
        Mockito.when(entryRepository.findById(entry.getId())).thenReturn(Optional.of(entry));
        Mockito.when(commentRepository.findById(comment.getCommentParent().getId())).thenReturn(Optional.of(parentComment));

        var resp = commentController.newComment(comment, String.valueOf(entry.getId()), String.valueOf(courseDetails.getId()));

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(entry, resp.getBody());
    }

    @Test
    void testNewComment_WithNullParent() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var courseDetails = new CourseDetails();
        courseDetails.setId(1L);
        courseDetails.setCourse(course);

        var parentComment = new Comment();
        parentComment.setId(1L);
        parentComment.setReplies(new ArrayList<>());

        var comment = new Comment();
        comment.setId(2L);
        comment.setCommentParent(parentComment);
        var comments = new ArrayList<Comment>();
        comments.add(comment);

        var entry = new Entry("Meu trabalho", 2021L, loggedUser);
        entry.setId(1L);
        entry.setComments(comments);

        Mockito.when(courseDetailsRepository.findById(courseDetails.getId())).thenReturn(java.util.Optional.of(courseDetails));
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);
        Mockito.when(authorizationService.checkAuthorizationUsers(courseDetails, course.getAttenders())).thenReturn(null);
        Mockito.when(entryRepository.findById(entry.getId())).thenReturn(Optional.of(entry));
        Mockito.when(commentRepository.findById(comment.getCommentParent().getId())).thenReturn(Optional.empty());

        var resp = commentController.newComment(comment, String.valueOf(entry.getId()), String.valueOf(courseDetails.getId()));

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }
}