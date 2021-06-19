import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client {
    JTextArea incoming;
    JTextField outgoing;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;

    public static void main(String[] args) {
        Client client = new Client();
        client.go();
    }

    public void go(){
        JFrame jFrame = new JFrame("Simple chat app");
        JPanel mainPanel = new JPanel();
        incoming = new JTextArea(15, 50);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);
        incoming.setVisible(true);
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());
        mainPanel.add(qScroller);
//        mainPanel.add(incoming);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        setUpNetworking();
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();

        jFrame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        jFrame.setSize(400, 500);
        jFrame.setVisible(true);
    }

    public void setUpNetworking(){
        try{
            sock = new Socket("127.0.0.1", 4242);
            InputStreamReader inputStreamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(inputStreamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("Networking established..");

        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }



    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try{
                String message = outgoing.getText();
                writer.println(message);
                writer.flush();

                outgoing.setText("");
                outgoing.requestFocus();
            }catch(Exception exception){
                exception.printStackTrace();
            }
        }
    }

    private class IncomingReader implements Runnable {
        public void run(){
            String incomingMessage = null;
            try{
                while((incomingMessage = reader.readLine()) != null){
                    incoming.append(incomingMessage + "\n");
                    System.out.println("Message received: " + incomingMessage);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
