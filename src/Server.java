import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

//Class to represent an instance of our Server
public class Server {

	//Create a datagram socket on a random port
	private DatagramSocket RandomSocket(){
		DatagramSocket UDPSocket = null;
		//Determine random number for port
		int RandomPort = 1024 + (int)(Math.random() * ((65535-1024) + 1));
		try {
			//Try to build UDP socket
			UDPSocket = new DatagramSocket (RandomPort);
		} catch (SocketException e) {
			//If socket not available, just try again 
			UDPSocket = RandomSocket();
		}
		return UDPSocket;
		
	}
	
	
	//Builds out welcome socket
	//Here we use a random number since assignment specifies we lose marks for hardcoding ports
	//Random number implementation here also makes it easy to retry on fail
	private ServerSocket BuildWelcomeSocket(){
		ServerSocket WelcomeSocket = null;
		//Get a random port
		int RandomPort = 1024 + (int)(Math.random() * ((65535-1024) + 1));
		try{
			//Try to build welcome TCP socket
			WelcomeSocket = new ServerSocket(RandomPort);
		} catch(BindException e){
			//If socket is taken try again
			WelcomeSocket = BuildWelcomeSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//Shouldnt reach this block
			e.printStackTrace();
		}
		return WelcomeSocket;
	}

	
	//Launches the server
	public void RunServer (){
		//Create welcome socket and print out its port
		ServerSocket WelcomeSocket = BuildWelcomeSocket();
		System.out.println(WelcomeSocket.getLocalPort());
		try {
			//Wait for a client to connect
			Socket Connection = WelcomeSocket.accept();
			
			//Setup input/output streams
			BufferedReader inFromClient = 
					new BufferedReader(new
					InputStreamReader(Connection.getInputStream()));
			DataOutputStream  outToClient = new DataOutputStream(Connection.getOutputStream());
			
			//Read number from Client
			int NumberFromClient = inFromClient.read();
			
			//If client sent 13 we will begin the reversal process
			if (NumberFromClient == 13){
				//Create a UDP socket on a random port
				DatagramSocket UDPSocket = RandomSocket();
				//Write random port to Client and close TCP
				outToClient.writeBytes(UDPSocket.getLocalPort() + "\n");
				Connection.close();

				//Create storage for UDP messages
				byte[] sendData = new byte[1024]; 
				byte[] receiveData = new byte[1024];
				
				//Create a place to hold a packet and receive a packet from the Client
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				UDPSocket.receive(receivePacket);
				
				//Pull message from packet
				String Message = new String(receivePacket.getData());
				
				//Fetch return information from the packet
				InetAddress IP = receivePacket.getAddress(); 
				int ReturnPort = receivePacket.getPort(); 
				
				//Reverse the message and get its Bytes
				String ReversedMessage = new StringBuffer(Message).reverse().toString();
				sendData = ReversedMessage.getBytes();
				
				//Create a new packet containing the reversed message and send it back
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IP, ReturnPort);
				UDPSocket.send(sendPacket);
				
				//Close UDP connection
				UDPSocket.close();
			} else{
				//Else we have no other services so we will just close
				Connection.close();
				WelcomeSocket.close();
			}
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public static void main(String[] args){
		//Create a new instance of the server
		Server ReverseServer = new Server();
		//Launch the server
		ReverseServer.RunServer();
	}
}
