package files;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class Login extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtAddress;
	private JTextField txtPort;
	private JLabel lblIpAddress;
	private JLabel lblPort;
	private JLabel lblAddressDesc;
	private JLabel lblPortDesc;

	public Login() {
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 600);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtName = new JTextField();
		txtName.setBounds(298, 80, 490, 28);
		contentPane.add(txtName);
		txtName.setColumns(10);

		JLabel lblName = new JLabel("Name:");
		lblName.setFont(new Font("Dialog", Font.BOLD, 16));
		lblName.setBounds(535, 57, 105, 16);
		contentPane.add(lblName);

		txtAddress = new JTextField();
		txtAddress.setBounds(37, 201, 546, 28);
		contentPane.add(txtAddress);
		txtAddress.setColumns(10);

		lblIpAddress = new JLabel("Inet Address");
		lblIpAddress.setFont(new Font("Dialog", Font.BOLD, 16));
		lblIpAddress.setBounds(196, 180, 124, 16);
		contentPane.add(lblIpAddress);

		txtPort = new JTextField();
		txtPort.setColumns(10);
		txtPort.setBounds(619, 201, 326, 28);
		contentPane.add(txtPort);

		lblPort = new JLabel("Port:");
		lblPort.setFont(new Font("Dialog", Font.BOLD, 16));
		lblPort.setBounds(733, 180, 146, 16);
		contentPane.add(lblPort);

		lblAddressDesc = new JLabel("(eg. 172.16.14.199)");
		lblAddressDesc.setFont(new Font("Dialog", Font.BOLD, 16));
		lblAddressDesc.setBounds(173, 240, 184, 16);
		contentPane.add(lblAddressDesc);

		lblPortDesc = new JLabel("(range : 0-65535 )");
		lblPortDesc.setFont(new Font("Dialog", Font.BOLD, 16));
		lblPortDesc.setBounds(690, 241, 212, 16);
		contentPane.add(lblPortDesc);

		JButton btnLogin = new JButton("Login");
		btnLogin.setFont(new Font("Dialog", Font.BOLD, 16));
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = txtName.getText();
				String address = txtAddress.getText();
				int port = Integer.parseInt(txtPort.getText());
				login(name, address, port);
			}
		});
		btnLogin.setBounds(400, 342, 308, 29);
		contentPane.add(btnLogin);
	}

	private void login(String name, String address, int port) {
		dispose();
		new ClientWindow(name, address, port);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
