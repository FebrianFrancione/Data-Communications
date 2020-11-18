package Lab3;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPClient implements Runnable {

    private final int PORT;

    public UDPClient(int port) {
        PORT = port;
    }

    @Override
    public void run(){
        try(DatagramSocket clientSocket = new DatagramSocket(PORT)){
            byte[] buffer = new byte[65507];
            clientSocket.setSoTimeout(3000);
            while(true){
                DatagramPacket datagramPacket = new DatagramPacket(buffer, 0, buffer.length);
                clientSocket.receive(datagramPacket);

                String receivedMessage = new String(datagramPacket.getData());
                System.out.println(receivedMessage);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Timeout. Client closing");
        }
    }
}
