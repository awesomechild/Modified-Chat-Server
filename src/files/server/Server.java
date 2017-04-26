package files.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server implements Runnable {

	private List<ServerClient> clients = new ArrayList<ServerClient>();
	// Stores information about all the Clients that are going to connect
	// This thing is similar to structs in c. Here we are using the class

	// and by we i mean -> I + (Chandan + Amit)

	private List<Integer> clientResponse = new ArrayList<Integer>();

	private DatagramSocket socket;
	private int port;
	private boolean running = false;
	private Thread run, manage, send, receive;
	private final int MAX_ATTEMPTS = 5;

	private boolean raw = false;

	public Server(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		run = new Thread(this, "Server");
		run.start();
	}

	// Whenever we accept the client Connection we dont know its ip address
	// so we have to do something here. Now i will try to use the send function
	// For this whenever we accept a client connection we will store it in some
	// kind
	// of list .
	// Go to ServerClient first and then come back
	public void run() {
		// The only purpose run thread serves is running manageClients() and
		// receive() thread and after that it is gone . its life is just to run
		// the server thats it
		running = true;
		System.out.println("Server started on port " + port);
		manageClients();
		receive();

		// Since server is only has console we have to take the input from Input
		// Stream .
		// wouldn't it be faster to use ISR class instead of Scanner. (Open to
		// discussion)
		Scanner scanner = new Scanner(System.in);
		while (running) {
			String text = scanner.nextLine();
			if (!text.startsWith("/")) {

				// if it doesn't start with a '/' it is a message to all client
				// so send to all clients
				sendToAll("/m/Server: " + text + "/e/");
				continue;
			}
			text = text.substring(1);

			// raw is just boolean value . Added raw mode just in case i wanted
			// to have a look at all the packets that are
			// coming in. To start raw mode just go in the server console and
			// type '/raw'

			// you should also see the /d/ packet just before the client
			// disconnects

			// use '/clients' to view all client details
			if (text.equals("raw")) {
				if (raw)
					System.out.println("Raw mode off.");
				else
					System.out.println("Raw mode on.");
				raw = !raw;
			} else if (text.equals("clients")) {
				System.out.println("Clients:");
				System.out.println("========");
				for (int i = 0; i < clients.size(); i++) {
					ServerClient c = clients.get(i);
					System.out.println(c.name + "(" + c.getID() + "): " + c.address.toString() + ":" + c.port);
				}
				System.out.println("========");
			} else if (text.startsWith("kick")) {
				String name = text.split(" ")[1];
				int id = -1;
				boolean number = true;
				try {
					id = Integer.parseInt(name);
				} catch (NumberFormatException e) {
					number = false;
				}
				if (number) {
					boolean exists = false;
					for (int i = 0; i < clients.size(); i++) {
						if (clients.get(i).getID() == id) {
							exists = true;
							break;
						}
					}
					if (exists)
						disconnect(id, true);
					else
						System.out.println("Client " + id + " doesn't exist! Check ID number.");
				} else {
					for (int i = 0; i < clients.size(); i++) {
						ServerClient c = clients.get(i);
						if (name.equals(c.name)) {
							disconnect(c.getID(), true);
							break;
						}
					}
				}
			} else if (text.equals("help")) {
				printHelp();
			} else if (text.equals("quit")) {
				quit();
			} else {
				System.out.println("Unknown command.");
				printHelp();
			}
		}
		scanner.close();
	}

	private void printHelp() {
		System.out.println("Here is a list of all available commands:");
		System.out.println("=========================================");
		System.out.println("/raw - enables raw mode.");
		System.out.println("/clients - shows all connected clients.");
		System.out.println("/kick [users ID or username] - kicks a user.");
		System.out.println("/help - shows this help message.");
		System.out.println("/quit - shuts down the server.");
	}

	// This will be sending out things like pings
	private void manageClients() {
		manage = new Thread("Manage") {
			@Override
			public void run() {
				while (running) {
					sendToAll("/i/server");
					sendStatus();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// This part is Not working
					for (int i = 0; i < clients.size(); i++) {
						ServerClient c = clients.get(i);
						if (!clientResponse.contains(c.getID())) {
							if (c.attempt >= MAX_ATTEMPTS) {
								disconnect(c.getID(), false);
							} else {
								c.attempt++;
							}
						} else {
							clientResponse.remove(new Integer(c.getID()));
							c.attempt = 0;
						}
					}
				}
			}
		};
		manage.start();
	}

	private void sendStatus() {
		if (clients.size() <= 0)
			return;
		String users = "/u/";
		for (int i = 0; i < clients.size() - 1; i++) {
			users += clients.get(i).name + "/n/";
		}

		// here
		users += clients.get(clients.size() - 1).name + "/e/";
		sendToAll(users);
	}

	private void receive() {
		receive = new Thread("Receive") {
			@Override
			public void run() {
				while (running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (SocketException e) {
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
				}
			}
		};
		receive.start();
	}

	// if message starts with /m/ send it to all clients
	private void sendToAll(String message) {
		if (message.startsWith("/m/")) {
			String text = message.substring(3);
			text = text.split("/e/")[0];
			System.out.println(message);
		}
		for (int i = 0; i < clients.size(); i++) {
			ServerClient client = clients.get(i);
			send(message.getBytes(), client.address, client.port);
		}
	}

	// These things have to be final because we are using anonymous inner
	// classes. short note on the bottom incase you wanna know
	// *Geek Alert
	private void send(final byte[] data, final InetAddress address, final int port) {
		send = new Thread("Send") {
			@Override
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
				try {
					socket.send(packet);
					// post office specifies where to send the letter. LOL!!!
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}

	private void send(String message, InetAddress address, int port) {
		message += "/e/";
		send(message.getBytes(), address, port);
	}

	// This is to prefix the data with a certain identifier so that we can know
	// what type of packet is being received
	// A message packet or a termination packet connection packet and whatever
	// packet ~~~~

	// Connection packet be like - Hey Server this is me this is my ip address
	// This is my port number. Let me connect. Now we have to device
	// such a method. ie we need to differentiate messages

	// I know this example sounds lame but you get the point
	private void process(DatagramPacket packet) {
		String string = new String(packet.getData());
		if (raw)
			System.out.println(string);
		if (string.startsWith("/c/")) {

			// Since we need a random number why don't we just do int id = new
			// Random.nextInt();
			// int id = new Random().nextInt(); you'll see that the range is
			// very good. its extremely unlikely to get the same
			// number. But it is actually possible. Remember Moore's law -->> :
			// If anything bad can happen, it probably will."
			// So what about SecureRandom ??

			// int id = new SecureRandom().nextInt();
			// still If anything bad can happen, it probably will

			// UUID id = UUID.randomUUID();
			// Universally unique identifier
			// Switching to unique identifier page
			int id = UniqueIdentifier.getIdentifier();// Calling the
														// UniqueIdentifire
														// class
			String name = string.split("/c/|/e/")[1];
			System.out.println(name + "(" + id + ") connected!");

			// How do you get the source InetAddress(ip) and Port
			// Simple!!
			// It doesnot matter if you are using TCP or UDP. The header fld in
			// both of these has S_IP and Port
			// for more ask SB or RSG. I was sleeping
			clients.add(new ServerClient(name, packet.getAddress(), packet.getPort(), id));
			String ID = "/c/" + id;
			send(ID, packet.getAddress(), packet.getPort());
		} else if (string.startsWith("/m/")) {
			sendToAll(string);
		} else if (string.startsWith("/d/")) {
			String id = string.split("/d/|/e/")[1];
			disconnect(Integer.parseInt(id), true);
		} else if (string.startsWith("/i/")) {
			clientResponse.add(Integer.parseInt(string.split("/i/|/e/")[1]));
		} else {
			System.out.println(string);
		}
	}

	private void quit() {
		for (int i = 0; i < clients.size(); i++) {
			disconnect(clients.get(i).getID(), true);
		}
		running = false;
		socket.close();
	}

	private void disconnect(int id, boolean status) {
		ServerClient c = null;
		boolean existed = false;
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getID() == id) {
				c = clients.get(i);
				clients.remove(i);
				existed = true;
				break;
			}
		}
		if (!existed)
			return;
		String message = "";
		if (status) {
			message = "Client " + c.name + " (" + c.getID() + ") @ " + c.address.toString() + ":" + c.port
					+ " disconnected.";
		} else {
			message = "Client " + c.name + " (" + c.getID() + ") @ " + c.address.toString() + ":" + c.port
					+ " timed out.";
		}
		System.out.println(message);
	}

}

// About inner classes (method local or anonymous), they say that we can't
// access the local variables because they live on the stack
// while the class lives on the heap and could get returned by the method and
// then try to have access to these variables that are on
// the stack but do not exist anymore since the method has ended...
//
// As we all know, we can bypass this by using the final keyword. This is what
// they say in the book but they don't really explain
// what's the effect of that final keyword... As far as i know, using the final
// keyword on a method local variable doesn't make it
// live on the heap... So how would the class be able to access a final variable
// that still lives on the stack while there could be
// no more stack???

// because the variable is final it is safe to make a copy of it. Of course for
// reference types this means copying the reference to the
// object and not the object it refers to.
