import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

//Class representing a client
public class Client {

	//Function that calls the Server to reverse the string and returns new string
	public String reverse (InetAddress IP, int Port, String Message){
		//Catch exceptions for debugging only
		//by this point in time any input bugs **should** be already handled
		try {
			//Create the TCP socket we will negotiate over and setup in/out streams
			Socket connect = new Socket(IP,Port);
			BufferedReader inFromServer = 
					new BufferedReader(new
					InputStreamReader(connect.getInputStream()));
			DataOutputStream  outToServer = new DataOutputStream(connect.getOutputStream());

			//Send the number 13 to the server, this signals that we want to reverse a string
			outToServer.writeByte(13);
			
			//Receive the port for reversal back from the server
			//parse and use readline here to make things easier to read
			//also since the port could be several bytes to represent
			int UDPPort = Integer.parseInt(inFromServer.readLine());
			
			//Tell server we are finished with the TCP
			connect.close();
			
			//Setup output and input for UDP socket
			byte[] sendData = new byte[1024]; 
			byte[] receiveData = new byte[1024]; 
			
			//Create UDP socket
			DatagramSocket clientSocket = new DatagramSocket();
			
			//Set our output as the Bytes of our string
			sendData = Message.getBytes();
			
			//Package up our data and send it to the UDP port the server is listeneing on
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IP,UDPPort); 
			clientSocket.send(sendPacket);
			
			//Create a packet to hold the servers response
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			
			//Receive the reversed string, close up connection and return the string
			clientSocket.receive(receivePacket);
			clientSocket.close();
			return new String(receivePacket.getData()); 
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectException e){
			//Only reached if connection refused
			System.out.println("Server refused connection on given port!");
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			//Only reached if a bad port number is given
			System.out.println("Given port is out of range! Should be between 1 and 65535");
			System.exit(-1);
		} 
		//If we error out and get here just return the message unaltered
		return Message;
	}
	
	
	public static void main (String[] args){
		//If the arguments arent right tell the user and quit
		if (args.length != 3){
			System.out.println("Incorrect number of command line paramaters");
			System.out.println("Should have <Server_Address> <Port> <Message>");
			System.exit(-1);
		}
			InetAddress IP = null;
			int  Port = 0;
			//Fetch our host and negotiation port
			try {
				IP = InetAddress.getByName(args[0]); 
				Port = Integer.parseInt(args[1]);
			} catch (NumberFormatException e){
				//If port is not a number exit out
				System.out.println("Invalid format: Port should be a number");
				System.exit(-1);
			} catch (UnknownHostException e) {
				//If hostname cant be found exit out
				System.out.println("Invalid Host name given");
				System.exit(-1);
			}
			String Message = args[2];
			
			//Create an instance of Client
			Client StringReverseClient = new Client();
			
			//Reverse Message and print it out
			String Reversed = StringReverseClient.reverse(IP,Port,Message);
			System.out.println(Reversed);
		

	}
}