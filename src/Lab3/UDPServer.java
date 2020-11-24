package Lab3;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.charset.StandardCharsets.UTF_8;
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
            for (; ; ) {
                buf.clear();
                SocketAddress router = channel.receive(buf);
                // Parse a packet from the received raw data.
                buf.flip();
                Packet packet = Packet.fromBuffer(buf);
                //what does this flip do?
                buf.flip();
                String msg = null;
                //todo: Switch case for the various types of packets
                switch (packet.getType()) {
                    case 0:
                        System.out.println("Received a DATA Packet");
                        if (channel.isConnected()){
                            System.out.println("channel connected");;
                            String payload = new String(packet.getPayload(), UTF_8);
                            System.out.println("Packet: " + packet);
                            System.out.println("Payload: " + payload);
                            System.out.println("Router: " + router);
                            //do request processing --> Httpfs
                            //todo here
                            //how will request processing be done?
                            String[] request = payload.split(" ");
                            for(int i = 0; i< request.length; i++){
                                System.out.println(request[i]);
                            }
                            if (request[0].equals("GET")){

                                // do get request
                                msg =  getProcessing(request);
                                System.out.println("Got string from file, sending back to client");
                                //check if message is above 1024;
                                System.out.println("msg byte length "+msg.getBytes().length);
                                System.out.println("content size: " + msg.getBytes().length);


//                                if (msg.getBytes().length > 1013){
//                                    //determine how many packets you need
//                                    int num_of_pkts = (int) Math.ceil((double) msg.getBytes().length / 1013);
//                                    System.out.println("Number of packets: " + num_of_pkts);
//                                    //this should determine num of packets. How to remove error?
////                    break content down into different packets
//                                    List<Packet> packet_lists = Packet.packetList(0, packet.getPeerAddress(), packet.getPeerPort(), msg.getBytes(), num_of_pkts);
//                                    System.out.println("Number of packets that will be sent to client: " + packet_lists.size());
//                                    for (Packet pkt : packet_lists) {
//                                        System.out.println("Packet seq#: " + pkt.getSequenceNumber() + " pkt payload: " + pkt.getPayload());
//                                        channel.send(pkt.toBuffer(), router);
//                                    }
//                                }else{
//                                    Packet resp = packet.toBuilder().setType(3)
//                                        .setPayload(msg.getBytes())
//                                        .create();
//                                channel.send(resp.toBuffer(), router);
//                                }

                            }else if(request[1].equals("POST")){
                                //do post request
                            }

                            //send back file to client
                        }else{
                            System.out.println("Channel not connected");
                        }
                        break;

                    case 1:
                        System.out.println("Received an ACK Packet!");
                        System.out.println("seq# " + packet.getSequenceNumber());
                        channel.connect(router);
                        System.out.println("Datagram Channel connected");
                        System.out.println("3-way handshake complete");
                        System.out.println("-----------------------------");
                        break;
                    case 2:
                        // in the case of SYN packet, following fsm structure, needs to wait for ACK from client and send a SYN-ACK back to client
                        System.out.println("Received a SYN Packet");
                        Packet resp = packet.toBuilder().setType(3).setSequenceNumber(packet.getSequenceNumber()+1).create();
                        System.out.println("Sending SYN-ACK to client");
                        System.out.println("Pkt type: " + resp.getType()+" Seq#: " + resp.getSequenceNumber());
                        channel.send(resp.toBuffer(), router);

                        System.out.println("-----------------------------");
                        break;
                    case 4:
                        System.out.println("Received a NAK Packet");
                        break;
                    default:
                        throw new Error("Invalid data type. System Exiting");
                }

//_______________________________sending responses____________________________

                if(packet.getType() == 0) {
                    try {
                        channel.configureBlocking(false);
                        Selector selector = Selector.open();
                        channel.register(selector, OP_READ);
                        System.out.println("Test 1");
//                    logger.info("Waiting {} milliseconds for the synack response", Config.RESPONSE_TIMEOUT_MILLIS);
//                        selector.select(500);
                        Set<SelectionKey> keys = selector.selectedKeys();
//                        if (keys.isEmpty()) {
////                            System.out.println("Test 4");
//////                        logger.error("No response after timeout - was waiting for preData ack - aborting connection");
////                            return;
////                        }
                        System.out.println("Test 4");
                        if (selector.isOpen()) {
                            selector.close();
                            keys.clear();
                            channel.configureBlocking(true);
                        }
                        System.out.println("Test 2");

                        if (msg.getBytes().length > 1013) {
                            System.out.println("Test 3");
                            //determine how many packets you need
                            int num_of_pkts = (int) Math.ceil((double) msg.getBytes().length / 1013);
                            System.out.println("Number of packets: " + num_of_pkts);
                            //this should determine num of packets. How to remove error?
//                    break content down into different packets
                            List<Packet> packet_lists = Packet.packetList(0, packet.getPeerAddress(), packet.getPeerPort(), msg.getBytes(), num_of_pkts);

                            System.out.println("Number of packets that will be sent to client: " + packet_lists.size());
                            for (Packet pkt : packet_lists) {
                                System.out.println("Packet seq#: " + pkt.getSequenceNumber() + " pkt payload: " + pkt.getPayload());
                                channel.send(pkt.toBuffer(), router);
                                System.out.println("Sent!");
                            }
                        } else {
                            Packet resp = packet.toBuilder().setType(3)
                                    .setPayload(msg.getBytes())
                                    .create();
                            channel.send(resp.toBuffer(), router);
                        }

                        //____________________________________sent, waiting in response___________________
                        while (true) {
                            channel.configureBlocking(false);
                            selector = Selector.open();
                            channel.register(selector, OP_READ);
                            System.out.println("Waiting milliseconds");
                            selector.select(500);
                            keys = selector.selectedKeys();
                            if (keys.isEmpty()) {
                                continue;
                            }

                            buf.clear();
                            channel.receive(buf);
                            buf.flip();
                            Packet retransOrAck = Packet.fromBuffer(buf);
//                        7 == all data confirmed
                            if (retransOrAck.getType() == 7) {
                                System.out.println("Server confirmed all apckets were received");
//                            logger.info("Server confirmed it got all data packets - this send is complete.");
                                selector.close();
                                break;
                            } else {
                                System.out.println("Error failed to reransmit ");
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Failed to transmit data");
                    }
                }


            }

        }
    }




    private String getProcessing(String[] request) throws IOException {
        System.out.println("Entered get processing");

        if(request[1].contains("txt")){
            //text file processing
            //regex on request[1]?
            //replacing forward slash for now - will be fixed later on
//
//            try {
            Path path = Paths.get("src/Lab3/Files/hello");
            System.out.println("abs path: " + path.toAbsolutePath());
            System.out.println("Parent: " + path.getParent());
            System.out.println("Real Path: " + path.toRealPath());
            System.out.println("_______________________________");

            String filepath = request[1].replace("/", "");
            System.out.println(filepath);
            System.out.println("abs path to hello.txt : " + new java.io.File(filepath).getAbsolutePath());
            String abs_path = new java.io.File(filepath).getAbsolutePath();
//            System.out.println("abs path to hello.txt : " + new java.io.File("hello.txt").getAbsolutePath());
            System.out.println("user home : " + System.getProperty("user.home"));
            String user_home = System.getProperty("user.home");
            System.out.println("user.dir : " + System.getProperty("user.dir"));
            String user_dir = System.getProperty("user.dir");
            System.out.println("running from : " + new java.io.File(".").getAbsolutePath());
//
//                System.out.println(new java.io.BufferedReader(new java.io.FileReader("hello.txt")).readLine());
//                System.out.println(new java.io.BufferedReader(new java.io.FileReader("test.txt")).readLine());
//            } catch (Exception e) {
//                System.out.println("Not found");
//            }

            File file = new File("src/Lab3/Files/hello");
            System.out.println("file: " + file);
            if(file.exists()){
                System.out.println("File is a file!");
            }


//            System.out.println(System.getProperty("user.dir") + filepath);
//            String path = System.getProperty("user.dir") + filepath;
//            Path file_path = new File(path).toPath();
//            System.out.println("file path: " + file_path);
//            String mimetype = "";
//            try {
//                mimetype = Files.probeContentType(file_path);
//                //System.out.println("File type: " + mimetype);
//            }catch(IOException e){
//                e.printStackTrace();
//            }


//            System.out.println("user.dir + src/Lab3/Files/hello");
            String filepath1 = "src/Lab3/Files/hello";

            String mimetype = "";
            Path file_path = new File(filepath1).toPath();
            System.out.println(file_path);
            try {
                mimetype = Files.probeContentType(file_path);
                //System.out.println("File type: " + mimetype);
            }catch(IOException e){
                e.printStackTrace();
            }

//        BufferedReader in = new BufferedReader(new FileReader(System.getProperty("user.dir") + filename));
            try (BufferedReader br = new BufferedReader(new FileReader(filepath1))){
                StringBuilder sb = new StringBuilder();
                String line;
                for (line = br.readLine(); line != null; line = br.readLine()) {
                    sb.append(line + '\n');
                    //System.out.println("CONTENT OF FILE: " + sb);
                }
//            System.out.println("Stringbuffer read contents: "+sb.toString());
//                http_response = getResponse(ResponseLibrary.status_200, sb.toString(), http_request.getUser_agent(), mimetype);
                //send status OK!
                System.out.println("Contents: "+sb.toString() + " mimetype: " + mimetype);

                return sb.toString();
//                System.out.println("httpfs: httpresponse after get response : " + http_response);
            } catch (FileNotFoundException f){
                //http response
                System.out.println("Error 500");
//                http_response = server.error_response(ResponseLibrary.status_500, "File not found: " + path,  http_request.getUser_agent());
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        return null;
    }
}




//    private void listenAndServe(int port) throws IOException {
//        try (DatagramChannel channel = DatagramChannel.open()) {
//
//            // bind channel to inetaddress
//            channel.bind(new InetSocketAddress(port));
//            System.out.println("EchoServer is listening at: " + channel.getLocalAddress());
//            ByteBuffer buf = ByteBuffer
//                    .allocate(Packet.MAX_LEN)
//                    .order(ByteOrder.BIG_ENDIAN);
//            //idk what this for loop is
//            for (; ; ) {
//                buf.clear();
//                SocketAddress router = channel.receive(buf);
//                // Parse a packet from the received raw data.
//                buf.flip();
//                Packet packet = Packet.fromBuffer(buf);
//                buf.flip();
//
//                if (packet.getType() == 2){
//                    System.out.println("SYN received");
//                }else
//
//
//                String payload = new String(packet.getPayload(), UTF_8);
//                System.out.println("Packet: " + packet);
//                System.out.println("Payload: " + payload);
//                System.out.println("Router: " + router);
//
//
//                // Send the response to the router not the client.
//                // The peer address of the packet is the address of the client already.
//                // We can use toBuilder to copy properties of the current packet.
//                // This demonstrate how to create a new packet from an existing packet.
//                System.out.println("Sending SYN-ACK to router");
//                Packet resp = packet.toBuilder().setType(3)
//                        .setPayload(payload.getBytes())
//                        .create();
//                channel.send(resp.toBuffer(), router);
//
//            }
//        }
//    }