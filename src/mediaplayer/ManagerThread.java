package mediaplayer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import network.DataChannel;
import network.NetworkException;
import network.PacketWrapper;


//Currently locked to one MediaPlayer at a time

public class ManagerThread extends Thread{

	private DataChannel dataChannel;
	private boolean verbose = true;
	private String serverIP = "10.0.0.101";
	private int listeningPort = 3010;
	private String deviceName = "m\\mediaDriver";
	private String playCmdKey = "play song";
	private String pauseCmdKey = "pause song";
	private InetAddress serverInet;

	private MediaPlayer mediaPlayer;


	public ManagerThread(boolean verbose){
		this.verbose = verbose;
		connectToServer();
	}

	private void connectToServer(){
		try{
			dataChannel = new DataChannel();
		} catch(SocketException e){
			if(verbose){
				System.out.println(e.getMessage());
			}
		}

		try {
			serverInet = InetAddress.getByName(serverIP);
		} catch (UnknownHostException e) {
			print(e.getMessage());
			e.printStackTrace();
		}

		try {
			dataChannel.connect(serverInet, listeningPort, deviceName);
		} catch (NetworkException e) {
			print(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			print(e.getMessage());
			e.printStackTrace();
		}
	}

	public void run(){
		while(true){
			try {
				PacketWrapper packet = dataChannel.receivePacket();

				if(packet.type() == DataChannel.TYPE_CMD){
					if(packet.commandKey().equals(playCmdKey)){
						mediaPlayer = new MediaPlayer(packet.info(),verbose);
						if(mediaPlayer.checkValid()){
							mediaPlayer.start();
							print("mediaPlayer instantiated and started");
						}
					}
					else if(packet.commandKey().equals(pauseCmdKey)){
						if(mediaPlayer != null){

						}

					}

				}
				else{
					print("Discarding invalid packet.");
				}

			} catch (NetworkException e) {
				print(e.getMessage());
				e.printStackTrace();
			}
		}



	}

	private void print(String msg){
		if(verbose){
			System.out.println(msg);
		}

	}


	public static void main(String[] args) {
		boolean vflag = true;
		if(args.length != 0){
			if(args[0].equalsIgnoreCase("verbose")){
				vflag = true;
			}
			else {
				vflag = false;
			}
		}

		ManagerThread mt = new ManagerThread(vflag);
		mt.start();
	}

}
