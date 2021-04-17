package me.aqua_rel.irrigationsystem.Utility;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpClient {
    public UdpClient() {
    }

    public void request(String ip, int port, String msg) throws Exception {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(ip);
        byte[] sendData;
        //byte[] receiveData = new byte[1024];
        sendData = msg.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        clientSocket.send(sendPacket);
        //new DatagramPacket(receiveData, receiveData.length);
        clientSocket.close();
        Log.i("Udp Client", "Sent messsage \"" + msg + "\" to " + ip + ":" + port);
    }
}
