package prev;
import java.net.Socket;
import java.util.Scanner;
import java.io.*;
import java.net.URL;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestLib {
	private final int PORT  = 8080;
	private final String VERSION = "HTTP/1.1";
	private final String USER_AGENT = "USER-AGENT: MyAwesomeBrowser";
	Pattern pattern;
    Matcher matcher;
	private BufferedReader reader;
	private PrintWriter writer;
	private Scanner scanner;
	private Socket socket;
	private URL url;
	private URI uri;
	
	public void get(String host) throws Exception {
		String temp;
		int skip = 0;
		//url = new URL(host);		
		//System.out.println(url.getHost() + "  hello " + url.getFile());
		String keyVal = "";
		String[] arr = host.split(" ");
		uri = new URI(arr[arr.length - 1]);
		//System.out.println(arr.length );
		for(int i = 0; i<arr.length; i++) {
			if(arr[i].equals("-h")) {
				//System.out.println(arr[i+1].substring(0,1).equals("\""));	
				if(arr[i+1].substring(0,1).equals("\"")) {
					//if  key value exists
					keyVal = keyVal + arr[i+1] + "\r\n";
					keyVal = keyVal.replace("\"", "");	
				}
			}
		}
		socket = new Socket(uri.getHost(), PORT);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream());
		writer.write("GET " + uri.toURL().getFile() + " "+ (String) VERSION+ "\r\n" + USER_AGENT+ "\r\n" + keyVal+ "\r\n\r\n");
		writer.flush();
		while ((temp = reader.readLine()) != null) {
			if(skip > 8) {
				System.out.println(temp);
			}
			else {
				skip ++;
			}
		}
		writer.close();
		reader.close();
		socket.close();
	}
	
	//Get with verbose
	public void getWVerbose(String host) throws Exception {
		String temp;
		//url = new URL(host);
		String keyVal = "";
		String[] arr = host.split(" ");
		uri = new URI(arr[arr.length - 1]);
		//System.out.println(arr.length );
		for(int i = 0; i<arr.length; i++) {
			if(arr[i].equals("-h")) {
				//System.out.println(arr[i+1].substring(0,1).equals("\""));	
				if(arr[i+1].substring(0,1).equals("\"")) {
					//if  key value exists
					keyVal = keyVal + arr[i+1] + "\r\n";
					keyVal = keyVal.replace("\"", "");	
				}
			}
		}
		socket = new Socket(uri.getHost(), PORT);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream());
		writer.write("GET " + uri.toURL().getFile() + " "+ (String) VERSION+ "\r\n" + USER_AGENT+ "\r\n" + keyVal+ "\r\n\r\n");
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
		pattern = Pattern.compile(".*-v.*");
		matcher = pattern.matcher(post);
		int contentLength = 0;
		String entityBody = "";
		String keyValue = "";
		String host = "";
		String temp = "";
		int skip = 0;
		String arr[] = post.split(" ");
		uri = new URI(arr[arr.length - 1]);
		host = uri.getHost();
		//System.out.println(uri.toURL().getFile());
		for(int i = 0; i < arr.length; i++) {
			//System.out.println(arr[i] + " arr[i] " +  arr.length);
			if(arr[i].equals("-h")) {
				if(arr[i+1].substring(0,1).equals("\"")) {
					//if  key value exists
					keyValue = keyValue + arr[i+1] + "\r\n";
					keyValue = keyValue.replace("\"", "");	
					System.out.println(keyValue);
				}
			}
			else if(arr[i].equals("-d")) {
				for(int j = i+1; j < arr.length; j++) {
					if(!arr[j].equals(arr[arr.length - 1])) {
						entityBody = entityBody + " "+ arr[j];
						entityBody = entityBody.replace("'", "");
					}
				}
			}
		}
		entityBody = entityBody.substring(1, entityBody.length());
		contentLength = entityBody.length();
		keyValue = keyValue + "Content-Length: " + contentLength;
		socket = new Socket(host, PORT);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream());
		writer.write("POST " + uri.toURL().getFile()+ " " +(String) VERSION+ "\r\n" + keyValue+ "\r\n\r\n" + entityBody);
		writer.flush();
		if(matcher.find() == true) {
			while((temp = reader.readLine()) != null) {
				System.out.println(temp);
			}
		}
		else{
			while ((temp = reader.readLine()) != null) {
				if(skip > 8) {
					System.out.println(temp);
				}
				else {
					skip ++;
				}
			}
		}
		writer.close();
		reader.close();
		socket.close();
	}
	
	public void postFile(String host, File file) throws Exception{
		//the code from local src file local445 crashes the entire thing figure out why
		
	}
	
	public void help() {
		System.out.println("httpc is a curl-like application but supports HTTP protocol only."+
               "Usage: httpc command [arguments] The commands are:\n"+ "get executes a HTTP GET request and prints the response.\n"+ 
			    "post executes a HTTP POST request and prints the response. help prints this screen.\n"+
               "Use \"httpc help [command]\" for more information about a command.");
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