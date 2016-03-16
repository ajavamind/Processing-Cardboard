# Processing-Cardboard
This repository is an implementation of Processing-Android with Google Android Cardboard SDK.
With this code you can write Processing sketches for Android apps to use stereo graphics/photo and Google Cardboard/VR features.

 The objective of this work is to provide an easier 
 development platform using Processing for learning to program VR Android apps intended for the Cardboard HMD viewer.
 The  Processing-Android library was modified to use the Cardboard SDK.
 The Processing library has an abstraction layer for OPENGL that makes it possible
 to write an Android Cardboard app without using direct Android OPENGL calls. Included are example Android apps for Google Cardboard   to demonstrate coding with the Processing Language for Android library and
 the Google Cardboard Android SDK. 
 
 Processing with Cardboard SDK is an alternative for writing Android VR applications. At the least it is
 another way to build/explore/learn the Cardboard VR app development platform.
 
 * Works with Android Studio (1.5.1)
 * Cardboard SDK for Android 0.6.0
 * Based on Processing for Android library version 3.0.0, 3.0.1, 3.0.2 RC1 Android Mode 247 (as of 2016/03/07)
   with Processing core source code from: https://github.com/processing/processing-android.
 * Based on Stereo library source code from: https://github.com/CreativeCodingLab/stereo
 
 * Minimum builds supports Android API 4.1 (16) platform and above
 * Tested with Sony Z1S phone, 1920 x 1080 pixel display, running Android version 5.0.2, with GPU hardware accelerator
 * Tested with Samsung Galaxy S6 phone, 2560 x 1440 pixel display, running Android version 5.1.1
 
 * Example App description
 
The app displays a stereo photo cube in front of a stereo photo background. In the cardboard viewer the user may change the viewing angle and size of the cube with head movement. A screen tap will bring the cube back to its original location. Tilting the viewer left or right will change the cube size. The app also shows how to display text and graphics (reticle).
 
 * Issues:
 
Distortion correction can be enabled, but the eye separation needs adjustment for viewing.
 The display is not distorted enough to matter with some Unofficial cardboard viewer lens and
 home made Cardboard viewer with stereoscopic quality lens.
 
 Out of memory can result when using large images or restarting the app.
 
 No library build was defined here to make a Processing Android SDK library 
 
 Notes:
 The magnet trigger does not work well with my phone so I use new convert tap to trigger feature
 available in Cardboard V2.

 * Modified the Processing-Android core Library to work directly with the Cardboard SDK:

 * PApplet extends CardboardActivity
 
 * The main display thread used by Cardboard SDK for its display rendering replaces the display thread in Processing Android that calls draw().

 * Added draw functions for left and right eye in Processing

 * SketchSurfaceView extends CardboardView

 * SketchSurfaceViewGL extends CardboardView

 * CardboardView rendering uses CardboardView.Renderer

 * CardboardView.StereoRenderer code is also available but not used.

 * Added PStereo class for stereo view control
  
 * Added headtransform, drawleft, drawright functions to Processing
  
 * Added external keyboard codes for remote Bluetooth controller key press.
 

 The following Processing-Android files were modified for this conversion:
 
   PGL.java,  PGLES.java,  PGraphicsOpenGL.java
   PApplet.java,  PConstants.java,  PGraphics.java,  PGraphicsAndroid2D.java,  PImage.java
 
 Moved processing/opengl/shaders folder to assets folder
 
 Cardboard is a trademark of Google Inc.
