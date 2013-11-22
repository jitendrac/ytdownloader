package dentex.youtube.downloader.service;

import android.os.Bundle;
import android.util.Log;
import dentex.youtube.downloader.DashboardActivity;
import dentex.youtube.downloader.YTD;
import dentex.youtube.downloader.ffmpeg.ShellUtils.ShellCallback;
import dentex.youtube.downloader.utils.Json;
import dentex.youtube.downloader.utils.Utils;

class MuxShellDummy implements ShellCallback {

	private FfmpegDownloadAndMuxService fdamService;

	/**
	 * @param fdamService
	 */
	MuxShellDummy(FfmpegDownloadAndMuxService ffmpegDownloadAndMuxService) {
		this.fdamService = ffmpegDownloadAndMuxService;
	}

	@Override
	public void shellOut(String shellLine) {
		int[] times = Utils.getAudioJobProgress(shellLine);
			
		Bundle resultData = new Bundle();
        resultData.putInt("progress" , times[1]);
        resultData.putInt("total" , times[0]);
        this.fdamService.receiver.send(FfmpegDownloadAndMuxService.UPDATE_PROGRESS, resultData);
		
		Utils.logger("d", shellLine, this.fdamService.DEBUG_TAG);
	}

	@Override
	public void processComplete(int exitValue) {
		Utils.logger("i", "FFmpeg process exit value: " + exitValue, this.fdamService.DEBUG_TAG);

		if (exitValue == 0) {
    		Bundle resultData = new Bundle();
            resultData.putInt("progress" , -1);
            resultData.putInt("total" , -1);
            this.fdamService.receiver.send(FfmpegDownloadAndMuxService.UPDATE_PROGRESS, resultData);
    		
    		Utils.scanMedia(this.fdamService.getApplicationContext(), 
					new String[] {this.fdamService.muxedVideo.getAbsolutePath()}, 
					new String[] {"video/*"});
    		
    		Json.addEntryToJsonFile(
    				this.fdamService, 
    				String.valueOf(System.currentTimeMillis()),
					YTD.JSON_DATA_TYPE_V, 
					this.fdamService.videoId,
					this.fdamService.pos,
					YTD.JSON_DATA_STATUS_COMPLETED,
					this.fdamService.muxedVideo.getParent(),
					this.fdamService.muxedFileName, 
					Utils.getFileNameWithoutExt(this.fdamService.muxedFileName), 
					"", 
					Utils.MakeSizeHumanReadable((int) this.fdamService.muxedVideo.length(), false), 
					true);
		} else {
			setNotificationForAudioJobError();
			
			Json.addEntryToJsonFile(
					this.fdamService, 
					String.valueOf(System.currentTimeMillis()),
					YTD.JSON_DATA_TYPE_V,  
					this.fdamService.videoId,
					this.fdamService.pos,
					YTD.JSON_DATA_STATUS_FAILED,
					this.fdamService.muxedVideo.getParent(),
					this.fdamService.muxedFileName, 
					Utils.getFileNameWithoutExt(this.fdamService.muxedFileName), 
					"", 
					"-", 
					true);
	}
	
	if (DashboardActivity.isDashboardRunning)
		DashboardActivity.refreshlist(DashboardActivity.sDashboardActivity);
	}

	@Override
	public void processNotStartedCheck(boolean started) {
		if (!started) {
			Utils.logger("w", "FFmpeg process not started or not completed", this.fdamService.DEBUG_TAG);
			setNotificationForAudioJobError();
		}
	}
	
	public void setNotificationForAudioJobError() {
		Log.e(this.fdamService.DEBUG_TAG, this.fdamService.muxedFileName + " MUX failed");
		
		Bundle resultData = new Bundle();
        resultData.putBoolean("error", true);
        this.fdamService.receiver.send(FfmpegDownloadAndMuxService.UPDATE_PROGRESS, resultData);
	}
}