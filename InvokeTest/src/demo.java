
public class demo {
	public static void main(String[] args) {
		MyInvocationHandle invocationHandle = new MyInvocationHandle();
		
		Subject subject = (Subject)invocationHandle.bind(new RealSubject("hello"));
		
		//String string = subject.say("sm", "man");
		//System.out.println(string);
		subject.getName();
	}
}
