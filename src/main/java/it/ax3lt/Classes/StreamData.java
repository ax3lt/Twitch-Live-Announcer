package it.ax3lt.Classes;

public class StreamData {
    private final String streamId;
    private final String gameName;
    private final String title;

    public StreamData(String streamId, String gameName, String title) {
        this.streamId = streamId;
        this.gameName = gameName;
        this.title = title;
    }

    public String getStreamId() {
        return streamId;
    }

    public String getGameName() {
        return gameName;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "StreamData{" +
                "streamId='" + streamId + '\'' +
                ", gameName='" + gameName + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
