package ThreadTest;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hello world!
 *
 */
public class App extends Thread
{
	private Seq seq ;
	public App(Seq seq){
		this.seq = seq;
	}
	public void run(){
		int i = 0;
		while(i<10000){
			System.out.println(seq.getNum()+i);
			
		}
	}
    public static void main( String[] args )
    {
    	Seq seq = new Seq();
    	App app = new App(seq);
    	App app1 = new App(seq);
    	app.start();
    	app1.start();
    	for(int i = 0;i< 10000;i++){
        	
        	System.out.println(app1.seq.getNum());
    	}
    	
    	
    	
        System.out.println( "Hello World!" );
    }
   
}

class Seq{
	private final int num = 0;
	public  int getNum(){
		
		return 	num;
	}

}
