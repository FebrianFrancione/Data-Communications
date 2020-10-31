
import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


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

//server used for base for httpfs
public class server{

    private req_handler requestHandler;
    private boolean debug;
    private int port;
    public static final String HTTP_VERSION = "HTTP/1.0";

    public server(int portNumber, req_handler requestHandler, boolean debug) {
        this.port = portNumber;
        this.requestHandler = requestHandler;
        this.debug = debug;
    }

    //todo
    public void run_server() throws IOException {
        System.out.println("Server starting");
        ServerSocket serverSocket = new ServerSocket(port);
        if (debug) System.out.println("Server is running ");

        while(true){
            System.out.println("1");
            try{
                Socket socket = serverSocket.accept();
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                RequestLibrary http_request = null;
                ResponseLibrary http_response = null;

                if (debug == true){
                    System.out.println("Server contacted by " + socket.getInetAddress());
                }

                try{

                    System.out.println(":Server");
                    http_request = extractRequest(in);
                    System.out.println("5: Run()");
                    System.out.println("httprequest is now: " + http_request);
                    System.out.println("----------");
                    http_response = requestHandler.request_handler(http_request);
                    System.out.println("6: got http_response sends back to client " + http_response);

                }catch (HeaderException e){
                    http_response = error_response(ResponseLibrary.status_500, e.getMessage(), http_request.getUser_agent());
                }catch(RequestException e){
                    http_response = error_response(ResponseLibrary.status_400, e.getMessage(), http_request.getUser_agent());
                }

                System.out.println("http response to string " + http_response.toString() + "end");
                String response_body = http_response.toString();


                out.print(response_body);
                out.flush();


            } catch (IOException ioe) {
            }
        }

    }

    //    private RequestLibrary extractRequest(BufferedReader in) throws HttpRequestFormatException, HeaderIOException{
    private RequestLibrary extractRequest(BufferedReader in) throws HeaderException, RequestException{
        ArrayList<String> headers;

        System.out.println("3: extract Request");
        try{

            boolean carriage_line_return = false;
            boolean carriage_line = false;
            int server_int;
            char current_char;
            StringBuilder sb = new StringBuilder();
            ArrayList<String> header =new ArrayList<>();

            while((server_int = in.read()) != -1){
                current_char = (char)server_int;

                if(carriage_line && current_char == '\n'){
                    header.add(sb.toString());
                    sb =new StringBuilder();
                    if(carriage_line_return){
                        break;
                    }
                    carriage_line_return = true;
                    carriage_line = false;

                }else if(current_char == '\r'){
                    if(carriage_line){
                        carriage_line_return = false; // two cr'sbreaks crlf crl;f sequence
                    }else carriage_line = true;
                }else{
                    carriage_line_return = false;
                    carriage_line=false;
         //appending char to strinbuilder
                    sb.append(current_char);
                }
            }
            headers = header;
            System.out.println("headers: " + headers);
//            headerLines = getHeader(in);
        }catch(IOException e){
            System.out.println("Could not extract HTTP header");
            throw new HeaderException("Request Header is not correct");
        }
        // parse header lines
        if(headers.isEmpty()) throw new HeaderException("Request Header is ill-formed\n");


        String[]requestLineArgs = headers.get(0).split("\\s+");
        String[]agenttest1 = headers.get(1).split("\\s+");
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
            throw new RequestException("rHeaders must have 3 components " + headers.get(0));
        }

        String method = requestLineArgs[0];
        String requestURI = requestLineArgs[1];
        String httpVersion = requestLineArgs[2];

//        String user_agent = agenttest1[1];
        System.out.println("--------------");
        System.out.println("Method: " + method);
        System.out.println("URI: " + requestURI);
        System.out.println("HTTP: " + httpVersion);
//        System.out.println("User Agent: " + user_agent);
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
        for (String line : headers) {
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
        String user_agent = "";
        if(userAgentIsSet) {
            user_agent = agenttest1[1];
            System.out.println("User Agent: " + user_agent);
        }
        System.out.println("testing headerLines: " + headers);
        System.out.println("0: "+headers.get(0));
        System.out.println("1: "+headers.get(1));

//        if(!contentLengthisSet && method.equalsIgnoreCase(RequestLibrary.POST)){
//        if(!contentLengthisSet && method.equalsIgnoreCase(RequestLibrary.POST)){
//            throw new RequestException("No content length for POST");
//        }


        String body = null;
        if (content_length > 0) {
            System.out.println("Content length greater than 0: go for body");
            try {
                int bytes = 0;
                int current_char;

                StringBuilder sb = new StringBuilder();

                while (bytes < content_length && (current_char = in.read()) != -1) {
                    if(current_char > 127){
                        bytes += 2;
                    }else{
                        bytes += 1;
                    }
                    sb.append((char)current_char);
                }

                body =  sb.toString();
            } catch (IOException e) {
                throw new HeaderException("Body could not be read");
            }
        }
        System.out.println("4");
        String content_type = "";
        System.out.println("user agent: " + user_agent);
        System.out.println("----------------------");
        return new RequestLibrary(method, requestURI, httpVersion, body, user_agent, content_length, content_type);
    }
    
    private void getContentType(String file, PrintWriter writer) throws Exception {
		String extension = "";
		int dot = file.lastIndexOf('.');
		if (dot > 0) 
		{
		    extension = file.substring(dot + 1);
		}
		if (extension.equals("txt"))
		{
			String keyVal[] = new String[2];
	        for (int i = 0; i < keyVal.length; i++)
	        {
	        	if(i == 1)
	        	{
	        		keyVal[i] = "Content-Type:text";     		
	        	}
	        	if(i == 2)
	        	{
	        		keyVal[i] = "Content-Disposition: attachment; filename = " + file;     		
	        	}
	        	writer.print(keyVal[i]);     	
	        }
		}
        writer.flush();
        writer.close();
	}

    public static ResponseLibrary error_response(String status, String message, String user_agent){
        int content_length = 0;
//        int content_length = message.getBytes().length;
        DateTimeFormatter date = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss O");
//        ResponseLibrary test = new ResponseLibrary();

        return new ResponseLibrary(HTTP_VERSION, status, date.format(ZonedDateTime.now()),user_agent, content_length, null,"null" );
    }
}
