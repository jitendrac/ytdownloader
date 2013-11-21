package dentex.youtube.downloader.service;

import java.io.File;
import java.io.IOException;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;

import dentex.youtube.downloader.DashboardActivity;
import dentex.youtube.downloader.R;
import dentex.youtube.downloader.YTD;
import dentex.youtube.downloader.ffmpeg.FfmpegController;
import dentex.youtube.downloader.ffmpeg.ShellUtils.ShellCallback;
import dentex.youtube.downloader.utils.Json;
import dentex.youtube.downloader.utils.Utils;

public class FfmpegDownloadAndMuxService extends Service {
	
	private String DEBUG_TAG = "FfmpegDownloadAndMuxService";
	private File muxedVideo;
	private String muxedFileName;
	private String muxedPath;
	private NotificationCompat.Builder mBuilder;
	private NotificationManager mNotificationManager;
	
	private String A_LINK;
	private String V_LINK;
	private int pos;
	private String videoId;
	
	@Override
	public void onCreate() {
		Utils.logger("d", "service created", DEBUG_TAG);
		BugSenseHandler.initAndStartSession(this, YTD.BugsenseApiKey);
		BugSenseHandler.leaveBreadcrumb("FfmpegDownloadAndMuxService_onCreate");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		A_LINK = intent.getStringExtra("A_LINK");
		V_LINK = intent.getStringExtra("V_LINK");
		
		pos = intent.getIntExtra("POS", 0);
		videoId = intent.getStringExtra("YT_ID");
		
		muxedFileName = intent.getStringExtra("FILENAME");
		muxedPath = intent.getStringExtra("PATH");
		muxedVideo = new File(muxedPath, muxedFileName);
		
		fdam();
		
		super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Utils.logger("d", "service destroyed", DEBUG_TAG);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void fdam() {
		mBuilder =  new NotificationCompat.Builder(FfmpegDownloadAndMuxService.this);
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder.setSmallIcon(R.drawable.ic_stat_ytd);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				
				FfmpegController ffmpeg = null;
				try {
					ffmpeg = new FfmpegController(FfmpegDownloadAndMuxService.this);
					
					mBuilder.setContentTitle(muxedFileName);
					mBuilder.setContentText("MUX " + getString(R.string.json_status_in_progress));
					mBuilder.setOngoing(true);
					mBuilder.setProgress(100, 0, true);
					mNotificationManager.notify(4, mBuilder.build());
					
				} catch (IOException ioe) {
					Log.e(DEBUG_TAG, "Error loading ffmpeg. " + ioe.getMessage());
				}
		
				ShellDummy shell = new ShellDummy();
		
				try {
					ffmpeg.downloadAndMuxAoVoStreams(A_LINK, V_LINK, muxedVideo, shell);
				} catch (IOException e) {
					Log.e(DEBUG_TAG, "IOException running ffmpeg" + e.getMessage());
				} catch (InterruptedException e) {
					Log.e(DEBUG_TAG, "InterruptedException running ffmpeg" + e.getMessage());
				}
				
				Looper.loop();
			}
		}).start();
	}
	
	private class ShellDummy implements ShellCallback {

		@Override
		public void shellOut(String shellLine) {
			int[] times = Utils.getAudioJobProgress(shellLine);
			if (times[0] != 0) {
				mBuilder.setProgress(times[0], times[1], false);
				mNotificationManager.notify(4, mBuilder.build());
			}
			
			Utils.logger("d", shellLine, DEBUG_TAG);
		}

		@Override
		public void processComplete(int exitValue) {
			Utils.logger("i", "FFmpeg process exit value: " + exitValue, DEBUG_TAG);
			
			Intent muxIntent = new Intent(Intent.ACTION_VIEW);
			if (exitValue == 0) {
				mBuilder.setContentTitle(muxedFileName);
				mBuilder.setContentText("MUX " + getString(R.string.json_status_completed));
				mBuilder.setOngoing(false);
				muxIntent.setDataAndType(Uri.fromFile(muxedVideo), "video/*");
				PendingIntent contentIntent = PendingIntent.getService(FfmpegDownloadAndMuxService.this, 0, muxIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        		mBuilder.setContentIntent(contentIntent);
        		
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
		
		if (DashboardActivity.isDashboardRunning)
			DashboardActivity.refreshlist(DashboardActivity.sDashboardActivity);
		
		Utils.setNotificationDefaults(mBuilder);
		
		mBuilder.setProgress(0, 0, false);
		mNotificationManager.cancel(4);
		mNotificationManager.notify(4, mBuilder.build());
		
		stopSelf();

		}

		@Override
		public void processNotStartedCheck(boolean started) {
			if (!started) {
				Utils.logger("w", "FFmpeg process not started or not completed", DEBUG_TAG);
				setNotificationForAudioJobError();
			}
			mNotificationManager.notify(4, mBuilder.build());
			
			stopSelf();
		}
		
		public void setNotificationForAudioJobError() {
			Log.e(DEBUG_TAG, muxedFileName + " MUX failed");
			Toast.makeText(FfmpegDownloadAndMuxService.this,  "YTD: " + muxedFileName + " MUX failed", Toast.LENGTH_SHORT).show();
			mBuilder.setContentText("MUX " + getString(R.string.json_status_failed));
			mBuilder.setOngoing(false);
		}
	}
}
