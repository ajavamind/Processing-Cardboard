# Processing-Cardboard

This repository is an implementation of Processing-Android with Google VR SDK for Android (Cardboard).
With this code you can write Processing sketches for Android apps to use stereo graphics/photo and Google VR/Cardboard features.

 The objective of this work is to provide an easier development platform by using Processing to learn about and program Google VR Android apps intended for the Cardboard HMD (Head  Mounted Device) viewer.
 
 The  Processing-Android core library was modified to use the Google VR (Cardboard) SDK replacing the main draw thread with the Cardboard rendering thread. The Processing-Android core library has an abstraction layer for OPENGL that makes it possible to write an Android Cardboard app without using direct Android OPENGL calls. 
 
 Included are example Android apps for Google VR Cardboard to demonstrate coding with the Processing Language for Android library and the Google Cardboard Android SDK. 
 
 Processing with Cardboard VR SDK is an alternative for writing Android VR applications. 
 It is another way to use/explore/learn the Cardboard VR app development platform.
 
 
![Transion Screenshot](Screenshot_2016-04-01-15-43-29.png)

![DisplayOBJ Screenshot](Screenshot_2016-04-01-15-43-49.png)

 
## Implementation
 
 * Works with Android Studio (2.3.3) build tools 25.0.2
 * Google VR SDK for Android libraries version 1.80.0 (aar files from jcenter )
 * Based on Processing for Android library version 3.0.0, 3.0.1, 3.0.2 RC1 Android Mode 247 (as of 2016/03/07)
   with Processing-Android core source code from: https://github.com/processing/processing-android.
 * Based on Stereo library source code from: https://github.com/CreativeCodingLab/stereo
 * Minimum builds supports Android API 4.4 (19) platform and above. AndroidManifest.xml is set for Kitkat 4.4 (19)
 * Tested with Sony Z1S phone, 1920 x 1080 pixel display, running Android version 5.0.2, with GPU hardware accelerator
 * Tested with Samsung Galaxy S7 phone, 2560 x 1440 pixel display, running Android version 7.0
 
## Processing-Android Modifications

 * Modified the Processing-Android core Library to work directly with the Google VR SDK for Android (Cardboard).

 * PApplet extends GvrActivity
 
 * The main display thread used by Google Cardboard VR SDK for its display rendering replaces the display thread 
 in Processing-Android that calls draw().

 * Added head transform and draw functions for left and right eye in Processing: headtransform(), drawleft(), drawright()

 * SketchSurfaceView extends GvrView

 * SketchSurfaceViewGL extends GvrView

 * GvrView rendering uses GvrView.StereoRenderer

 * GvrView.Renderer code is also available but not used.

 * Added PStereo class for stereo view control
  
 * Added external keyboard codes for remote Bluetooth controller key press.
 
 * Moved processing/opengl/shaders folder to assets folder

 * The following Processing-Android core files were modified for this conversion:
 
    - PGL.java,  
    - PGLES.java  
    - PGraphicsOpenGL.java
    - PApplet.java
    - PConstants.java
    - PGraphics.java
    - PGraphicsAndroid2D.java
    - PImage.java
 
## Example App Description
 
The repository has two example apps. 

MainActivity displays a stereo photo cube in front of a stereo photo background. 
In the cardboard viewer the user may change the viewing angle and size of the cube with head movement. 
A screen tap will bring the cube back to its original location. 
Tilting the HMD viewer left or right will change the cube size.
The app examples also show how to display text and graphics (reticle).

No distortion correction:

![No Distortion Correction Screenshot](Screenshot_2016-01-24-10-35-00.png)

Distortion correction enabled:

![Distortion Correction Screenshot](Screenshot_2016-03-31-16-05-40.png)

## Issues:
 
  1. Out of memory can result when using large images or restarting the app. Images need to be recycled by the app.
  2. Line drawing to the screen directly is very slow. (Processing-Android OPENGL)
 
## Updates:
 
### 2016/04/01

  1. Distortion correction can be enabled, but the eye separation may need adjustment for viewing.
  The display may not be distorted enough to matter with some Unofficial cardboard viewer lens and
   home made Cardboard viewer with stereoscopic quality wide field of view lens.
  2. Text and line drawing now work on screen directly in the correct eye viewport.
  3. Modifications to use Cardboard libraries version 0.7.0.
  
### 2016/05/21
  
  1. Modifications to use Google VR SDK for Android (Cardboard) version 0.8.0
  2. Removed use of android.opengl.GLSurfaceView in PApplet because it is not accessible with Google VR SDK version 0.8.0
  
### 2016/05/23
  
  1. Increase Matrix stack depth.

### 2016/08/17
  
  1. Modifications to use Google VR SDK for Android (Cardboard) version 0.9.1. This is now labeled "Stereo Mode" by Google to distinguish from Android release N VR Mode.
  2. Add constants for more keys used by bluetooth remote controllers

### 2016/10/02
  
  1. Modifications to use Google VR SDK for Android (Cardboard) version 1.0.0.
  2. Use Android Studio 2.2
  
### 2017/01/16
  
  1. Modifications to use Google VR SDK for Android (Cardboard) version 1.10.0.
  2. Use Android Studio 2.2.3
  
### 2017/09/05
  
  1. Modifications to use Google VR SDK for Android (Cardboard) version 1.80.0.
  2. Use Android Studio 2.3.3
  3. Change the headTransform positioning logic.
  
  
## Notes:
  1. The magnet pull trigger is no longer supported. Please use Cardboard V2 headset.
  2. No library build is defined here to make a Processing Android SDK library or mode for use with the Processing IDE.
  3. Starting with Google VR SDK, version 0.8.0, the minimum build is for Android 4.4 (19). This happens to be better for faster image/photo processing and display.
  4. With release 1.0.0 Google removed capability to not display the alignment marker and settings button.

## Processing Coding Notes:
  1. You must include a call to background() in the draw() method. 
  If not the result will be that nothing appears on the screen in VR mode.
  2. Google VR SDK expects each frame to be redrawn in VR mode, so you cannot assume graphics to remain on the screen or built up with each frame. 
  However in monocular mode (VR mode disabled), the screen can be built up with graphics until a call is made to background().  
  3. When using distortion correction (default enabled), you may need to adjust the eyeSeparation parameter to suit. The example apps assume distortion correction enabled. 

## Demo App in Google Play Store
I wrote a simple demonstration arcade game/simulation, Mosquito Swarm, for Google Cardboard VR based on the code Processing-Cardboard. Check it out at:

https://play.google.com/store/apps/details?id=com.modla.andy.swarm3dfree

## Credits

 Processing Foundation  http://processing.org
 
 Creative Coding Lab is the EVL Creative Coding Research Group at The University of Illinois at Chicago https://www.evl.uic.edu/creativecoding/about.php
 
 Cardboard is a trademark of Google Inc. http://google.com/cardboard 
 
 View-Master is a trademark of Mattel Inc.
