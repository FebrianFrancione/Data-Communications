
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.Socket;
import java.rmi.server.LogStream;
import java.util.Scanner;
//import net
import java.net.MalformedURLException;
import java.net.URL;
//import io
import javax.swing.text.AbstractDocument;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;



public class HTTPclient implements Serializable{

    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 80);
            PrintWriter pr = new PrintWriter(s.getOutputStream());
            InputStreamReader in = new InputStreamReader(s.getInputStream());
            BufferedReader bf = new BufferedReader(in);
            Scanner scanner = new Scanner(System.in);

            String user_input = "";
            System.out.println("Welcome to the Command Line: Please enter your command:");
            user_input = scanner.nextLine();
            System.out.println("You have input: " + user_input);


            while (!user_input.equals("exit")) {
                try{
                    URL url = null;
                    String parameters[];
                    String urlString = null; //
                    String hostString = "";
                    String bodyString = "";
                    // boolean settings for the command line interface, sets to true if user enters command.
                    boolean v = false;
                    boolean h = false;
                    boolean d = false;
                    boolean f = false;
///                    check
                    String contentType = "application/json";
                    Request.Request_Type requestType = Request.Request_Type.GET;
                    Request.HTTP_version httpVersion = Request.HTTP_version.HTTP1_0;
//                    Body body = null;
                    Query_Parameters queryParameters = new Query_Parameters();
                    String test = "?";
                    Request request;
                    FileReader fileReader;
                    File input_file = null;
//                    processing user input
                    //split the user input into string array chunks.
                    parameters = user_input.split("\\s+");
                    //check if the user input is empty or not start with httpc
                    if (parameters.length <= 1 || !parameters[0].equals("httpc")) {
                        System.out.println("Invalid command line.");
                    } else {
                        for (int i = 1; i < parameters.length; i++) {
                            // for help commands
                            if (parameters[i].equals("help")) {

                                if (parameters.length > i + 1 && parameters[i + 1].equals("get")) {
                                    System.out.println("check 0: gethelp");
                                    help("get_help");

                                } else if (parameters.length > i + 1 && parameters[i + 1].equals("post")) {
                                    help("post_help");

                                } else {
                                    help("");

                                }
                            }
                            // check if user wants verbose info
                            else if (parameters[i].equals("-v")) {
                                //TODO
                                v = true;
                                //check if user wants different content type
                            }
//                            else if (parameters[i].equals("-h")) {
//                                //TODO
//                                h = true;
//
//                                for (int l = 0; l < parameters[i + 1].length(); l++) {
//                                    if (parameters[i + 1].charAt(l) == ':') {
//                                        contentType = "";
//                                        for (int m = l + 1; m < parameters[i + 1].length(); m++) {
//                                            contentType += parameters[i + 1].charAt(m);
//                                        }
//
//                                    }
//                                }
//
//                                //check if the user wants GET
//                            }
//                            Checkpoint
                            else if (parameters[i].equals("get")) {
                                System.out.println("check 1: get reached, calling request type get");
                                requestType = Request.Request_Type.GET;
                                //check if the user wants POST
                            }
                            else if (parameters[i].equals("post")) {
//                                requestType = Request.Request_Type.POST;

                                //check if the host is in correct format and put it into url
                            }
                            else if (parameters[i].contains("http://")) {
                                System.out.println("1) print url" + urlString + "before");
                                urlString = parameters[i];
                                System.out.println("urlstriong is param[i] " + urlString);
                                String urlStringNoQuote = urlString.replace("\'", ""); //remove '' from string
//                                System.out.println("Url with no quotes " + urlStringNoQuote);

//                                dont need
                                url = new URL(urlStringNoQuote);
                                System.out.println("new url no quotes: " + url);

//                                System.out.println("Transofmring to new url " + url);

//                                dont need
                                hostString = url.getHost();
//                                System.out.println("Getting the url host " + hostString);

//                                String Path= url.getPath();
//                                System.out.println("url path: " + Path);
//                                String Auth = url.getAuthority();
//                                System.out.println("url authrority: " + Auth);
//                                Object cont = url.getContent();
//                                System.out.println("url content: " + cont);
//                                int defaultPort = url.getDefaultPort();
//                                System.out.println("url default port: " + defaultPort);
//                                String file = url.getFile();
//                                System.out.println("url file "+ file);
//                                String prot = url.getProtocol();
//                                System.out.println("url protocol: " + prot);
//                                String ref=url.getRef();
//                                System.out.println("url ref: " + ref);
//                                String userI= url.getUserInfo();
//                                System.out.println("User info: " + userI);
//                                String query = url.getQuery();
//                                System.out.println("Query from url "+query);
//                                System.out.println("url is "+urlString);
                                if (url.getQuery() == null) {
                                    test="?";
//                                    new Query_Parameters("");

                                } else {
                                    queryParameters = new Query_Parameters(url.getQuery());
                                    System.out.println("query param: " + queryParameters);
//                                    Query_Parameters query_parameters = new Query_Parameters(url.getQuery());
//                                    System.out.println(query_parameters);
//                                    test="?" + url.getQuery();
                                }
                                //check if user wants different version of HTTP
                            }
//                            else if (parameters[i].equals("-d")) {
//                                //TODO
//                                d = true;
//
//                                for (int j = 0; j < input_file.length(); j++) {
//                                    if (user_input.charAt(j) == '\'') {
//                                        j++;
//                                        for (int k = j; k < input_file.length(); k++) {
//                                            if (user_input.charAt(k) == '\'') {
//                                                break;
//                                            } else {
//                                                bodyString += user_input.charAt(k);
//                                            }
//                                        }
//                                        break;
//                                    }
//                                }
////                                body = new Body(bodyString);
//                            }
                            //check if user wants to associate the content of a input_file to the body HTTP POST
//                            else if (parameters[i].equals("-f")) {
//                                //TODO
//                                f = true;
//
//                                try {
//                                    input_file = new File(parameters[i + 1]);
//                                    System.out.println("input_file: " + input_file);
//
//                                    fileReader = new FileReader(input_file);
//                                    System.out.println("fileReader: " + fileReader);
//                                    int character;
//                                    while ((character = fileReader.read()) != -1) {
//                                        bodyString += (char) character;
//                                    }
//                                    System.out.println(bodyString);
////                                    body = new Body(bodyString);
//                                } catch (FileNotFoundException e) {
//                                    System.out.println("Please input Absolute Path of the input_file!");
//                                    //e.printStackTrace();
//                                } catch (IOException e) {
//                                    System.out.println("IO Exception occurred");
//                                    //e.printStackTrace();
//                                }
//                            }
                        }
                    }


                    //retrieved all information from user's input, now we could send our request and receive info
                    try {
//                        System.out.println("entering final stage " + url);
                        if (requestType.equals(Request.Request_Type.GET) && url != null) {
                            System.out.println("Get and not null enter");
////                            System.out.println("Making a new request with requestype "+requestType +" query paramters: "+ queryParameters+" and http version "+ httpVersion);
////                            System.out.println("Making a new request with requestype "+requestType +" query paramters: "+ test +" and http version "+ httpVersion);
//                            System.out.println("test" + test);
//                            request = new Request(requestType, queryParameters, httpVersion);
////                            System.out.println("replacing test with url: " + urlString);
//                            request = new Request(requestType, test, httpVersion);
////                            System.out.println("Cjeckpoint");
//                            System.out.println("hostring: "+hostString);
////                            System.out.println("v status "+v);
////                            System.out.println("This is the irl string: " + urlString.getClass());
//                            System.out.println("Entering the GET method");
//                            new GET(hostString, 80, url, request, v);
//                            pr.flush(new HTTPserver.GET(hostString,80,url, request,v));
//                            new HTTPserver().GET(80,url,v);
                            request = new Request(requestType, queryParameters, httpVersion);
                            new HTTPserver().GET(url,v,request);
                            pr.print(80);
                            pr.flush();

//                            pr.flush(new HTTPserver.GET(hostString,80,url,v));

                            pr.println("Test123");
                            pr.flush();
                        }
//                        else if (requestType.equals(Request.Request_Type.POST) && urlString != null) {
//                            request = new Request(requestType, contentType, httpVersion, queryParameters, body);
//                            if (d == true && f == true) {
//                                System.out.println("Either [-d] or [-f] can be used but not both.");
//                            } else {
//                                new POST(hostString, 80, request, v);
//                            }

//                        }
                    } catch (NullPointerException e) {
                        //System.out.println(e);
                        System.out.println("Due to the input mistake of input_file path, we cannot provide services.");
                    } catch (Exception e) {
                        //System.out.println(e);
                        System.out.println("Some error occurred, please check your input.");
                    }

                }catch (MalformedURLException URLe){
                    System.out.println(URLe);
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("Invalid Comman!");
                }
                System.out.println("\nEnter the command line here: ");
                user_input = scanner.nextLine();
            }
            scanner.close();

            System.out.print("Command Line has exited. Thank you for using our program!");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    //    helper method for command lines
    public static void help(String type) {
        if (type.equals("get_help")) {
            System.out.println(
                    "Usage: httpc get [-v] [-h key:value] URL\n"
                            + "\r\n"
                            + "Get executes a HTTP GET request for a given URL"
                            + "\r\n"
                            + "\t -v		    Prints the detail of the response such as protocol, status and headers.\n"
                            + "\t -h key:value	Associates headers to HTTP Request with the format 'key:value'.\n"
            );
        } else if (type.equals("post_help")) {
            System.out.println(
                    "Usage: httpc httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"
                            + "\r\n"
                            + "httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"
                            + "\r\n"
                            + "\t -v			Prints the detail of the response such as protocol, status and headers.\n"
                            + "\t -h key:value	Associates headers to HTTP Request with the format 'key:value'.\n"
                            + "\t -d string		Associates an inline data to the body HTTP POST request.\n"
                            + "\t -f file		Associates the content of a file to the body HTTP POSTrequest.\n"
                            + "\r\n"
                            + "Either [-d] or [-f] can be used but not both."
            );
        } else{
            System.out.println("httpc is a curl-like application but supports HTTP protocol only.\n"
                    + "Usage:\n"
                    + "\t httpc command [arguments]\n"
                    + "The commands are:\n"
                    + "\t get     executes a HTTP GET request and prints the response.\n"
                    + "\t post    executes a HTTP POST request and prints the response.\n"
                    + "\t help    prints this screen.\n"
                    + "Use \"httpc help [command]\" for more information about a command."
            );
        }
    }

//Body class is to create body for POST request.
//Body should locate at the end of the request.
//The body format should follow the Content-Type of request.
//By default the body is "{}" means empty
    public static class Body {
        String body;
        public Body(){
            body = "{}";
        }
        public Body(String body){
            this.body = body;
        }
        public int getBodyLength(){
            return body.length();
        }
        public String getBodyContent() {
            return body;
        }
    }


//Query_Parameters class is to create query for both GET and POST method.
//Query should locate after /post or /get.
//e.g. "POST /post?info=info HTTP/1.0\r\n" "?info=info " is query.
//NOTICE: if query not empty, should add "?" before the query.
    public static class Query_Parameters {
        String query_Parameter;
        public Query_Parameters() {
            query_Parameter = "? ";
        }
        public Query_Parameters(String query_Parameters) {
            System.out.println("Entering quetry param");
            this.query_Parameter = "?" + query_Parameters + " ";
            System.out.println("retruned query param "+this.query_Parameter);
        }
        public String getQuery_Parameter() {
            return query_Parameter;
        }
    }


    //Request class is to create request for both GET and POST.
    public static class Request {
        //    enum Request_Type is to define the request is use for GET or POST.
        public enum Request_Type {
            GET,
            POST;
        }
        //    enum HTTP_version is to define the request running on which version of HTTP.
//    by default using HTTP1.0
        public enum HTTP_version {
            HTTP1_0("HTTP/1.0\r\n");
            private String value;
            HTTP_version(String s) {
                value = s;
            }
            @Override
            public String toString() {
                return value;
            }
            }

        String request = "";
        Body body = new Body();
        Query_Parameters query = new Query_Parameters();
        String content_type = "application/json\r\n";
        HTTP_version http_version = HTTP_version.HTTP1_0;

        public Request() {
            request = "GET /get" + query.getQuery_Parameter() + http_version.toString() + "\r\n";
        }

        //    constructor of the Request
//    user must choose the request_type
        public Request(Request_Type request_type) {
            if (request_type.equals(Request_Type.GET)) {
                request = "GET /get" + query.getQuery_Parameter() + http_version.toString() + "\r\n";
            } else {
                request = "POST /post" + query.getQuery_Parameter() + http_version.toString()
                        + "Content-Type:" + content_type
                        + "Content-Length: " + body.getBodyLength() + "\r\n"
                        + "\r\n" + body.getBodyContent();
            }
        }

        //    constructor of the Request for GET
//    user must choose the request_type
        public Request(Request_Type request_type, Query_Parameters query_parameters, HTTP_version http_version) {
            this.query = query_parameters;
            this.http_version = http_version;
            if (request_type.equals(Request_Type.GET)) {
                request = "GET /get" + query.getQuery_Parameter() + http_version.toString() + "\r\n";
            }
        }
        //    another constructor of the Request for POST
//    If request_Type is GET, then the Content_Type and Body should be empty.
        public Request(Request_Type request_type, String content_type, HTTP_version http_version, Query_Parameters query_parameters, Body body)
        {
            this.body = body;
            this.content_type = content_type;
            this.query = query_parameters;
            this.http_version = http_version;

            if (request_type.equals(Request_Type.POST)) {
                this.request = "POST /post" + query.getQuery_Parameter() + http_version.toString()
                        + "Content-Type:" + this.content_type + "\r\n"
                        + "Content-Length: " + body.getBodyLength() + "\r\n"
                        + "\r\n" + body.getBodyContent();
            }
        }
        public String getRequest () {
            return request;
        }
    }
}