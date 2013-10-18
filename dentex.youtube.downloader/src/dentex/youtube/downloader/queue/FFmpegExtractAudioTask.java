package dentex.youtube.downloader.queue;

import java.io.File;

import android.content.Context;
import android.util.Log;
import dentex.youtube.downloader.DashboardActivity;
import dentex.youtube.downloader.YTD;
import dentex.youtube.downloader.ffmpeg.FfmpegController;
import dentex.youtube.downloader.ffmpeg.ShellUtils.ShellCallback;
import dentex.youtube.downloader.utils.Json;
import dentex.youtube.downloader.utils.Utils;

public class FFmpegExtractAudioTask implements Runnable {

	private static final String DEBUG_TAG = "FFmpegExtractAudioTask";
	private Context aContext;
	private File aFileToConvert;
	private File aAudioFile;
	private String aBitrateType;
	private String aBitrateValue;
	private String aYtId;
	private int aPos;
	
	public FFmpegExtractAudioTask(Context context, 
			File fileToConvert, File audioFile, 
			String bitrateType, String bitrateValue, 
			String YtId, int pos) {
		aContext = context;
		aFileToConvert = fileToConvert;
		aAudioFile = audioFile;
		aBitrateType = bitrateType;
		aBitrateValue = bitrateValue;
		aYtId = YtId;
		aPos = pos;
	}
	
	@Override
	public void run() {
		FfmpegController ffmpeg = null;
		try {
			ffmpeg = new FfmpegController(aContext);
			ShellDummy shell = new ShellDummy();
			ffmpeg.extractAudio(aFileToConvert, aAudioFile, aBitrateType, aBitrateValue, shell);
		} catch (Throwable t) {
			Log.e(DEBUG_TAG, "Error in FFmpegExtractAudioTask", t);
		}
	}
	
	private class ShellDummy implements ShellCallback {

		@Override
		public void shellOut(String shellLine) {
			//Utils.logger("d", shellLine, DEBUG_TAG);
		}

		@Override
		public void processComplete(int exitValue) {
			Utils.logger("v", aAudioFile.getName() + "':\nprocessComplete with exit value: " + exitValue, DEBUG_TAG);
			
			String newId = String.valueOf(System.currentTimeMillis());
			//boolean removeVideo;
			
			String type;
			if (aBitrateValue == null) {
				type = YTD.JSON_DATA_TYPE_A_E;
			} else {
				type = YTD.JSON_DATA_TYPE_A_M;
			}
			
			if (exitValue == 0) {
				Utils.scanMedia(aContext, 
						new String[] {aAudioFile.getPath()}, 
						new String[] {"audio/*"});
				
				//TODO removeVideo check

				Json.addEntryToJsonFile(
						aContext, 
						newId, 
						type, 
						aYtId, 
						aPos,
						YTD.JSON_DATA_STATUS_COMPLETED,
						aAudioFile.getParent(), 
						aAudioFile.getName(), 
						Utils.getFileNameWithoutExt(aAudioFile.getName()), 
						"", 
						Utils.MakeSizeHumanReadable((int) aAudioFile.length(), false), 
						false);
			} else {
				Json.addEntryToJsonFile(
						aContext, 
						newId, 
						type, 
						aYtId, 
						aPos,
						YTD.JSON_DATA_STATUS_FAILED,
						aAudioFile.getParent(), 
						aAudioFile.getName(), 
						Utils.getFileNameWithoutExt(aAudioFile.getName()), 
						"", 
						"-", 
						false);
			}
			
			if (DashboardActivity.isDashboardRunning)
				DashboardActivity.refreshlist(DashboardActivity.sDashboardActivity);
		}

		@Override
		public void processNotStartedCheck(boolean started) {
			if (!started) {
				Utils.logger("w", "FFmpegExtractAudioTask process not started or not completed", DEBUG_TAG);
			}
		}
	}
}
