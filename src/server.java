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

interface HttpRequestHandler {
    ResponseLibrary handleRequest(RequestLibrary httpRequest);

}

//server used for base for httpfs
public class server{

    private HttpRequestHandler requestHandler;
    private boolean debug;
    private int port;
    public static final String HTTP_VERSION = "HTTP/1.0";

    //todo
    public void run() throws IOException {
        System.out.println("Server starting");
        ServerSocket serverSocket = new ServerSocket(port);
        if (debug) System.out.println("Server is running ");

        while(true){
            try (Socket socket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){

                RequestLibrary http_request = null;
                ResponseLibrary http_response = null;

                if (debug == true){
                    System.out.println("Server contacted by " + socket.getInetAddress());
                }

                try{
//                    new RequestLibrary(method, requestURI, httpVersion, entityBody, content_length);
                    http_request = extractRequest(in);
//if(debug) System.out.println("Request: " + http_request);
                    //todo
                    //do extract and enter the 6 vaariables
                    http_response = requestHandler.handleRequest(http_request);
//                    http_response = http_request.getMethod(),http_request.getRequest(),http_request.getHttp_version(),http_request.getBody(),http_request.getContent_length()
                }catch (HeaderException e){
                    http_response = error_response(ResponseLibrary.status_500, e.getMessage());
                }catch(RequestException e){
                    http_response = error_response(ResponseLibrary.status_400, e.getMessage());
                }
                // need to seperate erros to get a secodn error for400 bad request
                out.print(http_response.toString());
                out.flush();

            }
        }

    }

    //    private RequestLibrary extractRequest(BufferedReader in) throws HttpRequestFormatException, HeaderIOException{
    private RequestLibrary extractRequest(BufferedReader in) throws HeaderException, RequestException{
        ArrayList<String> headerLines;
        try{
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
//            headerLines = getHeader(in);
        }catch(IOException e){
            System.out.println("Could not extract HTTP header");
            throw new HeaderException("Request Header is ill-formed\n");
        }
        // parse header lines
        if(headerLines.isEmpty()) throw new HeaderException("Request Header is ill-formed\n");


        String[]requestLineArgs = headerLines.get(0).split("\\s+");
        if(requestLineArgs.length != 3){
            throw new RequestException("request ill formed (three header objects required): " + headerLines.get(0) + "\n");
        }

        String method = requestLineArgs[0];
        String requestURI = requestLineArgs[1];
        String httpVersion = requestLineArgs[2];

        //todo
        //replace with sout
        if(!httpVersion.equalsIgnoreCase(HTTP_VERSION)){
            throw new RequestException("Unsupported version: " + httpVersion);
        }
        if(!requestURI.matches("^/.*")){
            throw new RequestException("Wrong format for URI path: " + requestURI);
        }

        if(!method.equalsIgnoreCase(RequestLibrary.GET) && !method.equalsIgnoreCase(RequestLibrary.POST)){
            throw new RequestException("wrong mehtod: " + method);
        }

        //parsing header lines
        boolean contentLengthisSet = false;
        int content_length = 0;


        //todo
        //look into and makew better
        for (String line : headerLines) {
            if (line.trim().toLowerCase().matches("^content-length: [0-9]+$")) {
                content_length = Integer.parseInt(line.split("\\s")[1]);
                contentLengthisSet = true;
            }
            //Note: irrelevant header lines are ignored.
        }

        if(!contentLengthisSet && method.equalsIgnoreCase(RequestLibrary.POST)){
            throw new RequestException("No content length for POST");
        }

        String entityBody = null;
        if (content_length > 0) {
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
        return new RequestLibrary(method, requestURI, httpVersion, entityBody, content_length);
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
        return new ResponseLibrary(HTTP_VERSION, status, date.format(ZonedDateTime.now()), null, message, content_length);
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