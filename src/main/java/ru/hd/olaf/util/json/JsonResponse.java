package ru.hd.olaf.util.json;

/**
 * Created by d.v.hozyashev on 20.04.2017.
 */
public class JsonResponse {
    private ResponseType type;
    private String message;

    public JsonResponse() {
    }

    public JsonResponse(ResponseType type, String message) {
        this.type = type;
        this.message = message;
    }

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
