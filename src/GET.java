//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.Socket;
//
//public class GET{
//public GET(String host_number, int port_number, HTTPclient request, Boolean displayHeader){
//    if (displayHeader){
//        try {
//            Socket socket = new Socket(host, port);
//
//            InputStream input = socket.getInputStream();
//            OutputStream output = socket.getOutputStream();
//
//            output.write(request.getRequest().getBytes());
//            output.flush();
//
//            StringBuilder response = new StringBuilder();
//
//            int data = input.read();
//
//            while (data != -1){
//                response.append((char) data);
//                data = input.read();
//            }
//
//            System.out.println(response);
//            socket.close();
//
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//    } else {
//        try {
//            Socket socket = new Socket(host, port);
//
//            InputStream input = socket.getInputStream();
//            OutputStream output = socket.getOutputStream();
//
//            output.write(request.getRequest().getBytes());
//            output.flush();
//
//            StringBuilder response = new StringBuilder();
//
//            int data = input.read();
//            boolean findkey = false;
//
//            while (data != -1){
//                char tem = (char) data;
//                if(tem == '{'){
//                    findkey = true;
//                }
//                if (findkey){
//                    response.append((char) data);
//                }
//                data = input.read();
//            }
//
//            System.out.println(response);
//            socket.close();
//
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//
//}
//}
//}