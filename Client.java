import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public class Client{
  Socket s;
  String room;
  State s;
  String nick = null;

  public Client(Socket sc){
    sc = this.sc;
  }

  public enum State{
    INIT,
    OUTSIDE,
    INSIDE
  }

  public void setState(String state){
    switch(state){
      case "INIT": this.s = State.INIT;break;
      case "OUTSIDE": this.s = State.OUTSIDE;break;
      case "INSIDE": this.s = State.INSIDE;break;
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
