package proto;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;

public class ByteBufTest {
	public static void main(String[] args) {
		CompositeByteBuf buffer = Unpooled.compositeBuffer();
		
		ByteBuf heapBuf = Unpooled.buffer();
		String name = "sunmengahah\nahah";
		heapBuf.writeBytes(name.getBytes());
		
		ByteBuf buf[] = new ByteBuf[2];
		
		ByteBuf directBuf = Unpooled.directBuffer();
		
		
		directBuf.writeBytes(name.getBytes());
		
		buf[0] = heapBuf;
		buf[1] = directBuf;
		buffer.addComponents(0,directBuf);
		ByteBuf tmpBuf = heapBuf.slice(0,6);
		while (tmpBuf.isReadable()) {
			System.out.println("tmpBuf: "+tmpBuf.getByte(0));
		}
		
		System.out.println(buffer.toString());
		while(heapBuf.isReadable()){
			System.out.println(heapBuf.readByte());

		}
		System.out.println(heapBuf.indexOf(2, 5, (byte)0x55));
		if(heapBuf.hasArray()){
			System.out.println(heapBuf.array());
			for(int i = 0; i < heapBuf.writerIndex();i++){
				int index = buffer.forEachByte(ByteBufProcessor.FIND_LF);
				ByteBufProcessor p  = new ByteBufProcessor() {
					
					
					@Override
					public boolean process(byte byte0) throws Exception {
						// TODO Auto-generated method stub
						
						return byte0 != 97;
					}
				};
				
				System.out.println(heapBuf.forEachByte(p));
			}
		}else {
			System.out.println("no array");
		}

	}
}
