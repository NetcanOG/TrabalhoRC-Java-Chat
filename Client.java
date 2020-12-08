import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public class Client{
  Socket s;
  String room = null;
  String state = "INIT";
  String nick = null;

  public Client(Socket s){
    this.s = s;
  }

  public void setState(String state){
    switch(state){
      case "INIT": this.state = "INIT";break;
      case "OUTSIDE": this.state = "OUTSIDE";break;
      case "INSIDE": this.state = "INSIDE";break;
      default: System.err.println("error state.Restart conection!");
    }
  }

  public void setRoom(String room){
    this.room = room;
  }

  public void setNick(String nick){
    this.nick = nick;
  }
}
