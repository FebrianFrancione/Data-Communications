package Lab3;

import prev.ResponseLibrary;
import prev.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.file.Files;
import java.nio.file.Path;

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
            for (; ; ) {
                buf.clear();
                SocketAddress router = channel.receive(buf);
                // Parse a packet from the received raw data.
                buf.flip();
                Packet packet = Packet.fromBuffer(buf);
                //what does this flip do?
                buf.flip();

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
                                getProcessing(request);

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
            }

        }
    }


    private void getProcessing(String[] request){
        System.out.println("Entered get processing");

        if(request[1].contains("txt")){
            //text file processing
            //regex on request[1]?

            //replacing forward slash for now - will be fixed later on
            String filepath = request[1].replace("/", "\\");
            System.out.println(filepath);
            System.out.println(System.getProperty("user.dir") + filepath);
            String path = System.getProperty("user.dir") + filepath;
            Path file_path = new File(path).toPath();
            System.out.println("file path: " + file_path);
            String mimetype = "";
            try {
                mimetype = Files.probeContentType(file_path);
                //System.out.println("File type: " + mimetype);
            }catch(IOException e){
                e.printStackTrace();
            }
            File file = new File(path);


            if(file.isFile()){
                System.out.println("File is a file!");
            }

            if(!Files.isReadable(file_path)) {
                System.out.println("403 error");
//                return server.error_response(ResponseLibrary.status_403, "Forbidden", http_request.getUser_agent());
            } else if(!file.isFile()) {
                System.out.println("httpfs 404: not found");
//                return server.error_response(ResponseLibrary.status_404, "Not Found", http_request.getUser_agent());
            }



        }
//        BufferedReader in = new BufferedReader(new FileReader(System.getProperty("user.dir") + filename));


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

