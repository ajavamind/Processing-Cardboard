/*
  Example Processing VR sketch using Google Android Cardboard SDK

  Copyright (c) 2015 Andy Modla

  Modifications to Processing Android library to use Google Cardboard library.

  This source code is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License version 2.1 as published by the Free Software Foundation.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/

package com.modla.andy.processingcardboard;

/**
 * Description
 * This example program is an Android activity coded with the Processing for Android library and
 * the Google Cardboard Android SDK.
 * The Processing library has an abstraction layer for OPENGL that makes it possible
 * to write an Android Cardboard app without needing direct Android OPENGL calls.
 * Using Processing with Cardboard SDK may be an alternative for writing Android VR applications.
 * <p/>
 *
 * <p/>
 * Requires Android Studio (1.2.2)
 * <p/>
 * Cardboard SDK for Android 0.5.4
 * <p/>
 * minimum Android API 4.1 (16)
 * <p/>
 * Tested with Sony Z1S phone, 1920x1080 pixel display, running Android version 5.0.2, GPU hardware
 *
 * <p/>
 * issues:
 * Distortion correction is disabled because the Cardboard correction feature does not work well
 * The display is not distorted enough to matter with my Unofficial cardboard viewer lens and
 * home made viewer with stereoscopic quality lens.
 * <p/>
 * Out of memory using large images
 * <p/>
 * <p/>
 * notes:
 * The magnet trigger does not work well with my phone so I use new convert tap to trigger feature
 * available in Cardboard V2.
 * <p/>
 * No library build was defined for processing SDK since this is a work in progress
 * <p/>
 * PStereo was not hidden in PApplet for greater control. Abstraction functions possible.
 * <p/>
 * Changes to Processing Library:
 * <p/>
 * PApplet extends CardboardActivity
 * <p/>
 * SketchSurfaceView extends CardboardView
 * <p/>
 * SketchSurfaceViewGL extends CardboardView
 * <p/>
 * CardboardView rendering uses CardboardView.Renderer
 * <p/>
 * CardboardView.StereoRenderer code is also available
 * <p/>
 * added PStereo class for stereo view control
 * <p/>
 * Cardboard is a trademark of Google Inc.
 */

import android.content.Context;
import android.opengl.GLES20;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Vibrator;

import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.HeadTransform;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PStereo;


public class MainActivity extends PApplet {
    private static String TAG = "MainActivity";

    CardboardView cardboardView;
    private Vibrator vibrator;
    PImage[] photo = null;
    PImage[] photoRight = null;
    PImage backgroundLeft = null;
    PImage backgroundRight = null;

    static final float STARTX = 0f;
    static final float STARTY = 0f;
    static final float STARTZ = 10f;
    static final int XBOUND = 9;
    static final int YBOUND = 10;
    static final int ZBOUND_IN = 4;
    static final int ZBOUND_OUT = 36;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        cardboardView = (CardboardView) surfaceView;
        //cardboardView.setAlignmentMarkerEnabled(false);
        //cardboardView.setSettingsButtonEnabled(false);
        setCardboardView(cardboardView);
        cardboardView.setDistortionCorrectionEnabled(false);
        //cardboardView.setDistortionCorrectionEnabled(true);
        cardboardView.setChromaticAberrationCorrectionEnabled(false);
        //cardboardView.setChromaticAberrationCorrectionEnabled(true);
        //cardboardView.setVRModeEnabled(false); // sets Monocular mode
        //Log.d(TAG, "getVRMode=" + cardboardView.getVRMode());
        setConvertTapIntoTrigger(true);
        Log.d(TAG, "getConvertTapIntoTrigger=" + getConvertTapIntoTrigger());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.modla.andy.processingcardboard.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.modla.andy.processingcardboard.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //    @Override
    public void onCardboardTrigger() {
        // user feedback
        vibrator.vibrate(50);
        cameraPositionX = STARTX;
        cameraPositionY = STARTY;
        cameraPositionZ = STARTZ;
        cardboardView.resetHeadTracker();
    }

    /**
     * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
     *
     * @param label Label to report in case of error.
     */
    private static void checkGLError(String label) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, label + ": glError " + error);
            throw new RuntimeException(label + ": glError " + error);
        }
    }

    private static void clearGlError() {
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR) {
            ;
        }
    }

    private static void checkGlError(String op) {
        int error;
        if ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        cardboardView.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        cardboardView.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Processing sketch for the Android app
    // The app shows a rotating stereo photo cube in front of a 3D background photo.
    // Head movement/direction or keyboard input determines the viewers location and direction.
    // Tap the screen to reset to starting view.
    //

    float rotx = 0; //PI / 4;
    float roty = 0; //PI / 4;
    float cubeScale = 2.0f;
    PShape texCube;
    PShape texCubeRight;
    PShape backgroundFrameLeft;
    PShape backgroundFrameRight;
    float nearPlane = .1f;
    float farPlane = 1000f;
    float convPlane = 20.0f;
    float eyeSeparation;
    float fieldOfViewY = 45f;
    float cameraPositionX = STARTX;
    float cameraPositionY = STARTY;
    float cameraPositionZ = STARTZ;
    PStereo stereo = null;
    float[] headView = new float[16];

    @Override
    public void settings() {
        // set size to full screen dimensions
        //size(displayWidth, displayHeight, P3D);  // equivalent to OPENGL
        // Processing variables displayWidth and displayHeight are your phone screen dimensions
        size(displayWidth, displayHeight, OPENGL);
        println("settings()");
    }

    /**
     * One time initial call to set up your Processing sketch variables, etc.
     */
    @Override
    public void setup() {
        background(0);
        strokeWeight(8.0f);
        // load images for cube face textures
        if (photo == null) {
            photo = new PImage[6];
            photo[0] = loadImage("data/IMG_0506_l.JPG");
            photo[1] = loadImage("data/IMG_0510_l.JPG");
            photo[2] = loadImage("data/IMG_0513_l.JPG");
            photo[3] = loadImage("data/IMG_0516_l.JPG");
            photo[4] = loadImage("data/IMG_0519_l.JPG");
            photo[5] = loadImage("data/IMG_0524_l.JPG");
            photoRight = new PImage[6];
            photoRight[0] = loadImage("data/IMG_0506_r.JPG");
            photoRight[1] = loadImage("data/IMG_0510_r.JPG");
            photoRight[2] = loadImage("data/IMG_0513_r.JPG");
            photoRight[3] = loadImage("data/IMG_0516_r.JPG");
            photoRight[4] = loadImage("data/IMG_0519_r.JPG");
            photoRight[5] = loadImage("data/IMG_0524_r.JPG");
            //backgroundLeft = loadImage("data/IMG_0338_l.JPG");
            //backgroundRight = loadImage("data/IMG_0338_r.JPG");
            backgroundLeft = loadImage("data/IMG_0526_l.JPG");
            backgroundRight = loadImage("data/IMG_0526_r.JPG");
        }
        texCube = createCube(photo);
        texCubeRight = createCube(photoRight);
        backgroundFrameLeft = createFrame(backgroundLeft);
        backgroundFrameRight = createFrame(backgroundRight);

        /* second constructor, custom eye separation, custom convergence */
        stereo = new PStereo(
                this, width, height, eyeSeparation, fieldOfViewY,
                nearPlane,
                farPlane, PStereo.StereoType.SIDE_BY_SIDE,
                convPlane);

        //println("Screen Width="+ width + " Height="+height);
        // only needs to be called repeatedly if you are
        // changing camera position
        stereo.start(
                cameraPositionX, cameraPositionY, cameraPositionZ,
                0f, 0f, -1f,  // directionX, directionY, directionZ
                0f, 1f, 0f);  // upX, upY, upZ
        cardboardView.resetHeadTracker();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        // TODO release image resources
    }

    PShape createCube(PImage[] photo) {
        PShape texCube = createShape(GROUP);
        for (int i = 0; i < 6; i++) {
            PShape face = createShape();
            face.beginShape(QUADS);
            face.noStroke();
            face.textureMode(NORMAL);
            face.texture(photo[i]);
            switch (i) {
                case 0:
                    // +Z "front" face
                    face.vertex(-1, -1, 1, 0, 0);
                    face.vertex(1, -1, 1, 1, 0);
                    face.vertex(1, 1, 1, 1, 1);
                    face.vertex(-1, 1, 1, 0, 1);
                    break;
                case 1:
                    // -Z "back" face
                    face.vertex(1, -1, -1, 0, 0);
                    face.vertex(-1, -1, -1, 1, 0);
                    face.vertex(-1, 1, -1, 1, 1);
                    face.vertex(1, 1, -1, 0, 1);
                    break;
                case 2:
                    // +Y "bottom" face
                    face.vertex(-1, 1, 1, 0, 0);
                    face.vertex(1, 1, 1, 1, 0);
                    face.vertex(1, 1, -1, 1, 1);
                    face.vertex(-1, 1, -1, 0, 1);
                    break;
                case 3:
                    // -Y "top" face
                    face.vertex(-1, -1, -1, 0, 0);
                    face.vertex(1, -1, -1, 1, 0);
                    face.vertex(1, -1, 1, 1, 1);
                    face.vertex(-1, -1, 1, 0, 1);
                    break;
                case 4:
                    // +X "right" face
                    face.vertex(1, -1, 1, 0, 0);
                    face.vertex(1, -1, -1, 1, 0);
                    face.vertex(1, 1, -1, 1, 1);
                    face.vertex(1, 1, 1, 0, 1);
                    break;
                case 5:
                    // -X "left" face
                    face.vertex(-1, -1, -1, 0, 0);
                    face.vertex(-1, -1, 1, 1, 0);
                    face.vertex(-1, 1, 1, 1, 1);
                    face.vertex(-1, 1, -1, 0, 1);
                    break;
            }
            face.endShape(CLOSE);
            texCube.addChild(face);
        }

        return texCube;
    }

    void drawPhotoCube(PShape cube) {
        pushMatrix();
        scale(cubeScale);
        rotateX(rotx);
        rotateY(roty);
        shape(cube);
        popMatrix();
    }

    PShape createFrame(PImage photo) {
        PShape face = createShape();
        face.beginShape(QUAD);
        face.noStroke();
        face.textureMode(NORMAL);
        face.texture(photo);
        face.vertex(-1, -1, 0, 0, 0);
        face.vertex(1, -1, 0, 1, 0);
        face.vertex(1, 1, 0, 1, 1);
        face.vertex(-1, 1, 0, 0, 1);
        face.endShape();
        return face;
    }

    PShape createText(String text) {
        PImage texture = createImage(100, 20, RGB);
        texture.loadPixels();
        // TODO
        PShape face = createShape();
        face.beginShape(QUAD);
        face.noStroke();
        face.textureMode(NORMAL);
        face.texture(texture);
        face.vertex(-1, -1, 0, 0, 0);
        face.vertex(1, -1, 0, 1, 0);
        face.vertex(1, 1, 0, 1, 1);
        face.vertex(-1, 1, 0, 0, 1);
        face.endShape();
        return face;
    }

    /**
     * Draw photo in frame defined as a PShape. We use PShape
     * to display a photo in P3D/OPENGL Processing mode.
     *
     * @param frame Photo to display.
     */
    void drawFrame(PShape frame) {
        pushMatrix();
        scale(32);  // scale(8);
        // move frame back so cube does not get clipped by background image
        translate(0, 0, -.5f);
        shape(frame);

        popMatrix();
    }

    void drawText(String s) {
        pushMatrix();
        text(s, 0, 0, 0);
        popMatrix();
    }

    public void mouseDragged() {
        float rate = 0.01f;
        rotx += (pmouseY - mouseY) * rate;
        roty += (mouseX - pmouseX) * rate;
    }

    @Override
    public void headtransform(HeadTransform headTransform) {
        float[] quat = new float[4];
        headTransform.getQuaternion(quat, 0);
        // normalize quaternion
        float length = (float) Math.sqrt(quat[0] * quat[0] + quat[1] * quat[1] + quat[2] * quat[2] + quat[3] * quat[3]);
        int DIV = 10;
        float lowSpeed = .01f;
        float mediumSpeed = .02f;
        float highSpeed = .04f;
        float pitchSpeed = 0;
        float yawSpeed = 0;
        float rollSpeed = 0;
        if (length != 0) {
            int pitch = (int) ((quat[0] / length) * DIV);  // pitch up/down
            int yaw = (int) ((quat[1] / length) * DIV);  // yaw left/ right
            int roll = (int) ((quat[2] / length) * DIV);  // roll left/right
            //int w = (int) ((quat[3] / length) * DIV);  //
            //Log.d(TAG, "normalized quaternion " + pitch + " " + yaw + " " + roll );

            if (pitch >= 3)
                pitchSpeed = -highSpeed;
            else if (pitch <= -3)
                pitchSpeed = highSpeed;
            else if (pitch == 2)
                pitchSpeed = -mediumSpeed;
            else if (pitch == -2)
                pitchSpeed = mediumSpeed;
            else if (pitch == 1)
                pitchSpeed = -lowSpeed;
            else if (pitch == -1)
                pitchSpeed = lowSpeed;
            else
                pitchSpeed = 0;

            if (yaw >= 3)
                yawSpeed = -highSpeed;
            else if (yaw <= -3)
                yawSpeed = highSpeed;
            else if (yaw == 2)
                yawSpeed = -mediumSpeed;
            else if (yaw == -2)
                yawSpeed = mediumSpeed;
            else if (yaw == 1)
                yawSpeed = -lowSpeed;
            else if (yaw == -1)
                yawSpeed = lowSpeed;
            else
                yawSpeed = 0;

            if (roll >= 3)
                rollSpeed = -highSpeed;
            else if (roll <= -3)
                rollSpeed = highSpeed;
            else if (roll == 2)
                rollSpeed = -mediumSpeed;
            else if (roll == -2)
                rollSpeed = mediumSpeed;
            else if (roll == 1)
                rollSpeed = -lowSpeed;
            else if (roll == -1)
                rollSpeed = lowSpeed;
            else
                rollSpeed = 0;

            if ((cameraPositionX > XBOUND && yawSpeed < 0) ||
                    (cameraPositionX < -XBOUND && yawSpeed > 0) ||
                    (cameraPositionX <= XBOUND && cameraPositionX >= -XBOUND))
                cameraPositionX += yawSpeed;


            if ((cameraPositionY > YBOUND && pitchSpeed < 0) ||
                    (cameraPositionY < -YBOUND && pitchSpeed > 0) ||
                    (cameraPositionY <= YBOUND && cameraPositionY >= -YBOUND))
                cameraPositionY += pitchSpeed;

            if ((cameraPositionZ > ZBOUND_IN && rollSpeed < 0) ||
                    (cameraPositionZ < ZBOUND_OUT && rollSpeed > 0) ||
                    (cameraPositionZ <= ZBOUND_OUT && cameraPositionZ >= ZBOUND_IN))
                cameraPositionZ += rollSpeed;

            //Log.d(TAG, "Normalized quaternion " + pitch + " " + yaw + " " + roll + " Camera position "+ cameraPositionX + " " + cameraPositionY + " " + cameraPositionZ);
        } else {
            Log.d(TAG, "Quaternion 0");
        }

//        headTransform.getHeadView(headView, 0);
//
//        if (!Float.isNaN(headView[0])) {
//            Log.d(TAG, "headView"  + " "+ headView[0] + " " + headView[1] + " " + headView[2] + " " + headView[3] + " " );
//            Log.d(TAG, "        "  +  " "+ headView[4] + " " + headView[5] + " " + headView[6] + " " + headView[7] + " " );
//            Log.d(TAG, "        "  +  " "+ headView[8] + " " + headView[9] + " " + headView[10] + " " + headView[11] + " " );
//            Log.d(TAG, "        "  +  " "+ headView[12] + " " + headView[13] + " " + headView[14] + " " + headView[15] + " " );
//        }

    }

    /**
     * Draw left eye. Called when VRMode enabled.
     */
    @Override
    public void drawLeft() {
        stereo.start(
                cameraPositionX, cameraPositionY, cameraPositionZ,
                0f, 0f, -1f,  // directionX, directionY, directionZ
                0f, 1f, 0f);  // upX, upY, upZ
        background(0);
        stereo.left();
        drawFrame(backgroundFrameLeft);
        drawPhotoCube(texCube);
    }

    /**
     * Draw right eye. Called when VRMode enabled.
     */
    @Override
    public void drawRight() {
        stereo.right();
        drawFrame(backgroundFrameRight);
        drawPhotoCube(texCubeRight);

    }

    /**
     * Processing draw function. Called when VRMode disabled.
     */
    @Override
    public void draw() {
        //println("draw()");
        stroke(128);
        line(0, 0, displayWidth, displayHeight);
        stroke(255);
        if (mousePressed) {
            println("mousePressed");
            line(mouseX, mouseY, pmouseX, pmouseY);
        }
    }

    int KEYCODE_MEDIA_NEXT = 87; // RIGHT
    int KEYCODE_MEDIA_PREVIOUS = 88;  // LEFT
    int KEYCODE_MEDIA_FAST_FORWARD = 90;  // UP
    int KEYCODE_MEDIA_REWIND = 89;  // DOWN
    int KEYCODE_ENTER = 66;

    public void keyPressed() {
        println("keyCode=" + keyCode);
        Log.d(TAG, "keyCode=" + keyCode);

        if (keyCode == LEFT || keyCode == KEYCODE_MEDIA_PREVIOUS) {
            rotx += PI / 4;
        } else if (keyCode == RIGHT || keyCode == KEYCODE_MEDIA_NEXT) {
            rotx -= PI / 4;
        } else if (keyCode == UP || keyCode == KEYCODE_MEDIA_FAST_FORWARD) {
            roty += PI / 4;
        } else if (keyCode == DOWN || keyCode == KEYCODE_MEDIA_REWIND) {
            roty -= PI / 4;
        }
    }

}
