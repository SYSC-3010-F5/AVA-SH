package mediaplayer;
import javazoom.jl.decoder.JavaLayerException;

import javazoom.jl.player.*;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MDPlaybackListener extends PlaybackListener {
	private MediaPlayer mediaPlayer;
	
	public MDPlaybackListener(MediaPlayer mp){
		super();
		mediaPlayer = mp;

	}
	
	@Override
    public void playbackFinished(PlaybackEvent event) {
        mediaPlayer.setCurrentFrame(event.getFrame());
    }

}
