# Processing-Cardboard
Example Processing Library Stereo/VR sketch using Google Android Cardboard SDK

 This example program is an Android activity coded with the Processing for Android library and
 the Google Cardboard Android SDK.
 The Processing library has an abstraction layer for OPENGL that makes it possible
 to write an Android Cardboard app without needing direct Android OPENGL calls.
 Using Processing with Cardboard SDK may be an alternative for writing Android VR applications.
 
 * Requires Android Studio (1.2.2)
 * Cardboard SDK for Android 0.5.4
 * minimum Android API 4.1 (16)
 * Tested with Sony Z1S phone, 1920x1080 pixel display, running Android version 5.0.2, GPU hardware
 
 
 * Issues:
Distortion correction is disabled because the Cardboard correction feature does not work well
 The display is not distorted enough to matter with my Unofficial cardboard viewer lens and
 home made viewer with stereoscopic quality lens.
 
 * Out of memory using large images
 
 
 Notes:
 The magnet trigger does not work well with my phone so I use new convert tap to trigger feature
 available in Cardboard V2.
 
 * No library build was defined here for processing SDK since this is a work in progress
 
 * PStereo was not hidden in PApplet for greater control. Abstraction functions possible.

 * Changes to Processing Library:

 * PApplet extends CardboardActivity

 * SketchSurfaceView extends CardboardView

 * SketchSurfaceViewGL extends CardboardView

 * CardboardView rendering uses CardboardView.Renderer

 * CardboardView.StereoRenderer code is also available

 * added PStereo class for stereo view control
 
 
 Cardboard is a trademark of Google Inc.
