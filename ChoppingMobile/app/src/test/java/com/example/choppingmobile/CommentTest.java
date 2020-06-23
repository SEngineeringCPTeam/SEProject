package com.example.choppingmobile;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class CommentTest {
    Comment comment = new Comment();
    Map<String, Object> testMap;
    @Test
    public void getTimestamp() {
    }

    @Test
    public void toMap() {
        comment.commentId="id";
        comment.content="content";
        comment.writerId="writer";
        comment.time= FieldValue.serverTimestamp();
        testMap = comment.toMap();
        assertEquals(comment.commentId,testMap.get("commentId"));
    }

    @Test
    public void fromMap() {
    }
}