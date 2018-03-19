
public class RealSubject implements Subject{
	
	private String name;
	
	public RealSubject(String name){
		this.name = name;
	}
	public String getName() {
		System.out.println(name);
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@Override
	public String say(String name,String sex){
		return "ÐÕÃû£º"+name+sex;
	}
	@Override
	public String setName() {
		// TODO Auto-generated method stub
		return null;
	}
}
