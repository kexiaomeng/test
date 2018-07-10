package kafkaListen.interFace;

public interface IListener<E> {
	
	abstract public void msgHandler(byte []msg);
	abstract public void handler(E obj);
}
