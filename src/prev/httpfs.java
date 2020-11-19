package prev;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.nio.file.Files;



public class httpfs implements req_handler {
    public static final String HTTP_VERSION = "HTTP/1.1";

    DateTimeFormatter date = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss");
    private String directory_root;
    public httpfs(String directory_root){
        this.directory_root = directory_root;
    }

    private ResponseLibrary GET_METHOD(RequestLibrary http_request){
        System.out.println("7: Httpfs: GET_METHOD");
        ResponseLibrary http_response = null;
//        String path = rootDir + http_request.getRequest();
//        File file = new File(path);
        //trying to set defAULT rootdir
        String path = directory_root + "/src/testFile" + http_request.getRequest();
//        System.out.println("HTTPFS: Rootdir: " + rootDir);
//        System.out.println("Httpsrequest.getrequesturi: " + httpRequest.getRequestURI().replace('/','\\'));
        //hardcoded
        //set default direcoty to this?
//        path = rootDir+"\\src\\testFile";
//        path = rootDir+"\\src\\testFile\\hello.txt";
        Path file_path = new File(path).toPath();
        String mimetype = "";
        try {
            mimetype = Files.probeContentType(file_path);
            //System.out.println("File type: " + mimetype);
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
        //System.out.println("file: " + file );
        if(!Files.isReadable(file_path)) {
        	return server.error_response(ResponseLibrary.status_403, "Forbidden", http_request.getUser_agent());
        }
        if(file.isDirectory()){
            File[] file_list = file.listFiles();
            StringBuilder sb = new StringBuilder();
            if (file_list.length > 0 && file_list != null){
                for (File directory : file_list) {
                    sb.append(directory.getName());
                    sb.append(directory.isDirectory() ? File.separator + "\n" : "\n");
                }
                //printing list of files in directory
                System.out.println(sb);
            }
            else {
                sb.append("Directory: Empty");
            }
//            String status, String message, String user_agent, String content_type
            return getResponse(ResponseLibrary.status_200, sb.toString(),http_request.getUser_agent(),mimetype);
//            System.out.println("File is directory");
//            System.out.println("file inside directory: " + file);
//            return httpfs.readFiles(file);
////            return server.error_response(ResponseLibrary.status_400, "Not a file!",  http_request.getUser_agent());

            //http server response
        }
        else if(!file.isFile()) {
            System.out.println("httpfs 404: not found");
            return server.error_response(ResponseLibrary.status_404, "Not Found", http_request.getUser_agent());
        } 
        else if(!file.canRead()){
            System.out.println("httpfs 403");
            return server.error_response(ResponseLibrary.status_403, "Cannot read file: " + path,  http_request.getUser_agent());
        }
        // http server response
        //in case passes correctly
        
        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            StringBuilder sb = new StringBuilder();
            String line;
            for (line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line + '\n');
                //System.out.println("CONTENT OF FILE: " + sb);
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
        //System.out.println("before returning : " + http_response);
        return http_response;
    }
    
    private ResponseLibrary POST_METHOD(RequestLibrary http_request) {
    	ResponseLibrary http_response = null;
    	String file_content = http_request.getBody();
        try {
            String file_path = directory_root + "/src/testFile" + http_request.getRequest();
            File file_directory = new File(file_path);
            //System.out.println(file_directory);
            String local_path = file_directory.getParent();
            Path path = new File(file_path).toPath();
            File file_folder = new File(local_path);
            //System.out.println(local_path + "  " + file_directory + file_directory.exists());
            System.out.println("POST: 1");
            if(!file_folder.exists()) {
            	File fyl = new File(local_path);
            	if (fyl.mkdirs()) {
                    System.out.println("New Directory is created!");
                } else {
                    System.out.println("Failed to create new directory!");
                }
            }
            else if (!Files.isWritable(path)) {
            	//http_response = server.error_response(ResponseLibrary.status_403, "Writing to directory not possible", http_request.getUser_agent());
            	System.out.println(http_response);
                return server.error_response(ResponseLibrary.status_403, "Writing to directory not possible", http_request.getUser_agent());
            } 
            else if (!file_folder.isDirectory()) {
                if (!file_folder.mkdirs()) {
                	//http_response = server.error_response(ResponseLibrary.status_500, "Creating Directory not possible", http_request.getUser_agent());
                	//System.out.println(http_response);
                    return server.error_response(ResponseLibrary.status_500, "Creating Directory not possible", http_request.getUser_agent());
                }
            }
            try {
                if (!file_directory.exists()) {
                    file_directory.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!file_directory.canWrite()) {
            	/*http_response = server.error_response(ResponseLibrary.status_403, "File cannot be written", http_request.getUser_agent());
            	System.out.println(http_response);*/
                //return server.error_response(ResponseLibrary.status_403, "File cannot be written", http_request.getUser_agent());
            }

            try {
                PrintWriter pw = new PrintWriter(file_directory);
                if (file_content != null) {
                    pw.print(file_content);
                    pw.close();
                } else {
                    pw.print("");
                    pw.close();
                }
//
            }
            catch (FileNotFoundException e) {
                System.out.println("Could not write contents to file");
                e.printStackTrace();
            }
            //System.out.println(http_response);
            //http_response =  getResponse(ResponseLibrary.status_201, file_content, http_request.getUser_agent(), http_request.getContent_type());
        } catch (Exception e) {
            e.printStackTrace();
        }
        http_response = getResponse(ResponseLibrary.status_201, file_content, http_request.getUser_agent(), http_request.getContent_type());
        System.out.println(http_response);
        return http_response;
    }
    private ResponseLibrary getResponse(String status, String message, String user_agent, String content_type){
        int content_length = message.getBytes().length;
        System.out.println("Response:");
        return new ResponseLibrary(HTTP_VERSION, status,date.format(ZonedDateTime.now()),user_agent,content_length,content_type,message);
//        return new ResponseLibrary(HTTP_VERSION, status, date.format(ZonedDateTime.now()), "test", message, content_length);
    }
    
    

    @Override
    public ResponseLibrary request_handler(RequestLibrary httpRequest) {
        ResponseLibrary httpResponse = null;
        if (httpRequest.getMethod().equalsIgnoreCase(httpRequest.GET)) {
            //calling get
            httpResponse = GET_METHOD(httpRequest);
        }
        else if (httpRequest.getMethod().equalsIgnoreCase(httpRequest.POST)) {
//calling post
            httpResponse = POST_METHOD(httpRequest);
        }
        return httpResponse;
    }
}
