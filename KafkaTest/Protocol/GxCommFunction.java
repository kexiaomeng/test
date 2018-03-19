/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Protocol;

import com.nari.commObjectPara.TerminalObject;
import com.nari.global.ShareGlobalObj;

/**
 * 
 * @author zhaomingyu
 */
public class GxCommFunction {
	private static ShareGlobalObj shareGlobalObjVar = ShareGlobalObj.getInstance();

	// 获取SEQ
	public static byte getSeqNumber(String terminalAddr) {
		// 帧序列域SEQ为1字节，用于描述帧之间的传输序列的变化规则，由于受报文长度限制，数据无法在一帧内传输，
		// 需要分成多帧传输（每帧都应有数据单元标识，都可以作为独立的报文处理）。

		// TpV=1：表示在附加信息域中带有时间标签Tp
		// FIR：置“1”，报文的第一帧。FIN：置“1”，报文的最后一帧。
		// 0 0 多帧：中间帧 //0 1 多帧：结束帧
		// 1 0 多帧：第1帧，有后续帧。 //1 1 单帧
		// 在所收到的报文中，CON位置“1”，表示需要对该帧报文进行确认；置“0”，表示不需要对该帧报文进行确认。

		byte tmpSeqNum = 0;
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		if (tmpTermObj != null) {
			if (tmpTermObj.isTerminalTpV())
				tmpSeqNum |= 0x80;
			if (tmpTermObj.isTerminalFIR())
				tmpSeqNum |= 0x40;
			if (tmpTermObj.isTerminalFIN())
				tmpSeqNum |= 0x20;
			if (tmpTermObj.isTerminalCON())
				tmpSeqNum |= 0x10;
			tmpSeqNum += tmpTermObj.getTerminalPSEQ();
		}
		return tmpSeqNum;
	}

	// 获取控制域C
	public static byte getControlField(String terminalAddr, boolean prmFlag, byte frameType) {
		byte tmpControlFieldVal = 0;
		byte funCode = 0;
		boolean terminalDIR = false;
		boolean terminalPRM = prmFlag;// 启动标志位PRM =1：表示此帧报文来自启动站
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		if (tmpTermObj != null) {
		boolean	terminalFCV = tmpTermObj.isTerminalFCV();
			if (terminalFCV) {
				tmpControlFieldVal |= 0x10;
				boolean terminalFCB = tmpTermObj.isTerminalFCB();
				if (tmpTermObj.isTerminalFCB())
					tmpControlFieldVal |= 0x20;
				tmpTermObj.setTerminalFCB(!terminalFCB);
			}
			if (terminalDIR)
				tmpControlFieldVal |= 0x80;
			if (terminalPRM)
				tmpControlFieldVal |= 0x40;

		}
		funCode = (byte) (frameType & 0x0f);
		tmpControlFieldVal += funCode;
		return tmpControlFieldVal;
	}

}
