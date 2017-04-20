package ru.hd.olaf.util.json;

/**
 * Created by d.v.hozyashev on 20.04.2017.
 */
public class JsonAnswer {
    private AnswerType type;
    private String message;

    public JsonAnswer() {
    }

    public JsonAnswer(AnswerType type, String message) {
        this.type = type;
        this.message = message;
    }

    public AnswerType getType() {
        return type;
    }

    public void setType(AnswerType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
