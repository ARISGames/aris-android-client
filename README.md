# aris-android-client

### 2/28/2017: Added Installable APK Builds for Brave and Intrepid Android Testers.
  The apk's can be found at the top level of the project tree. Currently there are two available:
  one from October of 2016, which was the point at which the first development phase of the Android
  build had come to an end, and another from 2/28/17. This older build was working with smaller
  games as tested on Android OS versions Lollipop (5.1), and KitKat (4.4.x). It appears to run less
  happily on OSs newer than those, at least in the brief attempts I made with a Samsung Galaxy S7
  running Marshmallow (6.0.1). The newer apk seemed to hate everything, but I put it up anyway in
  case it might just be me.

  If you're not familiar with how to manually install an apk, it's pretty straightforward: Copy the
  apk file to (for instance) the Downloads folder on your device with something like Android File
  Manager; alternatively you can email the apk to the device, or use a cloudy file host like Google
  Drive or DropBox. Once the apk is on the device, locate the file and open it. You will be asked if
  you want to install the app. Do so.

  One word of advice. Be very patient while running the app. There will be screen transitions that
  might appear to have stalled out, or are displaying a progress circle thingie. Let it finish before
  tapping anxiously on other things. If the app has a lot of large media files associated with it, it
  very well may crash the app. Painfully little memory management safeguarding code exists in this
  version of the app.


### 9/29/2016: Most Game Features are Working; Some Still to be Completed
  The ARIS Android app code is substantially complete. It is capable of loading and playing most
  games with fairly straightforward common requirements including, maps, items, plaques, dialogs,
  and web page views. Scanner, Decoder and Notebook tabs are stubbed in but not functional. the Quests
  tab is in place, but not tested. Sound media does not appear to be working.

  At this point the app is quite runnable, again, for simple game demonstrations, but do note that
  very little QA has been applied to this very raw collection of source code, and there WILL be bugs.

  Please feel free to test to your heart's content and send feedback on bugs, missing features,
  strange behaviors, etc.


### 11/10/2015: initial push to GitHub of this project.
  The code is runnable without crashing, but is far from complete. Presently it presents all the 
  essential screens for logging in, selecting a game and initiating the start of game play; from this
  screen on, there are only stubs of what will be the game play fragments.

 