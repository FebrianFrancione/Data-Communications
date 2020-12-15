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

import prev.ResponseLibrary;
import prev.server;

import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.sort;

public class UDPServer {
	
	static String method;
	static String url;
	static String version;
	static String reqheader;
	static String reqBody;
	static String responseVersion = "HTTP/1.1";
	static String responseHeader;
	static String responseBody;
	static String serverReply;
	static String status;
	static String phrase;
    public static void main(String[] args) throws IOException {
        int port = 8007;
        UDPServer server = new UDPServer();
        server.threeWayListen(port);
        server.listenAndServe(port);
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
                buf.flip();
                String msg = null;
                System.out.println("in 3way listen server received packet type  " + packet.getType());
                Selector selector;
                Set<SelectionKey> keys;
                boolean timeout = true;
                //synACK
                if (packet.getType() == 2) {
                	System.out.println("Received a SYN Packet");
                    Packet resp = packet.toBuilder().setType(3).setSequenceNumber(packet.getSequenceNumber()+1).create();
                    System.out.println("Sending SYN-ACK to client");
                    System.out.println("Pkt type: " + resp.getType()+" Seq#: " + resp.getSequenceNumber());
                    channel.send(resp.toBuffer(), router);
                    System.out.println("-----------------------------");
                    do {
                    	//resending synACK to client if not already acked
                    	channel.configureBlocking(false);
                    	selector = Selector.open();
                    	channel.register(selector, OP_READ);
                    	System.out.println("Waiting for response from client for SYNACK packet rcv");
                    	selector.select(5000);
                    	keys = selector.selectedKeys();
                        if(keys.isEmpty()) {
                        	System.out.println("No response within timeout, sending Syn-ack back to client again");
                        	channel.send(resp.toBuffer(), router);
                        }
                        else {
                        	keys.clear();
                            selector.close();
                        	buf.clear();
                            channel.receive(buf);
                            buf.flip();
                            Packet handshakeACK = Packet.fromBuffer(buf);
                            //System.out.println("Handshake! ACK " +handshakeACK.getType() + " "+ handshakeACK.getSequenceNumber());
                            if(handshakeACK.getType() == 1 && handshakeACK.getSequenceNumber() == (resp.getSequenceNumber() + 1)) {
                            	System.out.println("Received an ACK Packet for Handshake!");
                                System.out.println("seq# " + handshakeACK.getSequenceNumber());
                                //channel.connect(router);
                                System.out.println("Datagram Channel connected");
                                System.out.println("3-way handshake complete");
                                System.out.println("-----------------------------");
                            }
                            timeout = false;
                        }
                    }while(timeout);
                }
        } 
    }

    private String getProcessing(String[] request) throws IOException {
        System.out.println("Entered get processing");
        String contentofFile = null;
        String responseget = null;
        if(request[1].contains("txt")){
//            try {
            Path path = Paths.get("src/Lab3/Files/hello.txt");
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

            File file = new File("src/Lab3/Files/hello.txt");
            System.out.println("file: " + file);
            if(file.exists()){
                System.out.println("File is a file!");
            }
            else {
            	System.out.println("File not found error 404");
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
            String filepath1 = "src/Lab3/Files/hello.txt";

            String mimetype = "";
            Path file_path = new File(filepath1).toPath();
            System.out.println("file path: " + file_path);
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
                contentofFile = sb.toString() + " mimetype: " + mimetype + " " + "Status 200 Ok";
                
                System.out.println("Contents: "+sb.toString() + " mimetype: " + mimetype);
                System.out.println("Status code 200 Ok");

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

        return contentofFile;
    }
    
    private void postProcess(String[] request) {
        String file_path = "/Users/marjanaupama/git/Data-Communications/src/Lab3/Files" + request;
        File file_directory = new File(file_path);
        //System.out.println(file_directory);
        String local_path = file_directory.getParent();
        Path path = new File(file_path).toPath();
        File file_folder = new File(local_path);
        File file = new File("/Users/marjanaupama/git/Data-Communications/src/Lab3/Files" + request);
        if(file.exists()) { 
			PrintWriter write = null;
			
			try {
				write = new PrintWriter(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			write.close();
			String status = "200";
            responseHeader = reqheader + "\\r\\n" + "Content-length: " + reqBody.length() + "\\r\\n" + "Content-Type: text/html";
            responseBody = reqBody;
        }
        else {
        	try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			PrintWriter write = null;
			try {
				write = new PrintWriter(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			write.print(reqBody);
			write.close();
			status = "201";
            responseHeader = reqheader + "\\r\\n" + "Content-length: " + reqBody.length() + "\\r\\n" + "Content-Type: text/html";
            responseBody = reqBody;
		}
    }

    private void listenAndServe(int port) throws IOException {
        try (DatagramChannel channel = DatagramChannel.open()) {
        	// bind channel to inetaddress
            channel.bind(new InetSocketAddress(port));
            ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN).order(ByteOrder.BIG_ENDIAN);
            System.out.println("Server will  now serve requests at  " + channel.getLocalAddress());
            for (; ; ) {
            	buf.clear();
            	//buf = ByteBuffer.allocate(Packet.MAX_LEN);
                SocketAddress router = channel.receive(buf);
                // Parse a packet from the received raw data.
                buf.flip();
                Packet packet = Packet.fromBuffer(buf);
                buf.flip();
                String msg = null;
                System.out.println("in server received packet type  " + packet.getType());
                Selector selector;
                Set<SelectionKey> keys;
                //data packt
                if(packet.getType() == 0) {
                	System.out.println("Received a DATA Packet");
                    String payload = new String(packet.getPayload(), UTF_8);
                    System.out.println("Packet: " + packet);
                    System.out.println("Payload: " + payload);
                    System.out.println("Router: " + router);
                    //do request processing --> Httpfs
                    String[] request = payload.split(" ");
                    for(int i = 0; i< request.length; i++){
                        System.out.println(request[i]);
                    }
                    if (request[0].equals("GET")){
                        // do get request
                        msg =  getProcessing(request);
                        System.out.println("Got string from file, sending back to client");
                        System.out.println("msg byte length "+msg.getBytes().length);
                        System.out.println("content size: " + msg.getBytes().length);
                    }else if(request[1].equals("POST")){
                         postProcess(request);
                         System.out.println("Written to file, sending back to client");
                         System.out.println("msg byte length "+msg.getBytes().length);
                         System.out.println("content size: " + msg.getBytes().length);
                    }
                    try {
                    	boolean mainack = true;
                    	do {
                    		//ack for data pack rcvd 
                        	Packet resp = packet.toBuilder().setType(1).setSequenceNumber(packet.getSequenceNumber()).create();
                        	channel.send(resp.toBuffer(), router);
                        	System.out.println("Sent an ack back to client after receiving a data packet");
                    		channel.configureBlocking(false);
                        	selector = Selector.open();
                        	channel.register(selector, OP_READ);
                        	System.out.println("Waiting for response from client if data packet ack was not received");
                        	selector.select(5000);
                        	keys = selector.selectedKeys();
                            if(keys.isEmpty()) {
                            	mainack = false;
                            	System.out.println("No response that means we can move forward");
                            	//ack successfiully sent
                            	//determine how many packets you need
                            	int num_of_pkts = (int) Math.ceil((double) msg.getBytes().length / 1013);
                            	//int num_of_pkts = 2;
                               if (num_of_pkts >  1) {
                                    System.out.println("num of packets > 1");
                                    System.out.println("Number of packets: to be sent to the client " + num_of_pkts);
                                    //sending an ack with num of packets to let the client know how many packets to expect and resend until client acks
                                    boolean timer = true;
                                    do {
                                    	Packet num_pak_2client = packet.toBuilder().setType(num_of_pkts).setSequenceNumber((resp.getSequenceNumber() + 200)).create();
                                    	System.out.println("num pack set type and seq num " + num_pak_2client.getType() +" " + num_pak_2client.getSequenceNumber());
                                    	
                                    	channel.send(num_pak_2client.toBuffer(), router);
                                    	channel.configureBlocking(false);
                                    	selector = Selector.open();
                                    	channel.register(selector, OP_READ);
                                    	System.out.println("Waiting for response from client for NUM of pktst to be expected acknowledge");
                                    	selector.select(5000);
                                    	keys = selector.selectedKeys();
                                        if(keys.isEmpty()) {
                                        	System.out.println("No response within timeout, sending num of pkts back to client again");
                                        	channel.send(num_pak_2client.toBuffer(), router);
                                        }
                                        else{ 
                                        	//num of packet ack rcvd by client
                                        	//buf = ByteBuffer.allocate(Packet.MAX_LEN); notneeded
                                            router = channel.receive(buf);
                                            buf.flip();
                                            Packet numAck = Packet.fromBuffer(buf);
                                            if(numAck.getSequenceNumber() == num_pak_2client.getSequenceNumber() && numAck.getType() == num_pak_2client.getType()) {
                                            	timer = false;
                                            }
                                        }
                                    } while(timer);
                                    List<Packet> packet_lists = Packet.packetList(0, packet.getPeerAddress(), packet.getPeerPort(), msg.getBytes(), num_of_pkts);
                                    System.out.println("Number of packets that will be sent to client: " + packet_lists.size());
                                    for (Packet pkt : packet_lists) {
                                        //resend
                                        boolean timeout = true;
                                        do {
                                        	System.out.println("Packet seq#: " + pkt.getSequenceNumber() + " pkt payload: " + pkt.getPayload());
                                            channel.send(pkt.toBuffer(), router);
                                            channel.configureBlocking(false);
                                        	selector = Selector.open();
                                        	channel.register(selector, OP_READ);
                                        	System.out.println("Waiting for response from client for multiple packet with indiivudal acks");
                                        	selector.select(5000);
                                        	keys = selector.selectedKeys();
                                            if(keys.isEmpty()) {
                                            	System.out.println("No response within timeout, sending individiual of multiple packs back to client again");
                                            	channel.send(pkt.toBuffer(), router);
                                            }
                                            else{ 
                                            	boolean ackTimer = true;
                                            	do {
                                            		keys.clear();
                                                    selector.close();
                                                    //channel.configureBlocking(true);
                                                    buf = ByteBuffer.allocate(Packet.MAX_LEN);
                                                    router = channel.receive(buf);
                                                    buf.flip();
                                                    Packet responseDataACK = Packet.fromBuffer(buf);
                                                    buf.flip();
                                                    if(responseDataACK.getType() == 1 && responseDataACK.getSequenceNumber() == pkt.getSequenceNumber()) {
                                                    	ackTimer = false;
                                                    	System.out.println("Ack by clinet were succesfully sent for individiual packets ");
                                                    }
                                            	}while(ackTimer);

                                            }
                                        }while(timeout);
                                        System.out.println("Sent!");
                                    }
                                }
                               //single data pakcet response by server
                               else {
                                    Packet response = packet.toBuilder().setType(0).setSequenceNumber((packet.getSequenceNumber() +1))
                                            .setPayload(msg.getBytes())
                                            .create();
                                    channel.send(response.toBuffer(), router);
                                    boolean resend = true;
                                    do {
                                    	channel.configureBlocking(false);
                                    	selector = Selector.open();
                                    	channel.register(selector, OP_READ);
                                    	System.out.println("Waiting for response from client for single processed data packet to ACK");
                                    	selector.select(5000);
                                    	keys = selector.selectedKeys();
                                        if(keys.isEmpty()) {
                                        	System.out.println("No response within timeout, sending single processed data pack back to client again");
                                        	
                                        	channel.send(response.toBuffer(), router);
                                        }
                                        else {
                                        	router = channel.receive(buf);
                                            buf.flip();
                                            Packet processedDataACK = Packet.fromBuffer(buf);
                                            if(processedDataACK.getSequenceNumber() == response.getSequenceNumber() && processedDataACK.getType() == 1) {
                                            	//channel.configureBlocking(true);
                                            	resend = false;
                                            	
                                            } //processed data packet rcvd by client
                                        }
                                    } while(true);
                                }
                            }
                            else {
                            	channel.send(resp.toBuffer(), router);
                                }
                    		
                    	} while(mainack);
                    	
                    	boolean timerout = true;
                    	router = channel.receive(buf);
                        buf.flip();
                        Packet finPack = Packet.fromBuffer(buf);
                    	if(finPack.getType() == 4 && finPack.getSequenceNumber() == 100) {
                    		Packet resp = packet.toBuilder().setType(5).setSequenceNumber(finPack.getSequenceNumber()+1).create();
                            System.out.println("Sending FYN-ACK to client");
                            System.out.println("Pkt type: " + resp.getType()+" Seq#: " + resp.getSequenceNumber());
                            channel.send(resp.toBuffer(), router);
                            System.out.println("-----------------------------");
                    	do {
                        	//resending FInACK to client if not already acked
                        	channel.configureBlocking(false);
                        	selector = Selector.open();
                        	channel.register(selector, OP_READ);
                        	System.out.println("Waiting for response from client for SYNACK packet rcv");
                        	selector.select(5000);
                        	keys = selector.selectedKeys();
                            if(keys.isEmpty()) {
                            	System.out.println("No response within timeout, sending FIn-ack back to client again");
                            	channel.send(resp.toBuffer(), router);
                            }
                            else {
                            	keys.clear();
                                selector.close();
                            	buf.clear();
                                channel.receive(buf);
                                buf.flip();
                                Packet endACK = Packet.fromBuffer(buf);
                                if(endACK.getType() == 1 && endACK.getSequenceNumber() == (resp.getSequenceNumber() + 1)) {
                                	System.out.println("Received an ACK Packet for end connection!");
                                    System.out.println("seq# " + endACK.getSequenceNumber());
                                    System.out.println("Datagram Channel disconnected");
                                    channel.close();
                                }
                                timerout = false;
                            }
                        }while(timerout);
                    	//}

                          }
                    	}
                    catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Failed to transmit data");
                    }
                }
            }
        	
        }
    }
    
}
