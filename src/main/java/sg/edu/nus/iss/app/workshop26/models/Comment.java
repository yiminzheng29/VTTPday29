package sg.edu.nus.iss.app.workshop26.models;


import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Comment {
    private String characterId;
    private String user;
    private String text;
    private String timestamp;

    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getCharacterId() {
        return characterId;
    }
    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
            .add("id", getCharacterId())
            .add("user", getUser())
            .add("text", getText())
            .add("timestamp", getTimestamp())
            .build();
    }

    public static Comment fromCache(JsonObject doc) {
        Comment comment = new Comment();
        comment.setCharacterId(doc.getString("id"));
        comment.setUser(doc.getString("user"));
        comment.setText(doc.getString("text"));
        comment.setTimestamp(doc.getString("timestamp"));

        return comment;
    }
    public static Comment create(Document d) {
        Comment comment = new Comment();
        comment.setCharacterId(d.getString("id"));
        comment.setUser(d.getString("user"));
        comment.setText(d.getString("text"));
        comment.setTimestamp(d.getString("timestamp"));

        return comment;
    }
}
