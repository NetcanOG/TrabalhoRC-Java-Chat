import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public class ChatServer
{
  // A pre-allocated buffer for the received data
  static private final ByteBuffer buffer = ByteBuffer.allocate( 16384 );

  // Decoder for incoming text -- assume UTF-16
  static private final Charset charset = Charset.forName("UTF8");
  static private final CharsetDecoder decoder = charset.newDecoder();

  static List<Client> clients = new ArrayList<Client>();

  static public void main( String args[] ) throws Exception {
    // Parse port from command line
    int port = Integer.parseInt( args[0] );

    try {
      // Instead of creating a ServerSocket, create a ServerSocketChannel
      ServerSocketChannel ssc = ServerSocketChannel.open();

      // Set it to non-blocking, so we can use select
      ssc.configureBlocking( false );

      // Get the Socket connected to this channel, and bind it to the
      // listening port
      ServerSocket ss = ssc.socket();
      InetSocketAddress isa = new InetSocketAddress( port );
      ss.bind( isa );

      // Create a new Selector for selecting
      Selector selector = Selector.open();

      // Register the ServerSocketChannel, so we can listen for incoming
      // connections
      ssc.register( selector, SelectionKey.OP_ACCEPT );
      System.out.println( "Listening on port "+port );

      while (true) {
        // See if we've had any activity -- either an incoming connection,
        // or incoming data on an existing connection
        int num = selector.select();

        // If we don't have any activity, loop around and wait again
        if (num == 0) {
          continue;
        }

        // Get the keys corresponding to the activity that has been
        // detected, and process them one by one
        Set<SelectionKey> keys = selector.selectedKeys();
        Iterator<SelectionKey> it = keys.iterator();
        while (it.hasNext()) {
          // Get a key representing one of bits of I/O activity
          SelectionKey key = it.next();

          // What kind of activity is it?
          if (key.isAcceptable()) {

            // It's an incoming connection.  Register this socket with
            // the Selector so we can listen for input on it
            Socket s = ss.accept();
            Client tempClient = new Client(s);
            clients.add(tempClient);
            System.out.println( "Got connection from "+s );

            // Make sure to make it non-blocking, so we can use a selector
            // on it.
            SocketChannel sc = s.getChannel();
            sc.configureBlocking( false );

            // Register it with the selector, for reading
            sc.register( selector, SelectionKey.OP_READ );

          } else if (key.isReadable()) {

            SocketChannel sc = null;

            try {

              // It's incoming data on a connection -- process it
              sc = (SocketChannel)key.channel();
              boolean ok = processInput( sc );

              // If the connection is dead, remove it from the selector
              // and close it
              if (!ok) {
                key.cancel();

                Socket s = null;
                try {
                  s = sc.socket();
                  remove_user(s);
                  System.out.println( "Closing connection to "+s );
                  s.close();
                } catch( IOException ie ) {
                  System.err.println( "Error closing socket "+s+": "+ie );
                }
              }

            } catch( IOException ie ) {

              // On exception, remove this channel from the selector
              key.cancel();

              try {
                Socket s = sc.socket();
                remove_user(s);
                sc.close();
              } catch( IOException ie2 ) { System.out.println( ie2 ); }

              System.out.println( "Closed "+sc );
            }
          }
        }
        // We remove the selected keys, because we've dealt with them.
        //clients.clear();
        keys.clear();
      }
    } catch( IOException ie ) {
      System.err.println( ie );
    }
  }

  // Just read the message from the socket and send it to stdout
  static private boolean processInput( SocketChannel sc ) throws IOException {
    // Read the message to the buffer
    buffer.clear();
    sc.read( buffer );
    buffer.flip();

    // If no data, close the connection
    if (buffer.limit()==0) {
      return false;
    }

    // Decode and print the message
    String message = decoder.decode(buffer).toString();
    String[] bufferMessages = message.split("\n", 0);
    String tempmessage = bufferMessages[0].trim();

    System.out.println(tempmessage);

    Client user = getUser(sc.socket());
    if(user != null) processString(tempmessage,user);

    return true;
  }
  static void processString(String text, Client user) throws IOException{
    String[] words = text.split(" ", 0);
    String fstWord = words[0];

    switch(fstWord){
      case "/nick":
        if(user.state.equals("INIT") && nickAvailable(words[1])){
          user.s.getChannel().write(charset.encode("OK"));
          user.setState("OUTSIDE");
          user.nick = words[1];
          System.out.println("user name:"+user.nick);

        }
        else if(user.state.equals("INIT") && !nickAvailable(words[1])){
          user.s.getChannel().write(charset.encode("ERROR"));
        }
        else if(user.state.equals("OUTSIDE") && nickAvailable(words[1])){
          user.s.getChannel().write(charset.encode("OK"));
          user.nick = words[1];
        }
        else if(user.state.equals("OUTSIDE") && !nickAvailable(words[1])){
          user.s.getChannel().write(charset.encode("ERROR"));
        }
        else if(user.state.equals("INSIDE") && nickAvailable(words[1])){
          user.s.getChannel().write(charset.encode("OK"));
          for(Client otherUsr: clients){
            if(otherUsr.room == user.room){ //inside same room
              otherUsr.s.getChannel().write(charset.encode("NEWNICK "+ user.nick+" "+words[1]));
            }
          }
          user.nick = words[1];
        }
        else if(user.state.equals("INSIDE") && !nickAvailable(words[1])){
          user.s.getChannel().write(charset.encode("ERROR"));
        }
        else{ // nick without argument
          user.s.getChannel().write(charset.encode("ERROR"));
        }
        break;
      case "/join": break;
      case "/leave": break;
      case "/bye": break;
      default:
        if(words[0].charAt(0)=='/'){
          for(Client curClient: clients){
            if(curClient.room == user.room){
              curClient.s.getChannel().write(charset.encode("Message "+user.nick+" "+text.substring(1)));
            }
          }
        }
        else{
          for(Client curClient: clients){
            if(curClient.room == user.room){
              curClient.s.getChannel().write(charset.encode("Message "+user.nick+" "+text));
            }
          }
        }
        break;  //not a command but with "/"
    }
  }

  static private boolean nickAvailable(String nickname){
    System.out.println("nickAvailable:"+nickname);
    for(Client curClient: clients){
      if(curClient.nick.equals(nickname)) return false;
    }
    return true;
  }

  static private Client getUser( Socket s ){
    for(Client user: clients){
      if(user.s == s) return user;
    }
    return null;
  }

  static private void remove_user( Socket socket ){
    Iterator<Client> itr = clients.iterator();
    while(itr.hasNext()){
      Client c = itr.next();
      if(c.s.equals(socket)) itr.remove();
    }
  }
}
