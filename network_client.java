package network_client;

//네트워크 및 실습
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class network_client {
	public static void main(String[] args) throws Exception // 
	{
		Socket socket = null;
		BufferedReader in = null;
		BufferedWriter out = null;
		Scanner sc = new Scanner(System.in);

		try {
			File server_path = new File("server_info.txt");//Serverinfo text file
			
			
			// read the server information from server_info.txt

			String info, serverIP = null, str_server_port = null;
			int serverport = 0;
			
			
			if (!server_path.exists()) {//if file does not exist
				serverIP = "127.0.0.1";//set Ip address of server and port defualt
				serverport = 1234;
			} else {
				BufferedReader serverInfoReader = new BufferedReader(new FileReader("server_info.txt"));
				
				while ((info = serverInfoReader.readLine()) != null) {
					
					String[] serverInfo = info.split(" ");
					serverIP = serverInfo[0];
					str_server_port = serverInfo[1];
					serverport = Integer.parseInt(str_server_port);
				}
				serverInfoReader.close();
			}
			socket = new Socket(serverIP, serverport); // create clientSocket
			System.out.println("Connected");
			System.out.println("This is calculator proram.\n");
			System.out.println("You can use four basic operations. If you want to exit, enter X\n");
			in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // create input stream
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // create output stream

			while (true) {
				System.out.println("Ex)ADD 10 20, MIN 10 3, MUL 5 9, DIV 10 5");
				System.out.println("Enter:");
				String msg = sc.nextLine(); // read the input from keyboard
				// if input is exit, escape the while loop
				if (msg.equalsIgnoreCase("X")) {
					out.write("x");
					out.flush();
					break;
				}
				out.write(msg + "\n"); // send the input to server
				out.flush();
				String result = in.readLine(); // get the answer from the server
				System.out.println("Messge from server:" + result);
			}
		}
		catch (Exception e) {
			System.out.println("Failed to connect server");
			e.printStackTrace();
		} finally {
			try {
				sc.close(); // close the scanner
				if (socket != null) {
					socket.close();
				}

			} catch (IOException e) // exception handler
			{
				e.printStackTrace();
				System.out.println("Error Occured While Communicating");
			}
		}
	}
}
