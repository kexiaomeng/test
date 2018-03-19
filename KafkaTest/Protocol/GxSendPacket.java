/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Protocol;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arms.protocols.CommMethods;

import com.nari.fe.commdefine.define.FrontConstant;
import com.nari.fe.commdefine.param.ErrorCode;
import com.nari.fe.commdefine.task.Item;
import com.nari.fe.commdefine.task.MeterInfo;
import com.nari.fe.commdefine.task.Response;
import com.nari.fe.commdefine.task.ResponseItem;
import com.nari.fe.commdefine.task.TaskInfo;
import com.nari.global.ShareGlobalObj;
import com.nari.commObjectPara.TaskQueueObject;
import com.nari.commObjectPara.TaskResultObject;
import com.nari.commObjectPara.ConstDef;
import com.nari.commObjectPara.TerminalObject;
import com.nari.protocol.Dl645Protocol07New.DataForward645Report;
import com.nari.protocol.Dl698Protocol.Dl698SendPacket;
import com.nari.protocol.Dl698Protocol.PropertiesUtils;
import com.nari.protocol.Fk05Protocol.Fk05SendPacket;
import com.nari.protocolBase.AsduConverter;
import com.nari.util.HexDump;

/**
 * 
 * @author zhaomingyu
 */
public class GxSendPacket extends Dl698SendPacket{
	private static Logger log = LoggerFactory.getLogger(GxSendPacket.class);
	private static ShareGlobalObj shareGlobalObjVar = ShareGlobalObj.getInstance();
	private AsduConverter asduConver = new AsduConverter();

	// 复位命令下行报文
	public ArrayList<TaskResultObject> sendResetOrder(TaskQueueObject taskObj) {
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		int frameIndexNo = 0;
		int fnVal = 1;
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;

		long taskId = taskObj.getTermTask().getTaskId();
		TaskInfo taskInfo = null;
//		ITaskHandle taskhandle = null;
		try {
			InetAddress ia = null;//InetAddress.getLocalHost();
//			ia = shareGlobalObjVar.getLocalInetAddress();
//			String localName = ia.getHostName().toLowerCase();
//			taskhandle = TaskHandle.getSharedInstance("");
//			taskInfo = taskhandle.getTaskInfo(taskId);
			taskInfo = shareGlobalObjVar.getFrontTaskInfoByTaskId(taskId);
			if (taskInfo != null){
				log.debug("taskId:" + taskId + "任务信息："+ taskInfo.toString());
			}else{
				log.error("taskInfo==null");
				return null;
			}
		} catch (Exception e) {
			log.error("",e);
		}
		if (taskInfo == null) {
			log.error("taskInfo为空");
			return null;
		}
		Map<Short, List<Item>> fnMap = taskInfo.getPnFuncs().get((short)0);
		if (fnMap.size() <= 0) {
			log.error("funMap's size is 0!");
			return null;
		}
		for(Short fn:fnMap.keySet()){
			fnVal = fn;
		}

		// FIR=1，FIN=1，CON=0,有Tp---
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		byte terminalPFC = 0;
		short seqNum = 0;
		if (tmpTermObj != null) {
			tmpTermObj.setTerminalFIR(true);
			tmpTermObj.setTerminalFIN(true);
			tmpTermObj.setTerminalCON(true);
			//tmpTermObj.setTerminalTpV(true);
			// PSEQ
			terminalPFC = tmpTermObj.getTerminalPFC();
			tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));
			// FCB
			tmpTermObj.setTerminalFCB(false);
			sendBuff[frameIndexNo++] = 0x01;// 复位命令（AFN=01H）
			seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
			sendBuff[frameIndexNo++] = (byte) seqNum;

			sendBuff[frameIndexNo++] = 0x00;// 数据单元标识（DA=0),4字节
			sendBuff[frameIndexNo++] = 0x00;
			// F1 硬件初始化
			// F2 数据区初始化
			// F3 参数及全体数据区初始化（即恢复至出厂配置）
			// F4 参数（除与系统主站通信有关的）及全体数据区初始化
			sendBuff[frameIndexNo++] = (byte) ((asduConver.pow((fnVal + 7) % 8)) % 256);
			sendBuff[frameIndexNo++] = (byte) ((fnVal + 7) / 8 - 1);

			log.debug("--Fk04复位命令下行报文(AFN=01H):sendBuff length==" + frameIndexNo);
			resultObj = new TaskResultObject();
			resultObj.setFramePSEQ((short) (seqNum & 0x0f));
			resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
			resultObj.setResultValue(0);
			resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
			resultObj.setRetMsgLen(frameIndexNo);
			resultObj.setRetMsgBuf(sendBuff);
			retObjList = new ArrayList<TaskResultObject>();
			retObjList.add(resultObj);
		}
		return retObjList;
	}

	// 确认∕否认命令下行报文
	public ArrayList<TaskResultObject> sendConfirmOrder(TaskQueueObject taskObj, int fnType) {
		int afnNo = taskObj.getTermTask().getFuncCode();
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		int frameIndexNo = 0;
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;
		// FIR=1，FIN=1，CON=0,有Tp
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		byte terminalPFC = 0;
		short seqNum = 0;
		if (tmpTermObj != null) {
			tmpTermObj.setTerminalFIR(true);
			tmpTermObj.setTerminalFIN(true);
			tmpTermObj.setTerminalCON(false);
			//tmpTermObj.setTerminalTpV(true);
			// PSEQ
			terminalPFC = tmpTermObj.getTerminalPFC();
			tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));

			sendBuff[frameIndexNo++] = 0x00;// 确认∕否认命令（AFN=00H）
			seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
			sendBuff[frameIndexNo++] = (byte) seqNum;
		} else {
			fnType = 1;
		}

		if (afnNo == 0x02) {
			log.debug("--Fk04确认∕否认命令下行报文afnNo == 0x02");
			sendBuff[frameIndexNo++] = 0x00;// 数据单元标识（DA=0)
			sendBuff[frameIndexNo++] = 0x00;//
			sendBuff[frameIndexNo++] = 0x04;// 数据单元标识（DT=0)
			sendBuff[frameIndexNo++] = 0x00;//
			sendBuff[frameIndexNo++] = (byte) afnNo;// AFN（要被确认的报文的AFN）
			sendBuff[frameIndexNo++] = 0x00;// 数据单元标识1
			sendBuff[frameIndexNo++] = 0x00;//
			sendBuff[frameIndexNo++] = (byte) ((asduConver.pow((fnType + 7) % 8)) % 256);
			sendBuff[frameIndexNo++] = (byte) ((fnType + 7) / 8 - 1);
			sendBuff[frameIndexNo++] = 0x00;// ERR1
		} else {
			// 根据F1,F2,F3分别定义不同的数据单元标识、数据单元
			if (fnType == ConstDef.FN_DEFINE_F1)// f1全部确认
			{
				sendBuff[frameIndexNo++] = 0x00;// 数据单元标识（DA=0)
				sendBuff[frameIndexNo++] = 0x00;//
				sendBuff[frameIndexNo++] = 0x01;// 数据单元标识（DT=0)
				sendBuff[frameIndexNo++] = 0x00;// 0:正确; 1 :出错
			} else if (fnType == ConstDef.FN_DEFINE_F2)// f2全部否认
			{
				sendBuff[frameIndexNo++] = 0x00;// 数据单元标识（DA=0)
				sendBuff[frameIndexNo++] = 0x00;//
				sendBuff[frameIndexNo++] = 0x02;// 数据单元标识（DT=0)
				sendBuff[frameIndexNo++] = 0x00;// 0:正确; 1 :出错

			} else if (fnType == ConstDef.FN_DEFINE_F3)// F3：按数据单元标识确认和否认
			{
				sendBuff[frameIndexNo++] = 0x00;// 数据单元标识（DA=0)
				sendBuff[frameIndexNo++] = 0x00;//
				sendBuff[frameIndexNo++] = 0x04;// 数据单元标识（DT=0)
				sendBuff[frameIndexNo++] = 0x00;//
				sendBuff[frameIndexNo++] = 0x00;// 0:正确; 1 :出错

				// AFN（要被确认的报文的AFN） 1
				// 数据单元标识1 4
				// ERR1 1
				// 数据单元标识2 4
				// ERR2 1
				// sendBuff[frameIndexNo++] = 0x00;
			}
		}

		// 事件计数器EC用于ACD位置“1”的上行响应报文中，EC由2字节组成，分别为重要事件计数器EC1和一般事件计数器EC2。
		// 计数范围0~255，循环加1递增。
//		if (tmpTermObj != null) {
//			sendBuff[frameIndexNo++] = tmpTermObj.getTerminalReadEC1();// 重要事件计数器EC1
//			sendBuff[frameIndexNo++] = tmpTermObj.getTerminalReadEC2();// 一般事件计数器EC2
//		}
//		log.debug("--Fk04Protocol 确认∕否认命令下行报文(AFN=00H):sendBuff length==" + frameIndexNo);
		resultObj = new TaskResultObject();
		resultObj.setFramePSEQ((short) (seqNum & 0x0f));
		resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
		resultObj.setResultValue(0);
		resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
		resultObj.setRetMsgLen(frameIndexNo);
		resultObj.setRetMsgBuf(sendBuff);
		retObjList = new ArrayList<TaskResultObject>();
		retObjList.add(resultObj);
		return retObjList;
	}

	// 中继站命令（AFN=03H）
	public ArrayList<TaskResultObject> sendRelayStationCommand(TaskQueueObject taskObj) {

		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		int frameIndexNo = 0;
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;

		// FIR=1，FIN=1，CON=0,无Tp
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		byte terminalPFC = 0;
		short seqNum = 0;
		if (tmpTermObj != null) {
			tmpTermObj.setTerminalFIR(true);
			tmpTermObj.setTerminalFIN(true);
			tmpTermObj.setTerminalCON(false);
			tmpTermObj.setTerminalTpV(false);
			// PSEQ
			terminalPFC = tmpTermObj.getTerminalPFC();
			tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));

			sendBuff[frameIndexNo++] = 0x03;// 中继站命令（AFN=03H）
			seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
			sendBuff[frameIndexNo++] = (byte) seqNum;
		}

		ArrayList<String> retStrList = asduConver.convertDataObjToBuff(taskObj);
		if (retStrList.size() > 0) {
			byte tmpBuff[];
			try {
				tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
				for (int m = 0; m < tmpBuff.length; m++) {
					sendBuff[frameIndexNo++] = tmpBuff[m];
				}
			} catch (UnsupportedEncodingException e) {
				log.error("",e);
			}
		}

		log.debug("--Fk04中继站命令（AFN=03H）:: sendBuff length==" + frameIndexNo);

		resultObj = new TaskResultObject();
		resultObj.setFramePSEQ((short) ((seqNum & 0xff) % 16));
		resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
		resultObj.setResultValue(0);
		resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
		resultObj.setRetMsgLen(frameIndexNo);
		resultObj.setRetMsgBuf(sendBuff);
		retObjList = new ArrayList<TaskResultObject>();
		retObjList.add(resultObj);
		return retObjList;
	}

	// 设置参数（AFN=04H）命令下行报文
	public ArrayList<TaskResultObject> sendSetParaOrder(TaskQueueObject taskObj) {
		
		if(shareGlobalObjVar.getSysProvince().equals("3") 
				&& (taskObj.getTmnlPwd() == null
				    || taskObj.getTmnlPwd().equals("")
				    || taskObj.getTmnlPwd().equals("666666"))){//别的规约默认密码是666666
			//如果是华北现场，则先招測下密码（FN＝05），然后根据密码组装PW域
			ArrayList<TaskResultObject> retObjList = sendQueryParametersOrder(taskObj);
			byte[] buffer = new byte[6];
			if(retObjList != null && retObjList.size() >0){
				System.arraycopy(retObjList.get(0).getRetMsgBuf(), 0, buffer,
						0, buffer.length - 2);
				buffer[4] = 0x10;
				buffer[5] = 0x00;
				retObjList.get(0).setRetMsgBuf(buffer);
				retObjList.get(0).setRetMsgLen(buffer.length);	
				return retObjList;
			}
		}
		
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		int frameIndexNo = 0;
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;

		ArrayList<String> retStrList = asduConver.convertDataObjToBuff(taskObj);
		byte terminalPFC = 0;
		short seqNum = 0;
		if (retStrList.size() > 0) {
			retObjList = new ArrayList<TaskResultObject>();
			for (int k = 0; k < retStrList.size(); k++) {
				frameIndexNo = 0;
				// FIR=1，FIN=1，CON=1,有Tp
				TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
				if (tmpTermObj != null) {
					tmpTermObj.setTerminalFIR(true);
					tmpTermObj.setTerminalFIN(true);
					tmpTermObj.setTerminalCON(true);
					tmpTermObj.setTerminalTpV(false);
					// PSEQ
					terminalPFC = tmpTermObj.getTerminalPFC();
					tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));

					sendBuff[frameIndexNo++] = 0x04;// 设置参数（AFN=04H）
					seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
					sendBuff[frameIndexNo++] = (byte) seqNum;
				}

				byte tmpBuff[];
				try {
					tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
					for (int m = 0; m < tmpBuff.length; m++) {
						sendBuff[frameIndexNo++] = tmpBuff[m];
					}
				} catch (UnsupportedEncodingException e) {
					log.error("",e);
				}
				frameIndexNo = new Fk05SendPacket().adjustToAddLunciPower(taskObj, sendBuff, frameIndexNo);
				log.debug("--Fk04设置参数(AFN=04H)下行报文:sendBuff length==" + frameIndexNo);

				resultObj = new TaskResultObject();
				resultObj.setFramePSEQ((short) (seqNum & 0x0f));
				resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
				resultObj.setResultValue(0);
				resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
				resultObj.setRetMsgLen(frameIndexNo);
				resultObj.setRetMsgBuf(sendBuff);
				retObjList.add(resultObj);
			}
		}
		return retObjList;
	}
	
	String getChineseInfo(TaskQueueObject taskObj){
		String chineseInfo = null;
		String localName = "";
		InetAddress ia = null;
		TaskInfo taskInfo = null;
//		ITaskHandle taskhandle = null;
		long taskId = taskObj.getTermTask().getTaskId();
		try {
			//ia = InetAddress.getLocalHost();
//			ia = shareGlobalObjVar.getLocalInetAddress();
		} catch (Exception e) {
			log.error("取本机地址信息出错！");
		}
//		localName = ia.getHostName().toLowerCase();
//		taskhandle = TaskHandle.getSharedInstance("");
		try {
//			if(taskObj.getTermTask().getTaskType() != FrontConstant.TASK_TYPE_FRONT_TASK)
//				taskInfo = taskhandle.getTaskInfo(taskId);
//			else
				taskInfo = shareGlobalObjVar.getFrontTaskInfoByTaskId(taskId);
		} catch (Exception e) {
			log.error("取taskInfo出错");
			// log.error("",e);
		}
		if (taskInfo == null) {
			log.error("taskInfo==null ,  tmnl addr :"
					+ taskObj.getTermTask().getTerminalAddr() + "taskId:"
					+ taskId);
			return null;
		}
		Map<Short, Map<Short, List<Item>>> pointMap = taskInfo.getPnFuncs();
		for (Map.Entry<Short, Map<Short, List<Item>>> pointEntry : pointMap
				.entrySet()) {
			Map<Short, List<Item>> funMap = pointEntry.getValue();
			for (Map.Entry<Short, List<Item>> funEntry : funMap.entrySet()) {
				List<Item> itemList = funEntry.getValue();
				if(itemList != null){
					for(Item item : itemList){
						if(item.getCode().endsWith("0520004")){
							//中文信息值
							chineseInfo = item.getValue();
							break;
						}
					}
				}
			}
			break; //只有p0
		}
		return chineseInfo;
	}

	// 控制命令（AFN=05H）命令下行报文
	public ArrayList<TaskResultObject> sendControlOrder(TaskQueueObject taskObj) {
		
		if(shareGlobalObjVar.getSysProvince().equals("3") 
				&& (taskObj.getTmnlPwd() == null
				    || taskObj.getTmnlPwd().equals("")
				    || taskObj.getTmnlPwd().equals("666666"))){//别的规约默认密码是666666
			//如果是华北现场，则先招測下密码（FN＝05），然后根据密码组装PW域
			ArrayList<TaskResultObject> retObjList = sendQueryParametersOrder(taskObj);
			byte[] buffer = new byte[6];
			if(retObjList != null && retObjList.size() >0){
				System.arraycopy(retObjList.get(0).getRetMsgBuf(), 0, buffer,
						0, buffer.length - 2);
				buffer[4] = 0x10;
				buffer[5] = 0x00;
				retObjList.get(0).setRetMsgBuf(buffer);
				retObjList.get(0).setRetMsgLen(buffer.length);	
				return retObjList;
			}
		}
		
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		int frameIndexNo = 0;
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;

		String chineseInfo = null;
		chineseInfo = getChineseInfo(taskObj);
		
		ArrayList<String> retStrList = asduConver.convertDataObjToBuff(taskObj);
		byte terminalPFC = 0;
		short seqNum = 0;
		if (retStrList.size() > 0) {
			retObjList = new ArrayList<TaskResultObject>();
			for (int k = 0; k < retStrList.size(); k++) {
				frameIndexNo = 0;
				// FIR=1，FIN=1，CON=1,有Tp
				TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
				if (tmpTermObj != null) {
					tmpTermObj.setTerminalFIR(true);
					tmpTermObj.setTerminalFIN(true);
					tmpTermObj.setTerminalCON(true);
//					tmpTermObj.setTerminalTpV(taskObj.isHasTpV());

					// PSEQ
					terminalPFC = tmpTermObj.getTerminalPFC();
					tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));

					sendBuff[frameIndexNo++] = 0x05;// 控制命令（AFN=05H）
					seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
					sendBuff[frameIndexNo++] = (byte) seqNum;
				}

				byte tmpBuff[];
				try {
					tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
					for (int m = 0; m < tmpBuff.length; m++) {
						sendBuff[frameIndexNo++] = tmpBuff[m];
					}
				} catch (UnsupportedEncodingException e) {
					log.error("",e);
				}

				if(chineseInfo != null){
					try {
						byte[] value = chineseInfo.getBytes("GBK");
						
						log.debug("中文信息：" + HexDump.toHexString(value));
						frameIndexNo = 7;
						sendBuff[frameIndexNo++] = (byte)(value.length);
						for(byte b : value){
							sendBuff[frameIndexNo++] = b;
						}
						log.debug("sendBuff=" + HexDump.toHexString(sendBuff,0,frameIndexNo));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						log.error("",e);
					}
				}
				
				log.debug("--Fk04控制(AFN=05H)下行报文:sendBuff length==" + frameIndexNo);

				resultObj = new TaskResultObject();
				resultObj.setFramePSEQ((short) ((seqNum & 0xff) % 16));
				resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
				resultObj.setResultValue(0);
				resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
				resultObj.setRetMsgLen(frameIndexNo);
				resultObj.setRetMsgBuf(sendBuff);
				retObjList.add(resultObj);
			}
		}
		return retObjList;
	}

	// 身份认证及密钥协商（AFN=06H）命令下行报文
	public ArrayList<TaskResultObject> sendEncryptionOrder(TaskQueueObject taskObj) {
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		int frameIndexNo = 0;
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;

		// FIR=1，FIN=1，CON=1,有Tp
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		byte terminalPFC = 0;
		short seqNum = 0;
		if (tmpTermObj != null) {
			tmpTermObj.setTerminalFIR(true);
			tmpTermObj.setTerminalFIN(true);
			tmpTermObj.setTerminalCON(true);
			//tmpTermObj.setTerminalTpV(true);
			// PSEQ
			terminalPFC = tmpTermObj.getTerminalPFC();
			tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));

			sendBuff[frameIndexNo++] = 0x06;// 身份认证及密钥协商（AFN=06H）
			seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
			sendBuff[frameIndexNo++] = (byte) seqNum;
		}

		ArrayList<String> retStrList = asduConver.convertDataObjToBuff(taskObj);
		if (retStrList.size() > 0) {
			byte tmpBuff[];
			try {
				tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
				for (int m = 0; m < tmpBuff.length; m++) {
					sendBuff[frameIndexNo++] = tmpBuff[m];
				}
			} catch (UnsupportedEncodingException e) {
				log.error("",e);
			}
		}

		log.debug("--Fk04身份认证及密钥协商（AFN=06H）:: sendBuff length==" + frameIndexNo);

		resultObj = new TaskResultObject();
		resultObj.setFramePSEQ((short) (seqNum & 0x0f));
		resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
		resultObj.setResultValue(0);
		resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
		resultObj.setRetMsgLen(frameIndexNo);
		resultObj.setRetMsgBuf(sendBuff);
		retObjList = new ArrayList<TaskResultObject>();
		retObjList.add(resultObj);
		return retObjList;
	}

	// 请求被级联终端主动上报（AFN=08H）
	public ArrayList<TaskResultObject> sendRequestCascadedTerminalOrder(TaskQueueObject taskObj) {
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		int frameIndexNo = 0;
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;

		// FIR=1，FIN=1，CON=0,无Tp
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		byte terminalPFC = 0;
		short seqNum = 0;
		if (tmpTermObj != null) {
			tmpTermObj.setTerminalFIR(true);
			tmpTermObj.setTerminalFIN(true);
			tmpTermObj.setTerminalCON(true);
			tmpTermObj.setTerminalTpV(false);

			// PSEQ
			terminalPFC = tmpTermObj.getTerminalPFC();
			tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));

			sendBuff[frameIndexNo++] = 0x08;// 请求被级联终端主动上报（AFN=08H）
			seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
			sendBuff[frameIndexNo++] = (byte) seqNum;
		}

		ArrayList<String> retStrList = asduConver.convertDataObjToBuff(taskObj);
		if (retStrList.size() > 0) {
			byte tmpBuff[];
			try {
				tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
				for (int m = 0; m < tmpBuff.length; m++) {
					sendBuff[frameIndexNo++] = tmpBuff[m];
				}
			} catch (UnsupportedEncodingException e) {
				log.error("",e);
			}
		}

		log.debug("--Fk04请求被级联终端主动上报(AFN=08H):sendBuff length==" + frameIndexNo);

		resultObj = new TaskResultObject();
		resultObj.setFramePSEQ((short) ((seqNum & 0xff) % 16));
		resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
		resultObj.setResultValue(0);
		resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
		resultObj.setRetMsgLen(frameIndexNo);
		resultObj.setRetMsgBuf(sendBuff);
		retObjList = new ArrayList<TaskResultObject>();
		retObjList.add(resultObj);
		return retObjList;
	}

	// 请求终端配置（AFN=09H）
	public ArrayList<TaskResultObject> sendRequestTerminalConfigurationOrder(TaskQueueObject taskObj) {
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		int frameIndexNo = 0;
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;

		// FIR=1，FIN=1，CON=1,有Tp
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		byte terminalPFC = 0;
		short seqNum = 0;
		if (tmpTermObj != null) {
			tmpTermObj.setTerminalFIR(true);
			tmpTermObj.setTerminalFIN(true);
			tmpTermObj.setTerminalCON(false);
			//tmpTermObj.setTerminalTpV(true);

			// PSEQ
			terminalPFC = tmpTermObj.getTerminalPFC();
			tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));

			sendBuff[frameIndexNo++] = 0x09;// 请求终端配置（AFN=09H）
			seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
			sendBuff[frameIndexNo++] = (byte) seqNum;
		}
		// F1 终端版本信息
		// F2 输入、输出及通信端口配置
		// F3 其他配置
		// F4 参数配置（本终端软硬件版本支持的参数）
		// F5 控制配置（本终端软硬件版本支持的控制命令）
		// F6 1类数据配置（本终端软硬件版本支持的1类数据）
		// F7 2类数据配置（本终端软硬件版本支持的2类数据）
		// F8 事件记录配置（本终端软硬件版本支持的事件记录）

		ArrayList<String> retStrList = asduConver.convertDataObjToBuff(taskObj);
		if (retStrList.size() > 0) {
			byte tmpBuff[];
			try {
				tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
				for (int m = 0; m < tmpBuff.length; m++) {
					sendBuff[frameIndexNo++] = tmpBuff[m];
				}
			} catch (UnsupportedEncodingException e) {
				log.error("",e);
			}
		}
		log.debug("--Fk04请求终端配置:sendBuff length==" + frameIndexNo);

		resultObj = new TaskResultObject();
		resultObj.setFramePSEQ((short) ((seqNum & 0xff) % 16));
		resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
		resultObj.setResultValue(0);
		resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
		resultObj.setRetMsgLen(frameIndexNo);
		resultObj.setRetMsgBuf(sendBuff);
		retObjList = new ArrayList<TaskResultObject>();
		retObjList.add(resultObj);
		return retObjList;
	}

	// 查询参数（AFN=0AH）
	public ArrayList<TaskResultObject> sendQueryParametersOrder(TaskQueueObject taskObj) {
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		int frameIndexNo = 0;
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;

		// FIR=1，FIN=1，CON=1,有Tp
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		byte terminalPFC = 0;
		short seqNum = 0;
		if (tmpTermObj != null) {
			tmpTermObj.setTerminalFIR(true);
			tmpTermObj.setTerminalFIN(true);
			tmpTermObj.setTerminalCON(false);
			tmpTermObj.setTerminalTpV(false);

			// PSEQ
			terminalPFC = tmpTermObj.getTerminalPFC();
			tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));

			sendBuff[frameIndexNo++] = 0x0A;// 查询参数（AFN=0AH）
			seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
			sendBuff[frameIndexNo++] = (byte) seqNum;
		}

		ArrayList<String> retStrList = asduConver.convertDataObjToBuff(taskObj);
		if (retStrList == null) {
			log.debug("--------------Fk04Protocol 错误的TASK------------------");
		} else {
			if (retStrList.size() > 0) {
				byte tmpBuff[];
				try {
					tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
					for (int m = 0; m < tmpBuff.length; m++) {
						sendBuff[frameIndexNo++] = tmpBuff[m];
					}
				} catch (UnsupportedEncodingException e) {
					log.error("",e);
				}
			}
			log.debug("--Fk04 查询参数(AFN=0AH):sendBuff length==" + frameIndexNo);

			resultObj = new TaskResultObject();
			resultObj.setFramePSEQ((short) (seqNum & 0x0f));
			resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
			resultObj.setResultValue(0);
			resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
			resultObj.setRetMsgLen(frameIndexNo);
			resultObj.setRetMsgBuf(sendBuff);
			retObjList = new ArrayList<TaskResultObject>();
			retObjList.add(resultObj);
		}
		return retObjList;
	}

	// 请求任务数据（AFN=0BH）
	public ArrayList<TaskResultObject> sendRequestTaskDataOrder(TaskQueueObject taskObj) {
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		int frameIndexNo = 0;
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;

		// FIR=1，FIN=1，CON=1,有Tp
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		byte terminalPFC = 0;
		short seqNum = 0;
		if (tmpTermObj != null) {
			tmpTermObj.setTerminalFIR(true);
			tmpTermObj.setTerminalFIN(true);
			tmpTermObj.setTerminalCON(false);
			//tmpTermObj.setTerminalTpV(true);

			// PSEQ
			terminalPFC = tmpTermObj.getTerminalPFC();
			tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));

			sendBuff[frameIndexNo++] = 0x0B;// 请求任务数据（AFN=0BH）
			seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
			sendBuff[frameIndexNo++] = (byte) seqNum;
		}

		ArrayList<String> retStrList = asduConver.convertDataObjToBuff(taskObj);
		if (retStrList.size() > 0) {
			byte tmpBuff[];
			try {
				tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
				for (int m = 0; m < tmpBuff.length; m++) {
					sendBuff[frameIndexNo++] = tmpBuff[m];
				}
			} catch (UnsupportedEncodingException e) {
				log.error("",e);
			}
		}

		log.debug("--Fk04请求任务数据(AFN=0BH):sendBuff length==" + frameIndexNo);

		resultObj = new TaskResultObject();
		resultObj.setFramePSEQ((short) (seqNum & 0x0f));
		resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
		resultObj.setResultValue(0);
		resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
		resultObj.setRetMsgLen(frameIndexNo);
		resultObj.setRetMsgBuf(sendBuff);
		retObjList = new ArrayList<TaskResultObject>();
		retObjList.add(resultObj);
		return retObjList;
	}

	// 文件传输（AFN=0FH）
	public ArrayList<TaskResultObject> sendFileTransferOrder(TaskQueueObject taskObj) {
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		int frameIndexNo = 0;
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;

		// FIR=1，FIN=1，CON=1,有Tp
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		byte terminalPFC = 0;
		short seqNum = 0;
		if (tmpTermObj != null) {
			tmpTermObj.setTerminalFIR(true);
			tmpTermObj.setTerminalFIN(true);
			tmpTermObj.setTerminalCON(true);
			//tmpTermObj.setTerminalTpV(true);

			// PSEQ
			terminalPFC = tmpTermObj.getTerminalPFC();
			tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));
			sendBuff[frameIndexNo++] = 0x0F;// 文件传输（AFN=0FH）
			seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
			sendBuff[frameIndexNo++] = (byte) seqNum;
		}

		ArrayList<String> retStrList = asduConver.convertDataObjToBuff(taskObj);
		if (retStrList.size() > 0) {
			byte tmpBuff[];
			try {
				tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
				for (int m = 0; m < tmpBuff.length; m++) {
					sendBuff[frameIndexNo++] = tmpBuff[m];
				}
			} catch (UnsupportedEncodingException e) {
				log.error("",e);
			}
		}

		log.debug("--Fk04文件传输:sendBuff length==" + frameIndexNo);

		resultObj = new TaskResultObject();
		resultObj.setFramePSEQ((short) (seqNum & 0x0f));
		resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
		resultObj.setResultValue(0);
		resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
		resultObj.setRetMsgLen(frameIndexNo);
		resultObj.setRetMsgBuf(sendBuff);
		retObjList = new ArrayList<TaskResultObject>();
		retObjList.add(resultObj);
		return retObjList;
	}

	//---------------------------------------modify by wyx 2012-11-22--------------------------------------------//
	// 数据转发（AFN=10H）
	public ArrayList<TaskResultObject> sendDataForwardingOrder(TaskQueueObject taskObj) {
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		TerminalObject tmpTermObj = shareGlobalObjVar
				.getTerminalPara(terminalAddr);
		
		GxTransParent trans = new GxTransParent();
//		taskObj.setStartTime(System.currentTimeMillis());
//		taskObj.setTimeOutTime(taskObj.getTimeOutTime() *2);
		if (tmpTermObj == null) {
			return trans.assFailTaskResultObject(taskObj, null, null, "终端对象为空", true);
		}

		int funNo = -1;
		int step = taskObj.getStep();
		try{
		if (step<0){//初始化，并保存相关变量
		    List<MeterInfo> meterInfos = new ArrayList<MeterInfo>();
	        GxTransParent dp = new GxTransParent();		
		    funNo = dp.getMeterInfosByTaskID(taskObj.getTermTask(),meterInfos);
		    if(funNo < 0 || meterInfos == null || meterInfos.size() <= 0){
		    	return returnFailResponse(taskObj, ErrorCode.B_RuntimeErr,"运行期错误，或下发的电表列表为空");
		    }
		    
		    for(int i =0; i < meterInfos.size(); i++){
		    	String addr = meterInfos.get(i).getMeterAddr();
		    	addr = (addr == null ? "" : addr); 
		    	meterInfos.get(i).setMeterAddrAddZeroNum(12-addr.length());  //add by wyx 2012-12-06 for omid meter event
		    	String meterAddr = CommMethods.StretchStringByLen(addr, "0", 12);
		    	meterInfos.get(i).setMeterAddr(meterAddr);
		    }
		    //保存电表信息
		    tmpTermObj.setMeterInfos(meterInfos);
		    //保存功能号
		    tmpTermObj.setAfn((byte)funNo);
		    
		    tmpTermObj.setMeterNo(null);
		    
		    printMeterInfos(meterInfos);
		    
//		    for (int i = 0 ; i < meterInfos.size() ; i ++ ){
//		    	log.debug("第" + i +"块电表信息");
//		        log.debug("表地址"+meterInfos.get(i).getMeterAddr());
//		        log.debug("Note:"+meterInfos.get(i).getNote());
//		        log.debug("cmType="+meterInfos.get(i).getCmdType());
//		        log.debug("bakString"+meterInfos.get(i).getBakString());
//		        log.debug("belongCollector:" + meterInfos.get(i).getBelongCollector());
//		        log.debug("\n");
//		    }
		    
//		    if(Dl698FeikongTest.isStart){//测试付峰的批量费控下发
//				return new Dl698FeikongTest().asskTaskResultObject(taskObj, meterInfos);
//			}
		    
		    MeterInfo meterInfo = meterInfos.get(0);
	    	if(meterInfo.getCmdType() ==0x0C || meterInfo.getCmdType() == 0x05){ //无需身份认证，直接转发645报文
	    		return sendDataForward645Report(taskObj);
	    	}else{//just for compatible, hope delete later
	    		if(meterInfo.getNote() != null && meterInfo.getNote().length() >=2 
	    				&& meterInfo.getNote().substring(0, 2).equals("0A")){
	    			return sendDataForwardReading645Data(taskObj);
	    		}
	    	}
		
	    	//需要验证数据库中的电表资产号是否是表号时可开启此标志
		    if(/*MeterInfo.IDENTITY_BY_NO == meterInfo.getIdentityType() || */Boolean.parseBoolean(PropertiesUtils.getProperty("sm.askMeterNo"))){
		    	log.debug("首先询问电表表号");
		    	tmpTermObj.setAskMeterNo(true);
		    	taskObj.setStep(GxTransParent.STEP_ASKMETERNO);
		    }else if(meterInfo.getIdentityType() == 2/*明文控制电表拉合闸*/){
		    	log.debug("明文控制拉合闸");
		    	tmpTermObj.setAskMeterNo(false);
		    	taskObj.setStep(GxTransParent.STEP_CONTROL_BY_OPEN_KEY);
		    }else{
		    	taskObj.setStep(GxTransParent.STEP_IDENTITYAUTHENTICATION);
		    	tmpTermObj.setAskMeterNo(false);
		    }
		}else {
			funNo = tmpTermObj.getAfn();
		}
		
		return sendDataForwarding(taskObj);
		}catch(Exception e){
			log.error("",e);
			log.error(e.toString());
			return trans.assFailTaskResultObject(taskObj, null, null, "数据转发时异常", true);
		}
		
	}
	
	/**
	 * F1数据转发
	 * @param taskObj
	 * @return
	 */
	public ArrayList<TaskResultObject> sendDataForwarding(
			TaskQueueObject taskObj) {

		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;
		GxTransParent dp = new GxTransParent();
		
		TerminalObject tmpTermObj = shareGlobalObjVar
				.getTerminalPara(terminalAddr);
		if (tmpTermObj == null) {
			return dp.assFailTaskResultObject(taskObj, retObjList, null, "终端对象为空", true);
		}

		
		int step = taskObj.getStep();
		if ((step == GxTransParent.STEP_ASKMETERNO) && tmpTermObj.isAskMeterNo()) {
			// 询问表号
			List<MeterInfo> meterInfos = tmpTermObj.getMeterInfos();
			if (meterInfos.size() <= 0) {
				return dp.assFailTaskResultObject(taskObj, retObjList, null, "MeterInfos列表空", true);
			}
			String meterAddr = meterInfos.get(0).getMeterAddr();
			if (meterAddr == null) {
				return dp.assFailTaskResultObject(taskObj, retObjList, null, "电表地址空", true);
			}

			// 组装645报文
			byte[] sendbuf = null;
			try {
				sendbuf = dp.assAmeterNoReport(meterAddr);
			} catch (Exception e) {
				return dp.assFailTaskResultObject(taskObj, retObjList, null, "组装读电表报文出错", true);
			}

			if (sendbuf == null || sendbuf.length <= 0) {
				return dp.assFailTaskResultObject(taskObj, retObjList, null, "读电表报文长度零", true);
			}

			ShareGlobalObj.getInstance().setTransBuf(
					taskObj.getTermTask().getTerminalAddr(), sendbuf);
			ArrayList<String> retStrList = asduConver.convertDataObjToBuff(taskObj);
			int frameIndexNo = 0;
			if (retStrList == null) {
				return dp.assFailTaskResultObject(taskObj, retObjList, null, "asdu数据转换出错", true);
			} else {
				if (retStrList.size() > 0) {
					byte tmpBuff[];
					try {
						sendBuff[frameIndexNo++] = 0x10;// 透传（AFN=10H）
						byte terminalPFC = tmpTermObj.getTerminalPFC();
						tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));
						tmpTermObj.setTerminalTpV(false);
						tmpTermObj.setTerminalFIR(true);
						tmpTermObj.setTerminalFIN(true);
						tmpTermObj.setTerminalCON(false);

						int seqNum = GxCommFunction
								.getSeqNumber(terminalAddr);// SEQ
						sendBuff[frameIndexNo++] = (byte) seqNum;

						tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
						for (int m = 0; m < tmpBuff.length; m++) {
							sendBuff[frameIndexNo++] = tmpBuff[m];
						}

					} catch (UnsupportedEncodingException e) {
						return dp.assFailTaskResultObject(taskObj, retObjList, null, "不支持的字符编码转换", true);
					}
				}
				resultObj = new TaskResultObject();
				int seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
				resultObj.setFramePSEQ((short) (seqNum & 0x0f));
				resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
				resultObj.setResultValue(0);
				resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
				resultObj.setRetMsgLen(frameIndexNo);
				resultObj.setRetMsgBuf(sendBuff);
				retObjList = new ArrayList<TaskResultObject>();
				retObjList.add(resultObj);
				taskObj.setStep(GxTransParent.STEP_ASKMETERNO);
				return retObjList;
			}
		} else if (step == GxTransParent.STEP_IDENTITYAUTHENTICATION) {
			List<MeterInfo> meterInfos = tmpTermObj.getMeterInfos();
			if (meterInfos.size() <= 0) {
				return dp.assFailTaskResultObject(taskObj, retObjList, null, "MeterInfos列表为空", true);
			}
			
			MeterInfo meterInfo = meterInfos.get(0);
			String AmmeterAddr = meterInfo.getMeterAddr();
			String AmmeterNo = meterInfo.getBakString();
			int identityType = meterInfo.getIdentityType();
			log.debug("身份认证类型：" + identityType);
			if (AmmeterAddr == null || AmmeterAddr.length() <= 0) {
				return dp.assFailTaskResultObject(taskObj, retObjList, null, "电表地址为空", true);
			}
			
			byte[] AmmeterAddrbyte = null;
			if (identityType == MeterInfo.IDENTITY_BY_NO) {
				if (AmmeterNo == null || AmmeterNo.length()<=0) {
					return dp.assFailTaskResultObject(taskObj, retObjList, null, "电表表号为空", true);
				}
				if(AmmeterNo.length() %2 !=0){
					AmmeterNo = "0" + AmmeterNo;
				}
				AmmeterAddrbyte = HexDump.hexStringToByteArray(AmmeterNo);
			} else {
				if(tmpTermObj.getMeterNo() != null){
					AmmeterAddrbyte = tmpTermObj.getMeterNo();
				}else{
					AmmeterAddrbyte = HexDump.hexStringToByteArray(AmmeterAddr);
				}
			}
			int len = AmmeterAddrbyte.length;
			byte[] fsyz = new byte[8];
			for (int j = 7; j >= 0; j--) {
				fsyz[j] = 0x00;
			}
			System.arraycopy(AmmeterAddrbyte, 0, fsyz, 8 - len, len);

			log.debug("分散因子:" + HexDump.toHexString(fsyz).toString());

			byte[] randomAndPwdBuf = new byte[16];
			// 调用加密机的接口取得16位随机数2及16位密文
			try{
				int ret = dp.getPwdFromPwdComputer("IdentityAuthentication", 1,
						fsyz, randomAndPwdBuf);
				if (ret < 0) {
					return dp.assFailTaskResultObject(taskObj, retObjList, null, "加密机调用失败", true);
				}
			}catch(Exception e){
				return dp.assFailTaskResultObject(taskObj, retObjList, null, "加密机调用异常", true);
			}
			byte[] radom1 = new byte[8];
			byte[] pwdReport = new byte[8];
			// 得到8位随机数1
			System.arraycopy(randomAndPwdBuf, 0, radom1, 0, 8);
			// 得到8位密文
			System.arraycopy(randomAndPwdBuf, 8, pwdReport, 0, 8);
			// 组装认证下发报文

			log.debug("##########随机数1：" + HexDump.toHexString(radom1).toString());
			log.debug("##########密文：" + HexDump.toHexString(pwdReport).toString());

			byte[] sendbuf = null;
			try {
				sendbuf = dp.assIdentityAuthenticationReport(AmmeterAddr,
						radom1, pwdReport, fsyz);
			} catch (Exception e) {
				return dp.assFailTaskResultObject(taskObj, retObjList, null, "组装身份认证报文异常", true);
			}

			if (sendbuf == null || sendbuf.length <=0) {
				return dp.assFailTaskResultObject(taskObj, retObjList, null, "组装身份认证报文失败", true);
			}

			// 调用规约插件进行组包
			ShareGlobalObj.getInstance().setTransBuf(
					taskObj.getTermTask().getTerminalAddr(), sendbuf);
			ArrayList<String> retStrList = asduConver
					.convertDataObjToBuff(taskObj);
			int frameIndexNo = 0;
			if (retStrList == null) {
				return dp.assFailTaskResultObject(taskObj, retObjList, null, "asdu数据转换出错", true);
			} else {
				if (retStrList.size() > 0) {
					byte tmpBuff[];
					try {
						sendBuff[frameIndexNo++] = 0x10;// 透传（AFN=10H）
						byte terminalPFC = tmpTermObj.getTerminalPFC();
						tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));
						tmpTermObj.setTerminalTpV(false);
						tmpTermObj.setTerminalFIR(true);
						tmpTermObj.setTerminalFIN(true);
						tmpTermObj.setTerminalCON(false);

						int seqNum = GxCommFunction
								.getSeqNumber(terminalAddr);// SEQ
						sendBuff[frameIndexNo++] = (byte) seqNum;

						tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
						for (int m = 0; m < tmpBuff.length; m++) {
							sendBuff[frameIndexNo++] = tmpBuff[m];
						}
						log.debug("发送报文"
								+ HexDump.toHexString(sendBuff, 0,
										tmpBuff.length).toString());

					} catch (UnsupportedEncodingException e) {
						return dp.assFailTaskResultObject(taskObj, retObjList, null, "不支持的字符编码转换", true);
					}
				}

				resultObj = new TaskResultObject();
				int seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
				resultObj.setFramePSEQ((short) (seqNum & 0x0f));
				resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
				resultObj.setResultValue(0);
				resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
				resultObj.setRetMsgLen(frameIndexNo);
				resultObj.setRetMsgBuf(sendBuff);
				retObjList = new ArrayList<TaskResultObject>();
				retObjList.add(resultObj);
				taskObj.setStep(GxTransParent.STEP_IDENTITYAUTHENTICATION);
				return retObjList;
			}
		}
		else if(step == GxTransParent.STEP_SET_PARAM){
			return new GxReceivePacket().buildControlOrSetReport(taskObj);
		}else if(step == GxTransParent.STEP_READ_METER){
			//return sendDataForward645Report(taskObj);
			List<MeterInfo> meterInfos = tmpTermObj.getMeterInfos();
			if (null==meterInfos||meterInfos.size() <= 0) {
				return sendDataForward645Report(taskObj);
			}

			MeterInfo meterInfo = meterInfos.get(0);
			if(meterInfo.getCmdType() ==0x0C || meterInfo.getCmdType() == 0x05){ //无需身份认证，直接转发645报文
	    		return sendDataForward645Report(taskObj);
	    	}else{//just for compatible, hope delete later
	    		if(meterInfo.getNote() != null && meterInfo.getNote().length() >=2 
	    				&& meterInfo.getNote().substring(0, 2).equals("0A")){
	    			return sendDataForwardReading645Data(taskObj);
	    		}
	    	}
		}else if(step == GxTransParent.STEP_CONTROL_BY_OPEN_KEY){
			
			List<MeterInfo> meterInfos = tmpTermObj.getMeterInfos();
			if (meterInfos.size() <= 0) {
				return dp.assFailTaskResultObject(taskObj, retObjList, null,
						"MeterInfos列表为空", true);
			}

			MeterInfo meterInfo = meterInfos.get(0);
			String AmmeterAddr = meterInfo.getMeterAddr();
			String controlType = meterInfo.getNote();
			int delay = meterInfo.getBakInt();
			byte[] sendbuf = null;
			try {
				sendbuf = dp.assControlMeterByOpenKey(AmmeterAddr, controlType,delay);
			} catch (Exception e) {
				return dp.assFailTaskResultObject(taskObj, retObjList, null,
						"组装身份认证报文异常", true);
			}

			if (sendbuf == null || sendbuf.length <= 0) {
				return dp.assFailTaskResultObject(taskObj, retObjList, null,
						"组装身份认证报文失败", true);
			}
			// 调用规约插件进行组包
			ShareGlobalObj.getInstance().setTransBuf(
					taskObj.getTermTask().getTerminalAddr(), sendbuf);
			ArrayList<String> retStrList = asduConver
					.convertDataObjToBuff(taskObj);
			int frameIndexNo = 0;
			if (retStrList == null) {
				return dp.assFailTaskResultObject(taskObj, retObjList, null,
						"asdu数据转换出错", true);
			} else {
				if (retStrList.size() > 0) {
					byte tmpBuff[];
					try {
						sendBuff[frameIndexNo++] = 0x10;// 透传（AFN=10H）
						byte terminalPFC = tmpTermObj.getTerminalPFC();
						tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));
						tmpTermObj.setTerminalTpV(false);
						tmpTermObj.setTerminalFIR(true);
						tmpTermObj.setTerminalFIN(true);
						tmpTermObj.setTerminalCON(false);

						int seqNum = GxCommFunction
								.getSeqNumber(terminalAddr);// SEQ
						sendBuff[frameIndexNo++] = (byte) seqNum;

						tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
						for (int m = 0; m < tmpBuff.length; m++) {
							sendBuff[frameIndexNo++] = tmpBuff[m];
						}
						log.debug("发送报文"
								+ HexDump.toHexString(sendBuff, 0,
										tmpBuff.length).toString());

					} catch (UnsupportedEncodingException e) {
						return dp.assFailTaskResultObject(taskObj, retObjList,
								null, "不支持的字符编码转换", true);
					}
				}

				resultObj = new TaskResultObject();
				int seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
				resultObj.setFramePSEQ((short) (seqNum & 0x0f));
				resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
				resultObj.setResultValue(0);
				resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
				resultObj.setRetMsgLen(frameIndexNo);
				resultObj.setRetMsgBuf(sendBuff);
				retObjList = new ArrayList<TaskResultObject>();
				retObjList.add(resultObj);
				return retObjList;
			}
		}
		else {
			log.error("error!! step=" + step);
		}

		return retObjList;
	}
	
	private ArrayList<TaskResultObject> returnFailResponse(
			TaskQueueObject taskObj, ErrorCode errorCode, String note) {
		ArrayList<TaskResultObject> retObjList = new ArrayList<TaskResultObject>();

		log.error("ERROR:" + note);
		Response response = new Response();
		response.setTaskId(taskObj.getTermTask().getTaskId());
		String termAddr = taskObj.getTermTask().getTerminalAddr();
		response.setTerminalAddr(termAddr);
		response.setErrorCode(errorCode);
		response.setTmnlAssetNo(taskObj.getTermTask().getTmnlAssetNo());
		response.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
		response.setNote(note);

		TaskResultObject resultObj = new TaskResultObject();
		resultObj.setCall(true);
		resultObj.setFirfin((byte) 1);
		resultObj.setResultParaObj(response);
		resultObj.setResultType(ConstDef.TASKRESULT_RESPONSECOMMAND);
		resultObj.setResultValue(0);
		resultObj.setRetMsgLen(0);
		resultObj.setRetMsgBuf(null);
		resultObj.setTaskID(taskObj.getTermTask().getTaskId());

		retObjList.add(resultObj);

		return retObjList;
	}
	
	/**
	 * 转发直接645规约报文操作
	 * @param taskObj
	 * @return
	 */
	public ArrayList<TaskResultObject> sendDataForward645Report (TaskQueueObject taskObj){
		ArrayList<TaskResultObject> retObjList = null;
		
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		TaskResultObject resultObj = null;
		byte[] sendBuff = new byte[1024];
		GxTransParent trans = new GxTransParent();
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		if (tmpTermObj == null) { 
			return trans.assFailTaskResultObject(taskObj, retObjList, null, "终端对象为空", true);
		}
		List<MeterInfo> meterInfos = tmpTermObj.getMeterInfos();
		if (meterInfos.size() <= 0) {
			return trans.assFailTaskResultObject(taskObj, retObjList, null, "MeterInfos列表为空", true);
		}
		MeterInfo meterInfo = meterInfos.get(0);
		String meterAddr = meterInfo.getMeterAddr();
		if (meterAddr == null) {
			return trans.assFailTaskResultObject(taskObj, retObjList, null, "电表地址为空", true);
		}

		if(meterInfo.getCmdType() >0){
			DataForward645Report report = new DataForward645Report();
			report.setCmdType(meterInfo.getCmdType());
    		report.setDataMark(meterInfo.getNote());
    		report.setMeterAddr(meterInfo.getMeterAddr());
    		report.setMeterNo(meterInfo.getBakString());
    		report.setCollector(meterInfo.getBelongCollector());
    		report.setPassword(meterInfo.getBakString());
    		report.setTimeDelay(meterInfo.getTimeDelay());
			byte[] buffer = null;
			if(report.getCollector() == null){
				buffer = report.buildReport(null);
			}else{
				buffer = report.buildCollectorReport(null);
			}
			log.debug(report.toString());
			if(buffer == null){
				return trans.assFailTaskResultObject(taskObj, retObjList, null, "组装读数据长度零", true);
			}else{
				ShareGlobalObj.getInstance().setTransBuf(
						taskObj.getTermTask().getTerminalAddr(), buffer);
				ArrayList<String> retStrList = asduConver.convertDataObjToBuff(taskObj);
				int frameIndexNo = 0;
				if (retStrList == null) {
					return trans.assFailTaskResultObject(taskObj, retObjList, null, "asdu数据转换失败", true);
				} else {
					if (retStrList.size() > 0) {
						byte tmpBuff[];
						try {
							sendBuff[frameIndexNo++] = 0x10;// 透传（AFN=10H）
							byte terminalPFC = tmpTermObj.getTerminalPFC();
							tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));
							tmpTermObj.setTerminalTpV(false);
							tmpTermObj.setTerminalFIR(true);
							tmpTermObj.setTerminalFIN(true);
							tmpTermObj.setTerminalCON(false);
							
							int seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
							sendBuff[frameIndexNo++] = (byte) seqNum;
							
							tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
							for (int m = 0; m < tmpBuff.length; m++) {
								sendBuff[frameIndexNo++] = tmpBuff[m];
							}
						} catch (UnsupportedEncodingException e) {
							return trans.assFailTaskResultObject(taskObj, retObjList, null, "不支持的字符编码转换", true);
						}
					}

					resultObj = new TaskResultObject();
					int seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
					resultObj.setFramePSEQ((short) (seqNum & 0x0f));
//					if(meterInfo.getCmdType() == 0x05 && meterInfo.getNote() == null){
//						//控制---广播校时
//						resultObj.setResultType(ConstDef.TASKRESULT_FINISHTASK);
//						tmpTermObj.setMeterInfos(null);
//					}else{
//						resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
//					}
					resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
					
					resultObj.setResultValue(0);
					resultObj.setTaskProtoType(taskObj.getTermTask()
							.getProtocol());
					resultObj.setRetMsgLen(frameIndexNo);
					resultObj.setRetMsgBuf(sendBuff);
					retObjList = new ArrayList<TaskResultObject>();
					retObjList.add(resultObj);
					taskObj.setStep(GxTransParent.STEP_READ_METER);
					if(meterInfo.getCmdType() == 0x05 && meterInfo.getNote() == null){
						//控制---广播校时后直接返回成功response
						tmpTermObj.setMeterInfos(null);
						
						Response res = new Response();
						res.setTaskId(taskObj.getTermTask().getTaskId());
						res.setTerminalAddr(taskObj.getTermTask().getTerminalAddr());
						res.setTmnlAssetNo(taskObj.getTermTask().getTmnlAssetNo());
						res.setDbDatas(null);
						res.setContinue(false);
						res.setErrorCode(ErrorCode.OK);
						res.setFuncCode((short) (taskObj.getTermTask().getFuncCode()));
						res.setNote("校时成功");
						res.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
						
						ResponseItem item = new ResponseItem();
						item.setErrorCode((short)ErrorCode.OK.getId());
						item.setFn((short)1);
						item.setPn((short)0);
						item.setStatus((short)1);
						item.setValue(null);
						item.setCode("校时成功");
						List<ResponseItem> itemList = new ArrayList<ResponseItem>();
						itemList.add(item);
						res.setResponseItems(itemList);
						
						TaskResultObject resultObj1 = new TaskResultObject();
						resultObj1.setResultParaObj(res);
						resultObj1.setResultType(ConstDef.TASKRESULT_RESPONSECOMMAND);
						resultObj1.setResultValue(0);
						resultObj1.setRetMsgLen(0);
						resultObj1.setRetMsgBuf(null);
						resultObj1.setTaskID(taskObj.getTermTask().getTaskId());
						resultObj1.setCall(true);
						resultObj1.setFirfin((byte)0xBB);
						retObjList.add(resultObj1);
					}
				}
				return retObjList;
			}
		}else{
			return sendDataForwardReading645Data(taskObj);
		}
	}

	/**
	 * 转发读645电表数据
	 * @param taskObj
	 * @return
	 */
	public ArrayList<TaskResultObject> sendDataForwardReading645Data(TaskQueueObject taskObj){

		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;
		byte[] sendBuff = new byte[1024];
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		GxTransParent trans = new GxTransParent();
		if (tmpTermObj == null) {
			return trans.assFailTaskResultObject(taskObj, retObjList, null, "终端对象空", true);
		}
		
		List<MeterInfo> meterInfos = tmpTermObj.getMeterInfos();
		if (meterInfos.size() <= 0) {
			return trans.assFailTaskResultObject(taskObj, retObjList, null, "MeterInfo列表空", true);
		}
		String meterAddr = meterInfos.get(0).getMeterAddr();
		if (meterAddr == null) {
			return trans.assFailTaskResultObject(taskObj, retObjList, null, "电表地址空", true);
		}
		
		String note = meterInfos.get(0).getNote();
		String [] tmp = note.split(";");
		if(tmp.length<2 || !tmp[0].equals("0A")){
			log.error("note格式错误:" +note);
			return trans.assFailTaskResultObject(taskObj, retObjList, null, "下发note格式错误", true);
		}
		//组装645报文
		byte[] sendbuf = null;
		try {
			sendbuf = new GxTransParent().ass645ReadingDataReport(meterAddr,tmp[1]);
		} catch (Exception e) {
			return trans.assFailTaskResultObject(taskObj, retObjList, null, "组装读数据报文异常", true);
		}

		if (sendbuf == null || sendbuf.length <=0) {
			return trans.assFailTaskResultObject(taskObj, retObjList, null, "组装读数据报文长度零", true);
		}
		
		ShareGlobalObj.getInstance().setTransBuf(
				taskObj.getTermTask().getTerminalAddr(), sendbuf);
		ArrayList<String> retStrList = asduConver.convertDataObjToBuff(taskObj);
		int frameIndexNo = 0;
		if (retStrList == null) {
			return trans.assFailTaskResultObject(taskObj, retObjList, null, "asdu数据转换失败", true);
		} else {
			if (retStrList.size() > 0) {
				byte tmpBuff[];
				try {
					sendBuff[frameIndexNo++] = 0x10;// 透传（AFN=10H）
					byte terminalPFC = tmpTermObj.getTerminalPFC();
					tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));
					tmpTermObj.setTerminalTpV(false);
					tmpTermObj.setTerminalFIR(true);
					tmpTermObj.setTerminalFIN(true);
					tmpTermObj.setTerminalCON(false);
					
					int seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
					sendBuff[frameIndexNo++] = (byte) seqNum;
					
					tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
					for (int m = 0; m < tmpBuff.length; m++) {
						sendBuff[frameIndexNo++] = tmpBuff[m];
					}
				} catch (UnsupportedEncodingException e) {
					return trans.assFailTaskResultObject(taskObj, retObjList, null, "不支持的字符格式转换", true);
				}
			}

			resultObj = new TaskResultObject();
			int seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
			resultObj.setFramePSEQ((short) (seqNum & 0x0f));
			resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
			resultObj.setResultValue(0);
			resultObj.setTaskProtoType(taskObj.getTermTask()
					.getProtocol());
			resultObj.setRetMsgLen(frameIndexNo);
			resultObj.setRetMsgBuf(sendBuff);
			retObjList = new ArrayList<TaskResultObject>();
			retObjList.add(resultObj);
		}
		taskObj.setStep(GxTransParent.STEP_READ_METER);
		return retObjList;

	}
	
	
}
