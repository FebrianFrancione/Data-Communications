import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class client_tester {
    public static void main(String[] args) throws IOException {
        Socket client = new Socket("localhost", 80);
        PrintWriter pr = new PrintWriter(client.getOutputStream());
//        pr.println("working?");
        pr.println("GET /status/418 HTTP/1.0\r\n");
//        pr.println("\n");
        pr.flush();
        // more debugging required
    }
}
