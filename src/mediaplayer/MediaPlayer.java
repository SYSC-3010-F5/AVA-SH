package mediaplayer;

import javazoom.jl.decoder.JavaLayerException;

import javazoom.jl.player.*;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.FileInputStream;

public class MediaPlayer extends Thread {

	private String songName;
	private boolean verbose;
	private AdvancedPlayer playMP3;
	private int currentFrame = 0;


	public MediaPlayer(String title,boolean vflag){
		songName = title;
		verbose = vflag;

	}

	public boolean checkValid(){
		try{
			//TODO remove
			System.out.println("Checking valid");
		    FileInputStream fis = new FileInputStream("C:\\Users\\nathanielcharlebois.LABS\\Desktop\\tempProject\\AVA-SH\\src\\mediaplayer\\songLibrary\\"+songName+".mp3");
		    System.out.println(fis.toString());
		    playMP3 = new AdvancedPlayer(fis);
		    playMP3.setPlayBackListener(new PlaybackListener(){
		        @Override
		        public void playbackFinished(PlaybackEvent event) {
		            currentFrame = event.getFrame();
		        }
		    });
		    return true;

		}catch(Exception e){
			print(e.getMessage());
			return false;
		}
	}

	public void run(){
		play();
	}

	public void play(){

		try {
			playMP3.play();
		} catch (JavaLayerException e) {
			print(e.getMessage());
		}

	}

	public void pause(){
		playMP3.stop();
	}


	public void print(String msg){
		if(verbose){
			System.out.println(msg);
		}
	}
}
