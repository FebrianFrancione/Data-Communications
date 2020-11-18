/*
package Lab3;

import prev.RequestLibrary;

import javax.sound.midi.Soundbank;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//import org.apache.commons.io.IOUtils;

public class main {

    public static void main(String[] args){
        int port = 81;
        UDPServer server = new UDPServer(port);
        UDPClient client = new UDPClient(port);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(client);
        executorService.submit(server);
try{

    System.out.println("URL ------------------");
   URL url = new URL("http://httpbin.org/");
//    String contents = IOUtils.toString(url.openStream());
    System.out.println(url.toString());
    System.out.println("Protocol: "+ url.getProtocol());
    System.out.println("HostName: " + url.getHost());
    System.out.println("Default Port: " + url.getDefaultPort());
    System.out.println("Query: " + url.getQuery());
    System.out.println("Path: " + url.getPath());
    System.out.println("File: "+url.getFile());
    System.out.println("Reference: " + url.getRef());
*/
/* coonects!
    InetAddress web = InetAddress.getByName("httpbin.org")
*//*

//    InetAddress web = InetAddress.getByName("www.baeldung.com")

    InetAddress web = InetAddress.getByName("concordia.ca");
    System.out.println("Intet: " + web);

} catch (MalformedURLException e) {
    e.printStackTrace();
} catch (IOException e) {
    e.printStackTrace();
}

    }
}
*/
