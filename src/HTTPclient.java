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

    public static void GET_METHOD(String host, String path){
        try {
//            Creating the Socket to connect
//            using the host called and default port: 80
            Socket socket = new Socket(host, 80);
//            opening printwriter
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            String file_name = null;
            //request being created
            String cmd_request = "";
            if (path != "" || path != null) {
                cmd_request = "GET " + path + " HTTP/1.0";
            } else {
                cmd_request = "GET / HTTP/1.0\r\nHost: " + host + "\r\n\r\n";
            }
            pw.println(cmd_request);

            //Output file request
            if(path.contains("-out")){
                System.out.println("Outputfile called");
                file_name = path.substring(path.indexOf("-out")+3);
                path = path.substring(0, path.indexOf("-out"));
            }

            if (string_header != "") {
                String[] header_array = string_header.split(" ");
                for (int i = 0; i < header_array.length; i++) {
                    pw.println(header_array[i]);
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

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String string_output;
            String response = "";

            while ((string_output = br.readLine()) != null) {
                response += string_output + "\n";
            }
//            formatting the output
                if (cmd_verbose == true) {
                    System.out.println(response);
                } else {
                    String[] responseFormatted = response.split("\n\n");

                    for (int i = 1; i < responseFormatted.length; i++)
                        System.out.println(responseFormatted[i]);
                }


            if(file_name != null){
                try {
                    PrintWriter extWriter = new PrintWriter(file_name);
                    System.out.println("response:" +response);
                    extWriter.write(response);
                    extWriter.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }

            br.close();
            pw.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Displays the help menu
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
        Console console = System.console();
        if (console == null) {
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
        while (patternCheck != true) {
            System.out.println("Command Line Menu");
            System.out.println("Enter \"1\" to open help options");
            System.out.println("Enter \"2\" to open the httpc functions");
            String option = console.readLine();
//            Menu functions
            if (option.equals("1")) {
                System.out.println("Enter \"help\" for default help");
                System.out.println("Enter \"get\" for get help");
                System.out.println("Enter \"post\" for post help");
                System.out.println("Enter anything else to return to the main menu");
                String cmd_help_option = console.readLine();

                if(cmd_help_option.equals("help") || cmd_help_option.equals("get") || cmd_help_option.equals("post")) {
                    System.out.println("You have entered: " + cmd_help_option);
                    cmd_help(cmd_help_option);
                    System.out.println("Main Menu (Enter any key)");
                    console.readLine();
                }
                else {
                    continue;
                }
            } else if(option.equals("2")) {
                value = console.readLine("Enter HTTP function (Enter \"exit\" to return to Command Line Menu): ");
                //Exit if the value entered is 0
                if (value.equals("0")) {
                    continue;
                }
                //Regex pattern; separate entities grouped within parenthesis
                Pattern http_pattern = Pattern.compile("httpc(\\s+(get|post))((\\s+-v)?(\\s+-h\\s+([^\\s]+))?(\\s+-d\\s+('.+'))?(\\s+-f\\s+([^\\s]+))?)(\\s+'((http[s]?:\\/\\/www\\.|http[s]?:\\/\\/|www\\.)?([^\\/]+)(\\/.+)?)'*)");
                Matcher matcher = http_pattern.matcher(value);
                if (matcher.find()) {
                    patternCheck = true;
                    //setting post/get to uppercase: GET/POST
                    String match_type = matcher.group(2);
                    match_type = match_type.toUpperCase();
                    //Trim the host
                    String host = matcher.group(14).replaceAll("'", "");
                    host = host.trim();
                    //Assign the path if not empty
                    String path = "";
                    if (matcher.group(15) != null) {
                        path = matcher.group(15).replaceAll("'", "");
                        path = path.trim();
                    }
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

                    //Additional check GET method for cURL
                    if (match_type.equals(HTTP_GET) && (cmd_data || cmd_file)) {
                        patternCheck = false;
                        System.out.println("-f and -d cannot be combined in GET!");
                        continue;
                    }
                    //Additional check on POST method for cURL
                    if (match_type.equals(HTTP_POST) && cmd_data && cmd_file) {
                        patternCheck = false;
                        System.out.println("-f and -d cannot be combined in POST!");
                        continue;
                    }
                    System.out.println("User input has been processed, sending info to httpc");
                    httpc(path, host, match_type, null, cmd_data, cmd_file, cmd_verbose, file_name);
                } else {
                    System.out.println("Invalid Input!");
                }
            }
            else  {
                System.out.println("System exiting...");
                System.exit(0);
            }
        }
    }


    //Needed: getting body in json for -d or default
    public static void HTTP_METHOD(String host, String path, File file, boolean data) {
        try {
            //Initialize the socket
            Socket socket = new Socket(host, 80);
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            String body = "";
            String request = "";

            //If -h
            if (string_header != "") {
                String[] headersArray = string_header.split(" ");
                for (int i = 0; i < headersArray.length; i++) {
                    writer.println(headersArray[i]);
                }

                //Modify the string if necessary
                for (String header: headersArray) {
                    if (header.contains("=")) {
                        writer.println(header.split("=")[0] + ":" + header.split("=")[1]);
                    }
                }
            }

            //If -d
            if(data){
                System.out.println(data_string);
                System.out.println(data_string.substring(1, data_string.length() - 1));
                request = "POST /post?info=info HTTP/1.0\r\n"
                        + "Content-Type:application/json\r\n"
                        + "Content-Length: " + data_string.length() +"\r\n"
                        + "\r\n"
                        + data_string.substring(1, data_string.length() - 1);
            }

            //If -f
            else if (file != null) {

                BufferedReader in = new BufferedReader(new FileReader(file));
                String line = "";
                StringBuilder StringBuilder = new StringBuilder();

                while ((line = in .readLine()) != null) {
                    String formattedLine = line.replaceAll("[\\{\\}]", "").replaceAll("\\s", "");

                    String[] linesArray = formattedLine.split(",");
                    for (int i = 0; i < linesArray.length; i++) {
                        StringBuilder.append(linesArray[i]+",");
                    }
                    body = "{"+StringBuilder.toString().substring(0, StringBuilder.length() - 1)+"}";
                    request = "POST /post?info=info HTTP/1.0\r\n"
                            + "Content-Type:application/json\r\n"
                            + "Content-Length: " + body.length() +"\r\n"
                            + "\r\n"
                            + body;

                } in .close();

            }else{
                //Must refactor to get data passed in query
                body = "{"
                        + "\"DefaultAssignment\":1,"
                        + "\"DefaultCourse\": \"Networking\""
                        + "}";

                request = "POST /post?info=info HTTP/1.0\r\n"
                        + "Content-Type:application/json\r\n"
                        + "Content-Length: " + body.length() +"\r\n"
                        + "\r\n"
                        + body;
            }

            outputStream.write(request.getBytes());
            outputStream.flush();


            writer.println("");
            writer.flush();

            BufferedReader bufRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String outStr;
            String response = "";

            while ((outStr = bufRead.readLine()) != null) {
                response += outStr + "\n";
            }

            //Format output as needed
            if (cmd_verbose == true) {
                System.out.println(response);
            } else {
                String[] responseFormatted = response.split("\n\n");

                for (int i = 1; i < responseFormatted.length; i++)
                    System.out.println(responseFormatted[i]);
            }

            //Close everything
            bufRead.close();
            writer.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void httpc(String path, String host, String type, String query, boolean isData, boolean isFile, boolean isVerbose, File file) {
        try {
            if (host == null || host.equals("")) {
                host = "duckduckgo.com";
            }

            //https://stackoverflow.com/questions/2214308/add-header-in-http-request-in-java
            if (type.equals("GET")) {
                GET_METHOD(host, path);
            } else if (type.equals("POST")) {
                HTTP_METHOD(host, path, file, isData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static Map < String, String > getQueryMap(String query) {
//        String[] params = query.split("&");
//        Map < String, String > map = new HashMap < String, String > ();
//        for (String param: params) {
//            String[] p = param.split("=");
//            String name = p[0];
//            if (p.length > 1) {
//                String value = p[1];
//                map.put(name, value);
//            }
//        }
//        return map;
//    }


}