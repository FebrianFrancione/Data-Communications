
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
    	//for running with xampp /opt/lampp/htdocs/src/ which comes with some issues need to fix
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
        System.out.println("7: Httpfs: GET_METHOD");
        ResponseLibrary http_response = null;
//        String path = rootDir + http_request.getRequest();
//        File file = new File(path);
        //trying to set defAULT rootdir
        String path = rootDir + "\\src\\testFile\\" + http_request.getRequest().replace('/','\\');
//        System.out.println("HTTPFS: Rootdir: " + rootDir);
//        System.out.println("Httpsrequest.getrequesturi: " + httpRequest.getRequestURI().replace('/','\\'));
        //hardcoded
        //set default direcoty to this?
//        path = rootDir+"\\src\\testFile";
//        path = rootDir+"\\src\\testFile\\hello.txt";
//        System.out.println("test path: " + path);
        Path file_path = new File(path).toPath();
        String mimetype = "";

        try {
            mimetype = Files.probeContentType(file_path);
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
            return server.error_response(ResponseLibrary.status_400, "Not a file!",  http_request.getUser_agent());
            //http server response
        }else if(!file.isFile()) {
            System.out.println("httpfs 404: not found");
            return server.error_response(ResponseLibrary.status_404, "Not Found", http_request.getUser_agent());
        } else if(!file.canRead()){
            System.out.println("httpfs 500");
            return server.error_response(ResponseLibrary.status_500, "Cannot read file: " + path,  http_request.getUser_agent());
        }
        // http server response
        //in case passes correctly
        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            StringBuilder sb = new StringBuilder();
            String line;
            for (line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line + '\n');
//                System.out.println("content of file: " + sb);
            }
//            System.out.println("Stringbuffer read contents: "+sb.toString());
            http_response = getResponse(ResponseLibrary.status_200, sb.toString(), http_request.getUser_agent(), mimetype);
            System.out.println("httpfs: httpresponse after get response : " + http_response);
        } catch (FileNotFoundException f){
            //http response
            http_response = server.error_response(ResponseLibrary.status_500, "File not found: " + path,  http_request.getUser_agent());
        }
        catch (IOException e){
            http_response = server.error_response(ResponseLibrary.status_500, "I/O file error, file was not read: " + path,  http_request.getUser_agent());
        }
        System.out.println("before retyurnig : " + http_response);
        return http_response;
    }
//        private ResponseLibrary POST_METHOD(){
//
//        }
    private ResponseLibrary getResponse(String status, String message, String user_agent, String content_type){
        int content_length = message.getBytes().length;
        System.out.println("getResponse");
        return new ResponseLibrary(HTTP_VERSION, status,date.format(ZonedDateTime.now()),user_agent,content_length,content_type,message);
//        return new ResponseLibrary(HTTP_VERSION, status, date.format(ZonedDateTime.now()), "test", message, content_length);
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
