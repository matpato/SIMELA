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
package isel.alsrm_android.BITalino;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * This class represents a BITalino device and provides methods to interact with
 * said device.
 */
public class BITalinoDevice {

  private static final long SLEEP = 500;

  private final int[] analogChannels;
  private final int samplerate;
  private final int totalBytes;

  private BITalinoSocket socket = null;

  /**
   * @param samplerate
   *          the sampling frequency (Hz). Values available are 1000 (default),
   *          100, 10 and 1.
   * @param analogChannels
   *          the analog channels to read from.
   * @throws BITalinoException
   *           if analog channels are not valid.
   */
  public BITalinoDevice(final int samplerate, final int[] analogChannels)
      throws BITalinoException {

    // validate samplerate
    this.samplerate = samplerate != 1 && samplerate != 10 && samplerate != 100
        && samplerate != 1000 ? 1000 : samplerate;

    // validate analog channels length
    if (analogChannels.length < 1 || analogChannels.length > 6)
      throw new BITalinoException(BITalinoErrorTypes.INVALID_ANALOG_CHANNELS);
    // validate analog channels identifiers
    for (int channel : analogChannels)
      if (channel < 0 || channel > 5)
        throw new BITalinoException(BITalinoErrorTypes.INVALID_ANALOG_CHANNELS);
    Arrays.sort(analogChannels);
    this.analogChannels = analogChannels;

    // calculate totalBytes based on number of used analog channels
    totalBytes = analogChannels.length <= 4 ? (int) Math
        .ceil((12f + 10f * analogChannels.length) / 8) : (int) Math
        .ceil((52f + 6f * (analogChannels.length - 4)) / 8);
  }

  /**
   * Provided that a valid connection to BITalino is established, open
   * corresponding streams.
   * <p>
   * If everything goes smoothly, automatically set the <tt>samplerate</tt> in
   * the device.
   */
  public void open(final InputStream is, final OutputStream os)
      throws BITalinoException {
    try {
      socket = new BITalinoSocket(new DataInputStream(is), os);
      Thread.sleep(SLEEP);
    } catch (Exception e) {
      e.printStackTrace(System.err);
      close();
    }

    // set samplerate on the bluetooth device
    try {
      int command = 0;
      switch (samplerate) {
      case 1000:
        command = 0x3;
        break;
      case 100:
        command = 0x2;
        break;
      case 10:
        command = 0x1;
        break;
      case 1:
        command = 0x0;
        break;
      }
      command = (command << 6) | 0x03;
      socket.write(command);
    } catch (Exception e) {
      e.printStackTrace(System.err);
      throw new BITalinoException(BITalinoErrorTypes.LOST_COMMUNICATION);
    }
  }

  /**
   * Starts acquisition of predefined analog channels.
   * 
   * @throws BITalinoException
   */
  public void start() throws BITalinoException {
    int bit = 1;
    for (int channel : analogChannels)
      bit = bit | (1 << (2 + channel));
    try {
      socket.write(bit);
    } catch (Exception e) {
      throw new BITalinoException(BITalinoErrorTypes.BT_DEVICE_NOT_CONNECTED);
    }

  }

  /**
   * Stops acquisition.
   * 
   * @throws BITalinoException
   */
  public void stop() throws BITalinoException {
    try {
      socket.write(0);
      Thread.sleep(SLEEP);
      close();
    } catch (Exception e) {
      throw new BITalinoException(BITalinoErrorTypes.BT_DEVICE_NOT_CONNECTED);
    }
  }

  /**
   * Closes socket and releases any open resources.
   * 
   * @throws BITalinoException
   */
  private void close() throws BITalinoException {
    try {
      socket.close();
    } catch (Exception e) {
      throw new BITalinoException(BITalinoErrorTypes.BT_DEVICE_NOT_CONNECTED);
    } finally {
      socket = null;
    }
  }

  /**
   * Retrieves device version.
   * <p>
   * <strong>ATTENTION:</strong> Works only in idle mode!
   */
  public String version() throws BITalinoException {
    try {
      socket.write(7);
      ByteBuffer buffer = ByteBuffer.allocate(30);
      // read until '\n' arrives
      byte b = 0;
      while ((char) (b = (byte) socket.getInputStream().read()) != '\n')
        buffer.put(b);
      // return a minified array
      buffer.flip();
      return new String(buffer.array());
    } catch (Exception e) {
      throw new BITalinoException(BITalinoErrorTypes.LOST_COMMUNICATION);
    }
  }

  /** Sets the battery voltage threshold for the low-battery LED.
   * \param[in] value Battery voltage threshold. Default value is 0.
   * Value | Voltage Threshold
   * ----- | -----------------
   *     0 |   3.4 V
   *  ...  |   ...
   *    63 |   3.8 V
   * \remarks This method cannot be called during an acquisition.
   * \exception BITalinoException (BITalinoErrorTypes.THRESHOLD_NOT_VALID)
   * \exception BITalinoException (BITalinoErrorTypes.LOST_COMMUNICATION)
   */
  public void battery(int value) throws BITalinoException {

    int Mode;
    if (value >= 0 && value<=63) {
      Mode = value << 2;
      socket.write(Mode);

    }
    else {
      throw new BITalinoException(BITalinoErrorTypes.INVALID_THRESHOLD);
    }
  }

  /** Assigns the digital outputs states.
   * \param[in] digitalArray Vector of integers to assign to digital outputs, starting at first output (O1).
   * On each vector element, 0 sets the output to low level and 1 sets the output to high level.
   * This vector must contain exactly 4 elements.
   * \remarks This method must be called only during an acquisition on original %BITalino. On %BITalino 2 there is no restriction.
   * \exception BITalinoException (BITalinoErrorTypes.DIGITAL_CHANNELS_NOT_VALID)
   * \exception BITalinoException (BITalinoErrorTypes.LOST_COMMUNICATION)
   */
  public void trigger(int[] digitalArray) throws BITalinoException {

    if (digitalArray.length != 4) {
      throw new BITalinoException(BITalinoErrorTypes.INVALID_DIGITAL_CHANNELS);
    }
    else {
      int data  = 3;
      for (int i= 0;i<digitalArray.length;i++) {
        if (digitalArray[i]<0 | digitalArray[i]>1) {
          throw new BITalinoException(BITalinoErrorTypes.INVALID_DIGITAL_CHANNELS);
        }
        else {
          data = data | digitalArray[i]<<(2+i);
        }

      }
      socket.write(data);
    }
  }

  /**
   * Reads data from open socket.
   * 
   * @param numberOfSamples
   * @return an array of {@link BITalinoFrame} with numberOfSamples positions.
   * @throws BITalinoException
   */
  public BITalinoFrame[] read(final int numberOfSamples)
      throws BITalinoException {
    return socket.read(analogChannels, totalBytes, numberOfSamples);
  }

  public int [] getAnalogChannels(){
    return this.analogChannels;
  }
}