package com.fullteaching.backend.course;

import com.fullteaching.backend.comment.Comment;
import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.user.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class CourseTest {

    @Test
    void testCourse() {
        var title = "title";
        var image = "image";
        var user = new User();

        var course = new Course(title, image, user);

        assertEquals(title, course.getTitle());
        assertEquals(image, course.getImage());
        assertEquals(user, course.getTeacher());
    }

    @Test
    void testCourseWithDetails() {
        var title = "title";
        var image = "image";
        var user = new User();
        var courseDetails = new CourseDetails();

        var course = new Course(title, image, user, courseDetails);

        assertEquals(title, course.getTitle());
        assertEquals(image, course.getImage());
        assertEquals(user, course.getTeacher());
        assertEquals(courseDetails, course.getCourseDetails());
    }

}