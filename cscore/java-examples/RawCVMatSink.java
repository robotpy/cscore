/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.cscore;

import java.nio.ByteBuffer;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import edu.wpi.cscore.VideoMode.PixelFormat;
import edu.wpi.cscore.raw.RawFrame;

public class RawCVMatSink extends ImageSink {
  RawFrame frame = new RawFrame();
  Mat tmpMat;
  ByteBuffer origByteBuffer;
  int width;
  int height;
  int pixelFormat;
  int bgrValue = PixelFormat.kBGR.getValue();

  private int getCVFormat(PixelFormat pixelFormat) {
    int type = 0;
    switch (pixelFormat) {
    case kYUYV:
    case kRGB565:
      type = CvType.CV_8UC2;
      break;
    case kBGR:
      type = CvType.CV_8UC3;
      break;
    case kGray:
    case kMJPEG:
    default:
      type = CvType.CV_8UC1;
      break;
    }
    return type;
  }

  /**
   * Create a sink for accepting OpenCV images.
   * WaitForFrame() must be called on the created sink to get each new
   * image.
   *
   * @param name Source name (arbitrary unique identifier)
   */
  public RawCVMatSink(String name) {
    super(CameraServerJNI.createRawSink(name));
  }

  /**
   * Wait for the next frame and get the image.
   * Times out (returning 0) after 0.225 seconds.
   * The provided image will have three 3-bit channels stored in BGR order.
   *
   * @return Frame time, or 0 on error (call GetError() to obtain the error
   *         message)
   */
  public long grabFrame(Mat image) {
    return grabFrame(image, 0.225);
  }

  /**
   * Wait for the next frame and get the image.
   * Times out (returning 0) after timeout seconds.
   * The provided image will have three 3-bit channels stored in BGR order.
   *
   * @return Frame time, or 0 on error (call GetError() to obtain the error
   *         message); the frame time is in 1 us increments.
   */
  public long grabFrame(Mat image, double timeout) {
    frame.setWidth(0);
    frame.setHeight(0);
    frame.setPixelFormat(bgrValue);
    long rv = CameraServerJNI.grabSinkFrameTimeout(m_handle, frame, timeout);
    if (rv <= 0) {
      return rv;
    }

    if (frame.getDataByteBuffer() != origByteBuffer || width != frame.getWidth() || height != frame.getHeight() || pixelFormat != frame.getPixelFormat()) {
      origByteBuffer = frame.getDataByteBuffer();
      height = frame.getHeight();
      width = frame.getWidth();
      pixelFormat = frame.getPixelFormat();
      tmpMat = new Mat(frame.getHeight(), frame.getWidth(), getCVFormat(VideoMode.getPixelFormatFromInt(pixelFormat)), origByteBuffer);
    }
    tmpMat.copyTo(image);
    return rv;
  }
}
