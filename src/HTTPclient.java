import java.net.Socket;
import java.io.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;



class Httpclient {

    private static File file_name;
    private final static String HTTP_GET = "GET";
    private final static String HTTP_POST = "POST";

//    initializing all cmd additional commands to false, which will be modified when called.
    private static boolean patternCheck, cmd_verbose,cmd_data,cmd_file,cmd_header=false;

//    initializing strings data and header:
    private static String data_string, string_header = "";

//GET method
    public static void GET_METHOD(String host, String path){
        try {
            //initializing string output and response

            String string_output;
            StringBuilder response = new StringBuilder();

//            Creating the Socket to connect
//            using the host called and default port: 80
            Socket socket = new Socket(host, 80);
//            opening printwriter
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            //opening buffered reader
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String file_name = null;
            //request being created
            String cmd_request;
//            if (path.equals("") || path == null) {
//            simplified to:
            if (path.equals("")) {
                cmd_request = "GET / HTTP/1.0\r\nHost: " + host + "\r\n\r\n";

            } else {
                cmd_request = "GET " + path + " HTTP/1.0";
            }

            System.out.println("printing command request");
            pw.println(cmd_request);

            //Output file request
            if(path.contains("-out")){
                System.out.println("Outputfile called");
                file_name = path.substring(path.indexOf("-out")+3);
                path = path.substring(0, path.indexOf("-out"));
            }

//            if (string_header != "") {
//            converted to simplified version
            if (!string_header.equals("")) {
                String[] header_array = string_header.split(" ");

//                for (int i = 0; i < header_array.length; i++) {
//                    pw.println(header_array[i]);
//                }
//replaced with enhanced for loop.
                for (String s : header_array) {
                    pw.println(s);
                }

//String modifications
                for (String header: header_array) {
                    if (header.contains("=")) {
                        pw.println(header.split("=")[0] + ":" + header.split("=")[1]);
                    }
                }
            }
            pw.println("");
//            flushing data
            pw.flush();

            while ((string_output = br.readLine()) != null) {
//                response += string_output + "\n";
//                converted to append rather than +=
                response.append(string_output).append("\n");
            }
//            formatting the output
                if (cmd_verbose) {
                    System.out.println(response);
                } else {
                    String[] formatter_response = response.toString().split("\n\n");
                    for (int i = 1; i < formatter_response.length; i++)
                        System.out.println(formatter_response[i]);
                }
//                checking if file name is not null, writing response to printwriter
            if(file_name != null){
                try {
                    PrintWriter pw1 = new PrintWriter(file_name);
//                    printing response
                    System.out.println("response:" +response);
                    pw1.write(response.toString());
                    pw1.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
//closing buffered reader and printwriter instances
            br.close();
            pw.close();
//            closing socket instance
            System.out.println("Closing socket!");
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    POST method
    public static void POST_METHOD(String host, String path, File file, boolean data) {
        try {
            //Initialize the socket, port 80
            Socket socket = new Socket(host, 80);
            PrintWriter pw2 = new PrintWriter(socket.getOutputStream());
            OutputStream outputStream = socket.getOutputStream();
            String body;
            String request = "";

            //cmd -h header is called
            if (string_header != "") {

                String[] headersArray = string_header.split(" ");
                for (int i = 0; i < headersArray.length; i++) {
                    pw2.println(headersArray[i]);
                }
                //Modify the string if necessary
                for (String header: headersArray) {
                    if (header.contains("=")) {
                        pw2.println(header.split("=")[0] + ":" + header.split("=")[1]);
                    }
                }
            }

            //cmd -d data is called
            if(data){
                System.out.println("printing data string" + data_string);
                System.out.println("printing data string substrings: ");
                System.out.println(data_string.substring(1, data_string.length() - 1));
//                request being filled with data string length and substring to be used.
                request = "POST /post?info=info HTTP/1.0\r\n"
                        + "Content-Type:application/json\r\n"
                        + "Content-Length: " + data_string.length() +"\r\n"
                        + "\r\n"
                        + data_string.substring(1, data_string.length() - 1);
            }

            //cmd -f file is called
            else if (file != null) {

                BufferedReader br1 = new BufferedReader(new FileReader(file));
//                shortened redundant String line = ""
                String line;
//                creating a new string builder instance
                StringBuilder string_builder = new StringBuilder();

                while ((line = br1.readLine()) != null) {
                    String formattedLine = line.replaceAll("[\\{\\}]", "").replaceAll("\\s", "");

                    String[] linesArray = formattedLine.split(",");
                    for (int i = 0; i < linesArray.length; i++) {
                        string_builder.append(linesArray[i]+",");
                    }
                    body = "{"+string_builder.toString().substring(0, string_builder.length() - 1)+"}";
                    request = "POST /post?info=info HTTP/1.0\r\n"
                            + "Content-Type:application/json\r\n"
                            + "Content-Length: " + body.length() +"\r\n"
                            + "\r\n"
                            + body;

                }
//                closing buffered reader
                br1.close();

            }else{
//                body being formatted to be put into query
                body = "{"
                        + "\"DefaultAssignment\":1,"
                        + "\"DefaultCourse\": \"Networking\""
                        + "}";
//request being formatted to be passed to query
                request = "POST /post?info=info HTTP/1.0\r\n"
                        + "Content-Type:application/json\r\n"
                        + "Content-Length: " + body.length() +"\r\n"
                        + "\r\n"
                        + body;
            }
//writing and flushing request bytes to be used
            outputStream.write(request.getBytes());
            outputStream.flush();
//            flushing empty string
            pw2.println("");
            pw2.flush();
//opening another buffered reader instance
            BufferedReader bufRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String outStr;
            String response = "";
            while ((outStr = bufRead.readLine()) != null) {
                response += outStr + "\n";
            }

            //Formatting output
            if (cmd_verbose == true) {
                System.out.println(response);
            } else {
                String[] responseFormatted = response.split("\n\n");
                for (int i = 1; i < responseFormatted.length; i++)
                    System.out.println(responseFormatted[i]);
            }

            //Close the instances of buffered reader, printwriter and socket.
            bufRead.close();
            pw2.close();
            System.out.println("Socket closed!");
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //Displays the help menu, called in command line
    public static void cmd_help(String cmd_help_option) {
        if (cmd_help_option.equals("get")) {
            System.out.println(
                    "Usage: httpc get [-v] [-h key:value] URL\n"
                            + "\r\n"
                            + "Get executes a HTTP GET request for a given URL"
                            + "\r\n"
                            + "\t -v		    Prints the detail of the response such as protocol, status and headers.\n"
                            + "\t -h key:value	Associates headers to HTTP Request with the format 'key:value'.\n"
            );
        } else if (cmd_help_option.equals("post")) {
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
//            help option called to "help" is useless but enters else section
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

    public static void main(String[] args) {
        String value;
        Console command_console = System.console();
        if (command_console == null) {
            System.out.println("Enter your terminal to be able to access the command line");
            System.out.println("Basic troubleshooting: In terminal, enter the cd src directory, then enter \" Javac HTTPclient.java \" and finally enter \" Java Httpclient\". This will allow you to access the rest of the program.");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
        System.out.println("Assignment 1: Febrian Francione 40049253, ");
        //HTTP client
        while (!patternCheck) {
            System.out.println("Command Line Menu");
            System.out.println("Enter \"1\" to open help options");
            System.out.println("Enter \"2\" to open the httpc functions");
            String option = command_console.readLine();
//            Menu functions
            if (option.equals("1")) {
                System.out.println("Enter \"help\" for default help");
                System.out.println("Enter \"get\" for get help");
                System.out.println("Enter \"post\" for post help");
                System.out.println("Enter anything else to return to the main menu");
                String cmd_help_option = command_console.readLine();

                if(cmd_help_option.equals("help") || cmd_help_option.equals("get") || cmd_help_option.equals("post")) {
                    System.out.println("You have entered: " + cmd_help_option);
//                    calling the cmd_help method to return appropriate response based off user's input
                    cmd_help(cmd_help_option);
                    System.out.println("Return to Main Menu? (Enter any key)");
                    command_console.readLine();
                }
            } else if(option.equals("2")) {
                value = command_console.readLine("Enter HTTP function (Enter \"exit\" to return to Command Line Menu): ");
                //Exit if the value entered is 0
                if (value.equals("0")) {
                    continue;
                }
                //separate entities grouped within parenthesis. Seperating with regex to be worked on in following if loops
                Pattern http_pattern = Pattern.compile("httpc(\\s+(get|post))((\\s+-v)?(\\s+-h\\s+([^\\s]+))?(\\s+-d\\s+('.+'))?(\\s+-f\\s+([^\\s]+))?)(\\s+'((http[s]?:\\/\\/www\\.|http[s]?:\\/\\/|www\\.)?([^/]+)(\\/.+)?)'*)");
                Matcher matcher = http_pattern.matcher(value);
                if (matcher.find()) {
                    patternCheck = true;
                    //setting post/get to uppercase: GET/POST
                    String match_type = matcher.group(2);
                    match_type = match_type.toUpperCase();
                    //Assign the path if not empty
                    String path = "";
                    if (matcher.group(15) != null) {
                        path = matcher.group(15).replaceAll("'", "");
                        path = path.trim();
                    }
                    //Trimming the host
                    String host = matcher.group(14).replaceAll("'", "");
                    host = host.trim();

                    //Additional check GET method for cURL
                    if ((cmd_data || cmd_file) && match_type.equals(HTTP_GET)) {
                        patternCheck = false;
                        System.out.println("Patterncheck set to false for GET");
                        System.out.println("-f and -d cannot be combined in GET!");
                        continue;
                    }
                    //Additional check on POST method for cURL
                    if (cmd_data && cmd_file && match_type.equals(HTTP_POST)) {
                        patternCheck = false;
                        System.out.println("Patterncheck set to false for POST");
                        System.out.println("-f and -d cannot be combined in POST!");
                        continue;
                    }
//                    cmd additional commands to be used with cmd
                    //cmd -v
                    if (matcher.group(4) != null){
                        cmd_verbose = true;
                    }else{
                        cmd_verbose = false;
                    }
                    //cmd-h
                    if(matcher.group(5) != null){
                        cmd_header = true;
                        string_header = matcher.group(6);
                    }else{
                        cmd_header = false;
                    }
                    //cmd -d
                    if(matcher.group(7) != null){
                        cmd_data = true;
                        data_string = matcher.group(8);
                    }else{
                        cmd_data = false;
                    }
                    //cmd -f
                    if(matcher.group(9) != null){
                        cmd_file = true;
                        file_name = new File(matcher.group(10));
                    }else{
                        cmd_file = false;
                    }
                    System.out.println("User input has been processed, sending info to httpc");
//                    calling the http client method, sending the now processed parameters.
                    HTTPC_client_method(path, host, match_type, cmd_data, file_name);
                } else {
//                    printing invalid input if command fails
                    System.out.println("Invalid Input!");
                }
            }
            else  {
                System.out.println("System exiting...");
                System.exit(0);
            }
        }
    }



    public static void HTTPC_client_method(String path, String host, String type, boolean isData, File file) {
        try {
            if (host == null || host.equals("")) {
                host = "www.httbin.org";
            }
            if (type.equals("GET")) {
                //sending httbin host to get
                GET_METHOD(host, path);
            } else if (type.equals("POST")) {
                //sending httbin host to POST
                POST_METHOD(host, path, file, isData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}