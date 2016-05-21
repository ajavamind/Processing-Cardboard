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
 * This Android app is an example Cardboard VR program coded using the Processing for Android
 * library and the Google Cardboard Android SDK.
 *
 * <p/>
 * The Processing library has an abstraction layer for OPENGL making it possible
 * to write an Android Cardboard app without needing direct Android OPENGL calls.
 * Using Processing with Cardboard SDK is an alternative for writing Android VR applications.
 *
 * <p/>
 * Uses Android Studio (1.5.1)
 * <p/>
 * Cardboard SDK for Android 0.6.0
 * <p/>
 * Minimum Android API 4.1 (16)
 * <p/>
 * Tested with Sony Z1S phone, 1920x1080 pixel display, running Android version 5.0.2, and
 * hardware accelerated GPU
 * Tested with Samsung S6 phone
 *
 * <p/>
 * Issues:
 * Distortion correction is disabled because the Cardboard correction feature does not work well.
 * The display is not distorted enough to matter with my Unofficial cardboard viewer lens and
 * home made viewer with stereoscopic quality lens.
 * <p/>
 * Out of memory can occur when using large images and restarting the app
 * <p/>
 * Processing-Cardboard Library build for Processing 3.0 IDE not implemented. Here the library
 * is included with the app as Processing source code.
 * <p/>
 *
 * notes:
 * The magnet trigger does not work well with my phone so I use new convert tap to trigger feature
 * available in Cardboard V2.
 * <p/>
 * Changes made to Processing-Anddroid core library:
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
 * PStereo class added to Processing core for stereo view control
 * <p/>
 *
 *
 *
 * Cardboard is a trademark of Google Inc.
 */

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Vibrator;

import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;


public class MainActivity extends PApplet {
    private static String TAG = "MainActivity";

    private Vibrator vibrator;
    PImage[] photo = null;
    PImage[] photoRight = null;
    PImage backgroundLeft = null;
    PImage backgroundRight = null;
    boolean vrMode;

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
        //cardboardView.setAlignmentMarkerEnabled(false);
        //cardboardView.setSettingsButtonEnabled(false);
        setGvrView(cardboardView);
        //cardboardView.setDistortionCorrectionEnabled(false);
        //cardboardView.setDistortionCorrectionEnabled(true);  // default
        cardboardView.setTransitionViewEnabled(true);
        //cardboardView.setVRModeEnabled(false); // sets Monocular mode
        vrMode = cardboardView.getVRMode();
        Log.d(TAG, "getVRMode=" + vrMode);
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
        resetTracker();
    }

    @Override
    public void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        Log.d(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        Log.d(TAG, "onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        // TODO release image resources
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Processing sketch for the Android app starts here.
    // The app shows a rotating stereo photo cube in front of a 3D background photo.
    // Head movement/direction or keyboard input determines the viewers location and direction.
    // Tap the screen to reset to starting view or swipe to rotate the photo cube.
    // The app displays text and graphics including a reticle.
    //

    float rotx = 0; //PI / 4;
    float roty = 0; //PI / 4;
    float cubeScale = 2.0f;
    PShape texCube;
    PShape texCubeRight;
    PShape backgroundFrameLeft;
    PShape backgroundFrameRight;
    PShape textSVG;
    PShape textImage;
    PShape reticle;
    PFont font;
    float nearPlane = .1f;
    float farPlane = 1000f;
    float convPlane = 20.0f;
    float eyeSeparation = convPlane/ 1440.f;  // 30.0f;
    float fieldOfViewY = 45f;
    float cameraPositionX = STARTX;
    float cameraPositionY = STARTY;
    float cameraPositionZ = STARTZ;
    float[] headView = new float[16];
    int NUM_PHOTOS = 6;
    int photoCounter = 0;

    @Override
    public void settings() {
        // set size to full screen dimensions
        // Processing variables displayWidth and displayHeight are your phone screen dimensions
        size(displayWidth, displayHeight, OPENGL);
        println("settings() done");
    }

    /**
     * One time initial call to set up your Processing sketch variables, etc.
     */
    @Override
    public void setup() {
        background(0);
        // Note screen will be blank for a few seconds until
        // photos loaded.
        // TODO use requestImage()
        // load images for cube face textures
        if (photo == null) {
            photo = new PImage[NUM_PHOTOS];
            photo[0] = loadImage("data/IMG_0506_l.JPG");
            photo[1] = loadImage("data/IMG_0510_l.JPG");
            photo[2] = loadImage("data/IMG_0513_l.JPG");
            photo[3] = loadImage("data/IMG_0516_l.JPG");
            photo[4] = loadImage("data/IMG_0519_l.JPG");
            photo[5] = loadImage("data/IMG_0524_l.JPG");
            photoRight = new PImage[NUM_PHOTOS];
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
            textSVG = loadShape("data/text.svg");
        }
        texCube = createCube(photo);
        texCubeRight = createCube(photoRight);
        backgroundFrameLeft = createFrame(backgroundLeft);
        backgroundFrameRight = createFrame(backgroundRight);
        reticle = createReticle();
        font = createFont("Georgia", 32);
        textFont(font);

        //textMode(SHAPE);  // TODO not supported in Processing-Android
        textSize(32);
        textImage = createTextGraphics("Photo Cube");

        // set up stereo view
        stereoView(width, height, eyeSeparation, fieldOfViewY, nearPlane, farPlane, convPlane);

        cardboardView.resetHeadTracker();
        println("setup() done");
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
        //face.stroke(0);
        face.textureMode(NORMAL);
        face.texture(photo);
        face.vertex(-1, -1, 0, 0, 0);
        face.vertex(1, -1, 0, 1, 0);
        face.vertex(1, 1, 0, 1, 1);
        face.vertex(-1, 1, 0, 0, 1);
        face.endShape();
        return face;
    }

    PShape createReticle() {
        final int RADIUS = 64;
        PGraphics buffer = createGraphics(RADIUS, RADIUS);
        buffer.beginDraw();
        buffer.background(0, 0);  // transparent
        buffer.stroke(255);
        buffer.strokeWeight(4);
        buffer.ellipseMode(CENTER);
        buffer.ellipse(RADIUS / 2, RADIUS / 2, RADIUS / 3, RADIUS / 3);
        buffer.strokeWeight(8.0f);
        buffer.line(RADIUS/2, 0, RADIUS/2, RADIUS - 1);
        buffer.line(0, RADIUS/2, RADIUS-1, RADIUS/2);
        buffer.endDraw();
        PShape face = createShape();
        face.beginShape(QUAD);
        face.noStroke();
        face.textureMode(NORMAL);
        face.texture(buffer);
        face.vertex(-1, -1, 0, 0, 0);
        face.vertex(1, -1, 0, 1, 0);
        face.vertex(1, 1, 0, 1, 1);
        face.vertex(-1, 1, 0, 0, 1);
        face.endShape();
        return face;
    }

    PShape createTextGraphics(String s) {
        PGraphics buffer = createGraphics(width/2, height);
        buffer.beginDraw();
        buffer.strokeWeight(8.0f);
        buffer.background(color(0,128,0));
        buffer.stroke(0xff, 0, 0);
        buffer.text(s, 0, height / 4);
        buffer.stroke(255);
        buffer.textSize(64f);
        buffer.text(s, 0, height / 3);
        buffer.stroke(0x0000FF);
        buffer.textSize(128);
        buffer.text(s, 0, height / 2 + height / 4);
        buffer.fill(color(128, 0, 128));
        buffer.rect(400, 200, 400, 400);
        buffer.stroke(128);
        buffer.line(0, 0, width / 2, height);
        buffer.endDraw();

        PShape face = createShape();
        face.beginShape(QUAD);
        face.noStroke();
        face.textureMode(NORMAL);
        face.texture(buffer);
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

    void drawSVG(PShape s) {
        pushMatrix();
        scale(10.0f);
        translate(-100, 100, 140);
        s.disableStyle();
        noFill();
        stroke(0);
        shape(s);
        popMatrix();
    }

    void drawReticle(float sc) {
        pushMatrix();
        translate(cameraPositionX, cameraPositionY, cameraPositionZ - STARTZ);
        scale(sc);  // 1.0f
        shape(reticle);
        popMatrix();
    }

    void drawTextGraphics(PShape s) {
        pushMatrix();
        scale(8);
        translate(0, 0, -.25f);
        shape(s);
        popMatrix();
    }

    public void mouseDragged() {
        float rate = 0.01f;
        rotx += (pmouseY - mouseY) * rate;
        roty += (mouseX - pmouseX) * rate;
    }

    void resetTracker() {
        cameraPositionX = STARTX;
        cameraPositionY = STARTY;
        cameraPositionZ = STARTZ;
        cardboardView.resetHeadTracker();
    }

    @Override
    public void headTransform(HeadTransform headTransform) {
        float[] quat = new float[4];
        headTransform.getQuaternion(quat, 0);
        // normalize quaternion
        float length = (float) Math.sqrt(quat[0] * quat[0] + quat[1] * quat[1] + quat[2] * quat[2] + quat[3] * quat[3]);
        int DIV = 10;
        float lowSpeed = .01f;  //.005f;
        float mediumSpeed = .02f;  //.01f;
        float highSpeed = .04f;  //.02f;
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
                cameraPositionZ -= rollSpeed;

//            Log.d(TAG, "Normalized quaternion " + pitch + " " + yaw + " " + roll + " Camera position "+ cameraPositionX + " " + cameraPositionY + " " + cameraPositionZ);
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
        drawFrame(backgroundFrameLeft);
        drawPhotoCube(texCube);
        drawReticle(.1f);
        drawTextGraphics(textImage);
    }

    /**
     * Draw right eye. Called when VRMode enabled.
     */
    @Override
    public void drawRight() {
        drawFrame(backgroundFrameRight);
        drawPhotoCube(texCubeRight);
        drawReticle(.1f);
        drawTextGraphics(textImage);
   }

    /**
     * Processing draw function. Called before drawLeft and drawRight.
     */
    @Override
    public void draw() {
        background(0);
        stereoPosition(
                cameraPositionX, cameraPositionY, cameraPositionZ,
                0f, 0f, -1f,  // directionX, directionY, directionZ
                0f, 1f, 0f);  // upX, upY, upZ
        if (!vrMode) {
            // drawLeft() and drawRight() will not be called, so display the cube photos
            image(photo[photoCounter++], 0,0);
            if (photoCounter >= NUM_PHOTOS)
                photoCounter = 0;
        }
    }

    public void keyPressed() {
        println("keyCode=" + keyCode);
        Log.d(TAG, "keyCode=" + keyCode);

        if (keyCode == LEFT || keyCode == MEDIA_PREVIOUS) {
            rotx += PI / 4;
        } else if (keyCode == RIGHT || keyCode == MEDIA_NEXT) {
            rotx -= PI / 4;
        } else if (keyCode == UP || keyCode == MEDIA_FAST_FORWARD) {
            roty += PI / 4;
        } else if (keyCode == DOWN || keyCode == MEDIA_REWIND) {
            roty -= PI / 4;
        } else if (keyCode == MEDIA_ENTER || keyCode == ENTER) {
            resetTracker();
        }
    }

}
