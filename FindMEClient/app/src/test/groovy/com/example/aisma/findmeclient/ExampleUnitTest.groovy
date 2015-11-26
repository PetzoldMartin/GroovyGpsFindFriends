package com.example.aisma.findmeclient;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 3);
    };
    @Test
    public void vectorTest(){
        def b= new Vector(x: 1,y: 1)
        assertEquals(b.getX(), 2);
        assertEquals(b.getY(),1);
    }
}