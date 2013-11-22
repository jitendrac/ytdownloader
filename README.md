**YouTube Downloader for Android _ by dentex**

Test version
-----

MD5 checksum: `f146f79eb75bd9e4eb6d10c9cc623b16` dentex.youtube.downloader_v3.4.1.003-fdam.apk

*Copyright (C) 2012-2013  Samuele Rini*

**Reference**

XDA thread:  
http://forum.xda-developers.com/showpost.php?p=47734954&postcount=87

**INFO**

To be able to download the 1080p and higher YouTube video resolutions, 
I started the implementation of the new feature about download and MUX 
of the two video-only and audio-only dash streams from YouTube.

Instructions:
---
*(Please read carefully)*  

**Prerequisite**

If you have already downloaded the FFmpeg executable, delete the 
app's data and download again FFmpeg to have a newer version that 
supports this stuff (the app still doesn't have this check)

**To use the test feature**  

- enable the secondary (video-only and audio-only) streams into prefs;  
- go share a video to YTD (obviously);  
- click to download a "VO" stream.

This stream should be downloaded together with the higher quality 
audio-only steam available and muxed at the same time.

Download and mux is done by FFmpeg itself, so no pause/resume for now. 
The user can decide whether to proceed like this OR 
download manually two appropriate streams (and thus use the download 
manager) and then mux from the Dashboard [not implemented yet].

If everything works you should see a progress bar for this process
(this bar goes back to finish download the 2nd stream)
and after completion an entry into the dashboard (and a video into the 
stock gallery app of course).

*KNOWN ISSUE*
---
Although improved with this latest test version, it seems that the 
completion shown into the notification can "freeze" for very long 
operations, while the download completes in background.
