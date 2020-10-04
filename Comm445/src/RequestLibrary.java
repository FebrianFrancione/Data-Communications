import java.net.Socket;
import java.util.Scanner;
import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;

public class RequestLibrary {
	private final int PORT  = 80;
	private final String VERSION = "HTTP/1.0";
	private final String USER_AGENT = "MyAwesomeBrowser";
	private BufferedReader reader;
	private PrintWriter writer;
	private Scanner scanner;
	private Socket socket;
	private URL url;
	
	public void get(String host) throws Exception {
		String temp;
		int skip = 0;
		socket = new Socket(host, PORT);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream());
		//add host name as header:key
		writer.write("GET " + "/get?course=networking&assignment=1 " +(String) VERSION+ "\r\n" +"User-Agent:" + USER_AGENT+ "\r\n\r\n");
		writer.flush();
		while ((temp = reader.readLine()) != null) {
			if(skip > 8) {
				System.out.println(temp);
			}
			else {
				skip ++;
			}
		}
	}
	
	//Get with verbose
	public void getWVerbose(String host) throws Exception {
		String temp;
		//url = new URL(host);
		socket = new Socket(host, PORT);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream());
		//add host name as header:key
		writer.write("GET " + "/get?course=networking&assignment=1 " +(String) VERSION+ "\r\n" +"User-Agent:" + USER_AGENT+ "\r\n\r\n");
		writer.flush();
		while((temp = reader.readLine()) != null) {
			System.out.println(temp);
		}
		writer.close();
		reader.close();
		socket.close();
	}
	
	public void post(String connection) {
		
		
		
		
	}

	public static void main(String[] args) throws Exception {
		RequestLibrary lib = new RequestLibrary();
		lib.get("httpbin.org");
	}
}
