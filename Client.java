public class Client{
  Socket sc;
  String room;
  State s;

  public client(Socket sc,){
    sc = this.sc;
  }

  public enum State{
    INIT,
    OUTSIDE,
    INSIDE
  }

  public setState(int i){
    switch(i){
      case 1: this.s = INIT;break;
      case 2: this.s = OUTSIDE;break;
      case 3: this.s = INSIDE;break;
      default: ("error state.Restart conection!");
    }
  }
}
