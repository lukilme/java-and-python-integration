import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 65432;
        Socket socket = null;

        try {
            socket = new Socket(host, port);
            OutputStream os = socket.getOutputStream();
            Socket finalSocket = socket;

            Thread sendThread = new Thread(() -> {
                try {
                    while (true) {
                        String mensagem = JOptionPane.showInputDialog("Message:");
                        if (mensagem == null || mensagem.equalsIgnoreCase("exit")) {
                            os.write("CLOSE_CONNECTION".getBytes());
                            os.flush();
                            break;
                        }
                        os.write(mensagem.getBytes());
                        os.flush(); 
                        System.out.println("Message sent to server: " + mensagem);
                        Thread.sleep(1000);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    try {
                        os.write("CLOSE_CONNECTION".getBytes());
                        os.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            Thread receiveThread = new Thread(() -> {
                try (InputStream is = finalSocket.getInputStream();
                     BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

                    String linha;
                    while ((linha = br.readLine()) != null) {
                        System.out.println("Response from server: " + linha);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            sendThread.start();
            receiveThread.start();

            sendThread.join();
            receiveThread.join();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
