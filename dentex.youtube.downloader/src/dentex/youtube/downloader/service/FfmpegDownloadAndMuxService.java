package dentex.youtube.downloader.service;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import dentex.youtube.downloader.DashboardActivity;
import dentex.youtube.downloader.YTD;
import dentex.youtube.downloader.ffmpeg.FfmpegController;
import dentex.youtube.downloader.ffmpeg.ShellUtils.ShellCallback;
import dentex.youtube.downloader.utils.Json;
import dentex.youtube.downloader.utils.Utils;

public class FfmpegDownloadAndMuxService extends IntentService {
	
	public FfmpegDownloadAndMuxService() {
		super("FfmpegDownloadAndMuxService");
	}

	String DEBUG_TAG = "FfmpegDownloadAndMuxService";
	File muxedVideo;
	String muxedFileName;
	private String muxedPath;
	
	private int totSeconds;
	private int currentTime;
	
	private String A_LINK;
	private String V_LINK;
	int pos;
	String videoId;
	
	ResultReceiver receiver;
	public static final int UPDATE_PROGRESS = 1000;

	@Override
	protected void onHandleIntent(Intent intent) {
		receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
		
		A_LINK = intent.getStringExtra("A_LINK");
		V_LINK = intent.getStringExtra("V_LINK");
		
		pos = intent.getIntExtra("POS", 0);
		videoId = intent.getStringExtra("YT_ID");
		
		muxedFileName = intent.getStringExtra("FILENAME");
		muxedPath = intent.getStringExtra("PATH");
		muxedVideo = new File(muxedPath, muxedFileName);
		
		FfmpegController ffmpeg = null;
		try {
			ffmpeg = new FfmpegController(FfmpegDownloadAndMuxService.this);			
		} catch (IOException ioe) {
			Log.e(DEBUG_TAG, "Error loading ffmpeg. " + ioe.getMessage());
		}

		MuxShellDummy shell = new MuxShellDummy();

		try {
			ffmpeg.downloadAndMuxAoVoStreams(A_LINK, V_LINK, muxedVideo, shell);
		} catch (IOException e) {
			Log.e(DEBUG_TAG, "IOException running ffmpeg" + e.getMessage());
		} catch (InterruptedException e) {
			Log.e(DEBUG_TAG, "InterruptedException running ffmpeg" + e.getMessage());
		}
	}
	
	class MuxShellDummy implements ShellCallback {
		
		//int total = 0;
		//int progress = 0;

		@Override
		public void shellOut(String shellLine) {
			int[] times = getAudioJobProgress(shellLine);				
			Bundle resultData = new Bundle();
			resultData.putInt("total" , times[0]);
	        resultData.putInt("progress" , times[1]);
	        receiver.send(FfmpegDownloadAndMuxService.UPDATE_PROGRESS, resultData);
			Utils.logger("d", shellLine, DEBUG_TAG);
		}
		
		private int[] getAudioJobProgress(String shellLine) {
			Pattern totalTimePattern = Pattern.compile("Duration: (..):(..):(..)\\.(..)");
			Matcher totalTimeMatcher = totalTimePattern.matcher(shellLine);
			if (totalTimeMatcher.find()) {
				totSeconds = Utils.getTotSeconds(totalTimeMatcher);
			}
			Pattern currentTimePattern = Pattern.compile("time=(..):(..):(..)\\.(..)");
			Matcher currentTimeMatcher = currentTimePattern.matcher(shellLine);
			if (currentTimeMatcher.find()) {
				currentTime = Utils.getTotSeconds(currentTimeMatcher);
			}
			return new int[] { totSeconds, currentTime };
		}
		
		@Override
		public void processComplete(int exitValue) {
			Utils.logger("i", "FFmpeg process exit value: " + exitValue, DEBUG_TAG);

			if (exitValue == 0) {
	    		Bundle resultData = new Bundle();
	            resultData.putInt("progress" , -1);
	            resultData.putInt("total" , -1);
	            receiver.send(FfmpegDownloadAndMuxService.UPDATE_PROGRESS, resultData);
	    		
	    		Utils.scanMedia(getApplicationContext(), 
						new String[] {muxedVideo.getAbsolutePath()}, 
						new String[] {"video/*"});
	    		
	    		Json.addEntryToJsonFile(
	    				FfmpegDownloadAndMuxService.this, 
	    				String.valueOf(System.currentTimeMillis()),
						YTD.JSON_DATA_TYPE_V, 
						videoId,
						pos,
						YTD.JSON_DATA_STATUS_COMPLETED,
						muxedVideo.getParent(),
						muxedFileName, 
						Utils.getFileNameWithoutExt(muxedFileName), 
						"", 
						Utils.MakeSizeHumanReadable((int) muxedVideo.length(), false), 
						true);
			} else {
				setNotificationForAudioJobError();
				
				Json.addEntryToJsonFile(
						FfmpegDownloadAndMuxService.this, 
						String.valueOf(System.currentTimeMillis()),
						YTD.JSON_DATA_TYPE_V,  
						videoId,
						pos,
						YTD.JSON_DATA_STATUS_FAILED,
						muxedVideo.getParent(),
						muxedFileName, 
						Utils.getFileNameWithoutExt(muxedFileName), 
						"", 
						"-", 
						true);
			}
			DashboardActivity.refreshlist();
		}

		@Override
		public void processNotStartedCheck(boolean started) {
			if (!started) {
				Utils.logger("w", "FFmpeg process not started or not completed", DEBUG_TAG);
				setNotificationForAudioJobError();
			}
		}
		
		public void setNotificationForAudioJobError() {
			Log.e(DEBUG_TAG, muxedFileName + " MUX failed");
			
			Bundle resultData = new Bundle();
	        resultData.putBoolean("error", true);
	        receiver.send(FfmpegDownloadAndMuxService.UPDATE_PROGRESS, resultData);
		}

		@Override
		public void preProcess() {
			// TODO Auto-generated method stub
			
		}
	}
}
