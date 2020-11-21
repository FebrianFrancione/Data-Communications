package Lab3;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.apache.commons.cli.*;

import static java.nio.channels.SelectionKey.OP_READ;

public class UDPClient {
    public static void main(String[] args) throws IOException {
        String routerHost = "localhost";
        int routerPort = 3000;
        String serverHost = "localhost";
        int serverPort = 8007;
        SocketAddress routerAddress = new InetSocketAddress(routerHost, routerPort);
        InetSocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);


        String Sample_Request = "GET /hello.txt HTTP/1.0\r\nUser-Agent: Concordia\r\n\r\n";
        String sample_method = "GET";

        //httpc must be implemented before
        if (sample_method.equals("GET")) {
            GET_Method(routerAddress, serverAddress, Sample_Request);
        } else {
            POST_Method(routerAddress, serverAddress);
        }
    }


    /*
     * After having process the requests, call the get/post methods using UDP
     * */
    private static void GET_Method(SocketAddress routerAddress, InetSocketAddress serverAddress, String Sample_Request) throws IOException {
        //request processing done before
        threeWayHandshake(routerAddress, serverAddress, Sample_Request);

    }

    private static void POST_Method(SocketAddress routerAddress, InetSocketAddress serverAddress) {
    }


    private static void threeWayHandshake(SocketAddress routerAddress, InetSocketAddress serverAddress, String Sample_Request) throws IOException {
        //channel set to blocking by default
        try (DatagramChannel channel = DatagramChannel.open()) {
            //handshake packets will have the same format as usual pkt but no data will be attached.
            // Mapping for pkt types are as follows: 0 -> Data, 1-> ACK, 2-> SYN, 3-> SYN-ACK
            //create new packet of type SYN, no payload attached
            // todo: What to set sequence Number to?
             /* seq is x, sending to server, wehich replies with Ack=x+1 and SYn=y (server's sequence number start). Ack is thus sent from client with y+1.
             Note that the x+1 and y+1 sent by cleint/server respectively will indicate what they EXPECT the NEXT SEQUENCE NUMBER to be.
              create verification that packet is SYN? - done using different packets*/

            Packet p = Packet.HandshakePacket(2, 100, serverAddress.getAddress(), serverAddress.getPort());
            System.out.println("Sending SYN packet to router");
            System.out.println("Pkt type: " + p.getType()+" Seq#: " + p.getSequenceNumber());
            //when sending packet through channel, packet must pass buffer
            channel.send(p.toBuffer(), routerAddress);
            System.out.println("-----------------------------");

            /*
            Receiving message from server
             */

            // Try to receive a packet within timeout.
            //channel set to non-blocking
            channel.configureBlocking(false);
            //handle multi channels with single thread, used to select channel ready to communicate, allows readiness selection
            Selector selector = Selector.open();
            channel.register(selector, OP_READ);
            System.out.println("Waiting for response");
            selector.select(5000);
            //key = token representing the registration of a channel with selector (selector maintains 3 sets of keys)
            //1- key set- keys with registered channel, 2- selected key set: keys with channels ready for at least one operation 3- Cancelled key set: cancelled keys, channels not deregistered
            Set<SelectionKey> keys = selector.selectedKeys();
            if (keys.isEmpty()) {
                System.out.println("No response after timeout");
                return;
            }
            // We just want a single response.
            ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
            SocketAddress router = channel.receive(buf);
            //Prepare the buffer to read data by channel-writes or relative gets(limit=position, positiin=0)
            buf.flip();
            Packet resp = Packet.fromBuffer(buf);
            if(resp.getType() != 3){
                System.out.println("A SYN-ACK packet was expected.");
                System.exit(0);
            }else{
                System.out.println("Received a SYN-ACK packet!");
                System.out.println("Seq#" + resp.getSequenceNumber());
            }
            //clear the buffer, prepare buffer for writing data by channel-reads pr relative puts(lim=capacity, post=0)
            keys.clear();
            selector.close();
            //set back to blocking before sending ACK packet to server
            channel.configureBlocking(true);
            //creating ACK packet
            Packet ACK_pkt = Packet.HandshakePacket(1,resp.getSequenceNumber()+1,serverAddress.getAddress(), serverAddress.getPort());

            System.out.println("Sending ACK packet to router");
            System.out.println("Pkt type: " + ACK_pkt.getType()+" Seq#: " + ACK_pkt.getSequenceNumber());
            //when sending packet through channel, packet must pass buffer
            channel.send(ACK_pkt.toBuffer(), routerAddress);

            System.out.println("-----------------------------");

            //start the other method of sending request here? or back in GETmethod?
            System.out.println("3 Way hanshake complete");
//        running client
            runClient1(routerAddress, serverAddress, Sample_Request);
        }
    }

    private static void runClient1(SocketAddress routerAddress, InetSocketAddress serverAddress, String Sample_Request) throws IOException {
        try (DatagramChannel channel = DatagramChannel.open()) {
            System.out.println("Channel opened, not connected");
            System.out.println("Server Started: " + serverAddress);
            System.out.println("-----------------------------");

            //message that will be converted to bytes
            String msg = Sample_Request;

            //create new packet
            Packet p = new Packet.Builder()
                    .setType(0)
                    .setSequenceNumber(1L)
                    .setPortNumber(serverAddress.getPort())
                    .setPeerAddress(serverAddress.getAddress())
                    .setPayload(msg.getBytes())
                    .create();

            System.out.println("Sending packet to router");
            //when sending packet through channel, packet must pass buffer
            channel.send(p.toBuffer(), routerAddress);
            System.out.println("Sending: " + msg + " to router at: " + routerAddress);
            System.out.println("-----------------------------");

            /*
            Receiving message from server
             */


            // Try to receive a packet within timeout.
            //channel set to non-blocking
            channel.configureBlocking(false);

            //handle multi channels with single thread
            Selector selector = Selector.open();

            channel.register(selector, OP_READ);
            System.out.println("Waiting for response");
            selector.select(5000);
            Set<SelectionKey> keys = selector.selectedKeys();
            if (keys.isEmpty()) {
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





}

//    private static void runClient(SocketAddress routerAddress, InetSocketAddress serverAddress, String Sample_Request) throws IOException {
//        System.out.println("Creating DatagramChannel");
//        //channel set to blocking by default
//        try (DatagramChannel channel = DatagramChannel.open()) {
//            System.out.println("Channel opened, not connected");
//            System.out.println("Server Started: " + serverAddress);
//            System.out.println("-----------------------------");
//
//            //message that will be converted to bytes
//            String msg = "";
//
//            //create new packet
//            Packet p = new Packet.Builder()
//                    .setType(0)
//                    .setSequenceNumber(1L)
//                    .setPortNumber(serverAddress.getPort())
//                    .setPeerAddress(serverAddress.getAddress())
//                    .setPayload(msg.getBytes())
//                    .create();
//
//            System.out.println("Sending packet to router");
//            //when sending packet through channel, packet must pass buffer
//            channel.send(p.toBuffer(), routerAddress);
//            System.out.println("Sending: " + msg + " to router at: " + routerAddress);
//            System.out.println("-----------------------------");
//
//            /*
//            Receiving message from server
//             */
//
//
//            // Try to receive a packet within timeout.
//            //channel set to non-blocking
//            channel.configureBlocking(false);
//
//            //handle multi channels with single thread
//            Selector selector = Selector.open();
//
//            channel.register(selector, OP_READ);
//            System.out.println("Waiting for response");
//            selector.select(5000);
//            Set<SelectionKey> keys = selector.selectedKeys();
//            if (keys.isEmpty()) {
//                System.out.println("No response after timeout");
//                return;
//            }
//
//            // We just want a single response.
//            ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
//            SocketAddress router = channel.receive(buf);
//            buf.flip();
//            Packet resp = Packet.fromBuffer(buf);
//            System.out.println("Packet: " + resp);
//            System.out.println("Router: " + router);
//            String payload = new String(resp.getPayload(), StandardCharsets.UTF_8);
//            System.out.println("Payload: " + payload);
//            keys.clear();
//        }
//    }

/*
}*/
