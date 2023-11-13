package network_server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class network_server {
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(1234);// Set serverPort
			System.out.println("Server is for client to be connected(port:1234)");

			while (true) {
				System.out.println("Waiting for client to be connected");
				Socket clientSocket = serverSocket.accept(); // When client access the server
				System.out.println("New client connected from " + clientSocket.getInetAddress());
				new Thread(new clientcommunicate(clientSocket)).start();// Make thread for per client
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

class clientcommunicate implements Runnable {
	private Socket socket;

	public clientcommunicate(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));// socket to read  message from client
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));// socket to send message to client
			while (true) {
				String message = in.readLine();
				if (message.equalsIgnoreCase("x")) {
					System.out.println("Client OUT.(" + Thread.currentThread().getName() + ")");
					break;
				} else if (message == null || message.trim().isEmpty()) {
					out.write("no Messege. Send the Messege again");
					out.flush();// send everything in buffer
				} else {
					calculator calc = new calculator(message);
					String result = calc.calculate();
					out.write(result + "\n");
					out.flush();
				}
			}
		} catch (SocketException e) {
			System.out.println("Client has been disconnected (" + Thread.currentThread().getName() + ")");// when client
																											// close the
																											// connection
																											// unexpectly
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Error closing the socket");
				e.printStackTrace();
			}
		}
	}
}

class calculator {
    private int num1 = 0, num2 = 0;
    private String operator;
    String error_msg;
    calculator(String message) {
        String[] msg = message.split(" ");
        if (msg.length == 3) {
            try {
                num1 = Integer.parseInt(msg[1]);
                num2 = Integer.parseInt(msg[2]);
            } catch (NumberFormatException e) {
                // 숫자가 아닌 문자일 경우 operator를 wrong_format으로 설정
            	operator = "wrong_format";
            }
            operator = msg[0].toUpperCase(); // 연산자를 대문자로 변환
        } else {
            // 처리할 메시지가 잘못된 형식이라면 operator를 "INVALID"로 설정
            operator = "INVALID";
        }
    }
    String calculate() {
        if ("INVALID".equals(operator)) {
            return "There should be only 1 operator and 2 integer numbers";
        }
        else if("wrong_format".equals(operator)) {
        	return "You should write right format";
        }
        switch (operator) {
            case "ADD":
                return String.valueOf(num1 + num2);
            case "MIN":
                return String.valueOf(num1 - num2);
            case "MUL":
                return String.valueOf(num1 * num2);
            case "DIV":
                try {
                	if(num1 < num2) {
                		return String.valueOf((double)num1 / num2);
                	}
                	else {
                    double result = num1 / num2;
                    return String.valueOf(result);
                	}
                } catch (ArithmeticException e) {
                    return "Can't divide by 0.";
                }
            default:
                return "Enter the correct operator.";
        }
    }
}


