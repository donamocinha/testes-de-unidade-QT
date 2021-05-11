package com.fullteaching.backend.forum;

import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.coursedetails.CourseDetailsRepository;
import com.fullteaching.backend.entry.Entry;
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
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
class ForumControllerTest {

    @InjectMocks
    private ForumController forumController;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private CourseDetailsRepository courseDetailsRepository;

    private User loggedUser;


    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        loggedUser = new User("Flavia", "123", "Flavinha", "flavinha.jpg");
        loggedUser.setId(1L);
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);
    }

    @Test
    void testModifyForum_Success() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var forum = new Forum();
        forum.setEntries(new ArrayList<Entry>());
        forum.setActivated(false);

        var courseDetails = new CourseDetails();
        courseDetails.setId(1L);
        courseDetails.setCourse(course);
        courseDetails.setForum(forum);

        Mockito.when(courseDetailsRepository.findById(courseDetails.getId())).thenReturn(java.util.Optional.of(courseDetails));

        var resp = forumController.modifyForum(true, String.valueOf(courseDetails.getId()));

        Mockito.verify(courseDetailsRepository, Mockito.times(1)).save(courseDetails);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(true, resp.getBody());

    }
}