package dentex.youtube.downloader.queue;

import java.io.File;

import android.content.Context;
import android.util.Log;
import dentex.youtube.downloader.ffmpeg.FfmpegController;
import dentex.youtube.downloader.ffmpeg.ShellUtils.ShellCallback;
import dentex.youtube.downloader.utils.Utils;

public class AutoFFmpegTask implements Runnable {

	private static final String DEBUG_TAG = "TestTask";
	private Context aContext;
	private File aFileToConvert;
	private File aAudioFile;
	private String aBitrateType;
	private String aBitrateValue;
	
	public AutoFFmpegTask(Context context, File fileToConvert, File audioFile, String bitrateType, String bitrateValue) {
		aContext = context;
		aFileToConvert = fileToConvert;
		aAudioFile = audioFile;
		aBitrateType = bitrateType;
		aBitrateValue = bitrateValue;
	}
	
	@Override
	public void run() {
		FfmpegController ffmpeg = null;
		try {
			ffmpeg = new FfmpegController(aContext);
			ShellDummy shell = new ShellDummy();
			ffmpeg.extractAudio(aFileToConvert, aAudioFile, aBitrateType, aBitrateValue, shell);
		} catch (Throwable t) {
			Log.e(DEBUG_TAG, "Error in TestTask", t);
		}
	}
	
	private class ShellDummy implements ShellCallback {

		@Override
		public void shellOut(String shellLine) {
			// unused
		}

		@Override
		public void processComplete(int exitValue) {
			Log.i(DEBUG_TAG, "AutoFFmpegTask for '" + aAudioFile.getName() + "': processComplete");
			
			Utils.scanMedia(aContext, 
					new String[] {aAudioFile.getPath()}, 
					new String[] {"audio/*"});
			
			// TODO add entry to Dashboard (if it's running);
		}

		@Override
		public void processNotStartedCheck(boolean started) {
			if (!started) {
				Utils.logger("w", "Auto FFmpeg task process not started or not completed", DEBUG_TAG);
			}
		}
	}
}
