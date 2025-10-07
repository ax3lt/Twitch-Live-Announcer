package it.ax3lt.Classes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StreamDataTest {

    @Test
    void gettersReturnConstructorValues() {
        StreamData data = new StreamData("stream123", "Just Chatting", "Hanging out");

        assertEquals("stream123", data.getStreamId());
        assertEquals("Just Chatting", data.getGameName());
        assertEquals("Hanging out", data.getTitle());
    }

    @Test
    void toStringContainsAllFields() {
        StreamData data = new StreamData("stream123", "Just Chatting", "Hanging out");

        String asString = data.toString();

        assertTrue(asString.contains("stream123"));
        assertTrue(asString.contains("Just Chatting"));
        assertTrue(asString.contains("Hanging out"));
    }
}
