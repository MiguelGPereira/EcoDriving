import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;

public class SocketClient {
    
    public static void main(String[] args) throws ParseException, IOException {        
        DataOutputStream os = null; // os: output stream
        BufferedReader is = null; // is: input stream
        Socket smtpSocket = null; // smtpClient: our client socket

        try {
            InetAddress thisIp = InetAddress.getByName("nomadtech.serveftp.com");
            System.out.println(thisIp.getHostAddress());
            smtpSocket = new Socket( thisIp, 5000);
            os = new DataOutputStream(smtpSocket.getOutputStream());
            is = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: hostname");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: hostname");
        }
        
        if (smtpSocket != null && os != null && is != null) {
            try {
                // write to server
                os.writeBytes("R");
                String responseLine;
                while ((responseLine = is.readLine()) != null) {
                    System.out.println(responseLine);
                    LogLine logLine = new LogLine(responseLine);
                    logLine.print();
                }
                os.close();
                is.close();
                smtpSocket.close();   
            } catch (UnknownHostException e) {
                System.err.println("Trying to connect to unknown host: " + e);
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    } 
}
