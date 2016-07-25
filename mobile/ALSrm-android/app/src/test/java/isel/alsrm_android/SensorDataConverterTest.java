package isel.alsrm_android;

import org.junit.Test;

import isel.alsrm_android.BITalino.SensorDataConverter;

import static junit.framework.Assert.assertEquals;

public class SensorDataConverterTest {

    @Test
    public void test_emg_conversion() {
        assertEquals(SensorDataConverter.scaleEMG(0, 0), -1.65);
        assertEquals(SensorDataConverter.scaleEMG(0, 1023), 1.65);
    }

    @Test
    public void test_ecg_conversion() {
        assertEquals(SensorDataConverter.scaleECG(0, 0), -1.5);
        assertEquals(SensorDataConverter.scaleECG(0, 1023), 1.5);
    }

    @Test
    public void test_eda_conversion() {
        assertEquals(SensorDataConverter.scaleEDA(0, 0), 1.0);
        assertEquals(SensorDataConverter.scaleEDA(0, 1023), 1023);
    }

    @Test
    public void test_luminosity_conversion() {
        assertEquals(SensorDataConverter.scaleLuminosity(0, 0), 0.0);
        assertEquals(SensorDataConverter.scaleLuminosity(0, 1023), 100.0);
    }
    
    @Test
    public void test_tmp_celsius_conversion() {
        assertEquals(SensorDataConverter.scaleTMP(0, 0, true), -50.0);
        assertEquals(SensorDataConverter.scaleTMP(0, 1023, true), 280.0);
    }
        
    @Test
    public void test_tmp_fahrenheit_conversion() {
        assertEquals(SensorDataConverter.scaleTMP(0, 0, false), -58.0);
        assertEquals(SensorDataConverter.scaleTMP(0, 1023, false), 536.0);
    }

    @Test
    public void test_pzt_conversion() {
        assertEquals(SensorDataConverter.scalePZT(0, 0), -50.0);
        assertEquals(SensorDataConverter.scalePZT(0, 1023), 50.0);
    }
    
    @Test
    public void test_EEG_conversion() {
        assertEquals(SensorDataConverter.scaleEEG(0, 0), -41.25);
        assertEquals(SensorDataConverter.scaleEEG(0, 1023), 41.25);
    }

}
