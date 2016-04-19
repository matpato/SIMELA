package com.example.tiago.alsrm_android.BITalino;

import java.io.IOException;

/**
 * {@link BITalinoFrame} decoder.
 */
final class BITalinoFrameDecoder {

    /**
     * Decode {@link BITalinoFrame}.
     *
     * @param buffer an array of byte.
     * @return decoded {@link BITalinoFrame}
     * @param analogChannels an array of int.
     * @param totalBytes a int.
     * @throws java.io.IOException if any.
     * @throws BITalinoException if any.
     */
    public static BITalinoFrame decode(final byte[] buffer,
                                       final int[] analogChannels, final int totalBytes) throws IOException,
            BITalinoException {

        try {
            BITalinoFrame frame = new BITalinoFrame();
            final int j = (totalBytes - 1);
            int x0 = 0, x1 = 0, x2 = 0, x3 = 0, out = 0, inp = 0;
            final int CRC = (buffer[j - 0] & 0x0F) & 0xFF;

            // check CRC
            for (int bytes = 0; bytes < totalBytes; bytes++) {
                for (int bit = 7; bit > -1; bit--) {
                    inp = (buffer[bytes]) >> bit & 0x01;
                    if (bytes == (totalBytes - 1) && bit < 4)
                        inp = 0;
                    out = x3;
                    x3 = x2;
                    x2 = x1;
                    x1 = out ^ x0;
                    x0 = inp ^ out;
                }
            }
            // if the message was correctly received, start decoding
            if (CRC == ((x3 << 3) | (x2 << 2) | (x1 << 1) | x0)) {
                frame = new BITalinoFrame();
                frame.setSequence(((buffer[j - 0] & 0xF0) >> 4) & 0xf);
                frame.setDigital(0, (buffer[j - 1] >> 7) & 0x01);
                frame.setDigital(1, (buffer[j - 1] >> 6) & 0x01);
                frame.setDigital(2, (buffer[j - 1] >> 5) & 0x01);
                frame.setDigital(3, (buffer[j - 1] >> 4) & 0x01);

                // parse buffer frame

                if (analogChannels.length >= 1)
                    frame.setAnalog(
                            analogChannels[0],
                            (((buffer[j - 1] & 0xF) << 6) | ((buffer[j - 2] & 0XFC) >> 2)) & 0x3ff);
                if (analogChannels.length >= 2)
                    frame.setAnalog(
                            analogChannels[1],
                            (((buffer[j - 2] & 0x3) << 8) | (buffer[j - 3]) & 0xff) & 0x3ff);
                if (analogChannels.length >= 3)
                    frame.setAnalog(
                            analogChannels[2],
                            (((buffer[j - 4] & 0xff) << 2) | (((buffer[j - 5] & 0xc0) >> 6))) & 0x3ff);
                if (analogChannels.length >= 4)
                    frame.setAnalog(
                            analogChannels[3],
                            (((buffer[j - 5] & 0x3F) << 4) | ((buffer[j - 6] & 0xf0) >> 4)) & 0x3ff);
                if (analogChannels.length >= 5)
                    frame.setAnalog(
                            analogChannels[4],
                            (((buffer[j - 6] & 0x0F) << 2) | ((buffer[j - 7] & 0xc0) >> 6)) & 0x3f);
                if (analogChannels.length >= 6)
                    frame.setAnalog(analogChannels[5], (buffer[j - 7] & 0x3F));
            } else {
                frame = new BITalinoFrame();
                frame.setSequence(-1);
            }
            return frame;
        } catch (Exception e) {
            throw new BITalinoException(BITalinoErrorTypes.DECODE_INVALID_DATA);
        }
    }

}