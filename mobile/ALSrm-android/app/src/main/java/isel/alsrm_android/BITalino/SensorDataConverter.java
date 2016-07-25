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
/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package isel.alsrm_android.BITalino;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This class holds methods for converting/scaling raw data from BITalino
 * included sensors to human-readable data.
 */
public class SensorDataConverter {

    private static final double VCC = 3.3; // volts
    private static final int ACC_MIN = 208;
    private static final int ACC_MAX = 312;

    /**
     * ElectroMyoGraphy conversion.
     *
     * @param port
     *          the port where the <tt>raw</tt> value was read from.
     * @param raw
     *          the value read.
     * @return a value ranging between -1.65 and 1.65mV
     */
    public static double scaleEMG(final int port, final int raw) {
        final double result = (raw * VCC / getResolution(port) - VCC / 2);
        return new BigDecimal(result).setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * ElectroCardioGraphy conversion.
     *
     * @param port
     *          the port where the <tt>raw</tt> value was read from.
     * @param raw
     *          the value read.
     * @return a value ranging between -1.5 and 1.5mV
     */
    public static double scaleECG(final int port, final int raw) {
        final double result = (((raw / getResolution(port) -0.5)*VCC) / 1100) * 1000;
        return new BigDecimal(result).setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Accelerometer conversion based on reference calibration values. If you want
     * to acquire more precise values, use
     * {@link #scaleAccelerometerWithPrecision(int, int, int, int)
     * scaleAccelerometerWithPrecision} method.
     *
     * @param port
     *          the port where the <tt>raw</tt> value was read from.
     * @param raw
     *          the value read.
     * @return a value ranging between -5 and 4.5Gs
     */
    public static double scaleAccelerometer(final int port, final int raw) {
        final double result = scaleAccelerometerWithPrecision(port, raw, ACC_MIN,
                ACC_MAX);
        return new BigDecimal(result).setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Accelerometer conversion based on parameterized calibration values.
     *
     * @param port
     *          the port where the <tt>raw</tt> value was read from.
     * @param raw
     *          the value read.
     * @param min
     *          the calibration minimum value
     * @param max
     *          the calibration maximum value
     * @return a value ranging between -3 and 3Gs
     */
    public static double scaleAccelerometerWithPrecision(final int port,
                                                         final int raw, final int min, final int max) {
        return 2 * ((double)(raw - min) / (max - min)) - 1;
    }

    /**
     * Electrodermal Activity conversion.
     *
     * @param port
     *          the port where the <tt>raw</tt> value was read from.
     * @param raw
     *          the value read.
     * @return  a value ranging from 1 and inf uS (micro Siemens),
     *          beware might return Double.POSITIVE_INFINITY 
     */
    public static double scaleEDA(final int port, final int raw) {
        // need to round maximum value that otherwise is 1.05496875
        final double ohm = 1 - ( (double)raw /(double)1024);
        double result = 1/ohm;
        if (result != Double.POSITIVE_INFINITY)
            result = new BigDecimal(result).setScale(4, RoundingMode.HALF_UP)
                    .doubleValue();
        return result;
    }

    /**
     * Luminosity conversion.
     *
     * @param port
     *          the port where the <tt>raw</tt> value was read from.
     * @param raw
     *          the value read.
     * @return a value ranging from 0 and 100%.
     */
    public static double scaleLuminosity(final int port, final int raw) {
        return 100 * (raw / getResolution(port));
    }

    /**
     * Temperature conversion.
     *
     * @param port
     *          the port where the <tt>raw</tt> value was read from.
     * @param raw
     *          the value read.
     * @param celsius
     *          <tt>true</tt>:use celsius as metric,
     *          <tt>false</tt>: fahrenheit is used.
     * @return a value ranging between -40 and 125 Celsius (-40 and 257 Fahrenheit)
     */
    public static double scaleTMP(final int port, final int raw, boolean celsius){
        double result = (((raw/getResolution(port))*VCC) - 0.5)*100;

        if (!celsius)
            // Convert to fahrenheit
            result = result*((double)9/5) + 32;

        return new BigDecimal(result).setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Respiration conversion.
     *
     * @param port
     *          the port where the <tt>raw</tt> value was read from.
     * @param raw
     *          the value read.
     * @return a value ranging between -50% and 50%
     */
    public static double scalePZT(final int port, final int raw){
        double result =  ((raw/getResolution(port)) - 0.5)*100;
        return new BigDecimal(result).setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

    }

    /**
     * Electroencephalography conversion.
     *
     * @param port
     *          the port where the <tt>raw</tt> value was read from.
     * @param raw
     *          the value read.
     * @return a value ranging between -41.5 and 41.5 microvolt
     */
    public static double scaleEEG(final int port, final int raw){
        double G_ECG = 40000; // sensor gain

        // result rescaled to microvolt
        double result = (((raw/getResolution(port))-0.5)*VCC)/G_ECG;
        result = result* Math.pow(10, 6);

        return new BigDecimal(result).setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Returns the resolution (maximum value) for a certain port.
     * <p>
     * From port 0 to 3, the resolution is 10-bit, thus 2^10 - 1 = 1023. <br />
     * From port 4 to 5, the resolution is 6-bit, thus 2^6 - 1 = 63.
     *
     * @param port
     * @return the resolution (maximum value) for a certain port.
     */
    private static final double getResolution(final int port) {
        return (double) port < 4 ? 1023 : 63;
    }

}
