package mediaplayer;

import java.io.IOException;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import network.DataChannel;
import network.NetworkException;
import network.PacketWrapper;


/**
 *Class:                ManagerThread.java
 *Project:          	AVA Smart Home
 *Author:               Nathaniel Charlebois
 *Date of Update:       21/02/2017
 *Version:              1.0.0
 *
 *Purpose:				A control thread that instantiates an MediaPLayer to play the requested song.
 *						-General flow control is provided by received packets
 *
 *
 */

public class ManagerThread extends Thread{

	private DataChannel dataChannel;
	private boolean verbose = true;

	private int listeningPort = 3010;

	private static final String SERVER_IP = "192.168.2.100";
	private static final String DEVICE_NAME = "m\\Media Driver";
	private static final String PLAY_CMD_KEY = "play song";
	private static final String PAUSE_CMD_KEY = "pause music";
	private static final String STOP_CMD_KEY = "stop music";
	private static final String RESUME_CMD_KEY = "resume music";
	private InetAddress serverInet;
	private PacketWrapper packet;

	private MediaPlayer mediaPlayer = null;



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
			serverInet = InetAddress.getByName(SERVER_IP);
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
				packet = dataChannel.receivePacket();

				if(packet.type() == DataChannel.TYPE_CMD){
					if(packet.commandKey().equals(PLAY_CMD_KEY)){
						if(mediaPlayer == null){
							print("Manager: Play cmd received");;
							initMediaPlayer();

						} else{
							print("Stopping the currently playing song and starting "+packet.extraInfo());

							initMediaPlayer();
						}
					}
					else if(packet.commandKey().equals(PAUSE_CMD_KEY)){
						print("Manager: Pause cmd received");
						if(mediaPlayer == null){
							respondNoCurrentSong();
						}
						else{
							mediaPlayer.pause();
						}
					}
					else if(packet.commandKey().equals(STOP_CMD_KEY)){

						print("Manager: Stop cmd received");
						if(mediaPlayer == null){
							respondNoCurrentSong();
						} else {
							mediaPlayer.pause();
							mediaPlayer = null;
						}
					}
					else if(packet.commandKey().equals(RESUME_CMD_KEY)){

						print("Manager: Resume cmd received");
						if(mediaPlayer == null){
							respondNoCurrentSong();
						} else {
							mediaPlayer.resumeS();
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
		print("There is no song currently playing.");
	}

	private void print(String msg){
		if(verbose){
			System.out.println(msg);
		}

	}


	private void initMediaPlayer(){
		mediaPlayer = new MediaPlayer(packet.extraInfo(),verbose);
		if(mediaPlayer.checkValid()){
			mediaPlayer.start();
			print("mediaPlayer instantiated and started");

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
