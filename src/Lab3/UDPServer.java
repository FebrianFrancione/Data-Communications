package Lab3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Arrays.sort;

public class UDPServer {
    /*   private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);*/

    public static void main(String[] args) throws IOException {
        int port = 8007;
        UDPServer server = new UDPServer();
        server.threeWayListen(port);
//        server.listenAndServe(port);
    }

    private void threeWayListen(int port) throws IOException {
        try (DatagramChannel channel = DatagramChannel.open()) {
            // bind channel to inetaddress
            channel.bind(new InetSocketAddress(port));
            ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN).order(ByteOrder.BIG_ENDIAN);
            System.out.println("EchoServer is listening at: " + channel.getLocalAddress());
            buf.clear();
            SocketAddress router = channel.receive(buf);
            // Parse a packet from the received raw data.
            buf.flip();
            Packet packet = Packet.fromBuffer(buf);
            //what does this flip do?
            buf.flip();

            //todo: Switch case for the various types of packets
            switch (packet.getType()){
                case 1:
                    System.out.println("Received an ACK Packet!");
                    channel.connect(router);
                    System.out.println("Datagram Channel connected");
                case 2:
                    // in the case of SYN packet, following fsm structure, needs to wait for ACK from client and send a SYN-ACK back to client
                    System.out.println("Received a SYN Packet");
                    Packet resp = packet.toBuilder().setType(3).create();
                    System.out.println("Sending SYN-ACK to client");
                    channel.send(resp.toBuffer(), router);
            }

        }
    }

    private void listenAndServe(int port) throws IOException {
        try (DatagramChannel channel = DatagramChannel.open()) {

            // bind channel to inetaddress
            channel.bind(new InetSocketAddress(port));
            System.out.println("EchoServer is listening at: " + channel.getLocalAddress());
            ByteBuffer buf = ByteBuffer
                    .allocate(Packet.MAX_LEN)
                    .order(ByteOrder.BIG_ENDIAN);
            //idk what this for loop is
            for (; ; ) {
                buf.clear();
                SocketAddress router = channel.receive(buf);
                // Parse a packet from the received raw data.
                buf.flip();
                Packet packet = Packet.fromBuffer(buf);
                buf.flip();

                if (packet.getType() == 2){
                    System.out.println("SYN received");
                }


                String payload = new String(packet.getPayload(), UTF_8);
                System.out.println("Packet: " + packet);
                System.out.println("Payload: " + payload);
                System.out.println("Router: " + router);


                // Send the response to the router not the client.
                // The peer address of the packet is the address of the client already.
                // We can use toBuilder to copy properties of the current packet.
                // This demonstrate how to create a new packet from an existing packet.
                System.out.println("Sending SYN-ACK to router");
                Packet resp = packet.toBuilder().setType(3)
                        .setPayload(payload.getBytes())
                        .create();
                channel.send(resp.toBuffer(), router);

            }
        }
    }

}