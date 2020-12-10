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

    // Decoder for incoming text -- assume UTF-16
    static private final Charset charset = Charset.forName("UTF8");
    static private final CharsetDecoder decoder = charset.newDecoder();
    //static private final CharsetEncoder encoder = charset.newEncoder();
    static private ByteBuffer bufferRead = ByteBuffer.allocate( 16384 );
    static private ByteBuffer bufferWrite = ByteBuffer.allocate( 16384 );
    ReadThread read;
    WriteThread write;
    SocketChannel sc;
    InetSocketAddress scAddr;

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

         //create Threads to read and write text from server
         read = new ReadThread();
         write = new WriteThread();

         //connect socket to ChatServer
         scAddr = new InetSocketAddress(server,port);
         sc = SocketChannel.open(scAddr);
        // Se for necessário adicionar código de inicialização ao
        // construtor, deve ser colocado aqui

    }

    public class ReadThread implements Runnable{
      public void run(){
        try{
          bufferRead.clear();
          int closed = sc.read(bufferRead);
          bufferRead.flip();
          String message = decoder.decode(bufferRead).toString();
          //System.out.println(message);
          if(message.equals("BYE") || closed == -1){
            sc.socket().close();
            sc.close();
          }
          
          String[] rcvMessages = message.split(" ", 3);

          if(rcvMessages.length > 1){
            switch(rcvMessages[0]){
              case "MESSAGE":
              printMessage(rcvMessages[1]+": "+rcvMessages[2]+"\n");
              break;
              
              case "NEWNICK":
              printMessage(rcvMessages[1]+" mudou de nome para "+rcvMessages[2]+"\n");
              break;
              
              case "JOINED":
              printMessage(rcvMessages[1]+" entrou na sala\n");
              break;
              
              case "LEFT":
              printMessage(rcvMessages[1]+" saiu da sala\n");
              break;

              case "PRIVATE":
              printMessage("Mensagem privada de "+rcvMessages[1]+": "+rcvMessages[2]+"\n");
              break;
            }
          }else{
            printMessage(message+"\n");
          }
        }
        catch( IOException ie ) {
          System.err.println( ie );
        }
      }
    }

    public class WriteThread implements Runnable{
      public void run(){
        if(!(bufferWrite == null || bufferWrite.remaining()==0)){ //buffer is not empty
          try{
            sc.write(bufferWrite);
            bufferWrite.flip();
          } catch ( IOException ie ){
            System.err.println( ie );
          }
        }
      }
    }

    // Método invocado sempre que o utilizador insere uma mensagem
    // na caixa de entrada
    public void newMessage(String message) throws IOException {
      // PREENCHER AQUI com código que envia a mensagem ao servidor
      if(message.isEmpty()) return;
      bufferWrite.clear();
      String[] strs = message.split(" ",0);
      if(escape(strs[0]) == 1) bufferWrite = charset.encode("/"+message+"\n");
      else bufferWrite = charset.encode(message+"\n");
      write.run();
    }


    // Método principal do objecto
    public void run() throws IOException {
      // PREENCHER AQUI
      while(!sc.socket().isClosed()){
        read.run();
      }
    }

    //detect if is command, string or string with "/" first
    static private int escape (String cmd){
      switch(cmd){
        case "/nick": return 0;
        case "/join": return 0;
        case "/leave": return 0;
        case "/bye": return 0;
        case "/priv": return 0;
        default:
          if(cmd.charAt(0)=='/') return 1;  //not a command but with "/"
          return 0;                         //string
      }
    }

    // Instancia o ChatClient e arranca-o invocando o seu método run()
    // * NÃO MODIFICAR *
    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient(args[0], Integer.parseInt(args[1]));
        client.run();
    }

}
