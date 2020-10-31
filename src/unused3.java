//import java.io.*;
//import java.net.Socket;
//import java.util.Scanner;
//
//public class httpfsclient {
//
//    public final static int SOCKET_PORT = 80;      // you may change this
//    public final static String SERVER = "127.0.0.1";  // localhost
//    public final static String File_For_Server = "Default/SubDocs/source-downloaded2.txt";  // you may change this, I give a
//    // different name because i don't want to
//    // overwrite the one used by server...
//
//    public final static int FILE_SIZE = 6022386; // file size temporary hard coded
//    // should bigger than the file to be downloaded
//
//    public static void main (String [] args ) throws IOException {
//        Socket sock = new Socket(SERVER, SOCKET_PORT);
//        System.out.println("Connecting...");
//        int bytesRead;
//        int current = 0;
//        FileOutputStream fileOutputStream = null;
//        BufferedOutputStream bufferedOutputStream = null;
//
//
//        try {
//
//
//            // receive file from server
//            byte [] mybytearray  = new byte [FILE_SIZE];
//            //default
//            InputStream is = sock.getInputStream();
//            fileOutputStream = new FileOutputStream(File_For_Server);
//            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
//
////            BufferedInputStream input = new BufferedInputStream(sock.getInputStream());
//            //mine for commands
//            InputStreamReader in = new InputStreamReader(sock.getInputStream());
//            BufferedReader br = new BufferedReader(in);
//            PrintWriter pw = new PrintWriter(sock.getOutputStream());
//            Scanner scanner = new Scanner(System.in);
////            bytesRead = is.read(mybytearray,0,mybytearray.length);
////            current = bytesRead;
////            do {
////                bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
////                if(bytesRead >= 0) current += bytesRead;
////            } while(bytesRead > -1);
////
////
////
////            bufferedOutputStream.write(mybytearray, 0 , current);
////            bufferedOutputStream.flush();
////            System.out.println("File " + File_For_Server
////                    + " downloaded (" + current + " bytes read)");
//
////sending file directory name to server
//            System.out.println("Enter you file to be read and directory");
//            String line = scanner.nextLine();
//            System.out.println(line);
//            pw.write(line);
//            pw.flush();
//            pw.close();
//            scanner.close();
//        }catch(IOException e){
//            e.printStackTrace();
//        }
////        finally {
////            if (fileOutputStream != null) fileOutputStream.close();
////            if (bufferedOutputStream != null) bufferedOutputStream.close();
////
//////            if (sock != null) sock.close();
////        }
//    }
//
//}