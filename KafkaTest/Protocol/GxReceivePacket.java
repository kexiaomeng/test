/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nari.global.ShareGlobalObj;
import com.nari.commObjectPara.TaskQueueObject;
import com.nari.commObjectPara.TaskResultObject;
import com.nari.commObjectPara.ConstDef;
import com.nari.commObjectPara.TerminalObject;
import com.nari.protocol.Dl698Protocol.Dl698ReceivePacket;
import com.nari.protocol.Dl698Protocol.PropertiesUtils;
import com.nari.protocolBase.AsduConverter;
import com.nari.util.HexDump;
import com.nari.fe.commdefine.define.FrontConstant;
import com.nari.fe.commdefine.param.ErrorCode;
import com.nari.fe.commdefine.task.MeterInfo;
import com.nari.fe.commdefine.task.Response;
import com.nari.fe.commdefine.task.ResponseItem;
import com.nari.fe.dbservice.DBConn;
import com.nari.front.download.FrontLoad;

/**
 * 
 * @author zhaomingyu
 */
public class GxReceivePacket extends Dl698ReceivePacket{
	private static Logger log = LoggerFactory.getLogger(GxReceivePacket.class);	
	private static ShareGlobalObj shareGlobalObjVar = ShareGlobalObj.getInstance();
	private AsduConverter asduConver = new AsduConverter();
	private GxSendPacket sendPacket = new GxSendPacket();
	private GxTransParent transParent = new GxTransParent();
	private GxExC1Data exC1 = new GxExC1Data();
	private GxExC2Data exC2 = new GxExC2Data();
	private GxExC3Data exC3 = new GxExC3Data();

	// 确认∕否认报文数据处�?
	public ArrayList<TaskResultObject> receiveConfirmData(TaskQueueObject taskObj, byte[] packetBuff) {
		ArrayList<TaskResultObject> retObjList = null;
		byte tmpDt1 = packetBuff[16];// DT1
		byte tmpDt2 = packetBuff[17];// DT2
		// 信息点标识fn
		ArrayList<Short> fnVal = asduConver.getFnVal(tmpDt1, tmpDt2);

		if (fnVal.get(0) == ConstDef.FN_DEFINE_F1)// 全部确认
		{

		} else if (fnVal.get(0) == ConstDef.FN_DEFINE_F2)// 全部否认
		{

		} else if (fnVal.get(0) == ConstDef.FN_DEFINE_F3)// 按数据单元标识确认和否认
		{
			byte tmpAfn = packetBuff[18];// AFN（要被确认的报文的
			byte tmpData = packetBuff[19];// 数据单元标识1
			byte tmpErr = packetBuff[23];// ERR1
		}

		Response resPara = new Response();
		resPara.setTaskId(taskObj.getTermTask().getTaskId());
		resPara.setTerminalAddr(taskObj.getTermTask().getTerminalAddr());
		resPara.setFuncCode((short) (taskObj.getTermTask().getFuncCode()));
		resPara.setTmnlAssetNo(taskObj.getTermTask().getTmnlAssetNo());
		resPara.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
		if (fnVal.get(0) == ConstDef.FN_DEFINE_F1)// 全部确认
			resPara.setErrorCode(ErrorCode.AckAnswer);
		else if (fnVal.get(0) == ConstDef.FN_DEFINE_F2)// 全部否认
			resPara.setErrorCode(ErrorCode.NakAnswer);
		else if (fnVal.get(0) == ConstDef.FN_DEFINE_F3)// 按数据单元标识确认和否认
			resPara.setErrorCode(ErrorCode.PartAckAnswer);
		TerminalObject tObj = shareGlobalObjVar.getTerminalPara(taskObj.getTermTask()
				.getTerminalAddr());
		if (tObj != null) {
			List<ResponseItem> pns = tObj.getPns();
			resPara.setResponseItems(pns);
		}

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

		return retObjList;
	}

	// 链路接口检测命令上行报文数据处�?
	public ArrayList<TaskResultObject> receiveLinkInterfaceDetectionCommand(
			TaskQueueObject taskObj, byte[] packetBuff) {
		ArrayList<TaskResultObject> retObjList = null;
		byte tmpDa1 = packetBuff[14];// DA1
		byte tmpDa2 = packetBuff[15];// DA2
		byte tmpDt1 = packetBuff[16];// DT1
		byte tmpDt2 = packetBuff[17];// DT2

		// 信息点标识pn
		ArrayList<Short> pnVal = asduConver.getPnVal(tmpDa1, tmpDa2);
		// 信息点标识fn
		ArrayList<Short> fnVal = asduConver.getFnVal(tmpDt1, tmpDt2);

		if (fnVal.get(0) == 1)// F1 登录
		{
			shareGlobalObjVar.writeLog(taskObj.getTermTask().getTerminalAddr(),
					"F1   登录,修改终端登录状态，记录日志", null);
		} else if (fnVal.get(0) == 2)// F2 退出登�?
		{
			shareGlobalObjVar.writeLog(taskObj.getTermTask().getTerminalAddr(),
					" F2	退出登�?修改终端登录状态，记录日志", null);
		} else if (fnVal.get(0) == 3)// F3 心跳
		{
			shareGlobalObjVar.writeLog(taskObj.getTermTask().getTerminalAddr(),
					"F3	心跳,修改终端登录状态，记录日志", null);
		}

		// 发送确认报�?
		// 链路接口检测命令下行报文为确认/否认报文中的F3按数据单元标识确认和否认
		retObjList = sendPacket.sendConfirmOrder(taskObj, fnVal.get(0));
		return retObjList;
	}

	// 中继站命令上行报文数据处�?
	public ArrayList<TaskResultObject> receiveRelayStationCommand(TaskQueueObject taskObj,
			byte[] packetBuff) {
		ArrayList<TaskResultObject> retObjList = null;

		byte tmpDt1 = packetBuff[16];// DT1
		byte tmpDt2 = packetBuff[17];// DT2

		// 信息点标识fn
		ArrayList<Short> fnVal = asduConver.getFnVal(tmpDt1, tmpDt2);
		byte tmpRetVal = 0;

		if (fnVal.get(0) == ConstDef.FN_DEFINE_F1) {
			tmpRetVal = packetBuff[18];// 数据单元1
		} else if (fnVal.get(0) == ConstDef.FN_DEFINE_F2) {
			tmpRetVal = packetBuff[18];// 数据单元1
		} else if (fnVal.get(0) == ConstDef.FN_DEFINE_F3) {
			tmpRetVal = packetBuff[18];// 数据单元1 70个字�?
		} else if (fnVal.get(0) == ConstDef.FN_DEFINE_F4) {
			tmpRetVal = packetBuff[18];// 数据单元1 10个字�?
		}
		return retObjList;
	}

	public ArrayList<TaskResultObject> receiveSetParaResponse(TaskQueueObject taskObj,
			byte[] packetBuff) {
		ArrayList<TaskResultObject> retObjList = null;

		Response resPara = new Response();
		resPara.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);

		String termAddr = "##";
		if (taskObj.getTermTask() != null) {
			resPara.setTaskId(taskObj.getTermTask().getTaskId());
			termAddr = taskObj.getTermTask().getTerminalAddr();
			resPara.setTerminalAddr(termAddr);
			resPara.setTmnlAssetNo(taskObj.getTermTask().getTmnlAssetNo());
			resPara.setFuncCode((short) (taskObj.getTermTask().getFuncCode()));
		}

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

		return retObjList;
	}

	// 被级联终端主动上报数据处�?
	public ArrayList<TaskResultObject> receiveCascadedTerminalData(TaskQueueObject taskObj,
			byte[] packetBuff) {
		ArrayList<TaskResultObject> retObjList = null;

		TaskResultObject resultObj = new TaskResultObject();

		Response resPara = asduConver.convertBuffToDataObj(taskObj);

		resultObj.setResultParaObj(resPara);
		resultObj.setResultType(ConstDef.TASKRESULT_SAVEDATA);
		resultObj.setResultValue(0);
		resultObj.setRetMsgLen(0);
		resultObj.setRetMsgBuf(null);
		resultObj.setTaskID(taskObj.getTermTask().getTaskId());

		if (retObjList == null)
			retObjList = new ArrayList<TaskResultObject>();
		retObjList.add(resultObj);

		return retObjList;
	}

	// 请求终端配置命令上行报文数据处理
	public ArrayList<TaskResultObject> receiveTerminalConfigurationData(TaskQueueObject taskObj,
			byte[] packetBuff) {
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = new TaskResultObject();

		Response resPara = asduConver.convertBuffToDataObj(taskObj);

		resultObj.setResultParaObj(resPara);
		resultObj.setResultType(ConstDef.TASKRESULT_RESPONSECOMMAND);

		resultObj.setResultValue(0);
		resultObj.setRetMsgLen(0);
		resultObj.setRetMsgBuf(null);
		if (retObjList == null)
			retObjList = new ArrayList<TaskResultObject>();
		retObjList.add(resultObj);
		return retObjList;
	}

	// 查询参数命令上行报文数据处理
	public ArrayList<TaskResultObject> receiveQueryparametersData(TaskQueueObject taskObj,
			byte[] packetBuff) {
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = new TaskResultObject();

		Response resPara = asduConver.convertBuffToDataObj(taskObj);

		if(shareGlobalObjVar.getSysProvince().equals("3")){

			ConcurrentLinkedQueue<TaskQueueObject> queue = shareGlobalObjVar
					.getTmnlTaskQueue(taskObj.getTermTask().getTerminalAddr());
			TaskQueueObject o = queue.peek();
			//华北
			if(resPara.getResponseItems() != null ){
				String name = resPara.getResponseItems().get(0).getCode();
				if(name.substring(3, 5).equals("05")){//fn=5
					String terminalStr = taskObj.getTermTask().getTerminalAddr();
					TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalStr);
					if(tmpTermObj == null){
						
					}else{
						int schemeNo = Integer.parseInt(resPara.getResponseItems().get(0).getValue());
						if(schemeNo == 0){
							//不含密码
							o.setTmnlPwd("null");
							tmpTermObj.setCommPwd("null");
						}else{
							o.setTmnlPwd(resPara.getResponseItems().get(1).getValue());
							tmpTermObj.setCommPwd(o.getTmnlPwd());
						}
					}
					//重新下发参数设置
					if(o.getTermTask().getFuncCode() == 0x04 ||o.getTermTask().getFuncCode() == 0x05){
						return sendPacket.sendSetParaOrder(o);
					}
				}
					
					
			}
		}
		
		log.debug("产生response:" + resPara.toString() + "任务ID:" + resPara.getTaskId());
		if (resPara != null) {
			resultObj.setResultParaObj(resPara);
			resultObj.setResultType(ConstDef.TASKRESULT_RESPONSECOMMAND);
			resultObj.setResultValue(0);
			resultObj.setRetMsgLen(0);
			resultObj.setRetMsgBuf(null);
			if (retObjList == null)
				retObjList = new ArrayList<TaskResultObject>();
			resultObj.setTaskID(taskObj.getTermTask().getTaskId());
			retObjList.add(resultObj);
		}
		return retObjList;
	}

	// 请求1类数据命令上行报文数据处�?
	public ArrayList<TaskResultObject> receiveAClassData(TaskQueueObject taskObj, byte[] packetBuff) {
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;

		Response resPara = exC1.explainData(taskObj);
		resPara.setTaskId(taskObj.getTermTask().getTaskId());
		resPara.setTerminalAddr(taskObj.getTermTask().getTerminalAddr());
		resPara.setFuncCode((short) (taskObj.getTermTask().getFuncCode()));
		resPara.setTmnlAssetNo(taskObj.getTermTask().getTmnlAssetNo());
		resPara.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);

		resultObj = new TaskResultObject();
		resultObj.setResultParaObj(resPara);
		resultObj.setResultType(ConstDef.TASKRESULT_RESPONSECOMMAND);
		resultObj.setResultValue(0);
		resultObj.setRetMsgLen(0);
		resultObj.setRetMsgBuf(null);
		resultObj.setTaskID(taskObj.getTermTask().getTaskId());
		if (retObjList == null)
			retObjList = new ArrayList<TaskResultObject>();
		retObjList.add(resultObj);
		return retObjList;
	}

	// 请求2类数据命令上行报文数据处�?
	public ArrayList<TaskResultObject> receiveBClassData(TaskQueueObject taskObj, byte[] packetBuff) {
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;

		Response resPara = exC2.explainData(taskObj);
		resPara.setTaskId(taskObj.getTermTask().getTaskId());
		resPara.setTerminalAddr(taskObj.getTermTask().getTerminalAddr());
		resPara.setFuncCode((short) (taskObj.getTermTask().getFuncCode()));
		resPara.setTmnlAssetNo(taskObj.getTermTask().getTmnlAssetNo());
		resPara.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);

		resultObj = new TaskResultObject();
		resultObj.setResultParaObj(resPara);
		resultObj.setResultType(ConstDef.TASKRESULT_RESPONSECOMMAND);
		resultObj.setResultValue(0);
		resultObj.setRetMsgLen(0);
		resultObj.setRetMsgBuf(null);
		resultObj.setTaskID(taskObj.getTermTask().getTaskId());

		if (retObjList == null)
			retObjList = new ArrayList<TaskResultObject>();
		retObjList.add(resultObj);
		return retObjList;
	}

	// 请求3类数据命令上行报文数据处�?
	public ArrayList<TaskResultObject> receiveCClassData(TaskQueueObject taskObj, byte[] packetBuff) {
		if (packetBuff.length < 22)
			return null;
		
//		if(!DBConn.isParseEvent()){
//			return null;
//		}
//		if(shareGlobalObjVar.isNotParseEvent()){
//			return null;
//		}
		ArrayList<TaskResultObject> retObjList = null;
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		// zhouyu add
		boolean confirmFlag = false;// 判断是否要收到事件后要下发确认报文
		if (tmpTermObj != null) {
			tmpTermObj.setTerminalPrm(false);
			byte prm = (byte) ((packetBuff[6] & 0x40) >> 6);
			if (prm == 0x01)
				tmpTermObj.setTerminalPrm(true);// 判断PRM,为后面的确认做准备
			byte con = (byte) ((packetBuff[13] & 0x10) >> 4);
			if (con == 0x01)
				confirmFlag = true;
		}
		if ((packetBuff[6] & 0xc0) == 0xc0) {// 终端主动上送
			
		}else if(taskObj.getTermTask().getTaskId() >0){
			Response resPara = exC3.explainData(taskObj);
			//modi gaolx 20120412 如果终端不是在做控招測事件任务，则事件不送web
					ConcurrentLinkedQueue<TaskQueueObject> queue = shareGlobalObjVar
							.getTmnlTaskQueue(taskObj.getTermTask().getTerminalAddr());
					int afnNo = -1;
					if(queue != null){
						TaskQueueObject o = queue.peek();
						if(o != null){
							afnNo = o.getTermTask().getFuncCode();
						}
					}
					if(afnNo != FrontConstant.REQUEST_EVENT){
						resPara.setTaskId(-1);
					}else{
						resPara.setTaskId(taskObj.getTermTask().getTaskId());
					}
			
			resPara.setTerminalAddr(terminalAddr);
			resPara.setFuncCode((short) (taskObj.getTermTask().getFuncCode()));
			resPara.setTmnlAssetNo(taskObj.getTermTask().getTmnlAssetNo());
			resPara.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
	
			TaskResultObject resultObj = new TaskResultObject();
			resultObj.setResultParaObj(resPara);
			resultObj.setResultType(ConstDef.TASKRESULT_RESPONSECOMMAND);
			resultObj.setResultValue(0);
			resultObj.setRetMsgLen(0);
			resultObj.setRetMsgBuf(null);
			resultObj.setTaskID(taskObj.getTermTask().getTaskId());
	
			retObjList = new ArrayList<TaskResultObject>();
			retObjList.add(resultObj);
		}
		if(retObjList == null){
			retObjList = new ArrayList<TaskResultObject>();
		}
		// zhouyu add for confirm
		if (confirmFlag && FrontLoad.isFinishedLoad()/*&& resPara.getDbDatas() != null && resPara.getDbDatas().size() > 0*/) {
			ArrayList<TaskResultObject> tmpTaskResultObjs = sendPacket.sendConfirmOrder(taskObj,
					ConstDef.FN_DEFINE_F1);
			if (tmpTaskResultObjs != null && tmpTaskResultObjs.size() == 1) {
				TaskResultObject t = tmpTaskResultObjs.get(0);
				if (t != null) {
					retObjList.add(t);
				}
			}
		}
		return retObjList;
	}

	//------------------------------modify by wyx 2012-11-22-------------------------------------------------//
	// 数据转发命令上行报文数据处理,即数据转发的确认命令
	public ArrayList<TaskResultObject> receiveDataForwardingData(TaskQueueObject taskObj,
			byte[] packetBuff) {

		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		ArrayList<TaskResultObject> retObjList = new ArrayList<TaskResultObject>();
		try {
			TerminalObject tmpTermObj = shareGlobalObjVar
					.getTerminalPara(terminalAddr);
			if (tmpTermObj == null) {
				return transParent.assFailTaskResultObject(taskObj, retObjList, null, "终端对象空", true);
			}

			ConcurrentLinkedQueue<TaskQueueObject> queue = shareGlobalObjVar
					.getTmnlTaskQueue(taskObj.getTermTask().getTerminalAddr());
			TaskQueueObject o = queue.peek();
			if (o == null) {
				return transParent.assFailTaskResultObject(taskObj, retObjList, null, "终端对象空", true);
			}
			taskObj.setStep(o.getStep());

			if (tmpTermObj.getAfn() == (byte) 0x09) {
				return transParent.retDataForwardF9(taskObj, packetBuff);
			}

			int len_645 = packetBuff[18]&0xff;
			if (len_645 <= 0) {
				ResponseItem resItem = new ResponseItem();
				resItem.setErrorCode((short)ErrorCode.NoData_Err.getId());
//				if(shareGlobalObjVar.getSysProvince().equals("6"))
				{
					if(o.getTimeoutReport() != null){
						//reset timeout time
						o.setStartTime(System.currentTimeMillis());
						ArrayList<TaskResultObject> tmpObjList =o.getTimeoutReport().buildTaskResultObject(o); 
						if(tmpObjList != null){
							return tmpObjList;
						}
					}
					return transParent.assFailTaskResultObject(taskObj, retObjList,
							resItem, "上行报文中不含电表信息",true);
				}
//				return transParent.assFailTaskResultObject(taskObj, retObjList,
//							resItem, "上行报文中不含电表信息",true);
			}else{
				log.debug("重发次数清零");
				if(o.getTimeoutReport() != null)
					o.getTimeoutReport().setResendCnt(0);
			}

			int step = o.getStep();
			if (step == GxTransParent.STEP_ASKMETERNO) {
				log.debug("获取电表表号返回...");
				Response resPara = transParent.recIdentityAuthentication698Report(
						taskObj, packetBuff, step);
				if (resPara != null) {
//					removeCurrentMeter(tmpTermObj);
//					changeStep(taskObj, true);
					
					ResponseItem resItem = null;
					if (resPara.getResponseItems() != null
							&& resPara.getResponseItems().size() > 0) {
						resItem = resPara.getResponseItems().get(0);
					}
					transParent.assFailTaskResultObject(taskObj, retObjList,
							resItem, resPara.getNote(),true);
					o.setStep(taskObj.getStep());
					return retObjList;

				} else {
					tmpTermObj.setAskMeterNo(false);
					changeStep(taskObj, false);
					o.setStep(taskObj.getStep());
					return sendPacket.sendDataForwarding(o);
				}
			} else if (step == GxTransParent.STEP_IDENTITYAUTHENTICATION) {
				log.debug("身份认证返回...");
				try {
					Response resPara = transParent
							.recIdentityAuthentication698Report(taskObj,
									packetBuff, step);
					if (resPara != null) {
						List<MeterInfo> meterInfos = tmpTermObj.getMeterInfos();
						
						if ((meterInfos != null && meterInfos.size() > 0 && meterInfos
								.get(0).getIdentityType() > 0)
								|| (!tmpTermObj.isAskMeterNo() || tmpTermObj
										.getMeterNo() != null)) {
							removeCurrentMeter(tmpTermObj);
							changeStep(taskObj, true);
						} else {
							changeStep(taskObj, false);
						}
						o.setStep(taskObj.getStep());
						ResponseItem resItem = null;
						if (resPara.getResponseItems() != null
								&& resPara.getResponseItems().size() > 0) {
							resItem = resPara.getResponseItems().get(0);
						}
						transParent.assFailTaskResultObject(taskObj,
								retObjList, resItem, resPara.getNote());
						return retObjList;
					} else {
						return buildControlOrSetReport(taskObj);
					}
				}catch(Exception ex){
					log.error("接受身份认证解析出错" + ex);
					return transParent.assFailTaskResultObject(taskObj, retObjList, null, "接受身份认证解析出错", true);
				}

			} else if (step == GxTransParent.STEP_OPERATE || step == GxTransParent.STEP_CONTROL_BY_OPEN_KEY) {
				retObjList = transParent.receiveControlResponse(taskObj, packetBuff,
						len_645);
//				log.debug("prepare for change step");
				changeStep(taskObj, false);
				removeCurrentMeter(tmpTermObj);
				return retObjList;
			} else if (step == GxTransParent.STEP_SET_PARAM) {
				
				retObjList = transParent.receiveParamSetResponse(taskObj, packetBuff,
						len_645);
				
				return retObjList;
			} else if (step == GxTransParent.STEP_READ_METER) {
				try{
					retObjList = transParent.receiveReadMeterDataResponse(taskObj,
							packetBuff, len_645);
					changeStep(taskObj, false);
					removeCurrentMeter(tmpTermObj);
				}catch(Exception e){
					log.error("",e);
				}
				return retObjList;
			} else {
				log.error("收到无效step类型,step : " + step);
				return null;
			}
			
		} catch (Exception e) {
			log.error("",e);
			return transParent.assFailTaskResultObject(taskObj, retObjList, null,
					"解析数据转发时发生异常", true);
		}
	}
	
	/**
	 * 转换STEP
	 * @param taskObj
	 * @param error 如果为真，则初始化STEP
	 * @return 转换中出现错误，则返回结果小于0
	 */
	public int changeStep(TaskQueueObject taskObj,boolean error){
		try{
		TerminalObject tmpTermObj = shareGlobalObjVar.
			getTerminalPara(taskObj.getTermTask().getTerminalAddr());
		if (tmpTermObj == null) {
			log.error("tmpTermObj == null");
			return -1;
		}
		
		if(tmpTermObj.getMeterInfos()== null 
				|| tmpTermObj.getMeterInfos().size()<=0){
			log.warn("meterSize == null");
			return -1;
		}
		if(error){
			MeterInfo meterInfo = tmpTermObj.getMeterInfos().get(0);
			if(meterInfo.getNote().substring(0, 2).equals("0A")
					|| meterInfo.getCmdType() == 0x0A
					|| meterInfo.getCmdType() == 0x05
					|| meterInfo.getCmdType() == 0x0C){
				taskObj.setStep(GxTransParent.STEP_READ_METER);
			}else{
				taskObj.setStep(GxTransParent.STEP_IDENTITYAUTHENTICATION);
			}
			tmpTermObj.setMeterNo(null);
			return 0;
		}
		String note = tmpTermObj.getMeterInfos().get(0).getNote();
		int step = taskObj.getStep();
		log.debug("更改前step=" + step);
		if(step == GxTransParent.STEP_ASKMETERNO){
			step = GxTransParent.STEP_IDENTITYAUTHENTICATION;
		}else if(step == GxTransParent.STEP_IDENTITYAUTHENTICATION){
			if(tmpTermObj.isAskMeterNo()){
				step = GxTransParent.STEP_ASKMETERNO;
			}else{
				if(note.equals("01")||note.equals("02")
						||note.equals("03")||note.equals("04")
						||note.equals("05")||note.equals("06")
						||note.equals("07")||note.equals("08")
						||note.equals("09")){
					step = GxTransParent.STEP_OPERATE;
				}else{
					step = GxTransParent.STEP_SET_PARAM;
				}
			}
		}else if(step == GxTransParent.STEP_OPERATE){
			tmpTermObj.setMeterNo(null);
			step = GxTransParent.STEP_IDENTITYAUTHENTICATION;
			if(Boolean.parseBoolean(PropertiesUtils.getProperty("sm.askMeterNo"))){
				step = GxTransParent.STEP_ASKMETERNO;
			}
		}else if(step == GxTransParent.STEP_SET_PARAM){
			
			if(tmpTermObj.getMeterInfos().size() >=2){
				String current = tmpTermObj.getMeterInfos().get(0).getMeterAddr();
				String next =  tmpTermObj.getMeterInfos().get(1).getMeterAddr();
				if(current.equals(next)){
					//如果下面还有表，并且下面的表和当前表相同，那么跳过身份认证阶段
					step = GxTransParent.STEP_SET_PARAM;
				}else{
					tmpTermObj.setMeterNo(null);
					if(Boolean.parseBoolean(PropertiesUtils.getProperty("sm.askMeterNo"))){
						step = GxTransParent.STEP_READ_METER;
						tmpTermObj.setAskMeterNo(true);
					}else{
						step = GxTransParent.STEP_IDENTITYAUTHENTICATION;
					}
				}
			}else{
				tmpTermObj.setMeterNo(null);
			}
		}else if(step == GxTransParent.STEP_READ_METER){
			step = GxTransParent.STEP_READ_METER;
		}
		taskObj.setStep(step);

		log.debug("更改后step=" + step);
		return 0;
		}catch(Exception e ){
			log.error("异常：" ,e);
			return 0;
		}
	}
	
//	public boolean removeCurrentMeter(TerminalObject tmpTermObj){
//		
//		boolean ret = true;
//		if(tmpTermObj.getMeterInfos() == null || tmpTermObj.getMeterInfos().size()<0){
//			return false;
//		}else{
////			if(tmpTermObj.getMeterInfos().size() >= 2){
////				MeterInfo current = tmpTermObj.getMeterInfos().get(0);
////				MeterInfo next = tmpTermObj.getMeterInfos().get(1);
////				if(current.getMeterAddr().equals(next.getMeterAddr())){
////					
////				}else{
////					tmpTermObj.setMeterNo(null);  //电表表号设置为NULL
////				}
////			}
//			MeterInfo meter = tmpTermObj.getMeterInfos().remove(0);
//			log.debug("删出电表："+ meter.getMeterAddr());
//		}
//		if(Boolean.parseBoolean(PropertiesUtils.getProperty("sm.askMeterNo"))){
//			
//		}else{
//			tmpTermObj.setAskMeterNo(false);//读表号标志设置为假，或读表号结束标志
//		}
//		
//
//		return ret;
//	}
	
//	@SuppressWarnings("unused")
//	public ArrayList<TaskResultObject> buildControlOrSetReport(TaskQueueObject taskObj){
//		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
////		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
////		int frameIndexNo = 0;
//		ArrayList<TaskResultObject> retObjList = null;
//		TaskResultObject resultObj = null;
//
//		TerminalObject tmpTermObj = shareGlobalObjVar
//				.getTerminalPara(terminalAddr);
//		if (tmpTermObj == null) {
//			log.error("tmpTermObj == null in Dl698SendPacker Error #1");
//			return null;
//		}
//		Fk04TransParent dt = new Fk04TransParent();
//		Response resPara = null;
//		
//		List<MeterInfo> meterInfos = tmpTermObj.getMeterInfos();
////		//输入参数－－－－－－随机数2
//		byte[] random2 = tmpTermObj.getRandom2();
////		//输入参数－－－－－－ESAM序列号
//		byte[] esamSequence = tmpTermObj.getEsamSequence();
//		String meterAddr = meterInfos.get(0).getMeterAddr();
//		if(random2==null || esamSequence==null || meterInfos==null || meterInfos.size()<=0){
////			removeCurrentMeter(tmpTermObj);
////			changeStep(taskObj,true);
//			//resPara = new Response();
//			//return assResponse(taskObj, retObjList, resPara,"运行错误");
//			return dt.assFailTaskResultObject(taskObj, retObjList, null, "运行错误",true);
//		}
//		
//		boolean cfFlag = false;
//		boolean cfjcFlag = false;
//		boolean bdFlag = false;
//		boolean bdjcFlag = false;
//		boolean heZaiFlag = false;
//		boolean fenZaiFlag = false;
//		boolean hezha1 = false;
//		boolean hezha2 = false;
//		boolean hezha3 = false;
//		
//		//wj add at huabei 20110119
//		boolean controlFlag = false;
//		boolean setParamTypeFlag = false;
//		String note = meterInfos.get(0).getNote();
//		//wj add at huabei 20110119				
//		int delay = meterInfos.get(0).getBakInt();
//		
//		int controlType = 0; //控制类型
//		String dataMark = null;
//		if(note.length() >= 8){
//			dataMark = note.substring(0, 8);
//			if(dataMark.equalsIgnoreCase("FF020107")/*充值*/ || dataMark.equalsIgnoreCase("FF010107")/*开户*/){
//				controlType = 1;
//			}else if(dataMark.equalsIgnoreCase("040F0004")){//透支电量限值
//				controlType = 2;
//			}else if(dataMark.equalsIgnoreCase("03100004")){//透支金额限值
//				controlType = 3;
//			}
//		}
//		int CLASS_FLAG = 0;
//		if(controlType >0){
//			
//		}else if (note.equalsIgnoreCase("01")){
//			cfFlag = true;
//			controlFlag = true;
//		}
//		else if (note.equalsIgnoreCase("02")){
//			cfjcFlag = true;
//			controlFlag = true;
//		}
//		else if (note.equalsIgnoreCase("03")){
//			heZaiFlag = true;
//			controlFlag = true;
//		}
//		else if (note.equalsIgnoreCase("04")){
//			fenZaiFlag = true;
//			controlFlag = true;
//		}
//		else if (note.equalsIgnoreCase("05")){
//			bdFlag = true;					
//			controlFlag = true;
//		}
//		else if (note.equalsIgnoreCase("06")){
//			bdjcFlag = true;
//			controlFlag = true;
//		}else if(note.equals("07")){
//			hezha1 = true;
//			controlFlag = true;
//		}else if(note.equals("08")){
//			hezha2 = true;
//			controlFlag = true;
//		}else if(note.equals("09")){
//			hezha3 = true;
//			controlFlag = true;
//		}
//		else if(-1 != note.indexOf(";")){
//			setParamTypeFlag = true;
//			
//			String[] name = note.split(";"); 
//			if(-1 != name[0].indexOf("CLASS1") || name[0].equals("07")
//					|| name[0].equals("08")||name[0].equals("09")
//					|| name[0].equals("10")||name[0].equals("11")
//					||name[0].equals("12") || name[0].equals("FF010007")){
//				CLASS_FLAG = 1;
//			}else if(-1 != name[0].indexOf("CLASS2") || -1 != name[0].indexOf("20")
//					|| -1 != name[0].indexOf("21")
//					){
//				CLASS_FLAG = 2;
//			}else{
//				log.error("WEB端发的接口信息不对:" + note);
////				removeCurrentMeter(tmpTermObj);
////				changeStep(taskObj,true);
////				resPara = new Response();
////				return assResponse(taskObj, retObjList, resPara,"WEB端发的接口信息不对");
//				return dt.assFailTaskResultObject(taskObj, retObjList, null, "WEB端发的接口信息不对",true);
//			}
//		}else {
////			log.error("WEB端发的接口信息不对：" +note);
////			removeCurrentMeter(tmpTermObj);
////			changeStep(taskObj,true);
////			resPara = new Response();
////			return assResponse(taskObj, retObjList, resPara,"WEB端发的接口信息不对");
//			return dt.assFailTaskResultObject(taskObj, retObjList, null, "WEB端发的接口信息不对",true);
//		}
//
//		log.debug("controlFlag=" + controlFlag + "setParamFlag=" + setParamTypeFlag);
//		
//		//下发参数－－－－－数据明文
//		byte[] jmBeforeReport = null;
//		try {
//			if (controlFlag){
//			    jmBeforeReport = dt.assControlData_JmBeforeReport(cfFlag,cfjcFlag,heZaiFlag,fenZaiFlag,bdFlag,bdjcFlag,hezha1,hezha2,hezha3,delay);
//			}else if(setParamTypeFlag){
//				jmBeforeReport = dt.assSetParamData_JmBeforeReport(meterInfos.get(0).getNote());
//			}else{
//				MeterInfo meterInfo = meterInfos.get(0);
//				String val = meterInfo.getNote().substring(9);
//				log.debug("数据：" + val);
//				jmBeforeReport = dt.buildControlData(controlType,val,0);
//				
//			}
//		} catch (Exception e) {
//			log.error("组装加密报文失败",e);
//			return dt.assFailTaskResultObject(taskObj, retObjList, null, "组装加密报文失败",true);
//		}
//		if (jmBeforeReport == null || jmBeforeReport.length <= 0) {
//			log.error("组织加密报文数据区失败");
//			return dt.assFailTaskResultObject(taskObj, retObjList, null, "组织加密报文数据区失败",true);
//		}
//		StringBuilder sJmBeforeReport = HexDump.toHexString(jmBeforeReport);
//		log.debug("加密前报文：" + sJmBeforeReport.toString());
//
//		byte[] dataFlag = null;
//		if(setParamTypeFlag || controlType > 0)
//		{
//			if(setParamTypeFlag){
//				Dl698ReceivePacket  dl698Recvpack = new Dl698ReceivePacket();
//				dataFlag = dl698Recvpack.Init_DataFlag(note);
//			}else if(controlType > 0){
//				try{
//					String [] Str_tmp = note.split(";");
//					dataFlag = HexDump.hexStringToByteArray(Str_tmp[0]);
////					printDataFlag(Str_tmp[0]);
//				}catch(Exception e){
//					log.error("ERROR:" + e.toString());
//					return dt.assFailTaskResultObject(taskObj, retObjList, null, "NOTE中的数据标识解析错误",true);
//				}
//			}
//			if(dataFlag == null){
//				return dt.assFailTaskResultObject(taskObj, retObjList, null, "NOTE中的数据标识解析错误",true);
//			}
//			
//		}
//		
//		if(controlFlag){
//			//组装下发电表费控报文
//			changeStep(taskObj,false);
//		    return dt.receiveMeterControl(asduConver,taskObj,meterAddr,
//				esamSequence,random2,jmBeforeReport,dataFlag) ;
//		}
//		else if (setParamTypeFlag || controlType >=2 && controlType <=3){
//				//组装下发电表费控报文
//			byte[] updateCommand = null;
//			int startPos = -1;
//			String soLibName = null;
//			try	{
//			  updateCommand = dt.assUpdateCommand(note, dataFlag[2],controlType);
//              startPos = dt.assStartPos( note,controlType);
//              soLibName = dt.assSoLibName(note, controlType);
//			}
//			catch(Exception e){
//				e.printStackTrace();
//				log.error("动态库参数解析错误！" + e.toString());
//				//return assResponse(taskObj, retObjList, resPara, "动态库参数解析错误");
//				return dt.assFailTaskResultObject(taskObj, retObjList, null, "动态库参数解析错误",true);
//			}
//			if (updateCommand == null || startPos <0 || soLibName == null){
//				log.error("updateCommand or startPos or soLibName错误");
//				return dt.assFailTaskResultObject(taskObj, retObjList, null, "接受报文中判断异常，更新命令异常或起始位置为空 ",true);
//			}
//			int lc = dt.assLC(jmBeforeReport.length, CLASS_FLAG);
//		
//			ArrayList<TaskResultObject> retObj = dt.receiveMeterParamSetData(asduConver,taskObj, meterAddr,esamSequence,
//					random2,jmBeforeReport,updateCommand , startPos , lc,soLibName,dataFlag,CLASS_FLAG) ;
//			if(taskObj.getStep() == Fk04TransParent.STEP_SET_PARAM){
//				
//			}else{
//				changeStep(taskObj,false);
//			}
//			return retObj;
//		}else if(controlType ==1/*充值，开户*/){
//			retObjList = dt.buildControlReport(asduConver,taskObj,meterAddr,
//					esamSequence,random2,jmBeforeReport,controlType);
//			if(dataMark != null && dataMark.length() >=8 && dataMark.equalsIgnoreCase("FF010107")){
//				TaskResultObject taskResultObj = retObjList.get(0);
//				byte[] msgBuf = taskResultObj.getRetMsgBuf();
//				msgBuf[23] = 0x34;
//				log.debug("msgBuff=" + HexDump.toHexString(msgBuf));
//			}
//			return retObjList;
//		}
//		else{
//			return dt.assFailTaskResultObject(taskObj, retObjList, null, "无效报文 ",true);
//		}
//	}
//	
	
}
