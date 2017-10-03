package fahrradsicherung.bicylock.client;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import fahrradsicherung.bicylock.VARIABLES;

/**
 * Created by Raptor on 30.08.2017.
 */

public class Client {

    private MessageListener listener = null;

    private boolean running = false;
    private PrintWriter mBufferOut;
    private BufferedReader mBufferIn;
    public Client(){




    }


    public void setListener(MessageListener listener){
        this.listener = listener;
    }

    public void connect(){

        if(listener != null){


            running = true;

            try {
                InetAddress serverAddr = InetAddress.getByName(VARIABLES.SERVER_IP);

                Log.e("TCP Client", "C: Connecting...");

                //create a socket to make the connection with the server
                Socket socket = new Socket(serverAddr, VARIABLES.SERVER_PORT);

                try {

                    mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                    mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                    while (running) {

                        String mServerMessage = mBufferIn.readLine();

                        if (mServerMessage != null) {
                            listener.messageReceived(mServerMessage);
                        }
                    }


                } catch (Exception e) {

                    Log.e("TCP", "S: Error", e);

                } finally {
                    //the socket must be closed. It is not possible to reconnect to this socket
                    // after it is closed, which means a new socket instance has to be created.
                    socket.close();
                }

            } catch (Exception e) {

                Log.e("TCP", "C: Error", e);

            }



        }else{
            Log.e("TCP","No msg listener setted");

        }






    }

    public boolean isConnected(){
        return running;
    }

    public void sendLogin(String name,String passwort){
        sendText(MessageTypes.LOGIN,name + " " + passwort);
    }

    public void sendText(MessageTypes type,String text) {
        if (mBufferOut != null && !mBufferOut.checkError()) {
            if(type == MessageTypes.LOGIN){
                mBufferOut.print("LOGIN ");
            }
            mBufferOut.println(text);
            mBufferOut.flush();
        }
    }


    public void disconnect(){
        running = false;
    }



    public interface MessageListener{
        public void messageReceived(String message);
    }









}
