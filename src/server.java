import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

class HeaderException extends Exception{
    public HeaderException(String message){
        super(message);
    }
}
class RequestException extends Exception{
    public RequestException(String message){
        super(message);
    }
}
//
//class HttpRequestFormatException extends Exception {
//    public HttpRequestFormatException(String message) {
//        super(message);
//    }
//}
//server used for base for httpfs
public class server{

    private HttpRequestHandler requestHandler;
    private boolean debug;
    private int port;
    public static final String HTTP_VERSION = "HTTP/1.0";

    public server(int portNumber, HttpRequestHandler requestHandler, boolean debug) {
        this.port = portNumber;
        this.requestHandler = requestHandler;
        this.debug = debug;
    }

    //todo
    public void run() throws IOException {
        System.out.println("Server starting");
        ServerSocket serverSocket = new ServerSocket(port);
        if (debug) System.out.println("Server is running ");

        while(true){
            System.out.println("1");
//            try (Socket socket = serverSocket.accept();
//                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            try{
                Socket socket = serverSocket.accept();
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

//            try{
//                Socket socket = serverSocket.accept();
//                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
                RequestLibrary http_request = null;
                ResponseLibrary http_response = null;
//                System.out.println("Serversocket accepted!");
//                System.out.println("httprequest: " + http_request);
//                System.out.println("httpresponse: " + http_response);
                if (debug == true){
                    System.out.println("Server contacted by " + socket.getInetAddress());
                }

                try{
//                    new RequestLibrary(method, requestURI, httpVersion, entityBody, content_length);
//                    System.out.println("Extracting request for http request");
                    System.out.println(":Server");
                    http_request = extractRequest(in);
//if(debug) System.out.println("Request: " + http_request);
                    //todo
                    //do extract and enter the 6 vaariables
                    System.out.println("5: Run()");
                    System.out.println("httprequest is now: " + http_request);
                    System.out.println("----------");
                    http_response = requestHandler.handleRequest(http_request);
                    System.out.println("6: got http_response sends back to client " + http_response);

//                    System.out.println("server: " + http_request + "end server");
//                    http_response = http_request.getMethod(),http_request.getRequest(),http_request.getHttp_version(),http_request.getBody(),http_request.getContent_length()
                }catch (HeaderException e){
                    http_response = error_response(ResponseLibrary.status_500, e.getMessage());
                }catch(RequestException e){
                    http_response = error_response(ResponseLibrary.status_400, e.getMessage());
                }
                // need to seperate erros to get a secodn error for400 bad request
                System.out.println("httpresponse to string " + http_response.toString() + "end");
                String response_body = http_response.toString();

//                    out.print(http_response.toString());

                out.print(response_body);
                out.flush();


            } catch (IOException ioe) {
                //cant send 400 0r 500 error
            }
        }

    }

    //    private RequestLibrary extractRequest(BufferedReader in) throws HttpRequestFormatException, HeaderIOException{
    private RequestLibrary extractRequest(BufferedReader in) throws HeaderException, RequestException{
        ArrayList<String> headerLines;
//        System.out.println("File HttpServer: Entered here for extract request");
        System.out.println("3: extract Request");
        try{
//            System.out.println("getting Header: ");
            boolean justReadCRLF = false;
            boolean cr = false;
            int fromServer;
            char currentChar;
            StringBuilder sb = new StringBuilder();
            ArrayList<String> header =new ArrayList<>();

            while((fromServer = in.read()) != -1){
                currentChar = (char)fromServer;

                if(cr && currentChar == '\n'){
                    //CRLF
                    header.add(sb.toString());
                    sb =new StringBuilder();
                    if(justReadCRLF){
                        break;
                    }
                    cr = false;
                    justReadCRLF = true;
                }else if(currentChar == '\r'){
                    if(cr){
                        justReadCRLF = false; // two cr'sbreaks crlf crl;f sequence
                    }else cr = true;
                }else{
                    cr=false;
                    justReadCRLF = false;
                    sb.append(currentChar);
                }
            }
            headerLines = header;
            System.out.println("HeaderLines: " + headerLines);
//            headerLines = getHeader(in);
        }catch(IOException e){
            System.out.println("Could not extract HTTP header");
            throw new HeaderException("Request Header is ill-formed\n");
        }
        // parse header lines
        if(headerLines.isEmpty()) throw new HeaderException("Request Header is ill-formed\n");


        String[]requestLineArgs = headerLines.get(0).split("\\s+");
        String[]agenttest1 = headerLines.get(1).split("\\s+");
//        System.out.println(agenttest1[0] + " ----- " + agenttest1[1]);

//        System.out.println("testing headerLines: " + headerLines);
//        System.out.println("0: "+headerLines.get(0));
//        System.out.println("1: "+headerLines.get(1));
//        if(userAgentIsSet){
//            String[]agenttest = headerLines.get(1).split("\\s+");
//            System.out.println("Split agent line: " + agenttest[0] + " :split: " + agenttest[1]);
//            user_agent = agenttest[1];
//            System.out.println("Updated user-agent in true: " + user_agent);
//        }
//        System.out.println(user_agent);

        if(requestLineArgs.length != 3){
            throw new RequestException("request ill formed (three header objects required): " + headerLines.get(0) + "\n");
        }

        String method = requestLineArgs[0];
        String requestURI = requestLineArgs[1];
        String httpVersion = requestLineArgs[2];
        String user_agent = agenttest1[1];
        System.out.println("--------------");
        System.out.println("Method: " + method);
        System.out.println("URI: " + requestURI);
        System.out.println("HTTP: " + httpVersion);
        System.out.println("User Agent: " + user_agent);
        System.out.println("--------------");

        //todo
        //replace with sout
        if(!httpVersion.equalsIgnoreCase(HTTP_VERSION)){
            System.out.println("extractRequest: HTTP VERSION ERROR");
            throw new RequestException("Unsupported version: " + httpVersion);
        }
        if(!requestURI.matches("^/.*")){
            System.out.println("extractRequest: URI PATH ERROR");
            throw new RequestException("Wrong format for URI path: " + requestURI);
        }

        if(!method.equalsIgnoreCase(RequestLibrary.GET) && !method.equalsIgnoreCase(RequestLibrary.POST)){
            System.out.println("extractRequest: METHOD ERROR");
            throw new RequestException("wrong mehtod: " + method);
        }

        //parsing header lines
        boolean contentLengthisSet = false;
        boolean userAgentIsSet = false;
        int content_length = 0;


        //todo
        //look into and makew better
//        String user_agent = "";
        for (String line : headerLines) {
//            System.out.println("LINE:" + line);
//            System.out.println("line trimmed:" + line.trim().toLowerCase());
            line = line.trim().toLowerCase();
            if (line.matches("(content\\-length: )(.*)")) {
                content_length = Integer.parseInt(line.split("\\s")[1]);
                contentLengthisSet = true;
                System.out.println("Header content length: " + content_length);
            }
            else if (line.matches("(user\\-agent: )(.*)")) {
//                System.out.println("USER AGENT MATCH");
                userAgentIsSet = true;
//                String[]agenttest = line.split("\\s+");
//                System.out.println("Split agent line: " + agenttest[0] + " :split: " + agenttest[1]);
//                user_agent = agenttest[1];

            }
        }
        System.out.println("testing headerLines: " + headerLines);
        System.out.println("0: "+headerLines.get(0));
        System.out.println("1: "+headerLines.get(1));

//        if(userAgentIsSet){
//            String[]agenttest = headerLines.get(1).split("\\s+");
//            System.out.println("Split agent line: " + agenttest[0] + " :split: " + agenttest[1]);
//            user_agent = agenttest[1];
//            System.out.println("Updated user-agent in true: " + user_agent);
//        }
        if(!contentLengthisSet && method.equalsIgnoreCase(RequestLibrary.POST)){
            throw new RequestException("No content length for POST");
        }


        String entityBody = null;
        if (content_length > 0) {
            System.out.println("Content length greater than 0: go for body");
            try {
//                entityBody = getBody(in, content_length);
                int currentChar;
                int byteCount = 0;
                StringBuilder sb = new StringBuilder();

                while (byteCount < content_length && (currentChar = in.read()) != -1) {

                    // read() gives an int from 0 to 65535
                    // We need to know whether the character is a 1-byte, or 2-byte character
                    byteCount += (currentChar > 127)? 2 : 1;
                    sb.append((char)currentChar);
                }

                entityBody =  sb.toString();
            } catch (IOException e) {
                throw new HeaderException("Problem reading the entity body.\n");
            }
        }
        System.out.println("4");
        String content_type = "texttest";
        System.out.println("----------------------");
        return new RequestLibrary(method, requestURI, httpVersion, entityBody, user_agent, content_length, content_type);
    }


//    private String getBody(BufferedReader in, int size) throws  IOException{
//
//        int currentChar;
//        int byteCount = 0;
//        StringBuilder sb = new StringBuilder();
//
//        while (byteCount < size && (currentChar = in.read()) != -1) {
//
//            // read() gives an int from 0 to 65535
//            // We need to know whether the character is a 1-byte, or 2-byte character
//            byteCount += (currentChar > 127)? 2 : 1;
//            sb.append((char)currentChar);
//        }
//
//        return sb.toString();
//    }

//    private ArrayList<String> getHeader(BufferedReader in) throws IOException {
//
//        ArrayList<String> header = new ArrayList<>();
//        boolean cr = false;
//        boolean justReadCRLF = false;
//        int fromServer;
//        char currentChar;
//        StringBuilder sb = new StringBuilder();
//
//        //add the header lines, separated by CRLF
//        while ((fromServer = in.read()) != -1) {
//
//            currentChar = (char)fromServer;
//
//            if (cr && currentChar == '\n') { // crlf
//                header.add(sb.toString());
//                sb = new StringBuilder();
//
//                if (justReadCRLF) { //two crlf in a row = end of header lines
//                    break;
//                }
//                cr = false;
//                justReadCRLF = true;
//            }
//            else if (currentChar == '\r') {
//                if (cr) {
//                    justReadCRLF = false; // two cr's in a row breaks a possible crlfcrlf sequence
//                }
//                else cr = true;
//            }
//            else {
//                cr = false;
//                justReadCRLF = false;
//                sb.append(currentChar);
//            }
//        }
//
//        return header;
//    }

    public static ResponseLibrary error_response(String status, String message){
        int content_length = message.getBytes().length;
        DateTimeFormatter date = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss O");
//        ResponseLibrary test = new ResponseLibrary();
        return new ResponseLibrary(HTTP_VERSION, status, date.format(ZonedDateTime.now()),"null", content_length, message,"null" );
    }
}

//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.PrintWriter;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.net.InetAddress;
//
//public class server {
//
//    //First test for Client Request and Server Response with sockets
//    public static void main(String[] args)throws Exception {
//
//        System.out.println("Server is started");
////        ServerSocket ss = new ServerSocket(5000);
//        ServerSocket ss = new ServerSocket(80);
//        System.out.println("Server waiting for client request");
//        Socket s = ss.accept();
//        System.out.println("Client connected");
//        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
//
//        String str = br.readLine();
//        System.out.print("Data: " + str);
//
//
//
//    }
//
//}