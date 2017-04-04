package mediaplayer;

import javazoom.jl.decoder.JavaLayerException;

import javazoom.jl.player.*;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MediaPlayer extends Thread {

	private String songName;
	private boolean verbose;
	private AdvancedPlayer playMP3;
	private int currentFrame = 0;
	private MDPlaybackListener playbackListener;



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
		FileInputStream fis = new FileInputStream("C:\\Users\\nathanielcharlebois.LABS\\Desktop\\tempProject\\AVA-SH\\src\\mediaplayer\\songLibrary\\"+songName+".mp3");
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
