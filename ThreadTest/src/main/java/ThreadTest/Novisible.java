package ThreadTest;

public class Novisible {
	private static int number = 0;
	private static boolean ready;
	
	private static class TestThread extends Thread{
		@Override
		public void run(){
			while(!ready){
				Thread.yield();
				System.out.println(number);	
			}
		}
	}
	public static void main(String[] args) {
		for(int i = 0;i<10000;i++){
			number = 42;
			new Novisible.TestThread().start();
			ready  = true;
		}
		

	}
}
