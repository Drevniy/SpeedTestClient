import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		InetAddress serverIP = null;
		int serverPort = 0;
		
		for (int i = 0; i < args.length; i++) {
			if(args[i].matches(".+server-ip")){
				serverIP = InetAddress.getByName(args[i+1]);
			}else if(args[i].matches(".+server-port")){
				serverPort = Integer.parseInt(args[i+1]);
			}
		}
		
		Socket socket = new Socket(serverIP, serverPort);
		
		PrintWriter output = new PrintWriter(socket.getOutputStream(), true);		
		output.println("download");	
		
        InputStream input = socket.getInputStream();
        long total = 0;
        byte[] bytes = new byte[32*1024];
        long endTime = System.currentTimeMillis() + 5000;        
        while( System.currentTimeMillis() < endTime){
            int read = input.read(bytes);
            if (read < 0) break;
            total += read;
        }
        input.close();
        
        long downloadSpeedResult = total/10/8;  
        
        socket = new Socket(serverIP, serverPort);
        output = new PrintWriter(socket.getOutputStream(), true);		
        output.println("upload");
    	
        ClientUploadSpeed uploadSpeed = new ClientUploadSpeed(socket);
        Thread tUploadSpeed = new Thread(new ClientUploadSpeed(socket));
        tUploadSpeed.start();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        int uploadSpeedResult = Integer.parseInt(in.readLine());
        tUploadSpeed.interrupt();
        socket.close();
        
        System.out.printf("Download Speed: %,d Bits/s%n",downloadSpeedResult);
        System.out.printf("Upload Speed: %,d Bits/s%n",uploadSpeedResult);
	}

}

class ClientUploadSpeed implements Runnable{
	OutputStream output;
	
	public ClientUploadSpeed(Socket socket) throws IOException {
		output = socket.getOutputStream();	 
	}
	  public void run(){  
	       byte[] bytes = new byte[32*1024];
	       try {
	       while(true) {	            
					output.write(bytes);
	        }
	       } catch (IOException e) {				
	    	   }	      
	  } 
}
