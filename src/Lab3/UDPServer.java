package Lab3;


import java.io.IOException;
import java.net.*;

public class UDPServer implements Runnable{
    private final int clientPort;

    public UDPServer(int clientPort){
        this.clientPort = clientPort;
    }

    @Override
    public void run(){
        //Datagram socket since we;re using UDP
        try(DatagramSocket serverSocket = new DatagramSocket(80)){
            for(int i =0; i< 3; i++){
                String message = "Message number " + i;
                //Datagram packet since using UDP, takes bytes, length, ip,
                DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), InetAddress.getLocalHost(), clientPort);
                serverSocket.send(datagramPacket);
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
