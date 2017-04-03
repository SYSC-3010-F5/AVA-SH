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
	private static final String DEVICE_NAME = "m\\mediaDriver";
	private static final String PLAY_CMD_KEY = "play song";
	private static final String PAUSE_CMD_KEY = "pause song";
	private static final String STOP_CMD_KEY = "stop song";
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
			dataChannel.connect(serverInet, listeningPort, DEVICE_NAME);
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
					if(packet.commandKey().equals(PLAY_CMD_KEY)){
						mediaPlayer = new MediaPlayer(packet.info(),verbose);
						if(mediaPlayer.checkValid()){
							mediaPlayer.start();
							print("mediaPlayer instantiated and started");
						}
					}
					else if(packet.commandKey().equals(PAUSE_CMD_KEY)){
						if(mediaPlayer == null){
							respondNoCurrentSong();
						}
						else{
							mediaPlayer.pause();
						}
					}
					else if(packet.commandKey().equals(STOP_CMD_KEY)){
						if(mediaPlayer == null){
							respondNoCurrentSong();
						} else {
							mediaPlayer.pause();
							mediaPlayer = null;
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

	private void respondNoCurrentSong(){
		String error = "There is no song currently playing.";
		try {
			dataChannel.sendErr(error);
		} catch (NetworkException e) {
			print(e.getMessage());
		}
		print("Sending Error packet: " + error);
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
