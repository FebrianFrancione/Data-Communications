package Lab3;

/*import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static java.nio.channels.SelectionKey.OP_READ;

public class UDPClient {
    private int SYN_value = 0;
    private int ACK_value = 0;

  /*  private static final Logger logger = LoggerFactory.getLogger(UDPClient.class);*/

  public static void main(String[] args) throws IOException {
      String routerHost = "localhost";
      int routerPort = 3000;
      String serverHost ="localhost";
      int serverPort = 8007;
      SocketAddress routerAddress = new InetSocketAddress(routerHost, routerPort);
      InetSocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);


      runClient(routerAddress, serverAddress);
  }

    private static void runClient(SocketAddress routerAddr, InetSocketAddress serverAddr) throws IOException {
        System.out.println("Creating DatagramChannel");
        try(DatagramChannel channel = DatagramChannel.open()){
            System.out.println("Channel opened, not connected");
            System.out.println("Server Started: " + serverAddr);
            System.out.println("-----------------------------");

            //message that will be converted to bytes
            String msg = "Hello World!";

            //create new packet
            Packet p = new Packet.Builder()
                    .setType(0)
                    .setSequenceNumber(1L)
                    .setPortNumber(serverAddr.getPort())
                    .setPeerAddress(serverAddr.getAddress())
                    .setPayload(msg.getBytes())
                    .create();

            System.out.println("Sending packet to router");
            channel.send(p.toBuffer(), routerAddr);
            System.out.println("Sending: "+ msg + " to router at: " + routerAddr);

            /*
            Receiving message from server
             */


            // Try to receive a packet within timeout.
            channel.configureBlocking(false);
            Selector selector = Selector.open();
            channel.register(selector, OP_READ);
            System.out.println("Waiting for response");
            selector.select(5000);
            Set<SelectionKey> keys = selector.selectedKeys();
            if(keys.isEmpty()){
                System.out.println("No response after timeout");
                return;
            }

            // We just want a single response.
            ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
            SocketAddress router = channel.receive(buf);
            buf.flip();
            Packet resp = Packet.fromBuffer(buf);
            System.out.println("Packet: " + resp);
            System.out.println("Router: " + router);
            String payload = new String(resp.getPayload(), StandardCharsets.UTF_8);
            System.out.println("Payload: " + payload);
            keys.clear();
        }
    }

/*    private static void threeWayHandshake(SocketAddress routerAddr, InetSocketAddress serverAddr) throws IOException{
        try(DatagramChannel channel = DatagramChannel.open()){

        }
    }*/



}