import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.JFileChooser;

public class Send {
	private static int PORT_NUMBER = 4445;
    public static void main(String[] args) {
    	
        String host = "127.0.0.1"; // local host
        Send sender = new Send();
        sender.start(PORT_NUMBER, host);
    }
    
    private void start(int port, String host) {

        System.out.println("Choose file to send...");
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(host);
            String fileName;

            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (jfc.isMultiSelectionEnabled()) {
                jfc.setMultiSelectionEnabled(false);
            }

            int r = jfc.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION) {
                File f = jfc.getSelectedFile();
                
                fileName = f.getName();
                byte[] fileNameBytes = fileName.getBytes();
                DatagramPacket fileStatPacket = new DatagramPacket(fileNameBytes, fileNameBytes.length, address, port);
                socket.send(fileStatPacket);

                byte[] fileByteArray = convertFileToByteArray(f);
                sendFile(socket, fileByteArray, address, port);
            }
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    private static byte[] convertFileToByteArray(File file) {
        FileInputStream fis = null;

        byte[] byteArray = new byte[(int) file.length()];
        try {
            fis = new FileInputStream(file);
            fis.read(byteArray);
            fis.close();

        } catch (IOException ioExp) {
            ioExp.printStackTrace();
        }
        return byteArray;
    }
    
    private void sendFile(DatagramSocket socket, byte[] byteArrayOfFile, InetAddress address, int port) throws IOException {
        System.out.println("File sending...");
        boolean isEnd=false;
        int sizeOfFile = byteArrayOfFile.length;
        
        for (int i = 0; i < (byteArrayOfFile.length/100)+1; i=i+1) {
            
        	byte[] buf = new byte[101];
        	
        	if(sizeOfFile>=100) {
        		buf[0] =(byte) 100;
        		System.arraycopy(byteArrayOfFile , 100*i , buf , 1 , 100);
	            sizeOfFile-=100;
	            
        	}else {
        		buf[0] = (byte) sizeOfFile;
        		System.arraycopy(byteArrayOfFile , 100*i , buf , 1 , sizeOfFile);
        		isEnd=true;
           	}
        	
        	System.out.println(sizeOfFile);
        	DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, port);
            socket.send(sendPacket);
            
            boolean isReceived=false;
            
            while (!isReceived) {
                byte[] ack = new byte[101];
                
                DatagramPacket ackPack = new DatagramPacket(ack , ack.length);
                socket.receive(ackPack);
                
                isReceived = true;
            }
            if(isEnd) {
        		break;
        	}
        }
    }
    
    
}
