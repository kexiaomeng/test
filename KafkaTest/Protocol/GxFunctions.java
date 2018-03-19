package Protocol;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nari.ami.database.map.basicdata.BErateOffset;
import com.nari.commObjectPara.ConstDef;
import com.nari.commObjectPara.TerminalObject;
import com.nari.fe.commdefine.define.FrontConstant;
import com.nari.global.ClearCodeDef;
import com.nari.global.ShareGlobalObj;
import com.nari.protocol.Dl698Protocol.Dl698commFunction;

public class GxFunctions {

	private Logger log = LoggerFactory.getLogger(GxFunctions.class);
	
	private ShareGlobalObj shareGlobalObjVar = ShareGlobalObj.getInstance();

	public GxFunctions() {

	}

	public int getfcb(int fcb) {
		return fcb == 0 ? 0x00 : 0x20;
	}

	public byte checkSum(byte[] buf, int start, int length) {
		byte byt = 0;
		for (int lp = start; lp < start + length; lp++) {
			byt = (byte) (byt + buf[lp]);
		}
		return byt;
	}

	public int getInt(byte byt) {
		return (int) (byt & 0xff);
	}

	public short getShort(byte byt) {
		return (short) (byt & 0xff);
	}

	public long getLong(byte byt) {
		return (long) (byt & 0xff);
	}

	public double getDouble(byte byt) {
		return (double) (byt & 0xff);
	}

	public int TransBinayToBcd(byte bin) {// 使用8421BCD码时一定要注意其有效的编码仅十个，即：0000～1001
		return bin < 0 || bin > 99 ? 0xff : bin / 10 << 4 | bin % 10;
	}

	public int TransBcdToBinay(int bcd) {// 高四位和第四位均不能超过9
		return ((bcd & 0xf0) >> 4) * 10 + (bcd & 0x0f);
	}

	public boolean checkIllegal(byte[] buf, int pot, int length) {
		if(pot + length > buf.length){
			return false;
		}
		for (int i = 0; i < length; i++) {
			int high = (buf[pot + i] & 0xf0) >> 4;
			int low = buf[pot + i] & 0x0f;
			if (high > 9 || low > 9)
				return true;
		}
		return false;
	}

	public String TransBcdToString(byte byt) {
		StringBuffer m = new StringBuffer();
		String s = new String();
		if (((byt >> 4) & 0x0f) == 0x0a)
			m.append(String.valueOf(","));
		else if (((byt >> 4) & 0x0f) == 0x0b)
			m.append(String.valueOf("#"));
		else
			m.append(String.valueOf(getInt((byte) ((byt >> 4) & 0x0f))));
		if ((byt & 0x0f) == 0x0a)
			m.append(new String(","));
		else if ((byt & 0x0f) == 0x0b)
			m.append(new String("#"));
		else
			m.append(String.valueOf(getInt((byte) ((byt) & 0x0f))));
		s = m.toString();
		return s;
	}

	public int[] TransData1(byte[] buf, int pot) throws Exception {
		int[] buffer = new int[6];
		buffer[0] = TransBcdToBinay(buf[pot]);
		buffer[1] = TransBcdToBinay(buf[pot + 1]);
		buffer[2] = TransBcdToBinay(buf[pot + 2]);
		buffer[3] = TransBcdToBinay(buf[pot + 3]);
		buffer[4] = TransBcdToBinay(buf[pot + 4] & 0x1f);
		buffer[5] = TransBcdToBinay(buf[pot + 5]);
		return buffer;
	}

	public double TransData2(byte[] buf, int pot) throws Exception {
		byte[] tmpbuf = new byte[2];
		tmpbuf[0] = buf[pot];
		tmpbuf[1] = (byte) (buf[pot + 1] & 0x0f);
		if (checkIllegal(tmpbuf, 0, 2))
			return -9999.0;
		int ivalue = 0;
		ivalue += TransBcdToBinay(buf[pot]);
		ivalue += (buf[pot + 1] & 0x0f) * 100;
		double rawvalue = ivalue;
		switch ((buf[pot + 1] >> 5) & 0x07) {
		case 0:
			rawvalue *= 10000;
			break;
		case 1:
			rawvalue *= 1000;
			break;
		case 2:
			rawvalue *= 100;
			break;
		case 3:
			rawvalue *= 10;
			break;
		case 4:
			break;
		case 5:
			rawvalue *= 0.1;
			break;
		case 6:
			rawvalue *= 0.01;
			break;
		case 7:
			rawvalue *= 0.001;
			break;
		}
		BigDecimal bd = new BigDecimal(rawvalue);
		rawvalue = bd.setScale(3, RoundingMode.HALF_UP).doubleValue();
		return ((buf[pot + 1] >> 4) & 0x01) == 1 ? -rawvalue : rawvalue;
	}

	public long TransData3(byte[] buf, int pot) throws Exception {
		if (checkIllegal(buf, pot, 4))
			return -9999;
		long ivalue = TransBcdToBinay(buf[pot]);
		ivalue += TransBcdToBinay(buf[pot + 1]) * 100;
		ivalue += TransBcdToBinay(buf[pot + 2]) * 10000;
		ivalue += (buf[pot + 3] & 0x0f) * 1000000;
		long rawvalue = ivalue;
		// modified by joehan@ah 2011-03-16
		if ((buf[pot + 3] & 0x40) == 0x40)// G=1,统一 转换成，KWH、元处理
		{
			rawvalue = rawvalue * 1000;
		}
		return ((buf[pot + 3] >> 4) & 0x01) == 1 ? -rawvalue : rawvalue;
	}

	public long TransData4(byte[] buf, int pot) throws Exception {
		byte[] tmpbuf = new byte[1];
		tmpbuf[0] = (byte) (buf[pot] & 0x7f);
		if (checkIllegal(tmpbuf, 0, 1))
			return -9999;
		long ivalue = TransBcdToBinay(buf[pot] & 0x7f);
		return ivalue;
	}

	public double TransData5(byte[] buf, int pot) throws Exception {
		byte[] tmpbuf = new byte[2];
		tmpbuf[0] = buf[pot];
		tmpbuf[1] = (byte) (buf[pot + 1] & 0x7f);
		if (checkIllegal(tmpbuf, 0, 2))
			return -9999.0;
		int ivalue = TransBcdToBinay(buf[pot]);
		double rawvalue = (double) ivalue * 0.1;
		ivalue = 0;
		ivalue += TransBcdToBinay(buf[pot + 1] & 0x7f) * 10;
		rawvalue += (double) ivalue;
		BigDecimal bd = new BigDecimal(rawvalue);
		rawvalue = bd.setScale(1, RoundingMode.HALF_UP).doubleValue();
		return rawvalue;
	}

	public double TransData6(byte[] buf, int pot) throws Exception {
		byte[] tmpbuf = new byte[2];
		tmpbuf[0] = buf[pot];
		tmpbuf[1] = (byte) (buf[pot + 1] & 0x7f);
		if (checkIllegal(tmpbuf, 0, 2))
			return -9999.0;
		int ivalue = TransBcdToBinay(buf[pot]);
		double rawvalue = (double) ivalue * 0.01;
		ivalue = TransBcdToBinay(buf[pot + 1] & 0x7f);
		rawvalue += (double) ivalue;
		BigDecimal bd = new BigDecimal(rawvalue);
		rawvalue = bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
		return ((buf[pot + 1] >> 7) & 0x01) == 1 ? -rawvalue : rawvalue;
	}

	public double TransData7(byte[] buf, int pot) throws Exception {
		if (checkIllegal(buf, pot, 2))
			return -9999.0;
		int ivalue = TransBcdToBinay(buf[pot]);
		double rawvalue = (double) ivalue * 0.1;
		ivalue = 0;
		ivalue += TransBcdToBinay(buf[pot + 1]) * 10;
		rawvalue += (double) ivalue;
		BigDecimal bd = new BigDecimal(rawvalue);
		rawvalue = bd.setScale(1, RoundingMode.HALF_UP).doubleValue();
		return rawvalue;

	}

	public long TransData8(byte[] buf, int pot) throws Exception {
		if (checkIllegal(buf, pot, 2))
			return (long) -9999.0;
		long ivalue = TransBcdToBinay(buf[pot]);
		ivalue += TransBcdToBinay(buf[pot + 1]) * 100;
		return ivalue;
	}

	public double TransData9(byte[] buf, int pot) throws Exception {
		byte[] tmpbuf = new byte[3];
		tmpbuf[0] = buf[pot];
		tmpbuf[1] = buf[pot + 1];
		tmpbuf[2] = (byte) (buf[pot + 2] & 0x7f);
		if (checkIllegal(tmpbuf, 0, 3))
			return -9999.0;
		int ivalue = TransBcdToBinay(buf[pot]);
		ivalue += TransBcdToBinay(buf[pot + 1]) * 100;
		double rawvalue = (double) ivalue * 0.0001;
		ivalue = 0;
		ivalue += TransBcdToBinay(buf[pot + 2] & 0x7f);
		rawvalue += (double) ivalue;
		BigDecimal bd = new BigDecimal(rawvalue);
		rawvalue = bd.setScale(4, RoundingMode.HALF_UP).doubleValue();
		return ((buf[pot + 2] >> 7) & 0x01) == 1 ? -rawvalue : rawvalue;
	}

	public int TransData10(byte[] buf, int pot) throws Exception {
		if (checkIllegal(buf, pot, 3))
			return -9999;
		int ivalue = TransBcdToBinay(buf[pot]);
		ivalue += TransBcdToBinay(buf[pot + 1]) * 100;
		ivalue += TransBcdToBinay(buf[pot + 2]) * 10000;
		return ivalue;
	}

	public double TransData11(byte[] buf, int pot) throws Exception {
		if (checkIllegal(buf, pot, 4))
			return -9999.0;
		int ivalue = TransBcdToBinay(buf[pot]);
		double rawvalue = (double) ivalue * 0.01;
		ivalue = 0;
		ivalue += TransBcdToBinay(buf[pot + 1]);
		ivalue += TransBcdToBinay(buf[pot + 2]) * 100;
		ivalue += TransBcdToBinay(buf[pot + 3]) * 10000;
		rawvalue += (double) ivalue;
		BigDecimal bd = new BigDecimal(rawvalue);
		rawvalue = bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
		return rawvalue;
	}

	public long TransData12(byte[] buf, int pot) throws Exception {
		// A.12格式 含有EE字节的数据项当作空值处理
		if (checkIllegal(buf, pot, 6))
			return -9999;
		long ivalue = TransBcdToBinay(buf[pot]);
		ivalue += TransBcdToBinay(buf[pot + 1]) * 100;
		ivalue += TransBcdToBinay(buf[pot + 2]) * 10000;
		ivalue += TransBcdToBinay(buf[pot + 3]) * 1000000;
		ivalue += TransBcdToBinay(buf[pot + 4]) * 100000000;
		ivalue += TransBcdToBinay(buf[pot + 5]) * 10000000000l;
		return ivalue;
	}

	public double TransData13(byte[] buf, int pot) throws Exception {
		// A.13格式 含有EE字节的数据项当作空值处理
		if (checkIllegal(buf, pot, 4))
			return -9999.0;
		int ivalue = TransBcdToBinay(buf[pot]);
		ivalue += TransBcdToBinay(buf[pot + 1]) * 100;
		double rawvalue = (double) ivalue * 0.0001;
		ivalue = 0;
		ivalue += TransBcdToBinay(buf[pot + 2]);
		ivalue += TransBcdToBinay(buf[pot + 3]) * 100;
		rawvalue += (double) ivalue;
		BigDecimal bd = new BigDecimal(rawvalue);
		rawvalue = bd.setScale(4, RoundingMode.HALF_UP).doubleValue();
		return rawvalue;
	}

	public double TransData14(byte[] buf, int pot) throws Exception {
		// A.14格式 含有EE字节的数据项当作空值处理
		if (checkIllegal(buf, pot, 5))
			return -9999.0;
		int ivalue = TransBcdToBinay(buf[pot]);
		ivalue += TransBcdToBinay(buf[pot + 1]) * 100;
		double rawvalue = (double) ivalue * 0.0001;
		ivalue = 0;
		ivalue += TransBcdToBinay(buf[pot + 2]);
		ivalue += TransBcdToBinay(buf[pot + 3]) * 100;
		ivalue += TransBcdToBinay(buf[pot + 4]) * 10000;
		rawvalue += (double) ivalue;
		BigDecimal bd = new BigDecimal(rawvalue);
		rawvalue = bd.setScale(4, RoundingMode.HALF_UP).doubleValue();
		return rawvalue;

	}

	public int[] TransData15(byte[] buf, int pot) throws Exception {
		int[] buffer = new int[5];
		buffer[0] = TransBcdToBinay(buf[pot]);
		buffer[1] = TransBcdToBinay(buf[pot + 1]);
		buffer[2] = TransBcdToBinay(buf[pot + 2]);
		buffer[3] = TransBcdToBinay(buf[pot + 3]);
		buffer[4] = TransBcdToBinay(buf[pot + 4]);
		return buffer;

	}

	public int[] TransData17(byte[] buf, int pot) throws Exception {// TransData16
		int[] buffer = new int[4];
		buffer[0] = TransBcdToBinay(buf[pot]);
		buffer[1] = TransBcdToBinay(buf[pot + 1]);
		buffer[2] = TransBcdToBinay(buf[pot + 2]);
		buffer[3] = TransBcdToBinay(buf[pot + 3]);
		return buffer;

	}

	public int[] TransData18(byte[] buf, int pot) throws Exception {
		int[] buffer = new int[3];
		buffer[0] = TransBcdToBinay(buf[pot]);
		buffer[1] = TransBcdToBinay(buf[pot + 1]);
		buffer[2] = TransBcdToBinay(buf[pot + 2]);
		return buffer;
	}

	public int[] TransData19(byte[] buf, int pot) throws Exception {// TransData21,TransData24
		int[] buffer = new int[2];
		buffer[0] = TransBcdToBinay(buf[pot]);
		buffer[1] = TransBcdToBinay(buf[pot + 1]);

		return buffer;
	}

	public int[] TransData20(byte[] buf, int pot) throws Exception {
		int[] buffer = new int[3];
		buffer[0] = TransBcdToBinay(buf[pot]);
		buffer[1] = TransBcdToBinay(buf[pot + 1]);
		buffer[2] = TransBcdToBinay(buf[pot + 2]);
		return buffer;

	}

	public double TransData22(byte[] buf, int pot) throws Exception {
		if (checkIllegal(buf, pot, 1))
			return -9999.0;
		int ivalue = TransBcdToBinay(buf[pot]);
		double rawvalue = (double) ivalue * 0.1;
		BigDecimal bd = new BigDecimal(rawvalue);
		rawvalue = bd.setScale(1, RoundingMode.HALF_UP).doubleValue();
		return rawvalue;

	}

	public double TransData23(byte[] buf, int pot) throws Exception {
		if (checkIllegal(buf, pot, 3))
			return -9999.0;
		int ivalue = TransBcdToBinay(buf[pot]);
		ivalue += TransBcdToBinay(buf[pot + 1]) * 100;
		double rawvalue = (double) ivalue * 0.0001;
		ivalue = 0;
		ivalue += TransBcdToBinay(buf[pot + 2]);
		rawvalue += (double) ivalue;
		BigDecimal bd = new BigDecimal(rawvalue);
		rawvalue = bd.setScale(4, RoundingMode.HALF_UP).doubleValue();
		return rawvalue;
	}

	public double TransData25(byte[] buf, int pot) throws Exception {
		byte[] tmpbuf = new byte[3];
		tmpbuf[0] = buf[pot];
		tmpbuf[1] = buf[pot + 1];
		tmpbuf[2] = (byte) (buf[pot + 2] & 0x7f);
		if (checkIllegal(tmpbuf, 0, 3))
			return -9999.0;
		int ivalue = TransBcdToBinay(buf[pot]);
		ivalue += TransBcdToBinay(buf[pot + 1]) * 100;
		double rawvalue = (double) ivalue * 0.001;
		ivalue = 0;
		ivalue += TransBcdToBinay(buf[pot + 2] & 0x7f) * 10;
		rawvalue += (double) ivalue;
		BigDecimal bd = new BigDecimal(rawvalue);
		rawvalue = bd.setScale(3, RoundingMode.HALF_UP).doubleValue();
		return ((buf[pot + 2] >> 7) & 0x01) == 1 ? -rawvalue : rawvalue;
	}

	public double TransData26(byte[] buf, int pot) throws Exception {
		if (checkIllegal(buf, pot, 2))
			return -9999.0;
		int ivalue = TransBcdToBinay(buf[pot]);
		ivalue += TransBcdToBinay(buf[pot + 1]) * 100;
		double rawvalue = (double) ivalue * 0.001;
		BigDecimal bd = new BigDecimal(rawvalue);
		rawvalue = bd.setScale(3, RoundingMode.HALF_UP).doubleValue();
		return rawvalue;
	}

	public double TransData27(byte[] buf, int pot) throws Exception {
		if (checkIllegal(buf, pot, 4))
			return -9999.0;
		long ivalue = TransBcdToBinay(buf[pot]);
		ivalue += TransBcdToBinay(buf[pot + 1]) * 100;
		ivalue += TransBcdToBinay(buf[pot + 2]) * 10000;
		ivalue += TransBcdToBinay(buf[pot + 3]) * 1000000;
		return ivalue;
	}

	public String AsciiToString(byte[] buf, int pot, int num) {
		StringBuffer strBuf = new StringBuffer();
		for (int k = 0; k < num; k++) {
			strBuf.append((char) buf[pot + k]);
		}
		return strBuf.toString();
	}

	public byte[] StringToAscii(String str, int maxPosition) {
		byte byt[] = new byte[maxPosition];
		for (int k = 0; k < maxPosition; k++) {
			byt[k] = 0;
		}
		if (str == null || str.isEmpty())
			return byt;
		if (str.length() > maxPosition)
			str = str.substring(0, maxPosition);
		char tmpByt[] = str.toCharArray();
		for (int j = 0; j < tmpByt.length; j++) {
			byt[j] = (byte) tmpByt[j];
		}
		return byt;
	}

	public StringBuffer transBS8ToStrBuffer1(byte byt) {
		StringBuffer sb = new StringBuffer();
		for (int k = 7; k >=0; k--) {
			byte temp = (byte) ((0x01 << k));
			sb.append((byt & temp) == 0 ? "0" : "1");
		}
		return sb;
	}

	public StringBuffer transBS8ToStrBuffer(byte byt) {
		StringBuffer sb = new StringBuffer();
		for (int k = 0; k < 8; k++) {
			byte temp = (byte) ((0x01 << k));
			sb.append((byt & temp) == 0 ? "0" : "1");
		}
		return sb;
	}

	public Date getDate1(byte[] buf, int count) throws Exception {
		

		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		int time[] = TransData1(buf, count);
		byte[] tmp = new byte[buf.length];
		System.arraycopy(buf, 0, tmp, 0, tmp.length);
		tmp[count+4] = (byte) time[4];
		if (checkIllegal(tmp, count, 6)) {
			Date d = new Date(System.currentTimeMillis());
			return d;
		}
		cl.set(Calendar.SECOND, time[0]);
		cl.set(Calendar.MINUTE, time[1]);
		cl.set(Calendar.HOUR_OF_DAY, time[2]);
		cl.set(Calendar.DAY_OF_MONTH, time[3]);
		cl.set(Calendar.MONTH, time[4] - 1);
		cl.set(Calendar.YEAR, time[5] + 2000);
		Date retTime = cl.getTime();//调用getTime后强制刷新Calendar中的值
		return retTime;
	}
	
	public Date getInvalideTime(){
		Calendar cl = Calendar.getInstance();
		cl.set(Calendar.YEAR, 1970);
		cl.set(Calendar.MONTH, 0);
		cl.set(Calendar.DAY_OF_MONTH, 1);
		cl.set(Calendar.HOUR_OF_DAY, 0);
		cl.set(Calendar.MINUTE, 0);
		cl.set(Calendar.SECOND, 0);
		return cl.getTime();
	}
	
	public boolean checkValuesAll0(byte[] buf, int pot, int length){
		int num0 =0;
		for (int i = 0; i < length; i++) {
			if(buf[pot + i] == 0){
				++num0;
			}
		}
		if(num0 == length){
			return true;
		}
		return false;
	}

	public Date getDate15(byte[] buf, int count) throws Exception {
		if (checkIllegal(buf, count, 5)) {
			return getInvalideTime();
		}
		if(checkValuesAll0(buf,count, 5)){
			return getInvalideTime();
		}

		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		int time[] = TransData15(buf, count);
		cl.set(Calendar.SECOND, 0);
		cl.set(Calendar.MINUTE, time[0]);
		cl.set(Calendar.HOUR_OF_DAY, time[1]);
		cl.set(Calendar.DAY_OF_MONTH, time[2]);
		cl.set(Calendar.MONTH, time[3] - 1);
		cl.set(Calendar.YEAR, time[4] + 2000);
		return cl.getTime();
	}

	public Date getDate17(byte[] buf, int count) throws Exception {
		if (checkIllegal(buf, count, 4)) {
			Calendar cl = Calendar.getInstance();
			cl.setTimeInMillis(System.currentTimeMillis());
			cl.set(Calendar.SECOND, 0);
			return cl.getTime();
		}

		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		int time[] = TransData17(buf, count);
		cl.set(Calendar.SECOND, 0);
		cl.set(Calendar.MINUTE, time[0]);
		cl.set(Calendar.HOUR_OF_DAY, time[1]);
		cl.set(Calendar.DAY_OF_MONTH, time[2]);
		cl.set(Calendar.MONTH, time[3] - 1);
		return cl.getTime();
	}

	public Date getDate18(byte[] buf, int count, Date date) throws Exception {
		if (checkIllegal(buf, count, 3)){
			Calendar cl = Calendar.getInstance();
			cl.setTimeInMillis(System.currentTimeMillis());
			cl.set(Calendar.SECOND, 0);
			return cl.getTime();
		}

		Calendar cl = Calendar.getInstance();
		cl.setTime(date);
		int time[] = TransData18(buf, count);
		cl.set(Calendar.SECOND, 0);
		cl.set(Calendar.MINUTE, time[0]);
		cl.set(Calendar.HOUR_OF_DAY, time[1]);
		cl.set(Calendar.DAY_OF_MONTH, time[2]);
		return cl.getTime();
	}

	public Date getDate20(byte[] buf, int count) throws Exception {
		if (checkIllegal(buf, count, 3)) {
			Calendar cl = Calendar.getInstance();
			cl.setTimeInMillis(System.currentTimeMillis());
			cl.set(Calendar.SECOND, 0);
			cl.set(Calendar.MINUTE, 0);
			cl.set(Calendar.HOUR_OF_DAY, 0);

			return cl.getTime();
		}

		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		int time[] = TransData20(buf, count);
		cl.set(Calendar.SECOND, 0);
		cl.set(Calendar.MINUTE, 0);
		cl.set(Calendar.HOUR_OF_DAY, 0);
		cl.set(Calendar.DAY_OF_MONTH, time[0]);
		cl.set(Calendar.MONTH, time[1] - 1);
		cl.set(Calendar.YEAR, time[2] + 2000);
		return cl.getTime();
	}

	public int getLength(byte lenthStart, byte lengthEnd) {// Fk04长度,组装有差别
		int length = (getInt(lengthEnd) << 8) & 0xff00;
		length = ((length | (lenthStart & 0xfc)) >> 2) & 0x3fff;
		return length;
	}
	
	public void removeDuplicateWithOrder1(ArrayList<Short> arlList)// Set不会有重复数据所以用Set处理下
	{
		Set<Short> set = new HashSet<Short>();
		List<Short> newList = new ArrayList<Short>();
		for (Iterator<Short> iter = arlList.iterator(); iter.hasNext();) {
			Short element = iter.next();
			if (set.add(element))
				newList.add(element);
		}
		arlList.clear();
		arlList.addAll(newList);
	}

	public void removeDuplicateWithOrder(ArrayList<Short> arlList)// Set不会有重复数据所以用Set处理下
	{
		Set<Short> set = new HashSet<Short>();
		List<Short> newList = new ArrayList<Short>();
		for (Iterator<Short> iter = arlList.iterator(); iter.hasNext();) {
			Short element = iter.next();
			if (set.add(element))
				newList.add(element);
		}
		arlList.clear();
		arlList.addAll(newList);
	}

	public Date TimeBeforeProcess(TerminalObject tmpTermObj, Date startTime, short fn) {
		if (tmpTermObj != null) {
			if (tmpTermObj.getFreezeMode().equals(ConstDef.FREEZE_DAY_BEFORE)) {
				Calendar cl = Calendar.getInstance();
				cl.setTime(startTime);
				if(Dl698commFunction.getElectricityType(fn) == Dl698commFunction.ElectricityType.Energy/*如果是电能量类*/
						&& tmpTermObj.getCurTaskType() == FrontConstant.TASK_TYPE_WEB_TASK/*并且是手工任务*/){
					//召测当天的电能量数据
				}else{
					//否则提前一天
					cl.add(Calendar.DAY_OF_MONTH, -1);
				}
				return cl.getTime();
			}
		}

		return startTime;
	}

	public Date TimeAfterProcess(TerminalObject tmpTermObj,short fn, Date startTime, int countType) {
		if (tmpTermObj != null) {
			if (shareGlobalObjVar.getLocation() != null)
				if (shareGlobalObjVar.getLocation().equalsIgnoreCase("anhui"))
					return startTime;
			if(Dl698commFunction.getElectricityType(fn) == Dl698commFunction.ElectricityType.Energy){
				//如果是电能量数据入库，也不加1
				return startTime;
			}
			if (tmpTermObj.getFreezeMode().equals(ConstDef.FREEZE_DAY_BEFORE)) {
				Calendar cl = Calendar.getInstance();
				cl.setTime(startTime);
				if(countType == ClearCodeDef.TD_C){//如果是曲线数据，则首先处理，并且是招測昨天入到昨天的
					
				}else{
					cl.add(Calendar.DAY_OF_MONTH, 1);
				}
				return cl.getTime();
			}
		}

		return startTime;
	}
	
	// 1类数据：F33/F34
	// 2类数据: F1/F2/F3/F4
	public void changedCodeStr(String[] srcCodeStr, BErateOffset erateOffset) {
		String[] tmpCodeStr = new String[srcCodeStr.length];
		for (int i = 0; i < srcCodeStr.length; i++) {
			tmpCodeStr[i] = srcCodeStr[i];
		}
		for (int i = 0; i < 4; i++) {
			int startp = 13 * i;
			srcCodeStr[startp] = tmpCodeStr[startp + erateOffset.getR()];
			srcCodeStr[startp + 1] = tmpCodeStr[startp + erateOffset.getR1()];
			srcCodeStr[startp + 2] = tmpCodeStr[startp + erateOffset.getR2()];
			srcCodeStr[startp + 3] = tmpCodeStr[startp + erateOffset.getR3()];
			srcCodeStr[startp + 4] = tmpCodeStr[startp + erateOffset.getR4()];
		}
	}

	public C1DataUnit getC1DataUnit(short fn) {
		C1DataUnit du = new C1DataUnit();
		return du.getDU(fn) > 0 ? du : null;
	}

	public C2DataUnit getC2DataUnit(short fn) {
		C2DataUnit du = new C2DataUnit();
		return du.getC2DU(fn) > 0 ? du : null;
	}

	public C3DataUnit getC3DataUnit(short eventType) {
		C3DataUnit du = new C3DataUnit();
		return du.getC3DU(eventType) > 0 ? du : null;
	}

	public Map<Short, C1DataUnit> getClass1FnMap(ArrayList<Short> fns) {
		if (fns == null || fns.isEmpty())
			return null;
		SortedMap<Short, C1DataUnit> class1FnMap = new TreeMap<Short, C1DataUnit>();
		for (int i = 0; i < fns.size(); i++) {
			if (fns.get(i) == null)
				continue;
			Short fni = fns.get(i);
			C1DataUnit duTemp = this.getC1DataUnit(fni);
			if (duTemp == null)
				continue;
			class1FnMap.put(fni, duTemp);

		}
		return class1FnMap;

	}

	public Map<Short, ArrayList<Short>> getClass1FnGroupMap(ArrayList<Short> fns) {
		if (fns == null || fns.isEmpty())
			return null;
		SortedMap<Short, ArrayList<Short>> class1FnGruopMap = new TreeMap<Short, ArrayList<Short>>();
		for (int i = 0; i < fns.size(); i++) {
			if (fns.get(i) == null)
				continue;
			Short fni = fns.get(i);
			C1DataUnit duTemp = this.getC1DataUnit(fni);
			if (duTemp == null)
				continue;
			ArrayList<Short> fnsGroup = null;
			fnsGroup = class1FnGruopMap.get(duTemp.group);
			if (fnsGroup == null) {
				fnsGroup = new ArrayList<Short>();
				fnsGroup.add(fni);
				class1FnGruopMap.put(duTemp.group, fnsGroup);
			} else {
				fnsGroup.add(fni);
				class1FnGruopMap.put(duTemp.group, fnsGroup);
			}
		}
		return class1FnGruopMap;
	}

	public Map<Short, ArrayList<Short>> getClass1PnGroupMap(ArrayList<Short> pns) {// 和698不同
		if (pns == null || pns.isEmpty())
			return null;
		SortedMap<Short, ArrayList<Short>> class1PnGruopMap = new TreeMap<Short, ArrayList<Short>>();
		for (int i = 0; i < pns.size(); i++) {
			if (pns.get(i) == null)
				continue;
			Short pni = pns.get(i);
			Short da2 = 0;
			if (pni == 0) {
				da2 = 0;
			} else {
				if (0 == (pni & 0x07)) {
					da2 = (short) (0x01 << ((pni >> 3) - 1));
				} else {
					da2 = (short) (0x01 << (pni >> 3));
				}
			}
			ArrayList<Short> pnsGroup = null;
			pnsGroup = class1PnGruopMap.get(da2);
			if (pnsGroup == null) {
				pnsGroup = new ArrayList<Short>();
				pnsGroup.add(pni);
				class1PnGruopMap.put(da2, pnsGroup);
			} else {
				pnsGroup.add(pni);
				class1PnGruopMap.put(da2, pnsGroup);
			}
		}
		return class1PnGruopMap;
	}

	public Map<Short, C2DataUnit> getClass2FnMap(ArrayList<Short> fns) {
		if (fns == null || fns.isEmpty())
			return null;
		SortedMap<Short, C2DataUnit> class2FnMap = new TreeMap<Short, C2DataUnit>();
		for (int i = 0; i < fns.size(); i++) {
			if (fns.get(i) == null)
				continue;
			Short fni = fns.get(i);
			C2DataUnit duTemp = this.getC2DataUnit(fni);
			if (duTemp == null)
				continue;
			class2FnMap.put(fni, duTemp);

		}
		return class2FnMap;

	}
	
	public ArrayList<Short> clearCode2LocalFn(ArrayList<String> fns){
		ArrayList<Short> funcList = new ArrayList<Short>();
		for(String fn : fns){
			Short val = CC.getFnByCC(fn);
			if(val != null){
				funcList.add(val);
			}else{
				log.error("透明编码项：" + fn +"找不到本地对应编码");
			}
		}
		return funcList;
	}

	public Map<Short, ArrayList<Short>> getClass2FnGroupMap(ArrayList<Short> fns) {
		if (fns == null || fns.isEmpty())
			return null;
		SortedMap<Short, ArrayList<Short>> class2FnGruopMap = new TreeMap<Short, ArrayList<Short>>();
		for (int i = 0; i < fns.size(); i++) {
			if (fns.get(i) == null)
				continue;
			Short fni = fns.get(i);
			C2DataUnit duTemp = this.getC2DataUnit(fni);
			if (duTemp == null)
				continue;
			ArrayList<Short> fnsGroup = null;
			fnsGroup = class2FnGruopMap.get(duTemp.group);
			if (fnsGroup == null) {
				fnsGroup = new ArrayList<Short>();
				fnsGroup.add(fni);
				class2FnGruopMap.put(duTemp.group, fnsGroup);
			} else {
				fnsGroup.add(fni);
				class2FnGruopMap.put(duTemp.group, fnsGroup);
			}
		}
		return class2FnGruopMap;
	}

	public Map<Short, ArrayList<Short>> getClass2PnGroupMap(ArrayList<Short> pns) {// 和698不同
		if (pns == null || pns.isEmpty())
			return null;
		SortedMap<Short, ArrayList<Short>> class2PnGruopMap = new TreeMap<Short, ArrayList<Short>>();
		for (int i = 0; i < pns.size(); i++) {
			if (pns.get(i) == null)
				continue;
			Short pni = pns.get(i);
			Short da2 = 0;
			if (pni == 0) {
				da2 = 0;
			} else {
				if (0 == (pni & 0x07)) {
					da2 = (short) (0x01 << ((pni >> 3) - 1));
				} else {
					da2 = (short) (0x01 << (pni >> 3));
				}
			}
			ArrayList<Short> pnsGroup = null;
			pnsGroup = class2PnGruopMap.get(da2);
			if (pnsGroup == null) {
				pnsGroup = new ArrayList<Short>();
				pnsGroup.add(pni);
				class2PnGruopMap.put(da2, pnsGroup);
			} else {
				pnsGroup.add(pni);
				class2PnGruopMap.put(da2, pnsGroup);
			}
		}
		return class2PnGruopMap;
	}

	public class C1DataUnit {
		private short group;// 16组
		private short pnType;
		private int countType;//
		private String codeStr[];
		private int countContent;
		private int bytLength;
		private int off;
		private int valueType;
		private int tdType;

		C1DataUnit() {
		}

		public int getDU(short fn) {
			if (fn >= 1 && fn <= 121) {
				short fn_temp = (short) ((fn - 1) >> 3);
				group = (short) (fn_temp + 1);
				pnType = ClearCodeDef.CL_TYPE;
				codeStr = null;
				countContent = 0;
				countType = 0;// 0:,1:读取判断长度(如f5),2:n个数据块,3:功率曲线td_h
				bytLength = 0;
				off = 0;
				valueType = ConstDef.NO_USE;
				tdType = ClearCodeDef.TD_H;
				switch (fn_temp) {
				case 0:
					switch (fn) {
					case 1:
						codeStr = ClearCodeDef.F1_C1;
						countContent = 30;
						break;
					case 2:
						codeStr = ClearCodeDef.F2_C1;
						countContent = 6;
						break;
					case 3:
						codeStr = ClearCodeDef.F3_C1;
						countContent = 31;
						break;
					case 4:
						codeStr = ClearCodeDef.F4_C1;
						countContent = 1;
						break;
					case 5:
						codeStr = ClearCodeDef.F5_C1;
						countType = 1;
						bytLength = 6;
						off = 1;
						countContent = 2;
						break;
					case 6:
						codeStr = ClearCodeDef.F6_C1;
						countType = 1;
						bytLength = 8;
						off = 2;
						countContent = 3;
						break;
					case 7:
						codeStr = ClearCodeDef.F7_C1;
						countContent = 2;
						break;
					case 8:
						codeStr = ClearCodeDef.F8_C1;
						countContent = 8;
						break;
					}
					pnType = ClearCodeDef.P0_TYPE;
					break;
				case 1:
					if (fn > 9)
						return -1;
					codeStr = ClearCodeDef.F9_C1;
					countContent = 2;
					pnType = ClearCodeDef.P0_TYPE;
					break;
				case 2:
					switch (fn) {
					case 17:
						codeStr = ClearCodeDef.F17_C1;
						countContent = 2;
						break;
					case 18:
						codeStr = ClearCodeDef.F18_C1;
						countContent = 2;
						break;
					case 19:
						codeStr = ClearCodeDef.F19_C1;
						countType = 2;
						bytLength = 4;
						countContent = 5;
						tdType = ClearCodeDef.TD_D;
						break;
					case 20:
						codeStr = ClearCodeDef.F20_C1;
						countType = 2;
						bytLength = 4;
						countContent = 5;
						tdType = ClearCodeDef.TD_D;
						break;
					case 21:
						codeStr = ClearCodeDef.F21_C1;
						countType = 2;
						bytLength = 4;
						countContent = 5;
						tdType = ClearCodeDef.TD_M;
						break;
					case 22:
						codeStr = ClearCodeDef.F22_C1;
						countType = 2;
						bytLength = 4;
						countContent = 5;
						tdType = ClearCodeDef.TD_M;
						break;
					case 23:
						codeStr = ClearCodeDef.F23_C1;
						countContent = 4;
						break;
					case 24:
						codeStr = ClearCodeDef.F24_C1;
						countContent = 2;
						break;
					}
					pnType = ClearCodeDef.ZJ_TYPE;
					break;
				case 3:
					switch (fn) {
					case 25:// 和698不同
						codeStr = ClearCodeDef.F25_C1;
						countContent = 51;
						break;
					case 26:
						codeStr = ClearCodeDef.F26_C1;
						countContent = 57;
						break;
					case 27:// 和698不同
						codeStr = ClearCodeDef.F27_C1;
						countContent = 28;
						break;
					}
					break;
				case 4:
					switch (fn) {
					case 33:
						codeStr = ClearCodeDef.F33_C1;
						bytLength = 17;
						countContent = 23;
						break;
					case 34:
						codeStr = ClearCodeDef.F34_C1;
						bytLength = 17;
						countContent = 23;
						break;
					case 35:
						codeStr = ClearCodeDef.F35_C1;
						bytLength = 14;
						countContent = 20;
						tdType = ClearCodeDef.TD_M;
						break;
					case 36:
						codeStr = ClearCodeDef.F36_C1;
						bytLength = 14;
						countContent = 20;
						tdType = ClearCodeDef.TD_M;
						break;
					case 37:
						codeStr = ClearCodeDef.F37_C1;
						bytLength = 17;
						countContent = 23;
						tdType = ClearCodeDef.TD_M;
						break;
					case 38:
						codeStr = ClearCodeDef.F38_C1;
						bytLength = 17;
						countContent = 23;
						tdType = ClearCodeDef.TD_M;
						break;
					case 39:
						codeStr = ClearCodeDef.F39_C1;
						bytLength = 14;
						countContent = 20;
						tdType = ClearCodeDef.TD_M;
						break;
					case 40:
						codeStr = ClearCodeDef.F40_C1;
						bytLength = 14;
						countContent = 20;
						tdType = ClearCodeDef.TD_M;
						break;
					}
					countType = 2;
					off = 5;
					break;
				case 5:
					switch (fn) {
					case 41:
						codeStr = ClearCodeDef.F41_C1;
						tdType = ClearCodeDef.TD_D;
						break;
					case 42:
						codeStr = ClearCodeDef.F42_C1;
						tdType = ClearCodeDef.TD_D;
						break;
					case 43:
						codeStr = ClearCodeDef.F43_C1;
						tdType = ClearCodeDef.TD_D;
						break;
					case 44:
						codeStr = ClearCodeDef.F44_C1;
						tdType = ClearCodeDef.TD_D;
						break;
					case 45:
						codeStr = ClearCodeDef.F45_C1;
						tdType = ClearCodeDef.TD_M;
						break;
					case 46:
						codeStr = ClearCodeDef.F46_C1;
						tdType = ClearCodeDef.TD_M;
						break;
					case 47:
						codeStr = ClearCodeDef.F47_C1;
						tdType = ClearCodeDef.TD_M;
						break;
					case 48:
						codeStr = ClearCodeDef.F48_C1;
						tdType = ClearCodeDef.TD_M;
						break;
					}
					countType = 2;
					bytLength = 4;
					countContent = 5;
					break;
				case 6:
					if (fn > 49)
						return -1;
					codeStr = ClearCodeDef.F49_C1;
					countContent = 12;
					break;
				case 7:
					if (fn > 58)
						return -1;
					switch (fn) {
					case 57:
						codeStr = ClearCodeDef.F57_C1;
						bytLength = 12;
						countContent = -11;
						break;
					case 58:
						codeStr = ClearCodeDef.F58_C1;
						bytLength = 12;
						countContent = -5;
						break;
					}
					countType = 2;
					break;
				case 8:
					if (fn > 67)
						return -1;
					switch (fn) {
					case 65:
						codeStr = ClearCodeDef.F65_C1;
						countContent = 3;
						break;
					case 66:
						codeStr = ClearCodeDef.F66_C1;
						countContent = 72;
						break;
					case 67:
						codeStr = ClearCodeDef.F67_C1;
						countContent = 8;
						break;
					}
					break;
				case 9:
					if (fn > 73)
						return -1;
					codeStr = ClearCodeDef.F73_C1;
					pnType = ClearCodeDef.ZLDK_TYPE;
					countContent = 2;
					break;
				case 10:
					if (fn > 84)
						return -1;
					switch (fn) {
					case 81:
						codeStr = ClearCodeDef.F81_C1;
						bytLength = 2;
						valueType = ConstDef.TOTAL_AP;
						break;
					case 82:
						codeStr = ClearCodeDef.F82_C1;
						bytLength = 2;
						valueType = ConstDef.TOTAL_RP;
						break;
					case 83:
						codeStr = ClearCodeDef.F83_C1;
						bytLength = 4;
						valueType = ConstDef.TOTAL_AP;
						break;
					case 84:
						codeStr = ClearCodeDef.F84_C1;
						bytLength = 4;
						valueType = ConstDef.TOTAL_RP;
						break;
					}
					pnType = ClearCodeDef.ZJ_TYPE;
					countType = 3;
					break;
				case 11:
					switch (fn) {
					case 89:
						codeStr = ClearCodeDef.F89_C1;
						valueType = ConstDef.ACTIVE_POWER;
						break;
					case 90:
						codeStr = ClearCodeDef.F90_C1;
						valueType = ConstDef.A_ACTIVE_POWER;
						break;
					case 91:
						codeStr = ClearCodeDef.F91_C1;
						valueType = ConstDef.B_ACTIVE_POWER;
						break;
					case 92:
						codeStr = ClearCodeDef.F92_C1;
						valueType = ConstDef.C_ACTIVE_POWER;
						break;
					case 93:
						codeStr = ClearCodeDef.F93_C1;
						valueType = ConstDef.REACTIVE_POWER;
						break;
					case 94:
						codeStr = ClearCodeDef.F94_C1;
						valueType = ConstDef.A_REACTIVE_POWER;
						break;
					case 95:
						codeStr = ClearCodeDef.F95_C1;
						valueType = ConstDef.B_REACTIVE_POWER;
						break;
					case 96:
						codeStr = ClearCodeDef.F96_C1;
						valueType = ConstDef.C_REACTIVE_POWER;
						break;
					}
					countType = 3;
					bytLength = 3;
					break;
				case 12:
					if (fn > 103)
						return -1;
					switch (fn) {
					case 97:
						codeStr = ClearCodeDef.F97_C1;
						valueType = ConstDef.A_PHASE;
						break;
					case 98:
						codeStr = ClearCodeDef.F98_C1;
						valueType = ConstDef.B_PHASE;
						break;
					case 99:
						codeStr = ClearCodeDef.F99_C1;
						valueType = ConstDef.C_PHASE;
						break;
					case 100:
						codeStr = ClearCodeDef.F100_C1;
						valueType = ConstDef.A_PHASE;
						break;
					case 101:
						codeStr = ClearCodeDef.F101_C1;
						valueType = ConstDef.B_PHASE;
						break;
					case 102:
						codeStr = ClearCodeDef.F102_C1;
						valueType = ConstDef.C_PHASE;
						break;
					case 103:
						codeStr = ClearCodeDef.F103_C1;
						valueType = ConstDef.POWER_DIRECTOR;
						break;
					}
					bytLength = 2;
					countType = 3;
					break;
				case 13:
					switch (fn) {
					case 105:
						codeStr = ClearCodeDef.F105_C1;
						valueType = ConstDef.PAP;
						break;
					case 106:
						codeStr = ClearCodeDef.F106_C1;
						valueType = ConstDef.PRP;
						break;
					case 107:
						codeStr = ClearCodeDef.F107_C1;
						valueType = ConstDef.RAP;
						break;
					case 108:
						codeStr = ClearCodeDef.F108_C1;
						valueType = ConstDef.RRP;
						break;
					case 109:
						codeStr = ClearCodeDef.F109_C1;
						valueType = ConstDef.PAP;
						break;
					case 110:
						codeStr = ClearCodeDef.F110_C1;
						valueType = ConstDef.PRP;
						break;
					case 111:
						codeStr = ClearCodeDef.F111_C1;
						valueType = ConstDef.RAP;
						break;
					case 112:
						codeStr = ClearCodeDef.F112_C1;
						valueType = ConstDef.RRP;
						break;
					}
					bytLength = 4;
					countType = 3;
					break;
				case 14:
					if (fn > 116)
						return -1;
					switch (fn) {
					case 113:
						codeStr = ClearCodeDef.F113_C1;
						valueType = ConstDef.POWER_DIRECTOR;
						break;
					case 114:
						codeStr = ClearCodeDef.F114_C1;
						valueType = ConstDef.A_PHASE;
						break;
					case 115:
						codeStr = ClearCodeDef.F115_C1;
						valueType = ConstDef.B_PHASE;
						break;
					case 116:
						codeStr = ClearCodeDef.F116_C1;
						valueType = ConstDef.C_PHASE;
						break;
					}
					bytLength = 2;
					countType = 3;
					break;
				case 15:
					if (fn > 121)
						return -1;
					codeStr = ClearCodeDef.F121_C1;
					pnType = ClearCodeDef.ZLDK_TYPE;
					bytLength = 2;
					countType = 3;
					break;
				default:
					return -1;

				}
				return 1;
			}
			return -1;
		}

		public short getGroup() {
			return group;
		}

		public void setGroup(short group) {
			this.group = group;
		}

		public short getPnType() {
			return pnType;
		}

		public void setPnType(short pnType) {
			this.pnType = pnType;
		}

		public String[] getCodeStr() {
			return codeStr;
		}

		public void setCodeStr(String[] codeStr) {
			this.codeStr = codeStr;

		}

		public int getCountContent() {
			return countContent;
		}

		public void setCountContent(int countContent) {
			this.countContent = countContent;
		}

		public int getCountType() {
			return countType;
		}

		public void setCountType(int countType) {
			this.countType = countType;
		}

		public int getBytLength() {
			return bytLength;
		}

		public void setBytLength(int bytLength) {
			this.bytLength = bytLength;
		}

		public int getOff() {
			return off;
		}

		public void setOff(int off) {
			this.off = off;
		}

		public int getValueType() {
			return valueType;
		}

		public void setValueType(int valueType) {
			this.valueType = valueType;
		}

		public int getTdType() {
			return tdType;
		}

		public void setTdType(int tdType) {
			this.tdType = tdType;
		}

	}

	public class C2DataUnit {
		private short group;// 18组
		private int tdType;
		private int pnType;
		private int countContent;
		private int countType;// 0：,1:费率数m第一情况,2:费率数m第二情况,3:功率曲线td_c,4:谐波n
		private int bytLength;
		private String codeStr[];
		private int valueType;

		public C2DataUnit() {
		}

		public int getC2DU(short fn) {
			if (fn >= 1 && fn <= 138) {
				short fn_temp = (short) ((fn - 1) >> 3);
				this.group = (short) (fn_temp + 1);
				this.pnType = ClearCodeDef.CL_TYPE;
				this.tdType = ClearCodeDef.TD_D;
				this.countType = 0;
				this.countContent = 0;
				this.codeStr = null;
				this.bytLength = 0;
				valueType = 0;
				switch (fn_temp) {
				case 0:
					switch (fn) {
					case 1:
						codeStr = ClearCodeDef.F1_INDICATION_PHASE;
						countType = 1;
						this.bytLength = 17;
						break;
					case 2:
						codeStr = ClearCodeDef.F2_INDICATION_PHASE;
						countType = 1;
						this.bytLength = 17;
						break;
					case 3:
						codeStr = ClearCodeDef.F3_NEED_VALUE_PHASE;
						countType = 1;
						this.bytLength = 14;
						break;
					case 4:
						codeStr = ClearCodeDef.F4_NEED_VALUE_PHASE;
						countType = 1;
						this.bytLength = 14;
						break;
					case 5:
						codeStr = ClearCodeDef.F5_PAP_ENERGY_PHASE;
						countType = 2;
						break;
					case 6:
						codeStr = ClearCodeDef.F6_PRP_ENERGY_PHASE;
						countType = 2;
						break;
					case 7:
						codeStr = ClearCodeDef.F7_RAP_ENERGY_PHASE;
						countType = 2;
						break;
					case 8:
						codeStr = ClearCodeDef.F8_RRP_ENERGY_PHASE;
						countType = 2;
						break;
					}
					break;
				case 1:
					if (fn >= 13)
						return -1;
					switch (fn) {
					case 9:
						codeStr = ClearCodeDef.F9_INDICATION_PHASE;
						this.bytLength = 17;
						break;
					case 10:
						codeStr = ClearCodeDef.F10_INDICATION_PHASE;
						this.bytLength = 17;
						break;
					case 11:
						codeStr = ClearCodeDef.F11_NEED_VALUE_PHASE;
						this.bytLength = 14;
						break;
					case 12:
						codeStr = ClearCodeDef.F12_NEED_VALUE_PHASE;
						this.bytLength = 14;
						break;
					}
					countType = 1;
					break;
				case 2:
					switch (fn) {
					case 17:
						codeStr = ClearCodeDef.F17_INDICATION_PHASE_MONTH;
						countType = 1;
						this.bytLength = 17;
						break;
					case 18:
						codeStr = ClearCodeDef.F18_INDICATION_PHASE_MONTH;
						countType = 1;
						this.bytLength = 17;
						break;
					case 19:
						codeStr = ClearCodeDef.F19_NEED_VALUE_PHASE_MONTH;
						countType = 1;
						this.bytLength = 14;
						break;
					case 20:
						codeStr = ClearCodeDef.F20_NEED_VALUE_PHASE_MONTH;
						countType = 1;
						this.bytLength = 14;
						break;
					case 21:
						codeStr = ClearCodeDef.F21_PAP_ENERGY_PHASE_MONTH;
						countType = 2;
						break;
					case 22:
						codeStr = ClearCodeDef.F22_PRP_ENERGY_PHASE_MONTH;
						countType = 2;
						break;
					case 23:
						codeStr = ClearCodeDef.F23_RAP_ENERGY_PHASE_MONTH;
						countType = 2;
						break;
					case 24:
						codeStr = ClearCodeDef.F24_RRP_ENERGY_PHASE_MONTH;
						countType = 2;
						break;
					}
					this.tdType = ClearCodeDef.TD_M;
					break;
				case 3:
					switch (fn) {
					case 25:
						codeStr = ClearCodeDef.F25_THREE_PHASE_ACTIVE_POWER;
						countContent = 32;
						break;
					case 26:
						codeStr = ClearCodeDef.F26_THREE_PHASE_NEED;
						countContent = 24;
						break;
					case 27:
						codeStr = ClearCodeDef.F27_THREE_VOLTAGE_STATIS_DATA;
						countContent = 66;
						break;
					case 28:
						codeStr = ClearCodeDef.F28_UNBALANCED_STATIS_DATA;
						countContent = 4;
						break;
					case 29:
						codeStr = ClearCodeDef.F29_CURRENT_LIMITED_DATA;
						countContent = 34;
						break;
					case 30:
						codeStr = ClearCodeDef.F30_POWER_LIMITED_TIME;
						countContent = 4;
						break;
					case 31:
						codeStr = ClearCodeDef.F32_BROKEN_PHASE;
						countContent = 52;
						break;
					}
					break;
				case 4:
					if (fn > 38)
						return -1;
					switch (fn) {
					case 33:
						codeStr = ClearCodeDef.F33_THREE_PHASE_ACTIVE_POWER;
						countContent = 24;
						break;
					case 34:
						codeStr = ClearCodeDef.F34_THREE_PHASE_NEED;
						countContent = 24;
						break;
					case 35:
						codeStr = ClearCodeDef.F35_THREE_VOLTAGE_STATIS_DATA;
						countContent = 66;
						break;
					case 36:
						codeStr = ClearCodeDef.F36_UNBALANCED_STATIS_DATA;
						countContent = 4;
						break;
					case 37:
						codeStr = ClearCodeDef.F37_CURRENT_LIMITED_DATA;
						countContent = 34;
						break;
					case 38:
						codeStr = ClearCodeDef.F38_POWER_LIMITED_TIME;
						countContent = 4;
						break;
					}
					this.tdType = ClearCodeDef.TD_M;
					break;
				case 5: {
					if (fn > 44)
						return -1;
					switch (fn) {
					case 41:
						codeStr = ClearCodeDef.F41_CAPACITOR;
						countContent = 72;
						break;
					case 42:
						codeStr = ClearCodeDef.F42_REACTIVE_POWER;
						countContent = 8;
						break;
					case 43:
						codeStr = ClearCodeDef.F43_CUMULATIVE_TIME;
						countContent = 6;
						break;
					case 44:
						codeStr = ClearCodeDef.F44_CUMULATIVE_TIME_MONTH;
						countContent = 6;
						break;
					}
					if (fn == 44)
						this.tdType = ClearCodeDef.TD_M;
				}
					break;
				case 6: {
					if (fn > 52)
						return -1;
					switch (fn) {
					case 49:
						codeStr = ClearCodeDef.F49_TERMINAL_PARAM;
						break;
					case 50:
						codeStr = ClearCodeDef.F50_TERMINAL_PARAM;
						break;
					case 51:
						codeStr = ClearCodeDef.F51_TERMINAL_PARAM;
						break;
					case 52:
						codeStr = ClearCodeDef.F52_TERMINAL_PARAM;
						break;
					}
					countContent = 4;
					this.pnType = ClearCodeDef.P0_TYPE;
					if (fn == 51 || fn == 52)
						this.tdType = ClearCodeDef.TD_M;
				}
					break;
				case 7: {
					if (fn >= 63)
						return -1;
					switch (fn) {
					case 57:
						codeStr = ClearCodeDef.F57_TOTAL_POWER;
						countContent = 12;
						break;
					case 58:
						codeStr = ClearCodeDef.F58_TOTAL_AP_ENERGY;
						countType = 2;
						break;
					case 59:
						codeStr = ClearCodeDef.F59_TOTAL_RP_ENERGY;
						countType = 2;
						break;
					case 60:
						codeStr = ClearCodeDef.F60_TOTAL_POWER;
						countContent = 12;
						break;
					case 61:
						codeStr = ClearCodeDef.F61_TOTAL_AP_ENERGY;
						countType = 2;
						break;
					case 62:
						codeStr = ClearCodeDef.F62_TOTAL_RP_ENERGY;
						countType = 2;
						break;
					}
					this.pnType = ClearCodeDef.ZJ_TYPE;
					if (fn >= 60)
						this.tdType = ClearCodeDef.TD_M;
				}
					break;
				case 8: {
					if (fn >= 67)
						return -1;
					switch (fn) {
					case 65:
						codeStr = ClearCodeDef.F65_TOTAL_POWER_ENERGY;
						break;
					case 66:
						codeStr = ClearCodeDef.F66_TOTAL_ENERGY_ENERGY;
						break;
					}
					countContent = 6;
					this.pnType = ClearCodeDef.ZJ_TYPE;
					this.tdType = ClearCodeDef.TD_M;

				}
					break;
				case 9: {
					if (fn >= 77)
						return -1;
					switch (fn) {
					case 73:
						codeStr = ClearCodeDef.F73_TOTAL_AP_CURVE;
						bytLength = 2;
						valueType = ConstDef.TOTAL_AP;
						break;
					case 74:
						codeStr = ClearCodeDef.F74_TOTAL_RP_CURVE;
						bytLength = 2;
						valueType = ConstDef.TOTAL_RP;
						break;
					case 75:
						codeStr = ClearCodeDef.F75_TOTAL_AE_CURVE;
						bytLength = 4;
						valueType = ConstDef.TOTAL_AP;
						break;
					case 76:
						codeStr = ClearCodeDef.F76_TOTAL_RE_CURVE;
						bytLength = 4;
						valueType = ConstDef.TOTAL_RP;
						break;
					}
					this.countType = 3;
					this.pnType = ClearCodeDef.ZJ_TYPE;
					this.tdType = ClearCodeDef.TD_C;
				}
					break;
				case 10:
					switch (fn) {
					case 81:
						codeStr = ClearCodeDef.F81_POINT_CURVE;
						valueType = ConstDef.ACTIVE_POWER;
						break;
					case 82:
						codeStr = ClearCodeDef.F82_POINT_CURVE;
						valueType = ConstDef.A_ACTIVE_POWER;
						break;
					case 83:
						codeStr = ClearCodeDef.F83_POINT_CURVE;
						valueType = ConstDef.B_ACTIVE_POWER;
						break;
					case 84:
						codeStr = ClearCodeDef.F84_POINT_CURVE;
						valueType = ConstDef.C_ACTIVE_POWER;
						break;
					case 85:
						codeStr = ClearCodeDef.F85_POINT_CURVE;
						valueType = ConstDef.REACTIVE_POWER;
						break;
					case 86:
						codeStr = ClearCodeDef.F86_POINT_CURVE;
						valueType = ConstDef.A_REACTIVE_POWER;
						break;
					case 87:
						codeStr = ClearCodeDef.F87_POINT_CURVE;
						valueType = ConstDef.B_REACTIVE_POWER;
						break;
					case 88:
						codeStr = ClearCodeDef.F88_POINT_CURVE;
						valueType = ConstDef.C_REACTIVE_POWER;
						break;
					}
					this.bytLength = 3;
					this.countType = 3;
					this.tdType = ClearCodeDef.TD_C;
					break;
				case 11: {
					if (fn == 96)
						return -1;
					switch (fn) {
					case 89:
						codeStr = ClearCodeDef.F89_POINT_CURVE;
						valueType = ConstDef.A_PHASE;
						break;
					case 90:
						codeStr = ClearCodeDef.F90_POINT_CURVE;
						valueType = ConstDef.B_PHASE;
						break;
					case 91:
						codeStr = ClearCodeDef.F91_POINT_CURVE;
						valueType = ConstDef.C_PHASE;
						break;
					case 92:
						codeStr = ClearCodeDef.F92_POINT_CURVE;
						valueType = ConstDef.A_PHASE;
						break;
					case 93:
						codeStr = ClearCodeDef.F93_POINT_CURVE;
						valueType = ConstDef.B_PHASE;
						break;
					case 94:
						codeStr = ClearCodeDef.F94_POINT_CURVE;
						valueType = ConstDef.C_PHASE;
						break;
					case 95:
						codeStr = ClearCodeDef.F95_POINT_CURVE;
						valueType = ConstDef.POWER_DIRECTOR;
						break;
					}
					this.bytLength = 2;
					this.countType = 3;
					this.tdType = ClearCodeDef.TD_C;
				}
					break;
				case 12:
					switch (fn) {
					case 97:
						codeStr = ClearCodeDef.F97_POINT_CURVE;
						valueType = ConstDef.PAP;
						break;
					case 98:
						codeStr = ClearCodeDef.F98_POINT_CURVE;
						valueType = ConstDef.PRP;
						break;
					case 99:
						codeStr = ClearCodeDef.F99_POINT_CURVE;
						valueType = ConstDef.RAP;
						break;
					case 100:
						codeStr = ClearCodeDef.F100_POINT_CURVE;
						valueType = ConstDef.RRP;
						break;
					case 101:
						codeStr = ClearCodeDef.F101_POINT_CURVE;
						valueType = ConstDef.PAP;
						break;
					case 102:
						codeStr = ClearCodeDef.F102_POINT_CURVE;
						valueType = ConstDef.PRP;
						break;
					case 103:
						codeStr = ClearCodeDef.F103_POINT_CURVE;
						valueType = ConstDef.RAP;
						break;
					case 104:
						codeStr = ClearCodeDef.F104_POINT_CURVE;
						valueType = ConstDef.RRP;
						break;
					}
					this.bytLength = 4;
					this.countType = 3;
					this.tdType = ClearCodeDef.TD_C;
					break;
				case 13: {
					if (fn > 108)
						return -1;
					switch (fn) {
					case 105:
						codeStr = ClearCodeDef.F105_POINT_CURVE;
						valueType = ConstDef.POWER_DIRECTOR;
						break;
					case 106:
						codeStr = ClearCodeDef.F106_POINT_CURVE;
						valueType = ConstDef.A_PHASE;
						break;
					case 107:
						codeStr = ClearCodeDef.F107_POINT_CURVE;
						valueType = ConstDef.B_PHASE;
						break;
					case 108:
						codeStr = ClearCodeDef.F108_POINT_CURVE;
						valueType = ConstDef.C_PHASE;
						break;
					}
					this.bytLength = 2;
					this.countType = 3;
					this.tdType = ClearCodeDef.TD_C;
				}
					break;
				case 14:
					if (fn >= 119)
						return -1;
					switch (fn) {
					case 113:
						codeStr = ClearCodeDef.F113_POINT_HARMONIC_CURRENT;
						break;
					case 114:
						codeStr = ClearCodeDef.F114_POINT_HARMONIC_CURRENT;
						break;
					case 115:
						codeStr = ClearCodeDef.F115_POINT_HARMONIC_CURRENT;
						break;
					case 116:
						codeStr = ClearCodeDef.F116_POINT_HARMONIC_VOLTAGE;
						break;
					case 117:
						codeStr = ClearCodeDef.F117_POINT_HARMONIC_VOLTAGE;
						break;
					case 118:
						codeStr = ClearCodeDef.F118_POINT_HARMONIC_VOLTAGE;
						break;
					}
					countContent = 114;
					break;
				case 15:
					if (fn >= 124)
						return -1;
					switch (fn) {
					case 121:
						codeStr = ClearCodeDef.F121_POINT_HARMONIC_STATIS;
						break;
					case 122:
						codeStr = ClearCodeDef.F122_POINT_HARMONIC_STATIS;
						break;
					case 123:
						codeStr = ClearCodeDef.F123_POINT_HARMONIC_STATIS;
						break;
					}
					this.countType = 4;
					break;
				case 16: {
					if (fn >= 131)
						return -1;
					switch (fn) {
					case 129:
						codeStr = ClearCodeDef.F129_DC_SIMULATION_LIMITED;
						break;
					case 130:
						codeStr = ClearCodeDef.F130_DC_SIMULATION_LIMITED;
						break;
					}
					countContent = 14;
					this.pnType = ClearCodeDef.ZLDK_TYPE;
					if (fn == 130)
						this.tdType = ClearCodeDef.TD_M;
				}
					break;
				case 17: {
					if (fn == 138) {
						this.pnType = ClearCodeDef.ZLDK_TYPE;
						this.tdType = ClearCodeDef.TD_C;
						codeStr = ClearCodeDef.F138_DC_SIMULATION_HARMONIC;
						this.countType = 3;
						this.bytLength = 2;
					} else
						return -1;
				}
					break;
				default:
					return -1;
				}
				return 1;
			}
			return -1;
		}

		public short getGroup() {
			return group;
		}

		public void setGroup(short group) {
			this.group = group;
		}

		public int getTdType() {
			return tdType;
		}

		public void setTdType(int tdType) {
			this.tdType = tdType;
		}

		public int getPnType() {
			return pnType;
		}

		public void setPnType(int pnType) {
			this.pnType = pnType;
		}

		public String[] getCodeStr() {
			return codeStr;
		}

		public void setCodeStr(String[] codeStr) {
			this.codeStr = codeStr;
		}

		public int getCountContent() {
			return countContent;
		}

		public void setCountContent(int countContent) {
			this.countContent = countContent;
		}

		public int getCountType() {
			return countType;
		}

		public void setCountType(int countType) {
			this.countType = countType;
		}

		public int getBytLength() {
			return bytLength;
		}

		public void setBytLength(int bytLength) {
			this.bytLength = bytLength;
		}

		public int getValueType() {
			return valueType;
		}

		public void setValueType(int valueType) {
			this.valueType = valueType;
		}

	}

	public class C3DataUnit {
		private String codeStr[];
		private int pnType;

		public C3DataUnit() {

		}

		public int getC3DU(int eventType) {
			codeStr = null;
			pnType = ClearCodeDef.P0_TYPE;
			if (eventType > 0 && eventType < 31) {
				switch (eventType) {
				case 1:
					codeStr = ClearCodeDef.ERC1_Fk04;
					break;
				case 2:
					codeStr = ClearCodeDef.ERC2_Fk04;
					break;
				case 3:
					codeStr = ClearCodeDef.ERC3_Fk04;
					break;
				case 4:
					codeStr = ClearCodeDef.ERC4_Fk04;
					break;
				case 5:
					codeStr = ClearCodeDef.ERC5_Fk04;
					break;
				case 6:
					codeStr = ClearCodeDef.ERC6_Fk04;
					pnType = ClearCodeDef.ZJ_TYPE;
					break;
				case 7:
					codeStr = ClearCodeDef.ERC7_Fk04;
					pnType = ClearCodeDef.ZJ_TYPE;
					break;
				case 8:
					codeStr = ClearCodeDef.ERC8_Fk04;
					pnType = ClearCodeDef.CL_TYPE;
					break;
				case 9:
					codeStr = ClearCodeDef.ERC9_Fk04;
					pnType = ClearCodeDef.CL_TYPE;
					break;
				case 10:
					codeStr = ClearCodeDef.ERC10_Fk04;
					pnType = ClearCodeDef.CL_TYPE;
					break;
				case 11:
					codeStr = ClearCodeDef.ERC11_Fk04;
					pnType = ClearCodeDef.CL_TYPE;
					break;
				case 12:
					codeStr = ClearCodeDef.ERC12_Fk04;
					pnType = ClearCodeDef.CL_TYPE;
					break;
				case 13:
					codeStr = ClearCodeDef.ERC13_Fk04;
					pnType = ClearCodeDef.CL_TYPE;
					break;
				case 14:
					codeStr = ClearCodeDef.ERC14_Fk04;
					break;
				case 15:
					codeStr = ClearCodeDef.ERC15_Fk04;
					pnType = ClearCodeDef.CL_TYPE;
					break;
				case 16:
					codeStr = ClearCodeDef.ERC16_Fk04;
					pnType = ClearCodeDef.ZLDK_TYPE;
					break;
				case 17:
					codeStr = ClearCodeDef.ERC17_Fk04;
					pnType = ClearCodeDef.CL_TYPE;
					break;
				case 18:
					codeStr = ClearCodeDef.ERC18_Fk04;
					pnType = ClearCodeDef.CL_TYPE;
					break;
				case 19:
					codeStr = ClearCodeDef.ERC19_Fk04;
					pnType = ClearCodeDef.ZJ_TYPE;
					break;
				case 20:
					codeStr = ClearCodeDef.ERC20_Fk04;
					break;
				case 21:
					codeStr = ClearCodeDef.ERC21_Fk04;
					break;
				case 22:
					codeStr = ClearCodeDef.ERC22_Fk04;
					break;
				case 24:
					codeStr = ClearCodeDef.ERC24_Fk04;
					pnType = ClearCodeDef.CL_TYPE;
					break;
				case 25:
					codeStr = ClearCodeDef.ERC25_Fk04;
					pnType = ClearCodeDef.CL_TYPE;
					break;
				case 26:
					codeStr = ClearCodeDef.ERC26_Fk04;
					pnType = ClearCodeDef.CL_TYPE;
					break;
				case 27:
					codeStr = ClearCodeDef.ERC27_Fk04;
					break;
				case 28:
					codeStr = ClearCodeDef.ERC28_Fk04;
					pnType = ClearCodeDef.CL_TYPE;
					break;
				case 29:
					codeStr = ClearCodeDef.ERC29_Fk04;
					pnType = ClearCodeDef.CL_TYPE;
					break;
				case 30:
					codeStr = ClearCodeDef.ERC30_Fk04;
					pnType = ClearCodeDef.CL_TYPE;
					break;
				default:
					return -1;
				}
				return 1;
			}
			return -1;
		}

		public String[] getCodeStr() {
			return codeStr;
		}

		public void setCodeStr(String[] codeStr) {
			this.codeStr = codeStr;
		}

		public int getPnType() {
			return pnType;
		}

		public void setPnType(int pnType) {
			this.pnType = pnType;
		}

	}

	public static boolean allDigits(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isDigit(s.charAt(0)))
				return false;
		}
		return true;
	}

	public static void main(String args[]) {
		GxFunctions func = new GxFunctions();
		int temp = func.TransBinayToBcd((byte) 22);
//		log.debug("temp=" + temp);
		try {
			int bb = func.TransBcdToBinay((byte) 0x97);
//			log.debug("bb=" + bb);
//			log.debug("test=" + func.getInt((byte) 0xfe));
			byte tt = (byte) 128;
//			log.debug("tt=" + (tt + 1));
			// byte date15[] = { 0x56, 0x13, 0x01, 0x02, 0x10 };
			// Date datetest = func.getDate15(date15, 0);
			// Calendar cl = Calendar.getInstance();
			// cl.setTime(datetest);
			// log.debug("year=" + (cl.get(Calendar.YEAR) - 2000));
			// log.debug("month=" + (cl.get(Calendar.MONTH) + 1));
			// log.debug("day=" + cl.get(Calendar.DAY_OF_MONTH));
			// log.debug("hour=" + cl.get(Calendar.HOUR_OF_DAY));
			// log.debug("minute=" + cl.get(Calendar.MINUTE));
			byte data14[] = { 0x00, 0x57, 0x43, 0x68, 0x00 };
			double v14 = func.TransData14(data14, 0);
//			log.debug("TransData14=" + v14);
		} catch (Exception e) {
//			log.error("",e);
		}
	}
}
