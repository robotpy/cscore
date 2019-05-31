/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.cscore;

import org.opencv.core.Mat;

import edu.wpi.cscore.VideoMode.PixelFormat;

public class RawCVMatSource extends ImageSource {
  /**
   * Create an OpenCV source.
   *
   * @param name Source name (arbitrary unique identifier)
   * @param mode Video mode being generated
   */
  public RawCVMatSource(String name, VideoMode mode) {
    super(CameraServerJNI.createRawSource(name,
        mode.pixelFormat.getValue(),
        mode.width,
        mode.height,
        mode.fps));
  }

  /**
   * Create an OpenCV source.
   *
   * @param name Source name (arbitrary unique identifier)
   * @param pixelFormat Pixel format
   * @param width width
   * @param height height
   * @param fps fps
   */
  public RawCVMatSource(String name, VideoMode.PixelFormat pixelFormat, int width, int height, int fps) {
    super(CameraServerJNI.createRawSource(name, pixelFormat.getValue(), width, height, fps));
  }

  /**
   * Put an OpenCV image and notify sinks.
   *
   * <p>Only 8-bit single-channel or 3-channel (with BGR channel order) images
   * are supported. If the format, depth or channel order is different, use
   * Mat.convertTo() and/or cvtColor() to convert it first.
   *
   * @param image OpenCV image
   */
  public void putFrame(Mat image) {
    int channels = image.channels();
    if (channels != 1 && channels != 3) {
      throw new VideoException("Unsupported Image Type");
    }
    int imgType = channels == 1 ? PixelFormat.kGray.getValue() : PixelFormat.kBGR.getValue();
    CameraServerJNI.putRawSourceFrame(m_handle, image.dataAddr(), image.width(), image.height(), imgType, (int)image.total() * channels);
  }
}
