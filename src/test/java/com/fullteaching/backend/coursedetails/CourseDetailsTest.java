package com.fullteaching.backend.coursedetails;

import com.fullteaching.backend.comment.Comment;
import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.user.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class CourseDetailsTest {
    @Test
    void testCourseDetails() {
        var course = new Course( "title", "image", new User());

        var courseDetails = new CourseDetails(course);

        assertEquals(course, courseDetails.getCourse());
    }

}