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

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        var comments = new ArrayList<Comment>();
        comments.add(comment);

        var entry = new Entry("Meu trabalho", 2021L, loggedUser);
        entry.setId(1L);
        entry.setComments(comments);

        Mockito.when(courseDetailsRepository.findById(courseDetails.getId())).thenReturn(java.util.Optional.of(courseDetails));
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);
        Mockito.when(authorizationService.checkAuthorizationUsers(courseDetails, course.getAttenders())).thenReturn(null);
        Mockito.when(entryRepository.findById(entry.getId())).thenReturn(Optional.of(entry));

        var resp = commentController.newComment(comment, String.valueOf(entry.getId()), String.valueOf(courseDetails.getId()));

        Mockito.verify(entryRepository, Mockito.times(1)).save(entry);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(entry, resp.getBody());
    }
}