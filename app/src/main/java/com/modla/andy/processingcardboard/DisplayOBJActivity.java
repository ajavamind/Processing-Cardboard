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
 * library and the Google Cardboard Android SDK. The app displays an OBJ model rocket and allows
 * keyboard input to rotate, zoom with head tilt and pan left or right.
 *
 */

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.vr.sdk.base.HeadTransform;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;


public class DisplayOBJActivity extends PApplet {
    private static String TAG = "DisplayOBJActivity";

    private Vibrator vibrator;

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
        //cardboardView.setDistortionCorrectionEnabled(false);
        cardboardView.setTransitionViewEnabled(true);
        setGvrView(cardboardView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
    // Display OBJ file and zoom with head tilt and rotate with keyboard.

    float rotx = 0; //PI / 4;
    float roty = 0; //PI / 4;
    PShape rocket;
    PShape textImage;
    PShape gridImage;
    float nearPlane = .1f;
    float farPlane = 1000f;
    float convPlane = 20.0f;
    float eyeSeparation = convPlane / 1440.0f;  //60.f;
    float fieldOfViewY = 45f;
    float cameraPositionX = STARTX;
    float cameraPositionY = STARTY;
    float cameraPositionZ = STARTZ;
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
        rocket = loadShape("obj/rocket.obj");
        textSize(32);
        textImage = createTextGraphics("ROCKET");
        gridImage = createGridShape();
        /* second constructor, custom eye separation, custom convergence */
        stereoView(
                width, height, eyeSeparation, fieldOfViewY,
                nearPlane,
                farPlane,
                convPlane);

        //cardboardView.resetHeadTracker();
        cardboardView.recenterHeadTracker();

    }

    PShape createTextGraphics(String s) {
        PGraphics buffer = createGraphics(width/2, height);
        buffer.beginDraw();
        buffer.textSize(128);
        buffer.background(0x80800020);
        stroke(255);
        fill(255);
        buffer.text(s, width / 8, height / 2 + height / 4);
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

    void drawTextGraphics(PShape s) {
        pushMatrix();
        scale(1f);
        translate(0, 0, -.25f);
        shape(s);
        popMatrix();
    }

    void drawShape(PShape s) {
        pushMatrix();
        scale(.01f);
        rotateX(rotx);
        rotateY(roty);
        rotateZ(PI);
        shape(s);
        popMatrix();
    }

    public void drawText(String s, float x) {
        pushMatrix();
        stroke(255);
        fill(255);
        scale(.02f);
        text(s, x, 0.0f, 0.0f);
        popMatrix();
    }

    // this technique is very slow
    public void drawGrid() {
        int gridSize = 1;
        int delta = 5;
        pushMatrix();
        stroke(128);
        fill(64);
        for(int i = -delta; i <delta; i+=gridSize) {
            for(int j = -delta; j < delta; j+=gridSize) {
                int y = 1;
                int z = 0;
                line(i,          y, j,           i+gridSize, y, j          );
                line(i+gridSize, y, j,           i+gridSize, y, j+gridSize );
                line(i+gridSize, y, j+gridSize,  i,          y, j+gridSize );
                line(i,          y, j,           i,          y, j+gridSize );
            }
        }
        popMatrix();
    }

    public void drawGridShape(PShape grid) {
        pushMatrix();
        scale(10f);
        //rotateY(PI/4);
        //rotateX(rotx);  // test
        //rotateY(roty);
        shape(grid);
        popMatrix();
    }

    // experimental work in progress
    public PShape createGridShape() {
        int gridSize = 20;
        stroke(128);
        fill(64);
        strokeWeight(1);
        println("width=" + width + " height=" + height);
        PGraphics buffer = createGraphics(width / 2, height, P2D);
        buffer.beginDraw();
        for (int y = 0; y < height; y += gridSize) {
            buffer.line(0, y, width/2, y);
        }
        for (int x = 0; x < width; x += gridSize) {
            buffer.line(x, 0, x, height);
        }

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

    public void mouseDragged() {
        float rate = 0.01f;
        rotx += (pmouseY - mouseY) * rate;
        roty += (mouseX - pmouseX) * rate;
    }

    void resetTracker() {
        cameraPositionX = STARTX;
        cameraPositionY = STARTY;
        cameraPositionZ = STARTZ;
        //cardboardView.resetHeadTracker();
        cardboardView.recenterHeadTracker();
    }

    volatile float heading;  // degrees
    volatile float attitude; // degrees
    volatile float bank; // degrees
    private float[] euler = new float[3];
    private float[] quat = new float[4];

    @Override
    public void headTransform(HeadTransform headTransform) {
        headTransform.getQuaternion(quat, 0);
        headTransform.getEulerAngles(euler, 0);
        bank = degrees(euler[0]);
        heading = degrees(euler[1]);
        attitude = degrees(euler[2]);
        float zoomSpeedMax = .01f;  //.01f;
        float TILT_FOR_ZOOM = 7.5f; // degrees

        float inc = -.020f;
        // X
        if (heading >= -90 && heading <= 90) {  // 180 degrees swivel
            cameraPositionX = PI * 2 * heading * inc;
        }

        // Y
        inc = .04f;
        if (bank >= -90 && bank <= 90) {  // 180 degrees swivel
            cameraPositionY = -2 * bank * inc;
        }

        // Z
        inc = 0.0f;
        if (attitude > TILT_FOR_ZOOM) {
            inc = zoomSpeedMax;
        } else if (attitude < -TILT_FOR_ZOOM) {
            inc = -zoomSpeedMax;
        }
        cameraPositionZ += inc;

    }

    /**
     * Draw left eye. Called when VRMode enabled.
     */
    @Override
    public void drawLeft() {
        drawShape(rocket);
        drawTextGraphics(textImage);
        drawText("Space", 30.0f);
        //drawGrid();
        drawGridShape(gridImage);
    }

    /**
     * Draw right eye. Called when VRMode enabled.
     */
    @Override
    public void drawRight() {
        drawShape(rocket);
        drawTextGraphics(textImage);
        drawText("Space", 30.0f);
        //drawGrid();
        drawGridShape(gridImage);
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
        } else if (keyCode == MEDIA_ENTER) {
            resetTracker();
        }
    }

}
