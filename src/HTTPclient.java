
import java.util.Scanner;
//import net
import java.net.MalformedURLException;
import java.net.URL;
//import io
import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;

public class HTTPclient {

    public static void main(String[] args) {
        try {
            String user_input = "";
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter command: ");
            user_input = scanner.nextLine();

            while (!user_input.equals("exit")) {
                try {
//                    File imports. take inputs using fileReader
                    FileReader file_reader;
//                    defaulted to null
                    File input_file;
//setting body String to empty
                    String string_body = "";
//Setting http version to 1.0
                    Request.HTTP httpVersion = Request.HTTP.HTTP1_0;
//                    Creating Content-Type as application/json
                    String content_type = "application/json";
//                    Setting url & url String to null
                    URL url = null;
                    String string_url = null; //
//                    used in the GET/Post mthods
                    String hostString = "";
//                    creating an array for input parameters given by user
                    String input_parameters[];

                    // boolean settings for the command line interface, sets to true if user enters command.
//                    todo
//                    not fully implemented yet, to be continued.
//                    need to implement
                    boolean cmd_d = false;
                    boolean cmd_f = false;
                    boolean cmd_h = false;
                    boolean cmd_v = false;


//                    Request.Request_Type requestType = Request.Request_Type.GET;
//                    Body body = null;
//                    Query_Parameters queryParameters = new Query_Parameters();
//                    Request request;


//                    processing user input:
                    //split the user input by whitespaces
                    input_parameters = user_input.split("\\s+");
//                    user input must begin with httpc or returns error
                    if (!input_parameters[0].equals("httpc")) {
                        System.out.println("Input error: Command Line didn't recognize initial \"httpc\" parameter!");
//                        user input must also have a length greater than 1
                    } else if (input_parameters.length < 2) {
                        System.out.println("Input error: Invalid command line length!");
                    } else {
//                        processing the commands for getting help methods - options are help/gethelp/posthelp
                        for (int i = 1; i < input_parameters.length; i++) {
                            if (input_parameters[i].equals("help")) {
//                                other variable
                                if (input_parameters[i + 1].equals("get")) {
                                    System.out.println(help("get_help"));
                                } else if (input_parameters[i + 1].equals("post")) {
                                    System.out.println(help("post_help"));
                                } else {
//                                    return base help
                                    System.out.println(help(""));
                                }
                                // check if user wants verbose info
                            } else if (input_parameters[i].equals("get")) {
//todo GET
//                                requestType = Request.Request_Type.GET;
                                //check if the user wants POST
                            } else if (input_parameters[i].equals("post")) {
//todo post1
//                                requestType = Request.Request_Type.POST;
                                //check if the host is in correct format and put it into url
                            } else if (input_parameters[i].equals("-d")) {
                                cmd_d = true;
                                for (int j = 0; j < user_input.length(); j++) {
                                    if (user_input.charAt(j) == '\'') {
                                        j++;
                                        for (int k = j; k < user_input.length(); k++) {
                                            if (user_input.charAt(k) == '\'') {
                                                break;
                                            } else {
                                                string_body += user_input.charAt(k);
                                            }
                                        }
                                        break;
                                    }
                                }
//                                todo create new body
//                                body = new Body(bodyString);
                                //check if user wants to associate the content of a input_file to the body HTTP POST
                            } else if (input_parameters[i].equals("-f")) {
                                cmd_f = true;
                                try {
                                    input_file = new File(input_parameters[i + 1]);
                                    System.out.println("input_file: " + input_file);

                                    file_reader = new FileReader(input_file);
                                    System.out.println("fileReader: " + file_reader);
                                    int character;
                                    while ((character = file_reader.read()) != -1) {
                                        string_body += (char) character;
                                    }
                                    System.out.println(string_body);
//                                    todo new body
//                                    body = new Body(bodyString);
                                } catch (FileNotFoundException e) {
                                    System.out.println("Please input Absolute Path of the input_file!");
                                    //e.printStackTrace();
                                } catch (IOException e) {
                                    System.out.println("IO Exception occurred");
                                    //e.printStackTrace();
                                }
                            } else if (input_parameters[i].equals("-h")) {
                                cmd_h = true;
                                for (int l = 0; l < input_parameters[i + 1].length(); l++) {
                                    if (input_parameters[i + 1].charAt(l) == ':') {
                                        content_type = "";
                                        for (int m = l + 1; m < input_parameters[i + 1].length(); m++) {
                                            content_type += input_parameters[i + 1].charAt(m);
                                        }
                                    }
                                }
                                //check if the user wants GET
                            } else if (input_parameters[i].equals("-v")) {
                                cmd_v = true;
                                //check if user wants different content type
                            } else if (input_parameters[i].contains("http://")) {
                                string_url = input_parameters[i];
                                String urlStringNoQuote = string_url.replace("\'", ""); //remove '' from string

                                url = new URL(urlStringNoQuote);

                                hostString = url.getHost();
                                if (url.getQuery() == null) {
//                                    todo
//                                    new Query_Parameters("");
                                } else {
//                                    todo
//                                    new Query_Parameters(url.getQuery());
                                }

                                //check if user wants different version of HTTP
                            }
                        }
                    }

//todo try catch with reqyes types
// retrieved all information from user's input, now we could send our request and receive info
//                    try {
//                        if (requestType.equals(Request.Request_Type.GET) && urlString != null) {
//                            request = new Request(requestType, queryParameters, httpVersion);
//                            new GET(hostString, 80, request, cmd_v);
//                        } else if (requestType.equals(Request.Request_Type.POST) && urlString != null) {
//                            request = new Request(requestType, contentType, httpVersion, queryParameters, body);
//                            if (cmd_d == true && cmd_f == true) {
//                                System.out.println("Either [-d] or [-f] can be used but not both.");
//                            } else {
//                                new POST(hostString, 80, request, cmd_v);
//                            }
//                        }
//                    } catch (NullPointerException e) {
//                        //System.out.println(e);
//                        System.out.println("Due to the input mistake of input_file path, we cannot provide services.");
//                    } catch (Exception e) {
//                        //System.out.println(e);
//                        System.out.println("Some error occurred, please check your input.");
//                    }


                } catch (MalformedURLException URLe) {
                    System.out.println(URLe);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Invalid Command!");
                }
                System.out.println("\nEnter the command line here: ");
                user_input = scanner.nextLine();
            }
            scanner.close();

            System.out.print("Command Line has exited. Thank you for using our program!");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String help(String type) {
        if (type.equals("get_help")) {
            String GET_help_response = "Usage: httpc get [-v] [-h key:value] URL\n"
                    + "\r\n"
                    + "Get executes a HTTP GET request for a given URL"
                    + "\r\n"
                    + "\t -v		    Prints the detail of the response such as protocol, status and headers.\n"
                    + "\t -h key:value	Associates headers to HTTP Request with the format 'key:value'.\n";
            return GET_help_response;
        } else if (type.equals("post_help")) {
            String POST_help_response = "Usage: httpc httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"
                    + "\r\n"
                    + "httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"
                    + "\r\n"
                    + "\t -v			Prints the detail of the response such as protocol, status and headers.\n"
                    + "\t -h key:value	Associates headers to HTTP Request with the format 'key:value'.\n"
                    + "\t -d string		Associates an inline data to the body HTTP POST request.\n"
                    + "\t -f file		Associates the content of a file to the body HTTP POSTrequest.\n"
                    + "\r\n"
                    + "Either [-d] or [-f] can be used but not both.";
            return POST_help_response;
        } else {
            String base_help = "httpc is a curl-like application but supports HTTP protocol only.\n"
                    + "Usage:\n"
                    + "\t httpc command [arguments]\n"
                    + "The commands are:\n"
                    + "\t get     executes a HTTP GET request and prints the response.\n"
                    + "\t post    executes a HTTP POST request and prints the response.\n"
                    + "\t help    prints this screen.\n"
                    + "Use \"httpc help [command]\" for more information about a command.";
            return base_help;
        }
    }

    public static class Request {
        //        create empty string for request
        String request = "";
        String content_type = "application/json\r\n";
        //        setting HTTP version
        HTTP http_ = HTTP.HTTP1_0;

        public enum HTTP {
            HTTP1_0("HTTP/1.0\r\n");
            private String value;
            HTTP(String s) {
                value = s;
            }
            @Override
            public String toString() {
                return value;
            }
        }
        //    enum Request_Type is to define the request is use for GET or POST.
        public enum r_type {
            GET,
            POST;
        }

        Body body = new Body();
        Query_Parameters query = new Query_Parameters();

        public String getRequest() {
            return request;
        }
//        public Request(){
////            request = "GET /get" + query.getQuery_Parameter() + http_.toString() + "\r\n";
////        }
////        //    constructor of the Request
//////    user must choose the request_type
////        public Request(r_type rtype){
////            if (rtype.equals(r_type.GET)){
////                request = "GET /get" + query.getQuery_Parameter() + http_.toString() + "\r\n";
////            } else {
////                request = "POST /post" + query.getQuery_Parameter() + http_.toString()
////                        + "Content-Type:" + content_type
////                        + "Content-Length: " + body.getBodyLength() + "\r\n"
////                        + "\r\n" + body.getBodyContent();
////            }
////        }
////        //    constructor of the Request for GET

//    user must choose the request_type
        public Request(r_type rtype, Query_Parameters query_parameters, HTTP http_){
            this.query = query_parameters;
            this.http_ = http_;
            if (rtype.equals(r_type.GET)){
                request = "GET /get" + query.getQuery_Parameter() + http_.toString() + "\r\n";
            }
        }
        //    another constructor of the Request for POST
//    If request_Type is GET, then the Content_Type and Body should be empty.
        public Request(r_type rtype, String content_type, HTTP http_, Query_Parameters query_parameters, Body body){
            this.body = body;
            this.content_type = content_type;
            this.query = query_parameters;
            this.http_ = http_;

            if(rtype.equals(r_type.POST)){
                this.request = "POST /post" + query.getQuery_Parameter()  + http_.toString()
                        + "Content-Type:" + this.content_type + "\r\n"
                        + "Content-Length: " + body.getBodyLength() + "\r\n"
                        + "\r\n" + body.getBodyContent();
            }
        }


    }
}
