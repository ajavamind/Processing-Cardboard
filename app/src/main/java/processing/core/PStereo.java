package processing.core;

import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.*;

/**
 * processing.core.PStereo derived from repository:
 *
 * https://github.com/CreativeCodingLab/stereo
 *
 * https://github.com/CreativeCodingLab/stereo/blob/master/src/stereo/Stereo.java
 *
 * Modified by Andy Modla to use Processing OPENGL abstract layer API exclusively.
 *
 * @class processing.core.PStereo
 * @description
 */
public class PStereo {

    public enum StereoType {
        MONOCULAR,
        SIDE_BY_SIDE
    }

    public StereoType stereoType = null;
    public float eyeSeperation;
    PApplet app = null;
    PGL pgl; //Processing-OpenGL abstraction layer.
    float aspectRatio, nearPlane, farPlane, widthdiv2, convPlane;
    int width, height;
    float fovy = 45;
    float posx, posy, posz;
    float dirx, diry, dirz;
    float upx, upy, upz;
    float rightx, righty, rightz;

    // constructor convergence distance is calculated as 30 times the eye separation
    public PStereo(PApplet app, int width, int height, float eyeSeperation, float fovy, float nearPlane, float farPlane, StereoType stereoType) {
        this.app = app;
        this.width = width;
        this.height = height;

        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        this.fovy = fovy;
        this.eyeSeperation = eyeSeperation;
        this.convPlane = eyeSeperation * 30.0f;
        this.stereoType = stereoType;

    }

    // second constructor eye separation and conv plane are explictly set
    public PStereo(PApplet app, int width, int height, float eyeSeperation, float fovy, float nearPlane, float farPlane, StereoType stereoType, float convPlane) {
        this.app = app;
        this.width = width;
        this.height = height;

        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        this.fovy = fovy;
        this.eyeSeperation = eyeSeperation;
        this.convPlane = convPlane;
        this.stereoType = stereoType;
    }

    // third constructor eye separation is calculated as 1/30th from the conv distance
    public PStereo(PApplet app, int width, int height, float fovy, float nearPlane, float farPlane, StereoType stereoType, float convPlane) {
        this.app = app;
        this.width = width;
        this.height = height;

        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        this.fovy = fovy;
        this.convPlane = convPlane;
        this.eyeSeperation = convPlane / 30.0f;
        this.stereoType = stereoType;
    }

    // fourth constructor convergence distance is calculated from the  near and far planes, eye separation is calculated as 1/30th from the conv distance
    public PStereo(PApplet app, int width, int height, float fovy, float nearPlane, float farPlane, StereoType stereoType) {
        this.app = app;
        this.width = width;
        this.height = height;

        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        this.fovy = fovy;
        this.convPlane = (float) (nearPlane + (farPlane - nearPlane) / 100.0f);
        this.eyeSeperation = (float) convPlane / 30.f;
        this.stereoType = stereoType;

    }

    public void start(
                      float posx, float posy, float posz,
                      float dirx, float diry, float dirz,
                      float upx, float upy, float upz) {
        this.pgl = ((PApplet) app).beginPGL();
        if (this.stereoType == StereoType.SIDE_BY_SIDE) {
            this.aspectRatio = (float) (width / 2.0f) / (float) height;
        } else {
            this.aspectRatio = (float) width / (float) height;
        }

        this.widthdiv2 = nearPlane * (float) Math.tan(this.fovy / 2); // aperture in radians
        this.posx = posx;
        this.posy = posy;
        this.posz = posz;

        this.dirx = dirx;
        this.diry = diry;
        this.dirz = dirz;

        this.upx = upx;
        this.upy = upy;
        this.upz = upz;

        PVector cdir = new PVector(this.dirx, this.diry, this.dirz);
        PVector cup = new PVector(this.upx, this.upy, this.upz);
        PVector cright = cdir.cross(cup);

        this.rightx = cright.x * eyeSeperation / 2.0f;
        this.righty = cright.y * eyeSeperation / 2.0f;
        this.rightz = cright.z * eyeSeperation / 2.0f;
    }

    public void end() {
        pgl.colorMask(true, true, true, true);
    }

    /**
     * Set the right eye view
     */
    public void right() {
        // Adjusts viewport based on stereo type
        if(this.stereoType == StereoType.SIDE_BY_SIDE) {
            pgl.viewport(this.width / 2, 0, this.width / 2, this.height);
        } else {
            pgl.viewport(0, 0, this.width, this.height);
        }

        // Set frustum
        float top = (float)(widthdiv2);
        float bottom = (float)(-widthdiv2);
        float left = (float)(-aspectRatio * widthdiv2 - 0.5f * eyeSeperation * nearPlane / convPlane);
        float right = (float)(aspectRatio * widthdiv2 - 0.5f * eyeSeperation * nearPlane / convPlane);
        app.frustum(left, right, bottom, top, (float) nearPlane, (float) farPlane);

        // Set camera
        app.camera(
                posx + rightx, posy + righty, posz + rightz,
                posx + rightx + dirx, posy + righty + diry, posz + rightz + dirz,
                upx, upy, upz
        );
    }

    /**
     * Set the left eye view
     */
    public void left() {
        // Adjusts viewport based on stereo type
        if(this.stereoType == StereoType.SIDE_BY_SIDE) {
            pgl.viewport(0, 0, this.width / 2, this.height);
        } else {
            pgl.viewport(0, 0, this.width, this.height);
        }

        // Set frustum
        float top = (float)(widthdiv2);
        float bottom = (float)(-widthdiv2);
        float left = (float)(-aspectRatio * widthdiv2 + 0.5f * eyeSeperation * nearPlane / convPlane);
        float right = (float)(aspectRatio * widthdiv2 + 0.5f * eyeSeperation * nearPlane / convPlane);
        app.frustum(left, right, bottom, top, (float) nearPlane, (float) farPlane);

        // Set camera
        app.camera(
                posx - rightx, posy - righty, posz - rightz,
                posx - rightx + dirx, posy - righty + diry, posz - rightz + dirz,
                upx, upy, upz
        );
    }
}


