package mediaplayer;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.*;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.FileInputStream;

public class MediaPlayer extends Thread{

	private String songName;
	private boolean verbose;
	private AdvancedPlayer playMP3;


	public MediaPlayer(String title,boolean vflag){
		songName = title;
		verbose = vflag;

	}

	public boolean checkValid(){
		try{
		    FileInputStream fis = new FileInputStream(getClass().getResource("songLibrary/"+"Tester"+".mp3").toString());
		    playMP3 = new AdvancedPlayer(fis);
		    return true;

		}catch(Exception e){
			print(e.getMessage());
			return false;
		}
	}

	public void run(){
		try {
			playMP3.play();
		} catch (JavaLayerException e) {
			print(e.getMessage());
		}

	}

	public void pause(){

	}

	public void print(String msg){
		System.out.println(msg);
	}
}
