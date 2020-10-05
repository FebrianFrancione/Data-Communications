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
		//use url.host() after splitting // in the passed argument
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
	
	//post with inline
	public void post(String post) throws Exception {
		String[] arr = post.split(" ");
		int contentLength = 0;
		String entityBody = "";
		String keyValue = "";
		String host = "";
		String temp = "";
		for(int i = 0; i < arr.length; i++) {
			if(arr[i].contains("http://httpbin.org/post")) {
				host = arr[i];
				//System.out.println("host: " + host);
			}
			if(arr[i].contains("-h")) {
				keyValue = keyValue + arr[i+1];
				//System.out.println("keyval: " + keyValue);
			}
			if(arr[i].contains("-d")) {
				entityBody = entityBody + arr[i+1];
				//System.out.println("entitybody: "+ entityBody);
			}
			if(arr[i].contains("1}")) {
				entityBody = entityBody + arr[i];
				entityBody = entityBody.substring(1, 19);
				contentLength = entityBody.length();
				//System.out.println("contentlength:" + contentLength+ "entitybody: " + entityBody);
			}	
			keyValue = keyValue + "Content-Length: " + contentLength;
			
			socket = new Socket("httpbin.org", PORT);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			//add host name as header:key
			writer.write("POST " + "/post " +(String) VERSION+ "\r\n" + keyValue  +"\r\n\r\n" + entityBody);
			writer.flush();
			while((temp = reader.readLine()) != null) {
				System.out.println(temp);
			}
			writer.close();
			reader.close();
			socket.close();
		}
	}
	
	public void postFile(String host, File file) throws Exception{
		
	}
	
	public void help() {
		System.out.println("httpc is a curl-like application but supports HTTP protocol only.\nUsage:\n" + "httpc command [arguments]\n" + "The commands are:\n" + 
				"get executes a HTTP GET request and prints the response.\n post executes a HTTP POST request and prints the response.\n" + 
				"help prints this screen.\n" +"Use \"httpc help [command]\" for more information about a command.\n");
	}
	
	public void helpGet(){
		System.out.println("httpc help get\n"+
				"usage:\n httpc get [-v] [-h key:value] URL"+
				"Get executes a HTTP GET request for a given URL.\n"+
				"-v Prints the detail of the response such as protocol, status, and headers.\n"+
				"-h key:value Associates headers to HTTP Request with the format 'key:value'.");
	}
	
	public void helpPost() {
		System.out.println("httpc help post\n usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n"+
	     "Post executes a HTTP POST rgequest for a given URL with inline data or from file."+
          "-vPrints the detail of the response such as protocol, status, and headers."+     
	      "-h key:valueAssociates headers to HTTP Request with the format 'key:value'."+
          "-d stringAssociates an inline data to the body HTTP POST request."+
	      "-f fileAssociates the content of a file to the body HTTP POST request."+
          "Either [-d] or [-f] can be used but not both.");
	}
}