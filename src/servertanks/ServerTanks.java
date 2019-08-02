
package servertanks;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;



/**
 *
 * @author Dawid
 */
public class ServerTanks implements Runnable 
{

       Socket socket1;
       Socket socket2;
       String key;
       
       ServerTanks(Socket socket1, Socket socket2, String key)
       {
         this.socket1 = socket1;
         this.socket2 = socket2;
         this.key = key;
       }
       
       static public String getCurrentTimeStamp() 
       {
          return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
       }
       
       

  
    public static void main(String[] args) throws IOException{
        
     ServerSocketFactory ssf = SSLServerSocketFactory.getDefault();
     ServerSocket ss = ssf.createServerSocket(6666);
    

     while(true)
     {
        String key="jolo";  // losowo wygenerowany bezpieczny klucz inny dla każdej rozgrywki
        Socket socket1 = ss.accept();   
        Socket socket2 = ss.accept();
        System.out.println("connected both players");
        new Thread(new ServerTanks(socket1,socket2, key)).start();
     }      
        
        
     
    }

    @Override
    public void run() {
        
try { 
        
           
        DataOutputStream out1=new DataOutputStream(socket1.getOutputStream()); 
        DataOutputStream out2=new DataOutputStream(socket2.getOutputStream()); 
        DataInputStream  in1 = new DataInputStream(socket1.getInputStream());
        DataInputStream  in2 = new DataInputStream(socket2.getInputStream());
          
       
        String logLine;
        logLine = "Connected player1 ip: "+ socket1.getInetAddress().toString() + "; player2 ip: "+ socket2.getInetAddress().toString()+" at: "+getCurrentTimeStamp();
        String nameOfFile= "log.txt";
        try(PrintWriter output = new PrintWriter(new FileWriter(nameOfFile,true))) 
        {
             output.printf("%s\r\n", logLine);
        } 
        catch (Exception e) {}
        
        
        String mess1;
        String mess2;
        String logLine1;
        String logLine2;
        
        
        out1.writeUTF(key);
        out2.writeUTF(key);
        
        String encrypt;
        
        encrypt= AES.encrypt("COR:360:435:0", key);
        out1.writeUTF(encrypt);
        encrypt= AES.encrypt("COR:460:435:0", key);
        out1.writeUTF(encrypt);
        encrypt= AES.encrypt("COR:460:435:0", key);
        out2.writeUTF(encrypt);
        encrypt= AES.encrypt("COR:360:435:0", key);
        out2.writeUTF(encrypt);
        while(true)
        {
            mess1 = in1.readUTF();
            mess2 = in2.readUTF(); 
            logLine1 = AES.encrypt(mess1,key);
             logLine2 = AES.encrypt(mess1,key);
             System.out.println(mess1);
             System.out.println(mess2);
             if(mess1 == "EXT:0:0:0" )
             {
                 
                  logLine = "Disconect player1 ip: "+ socket1.getInetAddress().toString() + "; player2 ip: "+ socket2.getInetAddress().toString()+" at: "+getCurrentTimeStamp();
                  try(PrintWriter output = new PrintWriter(new FileWriter(nameOfFile,true))) 
                  {
                    output.printf("%s\r\n", logLine);
                  } 
                  catch (Exception e) {}
                  socket1.close();
                  out2.writeUTF(mess1);
                  break;
               
               }
              else if(mess2 == "EXT:0:0:0")
              {
                  logLine = "Disconect player1 ip: "+ socket1.getInetAddress().toString() + "; player2 ip: "+ socket2.getInetAddress().toString()+" at: "+getCurrentTimeStamp();
                  try(PrintWriter output = new PrintWriter(new FileWriter(nameOfFile,true))) 
                  {
                    output.printf("%s\r\n", logLine);
                  } 
                  catch (Exception e) {}
                  socket2.close();
                  out1.writeUTF(mess2);
                  break;
              }
              else
              {
                  out2.writeUTF(mess1);
                  out1.writeUTF(mess2); 
              }
        }
        
       

        
  
      
      
    
 
}
catch (IOException ex) 
{
     Logger.getLogger(ServerTanks.class.getName()).log(Level.SEVERE, null, ex);
}      
   
        
        
        
       
    }
    
}

