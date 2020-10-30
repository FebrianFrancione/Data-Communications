import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.File;


public class client_tester {
    public static void main(String[] args) throws IOException {
        Socket client = new Socket("localhost", 80);
        PrintWriter pr = new PrintWriter(client.getOutputStream());
//        pr.println("working?");


//this creates new file - source is required to go deeper
//        new File("C:\\Users\\febri\\IdeaProjects\\comp445-a2-http-server\\src\\test2.txt").createNewFile();
////        new File("C:\\Users\\Simon\\doesntexist\\test.txt").createNewFile();


//
//        String s = File.pathSeparator;
//        String fullPath = "C:\\Users\\febri\\IdeaProjects\\comp445-a2-http-server\\test.txt";
//        String fullPath2 = "/hey.txt";
//        File file = new File(fullPath);
//        String path = file.getParent();
//        String f = file.getName();
//        System.out.println("'" + path + "'" + "\n" + f);


        pr.println("GET /hello.txt HTTP/1.0\r\nUser-Agent: Concordia\r\n\r\n");

//        pr.println("\n");
//        pr.println("POST /test3  HTTP/1.0\r\n");
        pr.flush();
    }
}
