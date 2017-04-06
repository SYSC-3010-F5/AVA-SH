package mediaplayer;

import javazoom.jl.decoder.JavaLayerException;


import javazoom.jl.player.advanced.AdvancedPlayer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

/**
 *Class:                MediaPlayer.java
 *Project:          	AVA Smart Home
 *Author:               Nathaniel Charlebois
 *Date of Update:       21/02/2017
 *Version:              1.0.0
 *
 *Purpose:				Instantiates an AdvancedPlayer to play the requested song.
 *						-Currently, the path to the song library is hard-coded as relative paths
 *							require execution permissions.
 *
 */

public class MediaPlayer extends Thread {

	private String songName;
	private boolean verbose;
	private AdvancedPlayer playMP3;
	private int currentFrame = 0;
	private MDPlaybackListener playbackListener;

	private final static String SONG_LIB_PATH = "resources/songLibrary/";



	public MediaPlayer(String title,boolean vflag){
		songName = title;
		verbose = vflag;

		playbackListener = new MDPlaybackListener(this);

	}

	public boolean checkValid(){
		try{
			init();

		    return true;

		}catch(Exception e){
			print(e.getMessage());
			return false;
		}
	}

	public void run(){
		resumeS();
	}

	public void resumeS(){
		try {
			init();
			print("Resume, current frame: "+currentFrame);
			playMP3.play(currentFrame, Integer.MAX_VALUE);

		} catch (JavaLayerException e) {
			e.printStackTrace();
		} catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}

	private void init() throws FileNotFoundException, JavaLayerException{

		FileInputStream fis = new FileInputStream(Paths.get("").toAbsolutePath().resolve(SONG_LIB_PATH+songName+".mp3").toString());

	    playMP3 = new AdvancedPlayer(fis);
	    playMP3.setPlayBackListener(playbackListener);
	}

	public void pause(){

		playMP3.stop();
		print("Pause, current frame: "+currentFrame);
	}

	public void print(String msg){
		if(verbose){
			System.out.println(msg);
		}
	}

	public void setCurrentFrame(int frame){
		currentFrame = frame;
	}
	public int getCurrentFrame(){
		return currentFrame;
	}
}
