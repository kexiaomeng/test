package Protocol;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nari.commObjectPara.ConstDef;
import com.nari.commObjectPara.TaskQueueObject;
import com.nari.commObjectPara.TaskResultObject;
import com.nari.commObjectPara.TerminalObject;
import com.nari.fe.commdefine.task.Item;
import com.nari.fe.commdefine.task.TaskInfo;
import com.nari.global.ClearCodeDef;
import com.nari.global.ShareGlobalObj;
import com.nari.global.TaskInfoSection;

public class GxSendClassData {
	private static ShareGlobalObj shareGlobalObjVar = ShareGlobalObj.getInstance();
	private static Logger log = LoggerFactory.getLogger(GxSendClassData.class);
	private GxFunctions func = null;

	public GxSendClassData() {
		func = new GxFunctions();
	}

	// 请求1类数据（AFN=0CH）
	public ArrayList<TaskResultObject> sendRequestOneClassDataOrder(TaskQueueObject taskObj) {
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		int frameIndexNo = 0;
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;

		// FIR=1，FIN=1，CON=1,有Tp
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		byte terminalPFC = 0;
		short seqNum = 0;
		if (tmpTermObj == null) {
			log.error("TerminalObject==null!" + "任务ID:" + taskObj.getTermTask().getTaskId());
			return null;
		}
		tmpTermObj.setTerminalFIR(true);
		tmpTermObj.setTerminalFIN(true);
		tmpTermObj.setTerminalCON(false);
		// tmpTermObj.setTerminalTpV(true);
		tmpTermObj.setTerminalTpV(false);
		// PSEQ
		terminalPFC = tmpTermObj.getTerminalPFC();
		tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));
		sendBuff[frameIndexNo++] = 0x0C;// 请求1类数据（AFN=0CH）
		seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
		sendBuff[frameIndexNo++] = (byte) seqNum;
		// 测量点分包下发
		// ArrayList<byte[]> retStrList =
		// askClassOneData(taskObj.getTermTask().getTaskId());
		ArrayList<byte[]> retStrList = askClassOneData(taskObj);
		if (retStrList == null)
			return null;
		if (retStrList.size() > 0) {
			byte tmpBuff[];
			try {
				tmpBuff = retStrList.get(0);
				for (int m = 0; m < tmpBuff.length; m++) {
					sendBuff[frameIndexNo++] = tmpBuff[m];
				}
			} catch (Exception e) {
				log.error("",e);
				return null;
			}
		}
		log.debug("--Fk04Protocol 请求1类数据（AFN=0CH）:: sendBuff length==" + frameIndexNo + "任务ID:"
				+ taskObj.getTermTask().getTaskId());

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

	// 请求2类数据（AFN=0DH）
	public ArrayList<TaskResultObject> sendRequestTwoClassDataOrder(TaskQueueObject taskObj) {
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		int frameIndexNo = 0;
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;

		// FIR=1，FIN=1，CON=1,有Tp
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		byte terminalPFC = 0;
		short seqNum = 0;
		if (tmpTermObj == null) {
			log.error("TerminalObject==null!" + "任务ID:" + taskObj.getTermTask().getTaskId());
			return null;
		}
		tmpTermObj.setTerminalFIR(true);
		tmpTermObj.setTerminalFIN(true);
		tmpTermObj.setTerminalCON(false);
		tmpTermObj.setTerminalTpV(false);
		// PSEQ
		terminalPFC = tmpTermObj.getTerminalPFC();
		tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));
		sendBuff[frameIndexNo++] = 0x0D;// 请求2类数据（AFN=0DH）
		seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
		sendBuff[frameIndexNo++] = (byte) seqNum;
		ArrayList<byte[]> retStrList = askClassTwoData(taskObj, tmpTermObj);
		if (retStrList == null)
			return null;
		if (retStrList.size() > 0) {
			byte tmpBuff[];
			try {
				tmpBuff = retStrList.get(0);
				for (int m = 0; m < tmpBuff.length; m++) {
					sendBuff[frameIndexNo++] = tmpBuff[m];
				}
			} catch (Exception e) {
				log.error("",e);
				return null;
			}
		}
		log.debug("--广西规约 请求2类数据（AFN=0DH）:: sendBuff length==" + frameIndexNo + "任务ID:"
				+ taskObj.getTermTask().getTaskId());
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

	// 请求3类数据（AFN=0EH）
	public ArrayList<TaskResultObject> sendRequestThreeClassDataOrder(TaskQueueObject taskObj,
			int eventType) {
		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
		int frameIndexNo = 0;
		ArrayList<TaskResultObject> retObjList = null;
		TaskResultObject resultObj = null;

		// FIR=1，FIN=1，CON=1,有Tp
		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(terminalAddr);
		byte terminalPFC = 0;
		short seqNum = 0;
		if (tmpTermObj == null) {
			log.error("TerminalObject==null!" + "任务ID:" + taskObj.getTermTask().getTaskId());
			return null;
		}
		tmpTermObj.setTerminalFIR(true);
		tmpTermObj.setTerminalFIN(true);
		tmpTermObj.setTerminalCON(false);
		tmpTermObj.setTerminalTpV(false);
		// PSEQ
		terminalPFC = tmpTermObj.getTerminalPFC();
		tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));
		sendBuff[frameIndexNo++] = 0x0E;// 请求3类数据（AFN=0EH）
		seqNum = GxCommFunction.getSeqNumber(terminalAddr);// SEQ
		sendBuff[frameIndexNo++] = (byte) seqNum;
		if (eventType == 0)// 有应用服务器请求
		{
			ArrayList<byte[]> retStrList = askClassThreeData(taskObj.getTermTask().getTaskId(),
					tmpTermObj, eventType);
			if (retStrList == null)
				return null;
			if (retStrList.size() > 0) {
				byte tmpBuff[];
				try {
					tmpBuff = retStrList.get(0);
					for (int m = 0; m < tmpBuff.length; m++) {
						sendBuff[frameIndexNo++] = tmpBuff[m];
					}
				} catch (Exception e) {
					log.error("",e);
				}
			}
		} else if (eventType == 1) {
			sendBuff[frameIndexNo++] = 0x00;
			sendBuff[frameIndexNo++] = 0x00;
			sendBuff[frameIndexNo++] = 0x01;
			sendBuff[frameIndexNo++] = 0x00;
			sendBuff[frameIndexNo++] = tmpTermObj.getTerminalEC1();
			sendBuff[frameIndexNo++] = (byte) ((0xff & tmpTermObj.getTerminalEC1())+1);
//			if (tmpTermObj.getTerminalEC1() == tmpTermObj.getTerminalReadEC1()
//					&& tmpTermObj.getTerminalEC1() == 0) {
//				sendBuff[frameIndexNo++] = 0x00;
//				sendBuff[frameIndexNo++] = (byte) 0x0a;
//			} else {
//				sendBuff[frameIndexNo++] = tmpTermObj.getTerminalEC1();
//				sendBuff[frameIndexNo++] = tmpTermObj.getTerminalReadEC1();
//			}

		} else if (eventType == 2) {
			sendBuff[frameIndexNo++] = 0x00;
			sendBuff[frameIndexNo++] = 0x00;
			sendBuff[frameIndexNo++] = 0x02;
			sendBuff[frameIndexNo++] = 0x00;
			if (tmpTermObj.getTerminalEC2() == tmpTermObj.getTerminalReadEC2()
					&& tmpTermObj.getTerminalEC2() == 0) {
				sendBuff[frameIndexNo++] = 0x00;
				sendBuff[frameIndexNo++] = (byte) 0x0a;
			} else {
				sendBuff[frameIndexNo++] = tmpTermObj.getTerminalEC2();
				sendBuff[frameIndexNo++] = tmpTermObj.getTerminalReadEC2();
			}

		}

		log.debug("--Fk04Protocol 请求3类数据（AFN=0EH）:: sendBuff length==" + frameIndexNo + "任务ID:"
				+ taskObj.getTermTask().getTaskId());

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

	// public ArrayList<byte[]> askClassOneData(long taskId) {
	public ArrayList<byte[]> askClassOneData(TaskQueueObject taskObj) {
		try {
			ArrayList<byte[]> retStrList = new ArrayList<byte[]>();
			byte[] buff = new byte[4096];
			int frameIndexNo = 0;
			ArrayList<Short> pns = new ArrayList<Short>();
			ArrayList<Short> fns = new ArrayList<Short>();
			Short pointNo = 0;
			Short funNo = 0;
			TaskInfoSection taskInfoSection = null;
			if (taskObj.isResendFlag()) {
				taskObj.setResendFlag(false);
				taskInfoSection = taskObj.getTaskInfoSectionBak();
				if (taskInfoSection.getResendCount() < ConstDef.MAX_RESEND_NUM) {
					log.error("1类数据分段重发");
					taskInfoSection.setResendCount(taskInfoSection.getResendCount() + 1);
				} else {
					taskInfoSection = taskObj.removeTaskInfoSections();
					taskObj.setTaskInfoSectionBak(taskInfoSection);
				}
			} else {
				taskInfoSection = taskObj.removeTaskInfoSections();
				taskObj.setTaskInfoSectionBak(taskInfoSection);
			}

			if (taskInfoSection == null) {
				log.error("1类数据分段下发信息为空：");
				return null;
			}


			log.debug("1类数据分段下发信息：" + taskInfoSection.toString() + "，剩" + taskObj.getTaskInfoSectionsNum()+ 
			"段" +"终端地址：" + taskObj.getTermTask().getTerminalAddr() + "终端资产号：" + taskObj.getTermTask().getTmnlAssetNo());
			// --------------------从分布式缓存获取TaskInfo end-------------------------
			// Map<Short, Map<Short, List<Item>>> pointMap =
			// taskInfo.getPnFuncs();
			Map<Short, Map<Short, List<Item>>> pointMap = taskInfoSection.getPointMap();
			for (Map.Entry<Short, Map<Short, List<Item>>> pointEntry : pointMap.entrySet()) {
				pointNo = pointEntry.getKey();// 测量点号
				Map<Short, List<Item>> funMap = pointEntry.getValue();
				if (funMap == null || funMap.isEmpty()) {
					log.error("funMap's size is null or empty!");
					continue;
				}
				pns.add(pointNo);
				for (Map.Entry<Short, List<Item>> funEntry : funMap.entrySet()) {
					funNo = funEntry.getKey();
					fns.add(funNo);
				}
			}
			func.removeDuplicateWithOrder(pns);
			func.removeDuplicateWithOrder(fns);
			Map<Short, ArrayList<Short>> fnGrMap = func.getClass1FnGroupMap(fns);
			Map<Short, ArrayList<Short>> pnGrMap = func.getClass1PnGroupMap(pns);
			if (fnGrMap == null || fnGrMap.isEmpty()) {
				log.error("fnGrMap == null || fnGrMap is empty");
				return null;
			}
			if (pnGrMap == null || pnGrMap.isEmpty()) {
				log.error("pnGrMap == null || pnGrMap is empty");
				return null;
			}
			// 最多每8个Fn,8个Pn组包
			for (Map.Entry<Short, ArrayList<Short>> pnGrEntry : pnGrMap.entrySet()) {
				short pnGr = pnGrEntry.getKey();
				if (pnGr < 0 || pnGr > 8)
					continue;
				ArrayList<Short> pnsTemp = pnGrEntry.getValue();
				for (Map.Entry<Short, ArrayList<Short>> fnGrEntry : fnGrMap.entrySet()) {
					short fnGr = fnGrEntry.getKey();
					ArrayList<Short> fnsTemp = fnGrEntry.getValue();
					if (pnGr == 0 && (fnGr > 2))// PN为0
						continue;
					if ((fnGr == 1 || fnGr == 2) && pnGr != 0)
						continue;
					Collections.sort(pnsTemp);
					Collections.sort(fnsTemp);
					// 视情况分组合和拆分下发
					frameIndexNo = createMutiC1Data(frameIndexNo, buff, pnsTemp, pnGr, fnsTemp,
							fnGr);
					// frameIndexNo = createC1Data(frameIndexNo, buff,
					// pnsTemp,
					// pnGr, fnsTemp, fnGr);
					if (frameIndexNo == 0)
						continue;
					if (frameIndexNo > 4000) {
						// 太长时拆包,con=1，fin=0,最后一包时con=0，fin=1；
						byte tmpBuff[] = new byte[frameIndexNo];
						System.arraycopy(buff, 0, tmpBuff, 0, frameIndexNo);
						frameIndexNo = 0;// 重新初始化
						retStrList.add(tmpBuff);
					}
				}
			}
			fnGrMap = null;// 回收内存
			pnGrMap = null;
			if (frameIndexNo == 0) {// 没有匹配的FN和PN
				log.error("askClassOneData,no creat data flag! ");
				return null;
			}
			if (frameIndexNo > 0) {
				byte tmpBuff[] = new byte[frameIndexNo];
				System.arraycopy(buff, 0, tmpBuff, 0, frameIndexNo);
				// for (int j = 0; j < tmpBuff.length; j++)
				// tmpBuff[j] = buff[j];
				retStrList.add(tmpBuff);
			}
			log.debug("askClassOneData function  is finished! frameIndexNo=" + frameIndexNo);
			return retStrList;
		} catch (Exception e) {
			log.error("",e);
			return null;
		}
	}

	// public ArrayList<byte[]> askClassTwoData(long taskId, TerminalObject
	// tmpTermObj) {
	public ArrayList<byte[]> askClassTwoData(TaskQueueObject taskObj, TerminalObject tmpTermObj) {
		try {
			ArrayList<byte[]> retStrList = new ArrayList<byte[]>();
			byte[] buff = new byte[4096];
			int frameIndexNo = 0;
			ArrayList<Short> pns = new ArrayList<Short>();
			ArrayList<String> fns = new ArrayList<String>();
			Short pointNo = 0;
			Short funNo = 0;
			TaskInfoSection taskInfoSection = taskObj.removeTaskInfoSections();
			if (taskInfoSection == null) {
				log.error("2类数据分段下发信息为空：");
				return null;
			}

			log.debug("2类数据分段下发信息：" + taskInfoSection.toString());
			Map<Short, Map<Short, List<Item>>> pointMap = taskInfoSection.getPointMap();
			Date startTime = taskInfoSection.getStartTime();
			short interval = taskInfoSection.getInteval();
			short pointNum = taskInfoSection.getPointNum();
			for (Map.Entry<Short, Map<Short, List<Item>>> pointEntry : pointMap.entrySet()) {
				pointNo = pointEntry.getKey();// 测量点号
				Map<Short, List<Item>> funMap = pointEntry.getValue();
				if (funMap == null || funMap.isEmpty()) {
					log.error("funMap's size is null or empty!");
					continue;
				}
				pns.add(pointNo);
				for (Map.Entry<Short, List<Item>> funEntry : funMap.entrySet()) {
					for(Item  item:funEntry.getValue()){
						fns.add(item.getCode());
					}
				}
			}
			func.removeDuplicateWithOrder1(pns);
			func.removeDuplicateWithOrder(fns);
			ArrayList<Short> funList = func.clearCode2LocalFn(fns);
			Map<Short, ArrayList<Short>> fnGrMap = func.getClass2FnGroupMap(funList);
			Map<Short, ArrayList<Short>> pnGrMap = func.getClass2PnGroupMap(pns);
			if (fnGrMap == null || fnGrMap.isEmpty()) {
				log.error("fnGrMap == null || fnGrMap is empty");
				return null;
			}
			if (pnGrMap == null || pnGrMap.isEmpty()) {
				log.error("pnGrMap == null || pnGrMap is empty");
				return null;
			}
			// 最多每8个Fn,8个Pn组包
			for (Map.Entry<Short, ArrayList<Short>> pnGrEntry : pnGrMap.entrySet()) {
				short pnGr = pnGrEntry.getKey();
				if (pnGr < 0 || pnGr > 8)
					continue;
				ArrayList<Short> pnsTemp = pnGrEntry.getValue();
				for (Map.Entry<Short, ArrayList<Short>> fnGrEntry : fnGrMap.entrySet()) {
					short fnGr = fnGrEntry.getKey();
					ArrayList<Short> fnsTemp = fnGrEntry.getValue();
					if (pnGr == 0) {
						if (fnGr != 7 && fnGr != 28)
							continue;// 处理PN=0
					}
					if (pnGr != 0) {
						if (fnGr == 7 || fnGr == 28)
							continue;
					}
					Collections.sort(pnsTemp);
					Collections.sort(fnsTemp);
					int tmpIndexNo = frameIndexNo;
					// 视情况分组合和拆分下发
					frameIndexNo = createMutiC2Data(frameIndexNo, buff, pnsTemp, pnGr, fnsTemp,
							fnGr, startTime, interval, pointNum, tmpTermObj);
					// frameIndexNo = createC2Data(frameIndexNo, buff,
					// pnsTemp,
					// pnGr, fnsTemp, fnGr,
					// startTime, interval, pointNum, tmpTermObj);
					if (frameIndexNo == 0 || tmpIndexNo == frameIndexNo)
						continue;
					if (frameIndexNo > 4000) {
						// 太长时拆包,con=1，fin=0,最后一包时con=0，fin=1；
						byte tmpBuff[] = new byte[frameIndexNo];
						System.arraycopy(buff, 0, tmpBuff, 0, frameIndexNo);
						frameIndexNo = 0;// 重新初始化
						retStrList.add(tmpBuff);
					}
				}
			}
			fnGrMap = null;// 回收内存
			pnGrMap = null;
			if (frameIndexNo == 0) {// 没有匹配的FN和PN
				log.error("askClassTwoData,no creat data flag! ");
				return null;
			}
			if (frameIndexNo > 0) {
				byte tmpBuff[] = new byte[frameIndexNo];
				System.arraycopy(buff, 0, tmpBuff, 0, frameIndexNo);
				retStrList.add(tmpBuff);
			}
			log.debug("askClassTwoData function  is finished! frameIndexNo=" + frameIndexNo);
			return retStrList;
		} catch (Exception e) {

			log.error("",e);
			return null;
		}
	}

	public ArrayList<byte[]> askClassThreeData(long taskId, TerminalObject tmpTermObj, int eventType) {
		try {
			ArrayList<byte[]> retStrList = new ArrayList<byte[]>();
			byte[] buff = new byte[1024];
			// ArrayList<Short> fns = new ArrayList<Short>();
			HashMap<Short, List<Item>> fns = new HashMap<Short, List<Item>>();
			int frameIndexNo = 0;
			Short funNo = 0;
			// --------------------从分布式缓存获取TaskInfo-------------------------
			TaskInfo taskInfo = null;
//			ITaskHandle taskhandle = null;
			String localName = "";
			InetAddress ia = null;
			try {
				//ia = InetAddress.getLocalHost();
//				ia = shareGlobalObjVar.getLocalInetAddress();
//				localName = ia.getHostName().toLowerCase();
//				taskhandle = TaskHandle.getSharedInstance("");
//				taskInfo = taskhandle.getTaskInfo(taskId);
				taskInfo = shareGlobalObjVar.getFrontTaskInfoByTaskId(taskId);
				if (taskInfo != null)
					log.debug("taskId:" + taskId);
				else {
					log.error("taskinfo == null");
					return null;
				}
			} catch (Exception e) {
				log.error("",e);
			}
			// --------------------从分布式缓存获取TaskInfo end-------------------------
			Map<Short, Map<Short, List<Item>>> pointMap = taskInfo.getPnFuncs();
			for (Map.Entry<Short, Map<Short, List<Item>>> pointEntry : pointMap.entrySet()) {
				Map<Short, List<Item>> funMap = pointEntry.getValue();
				if (funMap == null || funMap.isEmpty()) {
					log.error("funMap's size is null or empty!" + "任务ID:" + taskId);
					continue;
				}
				for (Map.Entry<Short, List<Item>> funEntry : funMap.entrySet()) {
					funNo = funEntry.getKey();
					// fns.add(funNo);
					fns.put(funNo, funEntry.getValue());
				}
			}
			// func.removeDuplicateWithOrder(fns);
			// Collections.sort(fns);
			// for (int j = 0; j < fns.size(); j++) {
			for (Short fn : fns.keySet()) {
				buff[frameIndexNo++] = 0x00;
				buff[frameIndexNo++] = 0x00;
				buff[frameIndexNo++] = fn.byteValue();
				buff[frameIndexNo++] = 0x00;
				List<Item> PmPn = fns.get(fn);
				boolean isAssign = false;
				int Pm = 0, Pn = 0;
				if (null != PmPn) {
					isAssign = true;
					Pm = Integer.parseInt(PmPn.get(0).getValue());
					Pn = Integer.parseInt(PmPn.get(1).getValue());
					if (Pm == Pn)
						isAssign = false; // 值相同不处理
				}

				if (fn.byteValue() == 0x01) {
					if (tmpTermObj.getTerminalEC1() == tmpTermObj.getTerminalReadEC1()
							&& tmpTermObj.getTerminalEC1() == 0) {
						buff[frameIndexNo++] = 0x00;
						buff[frameIndexNo++] = (byte) 0x0a;
					} else {
						buff[frameIndexNo++] = tmpTermObj.getTerminalEC1();
						buff[frameIndexNo++] = tmpTermObj.getTerminalReadEC1();
					}
				} else if (fn.byteValue() == 0x02) {
					if (tmpTermObj.getTerminalEC2() == tmpTermObj.getTerminalReadEC2()
							&& tmpTermObj.getTerminalEC2() == 0) {
						buff[frameIndexNo++] = 0x00;
						buff[frameIndexNo++] = (byte) 0x0a;
					} else {
						buff[frameIndexNo++] = tmpTermObj.getTerminalEC2();
						buff[frameIndexNo++] = tmpTermObj.getTerminalReadEC2();
					}
				} else {
					log.debug("召唤事件Fn错误：" + fn.byteValue() + "任务ID:" + taskId);
					return null;
				}
			}

			if (frameIndexNo > 0) {
				byte tmpBuff[] = new byte[frameIndexNo];
				System.arraycopy(buff, 0, tmpBuff, 0, frameIndexNo);
				retStrList.add(tmpBuff);
			}
			log.debug("askClassThreeData function  is finished! frameIndexNo=" + frameIndexNo
					+ "任务ID:" + taskId);
			return retStrList;
		} catch (Exception e) {
			log.error("",e);
			return null;
		}

	}

	public int createMutiC1Data(int count, byte buf[], ArrayList<Short> pns, int pnGroup,
			ArrayList<Short> fns, int fnGroup) {
		try {
			for (int p = 0; p < pns.size(); p++) {
				short pn = pns.get(p);
				byte tmpda1 = 0;
				if (pn == 0) {
					tmpda1 = 0;// da1
				} else {
					if (0 == pn % 8) {
						tmpda1 = (byte) (0x01 << 7);// da1
					} else {
						tmpda1 = (byte) (0x01 << (pn % 8) - 1);// da1
					}
				}
				for (int f = 0; f < fns.size(); f++) {
					short fn = fns.get(f);
				
					byte tmpdt1 = 0;
					if (0 == fn % 8) {
						tmpdt1 = (byte) (0x01 << 7);// dt1
					} else {
						tmpdt1 = (byte) (0x01 << (fn % 8) - 1);// dt1
					}
					buf[count++] = (byte) tmpda1;// da1
					buf[count++] = (byte) pnGroup;// da2
					buf[count++] = (byte) tmpdt1;// dt1
					buf[count++] = (byte) (fnGroup - 1);// dt2
				}
			}

		} catch (Exception ex) {
			log.error("askClassOneData,in createData error! ");
			return 0;
		}
		return count;
	}

	public int createC1Data(int count, byte buf[], ArrayList<Short> pns, int pnGroup,
			ArrayList<Short> fns, int fnGroup) {
		try {
			int tmp_count = count;
			int da1 = 0, dt1 = 0;
			buf[count++] = (byte) da1;// da1
			buf[count++] = (byte) pnGroup;// da2
			buf[count++] = (byte) dt1;// dt1
			buf[count++] = (byte) (fnGroup - 1);// dt2
			boolean isDt1 = true;// 减少dt1的计算循环
			for (int p = 0; p < pns.size(); p++) {
				int pn = pns.get(p).intValue();
				byte tmpda1 = 0;
				if (pn == 0) {
					tmpda1 = 0;// da1
				} else {
					if (0 == pn % 8) {
						tmpda1 = (byte) (0x01 << 7);// da1
					} else {
						tmpda1 = (byte) (0x01 << (pn % 8) - 1);// da1
					}
				}
				for (int f = 0; f < fns.size(); f++) {
					short fn = fns.get(f);
					
					if (isDt1) {
						byte tmpdt1 = 0;
						if (0 == fn % 8) {
							tmpdt1 = (byte) (0x01 << 7);// dt1
						} else {
							tmpdt1 = (byte) (0x01 << (fn % 8) - 1);// dt1
						}
						dt1 = (byte) (dt1 | tmpdt1);
					}
				}
				isDt1 = false;
				da1 = (byte) (da1 | tmpda1);
			}
			buf[tmp_count] = (byte) da1;// da1
			buf[tmp_count + 2] = (byte) dt1;// dt1
		} catch (Exception ex) {
			log.error("askClassOneData,in createData error! ");
			return 0;
		}
		return count;
	}

	public int createMutiC2Data(int count, byte buf[], ArrayList<Short> pns, short pnGroup,
			ArrayList<Short> fns, short fnGroup, Date start, short interval, short pointNum,
			TerminalObject tmpTermObj) {
		try {
			for (int p = 0; p < pns.size(); p++) {
				short pn = pns.get(p);
				byte tmpda1 = 0;
				if (pn == 0) {
					tmpda1 = 0;// da1
				} else {
					if (0 == pn % 8) {
						tmpda1 = (byte) (0x01 << 7);// da1
					} else {
						tmpda1 = (byte) (0X01 << (pn % 8) - 1);// da1
					}
				}
				for (int f = 0; f < fns.size(); f++) {
					short fn = fns.get(f);
					
					byte tmpdt1 = 0;
					if (0 == fn % 8) {
						tmpdt1 = (byte) (0x01 << 7);// dt1
					} else {
						tmpdt1 = (byte) (0x01 << (fn % 8) - 1);// dt1
					}
					int tmp_count = count;
					buf[count++] = (byte) tmpda1;// da1
					buf[count++] = (byte) pnGroup;// da2
					buf[count++] = (byte) tmpdt1;// dt1
					buf[count++] = (byte) (fnGroup - 1);// dt2
					int tdType = func.getC2DataUnit(fn).getTdType();
					count = createContent(count, buf, tdType, start, interval, pointNum, (short) (fn+500), tmpTermObj);
					if (count == tmp_count + 4) {
						count = tmp_count;
						continue;
					}
				}
			}

		} catch (Exception ex) {
			log.error("askClassTwoData,in createData error! ");
			return 0;
		}
		return count;
	}

	public int createC2Data(int count, byte buf[], ArrayList<Short> pns, short pnGroup,
			ArrayList<Short> fns, short fnGroup, Date start, short interval, short pointNum,
			TerminalObject tmpTermObj) {
		try {
			int tmp_count = count;
			int da1 = 0, dt1 = 0;
			buf[count++] = (byte) da1;// da1
			buf[count++] = (byte) pnGroup;// da2
			buf[count++] = (byte) dt1;// dt1
			buf[count++] = (byte) (fnGroup - 1);// dt2
			boolean isDt1 = true;// 减少dt1的计算循环
			for (int p = 0; p < pns.size(); p++) {
				int pn = pns.get(p).intValue();
				byte tmpda1 = 0;
				if (pn == 0) {
					tmpda1 = 0;// da1
				} else {
					if (0 == pn % 8) {
						tmpda1 = (byte) (0x01 << 7);// da1
					} else {
						tmpda1 = (byte) (0x01 << (pn % 8) - 1);// da1
					}
				}
				for (int f = 0; f < fns.size(); f++) {
					short fn = fns.get(f);
					
					int tdType = func.getC2DataUnit(fn).getTdType();
					count = createContent(count, buf, tdType, start, interval, pointNum, fn, tmpTermObj);
					if (count == tmp_count + 4)
						continue;
					if (isDt1) {
						byte tmpdt1 = 0;
						if (0 == fn % 8) {
							tmpdt1 = (byte) (0x01 << 7);// dt1
						} else {
							tmpdt1 = (byte) (0x01 << (fn % 8) - 1);// dt1
						}
						dt1 = (byte) (dt1 | tmpdt1);
					}
				}
				if (dt1 == 0)
					return tmp_count;
				isDt1 = false;
				da1 = (byte) (da1 | tmpda1);
			}
			buf[tmp_count] = (byte) da1;// da1
			buf[tmp_count + 2] = (byte) dt1;// dt1
		} catch (Exception ex) {
			log.error("askClassTwoData,in createData error! ");
			return 0;
		}
		return count;
	}

	public int createContent(int count, byte[] sendbuf, int tdtype, Date start, short interval,
			short pointNum, short fn, TerminalObject tmpTermObj) {
		Calendar m_calendar = Calendar.getInstance();
		if (tdtype != ClearCodeDef.TD_C && tdtype != ClearCodeDef.TD_M)
			start = func.TimeBeforeProcess(tmpTermObj, start, fn);
		m_calendar.setTime(start);
		
//		if(tdtype == ClearCodeDef.TD_C)
//			m_calendar.add(Calendar.DAY_OF_MONTH, 1);
		byte min = (byte) (func.TransBinayToBcd((byte) m_calendar.get(Calendar.MINUTE)));
		byte hour = (byte) (func.TransBinayToBcd((byte) m_calendar.get(Calendar.HOUR_OF_DAY)));
		byte day = (byte) (func.TransBinayToBcd((byte) m_calendar.get(Calendar.DAY_OF_MONTH)));
		byte month = (byte) (func.TransBinayToBcd((byte) (m_calendar.get(Calendar.MONTH) + 1)));
		byte year = (byte) (func.TransBinayToBcd((byte) (m_calendar.get(Calendar.YEAR) - 2000)));
		// log.debug("year=" + year + ",month=" + month + ",day=" + day
		// + ",hour=" + hour + ",min=" + min);
		byte dotInterval = 1;
		if (tdtype == ClearCodeDef.TD_C) {
			switch (interval) {
			case 60:
				dotInterval = (byte) 0xff;
				break;
			case 300:
				dotInterval = (byte) 0xfe;
				break;
			case 900:
				dotInterval = 0x01;
				break;
			case 1800:
				dotInterval = 0x02;
				break;
			case 3600:
				dotInterval = 0x03;
				break;
			default:
				log.error("createContent,TD_C interval=" + interval + " error! ");
				return count;
			}
			if (pointNum > 255) {
				log.debug("askClassTwoData,in TD_C pointNum >255! ");
				pointNum = 255;
			}
			sendbuf[count++] = min;
			sendbuf[count++] = hour;
			sendbuf[count++] = day;
			sendbuf[count++] = month;
			sendbuf[count++] = year;
			sendbuf[count++] = dotInterval;
			sendbuf[count++] = (byte) pointNum;
		} else if (tdtype == ClearCodeDef.TD_D) {
			sendbuf[count++] = day;
			sendbuf[count++] = month;
			sendbuf[count++] = year;
		} else if (tdtype == ClearCodeDef.TD_M) {
			sendbuf[count++] = month;
			sendbuf[count++] = year;
		} else if (tdtype == ClearCodeDef.TD_H) {
			m_calendar.setTimeInMillis(System.currentTimeMillis());
			hour = (byte) (m_calendar.get(Calendar.HOUR_OF_DAY));
			hour = (byte) (hour == 0 ? 23 : hour - 1);
			hour = (byte) (func.TransBinayToBcd(hour));
			sendbuf[count++] = hour;
			sendbuf[count++] = dotInterval;
		} else {
			log.error("askClassTwoData,in creat_data_content error! ");
		}

		return count;
	}

}
