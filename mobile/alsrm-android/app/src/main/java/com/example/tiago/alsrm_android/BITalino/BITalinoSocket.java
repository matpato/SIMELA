/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.example.tiago.alsrm_android.BITalino;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Abstract socket implementation that implements BITalino I/O streams handling.
 */
final class BITalinoSocket {

  private DataInputStream dis;
  private OutputStream os;
  private int prevSeq;

  /**
   * <p>Constructor for BITalinoSocket.</p>
   *
   * @param is a {@link java.io.DataInputStream} object.
   * @param os a {@link java.io.OutputStream} object.
   */
  public BITalinoSocket(final DataInputStream is, final OutputStream os) {
    if (is != null)
      this.dis = is;
    if (os != null)
      this.os = os;
    this.prevSeq = 15;
  }

  /**
   * Reads data from open socket, if any.
   *
   * @param numberOfSamples
   *          the number of samples to read
   * @param analogChannels
   *          the analog channels to read from
   * @param totalBytes
   *          total available bytes to read
   * @return an array of decoded {@link BITalinoFrame}s.
   * @throws BITalinoException if any.
   */
  public BITalinoFrame[] read(final int[] analogChannels, final int totalBytes,
                              final int numberOfSamples) throws BITalinoException {
    try {
      BITalinoFrame[] frames = new BITalinoFrame[numberOfSamples];
      byte[] buffer = new byte[totalBytes];
      byte[] bTemp = new byte[1];
      int sampleCounter = 0;

      // parse frames
      while (sampleCounter < numberOfSamples) {
        // read number_bytes from buffer
        dis.readFully(buffer, 0, totalBytes);
        // let's try to decode the buffer
        BITalinoFrame f = BITalinoFrameDecoder.decode(buffer, analogChannels, totalBytes);
        // if CRC isn't valid, sequence equals -1
        if (f.getSequence() == -1) {
          // we're missing data, so let's wait and try to rebuild the buffer or
          // throw exception
          System.out.println("Missed a sequence. Are we too far from BITalino? Retrying..");

          while (f.getSequence() == -1) {
            dis.readFully(bTemp, 0, 1);
            for (int j = totalBytes - 2; j >= 0; j--)
              buffer[j + 1] = buffer[j];
            buffer[0] = bTemp[0];
            f = BITalinoFrameDecoder.decode(buffer, analogChannels, totalBytes);
          }
        } else if (f.getSequence() != (prevSeq + 1) % 16) {
          System.out.println("Sequence out of order.");
        }
        prevSeq = f.getSequence();

        frames[sampleCounter] = f;
        sampleCounter++;
      }
      return frames;
    } catch (Exception e) {
      throw new BITalinoException(BITalinoErrorTypes.LOST_COMMUNICATION);
    }
  }

  /**
   * Writes data to socket.
   *
   * @throws BITalinoException if any.
   * @param data a int.
   */
  public void write(final int data) throws BITalinoException {
    try {
      os.write(data);
      os.flush();
      Thread.sleep(1000);
    } catch (Exception e) {
      e.printStackTrace(System.err);
      throw new BITalinoException(BITalinoErrorTypes.LOST_COMMUNICATION);
    }
  }

  /**
   * Releases any open resources.
   *
   * @throws BITalinoException if any.
   */
  public void close() throws BITalinoException {
    try {
      dis.close();
      os.close();
    } catch (Exception e) {
      throw new BITalinoException(BITalinoErrorTypes.BT_DEVICE_NOT_CONNECTED);
    } finally {
      dis = null;
      os = null;
    }
  }

  /**
   * <p>getInputStream.</p>
   *
   * @return a {@link java.io.InputStream} object.
   */
  public InputStream getInputStream() {
    return dis;
  }

  /**
   * <p>getOutputStream.</p>
   *
   * @return a {@link java.io.OutputStream} object.
   */
  public OutputStream getOutputStream() {
    return os;
  }

}