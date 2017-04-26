package files;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
	private static final long serialVersionUID = 1L;

	private DatagramSocket socket;

	private String name, address;
	private int port;
	private InetAddress ip;
	private Thread send;
	private int ID = -1;

	public Client(String name, String address, int port) {
		this.name = name;
		this.address = address;
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public boolean openConnection(String address) {
		try {
			socket = new DatagramSocket();
			// Constructs a datagram socket and binds it to any available
			// port on the local host machine. (just hover over)
			// So if we dont even know what port we are binding it to
			// don't we need to tell the server which port we are sending
			// the data from and where it should it send the reply
			// you can use packet.getAddress() and packet.getPort() though
			// so we can get the source address and port
			ip = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String receive() {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);// This will freeze the app till it receives
									// any data
									// another reason we need threads
		} catch (IOException e) {
			e.printStackTrace();
		}
		String message = new String(packet.getData());
		return message;
	}

	public void send(final byte[] data) {
		send = new Thread("Send") {
			@Override
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}

	public void close() {
		new Thread() {
			@Override
			public void run() {
				synchronized (socket) {
					socket.close();
				}
			}
		}.start();
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public int getID() {
		return ID;
	}

}