
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;    

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author android-hw
 */
public class Server {
    
    private int port;
    ServerSocket server;
    Socket socket;
    ArrayList <ClientThread>al = new ArrayList();
    
    
    public Server(int port){
        
        this.port = port;
    }
    
    public void start(){
        try {
            System.out.println("Iniciating server...");
            server = new ServerSocket(port);
            
        } catch (IOException ex) {
            System.out.println("Could not create server");
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Server running.");
        
        while(true){
            try {
                socket = server.accept();
                System.out.println("Client connected!");
                al.add(new ClientThread(socket));
            } catch (IOException ex) {
                System.out.println("Could not create socket");
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }          

        }

        
    }  
    public static void main(String args[]){
        
        Server server = new Server(2121);
        server.start();
        
    }
    
class ClientThread extends Thread{
    
    Socket socket;
    DataInputStream input;
    DataOutputStream output;
    String username;
    String message;
    
    ClientThread(Socket socket){
        this.socket = socket;
        start();
    }
        public void writeMessage(String message){
        try {            
           byte size = (byte)message.length();
            //byte type = 0;
            
            //output.write(type);
            output.writeByte(size);
            output.write(message.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }          
        }
       
        public String readMessage(){
                      
            byte[] messageByte = new byte[1000];
            boolean end = false;
            String messageString = "";

            try 
            {               
                int bytesRead = 0;
                messageByte[0] = input.readByte();
                //messageByte[1] = input.readByte();
                
                int bytesToRead = messageByte[0];
                //int bytesToRead = messageByte[1];

                while(!end)
                {
                    bytesRead = input.read(messageByte);
                    messageString += new String(messageByte, 0, bytesRead);
                    if (messageString.length() == bytesToRead )
                    {
                        end = true;
                    }
                }
                System.out.println("MESSAGE: " + messageString);
                
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return messageString;
        }
    
        public void run(){
        try {            
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
            username = readMessage();
            
            //username = input.readUTF();
            System.out.println(username + " connected!");
        
        
            writeMessage("Users online: ");
            String users_online = "";
            for(ClientThread cl : al){ 
                if(!users_online.equals("")){users_online += ", ";}
                users_online += cl.username;
            }
            writeMessage(users_online);
            
        while(true){

            message = readMessage();

            if(message.equals("exit")){
                for(ClientThread cl : al){
                    if(cl.username.equals(username)){
                        al.remove(cl);
                        for(ClientThread c : al){
                            c.writeMessage(username + ": " + " desconectado.");
                        }
                        stop();
                    }
                
            }
            }
            
            for(ClientThread cl : al){
                cl.writeMessage(username + ": " + message);
            }
            
        }
            
        }
}
    
}

