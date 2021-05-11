package com.fullteaching.backend.course;

import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.security.AuthorizationService;
import com.fullteaching.backend.user.User;
import com.fullteaching.backend.user.UserComponent;
import com.fullteaching.backend.user.UserRepository;
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
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class CourseControllerTest {

    @InjectMocks
    private CourseController courseController;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

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
    void testGetCourses_Success() {
        var userIDs = new HashSet<Long>();
        userIDs.add(loggedUser.getId());

        var users = new ArrayList<User>();
        users.add(loggedUser);

        var course = new Course("prog", "prog.jpg", loggedUser);
        var courses = new ArrayList<Course>();
        courses.add(course);

        Mockito.when(userRepository.findAllById(userIDs)).thenReturn(users);
        Mockito.when(courseRepository.findByAttenders(users)).thenReturn(courses);

        var resp = courseController.getCourses(String.valueOf(loggedUser.getId()));

        Mockito.verify(authorizationService, Mockito.times(1)).checkBackendLogged();
        Mockito.verify(userRepository, Mockito.times(1)).findAllById(userIDs);
        Mockito.verify(courseRepository, Mockito.times(1)).findByAttenders(users);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(courses, resp.getBody());
    }

    @Test
    void testGetCourses_Unauthorized() {
        var userIDs = new HashSet<Long>();
        userIDs.add(loggedUser.getId());

        var users = new ArrayList<User>();
        users.add(loggedUser);

        var course = new Course("prog", "prog.jpg", loggedUser);
        var courses = new ArrayList<Course>();
        courses.add(course);

        Mockito.when(userRepository.findAllById(userIDs)).thenReturn(users);
        Mockito.when(courseRepository.findByAttenders(users)).thenReturn(courses);
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));


        var resp = courseController.getCourses(String.valueOf(loggedUser.getId()));

        Mockito.verify(authorizationService, Mockito.times(1)).checkBackendLogged();

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    void testGetCourses_InvalidNumber() {
        var userIDs = new HashSet<Long>();
        userIDs.add(loggedUser.getId());

        var users = new ArrayList<User>();
        users.add(loggedUser);

        var course = new Course("prog", "prog.jpg", loggedUser);
        var courses = new ArrayList<Course>();
        courses.add(course);

        Mockito.when(userRepository.findAllById(userIDs)).thenReturn(users);
        Mockito.when(courseRepository.findByAttenders(users)).thenReturn(courses);

        assertDoesNotThrow(() -> {
            var resp = courseController.getCourses("a");
        });

        Mockito.verify(authorizationService, Mockito.times(1)).checkBackendLogged();
    }


    @Test
    void testGetCourse_Success() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));

        var resp = courseController.getCourse(String.valueOf(course.getId()));

        Mockito.verify(courseRepository, Mockito.times(1)).findById(course.getId());

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(course, resp.getBody());
    }

    @Test
    void testGetCourse_Unauthorized() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        var resp = courseController.getCourse(String.valueOf(course.getId()));

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    void testGetCourse_InvalidNumber() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);


        var resp = courseController.getCourse("a");

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void testNewCourse_Success() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));

        var resp = courseController.newCourse(course);

        Mockito.verify(courseRepository).save(course);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(course, resp.getBody());
    }

    @Test
    void testNewCourse_Unauthorized() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        var resp = courseController.newCourse(course);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }


    @Test
    void testModifyCourse_Success() {
        var courseID = 1L;
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(courseID);

        var modifiedCourse = new Course("ed", "ed.jpg", loggedUser);
        modifiedCourse.setId(courseID);

        Mockito.when(authorizationService.checkAuthorization(modifiedCourse, modifiedCourse.getTeacher())).thenReturn(null);
        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));

        var resp = courseController.modifyCourse(modifiedCourse);

        Mockito.verify(courseRepository, Mockito.times(1)).save(modifiedCourse);

        var savedCourse = (Course) resp.getBody();

        assertNotNull(savedCourse);
        assertEquals("ed", savedCourse.getTitle());
        assertEquals("ed.jpg", savedCourse.getImage());
    }

    @Test
    void testModifyCourse_Unauthorized() {
        var courseID = 1L;
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(courseID);

        var modifiedCourse = new Course("ed", "ed.jpg", loggedUser);
        modifiedCourse.setId(courseID);

        Mockito.when(authorizationService.checkAuthorization(modifiedCourse, modifiedCourse.getTeacher())).thenReturn(null);
        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        var resp = courseController.modifyCourse(modifiedCourse);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }


    @Test
    void testModifyCourse_UnauthorizedTeacher() {
        var courseID = 1L;
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(courseID);

        var modifiedCourse = new Course("ed", "ed.jpg", loggedUser);
        modifiedCourse.setId(courseID);

        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);
        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));
        Mockito.when(authorizationService.checkAuthorization(modifiedCourse, modifiedCourse.getTeacher())).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        var resp = courseController.modifyCourse(modifiedCourse);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    void testModifyCourse_WithCourseDetails() {
        var courseDetails = new CourseDetails();
        courseDetails.setInfo("Bla bla");

        var courseID = 1L;
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(courseID);
        course.setCourseDetails(courseDetails);

        var modifiedCourse = new Course("ed", "ed.jpg", loggedUser);
        modifiedCourse.setId(courseID);
        modifiedCourse.setCourseDetails(courseDetails);

        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);
        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));
        Mockito.when(authorizationService.checkAuthorization(modifiedCourse, modifiedCourse.getTeacher())).thenReturn(null);

        var resp = courseController.modifyCourse(modifiedCourse);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void testDeleteCourse_Success() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        Mockito.when(authorizationService.checkAuthorization(course, course.getTeacher())).thenReturn(null);
        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));

        var resp = courseController.deleteCourse(String.valueOf(course.getId()));

        Mockito.verify(courseRepository).delete(course);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(course, resp.getBody());
    }

    @Test
    void testDeleteCourse_TeacherUnauthorized() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        Mockito.when(authorizationService.checkAuthorization(course, course.getTeacher())).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));



        var resp = courseController.deleteCourse(String.valueOf(course.getId()));

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    void testDeleteCourse_Unauthorized() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));

        var resp = courseController.deleteCourse(String.valueOf(course.getId()));

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    void testDeleteCourse_InvalidNumber() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        Mockito.when(authorizationService.checkBackendLogged()).thenReturn(null);
        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));

        var resp = courseController.deleteCourse("aa");

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void testAddAttenders_Success() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var user = new User("flavia.elias@id.uff.com", "123", "Flavinha", "flavinha.jpg");
        var users = new ArrayList<User>();
        users.add(user);

        var attenders = new String[]{user.getName()};

        Mockito.when(authorizationService.checkAuthorization(course, course.getTeacher())).thenReturn(null);
        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));
        Mockito.when(userRepository.findByNameIn(Mockito.anyCollection())).thenReturn(users);

        var resp = courseController.addAttenders(attenders, String.valueOf(course.getId()));

        Mockito.verify(courseRepository, Mockito.times(1)).save(course);
        Mockito.verify(userRepository, Mockito.times(1)).saveAll(users);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void testAddAttenders_Unauthorized() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var user = new User("flavia.elias@id.uff.com", "123", "Flavinha", "flavinha.jpg");
        var users = new ArrayList<User>();
        users.add(user);

        var attenders = new String[]{user.getName()};

        Mockito.when(authorizationService.checkAuthorization(course, course.getTeacher())).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));
        Mockito.when(userRepository.findByNameIn(Mockito.anyCollection())).thenReturn(users);

        var resp = courseController.addAttenders(attenders, String.valueOf(course.getId()));

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }

    @Test
    void testAddAttenders_InvalidNumber() {
        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);

        var user = new User("flavia.elias@id.uff.com", "123", "Flavinha", "flavinha.jpg");
        var users = new ArrayList<User>();
        users.add(user);

        var attenders = new String[]{user.getName()};

        Mockito.when(authorizationService.checkAuthorization(course, course.getTeacher())).thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));
        Mockito.when(userRepository.findByNameIn(Mockito.anyCollection())).thenReturn(users);

        var resp = courseController.addAttenders(attenders, "a");

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }




    @Test
    void testDeleteAttenders_Success() {
        var user = new User("flavia.elias@id.uff.com", "123", "Flavinha", "flavinha.jpg");
        var users = new HashSet<User>();
        users.add(user);

        var course = new Course("prog", "prog.jpg", loggedUser);
        course.setId(1L);
        course.setAttenders(users);

        Mockito.when(authorizationService.checkAuthorization(course, course.getTeacher())).thenReturn(null);
        Mockito.when(courseRepository.findById(course.getId())).thenReturn(java.util.Optional.of(course));
        Mockito.when(userRepository.findByNameIn(Mockito.anyCollection())).thenReturn(users);

        var resp = courseController.deleteAttenders(course);

        Mockito.verify(courseRepository, Mockito.times(1)).save(course);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}