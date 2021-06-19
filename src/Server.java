import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.BiFunction;

public class Server {

    ArrayList ClientOutputStreams;

    public static void main(String[] args) {
        new Server().go();
    }

    public void go(){
        ClientOutputStreams = new ArrayList();

        try{
            ServerSocket serverSocket = new ServerSocket(4242);
            System.out.println("System up on port 4242..");
            while(true){
                Socket sock = serverSocket.accept();
                PrintWriter writer = new PrintWriter(sock.getOutputStream());
                ClientOutputStreams.add(writer);

                Thread t = new Thread(new ClientHandler(sock));
                t.start();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    private class ClientHandler implements Runnable {
        Socket sock;
        public ClientHandler(Socket sock){
            this.sock = sock;
        }
        public void run(){
            InputStreamReader inputStreamReader = null;
            try {
                inputStreamReader = new InputStreamReader(sock.getInputStream());
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String message;
                while ((message = reader.readLine()) != null) {
                    tellEveryOne(message);
                }
            }catch(IOException ioException){
                    ioException.printStackTrace();
            }
        }
    }

    public void tellEveryOne(String message){
        Iterator it = ClientOutputStreams.iterator();

        while(it.hasNext()){
            try{
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            }catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
