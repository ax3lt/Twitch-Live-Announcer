package it.ax3lt.Utils;

import it.ax3lt.Classes.StreamData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StreamUtilsTest {

    @BeforeEach
    void resetStreamQueue() throws Exception {
        Field queueField = StreamUtils.class.getDeclaredField("streamQueue");
        queueField.setAccessible(true);
        queueField.set(null, new HashMap<String, StreamData>());
    }

    @Test
    void enqueueStreamAddsNewChannel() throws Exception {
        invokeEnqueue("channelOne", "stream1", "Game", "Title");

        Map<String, StreamData> queue = StreamUtils.getStreamQueue();
        assertEquals(1, queue.size());
        assertTrue(queue.containsKey("channelOne"));

        StreamData data = queue.get("channelOne");
        assertNotNull(data);
        assertEquals("stream1", data.getStreamId());
        assertEquals("Game", data.getGameName());
        assertEquals("Title", data.getTitle());
    }

    @Test
    void enqueueStreamDoesNotOverwriteExistingChannel() throws Exception {
        invokeEnqueue("channelOne", "stream1", "Game", "Title");
        StreamData original = StreamUtils.getStreamQueue().get("channelOne");

        invokeEnqueue("channelOne", "stream2", "Another Game", "Another Title");

        StreamData afterSecondCall = StreamUtils.getStreamQueue().get("channelOne");
        assertSame(original, afterSecondCall);
        assertEquals("stream1", afterSecondCall.getStreamId());
    }

    @Test
    void dequeueStreamRemovesChannel() throws Exception {
        Map<String, StreamData> queue = StreamUtils.getStreamQueue();
        queue.put("channelOne", new StreamData("stream1", "Game", "Title"));

        StreamUtils.dequeueStream("channelOne");

        assertFalse(queue.containsKey("channelOne"));
    }

    private void invokeEnqueue(String channel, String streamId, String streamGameName, String streamTitle)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method enqueue = StreamUtils.class.getDeclaredMethod("enqueueStream", String.class, String.class, String.class, String.class);
        enqueue.setAccessible(true);
        enqueue.invoke(null, channel, streamId, streamGameName, streamTitle);
    }
}
