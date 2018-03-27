package proto;

import com.google.protobuf.InvalidProtocolBufferException;

public class TestReq {
	private static byte[] encode(SubScribeReq1.SubScribeReq req){
		return req.toByteArray();
	}
	private static SubScribeReq1.SubScribeReq decode(byte[] body) throws InvalidProtocolBufferException{
		return SubScribeReq1.SubScribeReq.parseFrom(body);
	}
	private static SubScribeReq1.SubScribeReq createScribeReq() throws InvalidProtocolBufferException{
		SubScribeReq1.SubScribeReq.Builder builder = SubScribeReq1.SubScribeReq.newBuilder();
		
		builder.setSubId(1);
		builder.setAddr("ÖÐ¹ú");
		builder.setName("ËïÃÈ");
		builder.setProductName("Ï£");
		
		
		
		return builder.build();
	}
	
	public static void main(String[] args) throws InvalidProtocolBufferException {
		TestReq req = new TestReq();
		SubScribeReq1.SubScribeReq subReq = req.createScribeReq();
		
		System.out.println("Before: +"+subReq.toString());
		SubScribeReq1.SubScribeReq subReq2 = decode(encode(subReq));
		System.out.println("After: "+subReq2.toString());
		
		System.out.println("Assert equal "+subReq.equals(subReq));
	}
	
}
