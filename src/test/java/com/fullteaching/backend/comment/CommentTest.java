package com.fullteaching.backend.comment;

import com.fullteaching.backend.user.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
class CommentTest {

    @Test
    void testCommentWithUser() {
        var message = "messa";
        var date = 2021L;
        var user = new User();

        var comment = new Comment(message, date, user);

        assertEquals(message, comment.getMessage());
        assertEquals(date, comment.getDate());
        assertEquals(user, comment.getUser());


        var message2 = "messa2";
        comment.setMessage(message2);
        assertEquals(message2, comment.getMessage());
    }

    @Test
    void testCommentWithParent() {
        var message = "messa";
        var date = 2021L;
        var user = new User();
        var parent = new Comment();

        var comment = new Comment(message, date, user, parent);

        assertEquals(message, comment.getMessage());
        assertEquals(date, comment.getDate());
        assertEquals(user, comment.getUser());
        assertEquals(parent, comment.getCommentParent());
    }
}