import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ChatClient{

    // Variáveis relacionadas com a interface gráfica --- * NÃO MODIFICAR *
    JFrame frame = new JFrame("Chat Client");
    private JTextField chatBox = new JTextField();
    private JTextArea chatArea = new JTextArea();
    // --- Fim das variáveis relacionadas coma interface gráfica

    // Se for necessário adicionar variáveis ao objecto ChatClient, devem
    // ser colocadas aqui

    // Decoder for incoming text -- assume UTF-8
    static private final Charset charset = Charset.forName("UTF8");
    static private final CharsetDecoder decoder = charset.newDecoder();
    static private final ByteBuffer bufferRead = ByteBuffer.allocate( 16384 );
    static private final ByteBuffer bufferWrite = ByteBuffer.allocate( 16384 );
    int port;
    String server;

    // Método a usar para acrescentar uma string à caixa de texto
    // * NÃO MODIFICAR *
    public void printMessage(final String message) {
        chatArea.append(message);
    }


    // Construtor
    public ChatClient(String server, int port) throws IOException {

        // Inicialização da interface gráfica --- * NÃO MODIFICAR *
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(chatBox);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.SOUTH);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.setSize(500, 300);
        frame.setVisible(true);
        chatArea.setEditable(false);
        chatBox.setEditable(true);
        chatBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    newMessage(chatBox.getText());
                } catch (IOException ex) {
                } finally {
                    chatBox.setText("");
                }
            }
        });
        frame.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                chatBox.requestFocus();
            }
        });

        // --- Fim da inicialização da interface gráfica
         //port = this.port;
         //server = this.server;
         ReadThread read = new ReadThread();
         WriteThread write = new WriteThread();

         SocketChannel socket;
         InetSocketAddress scAddr = new InetSocketAddress(server,port);
         socket = SocketChannel.open(scAddr);
        // Se for necessário adicionar código de inicialização ao
        // construtor, deve ser colocado aqui

    }

    public class ReadThread implements Runnable{
      public void run(){

      }
    }

    public class WriteThread implements Runnable{
      public void run(){

      }
    }

    // Método invocado sempre que o utilizador insere uma mensagem
    // na caixa de entrada
    public void newMessage(String message) throws IOException {
      // PREENCHER AQUI com código que envia a mensagem ao servidor
      try {


      } catch( IOException ie ) {
        System.err.println( ie );
      }
    }


    // Método principal do objecto
    public void run() throws IOException {
      // PREENCHER AQUI
      try {
        read.run();
        write.run();


      } catch( IOException ie ) {
        System.err.println( ie );
    }


    }


    // Instancia o ChatClient e arranca-o invocando o seu método run()
    // * NÃO MODIFICAR *
    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient(args[0], Integer.parseInt(args[1]));
        client.run();
    }

}
