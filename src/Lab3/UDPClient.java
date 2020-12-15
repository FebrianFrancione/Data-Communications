package Lab3;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.*;

import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.charset.StandardCharsets.UTF_8;

public class UDPClient {
	private final static String VERSION = "HTTP/1.1";
	private static URL url;
	private static URI uri;
	private final static int PORT  = 8080;
	private static String USER_AGENT = "HTTP/1.1";
	private static Socket socket;
	static Pattern pattern;
    static Matcher matcher;
    public static void main(String[] args) throws IOException, URISyntaxException {
        String routerHost = "localhost";
        int routerPort = 3000;
        String serverHost = "localhost";
        int serverPort = 8007;
        SocketAddress routerAddress = new InetSocketAddress(routerHost, routerPort);
        InetSocketAddress serverAddress = new InetSocketAddress(serverHost, serverPort);

        //String Sample_Request = "GET /hello.txt HTTP/1.1\r\nUser-Agent: Concordia\r\n\r\n";
        //String sample_method = "GET";
        String Sample_Request = "post -d \"hello everyone\" http://localhost:8080/randmFyl.txt";
        String sample_method = "POST";
        threeWayHandshake(routerAddress, serverAddress);
        runClient(routerAddress, serverAddress, Sample_Request);
        if (sample_method.equals("GET")) {
            GET_Method(routerAddress, serverAddress, Sample_Request);
            
        } else {
            POST_Method(routerAddress, serverAddress, Sample_Request);
        }
    }
    
  //handshake to only establish connection betwn client and server. data packets CAN BE SENT AFTER SYN-ACK RECEIVED
    private static void threeWayHandshake(SocketAddress routerAddress, InetSocketAddress serverAddress) throws IOException {
        //channel set to blocking by default
    	boolean timeout = true;
        try (DatagramChannel channel = DatagramChannel.open()) {
            // Mapping for pkt types are as follows: 0 -> Data, 1-> ACK, 2-> SYN, 3-> SYN-ACK
             /* seq is x, sending to server, wehich replies with Ack=x+1 and SYn=y (server's sequence number start). Ack is thus sent from client with y+1.
             Note that the x+1 and y+1 sent by cleint/server respectively will indicate what they EXPECT the NEXT SEQUENCE NUMBER to be.*/
             
            Packet p = Packet.HandshakePacket(2, 100, serverAddress.getAddress(), serverAddress.getPort());
            System.out.println("Sending SYN packet to router");
            System.out.println("Pkt type: " + p.getType() + " Seq#: " + p.getSequenceNumber());
            //when sending packet through channel, packet must pass buffer
            channel.send(p.toBuffer(), routerAddress);
            System.out.println("-----------------------------");
            ByteBuffer buf;
            SocketAddress router;
            //channel set to non-blocking
            channel.configureBlocking(false);
            Selector selector;
            //key = token representing the registration of a channel with selector (selector maintains 3 sets of keys)
            //1- key set- keys with registered channel, 2- selected key set: keys with channels ready for at least one operation 3- Cancelled key set: cancelled keys, channels not deregistered
            Set<SelectionKey> keys;
            
            do {
            	
            	//resending SYN if not rcvd by server
            	selector  = Selector.open();
                channel.register(selector, OP_READ);
                System.out.println("Waiting for response from server for syn handshake");
                selector.select(5000);
                keys = selector.selectedKeys();
            	if(keys.isEmpty()) {
                    System.out.println("No response after timeout resending SYN packet again");
                    channel.send(p.toBuffer(), routerAddress);
            	}
            	else {
            		//received SYNACK packet
            		buf = ByteBuffer.allocate(Packet.MAX_LEN);
            		//buf.clear();
                    router = channel.receive(buf);
                    //Prepare the buffer to read data by channel-writes or relative gets(limit=position, positiin=0)
                    buf.flip();
                    Packet resp = Packet.fromBuffer(buf);
                    if (resp.getType() != 3) {
                        System.out.println("A SYN-ACK packet was expected from server for handshake.");
                        System.exit(0);
                    } else {
                        System.out.println("Received a SYN-ACK packet from server!");
                        System.out.println("Seq#" + resp.getSequenceNumber());
                      //clear the buffer, prepare buffer for writing data by channel-reads pr relative puts(lim=capacity, post=0)
                        keys.clear();
                        selector.close();
                        
                        //channel.configureBlocking(true); changed 3.57pm
                        //creating ACK packet
                        Packet ACK_pkt = Packet.HandshakePacket(1, resp.getSequenceNumber() + 1, serverAddress.getAddress(), serverAddress.getPort());
                        System.out.println("Sending ACK packet to router");
                        System.out.println("Pkt type: " + ACK_pkt.getType() + " Seq#: " + ACK_pkt.getSequenceNumber());
                        channel.send(ACK_pkt.toBuffer(), routerAddress);
                        System.out.println("-----------------------------");
                        System.out.println("3 Way hanshake complete");
                    }
            		timeout = false;
            	}
            }while(timeout);
            keys.clear();
            selector.close();
            channel.disconnect();
        }
    }
    private static String GET_Method(SocketAddress routerAddress, InetSocketAddress serverAddress, String Sample_Request) throws IOException, URISyntaxException {
    	String temp;
		int skip = 0;
		//url = new URL(host);		
		//System.out.println(url.getHost() + "  hello " + url.getFile());
		String keyVal = "";
		
		String[] arr = Sample_Request.split(" ");
		url = new URL(arr[arr.length - 1]);
		//System.out.println(arr.length );
		for(int i = 0; i<arr.length; i++) {
			if(arr[i].equals("-h")) {
				//System.out.println(arr[i+1].substring(0,1).equals("\""));	
				if(arr[i+1].substring(0,1).equals("\"")) {
					//if  key value exists
					keyVal = keyVal + arr[i+1] + "\r\n";
					keyVal = keyVal.replace("\"", "");	
				}
			}
		}
		//socket = new Socket(uri.getHost(), PORT);
		//BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		//PrintWriter writer = new PrintWriter(socket.getOutputStream());
		String payload = "GET " + url.getFile() + " "+ (String) VERSION+ "\r\n" + USER_AGENT+ "\r\n" + keyVal+ "\r\n\r\n";
		//writer.flush();
		/*while ((temp = reader.readLine()) != null) {
			if(skip > 8) {
				System.out.println(temp);
			}
			else {
				skip ++;
			}
		}
		writer.close();
		reader.close();
		socket.close();*/
		return payload;
    }
    private static String POST_Method(SocketAddress routerAddress, InetSocketAddress serverAddress, String request) throws URISyntaxException, UnknownHostException, IOException {
    	pattern = Pattern.compile(".*-v.*");
		matcher = pattern.matcher(request);
		int contentLength = 0;
		String entityBody = "";
		String keyValue = "";
		String host = "";
		String temp = "";
		int skip = 0;
		String arr[] = request.split(" ");
		url = new URL(arr[arr.length - 1]);
		//host = uri.getHost();
		//System.out.println(uri.toURL().getFile());
		for(int i = 0; i < arr.length; i++) {
			//System.out.println(arr[i] + " arr[i] " +  arr.length);
			if(arr[i].equals("-h")) {
				if(arr[i+1].substring(0,1).equals("\"")) {
					//if  key value exists
					keyValue = keyValue + arr[i+1] + "\r\n";
					keyValue = keyValue.replace("\"", "");	
					System.out.println(keyValue);
				}
			}
			else if(arr[i].equals("-d")) {
				for(int j = i+1; j < arr.length; j++) {
					if(!arr[j].equals(arr[arr.length - 1])) {
						entityBody = entityBody + " "+ arr[j];
						entityBody = entityBody.replace("'", "");
					}
				}
			}
		}
		entityBody = entityBody.substring(1, entityBody.length());
		contentLength = entityBody.length();
		keyValue = keyValue + "Content-Length: " + contentLength;
		//socket = new Socket(host, PORT);
		//BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		//PrintWriter writer = new PrintWriter(socket.getOutputStream());
		//writer.write("POST " + uri.toURL().getFile()+ " " +(String) VERSION+ "\r\n" + keyValue+ "\r\n\r\n" + entityBody);
		String payload = "POST " + url.getFile()+ " " +(String) VERSION+ "\r\n" + keyValue+ "\r\n\r\n" + entityBody;
		/*writer.flush();
		if(matcher.find() == true) {
			while((temp = reader.readLine()) != null) {
				System.out.println(temp);
			}
		}
		else{
			while ((temp = reader.readLine()) != null) {
				if(skip > 8) {
					System.out.println(temp);
				}
				else {
					skip ++;
				}
			}
		}
		writer.close();
		reader.close();
		socket.close();*/
		return payload;
    }
    private static void runClient(SocketAddress routerAddress, InetSocketAddress serverAddress, String Sample_Request) throws IOException {
    	try (DatagramChannel channel = DatagramChannel.open()) {
    		ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
            SocketAddress router;
            //channel set to non-blocking
            channel.configureBlocking(false);
            Selector selector;
            Set<SelectionKey> keys;
          //for single data packet
            System.out.println("sample request: " + Sample_Request);
            System.out.println("in udp client after completing handshake");
            //create new packet
            System.out.println("Sending packet to router for sending to server");
            Packet p1 = new Packet.Builder()
                    .setType(0)
                    .setSequenceNumber(1L)
                    .setPortNumber(serverAddress.getPort())
                    .setPeerAddress(serverAddress.getAddress())
                    .setPayload(Sample_Request.getBytes())
                    .create();
            //sending single data packet
            channel.send(p1.toBuffer(), routerAddress);
            System.out.println("Sending: " + Sample_Request + " to router at: " + routerAddress + "form udp client" + "packet type " + p1.toString() +" "+ p1.getType());
            channel.configureBlocking(false);
        	//selector = Selector.open();
        	//channel.register(selector, OP_READ);
        	//selector.select(5000);
        	//keys = selector.selectedKeys();
        	boolean repeat = true;
        	
            do {
            	selector = Selector.open();
            	channel.register(selector, OP_READ);
            	System.out.println("Waiting for ACK response from server for data packet rcv");
            	selector.select(5000);
            	keys = selector.selectedKeys();
                if (keys.isEmpty()) {
                    System.out.println("No response for data packet after timeout resending data req packet to server again");
                    channel.send(p1.toBuffer(), routerAddress);
                    //selector.close();
                    //keys.clear();
                } 
                else {
                	// ACK packet from server after sample request is sent by client
                	keys.clear();
                    selector.close();
                    //channel.configureBlocking(true);
                    buf = ByteBuffer.allocate(Packet.MAX_LEN);
                    router = channel.receive(buf);
                    buf.flip();
                    Packet responseACK = Packet.fromBuffer(buf);
                    System.out.println("response ack from from server to client with seq and packet type" + responseACK.getSequenceNumber() + " " +responseACK.getType() );
                  
                    if(responseACK.getType() == 1 && responseACK.getSequenceNumber() == p1.getSequenceNumber()) { 
    					repeat = false;
    					System.out.println("Response to sample request by client was replied with an ack from server ");
    					//buf.clear();
    				}
                }
            } while (repeat);
            boolean timer  = true;
            do {
            	channel.configureBlocking(false);
                selector = Selector.open();
            	channel.register(selector, OP_READ);
            	System.out.println("Waiting for DATA PRocessed packet from server for sample request");
            	selector.select(5000);
            	keys = selector.selectedKeys();
                if (keys.isEmpty()) {
                    System.out.println("No response for data processed packet yet, waiting for server to process get/post request");
                }
                //a single packet OR multiple packets is about to be rcvd
                else {
                	Map<Integer, String> dataParts = new HashMap<>();
                	System.out.println("Server sent processed get/post packet/packets ");
                	buf = ByteBuffer.allocate(Packet.MAX_LEN);
                    router = channel.receive(buf);
                    buf.flip();
                    Packet sampleResp = Packet.fromBuffer(buf);
                    System.out.println("Sample RESPONSE GET SEQ NUM AND GET TYPE" + sampleResp.getSequenceNumber() + " " + sampleResp.getType());
                    //if multiple packets are about to be rcvd
                    //&& sampleResp.getSequenceNumber() != p1.getSequenceNumber()
                	if(sampleResp.getType() != 0 && sampleResp.getType() != 1 && sampleResp.getSequenceNumber() == (p1.getSequenceNumber() + 200)) {
                		System.out.println("receinvng num of paktes form server  " + sampleResp.getType());
                		Packet numAck = sampleResp.toBuilder().setType(sampleResp.getType()).setSequenceNumber(sampleResp.getSequenceNumber()).create();
                    	channel.send(numAck.toBuffer(), routerAddress);
                    	//getType() here implies the num of packets the client is going to expect
                    	int numPackets = numAck.getType();
                    	for(int i = 0; i < numPackets; i++) {
                    		//buf = ByteBuffer.allocate(Packet.MAX_LEN);err
                            router = channel.receive(buf);
                            buf.flip();
                            Packet pkt_data = Packet.fromBuffer(buf);
                            String payload_chunk = new String(pkt_data.getPayload(), UTF_8);
                            Long seqLong = pkt_data.getSequenceNumber(); // conversion
                            dataParts.put(seqLong.intValue(), payload_chunk);
                            
                            System.out.println("multi packets seq and type " +pkt_data.getSequenceNumber() +" "+ pkt_data.getType());
                          //send ack
                            boolean individualtimer = true;
                            do {
                            	Packet ackP = pkt_data.toBuilder().setType(1).create();
                                channel.send(ackP.toBuffer(), routerAddress);
                            	System.out.println("Sent an ack back to client after receiving a data packet");
                        		channel.configureBlocking(false);
                            	selector = Selector.open();
                            	channel.register(selector, OP_READ);
                            	System.out.println("Waiting to see if server is trying to send data packs again or not");
                            	selector.select(5000);
                            	keys = selector.selectedKeys();
                            	if(keys.isEmpty()) {
                            		individualtimer = false;
                            	}
                            	else {
                            		System.out.println("in individual timer client when ack was successfully rcvd by server for multi pcks");
                            		continue;
                            		
                            	}
                            }while(individualtimer);
                    	}
                    	StringBuilder final_payload = new StringBuilder();
                        Set<Integer> keySet = dataParts.keySet();
                        List<Integer> keysList = new ArrayList<>(keySet);
                        Collections.sort(keysList);
                        for (Integer k : keysList) {
                            System.out.println("part: "+dataParts.get(k));
                            final_payload.append(dataParts.get(k));
                            System.out.println("final paylozd " + final_payload);
                        }
                	}
                	//single data packet by server
                	else {
                		//call get/post method to process from cleint side
                		System.out.println("single get packets processed by server ");
                		//maybe a loop to make sure serve get is or fin?
                		boolean individualtimer = true;
                		 router = channel.receive(buf);
                         buf.flip();
                         Packet pkt_data = Packet.fromBuffer(buf);
                         System.out.println("pkt data seq and data type " + pkt_data.getSequenceNumber() + " " + pkt_data.getType());
                         String payload = new String(pkt_data.getPayload(), UTF_8);
                         System.out.println("----Response from server ");
                         System.out.println("Server response: payload " + payload);
                         Packet ackP = pkt_data.toBuilder().setType(1).create();
                         if(pkt_data.getType() == 0 && pkt_data.getSequenceNumber() == (p1.getSequenceNumber() +1)) {
                        	 //display httpc
                        	 //send ack
                        	 channel.send(ackP.toBuffer(), routerAddress);
                         }
                         //listening for data packets and send ack repeatedly to server
                         do {
                        	channel.configureBlocking(false);
                            selector = Selector.open();
                         	channel.register(selector, OP_READ);
                         	System.out.println("Seeing if server receivs the singular data ack or not from client");
                         	selector.select(5000);
                         	keys = selector.selectedKeys();
                         	if(keys.isEmpty()) {
                         		//channel.configureBlocking(true);
                         		individualtimer = false;
                         		selector.close();
                                keys.clear();
                         		System.out.println("Sin if keys empty cvlient");
                         	}
                         	else {
                         		System.out.println("in if keys not empty client");
                         		router = channel.receive(buf);
                                buf.flip();
                                Packet rcvd_data = Packet.fromBuffer(buf);
                                if(rcvd_data.getType() == 0 && rcvd_data.getSequenceNumber() == (p1.getSequenceNumber() + 1)) {
                                	channel.send(ackP.toBuffer(), routerAddress);
                                	//selector.close();
                                	//keys.clear();
                                }
                         	}
                         }while(individualtimer);
                	}
                	timer = false;
                }
            } while(timer);
            boolean bool = true;
            Packet pFin = Packet.endHandshakePacket(4, 100, serverAddress.getAddress(), serverAddress.getPort());
            channel.send(pFin.toBuffer(), routerAddress);
            do {
            	//resending FIN if not rcvd by server
            	selector  = Selector.open();
                channel.register(selector, OP_READ);
                System.out.println("Waiting for response from server for FIN END");
                selector.select(5000);
                keys = selector.selectedKeys();
            	if(keys.isEmpty()) {
                    System.out.println("No response after timeout resending fIN packet again");
                    channel.send(pFin.toBuffer(), routerAddress);
            	}
            	else {
            		//received FINACK packet
            		buf = ByteBuffer.allocate(Packet.MAX_LEN);
            		//buf.clear();
                    router = channel.receive(buf);
                    buf.flip();
                    Packet resp = Packet.fromBuffer(buf);
                    if (resp.getType() != 5) {
                        System.out.println("A FIN-ACK packet was expected from server for handshake.");
                        System.exit(0);
                    } else {
                        System.out.println("Received a FIN-ACK packet from server!");
                        System.out.println("Seq#" + resp.getSequenceNumber());
                        keys.clear();
                        selector.close();
                        Packet ACK_pkt = Packet.HandshakePacket(1, resp.getSequenceNumber() + 1, serverAddress.getAddress(), serverAddress.getPort());
                        		//Packet.HandshakePacket(1, resp.getSequenceNumber() + 1, serverAddress.getAddress(), serverAddress.getPort());
                        System.out.println("Sending ACK  for finack packet to router");
                        System.out.println("Pkt type: " + ACK_pkt.getType() + " Seq#: " + ACK_pkt.getSequenceNumber());
                        channel.send(ACK_pkt.toBuffer(), routerAddress);
                        System.out.println("-----------------------------");
                        System.out.println("Connection ended");
                    }
            		bool = false;
            	}
            }while(bool);
            
            keys.clear();
            selector.close();
            
    		
    	}
    }
       

}