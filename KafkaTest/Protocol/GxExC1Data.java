package Protocol;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nari.protocol.Fk04Protocol.Fk04Functions;
import com.nari.protocol.Fk04Protocol.Fk04Functions.C1DataUnit;
import com.nari.ami.database.map.basicdata.BErateOffset;
import com.nari.ami.database.map.measurepoint.EDataMp;
import com.nari.ami.database.map.measurepoint.EDataTotal;
import com.nari.ami.database.map.orgnization.OOrg;
import com.nari.commObjectPara.ConstDef;
import com.nari.commObjectPara.TaskQueueObject;
import com.nari.commObjectPara.TerminalObject;
import com.nari.fe.commdefine.define.FrontConstant;
import com.nari.fe.commdefine.task.DataCode;
import com.nari.fe.commdefine.task.DbData;
import com.nari.fe.commdefine.task.Response;
import com.nari.global.ClearCodeDef;
import com.nari.global.ShareGlobalObj;

public class GxExC1Data {
	private Fk04Functions func = null;
	private ShareGlobalObj shareGlobalObj = null;
	private static final Logger log = LoggerFactory.getLogger(GxExC1Data.class);
	private byte EC1;
	private byte EC2;
	private boolean isSetEc = false;

	public GxExC1Data() {
		func = new Fk04Functions();
	}

	public Response explainData(TaskQueueObject taskObj) {
		log.debug("Fk04:ExplainClass1Data," + "任务ID:" + taskObj.getTermTask().getTaskId());
		int count = 0;
		boolean isAcd = false;// 判断ACD
		boolean isTpv = false;// 判断时间标签
		this.isSetEc = false;
		// int day, hour, minute, second;
		byte recBuf[] = null;
		String tmnlAddress = null;
		Response resPonse = new Response();
		Date startTime = null;
		ArrayList<DbData> dbDatasList = new ArrayList<DbData>();
		if (taskObj.getTaskMessage() == null) {
			resPonse.setDbDatas(null);
			return resPonse;
		}
		try {
			recBuf = taskObj.getTaskMessage();
			startTime = taskObj.getTermTask().getTaskStartTimeForApp();
			tmnlAddress = taskObj.getTermTask().getTerminalAddr();
			// StringBuffer sb = new StringBuffer();
			// for (int i = 0; i < recBuf.length; i++) {
			// sb.append(Integer.toHexString((func.getInt(recBuf[i]))));
			// sb.append(" ");
			// }
			// log.debug(sb.toString());
		} catch (Exception ex) {
			log.error("get taskObj error!!!" + "任务ID:" + taskObj.getTermTask().getTaskId());
		}
		int class1Length = func.getLength(recBuf[count + 1], recBuf[count + 2]);
		if (class1Length > 0
				&& class1Length == func.getLength(recBuf[count + 3], recBuf[count + 4])) {// 判断报文长度
		} else {
			taskObj.addLowLessReports(ConstDef.LOWLESS_LENGTH_ERROR, "class1Length=" + class1Length
					+ " error");
			resPonse.setDbDatas(null);
			return resPonse;
		}
		if ((func.getShort(recBuf[count + 6]) & 0x20) == 0x20) {// 判断ACD=1的情况，以后扩充
			isAcd = true;
			log.debug("isAcd=" + isAcd + "任务ID:" + taskObj.getTermTask().getTaskId());
		}
		if ((func.getShort(recBuf[count + 6]) & 0x0f) == 0x09) {// 否认，没有数据
			resPonse.setDbDatas(null);
			return resPonse;
		}
		// SEQ recBuf[count + 13]
		if ((func.getShort(recBuf[count + 13]) & 0x80) == 0x80) {// 判断是否有Tpv
			isTpv = true;
			log.debug("isTpv=" + isTpv + "任务ID:" + taskObj.getTermTask().getTaskId());
		}
		boolean isFE = false;// 新疆精正达
		if (func.getInt(recBuf[count + 12]) == 0xfe)
			isFE = true;
		count = 14;
		int content_length = class1Length;

		// fufeng add @anhui for 安徽安贝规约 20101223 淮南安贝特殊处理 终端地址为8位 地址为5000以后
		TerminalObject terminal = ShareGlobalObj.getInstance().getTerminalPara(
				taskObj.getTermTask().getTerminalAddr());
		// log.debug("##获取厂家类型 " + terminal.getTerminalModel());
		if (terminal.getTerminalModel().equals(ConstDef.ANHUI_ANBEI)
				&& terminal.getSrcTerminalAddr().startsWith("34045")
				&& terminal.getSrcTerminalAddr().length() == 8) {
			// log.debug("##找到安徽安贝终端");
			isAcd = true;
		}

		if (isAcd)
			content_length -= 2;
		if (isTpv)
			content_length -= 6;
		while (count < content_length + 6) {
			short[] Pn = getPns(taskObj.getTermTask().getTaskId(), recBuf, count);
			if (Pn == null) {
				taskObj.addLowLessReports(ConstDef.LOWLESS_CHECK_ERROR, "上送报文测量点组合异常");
				resPonse.setDbDatas(null);
				return resPonse;
			}
			count += 2;
			short[] Fn = getFns(taskObj.getTermTask().getTaskId(), recBuf, count);
			count += 2;
			for (int i = 0; i < 64; i++) {
				if (Pn[i] < 0)
					continue;
				for (int j = 0; j < 8; j++) {
					if (Fn[j] < 1)
						continue;
					try {
						if (isFE)
							count = getFEData(taskObj, recBuf, count, Pn[i], Fn[j], dbDatasList,
									tmnlAddress, startTime);
						else
							count = getClass1Data(taskObj, recBuf, count, Pn[i], Fn[j],
									dbDatasList, tmnlAddress, startTime);
					} catch (Exception ex) {
						taskObj.addLowLessReports(ConstDef.LOWLESS_INDEX_ERROR,
								"ArrayIndexOutOfBoundsException");
						resPonse.setDbDatas(null);
						return resPonse;
					}
					if (count < 0) {
						log.error("getClass1Data.count<0!" + "任务ID:"
								+ taskObj.getTermTask().getTaskId());
						resPonse.setDbDatas(null);
						return resPonse;
					}
				}
			}
		}
		if (count != content_length + 6) {// 到此处，count=content_length + 6
			resPonse.setDbDatas(null);
			return resPonse;
		}

		if (isAcd) {
			// int EC1 = func.getInt(recBuf[count]);
			// int EC2 = func.getInt(recBuf[count + 1]);
			// log.debug("ExC2Data,recbuf,EC1=" + EC1 + ",EC2=" + EC2);
			count += 2;// EC
		}
		if (isTpv) {
			// int PFC = func.getInt(recBuf[count]);
			// second = func.TransBcdToBinay(recBuf[count + 1]);
			// minute = func.TransBcdToBinay(recBuf[count + 2]);
			// hour = func.TransBcdToBinay(recBuf[count + 3]);
			// day = func.TransBcdToBinay(recBuf[count + 4]);
			// int delay_time = func.getInt(recBuf[count + 5]);
			// log.debug(
			// "ExplainClass1Data,in recbuf,pfc=" + PFC
			// + "Tp: second=" + second + ",minute=" + minute + ",hour=" + hour
			// + ",day="
			// + day + ",delay_time=" + delay_time);
			count += 6;
		}
		byte check = (byte) func.checkSum(recBuf, 6, class1Length);
		if (recBuf[count] != check) {
			taskObj.addLowLessReports(ConstDef.LOWLESS_CHECK_ERROR, "checkSum error!");
			resPonse.setDbDatas(null);
			return resPonse;
		}
		count += 1;
		if (recBuf[count] != 0x16) {
			resPonse.setDbDatas(null);
			return resPonse;
		}
		resPonse.setDbDatas(dbDatasList);
		if (isSetEc) {
			TerminalObject tmpTermObj = ShareGlobalObj.getInstance().getTerminalPara(tmnlAddress);
			if (tmpTermObj != null) {
				tmpTermObj.setTerminalReadEC1(EC1);
				tmpTermObj.setTerminalReadEC2(EC2);
			}
		}
		// for (int i = 0; i < dbDatasList.size(); i++) {
		// DbData db = (DbData) dbDatasList.get(i);
		// ArrayList<DataCode> dcArr = db.getDataCodes();
		// for (int j = 0; j < dcArr.size(); j++) {
		// DataCode dc = (DataCode) dcArr.get(j);
		// log.debug("dc.name()==" + dc.getName());
		// log.debug("dc.value()==" + dc.getValue());
		// }
		// }
		return resPonse;
	}

	public int getFEData(TaskQueueObject taskObj, byte buf[], int count, short Pn, short Fn,
			ArrayList<DbData> dbList, String tmnlAddress, Date startTime) throws Exception {
		Calendar cl = Calendar.getInstance();
		cl.set(Calendar.DAY_OF_MONTH, func.getInt(buf[count]));
		cl.set(Calendar.HOUR_OF_DAY, func.getInt(buf[count + 1]));
		cl.set(Calendar.MINUTE, func.getInt(buf[count + 2]));
		cl.set(Calendar.SECOND, func.getInt(buf[count + 3]));
		DbData db = initDbData(Fn, Pn, tmnlAddress, ClearCodeDef.CL_TYPE, cl.getTime(), null);
		if (db == null) {
			log.error("initDbData=null,pn=" + Pn + "任务ID:" + taskObj.getTermTask().getTaskId());
			taskObj.addLowLessReports(ConstDef.LOWLESS_PN_ERROR, "initDbData=null,pn=" + Pn);
			db = new DbData();
			db.setDataId(-1);
			db.setPn(Pn);
			db.setMark(0);
			db.setTime(cl.getTime());
			db.setTmnlAddr(tmnlAddress);
			TerminalObject tmpTermObj = shareGlobalObj.getTerminalPara(tmnlAddress);
			if(null!=tmpTermObj)
			{
				db.setTmnlAssetNo(tmpTermObj.getTmnlAssetNo());
			}
		}
		db.setDataType(FrontConstant.NORMAL_DATA);
		DataCode dc = null;
		ArrayList<DataCode> dataCodes = new ArrayList<DataCode>();
		count += 4;
		switch (Fn) {
		case 1:
			for (int j = 0; j < 4; j++) {
				dc = getDataCode(func.TransData9(buf, count), ClearCodeDef.F25_C1[j]);
				dataCodes.add(dc);
				count += 3;
			}
			break;
		case 2:
			for (int j = 0; j < 4; j++) {
				dc = getDataCode(func.TransData9(buf, count), ClearCodeDef.F25_C1[4 + j]);
				dataCodes.add(dc);
				count += 3;
			}
			break;
		case 3:
			for (int j = 0; j < 3; j++) {
				dc = getDataCode(func.TransData7(buf, count), ClearCodeDef.F25_C1[12 + j]);
				dataCodes.add(dc);
				count += 2;
			}
			break;
		case 4:
			for (int j = 0; j < 3; j++) {
				dc = getDataCode(func.TransData6(buf, count), ClearCodeDef.F25_C1[15 + j]);
				dataCodes.add(dc);
				count += 2;
			}
			break;
		case 5:
			for (int j = 0; j < 5; j++) {
				dc = getDataCode(func.TransData11(buf, count), ClearCodeDef.F33_C1[j]);
				dataCodes.add(dc);
				count += 4;
			}
			break;
		case 6:
			for (int j = 0; j < 5; j++) {
				dc = getDataCode(func.TransData11(buf, count), ClearCodeDef.F34_C1[j]);
				dataCodes.add(dc);
				count += 4;
			}
			break;
		case 7:
			for (int j = 0; j < 5; j++) {
				dc = getDataCode(func.TransData11(buf, count), ClearCodeDef.F33_C1[13 + j]);
				dataCodes.add(dc);
				count += 4;
			}
			break;
		case 8:
			for (int j = 0; j < 5; j++) {
				dc = getDataCode(func.TransData11(buf, count), ClearCodeDef.F34_C1[13 + j]);
				dataCodes.add(dc);
				count += 4;
			}
			break;
		case 9:
			for (int j = 0; j < 4; j++) {
				dc = getDataCode(func.TransData5(buf, count), ClearCodeDef.F25_C1[8 + j]);
				dataCodes.add(dc);
				count += 2;
			}
			break;
		case 10:
			for (int j = 0; j < 5; j++) {
				dc = getDataCode(func.TransData23(buf, count), ClearCodeDef.F35_C1[j]);
				dataCodes.add(dc);
				count += 3;
			}
			break;
		case 11:
			for (int j = 0; j < 5; j++) {
				dc = getDataCode(func.TransData23(buf, count), ClearCodeDef.F36_C1[j]);
				dataCodes.add(dc);
				count += 3;
			}
			break;
		}
		db.setDataCodes(dataCodes);
		dbList.add(db);
		return count;
	}

	public int getClass1Data(TaskQueueObject taskObj, byte buf[], int count, short Pn, short Fn,
			ArrayList<DbData> dbList, String tmnlAddress, Date startTime) {
		int dataNum = -1;
		C1DataUnit du = func.getC1DataUnit(Fn);
		if (du == null) {
			log
					.error("getC1Data,fn:" + Fn + " error" + "任务ID:"
							+ taskObj.getTermTask().getTaskId());
			taskObj.addLowLessReports(ConstDef.LOWLESS_FN_ERROR, "getC1Data,fn:" + Fn + " error");
			return -1;
		}
		int pnType = du.getPnType();
		String codeStr[] = du.getCodeStr();
		int countType = du.getCountType();
		int countContent = du.getCountContent();
		int byteLength = du.getBytLength();
		int offset = du.getOff();
		int valueType = du.getValueType();
		int tdType = du.getTdType();
		if (checkPn(pnType, Pn) < 0 || codeStr == null) {
			log.error("getC1Data error" + "任务ID:" + taskObj.getTermTask().getTaskId());
			taskObj.addLowLessReports(ConstDef.LOWLESS_CHECK_ERROR,
					"checkPn(pnType, Pn) < 0 || codeStr == null");
			return -1;
		}
		// 模拟dataid，如果要真实的，将下面一行去除
		// this.setForExPacket(true);
		Date collTimeTag = class1time(buf, count, countType, tdType, startTime);
		if (collTimeTag == null) {
			log.error("class1time error" + "任务ID:" + taskObj.getTermTask().getTaskId());
			taskObj.addLowLessReports(ConstDef.LOWLESS_CHECK_ERROR, "collTimeTag == null");
			return -1;
		}
		String[] tmpCodeStr = new String[codeStr.length];
		for (int i = 0; i < codeStr.length; i++) {
			tmpCodeStr[i] = codeStr[i];
		}
		DbData db = initDbData(Fn, Pn, tmnlAddress, pnType, collTimeTag, tmpCodeStr);
		if (db == null) {
			log.error("initDbData=null,pn=" + Pn + "任务ID:" + taskObj.getTermTask().getTaskId());
			taskObj.addLowLessReports(ConstDef.LOWLESS_PN_ERROR, "initDbData=null,pn=" + Pn);
			db = new DbData();
			db.setDataId(-1);
			db.setPn(Pn);
			db.setMark(0);
			db.setTime(collTimeTag);
			db.setTmnlAddr(tmnlAddress);
			TerminalObject tmpTermObj = shareGlobalObj.getTerminalPara(tmnlAddress);
			if(null!=tmpTermObj)
			{
				db.setTmnlAssetNo(tmpTermObj.getTmnlAssetNo());
			}
		}
		db.setDataType(FrontConstant.NORMAL_DATA);
		ArrayList<DataCode> dcArray = null;
		if (countType == 1)
			dataNum = getDataNum(buf[count + offset]);
		else if (countType == 2) {
			dataNum = func.getInt(buf[count + offset]);
		}
		switch (countType) {
		case 0:
			dcArray = getDataCodes(taskObj.getTermTask().getTaskId(), buf, count, Fn, dataNum,
					tmpCodeStr);
			if (dcArray == null || dcArray.isEmpty()) {
				log.error("dcArray=null or empty" + "任务ID:" + taskObj.getTermTask().getTaskId());
				return -1;
			}
			db.setDataCodes(dcArray);
			dbList.add(db);
			count += countContent;
			break;
		case 1:
		case 2:
			dcArray = getDataCodes(taskObj.getTermTask().getTaskId(), buf, count, Fn, dataNum,
					tmpCodeStr);
			if (dcArray == null || dcArray.isEmpty()) {
				log.error("dcArray=null or empty" + "任务ID:" + taskObj.getTermTask().getTaskId());
				return -1;
			}
			db.setDataCodes(dcArray);
			dbList.add(db);
			count += byteLength * dataNum + countContent;
			break;
		case 3:
			db.setDataType(FrontConstant.CURVE_DATA);
			db.setValueType(valueType);
			int lasthour = 0;
			try {
				lasthour = func.TransBcdToBinay(buf[count] & 0x3f);// 表示上一整点的小时时间,
			} catch (Exception e) {
				log.error(e.toString());
				return -1;
			}

			// 数值范围0～23
			int m = (buf[count] & 0xc0) >> 6;// 数据冻结密度m
			switch (m) {
			case 0:
				db.setInterval(0);
				break;
			case 1:
				db.setInterval(900);
				break;
			case 2:
				db.setInterval(1800);
				break;
			case 3:
				db.setInterval(3600);
				break;
			case 254:
				db.setInterval(300);
				break;
			case 255:
				db.setInterval(60);
				break;
			default:
				db.setInterval(0);
			}
			int n = getTd_hNum(m);// 数据点数n
			if (lasthour < 0 || lasthour > 23 || n < 0)
				return -1;
			count += 1;
			dcArray = getDataCodes(taskObj.getTermTask().getTaskId(), buf, count, Fn, n, tmpCodeStr);
			if (dcArray == null || dcArray.isEmpty()) {
				log.error("dcArray=null or empty" + "任务ID:" + taskObj.getTermTask().getTaskId());
				return -1;
			}
			db.setDataCodes(dcArray);
			dbList.add(db);
			count += n * byteLength;
			break;
		}
		return count;
	}

	public ArrayList<DataCode> getDataCodes(long taskId, byte[] buf, int startp, int Fn, int num,
			String[] codeString) {
		try {
			DataCode dc = null;
			ArrayList<DataCode> dataCodes = new ArrayList<DataCode>();
			StringBuffer strBuf = null;
			switch (Fn) {
			case 1:
				dc = getDataCode(func.AsciiToString(buf, startp, 4), codeString[0]);
				dataCodes.add(dc);
				startp += 4;
				dc = getDataCode(func.AsciiToString(buf, startp, 8), codeString[1]);
				dataCodes.add(dc);
				startp += 8;
				dc = getDataCode(func.AsciiToString(buf, startp, 4), codeString[2]);
				dataCodes.add(dc);
				startp += 4;
				dc = getDataCode(func.getDate20(buf, startp), codeString[3]);
				dataCodes.add(dc);
				startp += 3;
				dc = getDataCode(func.AsciiToString(buf, startp, 11), codeString[4]);
				dataCodes.add(dc);
				startp += 11;
				break;
			case 2:
				dc = getDataCode(func.getDate1(buf, startp), codeString[0]);
				dataCodes.add(dc);
				break;
			case 3:
			case 8:
//			case 9:
				strBuf = func.transBS8ToStrBuffer(buf[startp]);// 低位~高位
				int temp = 30;
				if (Fn == 8)
					temp = 7;
				if (Fn == 9)
					temp = 1;
				startp += 1;
				for (int j = 0; j < temp; j++) {
					strBuf.append(func.transBS8ToStrBuffer(buf[startp]).toString());
					startp += 1;
				}
				dc = getDataCode(strBuf.toString(), codeString[0]);
				dataCodes.add(dc);
				break;
			case 9:
				strBuf = func.transBS8ToStrBuffer1(buf[startp]);// 低位~高位
				temp = 30;
				if (Fn == 8)
					temp = 7;
				if (Fn == 9)
					temp = 1;
				startp += 1;
				for (int j = 0; j < temp; j++) {
					strBuf.append(func.transBS8ToStrBuffer1(buf[startp]).toString());
					startp += 1;
				}
				dc = getDataCode(strBuf.toString(), codeString[0]);
				dataCodes.add(dc);
				break;
			case 4:
				dc = getDataCode(buf[startp] & 0x0c, codeString[0]);
				dataCodes.add(dc);
				dc = getDataCode(buf[startp] & 0x03, codeString[1]);
				dataCodes.add(dc);
				break;
			case 5:
				strBuf = func.transBS8ToStrBuffer(buf[startp]);
				dc = getDataCode(strBuf.substring(0, 3), codeString[0]);
				dataCodes.add(dc);
				startp += 1;
				strBuf = func.transBS8ToStrBuffer(buf[startp]);
				dc = getDataCode(strBuf.toString(), codeString[1]);
				dataCodes.add(dc);
				startp += 1;
				for (int j = 0; j < num; j++) {
					dc = getDataCode(buf[startp] & 0x03, codeString[2]);
					dataCodes.add(dc);
					for (int k = 0; k < 5; k++) {
						strBuf = func.transBS8ToStrBuffer(buf[startp + 1 + k]);
						dc = getDataCode(strBuf.toString(), codeString[3 + k]);
						dataCodes.add(dc);
					}
					startp += 6;
				}
				break;
			case 6:
				for (int j = 0; j < 3; j++) {
					strBuf = func.transBS8ToStrBuffer(buf[startp]);
					dc = getDataCode(strBuf.toString(), codeString[j]);
					dataCodes.add(dc);
					startp += 1;
				}
				for (int j = 0; j < num; j++) {
					dc = getDataCode(func.TransData2(buf, startp), codeString[3]);
					dataCodes.add(dc);
					dc = getDataCode(func.TransData4(buf, startp + 2), codeString[4]);
					dataCodes.add(dc);
					for (int k = 0; k < 5; k++) {
						strBuf = func.transBS8ToStrBuffer(buf[startp + 3 + k]);
						dc = getDataCode(strBuf.toString(), codeString[5 + k]);
						dataCodes.add(dc);
					}
					startp += 8;
				}
				break;
			case 7:
				for (int j = 0; j < 2; j++) {
					dc = getDataCode(func.getInt(buf[startp + j]), codeString[j]);
					dataCodes.add(dc);
				}
				this.isSetEc = true;
				this.EC1 = buf[startp];
				this.EC2 = buf[startp + 1];
				break;
			case 17:
			case 18:
			case 24:
				dc = getDataCode(func.TransData2(buf, startp), codeString[0]);
				dataCodes.add(dc);
				break;
			case 19:
			case 20:
			case 21:
			case 22:
			case 41:
			case 42:
			case 43:
			case 44:
			case 45:
			case 46:
			case 47:
			case 48:
				startp += 1;
				if (Fn < 23)
					for (int j = 0; j < num + 1; j++) {
						dc = getDataCode(func.TransData3(buf, startp), codeString[j]);
						dataCodes.add(dc);
						startp += 4;
					}
				else
					for (int j = 0; j < num + 1; j++) {
						dc = getDataCode(func.TransData13(buf, startp), codeString[j]);
						dataCodes.add(dc);
						startp += 4;
					}
				break;
			case 23:
				dc = getDataCode((double)func.TransData3(buf, startp), codeString[0]);
				dataCodes.add(dc);
				break;
			case 25:
				dc = getDataCode(func.getDate15(buf, startp), codeString[24]);
				dataCodes.add(dc);
				startp += 5;
				for (int j = 0; j < 8; j++) {
					dc = getDataCode(func.TransData9(buf, startp), codeString[j]);
					dataCodes.add(dc);
					startp += 3;
				}
				for (int j = 0; j < 4; j++) {
					dc = getDataCode(func.TransData5(buf, startp), codeString[8 + j]);
					dataCodes.add(dc);
					startp += 2;
				}
				for (int j = 0; j < 3; j++) {
					dc = getDataCode(func.TransData7(buf, startp), codeString[12 + j]);
					dataCodes.add(dc);
					startp += 2;
				}
				for (int j = 0; j < 4; j++) {
					dc = getDataCode(func.TransData6(buf, startp), codeString[15 + j]);
					dataCodes.add(dc);
					startp += 2;
				}
				break;
			case 26:
				dc = getDataCode(func.getDate15(buf, startp), codeString[17]);
				dataCodes.add(dc);
				startp += 5;
				for (int j = 0; j < 4; j++) {
					dc = getDataCode(func.TransData8(buf, startp), codeString[j]);
					dataCodes.add(dc);
					startp += 2;
				}
				for (int j = 0; j < 4; j++) {
					dc = getDataCode(func.TransData10(buf, startp), codeString[4 + j]);
					dataCodes.add(dc);
					startp += 3;
				}
				for (int j = 0; j < 8; j++) {
					dc = getDataCode(func.getDate17(buf, startp), codeString[8 + j]);
					dataCodes.add(dc);
					startp += 4;
				}
				break;
			case 27:// 需要修改
				dc = getDataCode(func.getDate15(buf, startp), codeString[13]);
				dataCodes.add(dc);
				startp += 5;
				dc = getDataCode(func.getDate1(buf, startp), codeString[0]);
				dataCodes.add(dc);
				startp += 6;
				dc = getDataCode(func.TransData27(buf, startp), codeString[1]);
				dataCodes.add(dc);
				startp += 4;
				for (int j = 0; j < 5; j++) {
					dc = getDataCode(func.TransData10(buf, startp), codeString[2 * j + 2]);
					dataCodes.add(dc);
					startp += 3;
					dc = getDataCode(func.getDate1(buf, startp), codeString[2 * j + 3]);
					dataCodes.add(dc);
					startp += 6;
				}
				break;
			case 33:
			case 34:
			case 37:
			case 38:
				dc = getDataCode(func.getDate15(buf, startp), codeString[53]);
				dataCodes.add(dc);
				startp += 6;
				for (int j = 0; j < num + 1; j++) {
					dc = getDataCode(func.TransData14(buf, startp), codeString[j]);
					dataCodes.add(dc);
					startp += 5;
				}
				for (int i = 1; i < 4; i++) {
					for (int j = 0; j < num + 1; j++) {
						dc = getDataCode(func.TransData11(buf, startp), codeString[13 * i + j]);
						dataCodes.add(dc);
						startp += 4;
					}
				}
				break;
			case 35:
			case 36:
			case 39:
			case 40:
				dc = getDataCode(func.getDate15(buf, startp), codeString[53]);
				dataCodes.add(dc);
				startp += 6;
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < num + 1; j++) {
						dc = getDataCode(func.TransData9(buf, startp), codeString[j + 26 * i]);
						dataCodes.add(dc);
						startp += 3;
					}
					for (int j = 0; j < num + 1; j++) {
						dc = getDataCode(func.getDate17(buf, startp), codeString[26 * i + j + 13]);
						dataCodes.add(dc);
						startp += 4;
					}
				}
				break;
			case 49:
				for (int j = 0; j < 6; j++) {
					dc = getDataCode(func.TransData5(buf, startp), codeString[j]);
					dataCodes.add(dc);
					startp += 2;
				}
				break;
			case 57:
				startp += 1;
				for (int j = 0; j < 3; j++) {
					for (int k = 0; k < num - 1; k++) {
						dc = getDataCode(func.TransData7(buf, startp), codeString[18 * j + k]);
						dataCodes.add(dc);
						startp += 2;
					}
				}
				for (int j = 0; j < 3; j++) {
					for (int k = 0; k < num - 1; k++) {
						dc = getDataCode(func.TransData6(buf, startp), codeString[54 + 18 * j + k]);
						dataCodes.add(dc);
						startp += 2;
					}
				}
				break;
			case 58:
				startp += 1;
				for (int j = 0; j < 3; j++) {
					for (int k = 0; k < num; k++) {
						dc = getDataCode(func.TransData5(buf, startp), codeString[19 * j + k]);
						dataCodes.add(dc);
						startp += 2;
					}
				}
				for (int j = 0; j < 3; j++) {
					for (int k = 0; k < num - 1; k++) {
						dc = getDataCode(func.TransData5(buf, startp), codeString[57 + 18 * j + k]);
						dataCodes.add(dc);
						startp += 2;
					}
				}
				break;
			case 65:
				strBuf = func.transBS8ToStrBuffer(buf[startp]);
				dc = getDataCode(strBuf.toString(), codeString[0]);
				dataCodes.add(dc);
				strBuf = func.transBS8ToStrBuffer(buf[startp + 1]);
				strBuf.append(func.transBS8ToStrBuffer(buf[startp + 2]).toString());
				dc = getDataCode(strBuf.toString(), codeString[1]);
				dataCodes.add(dc);
				break;
			case 66:
				for (int j = 0; j < 18; j++) {
					int trTemp = 0;
					if ((buf[startp] == (byte) (0xee)) && (buf[startp + 1] == (byte) (0xee))
							&& (buf[startp + 2] == (byte) (0xee))
							&& (buf[startp + 3] == (byte) (0xee))) {
						trTemp = -9999;
					} else {
						trTemp += func.getInt(buf[startp]);
						trTemp += (func.getInt(buf[startp + 1]) << 8);
						trTemp += (func.getInt(buf[startp + 2]) << 16);
						trTemp += ((func.getInt(buf[startp + 3]) & 0x7f) << 24);
					}
					dc = getDataCode(trTemp, codeString[j]);
					dataCodes.add(dc);
					startp += 4;
				}
				break;
			case 67:
				for (int j = 0; j < 2; j++) {
					dc = getDataCode(func.TransData13(buf, startp), codeString[j]);
					dataCodes.add(dc);
					startp += 4;
				}
				break;
			case 73:
				dc = getDataCode(func.TransData2(buf, startp), codeString[0]);
				dataCodes.add(dc);
				break;
			case 81:
			case 82:
			case 121:
				double value[] = new double[num];
				for (int j = 0; j < num; j++) {
					value[j] = func.TransData2(buf, startp);
					startp += 2;
				}
				dc = getDataCode(value, codeString[0]);
				dataCodes.add(dc);
				break;
			case 83:
			case 84:
				long lvalue[] = new long[num];
				for (int j = 0; j < num; j++) {
					lvalue[j] = func.TransData3(buf, startp);
					startp += 4;
				}
				dc = getDataCode(lvalue, codeString[0]);
				dataCodes.add(dc);
				break;
			case 89:
			case 90:
			case 91:
			case 92:
			case 93:
			case 94:
			case 95:
			case 96:
				value = new double[num];
				for (int j = 0; j < num; j++) {
					value[j] = func.TransData9(buf, startp);
					startp += 3;
				}
				dc = getDataCode(value, codeString[0]);
				dataCodes.add(dc);
				break;
			case 97:
			case 98:
			case 99:
				value = new double[num];
				for (int j = 0; j < num; j++) {
					value[j] = func.TransData7(buf, startp);
					startp += 2;
				}
				dc = getDataCode(value, codeString[0]);
				dataCodes.add(dc);
				break;
			case 100:
			case 101:
			case 102:
			case 103:
				value = new double[num];
				for (int j = 0; j < num; j++) {
					value[j] = func.TransData6(buf, startp);
					startp += 2;
				}
				dc = getDataCode(value, codeString[0]);
				dataCodes.add(dc);
				break;
			case 105:
			case 106:
			case 107:
			case 108:
				value = new double[num];
				for (int j = 0; j < num; j++) {
					value[j] = func.TransData13(buf, startp);
					startp += 4;
				}
				dc = getDataCode(value, codeString[0]);
				dataCodes.add(dc);
				break;
			case 109:
			case 110:
			case 111:
			case 112:
				value = new double[num];
				for (int j = 0; j < num; j++) {
					value[j] = func.TransData11(buf, startp);
					startp += 4;
				}
				dc = getDataCode(value, codeString[0]);
				dataCodes.add(dc);
				break;
			case 113:
			case 114:
			case 115:
			case 116:
				value = new double[num];
				for (int j = 0; j < num; j++) {
					value[j] = func.TransData5(buf, startp);
					startp += 2;
				}
				dc = getDataCode(value, codeString[0]);
				dataCodes.add(dc);
				break;
			default:
				log.error("fn error!,fn=" + Fn + "任务ID:" + taskId);
				return null;
			}
			return dataCodes;
		} catch (Exception ex) {
			log.error("getDataCodes error!,fn=" + Fn + "任务ID:" + taskId);
			return null;
		}
	}

	public short[] getFns(long taskId, byte[] buf, int startp) {
		short[] Fns = new short[8];
		for (int i = 0; i < Fns.length; i++) {
			Fns[i] = -1;
		}
		try {
			short d1 = 0, d2 = 0;
			d2 = (short) ((func.getShort(buf[startp + 1])) << 3);
			for (int i = 0; i < 8; i++) {
				byte temp_d1 = (byte) ((0x01 << i));
				if ((buf[startp] & temp_d1) == temp_d1) {
					d1 = (short) (i + 1);
					Fns[i] = (short) (d2 + d1);
				}
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			log.error("getFns error!" + "任务ID:" + taskId);
		}
		return Fns;
	}

	public short[] getPns(long taskId, byte[] buf, int startp) {
		short[] Pns = new short[64];
		for (int i = 0; i < Pns.length; i++) {
			Pns[i] = -1;
		}
		try {
			if (buf[startp] == 0 && buf[startp + 1] == 0)
				Pns[0] = 0;
			else {
				if (buf[startp] != 0 && buf[startp + 1] != 0) {
					short d1 = 0, d2 = 0;
					for (int i = 0; i < 8; i++) {// da2
						byte temp_d2 = (byte) ((0x01 << i));
						if ((buf[startp + 1] & temp_d2) == temp_d2) {
							d2 = (short) (i << 3);
							for (int j = 0; j < 8; j++) {// da1
								byte temp_d1 = (byte) ((0x01 << j));
								if ((buf[startp] & temp_d1) == temp_d1) {
									d1 = (short) (j + 1);
									Pns[i * 8 + j] = (short) (d2 + d1);
								}
							}
						}
					}
				} else {
					log.error("getPns error!" + "任务ID:" + taskId);
					return null;
				}
			}
		} catch (Exception ex) {
			log.error("getPns error!" + "任务ID:" + taskId);
		}
		return Pns;
	}

	public int checkPn(int Pntype, int Pn) {
		if (Pn == 0 && Pntype != ClearCodeDef.P0_TYPE)
			return -1;
		if (Pn != 0 && Pntype == ClearCodeDef.P0_TYPE)
			return -1;
		return 1;
	}

	public DataCode getDataCode(Object obj, String str) {
		DataCode dac = new DataCode();
		dac.setValue(obj);
		dac.setName(str);
		return dac;
	}

	public DbData initDbData(short Fn, short Pn, String tmnlAddress, int pnType, Date timeTag,
			String[] codeString) {

		try {
			shareGlobalObj = ShareGlobalObj.getInstance();
			TerminalObject tmpTermObj = shareGlobalObj.getTerminalPara(tmnlAddress);
			if (tmpTermObj != null) {
				if(Fn == 23){//召测剩余金额，剩余电量,没有曲线数据（周永贞20110805）
					
				}else{
					int cyCleNum = tmpTermObj.getFreezeCyCleNum();
					timeTag = this.truncTimeTag(cyCleNum, timeTag);
				}
			}
			DbData db = new DbData();
			String orgNo = null;
			switch (pnType) {
			case ClearCodeDef.CL_TYPE:
			case ClearCodeDef.P0_TYPE:
				EDataMp eDataMp = shareGlobalObj.getMp(tmnlAddress, Pn);
				if (eDataMp == null)
					return null;
				Integer ct = eDataMp.getCt();
				Integer pt = eDataMp.getPt();
				db.setCT(ct == null ? 1 : ct.intValue());
				db.setPT(pt == null ? 1 : pt.intValue());
				db.setDataId(eDataMp.getId());
				orgNo = eDataMp.getOrgNo();
				if (codeString != null)
					changePostion(Fn, eDataMp.getErateOffset(), codeString);
				break;
			case ClearCodeDef.ZJ_TYPE:
				EDataTotal eDataTotal = shareGlobalObj.getEDataTotal(tmnlAddress, Pn);
				if (eDataTotal == null)
					return null;
				db.setDataId(eDataTotal.getId());
				orgNo = eDataTotal.getOrgNo();
				break;
			case ClearCodeDef.ZLDK_TYPE:
				eDataMp = shareGlobalObj.getDcMp(tmnlAddress, Pn);
				if (eDataMp == null)
					return null;
				ct = eDataMp.getCt();
				pt = eDataMp.getPt();
				db.setCT(ct == null ? 1 : ct.intValue());
				db.setPT(pt == null ? 1 : pt.intValue());
				db.setDataId(eDataMp.getId());
				orgNo = eDataMp.getOrgNo();
				if (codeString != null)
					changePostion(Fn, eDataMp.getErateOffset(), codeString);
				break;
			}
			/*OOrg oorg = shareGlobalObj.getOrgPara(orgNo);
			if (oorg != null) {
				db.setAreaCode(oorg.getAreaCode());
			}*/
			db.setOrgNo(orgNo);
			db.setPn(Pn);
			db.setTmnlAssetNo(shareGlobalObj.getTerminalPara(tmnlAddress).getTmnlAssetNo());
			db.setTmnlAddr(tmnlAddress);
			db.setTime(timeTag);
			db.setMark(0);
			return db;
		} catch (Exception ex) {
			log.error("",ex);
			return null;
		}
	}

	public Date class1time(byte rec[], int startp, int countType, int tdType, Date time) {
		Calendar m_calendar = Calendar.getInstance();
		if (time == null)
			m_calendar.setTimeInMillis(System.currentTimeMillis());
		else
			m_calendar.setTime(time);
		if (tdType == ClearCodeDef.TD_H) {
			if (countType == 3) {
				int tmpTime = 0;
				try {
					tmpTime = func.TransBcdToBinay(rec[startp] & 0x3f);
				} catch (Exception e) {
					log.error(e.toString());
					return null;
				}
				m_calendar.set(Calendar.HOUR_OF_DAY, tmpTime);
			}
		} else if (tdType == ClearCodeDef.TD_M) {
			m_calendar.set(Calendar.DAY_OF_MONTH, 1);
			m_calendar.set(Calendar.HOUR_OF_DAY, 0);
			m_calendar.set(Calendar.MINUTE, 0);
		} else if (tdType == ClearCodeDef.TD_D) {
			m_calendar.set(Calendar.MINUTE, 0);
			m_calendar.set(Calendar.HOUR_OF_DAY, 0);
		}
		return m_calendar.getTime();
	}

	public int getDataNum(byte byt) {
		int dataNum = 0;
		for (int k = 0; k < 8; k++) {
			byte temp = (byte) ((0x01 << k));
			dataNum += ((byt & temp) == 0 ? 0 : 1);
		}
		return dataNum;
	}

	public int getTd_hNum(int destiny) {
		switch (destiny) {
		case 0:// 不冻结
			return 0;
		case 1:// 15m
			return 4;
		case 2:// 30m
			return 2;
		case 3:// 60m
			return 1;
		case 254:// 5m
			return 12;
		case 255:// 1m
			return 60;
		}
		return -1;
	}

	public Date truncTimeTag(int cycleNum, Date timeTag) {
		Calendar m_calendar = Calendar.getInstance();
		m_calendar.setTime(timeTag);
		switch (cycleNum) {
		case 24:
			m_calendar.set(Calendar.MINUTE, 0);
			break;
		case 48:
			m_calendar.set(Calendar.MINUTE, m_calendar.get(Calendar.MINUTE) < 30 ? 0 : 30);
			break;
		case 96:
			m_calendar.set(Calendar.MINUTE, m_calendar.get(Calendar.MINUTE) / 15 * 15);
			break;
		case 288:
			m_calendar.set(Calendar.MINUTE, m_calendar.get(Calendar.MINUTE) / 5 * 5);
			break;
		}
		m_calendar.set(Calendar.SECOND, 0);
		m_calendar.set(Calendar.MILLISECOND, 0);
		return m_calendar.getTime();
	}

	public void changePostion(short fn, Short offset, String[] codeStr) throws Exception {
		if (offset == null || offset.shortValue() == 0)
			return;
		int[] afnC = shareGlobalObj.getAfn0c();
		if (afnC == null)
			return;
		boolean isChange = false;
		for (int i = 0; i < afnC.length; i++) {
			if (fn == afnC[i]) {
				isChange = true;
				break;
			}
		}
		if (isChange)
			if (fn == 33 || fn == 34)// 只对Fn=33,34进行费率调整
			{
				BErateOffset bErateOffset = shareGlobalObj.getErateOffsetById(offset.shortValue());
				if (bErateOffset == null) {
					log.debug("BErateOffset在缓存中记录为空");
					return;
				}
				log.debug("费率错位调整," + bErateOffset.getErateOffsetName());
				func.changedCodeStr(codeStr, bErateOffset);
			}
	}

	public static void main(String args[]) {
		GxExC1Data e1 = new GxExC1Data();
		byte testByt = (byte) 0xd3;
		int aa = e1.getDataNum(testByt);
		log.debug("aa=" + aa);
	}
}
