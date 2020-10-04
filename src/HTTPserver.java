import java.io.*;
import java.net.*;


import java.net.Socket;
import java.net.URL;



public class HTTPserver {

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(80);
            Socket s = ss.accept();
            System.out.println("Client connected");
            InputStreamReader in = new InputStreamReader(s.getInputStream());
            BufferedReader bf = new BufferedReader(in);
            PrintWriter pr = new PrintWriter(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //        public GET(String host, int port, URL url, HTTPclient request, Boolean displayHeader){
    public void GET(URL url, Boolean displayHeader){
        //        if need display Header
        if (displayHeader){
            System.out.println("TRY");
//                PrintWriter pr = new PrintWriter(HTTPserver.s.getOutputStream());
//                try {
//
//                    System.out.println("displayHeader " + displayHeader);
//                    System.out.println("host is: "+host);
//                    System.out.println(port);
////                    Socket socket = new Socket(host, port);
////                    InputStream input = socket.getInputStream();
////                    OutputStream output = socket.getOutputStream();
////                    output.write(request.getRequest1().getBytes());
////                    output.flush();
//                    StringBuilder response = new StringBuilder();
////                    int data = input.read();
////                    boolean findkey = false;
////
////                    while (data != -1){
////                        char tem = (char) data;
////                        if(tem == '{'){
////                            findkey = true;
////                        }
////                        if (findkey){
////                            response.append((char) data);
////                        }
////                        data = input.read();
////                    }
////
////                    System.out.println(response);
////                    socket.close();
//                } catch (IOException e){
//                    e.printStackTrace();
//                }
        } else {
            try {
//                System.out.println("entering the false section");
//                System.out.println("pruinting host" + host);
//                System.out.println("port "+ port);
//                Socket socket = new Socket(host, port);
//                InputStream input = socket.getInputStream();
//                OutputStream output = socket.getOutputStream();
//                output.write(request.getRequest1().getBytes());
////                System.out.println("output: "+request.getRequest1().getBytes());
//                output.flush();
//                StringBuilder response = new StringBuilder();
//                System.out.println("response: " + response);
//                int data = input.read();
////                int data = 500;
////                System.out.println("data : " + data);
//                boolean findkey = false;
//                while (data != -1){
//                    char tem = (char) data;
////                    System.out.println(tem);
//                    if(tem == '{'){
//                        findkey = true;
//                    }
//                    if (findkey){
//                        response.append((char) data);
//                    }
//                    data = input.read();
//                }
//                System.out.println(response);
//                socket.close();
                System.out.println("Entering this false section");
                System.out.println("False URL " + url);
                System.out.println("uhttpc get 'http://httpbin.org/get?course=networking&assignment=1'rl type " + url.getClass());
                // open the url stream, wrap it an a few "readers"
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                // write the output to stdout
                String line;
                while ((line = reader.readLine()) != null)
                {
                    if(!line.contains("Accept")&&!line.contains("X-Amzn-Trace-Id")&&!line.contains("origin")){
                        System.out.println(line);
                    }

                }
                reader.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}

