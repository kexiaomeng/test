package Protocol;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.nari.protocol.gx.Protocol.GxFunctions.C3DataUnit;

public class GxExC3Data {
	private GxFunctions func = null;
	private ShareGlobalObj shareGlobalObj = null;
	private static final Logger log = LoggerFactory.getLogger(GxExC3Data.class);
	private byte EC1;
	private byte EC2;

	public GxExC3Data() {
		func = new GxFunctions();
	}

	public Response explainData(TaskQueueObject taskObj) {
		log.debug("Fk04:ExplainClass3Data----------");
		int count = 0;
		int dt1 = 0;
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
			// log.debug(sb.toString());
		} catch (Exception ex) {
			log.error("get taskObj error!!!");
		}
		int class3Length = func.getLength(recBuf[count + 1], recBuf[count + 2]);
		if (class3Length > 0
				&& class3Length == func.getLength(recBuf[count + 3], recBuf[count + 4])) {// 判断报文长度
		} else {
			taskObj.addLowLessReports(ConstDef.LOWLESS_LENGTH_ERROR, "class3Length=" + class3Length
					+ " error");
			resPonse.setDbDatas(null);
			return resPonse;
		}
		if ((func.getShort(recBuf[count + 6]) & 0x20) == 0x20) {// 判断ACD=1的情况，以后扩充
			isAcd = true;
			log.debug("isAcd=" + isAcd);
		}
		if ((func.getShort(recBuf[count + 6]) & 0x0f) == 0x09) {// 否认，没有数据
			resPonse.setDbDatas(null);
			return resPonse;
		}
		if (func.getInt(recBuf[count + 12]) != 14) {
			resPonse.setDbDatas(null);
			return resPonse;
		}
		// SEQ recBuf[count + 13]
		if ((func.getShort(recBuf[count + 13]) & 0x80) == 0x80) {// 判断是否有Tpv
			isTpv = true;
			log.debug("isTpv=" + isTpv);
		}
		int content_length = class3Length;
		if (isAcd)
			content_length -= 2;
		if (isTpv)
			content_length -= 6;
		count = 14;
		while (count < content_length + 6) {
			dt1 = func.getInt(recBuf[count + 2]);
			if (func.getInt(recBuf[count]) != 0 || func.getInt(recBuf[count + 1]) != 0
					|| func.getInt(recBuf[count + 3]) != 0 || dt1 > 3 || dt1 < 1) {
				resPonse.setDbDatas(null);
				return resPonse;
			}
			count += 4;
			int loopNo = dt1 / 3 + 1;// 里面的F1和F2组合
			for (int i = 0; i < loopNo; i++) {
				EC1 = recBuf[count];
				EC2 = recBuf[count + 1];
				int pm = func.getInt(recBuf[count + 2]);
				int pn = func.getInt(recBuf[count + 3]);
				log.debug("EC1=" + EC1 + ",EC2=" + EC2 + "任务ID:"
						+ taskObj.getTermTask().getTaskId());
				int eventNum = (pm <= pn ? (pn - pm) : (256 + pn - pm));// 相等按0处理！
				if (eventNum <= 0) {
					resPonse.setDbDatas(null);
					return resPonse;
				}
				count += 4;
				for (int event_no = 0; event_no < eventNum; event_no++) {
					try {
						short eventType = func.getShort(recBuf[count]);// ERC类型
						DbData dbData = null;
						if (eventType > 30) {
							count += func.getInt(recBuf[count + 1]) + 2;// LE+2
							continue;
						}
						dbData = getEventDataByType(taskObj, recBuf, eventType, count, tmnlAddress);
						count += func.getInt(recBuf[count + 1]) + 2;// LE + 2
						if (dbData == null) {
							log.error("ERC type=" + eventType + ",count=" + count
									+ "getEventDataByType is null!");
							taskObj
									.addLowLessReports(ConstDef.LOWLESS_CHECK_ERROR, "ERC type="
											+ eventType + ",count=" + count
											+ "getEventDataByType is null!");
							continue;
						}
						dbDatasList.add(dbData);
					} catch (Exception ex) {
						resPonse.setDbDatas(null);
						
						log.error(ex.toString());
						taskObj
								.addLowLessReports(ConstDef.LOWLESS_CHECK_ERROR, ex.toString());
						
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
			// log.debug("ExC3Data,recbuf,EC1=" + EC1 + ",EC2=" + EC2);
			count += 2;// EC
		}
		if (isTpv) {
			// int PFC = func.getInt(recBuf[count]);
			// second = func.TransBcdToBinay(recBuf[count + 1]);
			// minute = func.TransBcdToBinay(recBuf[count + 2]);
			// hour = func.TransBcdToBinay(recBuf[count + 3]);
			// day = func.TransBcdToBinay(recBuf[count + 4]);
			// int delayTime = func.getInt(recBuf[count + 5]);
			// log.debug("ExC3Data,in recbuf,pfc=" + PFC +
			// "Tp: second=" + second
			// + ",minute=" + minute + ",hour=" + hour + ",day=" + day +
			// ",delayTime="
			// + delayTime);
			count += 6;
		}
		byte check = (byte) func.checkSum(recBuf, 6, class3Length);
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

		TerminalObject tmpTermObj = ShareGlobalObj.getInstance().getTerminalPara(tmnlAddress);
		if (tmpTermObj != null) {
			tmpTermObj.setTerminalEC1(tmpTermObj.getTerminalReadEC1());
			tmpTermObj.setTerminalEC2(tmpTermObj.getTerminalReadEC2());
			tmpTermObj.setTerminalReadEC1(EC1);
			tmpTermObj.setTerminalReadEC2(EC2);

		}
		return resPonse;

	}

	public DbData getEventDataByType(TaskQueueObject taskObj, byte[] recBuf, short eventType,
			int startp, String tmnlAddress) throws Exception  {
		DataCode dc = null;
		ArrayList<DataCode> dataCodes = new ArrayList<DataCode>();
		Date occurTime = null;
		try{
			occurTime = func.getDate15(recBuf, startp + 2);
		}
		catch(Exception e){
			log.error(e.toString());
			return null;
		}
		C3DataUnit du = func.getC3DataUnit(eventType);
		if (du == null) {
			log.error("getC3Data,eventType:" + eventType + " error");
			return null;
		}
		String codeStr[] = du.getCodeStr();
		if (codeStr == null) {
			log.error("getC3Data,codeStr=null");
			return null;
		}
		int pnType = du.getPnType();
		log.debug("ERC type=" + eventType + ",codeStr.length=" + codeStr.length);
		String valueStr[] = new String[codeStr.length];
		StringBuffer strBuf = null;
		short tmpPn = 0;
		short isStart = 1;
		int fromType = 1;
		switch (eventType) {
		case 1:
		case 2:
			strBuf = func.transBS8ToStrBuffer(recBuf[startp + 7]);// 低位~高位
			valueStr[0] = strBuf.substring(0, 1);
			valueStr[1] = strBuf.substring(1, 2);
			if (eventType == 1) {
				valueStr[2] = (func.AsciiToString(recBuf, startp + 8, 4));
				valueStr[3] = (func.AsciiToString(recBuf, startp + 12, 4));
			}

			break;
		case 3:
			int paramChangeNum = (func.getInt(recBuf[startp + 1]) - 6) >> 2;// 参数变更记录数
			valueStr[0] = Integer.toString(func.getInt(recBuf[startp + 7]));// 启动站地址
			dc = getDataCode(valueStr[0], codeStr[0]);
			dataCodes.add(dc);
			strBuf = new StringBuffer();
			for (int i = 0; i < paramChangeNum; i++) {
				strBuf.delete(0, strBuf.length());
				strBuf.append(String.format("%02X", recBuf[startp + 8 + i * 4]));
				strBuf.append(String.format("%02X", recBuf[startp + 9 + i * 4]));
				strBuf.append(String.format("%02X", recBuf[startp + 10 + i * 4]));
				strBuf.append(String.format("%02X", recBuf[startp + 11 + i * 4]));
				valueStr[1] = strBuf.toString();
				dc = getDataCode(valueStr[1], codeStr[1]);
				dataCodes.add(dc);
			}

			break;
		case 4:
			valueStr[0] = func.transBS8ToStrBuffer(recBuf[startp + 7]).toString();// 状态变化
			valueStr[1] = func.transBS8ToStrBuffer(recBuf[startp + 8]).toString();// 变化后状态
			break;
		case 5:
			valueStr[0] = func.transBS8ToStrBuffer(recBuf[startp + 7]).toString();// 跳闸轮次
			valueStr[1] = Double.toString(func.TransData2(recBuf, startp + 8));// 跳闸时功率
			valueStr[2] = Double.toString(func.TransData2(recBuf, startp + 10)); // 跳闸后功率
			break;
		case 6:
			fromType = 2;
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			valueStr[0] = Short.toString(tmpPn);// 总加组号
			valueStr[1] = func.transBS8ToStrBuffer(recBuf[startp + 8]).toString();// 跳闸轮次
			valueStr[2] = func.transBS8ToStrBuffer(recBuf[startp + 9]).substring(0, 4);// 功控类别
			valueStr[3] = Double.toString(func.TransData2(recBuf, startp + 10));// 跳闸前功率
			valueStr[4] = Double.toString(func.TransData2(recBuf, startp + 12));// 跳闸后功率
			valueStr[5] = Double.toString(func.TransData2(recBuf, startp + 14));// 跳闸时功率定值
			break;
		case 7:
			fromType = 2;
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			valueStr[0] = Short.toString(tmpPn);// 总加组号
			valueStr[1] = func.transBS8ToStrBuffer(recBuf[startp + 8]).toString();// 跳闸轮次
			valueStr[2] = func.transBS8ToStrBuffer(recBuf[startp + 9]).substring(0, 2);// 电控类别
			valueStr[3] = Long.toString(func.TransData3(recBuf, startp + 10));// 跳闸时电能量
			valueStr[4] = Long.toString(func.TransData3(recBuf, startp + 14));// 跳闸时电能量定值
			break;
		case 8:
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			valueStr[0] = Short.toString(tmpPn);// 测量点号
			valueStr[1] = func.transBS8ToStrBuffer(recBuf[startp + 8]).substring(0, 6);// 变更标志
			break;
		case 9:
		case 10:
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			isStart = (short) ((recBuf[startp + 7] >> 7) & 0x01);
			valueStr[0] = Short.toString(isStart);// 起止标志
			valueStr[1] = Short.toString(tmpPn);// 测量点号
			strBuf = func.transBS8ToStrBuffer(recBuf[startp + 9]);
			valueStr[2] = strBuf.substring(6, 8);// 异常标志（电压回路的异常类型）
			valueStr[3] = strBuf.substring(0, 3);// 异常标志(A、B、C相位)
			// UaUab,Ub,UcUcb,Ia,Ib,Ic
			for (int j = 0; j < 3; j++) {
				valueStr[4 + j] = Double.toString(func.TransData7(recBuf, startp + 10 + j * 2));
				valueStr[7 + j] = Double.toString(func.TransData6(recBuf, startp + 16 + j * 2));
			}
			valueStr[10] = Double.toString(func.TransData14(recBuf, startp + 21));// 正向有功总电能示值
			break;
		case 11:
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			isStart = (short) ((recBuf[startp + 7] >> 7) & 0x01);
			valueStr[0] = Short.toString(isStart);// 起止标志
			valueStr[1] = Short.toString(tmpPn);// 测量点号
			// UaUab,Ub,UcUcb,Ia,Ib,Ic
			for (int j = 0; j < 6; j++) {
				valueStr[2 + j] = Double.toString(func.TransData5(recBuf, startp + 8 + j * 2));
			}
			valueStr[8] = Double.toString(func.TransData14(recBuf, startp + 20));// 正向有功总电能示值
			break;
		case 12:
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			isStart = (short) ((recBuf[startp + 7] >> 7) & 0x01);
			valueStr[0] = Short.toString(isStart);// 起止标志
			valueStr[1] = Short.toString(tmpPn);// 测量点号
			break;
		case 13:
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			valueStr[0] = Short.toString(tmpPn);// 测量点号
			valueStr[1] = func.transBS8ToStrBuffer(recBuf[startp + 8]).substring(0, 5);// 异常标志
			break;
		case 14:
			Date tmpDate = func.getDate15(recBuf, startp + 2);
			valueStr[1] = getDateFormatStr(tmpDate);  //停电时间
			tmpDate = func.getDate15(recBuf, startp + 7);
			valueStr[0] = getDateFormatStr(tmpDate);  //上电时间
			if(tmpDate.equals(func.getInvalideTime())){
				//如果上电时间为无效，则认为此事件上送的是停电事件（wj定义2012-11-8）
				isStart = 1;
			}else{
				isStart = 0;
			}
//			valueStr[0] = getDateFormatStr(func.getDate15(recBuf, startp + 7));
			break;
		case 15:
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			isStart = (short) ((recBuf[startp + 7] >> 7) & 0x01);
			valueStr[0] = Short.toString(isStart);// 起止标志
			valueStr[1] = Short.toString(tmpPn);// 测量点号
			strBuf = func.transBS8ToStrBuffer(recBuf[startp + 8]);
			valueStr[2] = strBuf.substring(7, 8);// 异常标志(谐波越限事件)0:电压,1:电流
			valueStr[3] = strBuf.substring(0, 3);// 异常标志(对应相位)
			strBuf = func.transBS8ToStrBuffer(recBuf[startp + 9]);
			strBuf.append(func.transBS8ToStrBuffer(recBuf[startp + 10]).toString());
			strBuf.append(func.transBS8ToStrBuffer(recBuf[startp + 11]).substring(0, 3));
			valueStr[4] = strBuf.toString();// 谐波越限标志
			for (int j = 0; j < 19; j++) {
				int ii = 2 * j;
				if (Integer.parseInt(valueStr[2]) == 0) // 电压含有率
					valueStr[5 + ii] = Double.toString(func.TransData5(recBuf, startp + 12 + ii));
				else
					// 电流有效值
					valueStr[6 + ii] = Double.toString(func.TransData6(recBuf, startp + 12 + ii));
			}
			break;
		case 16:
			fromType = 4;
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			isStart = (short) ((recBuf[startp + 7] >> 7) & 0x01);
			valueStr[0] = Short.toString(isStart);// 起止标志
			valueStr[1] = Short.toString(tmpPn);// 端口号1~64
			valueStr[2] = func.transBS8ToStrBuffer(recBuf[startp + 8]).substring(0, 2);// 越限标志
			valueStr[3] = Double.toString(func.TransData2(recBuf, startp + 9));// 直流模拟量
			break;
		case 17:
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			isStart = (short) ((recBuf[startp + 7] >> 7) & 0x01);
			valueStr[0] = Short.toString(isStart);// 起止标志
			valueStr[1] = Short.toString(tmpPn);// 测量点号
			valueStr[2] = func.transBS8ToStrBuffer(recBuf[startp + 8]).substring(0, 2);// 异常标志
			valueStr[3] = Double.toString(func.TransData5(recBuf, startp + 9));// 电压不平衡度
			valueStr[4] = Double.toString(func.TransData5(recBuf, startp + 11));// 电流不平衡度
			valueStr[5] = Double.toString(func.TransData7(recBuf, startp + 13));
			valueStr[6] = Double.toString(func.TransData7(recBuf, startp + 15));
			valueStr[7] = Double.toString(func.TransData7(recBuf, startp + 17));
			valueStr[8] = Double.toString(func.TransData6(recBuf, startp + 19));
			valueStr[9] = Double.toString(func.TransData6(recBuf, startp + 21));
			valueStr[10] = Double.toString(func.TransData6(recBuf, startp + 23));
			break;
		case 18:
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			isStart = (short) ((recBuf[startp + 7] >> 7) & 0x01);
			valueStr[0] = Short.toString(isStart);// 起止标志
			valueStr[1] = Short.toString(tmpPn);// 测量点号
			valueStr[2] = func.transBS8ToStrBuffer(recBuf[startp + 8]).substring(0, 3);// 异常标志
			strBuf = func.transBS8ToStrBuffer(recBuf[startp + 9]);
			strBuf.append(func.transBS8ToStrBuffer(recBuf[startp + 10]).toString());
			valueStr[3] = strBuf.toString();// 电容器组标志
			valueStr[4] = Double.toString(func.TransData5(recBuf, startp + 11));// 功率因素
			valueStr[5] = Double.toString(func.TransData9(recBuf, startp + 13));// 无功功率
			valueStr[6] = Double.toString(func.TransData7(recBuf, startp + 16));// 电压
			break;
		case 19:
			fromType = 2;
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			valueStr[0] = Short.toString(tmpPn);// 总加组号
			int gddhTemp = 0;
			gddhTemp += func.getInt(recBuf[startp + 8]);
			gddhTemp += func.getInt(recBuf[startp + 9]) << 8;
			gddhTemp += func.getInt(recBuf[startp + 10]) << 16;
			gddhTemp += (func.getInt(recBuf[startp + 11]) & 0x7f) << 24;
			valueStr[1] = Integer.toString(gddhTemp);// 购电单号
			valueStr[2] = Integer.toString(func.getInt(recBuf[startp + 12]));// 追加刷新标志
			valueStr[3] = Long.toString(func.TransData3(recBuf, startp + 13));// 购电量值
			valueStr[4] = Long.toString(func.TransData3(recBuf, startp + 17));// 报警门限
			valueStr[5] = Long.toString(func.TransData3(recBuf, startp + 21));// 跳闸门限
			valueStr[6] = Long.toString(func.TransData3(recBuf, startp + 25));// 购电前剩余电能量
			valueStr[7] = Long.toString(func.TransData3(recBuf, startp + 29));// 购电后剩余电能量
			break;
		case 20:
			strBuf = new StringBuffer();
			for (int j = 0; j < 2; j++) {// 必须在0~9范围内
				strBuf.append(Integer.toString(func.getInt(recBuf[startp + 7 + j])));
			}
			valueStr[0] = strBuf.toString();// 消息认证码
			valueStr[1] = Integer.toString(func.getInt(recBuf[startp + 9]));// 启动站地址
			break;
		case 21:
			valueStr[0] = Integer.toString(func.getInt(recBuf[startp + 7]));// 终端故障编码
			break;
		case 22:
			fromType = 3;
			isStart = (short) ((recBuf[startp + 7] >> 7) & 0x01);
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			valueStr[0] = Short.toString(isStart);// 起止标志
			valueStr[1] = Short.toString(tmpPn); // 电能量差动组号
			valueStr[2] = Long.toString(func.TransData3(recBuf, startp + 8));// 越限时对比总加组有功总电能量
			valueStr[3] = Long.toString(func.TransData3(recBuf, startp + 12));// 越限时参照总加组有功总电能量
			valueStr[4] = Integer.toString(func.getInt(recBuf[startp + 16]));// 越限时差动越限相对偏差值
			valueStr[5] = Long.toString(func.TransData3(recBuf, startp + 17));// 越限时差动越限绝对偏差值
			int n = func.getInt(recBuf[startp + 21]);// 对比总加组测量点数量n（1≤n≤64）
			int m = func.getInt(recBuf[startp + 22 + 5 * n]);// 参照测量点数m（1≤m≤64）
			if (n < 1 || n > 64 || m < 1 || m > 64) {
				log.debug("ERC22：m or n Error");
				return null;
			}
			valueStr[6] = Integer.toString(n);
			for (int j = 0; j < n; j++) {
				valueStr[7] = Double.toString(func.TransData14(recBuf, startp + 22 + 5 * j));
				dc = getDataCode(valueStr[7], codeStr[7]);
				dataCodes.add(dc);
			}
			valueStr[8] = Integer.toString(m);
			for (int j = 0; j < m; j++) {
				valueStr[9] = Double
						.toString(func.TransData14(recBuf, startp + 23 + 5 * n + 5 * j));
				dc = getDataCode(valueStr[9], codeStr[9]);
				dataCodes.add(dc);
			}
			break;
		case 24:
		case 25:
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			isStart = (short) ((recBuf[startp + 7] >> 7) & 0x01);
			valueStr[0] = Short.toString(isStart);// 起止标志
			valueStr[1] = Short.toString(tmpPn);// 测量点号
			valueStr[2] = func.transBS8ToStrBuffer(recBuf[startp + 8]).substring(6, 8);// 电压电流越限标志
			valueStr[3] = func.transBS8ToStrBuffer(recBuf[startp + 8]).substring(0, 3);// 三相越限标志
			if (eventType == 24) {
				valueStr[4] = Double.toString(func.TransData7(recBuf, startp + 9));
				valueStr[5] = Double.toString(func.TransData7(recBuf, startp + 11));
				valueStr[6] = Double.toString(func.TransData7(recBuf, startp + 13));
			} else {
				valueStr[4] = Double.toString(func.TransData6(recBuf, startp + 9));
				valueStr[5] = Double.toString(func.TransData6(recBuf, startp + 11));
				valueStr[6] = Double.toString(func.TransData6(recBuf, startp + 13));
			}
			break;
		case 26:
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			isStart = (short) ((recBuf[startp + 7] >> 7) & 0x01);
			valueStr[0] = Short.toString(isStart);// 起止标志
			valueStr[1] = Short.toString(tmpPn);// 测量点号
			valueStr[2] = func.transBS8ToStrBuffer(recBuf[startp + 8]).substring(6, 8);// 越限标志
			valueStr[3] = Double.toString(func.TransData23(recBuf, startp + 9));// 视在功率
			valueStr[4] = Double.toString(func.TransData23(recBuf, startp + 12));// 视在功率限值
			break;
		case 27:
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			valueStr[0] = Short.toString(tmpPn);// 测量点号
			valueStr[1] = Double.toString(func.TransData14(recBuf, startp + 8));// 下降前电能表正向有功总电能示值
			valueStr[2] = Double.toString(func.TransData14(recBuf, startp + 13));// 下降后电能表正向有功总电能示值
			break;
		case 28:
		case 29:
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			valueStr[0] = Short.toString(tmpPn);// 测量点号
			valueStr[1] = Double.toString(func.TransData14(recBuf, startp + 8));// 对应正向有功总电能示值
			valueStr[2] = Double.toString(func.TransData14(recBuf, startp + 13));// 正向有功总电能示值
			valueStr[3] = Double.toString(func.TransData22(recBuf, startp + 18));// 阈值
			break;
		case 30:
			tmpPn = (short) (recBuf[startp + 7] & 0x3f);
			valueStr[0] = Short.toString(tmpPn);// 测量点号
			valueStr[1] = Double.toString(func.TransData14(recBuf, startp + 8));// 对应正向有功总电能示值
			valueStr[2] = Integer.toString(func.getInt(recBuf[startp + 13]));// 阈值
			break;
		default:
			log.debug("Wrong event type!");
			return null;
		}
		if (eventType != 3 && eventType != 22)
			for (int j = 0; j < codeStr.length; j++) {
				if (valueStr[j] == null)
					continue;
				dc = getDataCode(valueStr[j], codeStr[j]);
				dataCodes.add(dc);
			}
		DbData db = initDbData(tmnlAddress, occurTime, pnType, tmpPn, isStart, fromType);
		if (db == null) {
			log.error("initDbData=null");
			taskObj.addLowLessReports(ConstDef.LOWLESS_PN_ERROR, "initDbData=null,pn=" + tmpPn);
			db = new DbData();
			db.setDataId(-1);
			db.setPn(tmpPn);
			db.setMark(0);
			db.setTime(occurTime);
			db.setDataType(FrontConstant.EVENT_DATA);
			db.setTmnlAddr(tmnlAddress);
			TerminalObject tmpTermObj = shareGlobalObj.getTerminalPara(tmnlAddress);
			if(null!=tmpTermObj)
			{
				db.setTmnlAssetNo(tmpTermObj.getTmnlAssetNo());
			}
		}
		db.setDataCodes(dataCodes);
		return db;
	}

	public DataCode getDataCode(String obj, String str) {
		DataCode dac = new DataCode();
		dac.setValue(obj);
		dac.setName(str);
		return dac;
	}

	public DbData initDbData(String tmnlAddress, Date timeTag, int pnType, short tmpPn,
			short isStart, int fromType) {

		try {
			shareGlobalObj = ShareGlobalObj.getInstance();
			DbData db = new DbData();
			String orgNo = null;
			EDataMp eDataMp = null;
			db.setDataType(FrontConstant.EVENT_DATA);
			switch (pnType) {
			case ClearCodeDef.CL_TYPE:
			case ClearCodeDef.P0_TYPE:
				eDataMp = shareGlobalObj.getMp(tmnlAddress, tmpPn);
				break;
			case ClearCodeDef.ZJ_TYPE:
			case ClearCodeDef.ZLDK_TYPE:
				eDataMp = shareGlobalObj.getMp(tmnlAddress, (short) 0);
				break;
			}
			if (eDataMp == null)
				return null;
			Integer ct = eDataMp.getCt();
			Integer pt = eDataMp.getPt();
			db.setCT(ct == null ? 1 : ct.intValue());
			db.setPT(pt == null ? 1 : pt.intValue());
			db.setDataId(eDataMp.getId());
			orgNo = eDataMp.getOrgNo();
			/*OOrg oorg = shareGlobalObj.getOrgPara(orgNo);
			if (oorg != null) {
				db.setAreaCode(oorg.getAreaCode());
			}*/
			db.setOrgNo(orgNo);
			db.setMark(0);
			db.setTmnlAssetNo(shareGlobalObj.getTerminalPara(tmnlAddress).getTmnlAssetNo());
			db.setTmnlAddr(tmnlAddress);
			db.setTime(timeTag);
			db.setPn(tmpPn);
			db.setEventIsStart(isStart);
			db.setEventFromType(fromType);
			return db;
		} catch (Exception ex) {
			return null;
		}

	}

	public String getDateFormatStr(Date date) {
		SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleFormat.format(date);
	}

	public static void main(String args[]) {
		Date date = new Date();
		GxExC3Data ex = new GxExC3Data();
		String str = ex.getDateFormatStr(date);
		log.debug(str);
	}
}
