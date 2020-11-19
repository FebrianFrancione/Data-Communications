package prev;import java.net.Socket;
import java.util.Locale;
import java.util.Scanner;
import java.io.*;
import java.net.URL;
//public interface HTTPRequest{
//    ResponseLibrary handle_request(ResponseLibrary http_request);
//}
public class RequestLibrary {


    public static final String GET= "GET";
    public static final String POST = "POST";
    //request method components
    private String method, request, http_version, body, user_agent, content_type;
    private int content_length;

    // Getter amd setter

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getHttp_version() {
        return http_version;
    }

    public void setHttp_version(String http_version) {
        this.http_version = http_version;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getContent_length() {
        return content_length;
    }

    public void setContent_length(int content_length) {
        this.content_length = content_length;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    //constructor
    public RequestLibrary(String method, String request, String http_version, String body,String user_agent, int content_length,  String content_type) {
        this.method = method;
        this.request = request;
        this.http_version = http_version;
        this.body = body;
        this.content_length = content_length;
        this.user_agent = user_agent;
        this.content_type = content_type;
    }

    @Override
    public String toString() {
        return method + " " + request + " " + http_version + "\n" +
                "User-Agent: " + user_agent + "\n" +
                "Content-Length: " + content_length + "\n" +
                "Content-Type: " + content_type + "\n" +
                "\n" +
                ((content_length > 0 && body != null)? body : "" );
    }
}



