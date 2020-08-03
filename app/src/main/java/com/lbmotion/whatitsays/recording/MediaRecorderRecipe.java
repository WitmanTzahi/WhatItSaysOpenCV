package com.lbmotion.whatitsays.recording;

import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

import com.lbmotion.whatitsays.MainActivity;
import com.lbmotion.whatitsays.R;
import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.common.BaseActivity;
import com.lbmotion.whatitsays.data.TicketInformation;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MediaRecorderRecipe extends BaseActivity implements SurfaceHolder.Callback {
	private ToggleButton mToggleButton;
	private String mFileName = null;
	private SurfaceHolder surfaceHolder;
	private SurfaceView surfaceView;
	public MediaRecorder mrec = new MediaRecorder();
	private Camera mCamera;
	private boolean				surfaceWasCreated = false;
    public static AtomicBoolean recordingNow = new AtomicBoolean(false);
    
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_recorder_recipe);
        recordingNow.set(false);
        // we shall take the video in landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		mCamera = Camera.open();

		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mToggleButton = (ToggleButton)findViewById(R.id.toggleRecordingButton);
		mToggleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(((ToggleButton)v).isChecked()) {
					try {
						startRecording();
						recordingNow.set(true);
					} 
					catch (Exception e) {
						String message = e.getMessage();
						Log.i(null, "Problem " + message);
						releaseMediaRecorder();
						releaseCamera();
			    		finish();
					}
				}
				else {
					addSoundFile();
					if(mrec != null) {
						try{mrec.stop();}catch(Exception e){}
						try{mrec.release();}catch(Exception e){}
						mrec = null;
					}
				}
			}
		});
		doStartRecording();
    }

    @Override
    public void onBackPressed() {
		try {
    		if(recordingNow.get())
    			addSoundFile();
		}
		catch (Exception e) {}
		catch (Error r) {}
		try {
			releaseMediaRecorder();
		}
		catch (Exception e) {}
		catch (Error r) {}
		mrec = null;
		try {
			if (mCamera != null)
				mCamera.stopPreview();
		}
		catch (Exception e) {}
		catch (Error r) {}
		try {
			releaseCamera();
		}
		catch (Exception e) {}
		catch (Error r) {}
		super.onBackPressed();
    }
    
    private void doStartRecording() {
    	recordingNow.set(true);
    	mToggleButton.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(!surfaceWasCreated) {
					doStartRecording();
				}
				else {
					try {
						startRecording();
					} 
					catch (Exception e) {
						String message = e.getMessage();
						Log.i(null, "Problem " + message);
						if(mrec != null) 
							mrec.release();
					}
			    	mToggleButton.postDelayed(new Runnable() {
						@Override
						public void run() {
							if(mrec != null) {
								try {mrec.stop();}catch(Exception e) {}
								try {mrec.release();}catch(Exception e) {}
							}
							mrec = null;
							File file = new File(Environment.getExternalStorageDirectory(),mFileName);
							if(file.exists())
								file.delete();
							recordingNow.set(false);
						}
					},750);						
				}
			}
		},125);	
    }

	protected void startRecording() throws IOException {
		if(mCamera == null)
			mCamera = Camera.open();
		mFileName = TicketInformation.getPath("Data")+ MainActivity.getFileName()+".mp4";
		File file;
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			file = new File(mFileName);
		else
			file = new File(TicketInformation.getDirectory(mFileName), TicketInformation.getFilename(mFileName));
		// "touch" the file
		if(!file.exists()) {
			File parent = file.getParentFile();
			if(parent != null)
				if(!parent.exists())
					if(!parent.mkdirs())
						throw new IOException("Cannot create " + "parent directories for file: " + file);
				file.createNewFile();
		}
		mrec = new MediaRecorder();
		try{mCamera.lock();}catch(Exception e) {}
		try{mCamera.unlock();}catch(Exception e) {}
// Please maintain sequence of following code. If you change sequence it will not work.
		if(mrec != null) {
			mrec.setCamera(mCamera);
			mrec.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mrec.setAudioSource(MediaRecorder.AudioSource.MIC);
			mrec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mrec.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
			mrec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mrec.setMaxDuration(150000); //set maximum duration 15 seconds.
			mrec.setPreviewDisplay(surfaceHolder.getSurface());
			mrec.setOutputFile(file.getAbsolutePath());
		}
		try{mrec.prepare();}catch(Exception e){}
		try{mrec.start();}catch(Exception e){}
	}

	protected void stopRecording() {
		if(mrec != null) {
			try{mrec.stop();}catch(Exception e){}
			try{mrec.release();}catch(Exception e){}
			if(mCamera != null) { 
				try{mCamera.release();}catch(Exception e){}
				try{mCamera.lock();}catch(Exception e){}
			}
		}
	}

	private void releaseMediaRecorder() {
		if (mrec != null) {
			try{mrec.reset();}catch(Exception e){} // clear recorder configuration
			try{mrec.release();}catch(Exception e){} // release the recorder object
		}
	}

	private void releaseCamera() {
		if (mCamera != null) {
			try{mCamera.release();}catch(Exception e){} // release the camera for other applications
			mCamera = null;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			if (mCamera != null) {
				Parameters params = mCamera.getParameters();
				mCamera.setParameters(params);
				Log.i("Surface", "Created");
				surfaceWasCreated = true;
			}
			else {
				BaseActivity.showErrorMessage(R.string.recording_failed,MediaRecorderRecipe.this);
				releaseMediaRecorder();
				releaseCamera();
				MediaRecorderRecipe.this.finish();
			}
		}
		catch(Exception e) {}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(mCamera != null) {
			try{mCamera.stopPreview();}catch (Exception e) {}
			try{mCamera.release();}catch (Exception e) {}
		}
	}

	@Override
	protected void onDestroy() {
		stopRecording();
		recordingNow.set(false);
		super.onDestroy();
	}
	
    private void addSoundFile() {
    	recordingNow.set(false);
		UCApp.ticketInformation.sounds.add(mFileName);
    	TicketInformation.doSave(UCApp.ticketInformation,true);
    	if(UCApp.ticketInformation.sounds.size() > 9) {
			releaseMediaRecorder();
			releaseCamera();
    		finish();
    	}
	}
}