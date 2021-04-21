package me.aqua_rel.irrigationsystem.Utility;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class TcpClient {
    private final String host;
    private final int port;

    public TcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String request(String msg) throws Exception {
        Socket socket = new Socket(host, port);
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();

        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        dataOutputStream.writeBytes(String.format("%s\n", msg));
        dataOutputStream.flush();

        String line = reader.readLine();

        dataOutputStream.close();
        reader.close();
        socket.close();

        return line;
    }
}
