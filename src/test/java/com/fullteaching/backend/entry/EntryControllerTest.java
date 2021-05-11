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

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

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


        var entry = new Entry("Meu trabalho", 2021L, loggedUser);
        entry.setId(1L);
        entry.setComments(comments);

        Mockito.when(authorizationService.checkAuthorization(course, course.getTeacher())).thenReturn(null);
        Mockito.when(courseDetailsRepository.findById(courseDetails.getId())).thenReturn(java.util.Optional.of(courseDetails));

        var resp = entryController.newEntry(entry, String.valueOf(courseDetails.getId()));

        Mockito.verify(forumRepository, Mockito.times(1)).save(forum);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(forum, resp.getBody());

    }
}