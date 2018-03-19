
public class test {
	public static void main(String[] args) {
		Ifactory ifactory = new AodiFactory();
		
		Car car = ifactory.getCar();
		car.SayCar();
	}
}
