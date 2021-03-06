
import static org.junit.Assert.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */

/**
 * @author lenovo
 *
 */
public class CaseTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testHandler(){
		ByteBuf buf  = Unpooled.buffer();
		
		for(int i = 0; i< 10 ;i++){
			buf.writeByte(i);
		}
		ByteBuf input = buf.duplicate();
		
		EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
		assertTrue(channel.writeInbound(input.retain()));
		assertTrue(channel.finish());
		System.out.println(buf.refCnt());

		ByteBuf read = Unpooled.buffer();
		read = (ByteBuf)channel.readInbound();
		
		assertEquals(buf.readSlice(3),read);
		read.release();
		
		read = (ByteBuf)channel.readInbound();
		
		assertEquals(buf.readSlice(3),read);
		read.release();
		
		read = (ByteBuf)channel.readInbound();
		
		assertEquals(buf.readSlice(3),read);
		read.release();
		
		read = (ByteBuf)channel.readInbound();
		assertEquals(buf.readSlice(3), read);
		read.release();
		
		assertNull(channel.readInbound());

		buf.release();
		
	}

}
