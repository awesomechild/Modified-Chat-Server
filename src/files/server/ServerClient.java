package files.server;

import java.net.InetAddress;

//This class will store information about the clients that are connected to us
public class ServerClient {

	public String name;
	public InetAddress address;
	public int port;

	private final int ID;
	// Identification no that we will give to the client
	// This should be a randomly generated number that has to be unique
	// So should we use Socket as the id . NO!!!
	// The reason being you may have the same ip address and the same port
	// and that might cause a problem.

	// since id is final we also need to make a getter function. House keeping
	// stuff

	public int attempt = 0;
	// Attempt>5 . Kick the client. i.e when a Person abruptly kills the Client
	// program . We are going to configure the Server such that every few
	// seconds
	// it sends a dummy packet .

	// Server - You still here
	// Client says : Yeah bro. Whats the matter

	// Server - You still here
	// Client : ~~~~~~~~~~~~~~
	// Server - Oh Really. GTFO!!

	public ServerClient(String name, InetAddress address, int port, final int ID) {
		this.name = name;
		this.address = address;
		this.port = port;
		this.ID = ID;
	}

	public int getID() {
		return ID;
	}

}
