import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public class Client{
  Socket sc;
  String room;
  State s;

  public Client(Socket sc){
    sc = this.sc;
  }

  public enum State{
    INIT,
    OUTSIDE,
    INSIDE
  }

  public void setState(int i){
    switch(i){
      case 1: this.s = State.INIT;break;
      case 2: this.s = State.OUTSIDE;break;
      case 3: this.s = State.INSIDE;break;
      default: System.err.println("error state.Restart conection!");
    }
  }
}
