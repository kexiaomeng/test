package Protocol;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nari.ami.database.map.basicdata.BErateOffset;
import com.nari.ami.database.map.measurepoint.EDataMp;
import com.nari.ami.database.map.measurepoint.EDataTotal;
import com.nari.commObjectPara.ConstDef;
import com.nari.commObjectPara.TaskQueueObject;
import com.nari.commObjectPara.TerminalObject;
import com.nari.fe.commdefine.define.FrontConstant;
import com.nari.fe.commdefine.task.DataCode;
import com.nari.fe.commdefine.task.DbData;
import com.nari.fe.commdefine.task.Response;
import com.nari.global.ClearCodeDef;
import com.nari.global.ShareGlobalObj;
import com.nari.protocol.gx.Protocol.GxFunctions.C2DataUnit;

public class GxExC2Data {
	private GxFunctions func = null;
	private ShareGlobalObj shareGlobalObj = null;
	private static final Object lock0 = new Object[0];
	private static final Logger log = LoggerFactory.getLogger(GxExC2Data.class);

	public GxExC2Data() {
		func = new GxFunctions();
	}

	public Response explainData(TaskQueueObject taskObj) {
		log.debug("Fk04:ExplainClass2Data," + "任务ID:" + taskObj.getTermTask().getTaskId());
		int count = 0;
		boolean isAcd = false;// 判断ACD
		boolean isTpv = false;// 判断时间标签
		// int day, hour, minute, second;
		byte recBuf[] = null;
		String tmnlAddress = null;
		Response resPonse = new Response();
		ArrayList<DbData> dbDatasList = new ArrayList<DbData>();
		if (taskObj.getTaskMessage() == null) {
			resPonse.setDbDatas(null);
			return resPonse;
		}
		try {
			recBuf = taskObj.getTaskMessage();
			tmnlAddress = taskObj.getTermTask().getTerminalAddr();
			// StringBuffer sb = new StringBuffer();
			// for (int i = 0; i < recBuf.length; i++) {
			// sb.append(Integer.toHexString((func.getInt(recBuf[i]))));
			// sb.append(" ");
			// }
			// log.debug( sb.toString());
		} catch (Exception ex) {
			log.error("get taskObj error!!!" + "任务ID:" + taskObj.getTermTask().getTaskId());
		}
		int class2Length = func.getLength(recBuf[count + 1], recBuf[count + 2]);
		if (class2Length > 0
				&& class2Length == func.getLength(recBuf[count + 3], recBuf[count + 4])) {// 判断报文长度
		} else {
			taskObj.addLowLessReports(ConstDef.LOWLESS_LENGTH_ERROR, "class2Length=" + class2Length
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
		if (func.getInt(recBuf[count + 12]) != 13) {
			resPonse.setDbDatas(null);
			return resPonse;
		}
		// SEQ recBuf[count + 13]
		if ((func.getShort(recBuf[count + 13]) & 0x80) == 0x80) {// 判断是否有Tpv
			isTpv = true;
			log.debug("isTpv=" + isTpv + "任务ID:" + taskObj.getTermTask().getTaskId());
		}
		count = 14;
		int content_length = class2Length;

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
						count = getClass2Data(taskObj, recBuf, count, Pn[i], Fn[j], dbDatasList,
								tmnlAddress);
					} catch (ArrayIndexOutOfBoundsException ex) {
						taskObj.addLowLessReports(ConstDef.LOWLESS_INDEX_ERROR,
								"ArrayIndexOutOfBoundsException");
						resPonse.setDbDatas(null);
						return resPonse;
					}
					if (count < 0) {
						log.error("getC2Data.count<0!" + "任务ID:"
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
			// int delayTime = func.getInt(recBuf[count + 5]);
			// log.debug("ExC2Data",
			// "ExplainClass2Data,recbuf,pfc=" + PFC
			// + "Tp: second=" + second + ",minute=" + minute + ",hour=" + hour
			// + ",day="
			// + day + ",delayTime=" + delayTime);
			count += 6;
		}
		byte check = (byte) func.checkSum(recBuf, 6, class2Length);
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
		// for (int i = 0; i < dbDatasList.size(); i++) {
		// DbData db = (DbData) dbDatasList.get(i);
		// ArrayList<DataCode> dcArr = db.getDataCodes();
		// for (int j = 0; j < dcArr.size(); j++) {
		// DataCode dc = (DataCode) dcArr.get(j);
		// log.debug("ExC2Data", "dc.name()==" + dc.getName());
		// log.debug("ExC2Data", "dc.value()==" + dc.getValue());
		// }

		// }
		return resPonse;

	}

	public int getClass2Data(TaskQueueObject taskObj, byte buf[], int count, short Pn, short Fn,
			ArrayList<DbData> dbList, String tmnlAddress) {
		int data_num = -1;
		C2DataUnit du = func.getC2DataUnit(Fn);
		if (du == null) {
			log
					.error("getC2Data,fn:" + Fn + " error" + "任务ID:"
							+ taskObj.getTermTask().getTaskId());
			taskObj.addLowLessReports(ConstDef.LOWLESS_FN_ERROR, "getC2Data,fn:" + Fn + " error");
			return -1;
		}
		int pnType = du.getPnType();
		int tdType = du.getTdType();
		int countType = du.getCountType();
		int countContent = du.getCountContent();
		int bytLength = du.getBytLength();
		int valueType = du.getValueType();
		String codeStr[] = du.getCodeStr();
		int countFlag = getCountFlag(tdType);
		if (countFlag < 0 || checkPn(pnType, Pn) < 0 || codeStr == null) {
			log.error("getC2Data error" + "任务ID:" + taskObj.getTermTask().getTaskId());
			taskObj.addLowLessReports(ConstDef.LOWLESS_CHECK_ERROR,
					"countFlag < 0 || checkPn(pnType, Pn) < 0 || codeStr == null");
			return -1;
		}
		// 模拟dataid，如果要真实的，将下面一行去除
		// this.setForExPacket(true);
		Date collTimeTag = null;
		try {
			collTimeTag = class2time(buf, count, tdType);
		} catch (Exception e) {
			log.error(e.toString());
			taskObj.addLowLessReports(ConstDef.LOWLESS_CHECK_ERROR, e.toString());

			return -1;

		}
		String[] tmpCodeStr = new String[codeStr.length];
		for (int i = 0; i < codeStr.length; i++) {
			tmpCodeStr[i] = codeStr[i];
		}
		DbData db = initDbData(Fn, Pn, tmnlAddress, pnType, collTimeTag, countType, tdType,tmpCodeStr);
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
		ArrayList<DataCode> dcArray = null;
		count += countFlag;
		switch (countType) {
		case 0:
			if (Fn == 32 || (Fn >= 153 && Fn <= 160) || (Fn >= 209 && Fn <= 215))
				count += 5;
			dcArray = getDataCodes(taskObj.getTermTask().getTaskId(), buf, count, Fn, data_num,
					tmpCodeStr, collTimeTag);
			if (dcArray == null || dcArray.isEmpty()) {
				log.error("dcArray=null or empty" + "任务ID:" + taskObj.getTermTask().getTaskId());
				return -1;
			}
			db.setDataCodes(dcArray);
			dbList.add(db);
			count += countContent;
			break;
		case 1:
			count += 5;
			data_num = func.getInt(buf[count]);// 数据个数m
			if (data_num < 0 || data_num > 12) {
				log.error("getC2Data,data_num error,Fn=" + Fn + "任务ID:"
						+ taskObj.getTermTask().getTaskId());
				return -1;
			}
			count += 1;
			dcArray = getDataCodes(taskObj.getTermTask().getTaskId(), buf, count, Fn, data_num,
					tmpCodeStr, collTimeTag);
			if (dcArray == null || dcArray.isEmpty()) {
				log.error("dcArray=null or empty" + "任务ID:" + taskObj.getTermTask().getTaskId());
				return -1;
			}
			db.setDataCodes(dcArray);
			dbList.add(db);
			count += bytLength + bytLength * data_num;
			if (Fn == 216)
				count += 5;
			break;
		case 2:
			data_num = func.getInt(buf[count]);
			if (data_num < 0 || data_num > 12) {
				log.error("getC2Data,data_num error,Fn=" + Fn + "任务ID:"
						+ taskObj.getTermTask().getTaskId());
				return -1;
			}
			count += 1;
			dcArray = getDataCodes(taskObj.getTermTask().getTaskId(), buf, count, Fn, data_num,
					tmpCodeStr, collTimeTag);
			if (dcArray == null || dcArray.isEmpty()) {
				log.error("dcArray=null or empty" + "任务ID:" + taskObj.getTermTask().getTaskId());
				return -1;
			}
			db.setDataCodes(dcArray);
			dbList.add(db);
			count += 4 + 4 * data_num;
			break;
		case 3:
			db.setDataType(FrontConstant.CURVE_DATA);
			if (pnType == ClearCodeDef.ZJ_TYPE) {
				db.setDataType(FrontConstant.TOTAL_CURVE);
			}
			db.setValueType(valueType);
			int m = func.getInt(buf[count]);// 数据冻结密度m
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
			int n = func.getInt(buf[count + 1]);// 数据点数n
			count += 2;
			dcArray = getDataCodes(taskObj.getTermTask().getTaskId(), buf, count, Fn, n, tmpCodeStr,
					collTimeTag);
			if (dcArray == null || dcArray.isEmpty()) {
				log.error("dcArray=null or empty" + "任务ID:" + taskObj.getTermTask().getTaskId());
				return -1;
			}
			db.setDataCodes(dcArray);
			dbList.add(db);
			count += n * bytLength;
			break;
		case 4:
			int harmonicNum = func.getInt(buf[count]);// 谐波次数n
			if (harmonicNum < 0 || harmonicNum > 19) {
				log.error("harmonicNum error,Fn=" + Fn + "任务ID:"
						+ taskObj.getTermTask().getTaskId());
				return -1;
			}
			count += 1;
			dcArray = getDataCodes(taskObj.getTermTask().getTaskId(), buf, count, Fn, harmonicNum,
					tmpCodeStr, collTimeTag);
			if (dcArray == null || dcArray.isEmpty()) {
				log.error("dcArray=null or empty" + "任务ID:" + taskObj.getTermTask().getTaskId());
				return -1;
			}
			db.setDataCodes(dcArray);
			dbList.add(db);
			count += 38 + 2 * harmonicNum;
			break;
		default:
			log.error("countType:" + countType + " error!" + "任务ID:"
					+ taskObj.getTermTask().getTaskId());
			return -1;
		}
		return count;
	}

	public ArrayList<DataCode> getDataCodes(long taskId, byte[] buf, int startp, short Fn, int num,
			String[] codeString, Date collTimeTag) {
		try {
			DataCode dc = null;
			ArrayList<DataCode> dataCodes = new ArrayList<DataCode>();
			switch (Fn) {
			case 1:
			case 2:
			case 9:
			case 10:
			case 17:
			case 18:
				dc = getDataCode(func.getDate15(buf, startp - 6), codeString[53]);
				dataCodes.add(dc);
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
			case 3:
			case 4:
			case 11:
			case 12:
			case 19:
			case 20:
				dc = getDataCode(func.getDate15(buf, startp - 6), codeString[53]);
				dataCodes.add(dc);
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
			case 5:
			case 6:
			case 7:
			case 8:
			case 21:
			case 22:
			case 23:
			case 24:
			case 58:
			case 59:
			case 61:
			case 62:
				if (Fn < 58)
					for (int j = 0; j < num + 1; j++) {
						dc = getDataCode(func.TransData13(buf, startp), codeString[j]);
						dataCodes.add(dc);
						startp += 4;
					}
				else
					for (int j = 0; j < num + 1; j++) {
						dc = getDataCode(func.TransData3(buf, startp), codeString[j]);
						if(Fn == 58){
							dc.setValue(.0 + (Long)dc.getValue());
						}
						dataCodes.add(dc);
						startp += 4;
					}
				break;
			case 25:
			case 26:
			case 33:
			case 34:
				if (Fn == 33) {
					for (int j = 1; j < 4; j++) {
						dc = getDataCode(func.TransData9(buf, startp), codeString[2 * j]);
						dataCodes.add(dc);
						startp += 3;
						dc = getDataCode(func.getDate18(buf, startp, collTimeTag),
								codeString[2 * j + 1]);
						dataCodes.add(dc);
						startp += 3;
					}
					for (int j = 1; j < 4; j++) {
						int zeroTime = 0;
						if ((buf[startp] == (byte) (0xee)) && (buf[startp + 1] == (byte) (0xee))) {
							zeroTime = -9999;
						} else {
							zeroTime += func.getInt(buf[startp]);
							zeroTime += (func.getInt(buf[startp + 1]) << 8);
						}
						dc = getDataCode(zeroTime, codeString[j + 8]);
						dataCodes.add(dc);
						startp += 2;
					}
				} else {
					for (int j = 0; j < 4; j++) {
						dc = getDataCode(func.TransData9(buf, startp), codeString[2 * j]);
						dataCodes.add(dc);
						startp += 3;
						dc = getDataCode(func.getDate18(buf, startp, collTimeTag),
								codeString[2 * j + 1]);
						dataCodes.add(dc);
						startp += 3;
					}
					if (Fn == 25) {
						for (int j = 0; j < 4; j++) {
							int zeroTime = 0;
							if ((buf[startp] == (byte) (0xee))
									&& (buf[startp + 1] == (byte) (0xee))) {
								zeroTime = -9999;
							} else {
								zeroTime += func.getInt(buf[startp]);
								zeroTime += (func.getInt(buf[startp + 1]) << 8);
							}
							dc = getDataCode(zeroTime, codeString[j + 8]);
							dataCodes.add(dc);
							startp += 2;
						}
					}
				}
				break;
			case 27:
			case 35:
				for (int j = 0; j < 15; j++) {
					int sj_temp = 0;
					if ((buf[startp] == (byte) (0xee)) && (buf[startp + 1] == (byte) (0xee))) {
						sj_temp = -9999;
					} else {
						sj_temp += func.getInt(buf[startp]);
						sj_temp += (func.getInt(buf[startp + 1]) << 8);
					}
					dc = getDataCode(sj_temp, codeString[j]);
					dataCodes.add(dc);
					startp += 2;
				}
				for (int j = 0; j < 6; j++) {
					dc = getDataCode(func.TransData7(buf, startp), codeString[2 * j + 15]);
					dataCodes.add(dc);
					startp += 2;
					dc = getDataCode(func.getDate18(buf, startp, collTimeTag),
							codeString[2 * j + 16]);
					dataCodes.add(dc);
					startp += 3;
				}
				for (int j = 0; j < 3; j++) {
					dc = getDataCode(func.TransData7(buf, startp), codeString[j + 27]);
					dataCodes.add(dc);
					startp += 2;
				}
				break;
			case 28:
			case 36:
				for (int j = 0; j < 2; j++) {
					int yxsj_temp = 0;
					if ((buf[startp] == (byte) (0xee)) && (buf[startp + 1] == (byte) (0xee))) {
						yxsj_temp = -9999;
					} else {
						yxsj_temp += func.getInt(buf[startp]);
						yxsj_temp += (func.getInt(buf[startp + 1]) << 8);
					}
					dc = getDataCode(yxsj_temp, codeString[j]);
					dataCodes.add(dc);
					startp += 2;
				}
				break;
			case 29:
			case 37:
				for (int j = 0; j < 7; j++) {
					int ljsj_temp = 0;
					if ((buf[startp] == (byte) (0xee)) && (buf[startp + 1] == (byte) (0xee))) {
						ljsj_temp = -9999;
					} else {
						ljsj_temp += func.getInt(buf[startp]);
						ljsj_temp += (func.getInt(buf[startp + 1]) << 8);
					}
					dc = getDataCode(ljsj_temp, codeString[j]);
					dataCodes.add(dc);
					startp += 2;
				}
				for (int j = 0; j < 4; j++) {
					dc = getDataCode(func.TransData6(buf, startp), codeString[2 * j + 7]);
					dataCodes.add(dc);
					startp += 2;
					dc = getDataCode(func.getDate18(buf, startp, collTimeTag),
							codeString[2 * j + 8]);
					dataCodes.add(dc);
					startp += 3;
				}
				break;
			case 30:
			case 38:
			case 43:
			case 44:
			case 49:
			case 51:
				int ii = (Fn == 43 || Fn == 44) ? 3 : 2;
				for (int j = 0; j < ii; j++) {
					int ljsj_temp = 0;
					if ((buf[startp] == (byte) (0xee)) && (buf[startp + 1] == (byte) (0xee))) {
						ljsj_temp = -9999;
					} else {
						ljsj_temp += func.getInt(buf[startp]);
						ljsj_temp += (func.getInt(buf[startp + 1]) << 8);
					}
					dc = getDataCode(ljsj_temp, codeString[j]);
					dataCodes.add(dc);
					startp += 2;
				}
				break;
			case 31:
				for (int j = 0; j < 4; j++) {
					dc = getDataCode(func.TransData8(buf, startp), codeString[j]);
					dataCodes.add(dc);
					startp += 2;
				}
				for (int j = 0; j < 4; j++) {
					dc = getDataCode(func.TransData10(buf, startp), codeString[j + 4]);
					dataCodes.add(dc);
					startp += 3;
				}
				for (int j = 0; j < 8; j++) {
					dc = getDataCode(func.getDate17(buf, startp), codeString[j + 8]);
					dataCodes.add(dc);
					startp += 4;
				}
				break;
			case 41:
				for (int j = 0; j < 18; j++) {
					long trTemp = 0;
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
			case 42:
				for (int j = 0; j < 2; j++) {
					dc = getDataCode(func.TransData13(buf, startp), codeString[j]);
					dataCodes.add(dc);
					startp += 4;
				}
				break;
			case 50:
			case 52:
				for (int j = 0; j < 4; j++) {
					dc = getDataCode(func.getInt(buf[startp]), codeString[j]);
					dataCodes.add(dc);
					startp += 1;
				}
				break;
			case 57:
			case 60:
				for (int j = 0; j < 2; j++) {
					dc = getDataCode(func.TransData2(buf, startp), codeString[2 * j]);
					dataCodes.add(dc);
					startp += 2;
					dc = getDataCode(func.getDate18(buf, startp, collTimeTag),
							codeString[2 * j + 1]);
					dataCodes.add(dc);
					startp += 3;
				}
				int sumTime = 0;
				if ((buf[startp] == (byte) (0xee)) && (buf[startp + 1] == (byte) (0xee))) {
					sumTime = -9999;
				} else {
					sumTime += func.getInt(buf[startp]);
					sumTime += (func.getInt(buf[startp + 1]) << 8);
				}
				dc = getDataCode(sumTime, codeString[4]);
				dataCodes.add(dc);
				startp += 2;
				break;
			case 65:
			case 66:
				int ljsj_temp = 0;
				if ((buf[startp] == (byte) (0xee)) && (buf[startp + 1] == (byte) (0xee))) {
					ljsj_temp = -9999;
				} else {
					ljsj_temp += func.getInt(buf[startp]);
					ljsj_temp += (func.getInt(buf[startp + 1]) << 8);
				}
				dc = getDataCode(ljsj_temp, codeString[0]);
				dataCodes.add(dc);
				startp += 2;
				dc = getDataCode(func.TransData3(buf, startp), codeString[1]);
				dataCodes.add(dc);
				startp += 4;
				break;
			case 73:
			case 74:
			case 138:
				double value[] = new double[num];
				for (int j = 0; j < num; j++) {
					value[j] = func.TransData2(buf, startp);
					startp += 2;
				}
				dc = getDataCode(value, codeString[0]);
				dataCodes.add(dc);
				break;
			case 75:
			case 76:
				//mody gaolx change data type double to long
				long[] lValue = new long[num];
				for (int j = 0; j < num; j++) {
					lValue[j] = func.TransData3(buf, startp);
					startp += 4;
				}
				dc = getDataCode(lValue, codeString[0]);
				dataCodes.add(dc);
				break;
			case 81:
			case 82:
			case 83:
			case 84:
			case 85:
			case 86:
			case 87:
			case 88:
				value = new double[num];
				for (int j = 0; j < num; j++) {
					value[j] = func.TransData9(buf, startp);
					startp += 3;
				}
				dc = getDataCode(value, codeString[0]);
				dataCodes.add(dc);
				break;
			case 89:
			case 90:
			case 91:
				value = new double[num];
				for (int j = 0; j < num; j++) {
					value[j] = func.TransData7(buf, startp);
					startp += 2;
				}
				dc = getDataCode(value, codeString[0]);
				dataCodes.add(dc);
				break;
			case 92:
			case 93:
			case 94:
			case 95:
				value = new double[num];
				for (int j = 0; j < num; j++) {
					value[j] = func.TransData6(buf, startp);
					startp += 2;
				}
				dc = getDataCode(value, codeString[0]);
				dataCodes.add(dc);
				break;
			case 97:
			case 98:
			case 99:
			case 100:
				value = new double[num];
				for (int j = 0; j < num; j++) {
					value[j] = func.TransData13(buf, startp);
					startp += 4;
				}
				dc = getDataCode(value, codeString[0]);
				dataCodes.add(dc);
				break;
			case 101:
			case 102:
			case 103:
			case 104:
				value = new double[num];
				for (int j = 0; j < num; j++) {
					value[j] = func.TransData11(buf, startp);
					startp += 4;
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
					value[j] = func.TransData5(buf, startp);
					startp += 2;
				}
				dc = getDataCode(value, codeString[0]);
				dataCodes.add(dc);
				break;
			case 113:
			case 114:
			case 115:
				for (int j = 0; j < 19; j++) {
					dc = getDataCode(func.TransData6(buf, startp), codeString[2 * j]);
					dataCodes.add(dc);
					startp += 2;
					dc = getDataCode(func.getDate17(buf, startp), codeString[2 * j + 1]);
					dataCodes.add(dc);
					startp += 4;
				}
				break;
			case 116:
			case 117:
			case 118:
				for (int j = 0; j < 19; j++) {
					dc = getDataCode(func.TransData5(buf, startp), codeString[2 * j]);
					dataCodes.add(dc);
					startp += 2;
					dc = getDataCode(func.getDate17(buf, startp), codeString[2 * j + 1]);
					dataCodes.add(dc);
					startp += 4;
				}
				break;
			case 121:
			case 122:
			case 123:
				for (int j = 0; j < 19 + num; j++) {
					int yxsj_temp = 0;
					if ((buf[startp] == (byte) (0xee)) && (buf[startp + 1] == (byte) (0xee))) {
						yxsj_temp = -9999;
					} else {
						yxsj_temp += func.getInt(buf[startp]);
						yxsj_temp += (func.getInt(buf[startp + 1]) << 8);
					}
					dc = getDataCode(yxsj_temp, codeString[j]);
					dataCodes.add(dc);
					startp += 2;
				}
				break;
			case 129:
			case 130:
				for (int j = 0; j < 2; j++) {
					int yxsj_temp = 0;
					if ((buf[startp] == (byte) (0xee)) && (buf[startp + 1] == (byte) (0xee))) {
						yxsj_temp = -9999;
					} else {
						yxsj_temp += func.getInt(buf[startp]);
						yxsj_temp += (func.getInt(buf[startp + 1]) << 8);
					}
					dc = getDataCode(yxsj_temp, codeString[j]);
					dataCodes.add(dc);
					startp += 2;
				}
				for (int j = 0; j < 2; j++) {
					dc = getDataCode(func.TransData2(buf, startp), codeString[2 * j + 2]);
					dataCodes.add(dc);
					startp += 2;
					dc = getDataCode(func.getDate18(buf, startp, collTimeTag),
							codeString[2 * j + 3]);
					dataCodes.add(dc);
					startp += 3;
				}
				break;
			default:
				log.debug("fn error!,fn=" + Fn);
				return null;
			}
			return dataCodes;
		} catch (Exception ex) {
			log.error("getDataCodes error!,fn=" + Fn + "任务ID:" + taskId);
			return null;
		}
	}

	public DataCode getDataCode(Object obj, String str) {
		DataCode dac = new DataCode();
		dac.setValue(obj);
		dac.setName(str);
		return dac;
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
			log.error("getFns error" + "任务ID:" + taskId);
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
		} catch (ArrayIndexOutOfBoundsException ex) {
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

	public Date class2time(byte[] recBuf, int startp, int tdtype) throws Exception {
		int year = 2000, month = 0, day = 1, hour = 0, minute = 0;
		Date time = new Date();
		Calendar m_calendar = Calendar.getInstance();
		m_calendar.setTime(time);
		switch (tdtype) {
		case ClearCodeDef.TD_D:
			day = func.TransBcdToBinay(recBuf[startp]);
			month = func.TransBcdToBinay(recBuf[startp + 1]) - 1;
			year = func.TransBcdToBinay(recBuf[startp + 2]) + 2000;
			break;
		case ClearCodeDef.TD_M:
			month = func.TransBcdToBinay(recBuf[startp]) - 1;
			year = func.TransBcdToBinay(recBuf[startp + 1]) + 2000;
			break;
		case ClearCodeDef.TD_C:
			minute = func.TransBcdToBinay(recBuf[startp]);
			hour = func.TransBcdToBinay(recBuf[startp + 1]);
			day = func.TransBcdToBinay(recBuf[startp + 2]);
			month = func.TransBcdToBinay(recBuf[startp + 3]) - 1;
			year = func.TransBcdToBinay(recBuf[startp + 4]) + 2000;
			break;
		case ClearCodeDef.TD_H:
		}

		m_calendar.set(Calendar.SECOND, 0);
		m_calendar.set(Calendar.MINUTE, minute);
		m_calendar.set(Calendar.HOUR_OF_DAY, hour);
		m_calendar.set(Calendar.DAY_OF_MONTH, day);
		m_calendar.set(Calendar.MONTH, month);
		m_calendar.set(Calendar.YEAR, year);
		time = m_calendar.getTime();
		return time;
	}

	public int getCountFlag(int tdType) {
		switch (tdType) {
		case ClearCodeDef.TD_D:
			return 3;
		case ClearCodeDef.TD_M:
			return 2;
		case ClearCodeDef.TD_C:
			return 5;
		default:
			return -1;
		}
	}

	public DbData initDbData(short Fn, short Pn, String tmnlAddress, int pnType, Date timeTag,
			int countType, int tdType,String[] codeString) {
		// for test
		// synchronized (lock0) {
		// DbData db = new DbData();
		// db.setAreaCode(areaCode);
		// db.setDataId(System.currentTimeMillis() + ClearCodeDef.tmp++);
		// db.setMark(0);
		// db.setCT(1);
		// db.setPT(1);
		// db.setTime(timeTag);
		// return db;
		// }

		try {
			shareGlobalObj = ShareGlobalObj.getInstance();
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
				changePostion(Fn, eDataMp.getErateOffset(), codeString);
				break;
			}
			/*OOrg oorg = shareGlobalObj.getOrgPara(orgNo);
			if (oorg != null) {
				db.setAreaCode(oorg.getAreaCode());
			}*/
			db.setOrgNo(orgNo);
			TerminalObject tmpTermObj = shareGlobalObj.getTerminalPara(tmnlAddress);
			if (tmpTermObj != null) {
				db.setTmnlAssetNo(tmpTermObj.getTmnlAssetNo());
				db.setTmnlAddr(tmpTermObj.getTerminalAllAddr());
				if (countType != ClearCodeDef.TD_C && tdType != ClearCodeDef.TD_M) // 2类曲线数据,月冻结数据时标不处理
					timeTag = func.TimeAfterProcess(tmpTermObj, (short)(Fn+500),timeTag,countType);
			}
			db.setTime(timeTag);
			db.setPn(Pn);
			db.setMark(0);
			return db;
		} catch (Exception ex) {
			log.error("",ex);
			return null;
		}
	}

	public void changePostion(short fn, Short offset, String[] codeStr) throws Exception {
		if (offset == null || offset.shortValue() == 0)
			return;
		int[] afnD = shareGlobalObj.getAfn0d();
		if (afnD == null)
			return;
		boolean isChange = false;
		for (int i = 0; i < afnD.length; i++) {
			if (fn == afnD[i]) {
				isChange = true;
				break;
			}
		}
		if (isChange)
			if (fn >= 1 && fn <= 4)// 只对Fn=1,2,3,4进行费率调整
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

}
