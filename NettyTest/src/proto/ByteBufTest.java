package proto;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;

public class ByteBufTest {
	public static void main(String[] args) {
		ByteBuf buffer = Unpooled.buffer(10);
		
		String name = "sunmengahah\nahah";
		buffer.writeBytes(name.getBytes());
		System.out.println(buffer.array());
		for(int i = 0; i < buffer.writerIndex();i++){
			int index = buffer.forEachByte(ByteBufProcessor.FIND_LF);
			ByteBufProcessor p  = new ByteBufProcessor() {
				
				
				@Override
				public boolean process(byte byte0) throws Exception {
					// TODO Auto-generated method stub
					
					return byte0 != 97;
				}
			};
			
			System.out.println(buffer.forEachByte(p));
		}
	}
}
