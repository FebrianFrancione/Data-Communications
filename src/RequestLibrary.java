import jdk.jshell.JShell;

import java.net.Socket;
import java.util.Locale;
import java.util.Scanner;
import java.io.*;
import java.net.URL;
//public interface HTTPRequest{
//    ResponseLibrary handle_request(ResponseLibrary http_request);
//}
public class RequestLibrary {


    //
//    private final int PORT  = 80;
//    private final String VERSION = "HTTP/1.0";
//    private final String USER_AGENT = "USER-AGENT: MyAwesomeBrowser";
//    private BufferedReader reader;
//    private PrintWriter writer;
//    private Scanner scanner;
//    private Socket socket;
//    private URL url;
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

    //    public static class request_assembler {
//        String method, request, http_version, body;
//        int content_length;
//
//        //default constructor
//        public request_assembler(){
//            this.method = method;
//        }
//        public request_assembler request(String URI){
//            this.request = URI;
//            return this;
//        }
//        public request_assembler body(String body){
//            this.body = body;
//            return this;
//        }
//        public request_assembler content_lenth(int content_length){
//            this.content_length = content_length;
//            return this;
//        }
//        public request_assembler version(String version){
//            this.http_version = version;
//            return this;
//        }
//
//        public RequestLibrary build(){
//            return new RequestLibrary(this);
//        }
//
//    }

//    private RequestLibrary(request_assembler request_assembler){
//        this.method = method;
//        this.request = request;
//        this.http_version = http_version;
//        this.content_length = content_length;
//        this.body = body;
//    }




}
//
//    public void get(String host) throws Exception {
//        String temp;
//        int skip = 0;
//        url = new URL(host);
//        socket = new Socket(url.getHost(), PORT);
//        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        writer = new PrintWriter(socket.getOutputStream());
//        writer.write("GET " + url.getFile() + " "+ (String) VERSION+ "\r\n" + USER_AGENT+ "\r\n\r\n");
//        writer.flush();
//        while ((temp = reader.readLine()) != null) {
//            if(skip > 8) {
//                System.out.println(temp);
//            }
//            else {
//                skip ++;
//            }
//        }
//        writer.close();
//        reader.close();
//        socket.close();
//    }
//
//    //Get with verbose
//    public void getWVerbose(String host) throws Exception {
//        String temp;
//        url = new URL(host);
//        socket = new Socket(url.getHost(), PORT);
//        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        writer = new PrintWriter(socket.getOutputStream());
//        writer.write("GET " + url.getFile() + " " +(String) VERSION+ "\r\n" + USER_AGENT+ "\r\n\r\n");
//        writer.flush();
//        while((temp = reader.readLine()) != null) {
//            System.out.println(temp);
//        }
//        writer.close();
//        reader.close();
//        socket.close();
//    }
//
//    //post with inline
//    public void post(String post) throws Exception {
//        String[] arr = post.split(" ");
//        int contentLength = 0;
//        String entityBody = "";
//        String keyValue = "";
//        String host = "";
//        String temp = "";
//        //System.out.println(arr.length);
//        for(int i = 0; i < arr.length; i++) {
//            if(arr[i].contains("http://httpbin.org/post")) {
//                host = arr[i];
//                host = host.substring(7,18);
//                //System.out.println("host: " + host);
//            }
//            if(arr[i].contains("-h")) {
//                keyValue = keyValue + arr[i+1];
//                //System.out.println("keyval: " + keyValue);
//            }
//            if(arr[i].contains("-d")) {
//                entityBody = entityBody + arr[i+1];
//                //System.out.println("entitybody: "+ entityBody);
//            }
//            if(arr[i].contains("1}")) {
//                entityBody = entityBody + " " + arr[i];
//                //to remove single quote
//                entityBody = entityBody.substring(1, 18);
//                contentLength = entityBody.length();
//                //System.out.println("contentlength:" + contentLength+ "entitybody: " + entityBody);
//            }
//        }
//        keyValue = keyValue + "\nContent-Length: " + contentLength;
//        socket = new Socket(host, PORT);
//        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        writer = new PrintWriter(socket.getOutputStream());
//        writer.write("POST " + "/post " +(String) VERSION+ "\r\n" + keyValue  +"\r\n\r\n" + entityBody);
//        writer.flush();
//        while((temp = reader.readLine()) != null) {
//            System.out.println(temp);
//        }
//        writer.close();
//        reader.close();
//        socket.close();
//    }
//
//    public void postFile(String host, File file) throws Exception{
//
//
//    }
//
//    public void help() {
//        System.out.println("httpc is a curl-like application but supports HTTP protocol only."+
//                "Usage: httpc command [arguments] The commands are:\n"+ "get executes a HTTP GET request and prints the response.\n"+
//                "post executes a HTTP POST request and prints the response. help prints this screen.\n"+
//                "Use \"httpc help [command]\" for more information about a command.");
//    }
//
//    public void helpGet(){
//        System.out.println("httpc help get\n"+
//                "usage:\n httpc get [-v] [-h key:value] URL"+
//                "Get executes a HTTP GET request for a given URL.\n"+
//                "-v Prints the detail of the response such as protocol, status, and headers.\n"+
//                "-h key:value Associates headers to HTTP Request with the format 'key:value'.");
//    }
//
//    public void helpPost() {
//        System.out.println("httpc help post\n usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"+
//                "Post executes a HTTP POST rgequest for a given URL with inline data or from file."+
//                "-vPrints the detail of the response such as protocol, status, and headers."+
//                "-h key:valueAssociates headers to HTTP Request with the format 'key:value'."+
//                "-d stringAssociates an inline data to the body HTTP POST request."+
//                "-f fileAssociates the content of a file to the body HTTP POST request."+
//                "Either [-d] or [-f] can be used but not both.");
//    }
