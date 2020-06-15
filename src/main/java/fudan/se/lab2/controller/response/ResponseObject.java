package fudan.se.lab2.controller.response;

public class ResponseObject<T> {
    private int statusCode;
    private String message;
    private T content;

    public ResponseObject(){}
    public ResponseObject(int statusCode, String message, T content){
        this.statusCode = statusCode;
        this.message = message;
        this.content = content;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public T getContent() {
        return content;
    }

    public void setData(T data) {
        this.content = data;
    }
}
