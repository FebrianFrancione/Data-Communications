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
import java.util.*;

import org.apache.commons.cli.*;

import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.charset.StandardCharsets.UTF_8;

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
        threeWayHandshake(routerAddress, serverAddress, Sample_Request);


        //httpc must be implemented before
        if (sample_method.equals("GET")) {
            //to be done by Marjana
            GET_Method(routerAddress, serverAddress, Sample_Request);
        } else {
            //to be done by marjana
            POST_Method(routerAddress, serverAddress);
        }
    }


    /*
     * After having process the requests, call the get/post methods using UDP
     * */
    private static void GET_Method(SocketAddress routerAddress, InetSocketAddress serverAddress, String Sample_Request) throws IOException {
        //request processing done before


    }

    private static void POST_Method(SocketAddress routerAddress, InetSocketAddress serverAddress) {
    }


    //handshake to only establish connection betwn client and server. data packets CAN BE SENT AFTER SYN-ACK RECEIVED
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
            System.out.println("Pkt type: " + p.getType() + " Seq#: " + p.getSequenceNumber());
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
            //NEED TO IMPLEMENT : RESEND SYN IF NO RESPONSE SYN-ACK  AFTER TIMEOUT 

            // We just want a single response.
            ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
            SocketAddress router = channel.receive(buf);
            //Prepare the buffer to read data by channel-writes or relative gets(limit=position, positiin=0)
            buf.flip();
            Packet resp = Packet.fromBuffer(buf);
            if (resp.getType() != 3) {
                System.out.println("A SYN-ACK packet was expected.");
                System.exit(0);
            } else {
                System.out.println("Received a SYN-ACK packet!");
                System.out.println("Seq#" + resp.getSequenceNumber());
            }
            //clear the buffer, prepare buffer for writing data by channel-reads pr relative puts(lim=capacity, post=0)
            keys.clear();
            selector.close();
            //set back to blocking before sending ACK packet to server
            channel.configureBlocking(true);
            //creating ACK packet
            Packet ACK_pkt = Packet.HandshakePacket(1, resp.getSequenceNumber() + 1, serverAddress.getAddress(), serverAddress.getPort());

            System.out.println("Sending ACK packet to router");
            System.out.println("Pkt type: " + ACK_pkt.getType() + " Seq#: " + ACK_pkt.getSequenceNumber());
            //when sending packet through channel, packet must pass buffer
            channel.send(ACK_pkt.toBuffer(), routerAddress);

            System.out.println("-----------------------------");

            //start the other method of sending request here? or back in GETmethod?
            System.out.println("3 Way hanshake complete");
//        running client
//            runClient1(routerAddress, serverAddress, Sample_Request);
            System.out.println("sample request: " + Sample_Request);
            System.out.println("0.1");
            //create new packet
            Packet p1 = new Packet.Builder()
                    .setType(0)
                    .setSequenceNumber(1L)
                    .setPortNumber(serverAddress.getPort())
                    .setPeerAddress(serverAddress.getAddress())
                    .setPayload(Sample_Request.getBytes())
                    .create();

            System.out.println("Sending packet to router");
            //when sending packet through channel, packet must pass buffer
            channel.send(p1.toBuffer(), routerAddress);
            System.out.println("Sending: " + Sample_Request + " to router at: " + routerAddress);
            System.out.println("-----------------------------");


            long startTime = System.currentTimeMillis();
            Map<Integer, String> dataParts = new HashMap<>();

            while (true) {

                buf.clear();
                channel.receive(buf);
                buf.flip();

                Packet respDataPart = Packet.fromBuffer(buf);

                String currentPayload = new String(respDataPart.getPayload(), UTF_8);
                Long seqLong = respDataPart.getSequenceNumber(); // conversion

                dataParts.put(seqLong.intValue(), currentPayload);

                // Check if we've got all the parts
                if (dataParts.size() == 2) {
                    StringBuilder payloadBuilder = new StringBuilder();
                    Set<Integer> keySet = dataParts.keySet();
                    List<Integer> keysList = new ArrayList<>(keySet);
                    Collections.sort(keysList);
                    for (Integer k : keysList) {
                        System.out.println("part: "+dataParts.get(k));
                        payloadBuilder.append(dataParts.get(k));
                    }

                    // Return an all-clear data message to the server
//                    Packet gotAllDataPacket = Packet.buildGotAllDataPacket(peerAddress, peerPort);
//                    channel.send(gotAllDataPacket.toBuffer(), router);

                    // Finally, we can return the full payload
                    System.out.println("Got the full response from server");
                    System.out.println(payloadBuilder.toString());

                } else {
                    continue;
                }
            }
        }
    }
}
////        running client
////            runClient1(routerAddress, serverAddress, Sample_Request);
//            String msg = Sample_Request;
//            System.out.println("sample request: " + Sample_Request);
//            System.out.println("0.1");
//            //create new packet
//            Packet p1 = new Packet.Builder()
//                    .setType(0)
//                    .setSequenceNumber(1L)
//                    .setPortNumber(serverAddress.getPort())
//                    .setPeerAddress(serverAddress.getAddress())
//                    .setPayload(Sample_Request.getBytes())
//                    .create();
//
//            System.out.println("Sending packet to router");
//            //when sending packet through channel, packet must pass buffer
//            channel.send(p1.toBuffer(), routerAddress);
//            System.out.println("Sending: " + msg + " to router at: " + routerAddress);
//            System.out.println("-----------------------------");
//
//            System.out.println("0.2");
//            channel.configureBlocking(false);
//            selector = Selector.open();
//            channel.register(selector, OP_READ);
//            System.out.println("waiting milliseconds3");
////            logger.info("Waiting {} milliseconds for the synack response", Config.RESPONSE_TIMEOUT_MILLIS);
//            selector.select(500);
//            keys = selector.selectedKeys();
//            if (keys.isEmpty()) {
//                System.out.println("No response after timeout");
//            }
//            System.out.println("Keys size: " + keys.size());
//            buf = ByteBuffer.allocate(5000);
//            router = channel.receive(buf);
//            buf.flip();
//
//            if (selector.isOpen()) {
//                selector.close();
//                keys.clear();
//                channel.configureBlocking(true);
//            }
//
//            long startTime = System.currentTimeMillis();
//            Map<Integer, String> dataParts = new HashMap<>();
//
//            while (true) {
//                System.out.println("Here?");
////                    if (System.currentTimeMillis() - startTime > 500) {
////                        List<Integer> missingParts = new ArrayList<>();
////                        // Which parts are missing
////                        for (int i = 1; i == 2; i++) {
////                            if (!dataParts.containsKey(i)) {
////                                missingParts.add(i);
////                            }
////                        }
////
//////                        for (Integer missingSeq : missingParts) {
//////                            Packet resendP = Packet.buildRetransmitRequestPacket(peerAddress, peerPort, missingSeq);
//////                            channel.send(resendP.toBuffer(), router);
//////                        }
////                        startTime = System.currentTimeMillis();
////                    }
//
//                buf.clear();
//
//                System.out.println("Buf: " + buf);
//                channel.receive(buf);
//                System.out.println("HEre 1.2");
//                buf.flip();
//                System.out.println("HEre 1.3");
//                Packet respDataPart = Packet.fromBuffer(buf);
//                System.out.println("HEre 1.25: " + respDataPart);
//                System.out.println("Seq$: " + respDataPart.getSequenceNumber());
//                String currentPayload = new String(respDataPart.getPayload(), UTF_8);
//                Long seqLong = respDataPart.getSequenceNumber(); // conversion
//
//                dataParts.put(seqLong.intValue(), currentPayload);
//                System.out.println("Dataparts size: " + dataParts.size());
//                if (dataParts.size() == 1) {
//                    StringBuilder payloadBuilder = new StringBuilder();
//                    Set<Integer> keySet = dataParts.keySet();
//                    List<Integer> keysList = new ArrayList<>(keySet);
//                    Collections.sort(keysList);
//                    for (Integer k : keysList) {
//                        System.out.println("get k dataparts: " + dataParts.get(k));
//                        payloadBuilder.append(dataParts.get(k));
//                    }
//// Return an all-clear data message to the server
////                        Packet gotAllDataPacket = Packet.buildGotAllDataPacket(peerAddress, peerPort);
//                    System.out.println("String builder: " + payloadBuilder);
//                    System.out.println("Here 3");
////                    String msg2 = "confirmed";
////                    Packet gotAllDataPacket = new Packet.Builder()
////                            .setType(7)
////                            .setSequenceNumber(1L)
////                            .setPortNumber(serverAddress.getPort())
////                            .setPeerAddress(serverAddress.getAddress())
////                            .setPayload(msg2.getBytes())
////                            .create();
//
//                    Packet gotalldata = Packet.HandshakePacket(7, 100, serverAddress.getAddress(), serverAddress.getPort());
//                    channel.send(gotalldata.toBuffer(), router);
//                    // Finally, we can return the full payload
////                        logger.info("Got the full response from server");
//                    System.out.println("Got full response");
//                    System.out.println(payloadBuilder.toString());;
//
//                } else {
//                    continue;
//                }
//            }

//    private static String runClient1(SocketAddress routerAddress, InetSocketAddress serverAddress, String Sample_Request) throws IOException {
//
//        System.out.println("Channel opened, not connected");
//        System.out.println("Server Started: " + serverAddress);
//        System.out.println("-----------------------------");
//
//        try (DatagramChannel channel = DatagramChannel.open()) {
//
//            String msg = Sample_Request;
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
//
//
//
//            channel.configureBlocking(false);
//            Selector selector = Selector.open();
//            channel.register(selector, OP_READ);
//            System.out.println("waiting milliseconds3");
////            logger.info("Waiting {} milliseconds for the synack response", Config.RESPONSE_TIMEOUT_MILLIS);
//            selector.select(500);
//            Set<SelectionKey> keys = selector.selectedKeys();
//            if (keys.isEmpty()) {
//                return "No response after timeout";
//            }
//            System.out.println("Keys size: " + keys.size());
//            ByteBuffer buf = ByteBuffer.allocate(5000);
//            SocketAddress router = channel.receive(buf);
//            buf.flip();
//
//            if (selector.isOpen()) {
//                selector.close();
//                keys.clear();
//                channel.configureBlocking(true);
//            }
//
//            long startTime = System.currentTimeMillis();
//            Map<Integer, String> dataParts = new HashMap<>();
//
//            while (true) {
//                System.out.println("Here?");
////                    if (System.currentTimeMillis() - startTime > 500) {
////                        List<Integer> missingParts = new ArrayList<>();
////                        // Which parts are missing
////                        for (int i = 1; i == 2; i++) {
////                            if (!dataParts.containsKey(i)) {
////                                missingParts.add(i);
////                            }
////                        }
////
//////                        for (Integer missingSeq : missingParts) {
//////                            Packet resendP = Packet.buildRetransmitRequestPacket(peerAddress, peerPort, missingSeq);
//////                            channel.send(resendP.toBuffer(), router);
//////                        }
////                        startTime = System.currentTimeMillis();
////                    }
//                System.out.println("Here 1");
//                buf.clear();
//                System.out.println("HEre 1.1");
//                System.out.println("Buf: " + buf);
//                channel.receive(buf);
//                System.out.println("HEre 1.2");
//                buf.flip();
//                System.out.println("HEre 1.3");
//                Packet respDataPart = Packet.fromBuffer(buf);
//                System.out.println("HEre 1.25: " + respDataPart);
//                String currentPayload = new String(respDataPart.getPayload(), UTF_8);
//                Long seqLong = respDataPart.getSequenceNumber(); // conversion
//
//                dataParts.put(seqLong.intValue(), currentPayload);
//                System.out.println("Here 1.5");
//                System.out.println("Dataparts size: " + dataParts.size());
//                if (dataParts.size() == 1) {
//                    System.out.println("Here 2");
//                    StringBuilder payloadBuilder = new StringBuilder();
//                    Set<Integer> keySet = dataParts.keySet();
//                    List<Integer> keysList = new ArrayList<>(keySet);
//                    Collections.sort(keysList);
//                    for (Integer k : keysList) {
//                        System.out.println("get k dataparts: " + dataParts.get(k-1));
//                        payloadBuilder.append(dataParts.get(k));
//                    }
//// Return an all-clear data message to the server
////                        Packet gotAllDataPacket = Packet.buildGotAllDataPacket(peerAddress, peerPort);
//                    System.out.println("String builder: " + payloadBuilder);
//                    System.out.println("Here 3");
////                    String msg2 = "confirmed";
////                    Packet gotAllDataPacket = new Packet.Builder()
////                            .setType(7)
////                            .setSequenceNumber(1L)
////                            .setPortNumber(serverAddress.getPort())
////                            .setPeerAddress(serverAddress.getAddress())
////                            .setPayload(msg2.getBytes())
////                            .create();
//
//                    Packet gotalldata = Packet.HandshakePacket(7, 100, serverAddress.getAddress(), serverAddress.getPort());
//                                        channel.send(gotalldata.toBuffer(), router);
//                    // Finally, we can return the full payload
////                        logger.info("Got the full response from server");
//                    System.out.println("Got full response");
//                    return payloadBuilder.toString();
//
//                } else {
//                    continue;
//                }
//
//            }
//
//        }
//    }

//            //message that will be converted to bytes
//            String msg = Sample_Request;
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


// We just want a single response.
//            ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
//            SocketAddress router = channel.receive(buf);
//            buf.flip();
//            Packet resp = Packet.fromBuffer(buf);
//            System.out.println("Packet: " + resp);
//            System.out.println("Router: " + router);
//            String payload = new String(resp.getPayload(), StandardCharsets.UTF_8);
//            System.out.println("Payload: " + payload);
//            System.out.println("TYPE: "+resp.getType()+"seq#" + resp.getSequenceNumber() );
//            keys.clear();
