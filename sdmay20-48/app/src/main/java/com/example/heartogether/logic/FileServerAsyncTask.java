package com.example.heartogether.logic;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import com.example.heartogether.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServerAsyncTask extends AsyncTask {

    private Context context;
    private WifiP2pConfig config;
    private TextView statusText;

    public FileServerAsyncTask(Context context, WifiP2pConfig config) {
        this.context = context;
        this.config = config;
        // this.statusText = (TextView) statusText;
    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(MainActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {

            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = serverSocket.accept();

            /**
             * Client code
             */
            int len = 1024;
            Socket socket = new Socket();
            byte buf[]  = new byte[1024];
            try {
                /**
                 * Create a client socket with the host,
                 * port, and timeout information.
                 */
                socket.bind(null);
                socket.connect(new InetSocketAddress(config.deviceAddress, 8888), 500);

                /**
                 * Create a byte stream from a JPEG file and pipe it to the output stream
                 * of the socket. This data is retrieved by the server device.
                 */
                OutputStream outputStream = socket.getOutputStream();
                ContentResolver cr = context.getContentResolver();
                InputStream inputStream = null;
                inputStream = cr.openInputStream(Uri.parse("path/to/picture.jpg"));
                while ((len = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                inputStream.close();
            } catch (FileNotFoundException e) {
                //catch logic
            } catch (IOException e) {
                //catch logic
            }

            /**
             * Clean up any open sockets when done
             * transferring or if an exception occurred.
             */
            finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            //catch logic
                        }
                    }
                }
            }

            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a JPEG file
             */
            final File f = new File(Environment.getExternalStorageDirectory(), "Notes");
            File gpxfile = new File(f, "FileName");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append("WORDSSSS");
            writer.flush();
            writer.close();
            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();
            InputStream inputstream = client.getInputStream();
            copyFile(inputstream, new FileOutputStream(f));
            serverSocket.close();
            return f.getAbsolutePath();
        } catch (IOException e) {
            Log.e(MainActivity.TAG, e.getMessage());
            return null;
        }

    }
}
