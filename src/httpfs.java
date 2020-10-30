
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class httpfs implements HttpRequestHandler{
    public static final String GET= "GET";
    public static final String POST = "POST";
    public static final String HTTP_VERSION = "HTTP/1.0";

    DateTimeFormatter date =DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss");
    //    "EEE, dd MMM yyyy HH:mm:ss O"
    private String rootDir;
    //default
    public httpfs(){
        this.rootDir = System.getProperty("user.dir");
    }
    public httpfs(String rootDir){
        this.rootDir = rootDir;
    }

    public ResponseLibrary handle_request(RequestLibrary http_request){
        ResponseLibrary http_response = null;
        if (http_request.getMethod().equalsIgnoreCase(GET)){
//            request_response = GET_METHOD();
           http_response =  GET_METHOD(http_request);
            System.out.println(" after get method" + http_response);
            System.out.println("back to handlerequest");
        }else if(http_request.getMethod().equalsIgnoreCase(POST)){
//            request_response = POST_METHOD();
            //todo
//            POST_METHOD();

        }
        return http_response;
    }

    private ResponseLibrary GET_METHOD(RequestLibrary http_request){
        ResponseLibrary http_response = null;
//        String path = rootDir + http_request.getRequest();
//        File file = new File(path);
        //trying to set defAULT rootdir
        String path = rootDir + "\\src\\testFile\\" + http_request.getRequest().replace('/','\\');
        System.out.println("HTTPFS: Rootdir: " + rootDir);
//        System.out.println("Httpsrequest.getrequesturi: " + httpRequest.getRequestURI().replace('/','\\'));
        //hardcoded
        //set default direcoty to this?
//        path = rootDir+"\\src\\testFile";
//        path = rootDir+"\\src\\testFile\\hello.txt";
        System.out.println("test path: " + path);
        Path file_path = new File(path).toPath();
        try {
            String mimetype = Files.probeContentType(file_path);
            System.out.println("File type: " + mimetype);
        }catch(IOException e){
            e.printStackTrace();
        }
        File file = new File(path);
        //this causes error with file
//        String[] pathnames;
//        // Populates the array with names of files and directories
//        pathnames = file.list();
////////
//        // For each pathname in the pathnames array
//        for (String pathname : pathnames) {
//            // Print the names of files and directories
//            System.out.println(pathname);
//        }
        //////
        if(file.isDirectory()){
            System.out.println("httpfs 400");
            return server.error_response(ResponseLibrary.status_400, "Not a file!");
            //http server response
        } else if(!file.canRead()){
            System.out.println("httpfs 500");
            return server.error_response(ResponseLibrary.status_500, "Cannot read file: " + path);
        }else if(!file.isFile()) {
            System.out.println("httpfs 400: non existent");
            return server.error_response(ResponseLibrary.status_400, "Non-Existent");
        }
        // http server response
        //in case passes correctly
        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            StringBuilder sb = new StringBuilder();
            String line;
            for (line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line + '\n');
                System.out.println("httpfs sb: " + sb);
            }
            http_response = getResponse(ResponseLibrary.status_200, sb.toString());
            System.out.println("httpresponse after get respoinse : " + http_response);
        } catch (FileNotFoundException f){
            //http response
            http_response = server.error_response(ResponseLibrary.status_500, "File not found: " + path);
        }
        catch (IOException e){
            http_response = server.error_response(ResponseLibrary.status_500, "I/O file error, file was not read: " + path);
        }
        System.out.println("Reach return?");
        return http_response;
    }
//        private ResponseLibrary POST_METHOD(){
//
//        }
    private ResponseLibrary getResponse(String status, String message){
        int content_length = message.getBytes().length;
        System.out.println("getResponse");
        return new ResponseLibrary(HTTP_VERSION, status, date.format(ZonedDateTime.now()), "test", message, content_length);
    }

    @Override
    public ResponseLibrary handleRequest(RequestLibrary httpRequest) {
        ResponseLibrary httpResponse = null;
        if (httpRequest.getMethod().equalsIgnoreCase(httpRequest.GET)) {
            httpResponse = GET_METHOD(httpRequest);
        }
        else if (httpRequest.getMethod().equalsIgnoreCase(httpRequest.POST)) {
//            httpResponse = POST_METHOD(httpRequest);
        }
        return httpResponse;
    }
}

//    import java.io.*;
//    import java.net.ServerSocket;
//    import java.net.Socket;
//    import java.nio.file.FileSystems;
//    import java.nio.file.Files;
//    import java.nio.file.Path;
//    import java.nio.file.attribute.FileAttribute;
//    //
//    class Server_response{
//        //hardcoded for now, must be implemented with user http version
//        String Http_Version = "HTTP/1.0";
//        String User_Agent = "Concordia";
//        int content_length;
//        public static void response(String Http_Verion, String User_Agent, int Content_Length){
//        }
//    }
//    class status{
//        public static String Status_codes(int status_code){
//            String status;
//            switch (status_code){
//                case 404:
//                    status = "404: Not Found\n-- Server has not found anything matching the requested URI.";
//                    break;
//                case 201:
//                    status = "201: Created\n-- Server has successfully created a resource from a POST ONLY request.";
//                    break;
//                case 400:
//                    status = "400: Bad request\n-- Server doesn't understand the request.";
//                    break;
//                case 403:
//                    status = "403: Forbidden\n-- Server understood the request, but refuses to 'service' it.";
//                    break;
//                case 200:
//                    status = "200: OK\n--  Server successfully found the resource and has sent it back to the client.";
//                    break;
//                default:
//                    throw new IllegalStateException("Unexpected Error value: " + status_code);
//            }
//            return status;
//        }
//
//    }
//    public class httpfs{
//
//        public final static int SOCKET_PORT = 80;  // you may change this
//        //    public final static String FILE_TO_SEND = "c:/temp/source.pdf";  // you may change this
//        public final static String File_for_client = "Default/test.txt";  // you may change this
//
//
//        public static void main(String[] args) throws IOException {
//            ServerSocket servsock = new ServerSocket(SOCKET_PORT);
//    //        ServerSocket(port, backlog, bindAddr)
//            System.out.println("Waiting...");
//            Socket sock = servsock.accept();
//            System.out.println("Accepted connection : " + sock);
//
//
//            FileInputStream fis = null;
//            BufferedInputStream bis = null;
//            OutputStream os = null;
//            //mine
//            try {
//                try {
//                    InputStreamReader in = new InputStreamReader(sock.getInputStream());
//                    BufferedReader br = new BufferedReader(in);
//
//                    // send file
//                    File myFile = new File(File_for_client);
//                    byte[] mybytearray = new byte[(int) myFile.length()];
//                    fis = new FileInputStream(myFile);
//                    bis = new BufferedInputStream(fis);
//
//                    bis.read(mybytearray, 0, mybytearray.length);
//                    os = sock.getOutputStream();
//                    System.out.println("Sending " + File_for_client + "(" + mybytearray.length + " bytes)");
//                    os.write(mybytearray, 0, mybytearray.length);
//                    os.flush();
//                    System.out.println("Done.");
//
//    // 1. prints directory files must be implemented for subdocs too
//
//
//                    String file_command = br.readLine();
//                    System.out.println("command: " + file_command);
//                    File folder = new File(file_command);
//                    File[] listOfFiles = folder.listFiles();
//                    System.out.println("Reading from directory: " + folder);
//
//                    if(folder.exists()){
//                        System.out.println("folder: " + folder + " exists!");
//                        System.out.println(status.Status_codes(200));
//                    }else{
//                        System.out.println("folder: " + folder + " does not exist");
//                        System.out.println(status.Status_codes(404));
//                    }
//
//    //                    this created directory
//    //                if(!folder.mkdir()){
//    //                        System.out.println("Directpry Strucutre not created");
//    //                    }else{
//    //                        System.out.println("Got created!");
//    //                    }
//
//
//    ////returns content of file
//                    BufferedReader objReader = null;
//                    int content_length = 0;
//                    try {
//                        String strCurrentLine;
//
//                        //file to be read
//    //                        objReader = new BufferedReader(new FileReader("Default/test.txt"));
//    //                        while ((strCurrentLine = objReader.readLine()) != null) {
//    //                            System.out.println("current" + strCurrentLine);
//    //                            content_length += 1;
//    //                        }
//
//                        //file being read and contnt tpye - Works!
//                        Reader reader = new BufferedReader(new FileReader("Default/source-downloaded.txt"));
//                        //testting fiel type read.
//                        //test file is taken from client
//                        String test_file="txt.txt";
//                        String filteType = "Undetermined";
//                        final File file = new File(test_file);
//                        try {
//                            filteType = Files.probeContentType(file.toPath());
//                        }catch (IOException e){
//                            System.out.println("Error: Unable to deeminefile type for " + test_file +" due to exception " + e);
//                        }
//                        System.out.println("Filetype is: "+ filteType);
//                        //
//
//                        int theCharNum = reader.read();
//                        System.out.println("the char num" + theCharNum);
//                        while(theCharNum != -1) {
//                            char theChar = (char) theCharNum;
//
//                            System.out.print(theChar);
//                            content_length +=1;
//                            theCharNum = reader.read();
//                        }
//                        System.out.println("Content-Length: " + content_length);
//                    } catch (Exception e) {
//                        System.out.println("HTTP ERROR 404");
//                    } finally {
//                        try {
//                            if (objReader != null)
//                                objReader.close();
//                        } catch (IOException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//    //end of file print
//
//
//                } finally {
//                    if (bis != null) bis.close();
//                    if (os != null) os.close();
//                    if (sock != null) sock.close();
//                }
//
//            } finally {
//                if (servsock != null) servsock.close();
//            }
//        }
//
//
//    }
//    //retired
//    //                for (int i = 0; i < listOfFiles.length; i++) {
//    //                    if (listOfFiles[i].isFile()) {
//    //                        System.out.println("File: " + listOfFiles[i].getName());
//    //                    } else if (listOfFiles[i].isDirectory()) {
//    //                        System.out.println("Directory: " + listOfFiles[i].getName());
//    //                    }
//    //                }
//
//
//    //can be used wit hfi;e headers
//    //                    try {
//    //                        File f = new File("Default/");
//    //
//    //                        FilenameFilter filter = new FilenameFilter() {
//    //                            @Override
//    //                            public boolean accept(File f, String name) {
//    //                                // We want to find only .c files
//    //                                return name.endsWith(".c");
//    //                            }
//    //                        };
//    //
//    //                        // Note that this time we are using a File class as an array,
//    //                        // instead of String
//    //                        File[] files = f.listFiles(filter);
//    //
//    //                        // Get the names of the files by using the .getName() method
//    //                        for (int i = 0; i < files.length; i++) {
//    //                            System.out.println(files[i].getName());
//    //                        }
//    //                    } catch (Exception e) {
//    //                        System.err.println(e.getMessage());
//    //                    }
//    //
//    //                    try {