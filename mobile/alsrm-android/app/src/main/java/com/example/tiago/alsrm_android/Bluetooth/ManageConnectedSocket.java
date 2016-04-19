package com.example.tiago.alsrm_android.Bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.tiago.alsrm_android.Activity.ECG_Activity;
import com.example.tiago.alsrm_android.Activity.MainActivity;
import com.example.tiago.alsrm_android.BITalino.BITalinoDevice;
import com.example.tiago.alsrm_android.BITalino.BITalinoException;
import com.example.tiago.alsrm_android.BITalino.BITalinoFrame;
import com.example.tiago.alsrm_android.BITalino.SensorDataConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Message;

public class ManageConnectedSocket extends Thread {

    private BluetoothSocket btSocket;
    private InputStream InStream;
    private OutputStream OutStream;
    private BITalinoDevice bitalino;
    private int [] digitalChannels;
    private int [] analogChannels;
    private int samplerate;

    public ManageConnectedSocket(BluetoothDevice device, int samplerate, int[] analogChannels, int[] digitalChannels) {

        BluetoothSocket tmp = null;

        this.analogChannels = analogChannels;
        this.digitalChannels = digitalChannels;
        this.samplerate = samplerate;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MainActivity.MY_UUID);
        } catch (IOException e) { }
        btSocket = tmp;
    }

    public void run() {

        // Cancel discovery because it will slow down the connection
        MainActivity.bluetoothAdapt.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            btSocket.connect();

        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                btSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = btSocket.getInputStream();
            tmpOut = btSocket.getOutputStream();
        } catch (IOException e) { }

        this.InStream = tmpIn;
        this.OutStream = tmpOut;

        try {

            bitalino = new BITalinoDevice(samplerate, analogChannels);
            bitalino.open(InStream, OutStream);

            // get BITalino version
            //System.out.println("Version: " + bitalino.version());

            // start acquisition on predefined analog channels
            bitalino.start();

            // trigger digital outputs
            bitalino.trigger(digitalChannels);

            // Keep listening to the InputStream until an exception occurs
            while (true) {

                // Read from the InputStream
                final int numberOfSamplesToRead = 100;
                BITalinoFrame[] frames = bitalino.read(numberOfSamplesToRead);

                for (BITalinoFrame frame : frames) {

                    double [] data = DataConverter(frame);
                    // System.out.println("Frame: " + frame.toString());

                    // Send the obtained bytes to the UI activity
                    // Gets a Message object, stores the state in it, and sends it to the Handler
                    Message completeMessage = ECG_Activity.HandlerMessager.obtainMessage(0, data);
                    completeMessage.sendToTarget();
                    Thread.sleep(15);
                }
            }

        } catch (BITalinoException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private double[] DataConverter(BITalinoFrame frame){

        double [] data = new double [6];

        int[] analogChannels = bitalino.getAnalogChannels();

        for (int i = 0; i < analogChannels.length; i++) {

            if (analogChannels[i] == 0) data[0] = SensorDataConverter.scaleEMG(0, frame.getAnalog(0));
            if (analogChannels[i] == 1) data[1] = SensorDataConverter.scaleECG(1, frame.getAnalog(1));
            if (analogChannels[i] == 2) data[2] = SensorDataConverter.scaleEDA(2, frame.getAnalog(2));
            if (analogChannels[i] == 3) data[3] = SensorDataConverter.scaleEEG(3, frame.getAnalog(3));

            if (analogChannels[i] == 4) data[4] = SensorDataConverter.scaleAccelerometer(4, frame.getAnalog(4));
            if (analogChannels[i] == 5) data[5] = SensorDataConverter.scaleLuminosity(5, frame.getAnalog(5));
        }

        return data;
    }

    /* Call this from the main activity to send data to the remote device */
    public void stopBitalino(){
        try {
            bitalino.stop();
        } catch (BITalinoException e) {
            e.printStackTrace();
        }
    }

    public String versionBitalino(){
        try{
            return bitalino.version();
        } catch (BITalinoException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void batteryBitalino(int value){
        try {
            bitalino.battery(value);
        } catch (BITalinoException e) {
            e.printStackTrace();
        }
    }

    public void triggerBitalino(int[] digitalArray){
        try {
            bitalino.trigger(digitalArray);
        } catch (BITalinoException e) {
            e.printStackTrace();
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            btSocket.close();
        } catch (IOException e) { }
    }
}
