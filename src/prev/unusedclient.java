//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Scanner;
//
//public class client {
//    static String GET_URL = "http://httpbin.org/get?course=networking&assignment=1"; //dummy test url for GET
//    static String POST_URL = "http://httpbin.org/post";//dummy test url for POST
//    final static Integer PORT = 80; //always 80 for HTTP requests
//
//    public static void main(String[] args) throws Exception {
//        /*
//         * First test for Client Request and Server Response with sockets
//         * String ip = "localhost"; int port = 5000; Socket s = new Socket(ip,port);
//         *
//         * String str = "Hello World!";
//         *
//         * OutputSreamWriter os = new OutputStreamWriter(s.getOutputStream());
//         * PrintWriter out = new PrintWriter(os); os.write(str); os.flush(); os.close();
//         */
//
//        Map<String, String> responseMap;
//        System.out.print("$ httpc ");
//        Scanner scanner = new Scanner(System.in);
//        String command = scanner.nextLine();
//
//        if(command.contentEquals("help")) {
//            System.out.println("httpc is a curl-like application but supports HTTP protocol only.");
//            System.out.println("Usage:");
//            System.out.println("       httpc command [arguments]");
//            System.out.println("The commands are:");
//            System.out.println("       get   executes a HTTP GET request and prints the response."
//                    + "\n       post   executes a HTTP POST request and prints the response."
//                    + "\n       help   prints this screen");
//            System.out.println("Use \"httpc help [command]\" for more information about a command.");
//            System.out.println();
//
//        } else if(command.contentEquals("help get")){
//            System.out.println("Usage:");
//            System.out.println("       httpc get [-v] [-h key:value] URL");
//            System.out.println("Get executes a HTTP GET request for a given URL.");
//            System.out.println("       get   executes a HTTP GET request and prints the response."
//                    + "\n       -v Prints the detail of the response such as protocol, status, and headers."
//                    + "\n       -h key:value Associates headers to HTTP Request with the format 'key:value'");
//            System.out.println();
//
//        } else if(command.contentEquals("help post")) {
//            System.out.println("Usage:");
//            System.out.println("       httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL");
//            System.out.println("Post executes a HTTP POST request for a given URL with inline data or from file.");
//            System.out.println("       get   executes a HTTP GET request and prints the response."
//                    + "\n       -v Prints the detail of the response such as protocol, status, and headers."
//                    + "\n       -h key:value Associates headers to HTTP Request with the format 'key:value'"
//                    + "\n       -d stringAssociates an inline data to the body HTTP POST request."
//                    + "\n       -f fileAssociates the content of a file to the body HTTP POST request.");
//            System.out.println();
//
//        }
//        else if((command.contains("get")) && (command.contains("-v")) && (command.contains(GET_URL))) {
//            try {
//                responseMap = sendGetRequest(GET_URL);
//                System.out.println("\n" + responseMap.get("header"));
//                System.out.println("\n" + responseMap.get("body"));
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        else if((command.contains("get")) && (command.contains("-h")) && (command.contains(GET_URL))) {
//            try {
//                responseMap = sendGetRequest(GET_URL);
//                System.out.println("\n" + responseMap.get("header"));
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        else if((command.contains("post")) && (command.contains("-v")) && (command.contains(POST_URL))) {
//            try {
//                responseMap = sendGetRequest(POST_URL);
//                System.out.println("\n" + responseMap.get("header"));
//                System.out.println("\n" + responseMap.get("body"));
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        else if((command.contains("post")) && (command.contains("-h")) && (command.contains(POST_URL))) {
//            try {
//                responseMap = sendGetRequest(POST_URL);
//                System.out.println("\n" + responseMap.get("header"));
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        else if((command.contains("post")) && (command.contains("-d")) && (command.contains(POST_URL))) {
//            try {
//
//                responseMap = sendGetRequest(GET_URL);
//                System.out.println("\n" + responseMap.get("header"));
//            }
//            catch (Exception e){
//
//                e.printStackTrace();
//            }
//        }
//        else {
//            System.out.println("Missing or error HTTP protocol.");
//            System.out.println("httpc (get|post) [-v] (-h \"k:v\")* [-d inline-data] [-f file] URL");
//        }
//    }
//
//
//    private static Map<String, String> sendPostRequest(String url, String data) throws Exception {
//
//        System.out.println("POST request");
//
//        Map<String, String> responseMap = new HashMap<>();
//        URL urlObject = new URL(url);
//        System.out.println("Requestion Connection");
//        Socket socket = new Socket(InetAddress.getByName(urlObject.getHost()), PORT);
//        System.out.println("Connected");
//        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
//        printWriter.println("POST /" + urlObject.getFile() + " HTTP/1.0");
//        printWriter.println("Host: " + urlObject.getHost());
//        printWriter.println("Content-Length: " + data.length());
//        printWriter.println();
//        printWriter.println(data);
//        printWriter.println();
//        printWriter.flush();
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        String str;
//        StringBuilder response = new StringBuilder();
//        boolean header = false;
//
//        System.out.println("Waiting for response");
//        while ((str = bufferedReader.readLine()) != null)
//        {
//            response.append(str + "\n");
//            if (str.isEmpty() && !header)
//            {
//                responseMap.put("header", response.toString()); //parsing header
//                header = true;
//                response = new StringBuilder();
//            }
//        }
//        bufferedReader.close();
//        printWriter.close();
//        socket.close();
//        System.out.println("Socket closed\n");
//
//        responseMap.put("body", response.toString());
//        return responseMap;
//
//    }
//
//    //added comments on GET but the same comments apply for POST above
//    private static Map<String, String> sendGetRequest(String url)throws Exception{
//
//        System.out.println("GET request");
//
//        Map<String,String> responseMap = new HashMap<>();//mapping sting elements header and body
//        URL urlObject = new URL(url); //passing url
//        System.out.println("Connection Request"); //establishing connection
//        Socket socket = new Socket(InetAddress.getByName(urlObject.getHost()),PORT); //(IP,PORT)
//        System.out.println("Connected");
//        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());//sending request
//        printWriter.println("GET /" + urlObject.getFile() + " HTTP/1.0"); //getting the name of the URL
//        printWriter.println("Host: " + urlObject.getHost()); //getting the host
//        printWriter.println(""); //\n\r
//        printWriter.flush(); //making sure all data is out
//
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); //reading response from "server socket"
//        String str;
//        StringBuilder response = new StringBuilder(); //mutable sequence of chars
//
//
//        boolean header = false;
//        System.out.println("Waiting for response"); //check if everything is okay until here if this prints
//        while ((str = bufferedReader.readLine()) != null) //reading response
//        {
//            response.append(str + "\n");  //storing data
//            if (str.isEmpty() && !header) //looking for the header
//            {
//                responseMap.put("header", response.toString()); //parsing header
//                header = true;
//                response = new StringBuilder(); //update response
//            }
//        }
//        bufferedReader.close();
//        printWriter.close();
//        socket.close();
//        System.out.println("Socket closed\n");//check if everything is fine until now if this prints
//
//        responseMap.put("body", response.toString()); //body
//        return responseMap;
//
//
//    }
//
//
//
//}
//