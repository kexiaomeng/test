package Protocol;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arms.protocols.CommMethods;
import arms.protocols.Verifier;

import com.nari.ami.database.map.runcontrol.RCpCommPara;
import com.nari.commObjectPara.CommTime;
import com.nari.commObjectPara.ConstDef;
import com.nari.commObjectPara.TaskQueueObject;
import com.nari.commObjectPara.TaskResultObject;
import com.nari.commObjectPara.TerminalObject;
import com.nari.fe.commdefine.define.FrontConstant;
import com.nari.fe.commdefine.param.ErrorCode;
import com.nari.fe.commdefine.task.Response;
import com.nari.global.ShareGlobalObj;
import com.nari.protocol.Fk05Protocol.Fk05Functions;
import com.nari.protocolBase.ProtocolBaseClass;

public class GxProtocol extends ProtocolBaseClass {
	private static Logger log = LoggerFactory.getLogger(GxProtocol.class);
	private ShareGlobalObj shareGlobalObjVar = ShareGlobalObj.getInstance();
	private GxSendPacket sendPacket = new GxSendPacket();
	private GxReceivePacket recvPacket = new GxReceivePacket();
	private GxSendClassData sendClassData = new GxSendClassData();
	
	private boolean needEvent = false ;


	public GxProtocol() {
		needEvent = ShareGlobalObj.getInstance().getFrontProtocolPara("5").needEvent ;
	}

	// 规约插件初始化
	@Override
	public void initial() {
		log.debug("---广西负控、配变、集中器规约初始化--");
		
	}

	// 规约插件退出处理
	@Override
	public void quit() {
		shareGlobalObjVar.writeLog(ConstDef.FRONTDOWN_LOG, "---广西负控、配变、集中器规约退出处理--", null);
	}

	public ArrayList<TaskResultObject> timeOutProcess(TaskQueueObject taskObj) {
		log.debug("--DL04Protocol 超时处理,任务ID:" + taskObj.getTermTask().getTaskId());
		ArrayList<TaskResultObject> retObjList = null;
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		TerminalObject to = shareGlobalObjVar.getTerminalPara(terminalAddr);
		if (to == null) {
			log.debug("TerminalObject=null,terminalAddr=" + terminalAddr);
			return null;
		}
		

		Response resPara = new Response();
		resPara.setTaskId(taskObj.getTermTask().getTaskId());
		resPara.setTerminalAddr(terminalAddr);
		resPara.setFuncCode((short) (taskObj.getTermTask().getFuncCode()));
		resPara.setTmnlAssetNo(taskObj.getTermTask().getTmnlAssetNo());
		resPara.setTaskStatus(FrontConstant.TASK_STATUS_TIMEOUT);
		resPara.setErrorCode(ErrorCode.TimeOut);
		resPara.setNote("超时处理,重发，请继续等待返回");
		resPara.setContinue(false);
		resPara.setWaitCounter(0);
		TaskResultObject resultObj = new TaskResultObject();
		resultObj.setResultParaObj(resPara);
		resultObj.setResultType(ConstDef.TASKRESULT_RESPONSECOMMAND);
		resultObj.setResultValue(0);
		resultObj.setRetMsgLen(0);
		resultObj.setRetMsgBuf(null);
		resultObj.setTaskID(taskObj.getTermTask().getTaskId());
		
		if (retObjList == null)
			retObjList = new ArrayList<TaskResultObject>();
		retObjList.add(resultObj);
		
		to.getTimeOutNum().incrementAndGet();
		return retObjList;
     }

	// 任务命令解析、处理
	@Override
	public ArrayList<TaskResultObject> taskInterpret(TaskQueueObject taskObj) {
		ArrayList<TaskResultObject> retObjList = null;
		try {
			if (taskObj == null)
				return retObjList;

			TaskResultObject taskResultObject = null;
			// 根据内部规约解析该任务内容，并作出相应处理；
			if (taskObj.getTaskSrcType() == 0)// 任务类型 0--应用下行命令，1--网关上行报文
			{
				retObjList = downTaskInterpret(taskObj);
				if (retObjList != null) {

					for (int k = 0; k < retObjList.size(); k++) {
						taskResultObject = retObjList.get(k);
						taskResultObject.setTaskType(taskObj.getTermTask().getTaskType());

						if (taskResultObject.getResultType() == ConstDef.TASKRESULT_SENDCOMMAND) {
							taskObj.setFramePSEQ((byte)taskResultObject.getFramePSEQ());
							taskObj.setStartTime(System.currentTimeMillis());
						}
					}
				}
			} else if (taskObj.getTaskSrcType() == 1)// 任务类型 0--应用下行命令，1--网关上行报文
			{
				retObjList = packetInterpret(taskObj);
				if (retObjList != null) {
					for (int k = 0; k < retObjList.size(); k++) {
						taskResultObject = retObjList.get(k);
						taskResultObject.setTaskType(taskObj.getTermTask().getTaskType());

						if (taskResultObject.getResultType() == ConstDef.TASKRESULT_SENDCOMMAND) {
							taskObj.setFramePSEQ((byte)taskResultObject.getFramePSEQ());
						}
					}
				}
			}
			return retObjList;
		} catch (Exception e) {
			log.error("",e);
		}
		return null;
	}

	// 下行任务命令解析、处理
	@Override
	public ArrayList<TaskResultObject> downTaskInterpret(TaskQueueObject taskObj) {
		ArrayList<TaskResultObject> retObjList = null;
		byte afnNo = (byte) (taskObj.getTermTask().getFuncCode());
		taskObj.setTaskAfnType(afnNo);

		log.debug(taskObj.getTermTask().getTerminalAddr(), "--广西规约任务命令解析、处理-----TaskID="
				+ taskObj.getTermTask().getTaskId() + "--AFN=" + afnNo, null);

		switch (taskObj.getTaskAfnType()) {
		case 0x00:// 确认∕否认（AFN=00H）
			break;
		case 0x01:// 复位命令（AFN=01H)
			 retObjList = sendPacket.sendResetOrder(taskObj);
			break;
		case 0x02:// 链路接口检测（AFN=02H）
			break;
		case 0x03:// 中继站命令（AFN=03H）
			break;
		case 0x04:// 设置参数（AFN=04H）命令下行报文
			retObjList = sendPacket.sendSetParaOrder(taskObj);
			break;
		case 0x05:// 控制命令（AFN=05H）命令下行报文
			retObjList = sendPacket.sendControlOrder(taskObj);
			break;
		case 0x06:// 身份认证及密钥协商（AFN=06H）命令下行报文
			retObjList = sendPacket.sendEncryptionOrder(taskObj);
			break;
		case 0x07:// reserved
			break;
		case 0x08:// 请求被级联终端主动上报（AFN=08H）
			retObjList = sendPacket.sendRequestCascadedTerminalOrder(taskObj);
			break;
		case 0x09:// 请求终端配置（AFN=09H）
			retObjList = sendPacket.sendRequestTerminalConfigurationOrder(taskObj);
			break;
		case 0x0A:// 查询参数（AFN=0AH）
			retObjList = sendPacket.sendQueryParametersOrder(taskObj);
			break;
		case 0x0B:// 请求任务数据（AFN=0BH）
			// 上行报文根据请求的定时上报任务的数据类别，分别用请求1类数据和请求2类数据的上行报文进行应答。
			retObjList = sendPacket.sendRequestTaskDataOrder(taskObj);
			break;
		case 0x0C:// 请求1类数据（AFN=0CH）
			retObjList = sendClassData.sendRequestOneClassDataOrder(taskObj);
			break;
		case 0x0D:// 请求2类数据（AFN=0DH）
			retObjList = sendClassData.sendRequestTwoClassDataOrder(taskObj);
			break;
		case 0x0E:// 请求3类数据（AFN=0EH）
			retObjList = sendClassData.sendRequestThreeClassDataOrder(taskObj, 0);
			break;
		case 0x0F:// 文件传输（AFN=0FH）
			retObjList = sendPacket.sendFileTransferOrder(taskObj);
			break;
		case 0x10:// 数据转发（AFN=10H）
			retObjList = sendPacket.sendDataForwardingOrder(taskObj);
			break;
		default:
			break;
		}
		return retObjList;
	}

	// 网关上送报文解析、处理
	@Override
	public ArrayList<TaskResultObject> packetInterpret(TaskQueueObject taskObj) {
		ArrayList<TaskResultObject> retObjList = null;
		// 根据网关与前置通讯协议分析链路状态，提取终端应用报文，并根据终端规约进行解析、处理；
		if (taskObj.getTaskMessage() == null)
			return retObjList;
		byte packetBuff[] = null;
		try {
			packetBuff = taskObj.getTaskMessage();
			if (packetBuff == null || packetBuff.length < 2) {
				log.debug("-- Error:: 网关上送报文错误！" + "任务ID:" + taskObj.getTermTask().getTaskId());
				return retObjList;
			}

		} catch (Exception ex) {
			log.error("网关上送报文解析错误:" + "任务ID:" + taskObj.getTermTask().getTaskId(), ex);
		}

		int frameLength = taskObj.getBuflen();
		// 检查帧是否完整
		int len = packetBuff[1] & 0xff;
		len += (packetBuff[2] & 0xff) << 8;
		len = (len - 1) / 4;
		if (frameLength < (len + 8))// 长度是否够
		{
			log.error("-- Error:: 网关上送报文帧长度不够！" + "任务ID:" + taskObj.getTermTask().getTaskId());
			return null;
		}
		int rseq = packetBuff[13] & 0x0f;
		String terminalStr = taskObj.getTermTask().getTerminalAddr();
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalStr);
		if (tmpTermObj != null && tmpTermObj.isMasterCeshi()) {
			log.debug("--Dl04Protocol 保存主站透传报文！");
			byte[] recBuf = taskObj.getTaskMessage();
			int reclen = taskObj.getBuflen();

			byte[] newbuf = new byte[reclen];
			System.arraycopy(recBuf, 0, newbuf, 0, reclen);
			shareGlobalObjVar.setMasterCeshi(tmpTermObj.getMasterCeshiIp(), newbuf);
		}

		// 设置网关上送报文任务的关联命令任务ID，根据preq与终端地址确定
		long tmpTaskId = -1;
		boolean isCall = false;
		if ((packetBuff[6] & 0xc0) == 0xc0) {// 终端主动上送
			taskObj.getTermTask().setTaskId(tmpTaskId);
			if (tmpTermObj != null) {
				taskObj.getTermTask().setTerminalAddr(terminalStr);
				taskObj.getTermTask().setTmnlAssetNo(tmpTermObj.getTmnlAssetNo());
			}

		} else {// 主站请求！！
			isCall = true;
		}
		
		ConcurrentLinkedQueue<TaskQueueObject> queue = shareGlobalObjVar
		.getTmnlTaskQueue(taskObj.getTermTask().getTerminalAddr());
		TaskQueueObject o = queue.peek();
		if(o == null){
			isCall = false;
		}
		
		log.debug("-- 网关上送报文解析 AfnNo=" + (packetBuff[12] & 0xff) + "taskid: "
				+ taskObj.getTermTask().getTaskId() + "frameLength:" + frameLength
				+ "packetBuff len:" + packetBuff.length);
		switch (packetBuff[12]) {
		case 0x00:// 确认∕否认（AFN=00H）
			retObjList = recvPacket.receiveConfirmData(taskObj, packetBuff);
			break;
		case 0x01:// 复位命令（AFN=01H)
			break;
		case 0x02:// 链路接口检测（AFN=02H）
			// 链路接口检测命令下行报文为确认/否认报文中的F3按数据单元标识确认和否认
			retObjList = recvPacket.receiveLinkInterfaceDetectionCommand(taskObj, packetBuff);
			break;
		case 0x03:// 中继站命令（AFN=03H）
			retObjList = recvPacket.receiveRelayStationCommand(taskObj, packetBuff);
			break;
		case 0x04:// 设置参数（AFN=04H）
			// 回答确认/否认报文
			break;
		case 0x05:// 控制命令（AFN=05H）
			// 回答确认/否认报文
			break;
		case 0x06:// 身份认证及密钥协商（AFN=06H）
			break;
		case 0x07:// reserved
			break;
		case 0x08:// 请求被级联终端主动上报（AFN=08H）
			retObjList = recvPacket.receiveCascadedTerminalData(taskObj, packetBuff);
			break;
		case 0x09:// 请求终端配置（AFN=09H）
			retObjList = recvPacket.receiveTerminalConfigurationData(taskObj, packetBuff);
			break;
		case 0x0A:// 查询参数（AFN=0AH）
			retObjList = recvPacket.receiveQueryparametersData(taskObj, packetBuff);
			break;
		case 0x0B:// 请求任务数据（AFN=0BH）
			// 上行报文根据请求的定时上报任务的数据类别，分别用请求1类数据和请求2类数据的上行报文进行应答。
			break;
		case 0x0C:// 请求1类数据（AFN=0CH）		
		case (byte)0xFE://zhouyu add for 新疆精正达
			retObjList = recvPacket.receiveAClassData(taskObj, packetBuff);
			break;
		case 0x0D:// 请求2类数据（AFN=0DH）
			retObjList = recvPacket.receiveBClassData(taskObj, packetBuff);
			break;
		case 0x0E:// 请求3类数据（AFN=0EH）
			retObjList = recvPacket.receiveCClassData(taskObj, packetBuff);
			break;
		case 0x0F:// 文件传输（AFN=0FH）
			break;
		case 0x10:// 数据转发（AFN=10H）
			retObjList = recvPacket.receiveDataForwardingData(taskObj, packetBuff);
			break;
		default:
			break;
		}
		byte fi = (byte)((packetBuff[13]>>5) & 0x03); //RandySuh@xining 2010.07.22
		if (retObjList != null && retObjList.size() > 0) {
			for(TaskResultObject ro : retObjList){
				if (ro.getResultType() == ConstDef.TASKRESULT_RESPONSECOMMAND && 
					ro.getResultParaObj() instanceof Response) {
					ro.setCall(isCall);
					ro.setFramePSEQ((short)rseq);
					
					//wj add at anhui2011/5/8 for 230通道，每次收到的帧当作结束帧
					if (taskObj.getTermTask().getCollMode().equalsIgnoreCase(FrontConstant.COLL_MODE_230M)){
					   ro.setFirfin((byte)0x01);
					}
					else{
					   ro.setFirfin(fi);
					}
				}
			}
		}
		if (!((fi & 0x01)==1)){
			log.debug("FIN=0,有后继帧---------"+"终端地址：" + taskObj.getTermTask().getTerminalAddr() + "终端资产号：" + taskObj.getTermTask().getTmnlAssetNo());
		}else{
			log.debug("FIN=1,结束帧清除任务---------"+"终端地址：" + taskObj.getTermTask().getTerminalAddr() + "终端资产号：" + taskObj.getTermTask().getTmnlAssetNo());
		}
		byte controlByte = packetBuff[6];// 控制字
		if ((controlByte & 0x20) == 0x20 && needEvent)// ACD=1,自上次收到报文后发生新的重要事件
		{
			byte EC1, EC2;
			ArrayList<TaskResultObject> tmpRetObjList = null;
			if (tmpTermObj != null) {
				if ((packetBuff[13] & 0x80) == 0x80) {// 有Tpv
					EC1 = packetBuff[len - 2];// 重要事件计数器EC1
					EC2 = packetBuff[len - 1];// 一般事件计数器EC2
				} else {
					EC1 = packetBuff[len + 4];
					EC2 = packetBuff[len + 5];
				}
				tmpTermObj.setTerminalReadEC1(EC1);
				tmpTermObj.setTerminalReadEC2(EC2);
				// 启动新的请求
				if(taskObj.getTermTask().getFuncCode() == 0x0C ||  taskObj.getTermTask().getFuncCode() == 0x0D){//除了1，2类事件以外的事件则不召测
					
					if(!(tmpTermObj.getCollMode().equals(FrontConstant.COLL_MODE_230M))){
						if(!o.isSplitMulti() || (o.isSplitMulti() && !o.hasNextSection())){
							shareGlobalObjVar.resetTimeOutTime(taskObj.getTermTask());// 重新设置超时时间
							tmpRetObjList = sendClassData.sendRequestThreeClassDataOrder(taskObj, 1);
							tmpTermObj.setTerminalEC1((byte) (((tmpTermObj.getTerminalEC1()& 0xff) + 1)%256));
						}
					}
				}
				if (tmpRetObjList != null) {
					if (retObjList == null)
						retObjList = new ArrayList<TaskResultObject>();
					retObjList.add(tmpRetObjList.get(0));
				}
			}
		}
		return retObjList;
	}
	
	 private int getFn(byte DT1,byte DT2){
		 	int v =DT1&0XFF;
	        int val =0;
	        while(v != 0){
	            v >>= 1;
	            val++;
	        }
	        return val + ((DT2 & 0xFF)*8);
	 }

	// 封装应用报文的链路数据与报文尾
	@Override
	public void linkDataPackage(String terminalAddr, TaskResultObject resultObj) {
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		int frameIndexNo = 0;
		if (resultObj.getRetMsgLen() <= 0)
			return;
		byte afnNo = resultObj.getRetMsgBuf()[0];
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		if (tmpTermObj == null) {
			log.error("封装应用报文的链路数据与报文尾,TerminalObject==null!");
			return;
		}
		tmpTermObj.setTerminalFCV(false);

		sendBuff[frameIndexNo++] = 0x68;
		sendBuff[frameIndexNo++] = 0x00;
		sendBuff[frameIndexNo++] = 0x00;
		sendBuff[frameIndexNo++] = 0x00;
		sendBuff[frameIndexNo++] = 0x00;
		sendBuff[frameIndexNo++] = 0x68;
		sendBuff[frameIndexNo++] = 0x00;

		int tempaddr1 = Integer.parseInt(terminalAddr.substring(2, 4), 16);
		int tempaddr2 = Integer.parseInt(terminalAddr.substring(0, 2), 16);
		int tmpAddr2 = Integer.parseInt(terminalAddr.substring(4, 8), 16);

		sendBuff[frameIndexNo++] = (byte) tempaddr1;// 地址域A
		sendBuff[frameIndexNo++] = (byte) tempaddr2;// 
		sendBuff[frameIndexNo++] = (byte) (tmpAddr2 % 256);//
		sendBuff[frameIndexNo++] = (byte) (tmpAddr2 >> 8);//  
		ConcurrentLinkedQueue<TaskQueueObject> queue = shareGlobalObjVar
		.getTmnlTaskQueue(terminalAddr);
		TaskQueueObject o = queue.peek();
		if(o == null || o.getTermTask().getTaskId() == -1){
			//如果由终端启动的报文，则MASTER_R为0
			sendBuff[frameIndexNo++] = 0x00;
		}else{
			sendBuff[frameIndexNo++] = (byte) (shareGlobalObjVar.getMasterNo() << 1);//
		}

		for (int k = 0; k < resultObj.getRetMsgLen(); k++) {
			sendBuff[frameIndexNo++] = resultObj.getRetMsgBuf()[k];
		}

		switch (afnNo) {
		// -----------------------以下含有Tp-----------------------------------
		case 0x0C:// 请求1类数据（AFN=0CH）
			sendBuff[6] = GxCommFunction.getControlField(terminalAddr, true,
					ConstDef.COMMAND_GRADE2DATA);// 控制域C
			break;
		case 0x0D:// 请求2类数据（AFN=0DH）
			sendBuff[6] = GxCommFunction.getControlField(terminalAddr, true,
					ConstDef.COMMAND_GRADE2DATA);
			break;
		case 0x0A:// 查询参数（AFN=0AH）
		case 0x0B:// 请求任务数据（AFN=0BH）
		case 0x0E:// 请求3类数据（AFN=0EH）
		case 0x09:// 请求终端配置（AFN=09H）
		case 0x00:// 确认∕否认（AFN=00H）
			if (afnNo == 0) {
				sendBuff[11] = 0x00; ////modify by wyx 2012-11-06 				
				if (tmpTermObj.isTerminalPrm()) {// prm=true为主动上送
					sendBuff[6] = GxCommFunction
							.getControlField(terminalAddr, false, (byte) 0x00);// 控制域C
				} else {// prm=false为召测返回的报文
					sendBuff[6] = GxCommFunction.getControlField(terminalAddr, true,
							ConstDef.COMMAND_USERDATA);
				}
			} else {
				sendBuff[6] = GxCommFunction.getControlField(terminalAddr, true,
						ConstDef.COMMAND_GRADE2DATA);
			}
			break;
		// -----------------------以下含有Tp与PW---------------------------
		case 0x10:// 数据转发（AFN=10H）
		case 0x0F:// 文件传输（AFN=0FH）
		case 0x06:// 身份认证及密钥协商（AFN=06H）
		case 0x05:// 控制命令（AFN=05H）
		case 0x04:// 设置参数（AFN=04H）
		case 0x01:// 复位命令（AFN=01H)
			if(afnNo == 0x01){
				tmpTermObj.setTerminalFCV(false);
				tmpTermObj.setTerminalFCB(false);
				sendBuff[6] = GxCommFunction.getControlField(terminalAddr, true,
						ConstDef.COMMAND_RESET);
			}else
				sendBuff[6] = GxCommFunction.getControlField(terminalAddr, true,
						ConstDef.COMMAND_GRADE1DATA);
			byte[] framebyte=new byte[12] ;
			int pos = 0;
			for(int i=0;i<12 ;i++){
				framebyte[i] = sendBuff[6+pos];
				pos ++ ;
			}
			RCpCommPara commParam =shareGlobalObjVar.getRcpMap().get(tmpTermObj.getSrcTerminalAddr());
			
			boolean bNeedKey = true;
			String algNo = "";
			String algKey = "";
			if(commParam != null){
				Fk05Functions fkFunc = new Fk05Functions();
				algNo = fkFunc.replaceBlank(commParam.getAlgNo());
				algKey = fkFunc.replaceBlank(commParam.getAlgKey());
				if(algNo == null || algKey == null || algNo.isEmpty() || algKey.isEmpty()){
					bNeedKey = false;
				}
			}else{
				bNeedKey = false;
			}

			
			int fn = getFn(resultObj.getRetMsgBuf()[4] ,resultObj.getRetMsgBuf()[5]);
			if(shareGlobalObjVar.getSysProvince().equals("3")){
				
				byte[] crcpw = new byte[]{0,0};
				//华北现场
				if(tmpTermObj.getCommPwd() != null
						&& !tmpTermObj.getCommPwd().equals("")
						&& !tmpTermObj.getCommPwd().equalsIgnoreCase("null")){
					crcpw = Verifier.P_CrcCheck(framebyte, Integer.parseInt(tmpTermObj.getCommPwd())) ;
				}
				for(int i = 0 ; i < crcpw.length; i++){
					sendBuff[frameIndexNo++] = crcpw[i];
				}
			}else	if(tmpTermObj.getTerminalModel().equals("1463")){ //上海协同
				byte[] crcpw = Verifier.P_CrcCheck(framebyte, 100) ;
				if(crcpw != null){
					sendBuff[frameIndexNo++] = crcpw[0];
					sendBuff[frameIndexNo++] = crcpw[1];
				}else{
					sendBuff[frameIndexNo++] = 0x00;
					sendBuff[frameIndexNo++] = 0x00;
				}
			}else if(fn ==38 && afnNo ==0x05){				
				tmpTermObj.setTerminalTpV(true);
				for(int i=0;i<16;i++){
					sendBuff[frameIndexNo++] = 0x00;
				}				
			}else if(commParam!=null && bNeedKey){
				byte[] alg= new byte[]{0,0};
		    	switch(Integer.parseInt(algNo)){
			    	case 0:{
			    		alg= CommMethods.ReorderRawFrame(
			    				CommMethods.getBytesfromString(String.format("%04X", Long.parseLong(algKey)))
			    			);
			    	}break;
			    	case 1:{
			    		alg= Verifier.P_CrcCheck(framebyte, Integer.parseInt(algKey));
			    	}break;			
		    	}
		    	for(int i=0;i<2;i++){
					sendBuff[frameIndexNo++] = alg[i];
				}
			}else{
				for(int i=0;i<2;i++){
					sendBuff[frameIndexNo++] = 0x00;
				}
			}
			break;
		// -----------------------以下不含Tp与PW---------------------------
		case 0x02:// 链路接口检测（AFN=02H）
			tmpTermObj.setTerminalTpV(false);
			sendBuff[6] = GxCommFunction.getControlField(terminalAddr, true,
					ConstDef.COMMAND_LINKTEST);// 控制域C
			break;
		case 0x03:// 中继站命令（AFN=03H）
			tmpTermObj.setTerminalTpV(false);
			sendBuff[6] = GxCommFunction.getControlField(terminalAddr, true,
					ConstDef.COMMAND_USERDATA);// 控制域C
			break;
		case 0x08:// 请求被级联终端主动上报（AFN=08H）
			tmpTermObj.setTerminalTpV(false);
			sendBuff[6] = GxCommFunction.getControlField(terminalAddr, true,
					ConstDef.COMMAND_USERDATA);// 控制域C
			break;
		default:
			break;
		}

		if (tmpTermObj.isTerminalTpV()) {
			sendBuff[frameIndexNo++] = tmpTermObj.getTerminalPFC();// 启动帧帧序号计数器PFC
			sendBuff[frameIndexNo++] = ConvertByteToDataFormatType((byte) (CommTime.getTimeSecond() % 60));// 秒,BCD码十位,BCD码个位
			sendBuff[frameIndexNo++] = ConvertByteToDataFormatType((byte) (CommTime.getTimeMinute() % 60));// 分
			sendBuff[frameIndexNo++] = ConvertByteToDataFormatType((byte) (CommTime.getTimeHour() % 24));// 时
			sendBuff[frameIndexNo++] = ConvertByteToDataFormatType((byte) (CommTime.getTimeDate() % 31));// 日
			sendBuff[frameIndexNo++] = 0x00;// 允许发送传输延时时间,min
		}

		int dlen = ((frameIndexNo - 6) << 2) | 0x01;// 和698不同
		sendBuff[1] = (byte) (dlen & 0xff);// l*4+1
		sendBuff[2] = (byte) (dlen >> 8);
		sendBuff[3] = (byte) (dlen & 0xff);// l*4+1
		sendBuff[4] = (byte) (dlen >> 8);

		int tmpLength = frameIndexNo - 1;
		sendBuff[frameIndexNo++] = 0x00;
		sendBuff[tmpLength + 1] = 0;
		for (int k = 6; k <= tmpLength; k++) {
			sendBuff[tmpLength + 1] += sendBuff[k]; // CS
		}
		sendBuff[frameIndexNo++] = 0x16;

		appendGateWayHead(tmpTermObj, sendBuff, frameIndexNo, resultObj);
	}

	private byte ConvertByteToDataFormatType(byte val) {
		if (val < 0 || val > 99)
			return (byte) 0xFF;
		return (byte) (((val / 10) << 4 | (val % 10)) & 0xFF);
	}

	// 封装网关报文协议头
	public void appendGateWayHead(TerminalObject tmpTermObj, byte buff[], int buffLen,
			TaskResultObject resultObj) {
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		int frameIndexNo = 0;
		int tmpAddr1 = 0;
		int bufLength = buffLen + 8;

		sendBuff[frameIndexNo++] = 0x69;
		sendBuff[frameIndexNo++] = (byte) (bufLength % 256);
		sendBuff[frameIndexNo++] = (byte) (bufLength >> 8);
		sendBuff[frameIndexNo++] = 0x69;

		byte collMode = shareGlobalObjVar.getGateCommType(tmpTermObj.getCollMode());
		byte protocolByte = 0;
		sendBuff[frameIndexNo++] = collMode;
		try {
			protocolByte = Byte.parseByte(resultObj.getTaskProtoType(), 16);
			sendBuff[frameIndexNo++] = (byte) protocolByte;
			if (collMode == 0x02 || collMode == 0x04 || collMode == 0x05 ||collMode == 0x08/*短信激活 added by joehan@xj 2010-12-07*/) {
				// 网关数据长度
				sendBuff[1] = (byte) ((bufLength + 38) % 256);
				sendBuff[2] = (byte) ((bufLength + 38) >> 8);
				String ip = tmpTermObj.getCommIpAddress() == null ? "0.0.0.0" : tmpTermObj
						.getCommIpAddress();

				String[] ipseg = ip.split("\\.");
				if (ipseg == null || ipseg.length != 4) {
					log.error("终端TerminalObject:" + tmpTermObj.getTmnlAssetNo() + " IP地址有误:"
							+ tmpTermObj.getCommIpAddress());
					ipseg = new String[] { "0", "0", "0", "0" };
				}
				for (int i = 0; i < 4; i++) {
					sendBuff[frameIndexNo++] = (byte) Short.parseShort(ipseg[i]);
				}
				int port = tmpTermObj.getPort();
				sendBuff[frameIndexNo++] = (byte) (port & 0xFF);
				sendBuff[frameIndexNo++] = (byte) ((port >> 8) & 0xFF);
				// AT命令 （32个字节ASCII）
				String at = tmpTermObj.getAtCommand();
				byte[] atCmd;
				if (at == null) {
					atCmd = new byte[32];
				} else {
					atCmd = tmpTermObj.getAtCommand().getBytes("ASCII");
				}

				System.arraycopy(atCmd, 0, sendBuff, frameIndexNo, atCmd.length > 32 ? 32
						: atCmd.length);
				frameIndexNo += 32;
			}
		} catch (Exception e) {
			log.error("封装网关报头异常", e);
		}

		String terminalAddr = tmpTermObj.getTerminalAllAddr();
		tmpAddr1 = Integer.parseInt(terminalAddr.substring(0, 4), 16);
		sendBuff[frameIndexNo++] = 0x00;// 6byte--terminalAddr
		sendBuff[frameIndexNo++] = 0x00;
		sendBuff[frameIndexNo++] = (byte) (tmpAddr1 % 256);// 地址域A
		sendBuff[frameIndexNo++] = (byte) (tmpAddr1 >> 8);//        
		int tmpAddr2 = Integer.parseInt(terminalAddr.substring(4, 8), 16);
		sendBuff[frameIndexNo++] = (byte) (tmpAddr2 % 256);//
		sendBuff[frameIndexNo++] = (byte) (tmpAddr2 >> 8);//
		log.debug("terminalAddr:" + terminalAddr + "tmpAddr1:" + tmpAddr1 + "tmpAddr2:" + tmpAddr2);

		for (int i = 0; i < buffLen; i++)
			sendBuff[frameIndexNo++] = buff[i];
		sendBuff[frameIndexNo++] = 0x16;
		resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
		resultObj.setResultValue(0);
		resultObj.setRetMsgLen(frameIndexNo);
		resultObj.setDataMsgLen(buffLen);
		resultObj.setRetMsgBuf(sendBuff);
	}

}
