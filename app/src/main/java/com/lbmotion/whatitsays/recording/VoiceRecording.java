package com.lbmotion.whatitsays.recording;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;


import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.data.TicketInformation;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class VoiceRecording {

	private static final String LOG_TAG = "VoiceRecordingActivity";
    private String mFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private boolean				fileWasAdded = false;
    public static AtomicBoolean recordingNow = new AtomicBoolean(false);
    
    public VoiceRecording(String filename) {
    	mFileName = filename;
    }
    
    public boolean onRecord(boolean start) {
        if (start) {
        	recordingNow.set(true);
            return startRecording();
        } 
        else {
            stopRecording();
            return true;
        }
    }

    public void onPlay(boolean start) {
        if(start) {
            startPlaying();
        } 
        else {
        	recordingNow.set(false);
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void addSoundFile() {
    	if(!fileWasAdded) {
			UCApp.ticketInformation.sounds.add(mFileName);
			TicketInformation.doSave(UCApp.ticketInformation,true);
    		fileWasAdded = true;
    		recordingNow.set(false);
    	}
	}

    private void stopPlaying() {
    	try {
    		mPlayer.release();
    		mPlayer = null;
    		recordingNow.set(false);
    	}
    	catch (Exception e) {}
    }

    private boolean startRecording() {
    	boolean returnCode = true;
    	try {
    		mRecorder = new MediaRecorder();
    		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    		mRecorder.setOutputFile(mFileName);
    		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    		try {
    			mRecorder.prepare();
    		}
    		catch (IOException e) {
    			Log.e(LOG_TAG, "prepare() failed:"+e.getMessage());
    			returnCode = false;
    		}
    		try {
    			mRecorder.start();
    		}
    		catch (Exception e) {
    			Log.e(LOG_TAG, "start() failed:"+e.getMessage());
    			returnCode = false;
    		}
    	}
    	catch (Exception e) {
			returnCode = false;
    	}
        return returnCode;
    }

    private void stopRecording() {
    	try {
    		mRecorder.stop();
    		mRecorder.release();
    		mRecorder = null;
    		addSoundFile();
    		recordingNow.set(false);
    	}
    	catch (Exception e) {}
    }

    public void onPause() {
    	try {
    		if (mRecorder != null) {
    			mRecorder.release();
    			mRecorder = null;
    		}
    		if (mPlayer != null) {
    			mPlayer.release();
    			mPlayer = null;
    		}
    		addSoundFile();
    		recordingNow.set(false);
    	}
    	catch (Exception e) {}
    }
}