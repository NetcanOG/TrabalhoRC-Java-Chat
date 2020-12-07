import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public class Client{
  Socket s;
  String room = null;
  State st;
  String nick = null;

  public Client(Socket s){
    this.s = s; 
  }

  public enum State{
    INIT,
    OUTSIDE,
    INSIDE
  }

  public void setState(String state){
    switch(state){
      case "INIT": this.st = State.INIT;break;
      case "OUTSIDE": this.st = State.OUTSIDE;break;
      case "INSIDE": this.st = State.INSIDE;break;
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
