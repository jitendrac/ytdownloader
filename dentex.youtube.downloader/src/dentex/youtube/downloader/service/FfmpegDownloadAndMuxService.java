package dentex.youtube.downloader.service;

import java.io.File;
import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;
import android.util.Log;
import dentex.youtube.downloader.ffmpeg.FfmpegController;

public class FfmpegDownloadAndMuxService extends IntentService {
	
	public FfmpegDownloadAndMuxService() {
		super("FfmpegDownloadAndMuxService");
	}

	String DEBUG_TAG = "FfmpegDownloadAndMuxService";
	File muxedVideo;
	String muxedFileName;
	private String muxedPath;
	
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

		MuxShellDummy shell = new MuxShellDummy(this);

		try {
			ffmpeg.downloadAndMuxAoVoStreams(A_LINK, V_LINK, muxedVideo, shell);
		} catch (IOException e) {
			Log.e(DEBUG_TAG, "IOException running ffmpeg" + e.getMessage());
		} catch (InterruptedException e) {
			Log.e(DEBUG_TAG, "InterruptedException running ffmpeg" + e.getMessage());
		}
	}
}
