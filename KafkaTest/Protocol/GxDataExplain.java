package Protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nari.commObjectPara.ConstDef;
import com.nari.commObjectPara.TaskQueueObject;
import com.nari.fe.commdefine.task.DataCode;
import com.nari.fe.commdefine.task.DbData;
import com.nari.fe.commdefine.task.Response;
import com.nari.global.ShareGlobalObj;
import com.nari.protocol.Fk05Protocol.Fk05DataExplain;
import com.nari.protocolBase.AsduConverter;

public class GxDataExplain {
	private static Logger log = LoggerFactory.getLogger(GxDataExplain.class);

	private ArrayList<DbData> g_responseDbDataList = new ArrayList<DbData>();
	private ShareGlobalObj shareGlobalObjVar = ShareGlobalObj.getInstance();

	public GxDataExplain() {

	}

	// 计算2的倍数
	private long pow(int rate) {
		long retVal = 1;

		for (int k = 0; k < rate; k++) {
			retVal = retVal * 2;
		}

		return retVal;
	}

	private ArrayList<Short> getPnVal(byte dadt1, byte dadt2) {
		shareGlobalObjVar.writeLog(ConstDef.FRONTDOWN_LOG, "dadt1=" + dadt1 + "dadt2=" + dadt2,
				null);

		ArrayList<Short> pnFnValList = new ArrayList<Short>();
		short pnFnVal = 0;
		pnFnValList.clear();
		int step = 1;
		for (short k = 0; k < 8; k++) {
			pnFnVal = (short) ((dadt2 & 0xff - 1) * 8 + 1);
			step = (int) pow(k);
			if (((dadt1 & 0xff) & step) == step) {
				pnFnVal += k;
				pnFnValList.add(pnFnVal);
			}
		}
		pnFnValList.add(pnFnVal);
		return pnFnValList;
	}

	private ArrayList<Short> getFnVal(byte dadt1, byte dadt2) {

		log.debug("dadt1=" + dadt1 + "dadt2=" + dadt2);
		ArrayList<Short> pnFnValList = new ArrayList<Short>();
		short pnFnVal = 0;
		pnFnValList.clear();
		int step = 1;
		for (short k = 0; k < 8; k++) {
			pnFnVal = (short) ((dadt2 & 0xff - 1) * 8 + 1);
			step = (int) pow(k);
			if (((dadt1 & 0xff) & step) == step) {
				pnFnVal += k;
				pnFnValList.add(pnFnVal);
			}
		}
		return pnFnValList;
	}

	private void parseBuff2Data(short pointNo, short fnNo, byte[] buff, List<DataCode> dataCodes) {
		return;
	}

	public Response explainData(TaskQueueObject taskObj) {
		g_responseDbDataList.clear();

		byte packetBuff[] = null;
		Response resPara = new Response();
		ArrayList<DbData> dbDatasList = new ArrayList<DbData>();

		ArrayList<Short> pointNoList = new ArrayList<Short>();
		ArrayList<Short> fnNoList = new ArrayList<Short>();
		String protoType = taskObj.getTermTask().getProtocol();

		if (taskObj.getTaskMessage() == null) {
			log.debug("explainData()==null");
			return null;
		}
		try {
			// packetBuff = taskObj.getTaskMessage().getBytes("ISO-8859-1");
			packetBuff = taskObj.getTaskMessage();
		} catch (Exception ex) {
//			Logger.getLogger(AsduConverter.class.getName()).log(Level.SEVERE, null, ex);
		}

		byte afnNo = packetBuff[12];// AFN
		int frameIndex = 14;
		short pointNo = 0;
		short fnNo = 0;
		log.debug("explainData:frameIndex==" + frameIndex + "packetBuff.length="
				+ packetBuff.length);
		if (frameIndex < packetBuff.length) {
			byte tmpDa1 = packetBuff[frameIndex++];// DA1
			byte tmpDa2 = packetBuff[frameIndex++];// DA2
			byte tmpDt1 = packetBuff[frameIndex++];// DT1
			byte tmpDt2 = packetBuff[frameIndex++];// DT2
			// 信息点标识pn
			pointNoList = getPnVal(tmpDa1, tmpDa2);
			// 信息点标识fn
			fnNoList = getFnVal(tmpDt1, tmpDt2);

			DbData dbData = new DbData();
			for (int p = 0; p < pointNoList.size(); p++) {
				log.debug("pointNoList.size():" + pointNoList.size());
				pointNo = pointNoList.get(p);
				log.debug("pointNo:" + pointNo);
				log.debug("fnNoList.size():" + fnNoList.size());

				//dbData.setAreaCode("1");
				dbData.setOrgNo("1");
				dbData.setCT(1);
				dbData.setDataId(123456);
				dbData.setMark(0);
				dbData.setPT(1);
				dbData.setTime(new Date(System.currentTimeMillis()));

				ArrayList<DataCode> dataCodes = new ArrayList<DataCode>();
				for (int m = 0; m < fnNoList.size(); m++) {
					fnNo = fnNoList.get(m);
					log.debug("fnNo:" + fnNo);
					String funCodeInProto = String.format("%01X%02X%02X", protoType, afnNo, fnNo);// 功能规约项编码
					log.debug("funCodeInProto:" + funCodeInProto);
					parseBuff2Data(pointNo, fnNo, taskObj.getTaskMessage(), dataCodes);
				}
				dbData.setDataCodes(dataCodes);
			}
			dbDatasList.add(dbData);
		}// end of while

		resPara.setDbDatas(dbDatasList);

		log.debug("g_responseDbDataList.size()==" + g_responseDbDataList.size());

		return resPara;

	}
}
