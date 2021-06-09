import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Receive {

	public static void main(String[] args) {
        int PORT_NUMBER  = 4445;
        String pathToSave = "C:\\Users\\aselc\\eclipse-files\\";
        createFile(PORT_NUMBER, pathToSave);
    }

    public static void createFile (int port, String serverRoute){
        try{
            DatagramSocket socket = new DatagramSocket(port);
            byte[] receiveFileName = new byte[1024];
            DatagramPacket receiveFileNamePacket = new DatagramPacket(receiveFileName, receiveFileName.length);
            socket.receive(receiveFileNamePacket);
            
            System.out.println("File name receiving...");
            
            byte [] data = receiveFileNamePacket.getData();
            String fileName = new String(data, 0, receiveFileNamePacket.getLength());
            
            System.out.println("File creating...");
            File f = new File (serverRoute + fileName);
            
            FileOutputStream outToFile = new FileOutputStream(f);
            
            receiveFile(outToFile, socket);
        }catch(Exception ex){
            ex.printStackTrace();
            }   
    }
    
    private static void receiveFile(FileOutputStream outToFile, DatagramSocket socket) throws IOException {
        System.out.println("File receiving...");
        
        while (true) {
            byte[] message = new byte[101];
            byte[] fileByteArray = new byte[100];
            boolean isEnd;
            DatagramPacket receivedPacket = new DatagramPacket(message, message.length);
            socket.receive(receivedPacket);
            message = receivedPacket.getData();

            InetAddress address = receivedPacket.getAddress();
            int port = receivedPacket.getPort();
            
            int fileLength=(int) message[0];
            System.out.println(fileLength);
            isEnd = (fileLength<100);
           
            if (!isEnd) {
                System.arraycopy(message, 1, fileByteArray, 0, 100);
                outToFile.write(fileByteArray);
                
                sendAck(socket, address, port);
                
            }
            if (isEnd) {
            	System.arraycopy(message, 1, fileByteArray, 0, fileLength);
                outToFile.write(fileByteArray);
                outToFile.close();
                sendAck(socket, address, port);
                break;
            }
        }
    }
        private static void sendAck(DatagramSocket socket, InetAddress address, int port) throws IOException {
            byte[] ackPacket = new byte[101];
            for (int i = 0; i < 101; i++) {
            	  ackPacket[i]=0x7E;
            	}
            DatagramPacket acknowledgement = new DatagramPacket(ackPacket, ackPacket.length, address, port);
            socket.send(acknowledgement);
        }
}    
