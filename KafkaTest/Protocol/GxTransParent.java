package Protocol;

import com.nari.protocol.Dl698Protocol.Dl698TransParent;

public class GxTransParent extends Dl698TransParent{
//	private Fk04Functions func = new Fk04Functions();
//	public static final int STEP_ASKMETERNO = 0; //读表号
//	public static final int STEP_IDENTITYAUTHENTICATION = 1; //身份认证
//	public static final int STEP_OPERATE = 2;      //控制命令
//	public static final int STEP_SET_PARAM = 3;    //1，2类参数设置
//	public static final int STEP_READ_METER = 4;   //读电表数据
//	public static final int STEP_SET_IDENTITY_TIME = 5; //设置身份认证有效时长
//	public static final int STEP_CONTROL_BY_OPEN_KEY = 6; //明文控制
//
//	private static Logger log = LoggerFactory.getLogger(Fk04TransParent.class);
//	private ShareGlobalObj shareGlobalObjVar = ShareGlobalObj.getInstance();
//	private IMPacket imPack = new IMPacket();
//	private DataForward645Report report = new DataForward645Report();
//	
//	public Fk04TransParent() {
//		
//	}
//
//
//	/**
//	 * 获取下发的电能表信息及功能号
//	 * @param taskId
//	 * @param meterInfos
//	 * @return 成功则返回功能号，否则为负值
//	 */
//	public int getMeterInfosByTaskID(long taskId, List<MeterInfo> meterInfos) {
//		TaskInfo taskInfo = null;
//		ITaskHandle taskhandle = null;
//		String localName = "";
//		InetAddress ia = null;
//		int pointNo = 0;
//		int funNo = 0;
//		try {
//			ia = shareGlobalObjVar.getLocalInetAddress();
//			localName = ia.getHostName().toLowerCase();
//			taskhandle = TaskHandle.getSharedInstance(localName);
//			taskInfo = taskhandle.getTaskInfo(taskId);
//
//			if (taskInfo != null) {
//				log.debug("taskId:" + taskId + "任务信息：" + taskInfo.toString());
//			} else {
//				log.error("taskInfo==null");
//				return -1;
//			}
//		} catch (Exception e) {
//			log.error("", e);
//			return -1;
//		}
//
//		Map<Short, Map<Short, List<Item>>> pointMap = taskInfo.getPnFuncs();
//		for (Map.Entry<Short, Map<Short, List<Item>>> pointEntry : pointMap
//				.entrySet()) {
//			pointNo = pointEntry.getKey().shortValue();// 测量点号
//			log.debug("pointNo=" + pointNo);
//			Map<Short, List<Item>> funMap = pointEntry.getValue();
//
//			for (Map.Entry<Short, List<Item>> funEntry : funMap.entrySet()) {
//				funNo = funEntry.getKey().shortValue();
//				log.debug("funNo=" + funNo);
//				if(taskInfo.getMeterInfos() != null || taskInfo.getMeterInfos().size() >0){
//					for(int i = 0; i < taskInfo.getMeterInfos().size(); i++){
//						meterInfos.add(taskInfo.getMeterInfos().get(i));
//					}
//				}
//				return funNo;
//			}
//		}
//		return -2;
//	}
//	
//	private byte[] getfsyz(byte[] ammeterAddr){
//		byte[] fsyz = new byte[8];
//		System.arraycopy(ammeterAddr, 0, fsyz, 8-ammeterAddr.length, ammeterAddr.length);
//		return fsyz;
//	}
//	
//	public byte[] getfsyz(String AmmeterAddr){
//		
//		byte[] fsyz = new byte[8];
//		
//		byte[]  AmmeterAddrbyte = HexDump.hexStringToByteArray(AmmeterAddr);
//		int len = AmmeterAddrbyte.length;
//		log.debug("取分散因子，电表字符长度:"+ len);
//		for (int j = 7 ; j>= 0 ; j--){
//			fsyz[j] = 0x00;
//		}
//		
//		System.arraycopy(AmmeterAddrbyte, 0,fsyz, 8-len,  len);
//
//		return fsyz;
//	}
//
//	public byte[] identityTest(){
//		//68 92 96 26 00 00 01  68 11 04 33 32 34 33 00 16
//		int index =0;
//		int bufSize =17;
//		byte[] buff = new byte[bufSize]; 
//		
//		buff[index++] = 0x68;
//		buff[index++] = (byte) 0x92;
//		buff[index++] = (byte) 0x96;
//		buff[index++] = 0x26;
//		buff[index++] = 0x00;
//		buff[index++] = 0x00;
//		buff[index++] = 0x01;
//		buff[index++] = 0x00;
//		buff[index++] = 0x68;
//		buff[index++] = 0x11;
//		buff[index++] = 0x04;
//		buff[index++] = 0x33;
//		buff[index++] = 0x32;
//		buff[index++] = 0x34;
//		buff[index++] = 0x33;
//		buff[index++] = 0x00;
//		buff[index++] = 0x16;
//		
//		return buff;
//	}
//	
//	public byte[] ass645ReadingDataReport(String meterAddr,String flag){
//		
//		byte[] sendBuff = new byte[512];
//		int indexNo = 0;
//		
//		while(meterAddr.length()<12){
//			meterAddr = "0" + meterAddr;
//		}
//		byte[] tmpMeterAddr = HexDump.hexStringToByteArray(meterAddr);
//		sendBuff[indexNo++] = 0x68;
//		for(int i =5; i >= 0; i--){
//			sendBuff[indexNo++] = tmpMeterAddr[i];
//		}
//		sendBuff[indexNo++] = 0x68;
//		sendBuff[indexNo++] = 0x11;
//		sendBuff[indexNo++] = 0x04;
//		
////		byte[] dataFlag = new Fk04ReceivePacket().Init_DataFlag(flag);  //wyx
////		if(dataFlag == null){
////			log.error("不能识别的数据标识定义：" + flag);
////		}
////		for(int i =0; i < 4; i++){
////			sendBuff[indexNo++] = (byte)(dataFlag[i] + 0x33);
////		}
//		sendBuff[indexNo] = 0;
//		for(int cs = 0; cs < indexNo; cs++){
//			sendBuff[indexNo] += sendBuff[cs];
//		}
//		indexNo++;
//		sendBuff[indexNo++] = 0x16;
//		
//		byte[] retBuff = new byte[indexNo];
//		System.arraycopy(sendBuff, 0, retBuff, 0, indexNo);
//		log.debug("××××××××××645召测表数据" + HexDump.toHexString(retBuff).toString());
//		return retBuff;
//	}
//	
//	public byte[] assAmeterNoReport(String meterAddr){
//		byte[] sendBuff = new byte[512];
//		int indexNo = 0;
//		
//		while(meterAddr.length()<12){
//			meterAddr = "0" + meterAddr;
//		}
//		byte[] tmpMeterAddr = HexDump.hexStringToByteArray(meterAddr);
//		sendBuff[indexNo++] = 0x68;
//		for(int i =5; i >= 0; i--){
//			sendBuff[indexNo++] = tmpMeterAddr[i];
//		}
//		sendBuff[indexNo++] = 0x68;
//		sendBuff[indexNo++] = 0x11;
//		sendBuff[indexNo++] = 0x04;
//		
//		sendBuff[indexNo++] = 0x02 + 0x33;
//		sendBuff[indexNo++] = 0x04 + 0x33;
//		sendBuff[indexNo++] = 0x00 + 0x33;
//		sendBuff[indexNo++] = 0x04 + 0x33;
//		sendBuff[indexNo] = 0;
//		for(int cs = 0; cs < indexNo; cs++){
//			sendBuff[indexNo] += sendBuff[cs];
//		}
//		indexNo++;
//		sendBuff[indexNo++] = 0x16;
//		
//		byte[] retBuff = new byte[indexNo];
//		System.arraycopy(sendBuff, 0, retBuff, 0, indexNo);
//		log.debug("××××××××××645召测表号" + HexDump.toHexString(retBuff).toString());
//		return retBuff;
//	}
//	
//	public byte[] assControlMeterByOpenKey(String meterAddr, String controlType,int delay){
//		byte[] PAP0P1P2 = new byte[]{02,00,00,00};
//		return assControlMeterByOpenKey(meterAddr, controlType,PAP0P1P2, delay);
//	}
//	
//	/**
//	 * 组装645明文控制报文
//	 * @param meterAddr 电表地址 
//	 * @param controlType 控制类型
//	 * @param PAP0P1P2 操作密码
//	 * @return 组装后的明文
//	 */
//	public byte[] assControlMeterByOpenKey(String meterAddr, String controlType,byte[] PAP0P1P2,int delay){
//		byte[] sendBuff = new byte[512];
//		int frameIndex = 0;
//		sendBuff[frameIndex++] = 0x68;
//		int al, ah;
//		for(int i = meterAddr.length()-1; i>=1; i-=2){
//			al = Character.digit(meterAddr.charAt(i), 16);
//			ah = Character.digit(meterAddr.charAt(i-1), 16);
//			sendBuff[frameIndex++] = (byte)((0xf0 & (ah << 4)) | (al & 0x0f));
//		}
//		sendBuff[frameIndex++] = 0x68;
//		if(controlType == null){
//			return null;
//		}
//		sendBuff[frameIndex++] = 0x1C; //控制码
//		sendBuff[frameIndex++] = 0x10; //共16个字节
//		sendBuff[frameIndex++] = PAP0P1P2[0];
//		sendBuff[frameIndex++] = PAP0P1P2[1];
//		sendBuff[frameIndex++] = PAP0P1P2[2];
//		sendBuff[frameIndex++] = PAP0P1P2[3];
//		sendBuff[frameIndex++] = 0x78;
//		sendBuff[frameIndex++] = 0x56;
//		sendBuff[frameIndex++] = 0x34;
//		sendBuff[frameIndex++] = 0x12;
//		//控制类型
//		if(controlType.equalsIgnoreCase("01")){
//			sendBuff[frameIndex++] = 0x2A;
//		}else if(controlType.equalsIgnoreCase("02")){
//			sendBuff[frameIndex++] = 0x2B;
//		}else if(controlType.equalsIgnoreCase("03")){
//			sendBuff[frameIndex++] = 0x1B;
//		}else if(controlType.equalsIgnoreCase("04")){
//			sendBuff[frameIndex++] = 0x1A;
//		}else if(controlType.equalsIgnoreCase("05")){
//			sendBuff[frameIndex++] = 0x3A;
//		}else if(controlType.equalsIgnoreCase("06")){
//			sendBuff[frameIndex++] = 0x3B;
//		}else if(controlType.equalsIgnoreCase("07")){
//			sendBuff[frameIndex++] = 0x1C;
//		}else if(controlType.equalsIgnoreCase("08")){
//			sendBuff[frameIndex++] = 0x1D;
//		}else if(controlType.equalsIgnoreCase("09")){
//			sendBuff[frameIndex++] = 0x1E;
//		}
////		sendBuff[frameIndex++] = 0x00;//保留
//		sendBuff[frameIndex++] = (byte)delay;
//		
//		//命令失效时间
//		Calendar c = Calendar.getInstance();
//		c.add(Calendar.HOUR_OF_DAY, 1);
//		sendBuff[frameIndex++] = (byte)((c.get(Calendar.SECOND)/10)*16 + (c.get(Calendar.SECOND)%10));
//		sendBuff[frameIndex++] = (byte)((c.get(Calendar.MINUTE)/10)*16 +(c.get(Calendar.MINUTE)%10));
//		sendBuff[frameIndex++] = (byte)((c.get(Calendar.HOUR_OF_DAY)/10)*16 + (c.get(Calendar.HOUR_OF_DAY)%10));
//		sendBuff[frameIndex++] = (byte)((c.get(Calendar.DAY_OF_MONTH)/10)*16 + (c.get(Calendar.DAY_OF_MONTH)%10));
//		sendBuff[frameIndex++] = (byte)(((c.get(Calendar.MONTH)+1)/10)*16 + ((c.get(Calendar.MONTH)+1)%10));
//		sendBuff[frameIndex++] = (byte)(((c.get(Calendar.YEAR)-2000)/10)*16 + ((c.get(Calendar.YEAR)-2000)%10));
//		
//		for(int i = 10; i <= frameIndex; i++){
//			sendBuff[i] += 0x33;
//		}
//		int cs = 0;
//		for(int i = 0; i < frameIndex; i++){
//			cs += sendBuff[i];
//		}
//		
//		sendBuff[frameIndex++] = (byte)cs;
//		sendBuff[frameIndex++] = 0x16;
//		
//		byte[] retBuff = new byte[frameIndex];
//		System.arraycopy(sendBuff, 0, retBuff, 0, frameIndex);
//		return retBuff;
//	}
//	
//	/**
//	 * 组装身份认证报文
//	 * 
//	 * @param meterAddr
//	 * @return
//	 */
//	public byte[] assIdentityAuthenticationReport(String meterAddr,
//			byte[] random1, byte[] pwdReport, byte[] fsyz) throws Exception {
//
//		byte[] sendBuff = new byte[512];
//		int counter = 0;
//		sendBuff[counter++] = 0x68;
//
//		int AmmeterAddr_len = meterAddr.length();
//		Integer[] AmmeterAddr_arra_i = null;
//		AmmeterAddr_arra_i = new Integer[12];
//		for (int ai_i = 0; ai_i < 12; ai_i++) {
//			AmmeterAddr_arra_i[ai_i] = 0;
//		}
//
//		char[] AmmeterAddr_arra_c = null;
//		AmmeterAddr_arra_c = new char[AmmeterAddr_len];
//
//		AmmeterAddr_arra_c = meterAddr.toCharArray();
//
//		int tmpAddr1 = 0;
//		int tmpAddr2 = 0;
//		byte tmpb = 0x00;
//
//		for (int i = AmmeterAddr_len - 1; i >= 0; i -= 2) {
//			tmpAddr1 = Character.digit(AmmeterAddr_arra_c[i], 16);
//			tmpAddr2 = Character.digit(AmmeterAddr_arra_c[i - 1], 16);
//			if (-1 != tmpAddr1) {
//				tmpb = (byte) (0x0f & tmpAddr1);
//				sendBuff[counter++] = (byte) ((byte) (0xf0 & (tmpAddr2 << 4)) | tmpb);
//			} else {
//				;
//			}
//		}
//
//		// 不足补0X00
//		for (int j = 0; j < 6 - (AmmeterAddr_len / 2); j++) {
//			sendBuff[counter++] = (byte) 0x00;
//		}
//		sendBuff[counter++] = 0x68;
//
//		sendBuff[counter++] = 0x03;
//
//		byte[] databuf = null;
//		try {
//			databuf = assIdentityAuthentication_DataReport(random1, pwdReport,
//					fsyz);
//		} catch (Exception e) {
//			throw new Exception(e);
//		}
//		if (databuf == null) {
//			throw new Exception("组织报文为空");
//		} else if (databuf.length <= 0) {
//			throw new Exception("组织报文长度为零");
//		}
//
//		int dataLen = databuf.length;
//		data_add_33h(databuf, dataLen);// 数据区加33H
//		sendBuff[counter++] = (byte) (dataLen & 0xff);
//		System.arraycopy(databuf, 0, sendBuff, counter, dataLen);
//		counter += dataLen;
//
//		// 计算长度
//		int tmpLength = counter - 1;
//		// sendBuff[frameIndexNo++] = 0x00;
//		sendBuff[tmpLength + 1] = 0x0;
//
//		for (int csi = 0; csi <= tmpLength; csi++) {
//			sendBuff[tmpLength + 1] += sendBuff[csi]; // CS
//		}
//		counter++;
//
//		sendBuff[counter++] = 0x16;
//		
//		byte[] buf = new byte[counter];
//		System.arraycopy(sendBuff, 0, buf, 0, counter);
//		log.debug("××××××××××645身份认证：" + HexDump.toHexString(buf).toString());
//		return buf;
//	}
//
//	/**
//	 * 组装身份认证数据区报文
//	 * 
//	 * @param meterAddr
//	 * @return
//	 */
//	public byte[] assIdentityAuthentication_DataReport(byte[] random1,
//			byte[] pwdReport, byte[] fsyz) throws Exception {
//		if (random1 == null || pwdReport == null || fsyz == null) {
//			log
//					.debug("random1==null || pwdReport == null || fsyz ==null in Dl698TransParent Error#1");
//			return null;
//		}
//		int lenth = random1.length + pwdReport.length + fsyz.length + 4 + 4;
//
//		byte[] random1buf = new byte[random1.length];
//		for (int i = 0; i < random1.length; i++) {
//			random1buf[i] = random1[random1.length - i-1];
//		}
//
//		byte[] pwdReportbuf = new byte[pwdReport.length];
//		for (int i = 0; i < pwdReport.length; i++) {
//			pwdReportbuf[i] = pwdReport[pwdReport.length - i-1];
//		}
//
//		byte[] fsyzbuf = new byte[fsyz.length];
//		for (int i = 0; i < fsyz.length; i++) {
//			fsyzbuf[i] = fsyz[fsyz.length - i-1];
//		}
//
//		byte[] buf = new byte[lenth];
//		int counter = 0;
//		// FF 00 00 07
//		buf[counter++] = (byte) 0xff;
//		buf[counter++] = (byte) 0x00;
//		buf[counter++] = (byte) 0x00;
//		buf[counter++] = (byte) 0x07;
//		// 78 56 34 12
//		buf[counter++] = (byte) 0x78;
//		buf[counter++] = (byte) 0x56;
//		buf[counter++] = (byte) 0x34;
//		buf[counter++] = (byte) 0x12;
//
//		// 密文
//		System.arraycopy(pwdReportbuf, 0, buf, counter, pwdReportbuf.length);
//		// 随机数
//		System.arraycopy(random1buf, 0, buf, counter + pwdReportbuf.length,
//				random1buf.length);
//		// 分散因子
//		System.arraycopy(fsyzbuf, 0, buf, counter + pwdReportbuf.length
//				+ random1buf.length, fsyzbuf.length);
//
//		return buf;
//	}
//
//	public int TransBinayToBcd(byte bin) {// 使用8421BCD码时一定要注意其有效的编码仅十个，即：0000～1001
//		return bin < 0 || bin > 99 ? 0xff : bin / 10 << 4 | bin % 10;
//	}
//	
//	
//	public ArrayList<TaskResultObject> retDataForwardF9(TaskQueueObject taskObj, byte[] packetBuff ){
//		log.debug("--Dl698Protocol 数据转发F09命令上行报文数据处理（AFN=10H）");
//		ArrayList<TaskResultObject> retObjList = null;
//		TaskResultObject resultObj = new TaskResultObject();
//		//18
//		int frameIndexNo = 18;
//		byte DuanKou = packetBuff[frameIndexNo++];
//		byte[] TargetAddr = new byte[6];
//		System.arraycopy(packetBuff, frameIndexNo, TargetAddr, 0, 6);
//		frameIndexNo += 6;
//		byte result = packetBuff[frameIndexNo++]; //转发结果标志
//		int resultLen = packetBuff[frameIndexNo++]; //转发结果数据长度
//		byte[] DataBiaoShi = new byte[4];
//		System.arraycopy(packetBuff, frameIndexNo, DataBiaoShi, 0, 4);
//		frameIndexNo += 4;
//		String value = new String();
//		for(int i =DataBiaoShi.length-1; i >= 0; i--){
//			String tmp = new String();
//			tmp = String.format("%02X", DataBiaoShi[i]);
//			value += tmp;
//		}
//		value += ":";
//		byte[] Data = new byte[resultLen - DataBiaoShi.length];
//		System.arraycopy(packetBuff, frameIndexNo, Data, 0, Data.length);
//		for(int i = Data.length-1; i >= 0; i--){
//			String tmp = new String();
//			tmp = String.format("%02X", (0xff &(Data[i]- 0x33)));
//			value += tmp;
//		}
//		
//		TerminalObject tmpTermObj = shareGlobalObjVar
//			.getTerminalPara(taskObj.getTermTask().getTerminalAddr());
//		if (tmpTermObj == null) {
//			log.error("tmpTermObj == null in Dl698SendPacker Error #1");
//			return null;
//		}
//		List<MeterInfo> meterInfos = tmpTermObj.getMeterInfos();
//		MeterInfo meterInfo = meterInfos.get(0); 
//		//String meterId = String.valueOf(meterInfo.getMeterID());
//		
//		Response response = new Response();
//		response.setContinue(false);
//		ResponseItem responseItems = new ResponseItem();
//		if(result == 0x03 || result == 0x05){
//			response.setErrorCode(ErrorCode.OK);
//			responseItems.setErrorCode((short)ErrorCode.OK.getId());
//		}else {
//			response.setErrorCode(ErrorCode.NakAnswer);
//			responseItems.setErrorCode((short)ErrorCode.NakAnswer.getId());
//		}
//		String note = null;
//		switch(result){
//		case 0x00:
//			note = "不能执行转发";
//			break;
//		case 0x01:
//			note = "转发接收超时";
//			break;
//		case 0x02:
//			note = "转发接收错误";
//			break;
//		case 0x03:
//			note = "转发接收确认";
//			break;
//		case 0x04:
//			note = "转发接收否认";
//			break;
//		case 0x05:
//			note = "转发接收数据";
//			break;
//			default:
//				log.error("没有说明的转发结果" + result);
//				note = "没有说明的转发结果";
//				note += result;
//				break;
//		}
//		
//		responseItems.setCode(meterInfos.get(0).getMeterAddr());
//		responseItems.setValue(value);
//		responseItems.setStatus((short)1);
//		responseItems.setPn((short)0);
//		responseItems.setFn((short)9);
//		
//		List<ResponseItem> itemList = new ArrayList<ResponseItem>();
//		itemList.add(responseItems);
//		response.setResponseItems(itemList);
//		response.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
//
//		long taskId = taskObj.getTermTask().getTaskId();
//		if(taskId < 0){
//			ConcurrentLinkedQueue<TaskQueueObject> queue = shareGlobalObjVar
//	  		.getTmnlTaskQueue(taskObj.getTermTask().getTerminalAddr());
//			TaskQueueObject o = queue.peek();
//			if (o == null) {
//				log.error("TaskQueueObject 为空");
//				return null;
//			}
//			o.setStartTime(System.currentTimeMillis());
//			taskId = o.getTermTask().getTaskId();
//		}
//		response.setTaskId(taskId);
//		response.setTerminalAddr(taskObj.getTermTask().getTerminalAddr());
//		response.setFuncCode((short) (taskObj.getTermTask().getFuncCode()));
//		response.setTmnlAssetNo(taskObj.getTermTask().getTmnlAssetNo());
//		response.setNote(note);
//
//		log.debug("Response=" + response.toString());
// 		if (meterInfos.size() > 0) {
// 			MeterInfo tmpMeterInfo = meterInfos.remove(0);
// 			tmpTermObj.setMeterInfos(meterInfos);
// 			log.debug("移出电表:" + tmpMeterInfo.toString());
//		}
//// 		if(tmpTermObj.getMeterInfos().size() > 0){
//// 			retObjList = sendProc.sendDataForwardingF9(taskObj);
//// 		}
// 		
//		if (response != null) {
//			resultObj.setResultParaObj(response);
//			resultObj.setResultType(ConstDef.TASKRESULT_RESPONSECOMMAND);
//			resultObj.setResultValue(0);
//			resultObj.setRetMsgLen(0);
//			resultObj.setRetMsgBuf(null);
//			resultObj.setTaskID(taskObj.getTermTask().getTaskId());
//
//			if (retObjList == null)
//				retObjList = new ArrayList<TaskResultObject>();
//			retObjList.add(resultObj);
//		}
//		return retObjList;
//	}
//	
//	public Response recIdentityAuthentication698Report(TaskQueueObject taskObj, byte[] packetBuff,int step ){
//		
//		int count = 0;
//		boolean isTpv = false;// 判断时间标签
//		// int day, hour, minute, second;
//		byte recBuf[] = null;
//		String tmnlAddress = null;
//		//Date startTime = null;
//		Response resPonse = new Response();
//		//ArrayList<DbData> dbDatasList = new ArrayList<DbData>();
//		if (taskObj.getTaskMessage() == null) {
//			resPonse.setDbDatas(null);
//			return resPonse;
//		}
//		recBuf = taskObj.getTaskMessage();
//		tmnlAddress = taskObj.getTermTask().getTerminalAddr();
//		int dataLength = func.getLength(recBuf[count + 1], recBuf[count + 2]);
//		if (dataLength > 0 && dataLength == func.getLength(recBuf[count + 3], recBuf[count + 4])){
//			// 判断报文长度
//		} else {
//			taskObj.addLowLessReports(ConstDef.LOWLESS_LENGTH_ERROR, "class1Length=" + dataLength + " error");
//			resPonse.setDbDatas(null);
//			return resPonse;
//		}
//		if ((func.getShort(recBuf[count + 6]) & 0x0f) == 0x09) {// 否认，没有数据
//			resPonse.setDbDatas(null);
//			return resPonse;
//		}
//		if (func.getInt(recBuf[count + 12]) != 0x10) {
//			log.error("上行的AFN不是透抄，afn:"+	func.getInt(recBuf[count + 12]) 
//					+"任务ID:" + taskObj.getTermTask().getTaskId());			
//			resPonse.setDbDatas(null);
//			return resPonse;
//		}
//	
//		if ((func.getShort(recBuf[count + 13]) & 0x80) == 0x80) {// 判断是否有Tpv
//			isTpv = true;
//		}
//		count = 14;
//		int content_length = dataLength;
//		if (isTpv)
//			content_length -= 6;
//		
//		//pn
//		count += 2;
//		//fn
//		count += 2;
//		
//		//截除645报文进行数据解析
//		int len_698_response = taskObj.getBuflen()-count;
//		byte[]  data_698 = new byte[len_698_response];
//		System.arraycopy(recBuf, count, data_698, 0, len_698_response);
//		int count_698 =0;
//		byte port = data_698[count_698];		
//		count ++;
//		count_698 ++;
//		
//		int dSize = 0;
//		int size1= (data_698[count_698] &0xff) ;
//		count ++;
//		count_698 ++;		
//		int size2= data_698[count_698] &0xff;
//		count ++;
//		count_698 ++;
//		dSize = size1 + size2*256;
//		
//		if (dSize > taskObj.getBuflen()-count){
//			log.error("集中器上传的数据长度不够！");
//			resPonse.setNote("集中器上传的数据长度不够");
//			resPonse.setDbDatas(null);
//			return resPonse;
//		}
//
//		byte[]  data_645 = new byte[dSize];
//		
//		System.arraycopy(data_698, count_698, data_645, 0, dSize);	
//		try{
//			if(step == Fk04TransParent.STEP_ASKMETERNO){
//				resPonse = this.recMeterNoReport(taskObj, data_645, dSize);
//			}else{
//				resPonse = this.recIdentityAuthenticationReport(tmnlAddress, data_645, dSize);
//			}
//		}
//		catch(Exception e){
//			log.error(
//					"recIdentityAuthenticationReport Error in Dl698TransParent",
//					e);
//			Response res = new Response();
//			res.setContinue(false);
//			res.setErrorCode(ErrorCode.OK);
//			ResponseItem responseItems = new ResponseItem();
//			responseItems.setValue(null);
//			responseItems.setStatus((short) 1);
//			responseItems.setErrorCode((short) ErrorCode.Other_Err.getId());
//			responseItems.setPn((short) 0);
//			responseItems.setFn((short) 1);
//			List<ResponseItem> itemList = new ArrayList<ResponseItem>();
//			itemList.add(responseItems);
//			res.setResponseItems(itemList);
//
//			res.setNote(e.toString());
//			return res;
//		}
//		
//		if (resPonse!=null){
//			return resPonse;			
//		}
//
//		if (isTpv) {
//			count += 6;
//		}
///*		byte check = (byte) func.checkSum(recBuf, 6, dataLength);
//		if (recBuf[count] != check) {
//			log.error("recBuf[count] != check in Dl698TransParent Error #2");
//			taskObj.addLowLessReports(ConstDef.LOWLESS_CHECK_ERROR, "checkSum error!");
//			resPonse.setDbDatas(null);
//			return resPonse;
//		}
//		*/
//		count += 1;
//		/*if (recBuf[count] != 0x16) {
//			resPonse.setDbDatas(null);
//			return resPonse;
//		}
//*/
//		return null;
//	}
//	
//	public Response recMeterNoResponse(byte[] recbuf, TerminalObject to){
//		Response res = new Response();
//		int len = recbuf[9] & 0xff; //数据长度域
//		int counter = 10;
//		byte[] dataFlag = new byte[4];
//		System.arraycopy(recbuf, counter, dataFlag, 0, 4);
// 
//		data_decrease_33h(dataFlag, 4);
//		log.debug("收到数据标识：" + HexDump.toHexString(dataFlag).toString());
//		// 02 04 00 04
//		if (dataFlag[0] == 0x02 && dataFlag[1] == 0x04
//				&& dataFlag[2] == 0x00 && dataFlag[3] == 0x04) {
//			;//数据标识
//		} else
//		{
//			try {
//				throw new Exception("上行的数据不是召测表号的返回帧");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return new Response();
//		}
//
//		counter += 4;
//		byte[] meterNo = new byte[6];
//		System.arraycopy(recbuf, counter, meterNo, 0, 6);
//		data_decrease_33h(meterNo, 6);
//		
//		byte[] newMeterNo = new byte[6];
//		for (int i = 0 ; i < 6 ; i ++){
//			newMeterNo[i] = meterNo[6-i-1];
//		}
//		to.setMeterNo(newMeterNo);
//		if(to.getMeterInfos() != null && to.getMeterInfos().size() >0){
//			to.getMeterInfos().get(0).setBakString(HexDump.toHexString(newMeterNo).toString());
//		}
//		log.debug("收到电表表号：" + HexDump.toHexString(newMeterNo).toString());
//		
//		res.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
//		return null;
//	}
//	
//	public Response indentityRightResponse(byte[] recbuf, TerminalObject to){
//		Response res = new Response();
//		
//		int len = recbuf[9] & 0xff; //数据长度域
//		int counter = 10;
//		byte[] dataFlag = new byte[4];
//		System.arraycopy(recbuf, counter, dataFlag, 0, 4);
// 
//		data_decrease_33h(dataFlag, 4);
//		log.debug("收到数据标识：" + HexDump.toHexString(dataFlag).toString());
//		// FF 00 00 07
//		if (dataFlag[0] == (byte) 0xff && dataFlag[1] == 0x00
//				&& dataFlag[2] == 0x00 && dataFlag[3] == 0x07) {
//			;//数据标识
//		} else
//		{
////			try {
////				throw new Exception("上行的数据不是身份认证");
////			} catch (Exception e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//			res.setNote("上行的数据不是身份认证");
//			return res;
//		}
//
//		counter += 4;
//		byte[] random2 = new byte[4];
//		System.arraycopy(recbuf, counter, random2, 0, 4);
//		data_decrease_33h(random2, 4);
//		//log.debug("收到随机数2：" + HexDump.toHexString(random2).toString());
//
//		//随机数2取反
//		byte[] random2_1 = new byte[4];
//		for (int i = 0 ; i < 4 ; i ++){
//			random2_1[i] = random2[4-i-1];
//		}
//		to.setRandom2(random2_1);
//
//		counter += 4;
//		byte[] esamSquenece = new byte[8];
//		System.arraycopy(recbuf, counter, esamSquenece, 0, 8);
//		data_decrease_33h(esamSquenece, 8);
//		//log.debug("收到ESAM序列号：" + HexDump.toHexString(esamSquenece).toString());
//		byte[] esamSquenece_1 = new byte[8];
//		for (int i = 0 ; i < 8 ; i ++){
//			esamSquenece_1[i] = esamSquenece[8-i-1];
//		}
//		to.setEsamSequence(esamSquenece_1);
//		counter+=8;		
//		res.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
//		return null;
//	}
//
//	public Response meterNoWrongResponse(byte[] recbuf, TerminalObject to){
//		Response response = new Response();
//		response.setErrorCode(ErrorCode.OK);
//		List<MeterInfo> meterInfos = to.getMeterInfos();
//		log.debug("meterInfos.size=" + meterInfos.size());
//		MeterInfo meterInfo = meterInfos.get(0);
//		String meterId = String.valueOf(meterInfo.getMeterID());
//		response.setContinue(false);
//		ResponseItem responseItems = new ResponseItem();
//		responseItems.setValue(meterId);
//		responseItems.setStatus((short)1);
//		responseItems.setPn((short)0);
//		responseItems.setFn((short)1);
//		
//		responseItems.setErrorCode((short)ErrorCode.Other_Err.getId());
//		List<ResponseItem> itemList = new ArrayList<ResponseItem>();
//		itemList.add(responseItems);
//		response.setResponseItems(itemList);
//		response.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
////		
////		if (meterInfos.size() > 0) {
//// 			MeterInfo tmpMeterInfo = meterInfos.remove(0);
//// 			to.setMeterInfos(meterInfos);
//// 			log.debug("移出电表:" + tmpMeterInfo.toString());
////		}
////	
//		return response;
//	}
//	
//
//	
//	public Response indentityWrongResponse(byte[] recbuf, TerminalObject to){
//		Response response = new Response();
//		response.setErrorCode(ErrorCode.OK);
//		int len = recbuf[9] & 0xff;
//		if(len != 2){
////			String note ="返回的错误字节个数不等于2"; 
////			log.error(note);
////			response.setNote(note);
//			
//			List<MeterInfo> meterInfos = to.getMeterInfos();
//			MeterInfo meterInfo = meterInfos.get(0);
//			String meterId = String.valueOf(meterInfo.getMeterID());
//			response.setContinue(false);
//			ResponseItem responseItems = new ResponseItem();
//			responseItems.setValue(meterId);
//			responseItems.setStatus((short)1);
//			responseItems.setPn((short)0);
//			responseItems.setFn((short)1);
//			responseItems.setErrorCode((short)ErrorCode.Authentication_Err.getId());
//			responseItems.setCode("返回报文长度不够");
//			List<ResponseItem> itemList = new ArrayList<ResponseItem>();
//			itemList.add(responseItems);
//			response.setResponseItems(itemList);
//			response.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
//			response.setNote("身份认证失败");
//			return response;
//		}
//		byte[] errorCode = new byte[len];
//		System.arraycopy(recbuf, 10, errorCode, 0, 2);
//		errorCode = data_decrease_33h(errorCode,2);
//	
//		List<MeterInfo> meterInfos = to.getMeterInfos();
//		MeterInfo meterInfo = meterInfos.get(0);
//		String meterId = String.valueOf(meterInfo.getMeterID());
//		response.setContinue(false);
//		ResponseItem responseItems = new ResponseItem();
//		responseItems.setValue(meterId);
//		responseItems.setStatus((short)1);
//		responseItems.setPn((short)0);
//		responseItems.setFn((short)1);
//		
//		for (int j = 0; j < 8; j++) {
//			int flag = (errorCode[0] >> j )& 0x01;
//			if (1 ==flag) {
//				if (j == 0) {
//					log.debug("其它错误");
//					responseItems.setErrorCode((short)ErrorCode.Other_Err.getId());
//					
//				} else if (j == 1) {
//					log.debug("重复充值");
//					responseItems.setErrorCode((short)ErrorCode.RepeatAbundance_Err.getId());
//				} else if (j == 2) {
//					log.debug("ESAM验证失败");
//					responseItems.setErrorCode((short)ErrorCode.Esam_Err.getId());
//				} else if (j == 3) {
//					log.debug("身份认证失败");
//					responseItems.setErrorCode((short)ErrorCode.Authentication_Err.getId());
//				} else if (j == 4) {
//					log.debug("客户编号不匹配");
//					responseItems.setErrorCode((short)ErrorCode.UserNumberUnmatch_Err.getId());
//				} else if (j == 5) {
//					log.debug("充值次数错误");
//					responseItems.setErrorCode((short)ErrorCode.Sufficient_Error.getId());
//				} else if (j == 6) {
//					log.debug("购电超囤积");
//					responseItems.setErrorCode((short)ErrorCode.BuyCoemption_Error.getId());
//				}else{
//					log.debug("保留错误");
//				}
//			}
//		}
//	
//		List<ResponseItem> itemList = new ArrayList<ResponseItem>();
//		itemList.add(responseItems);
//		response.setResponseItems(itemList);
//		response.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
//		response.setNote("身份认证失败");
//		return response;
//	}
//	
//	public Response recMeterNoReport(TaskQueueObject taskObj, byte[] recbuf, int recLen) throws Exception{
//		Response res = new Response();
//		if (recbuf == null || recbuf.length <=0 || recLen <= 0){
//			log.error("645报文长度异常");
//			res.setNote("645报文长度异常");
//			return res;
//		}
//		log.debug("××××××××××接收645召测表号返回：" + HexDump.toHexString(recbuf).toString());
//		String terminalStr = taskObj.getTermTask().getTerminalAddr();
//		TerminalObject to = shareGlobalObjVar.getTerminalPara(terminalStr);
//		if (to == null){
//			log.error("终端对象为空");
//			res.setNote("终端对象为空");
//			return res;
//		}
//		byte[] newBuff = new byte[recbuf.length];
//		System.arraycopy(recbuf, 0, newBuff, 0, newBuff.length);
//		int ii =0;
//		while(ii + 7 < newBuff.length && (newBuff[ii] != 0x68 || newBuff[ii+7] != 0x68)){
//			ii ++;
//		}
//		if(ii +7 >= newBuff.length){
//			log.error("找不到报文头0x68");
//			res.setNote("找不到报文头0x68");
//			return res;
//		}
//		recbuf = new byte[newBuff.length - ii];
//		System.arraycopy(newBuff, ii, recbuf, 0, recbuf.length);
//		if(0x68 ==recbuf[0] && 0x68 == recbuf[7]){
//			int controlFlag = recbuf[8] & 0xff;
//			log.debug("控制域＝:" + controlFlag);
//			switch(controlFlag){
//			case 0x91:
//				return recMeterNoResponse(recbuf, to);
//			case 0xD1:
//				log.error("读表号时返回否认报文" );
//				return meterNoWrongResponse(recbuf, to);
//			default:
//					log.error("未定义的控制码：" + controlFlag);
//					res.setNote("未定义的控制码：" + controlFlag);
//					return res;
//			}
//		}else{
//			log.debug("判断recbuf第一个字节或第8个字节不等于0x68");
//			res.setNote("判断recbuf第一个字节或第8个字节不等于0x68");
//			return res;
//		}
//	}
//	
//	public Response recIdentityAuthenticationReport(String terminalStr,
//			byte[] recbuf, int recLen) throws Exception {
//		Response response = new Response();
//		if (recbuf == null || recbuf.length<=0 || recLen <= 0){
//			response.setNote("接收645数据长度为0");
//			return response;
//		}
//		
//		log.debug("××××××××××召测645身份认证返回：" + HexDump.toHexString(recbuf).toString());
//		
//		TerminalObject to = shareGlobalObjVar.getTerminalPara(terminalStr);
//		if (to == null){
//			log.error("终端地址：" + terminalStr + "找不到");
//			response.setNote("终端对象为空");
//			return response;
//		}
//
//		byte[] newBuff = new byte[recbuf.length];
//		System.arraycopy(recbuf, 0, newBuff, 0, newBuff.length);
//		int ii =0;
//		while(ii + 7 < newBuff.length && (newBuff[ii] != 0x68 || newBuff[ii+7] != 0x68)){
//			ii ++;
//		}
//		if(ii +7 >= newBuff.length){
//			log.error("找不到报文头0x68");
//			response.setNote("找不到报文头0x68");
//			return response;
//		}
//		recbuf = new byte[newBuff.length - ii];
//		System.arraycopy(newBuff, ii, recbuf, 0, recbuf.length);
//		if(0x68 ==recbuf[0] && 0x68 == recbuf[7]){
//			int controlFlag = recbuf[8] & 0xff;
//			switch(controlFlag){
//			case 0x83:
//				return indentityRightResponse(recbuf, to);
//			case 0xc3:
//				//added by gaolx 20110311 --start
//				//身份认证失败之后再次验证（第一次：表地址，第二次表号）
//				if(to.getMeterInfos() != null && to.getMeterInfos().size() >0 
//						&& to.getMeterInfos().get(0).getIdentityType()==0){
//					to.setAskMeterNo(true);
//				}
//				//added by gaolx 20110311 --end
//				return indentityWrongResponse(recbuf, to);
//			default:
//					log.error("未处理的控制码");
//					response.setNote("未处理的控制码");
//					List<MeterInfo> meterInfos = to.getMeterInfos();
//					MeterInfo meterInfo = meterInfos.get(0);
//					String meterId = String.valueOf(meterInfo.getMeterID());
//					ResponseItem responseItems = new ResponseItem();
//					responseItems.setValue(meterId);
//					responseItems.setStatus((short)1);
//					responseItems.setPn((short)0);
//					responseItems.setFn((short)1);
//					responseItems.setErrorCode((short)ErrorCode.Authentication_Err.getId());
//					responseItems.setCode("身份认证失败");
//					List<ResponseItem> itemList = new ArrayList<ResponseItem>();
//					itemList.add(responseItems);
//					response.setResponseItems(itemList);
//					return response;
//			}
//		}else{
//			log.debug("报文第一个字节或第8个字节不等于0x68");
//			response.setNote("报文第一个字节或第8个字节不等于0x68");
//			List<MeterInfo> meterInfos = to.getMeterInfos();
//			MeterInfo meterInfo = meterInfos.get(0);
//			String meterId = String.valueOf(meterInfo.getMeterID());
//			ResponseItem responseItems = new ResponseItem();
//			responseItems.setValue(meterId);
//			responseItems.setStatus((short)1);
//			responseItems.setPn((short)0);
//			responseItems.setFn((short)1);
//			responseItems.setErrorCode((short)ErrorCode.Authentication_Err.getId());
//			responseItems.setCode("身份认证失败");
//			List<ResponseItem> itemList = new ArrayList<ResponseItem>();
//			itemList.add(responseItems);
//			response.setResponseItems(itemList);
//			return response;
//		}
//	}
//
//	private byte[] data_decrease_33h(byte[] buf, int len) {
//		for (int i = 0; i < len; i++) {
//			buf[i] = (byte) (((int) buf[i] - 0x33) & 0xff);
//		}
//
//		return buf;
//	}
//
//	private byte[] data_decrease_33h(byte[] buf, int startpos ,int len) {
//		for (int i = startpos; i < startpos + len; i++) {
//			buf[i] = (byte) (((int) buf[i] - 0x33) & 0xff);
//		}
//
//		return buf;
//	}
//
//	private byte[] data_add_33h(byte[] buf, int len) {
//		for (int i = 0; i < len; i++) {
//			buf[i] = (byte) (((int) buf[i] + 0x33) & 0xff);
//		}
//		return buf;
//	}
//	
//	public byte[] buildControlBuffer(String meterAddr, byte[] jmAfterReport, int controlType){
//		try {
//			if (jmAfterReport == null) {
//				log.debug("jmAfterReport==null  in Dl698TransParent Error#1");
//				return null;
//			}
//			byte[] sendBuff = new byte[512];
//			int frameIndex = 0;
//			sendBuff[frameIndex++] = 0x68;
//			byte[] tmpBuff = HexDump.hexStringToByteArray(meterAddr);
//			for (int i = tmpBuff.length - 1; i >= 0; i--) {
//				sendBuff[frameIndex++] = tmpBuff[i];
//			}
//			sendBuff[frameIndex++] = 0x68;
//			if (controlType == 1) {// 充值
//				sendBuff[frameIndex++] = 0x03;
//			}
//
//			byte[] databuf = buildControl645Data(jmAfterReport, controlType);
//
//			sendBuff[frameIndex++] = (byte) (databuf.length & 0xFF);
//			data_add_33h(databuf, databuf.length);// 数据区加33H
//			System.arraycopy(databuf, 0, sendBuff, frameIndex, databuf.length);
//			frameIndex += databuf.length;
//
//			sendBuff[frameIndex] = 0x0;
//			for (int csi = 0; csi < frameIndex; csi++) {
//				sendBuff[frameIndex] += sendBuff[csi]; // CS
//			}
//			frameIndex++;
//
//			sendBuff[frameIndex++] = 0x16;
//
//			byte[] retBuff = new byte[frameIndex];
//			System.arraycopy(sendBuff, 0, retBuff, 0, frameIndex);
//			return retBuff;
//		}catch(Exception e){
//			e.printStackTrace();
//			log.error("异常错误：" + e.toString());
//			return null;
//		}
//		
//	}
//
//	public byte[] buildControl645Data(byte[] data, int type){
//		byte[] sendBuff = new byte[512];
//		int frameIndex = 0; 
//		if(type == 1){
//			if(data.length < 22){
//				return null;
//			}
//			sendBuff[frameIndex++] = (byte)0xff;//数据标识
//			sendBuff[frameIndex++] = 0x02;
//			sendBuff[frameIndex++] = 0x01;
//			sendBuff[frameIndex++] = 0x07;
//			sendBuff[frameIndex++] = 0x78; //操作者代码
//			sendBuff[frameIndex++] = 0x56;
//			sendBuff[frameIndex++] = 0x34;
//			sendBuff[frameIndex++] = 0x12;
//			for(int i = 0; i < 4; i++){//充值金额
//				sendBuff[frameIndex++] = data[3-i]; //0-3
//			}
//			for(int i = 4; i < 8; i++){//次数
//				sendBuff[frameIndex++] = data[11-i]; //4-7
//			}
//			for(int i = 8; i < 12; i++){//MAC1
//				sendBuff[frameIndex++] = data[19-i]; //8-11
//			}
//			for(int i = 12; i < 18; i++){
//				sendBuff[frameIndex++] = data[29-i]; //12-17
//			}
//			for(int i = 18; i < 22; i++){
//				sendBuff[frameIndex++] = data[39-i]; //18-21
//			}
//		}
//		
//		byte[] retBuff = null;
//		if(frameIndex >0){
//			retBuff = new byte[frameIndex];
//			System.arraycopy(sendBuff, 0, retBuff, 0, frameIndex);
//		}
//		return retBuff;
//	}
//	/*
//	 * 应用举例： 以远程跳闸命令为例，命令有效截止时间：2010-07-22 18：56：17 PA= 98 P0P1P2 = 000000
//	 * 操作者代码：12345678 加密前的铭文信息：1A 00 10 07 22 18 56 17
//	 * 加密后的密文信息：865BAA06CDEA44E07EC90AD8D750C27103C131EB 组织报文： 98(PA) 00 00
//	 * 00(p0p1p2) 78 56 34 12 (操作者代码) EB 31 C1 03 71 C2 50 D7 D8 0A C9 7E E0 44
//	 * EA CD 06 AA 5B 86(加密后的密文信息) 报文数据： 【发送数据】 FE FE FE FE 68 11 11 11 11 11 11
//	 * 68 1C 1C CB 33 33 33 AB 89 67 45 1E 64 F4 36 A4 F5 83 0A 0B 3D FC B1 13
//	 * 77 1D 00 39 DD 8E B9 7D 16 【接收数据】 FE FE 68 11 11 11 11 11 11 68 9C 00 D2
//	 * 16
//	 * 
//	 * cfFlag 催费标记： true :催费；false: 非催费； brakeFlag 合闸标记: true: 合闸； false: 分闸；
//	 * jmAfterReport: 加密后的密文信息
//	 */
//	public byte[] assControlReport(String meterAddr, byte[] jmAfterReport,byte[] dataFlag,int CLASS_FLAG) throws Exception {
//		if (jmAfterReport == null) {
//			log.debug("jmAfterReport==null  in Dl698TransParent Error#1");
//			return null;
//		}
//		byte[] sendBuff = new byte[512];
//		int counter = 0;
//	/*	sendBuff[counter++] = (byte) 0xfe;
//		sendBuff[counter++] = (byte) 0xfe;
//		sendBuff[counter++] = (byte) 0xfe;
//		sendBuff[counter++] = (byte) 0xfe;*/
//		
//		log.debug("用户控制－－645组织报文帧数据：");
//		sendBuff[counter++] = 0x68;
//		
//		//modify by gaolx 20110227 start
//		while(meterAddr.length() <12){
//			meterAddr = "0" + meterAddr;
//		}
//		byte[] tmpBuff = HexDump.hexStringToByteArray(meterAddr);
//		for(int i =tmpBuff.length -1; i>= 0; i--){
//			sendBuff[counter++] = tmpBuff[i];
//		}
//		//modify by gaolx 20110227 end
//		
//		sendBuff[counter++] = 0x68;
//		//modi gaolx start
//		//sendBuff[counter++] = 0x1c;
//		if(dataFlag == null){
//			sendBuff[counter++] = 0x1c;
//		}else{
//			sendBuff[counter++] = 0x14;
//		}
//		//modi gaolx end
//		byte[] databuf = null;
//		if (dataFlag == null){//控制报文
//			try {
//				databuf = assControlData_JmAfterReport(jmAfterReport);
//			} catch (Exception e) {
//				throw new Exception(e);
//			}
//		}
//		else{
//			try {
//				//mafeng add 20110302
//				//modi by gaolx 20110309 --start
//				//databuf = assParamData_JmAfterReport(jmAfterReport,CLASS_FLAG);
//				databuf = assParamData_JmAfterReport(dataFlag, jmAfterReport,CLASS_FLAG);
//				// modi by gaolx 20110309 --end
//			} catch (Exception e) {
//				throw new Exception(e);
//			}			
//		}
//		if (databuf == null) {
//			throw new Exception("组织报文为空");
//		} else if (databuf.length <= 0) {
//			throw new Exception("组织报文长度为零");
//		}
//
//		int dataLen = databuf.length;
//		sendBuff[counter++] = (byte) (dataLen & 0xff);
//		data_add_33h(databuf, dataLen);// 数据区加33H		
//		System.arraycopy(databuf, 0, sendBuff, counter, dataLen);
//		counter += dataLen;
//
//		//modi gaolx 20110307 start
//		// 计算长度
//		//int tmpLength = counter - 1;
//		// sendBuff[frameIndexNo++] = 0x00;
//		sendBuff[counter] = 0x0;
//		for (int csi = 0; csi < counter; csi++) {
//			sendBuff[counter] += sendBuff[csi]; // CS
//		}
//		//modi gaolx end 
//		counter++;
//
//		sendBuff[counter++] = 0x16;
//
//		byte[] retBuff = new byte[counter];
//		System.arraycopy(sendBuff, 0, retBuff, 0, counter);
//	
//		return retBuff;
//	}
//
//	public byte[] assControlData_JmAfterReport(byte[] jmAfterReport)
//			throws Exception {
//		byte[] dataBuf = new byte[512];
//		int counter = 0;
//		dataBuf[counter++] = (byte) 0x98;
//		// 00 00 00(p0p1p2)
//		dataBuf[counter++] = (byte) 0x00;
//		dataBuf[counter++] = (byte) 0x00;
//		dataBuf[counter++] = (byte) 0x00;
//		// 78 56 34 12 (操作者代码)
//		dataBuf[counter++] = (byte) 0x78;
//		dataBuf[counter++] = (byte) 0x56;
//		dataBuf[counter++] = (byte) 0x34;
//		dataBuf[counter++] = (byte) 0x12;
//
//		int len = jmAfterReport.length;
//		if (len <= 0) {
//			throw new Exception("加密报文长度小于零，len =" + len);
//		}
//
//		byte[] tmpJmAfterReport = new byte[len];
//		for (int i = 0; i < len; i++) {
//			tmpJmAfterReport[i] = jmAfterReport[len - i-1];
//		}
//
//		System.arraycopy(tmpJmAfterReport, 0, dataBuf, counter, len);
//		counter += len;
//
//		byte[] returnBuf = new byte[counter];
//		System.arraycopy(dataBuf, 0, returnBuf, 0, counter);
//
//		return returnBuf;
//	}
//	
//	public byte[] buildControlData(int controlType,String val, int data){
//		byte[] sendBuff = new byte[1024];
//		int frameIndex = 0;
//		if(val == null){
//			return null;
//		}
//		if(controlType == 1){//充值,开户
//			
//			String[] tmpVal = val.split(":");
//			if(tmpVal.length < 3){
//				return null;
//			}
//			log.debug("val = "+ tmpVal[0]);
//			log.debug("val[2]=" + tmpVal[2]);
//			byte[] jiner = getHexBuff8(4, tmpVal[0], 2); //金额
//			log.debug("金额=" + jiner);
//			for(int i = 0; i < jiner.length; i++){
//				sendBuff[frameIndex++] = jiner[i];
//			}
//			byte[] cishu = getHexBuff8(4,tmpVal[1],0); //次数
//			for(int i = 0; i < cishu.length; i++){
//				sendBuff[frameIndex++] = cishu[i];
//			}
//			byte[] customerNo = getBcdBuff(6,tmpVal[2],0); //用户编号
//			for(int i = 0; i < customerNo.length;i++){
//				sendBuff[frameIndex++] = customerNo[i];
//			}
//		}else if(controlType == 2/*透支电量限值*/ || controlType == 3/*透支金额限值*/){
//			byte[] passVal = getBcdBuff(4,val,2);
//			for(int i = 0; i < passVal.length; i++){
//				sendBuff[frameIndex++] = passVal[i];
//			}
//		}
//		byte[] retBuff = new byte[frameIndex];
//		System.arraycopy(sendBuff, 0, retBuff, 0, frameIndex);
//		return retBuff;
//	}
//	
//	public ArrayList<TaskResultObject> buildControlReport(AsduConverter asduConver,
//			TaskQueueObject taskObj, String meterAddr, byte[] esamSequence, 
//			byte[] random2, byte[] jmBeforeReport, int controlType) {
//
//		ArrayList<TaskResultObject> retObjList = null;
//		TaskResultObject resultObj = null;
//		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
//		TerminalObject tmpTermObj = shareGlobalObjVar
//				.getTerminalPara(terminalAddr);
//		if (tmpTermObj == null) {
//			log.error("终端对象为空");
//			return assFailTaskResultObject(taskObj, retObjList, null,"终端对象为空", true);
//		}
//		MeterInfo meterInfo = tmpTermObj.getMeterInfos().get(0);
//		int identityType = meterInfo.getIdentityType();
//		String meterNo = meterInfo.getBakString();
//		byte[] fsyz = null;
//		if (identityType == MeterInfo.IDENTITY_BY_NO) {
//			fsyz = getfsyz(meterNo);
//		} else {
//			if (tmpTermObj.getMeterNo() != null) {
//				fsyz = getfsyz(tmpTermObj.getMeterNo());
//			} else {
//				fsyz = getfsyz(meterAddr);
//			}
//		}
//		if (fsyz == null || fsyz.length <= 0) {
//			return assFailTaskResultObject(taskObj, retObjList, null,
//					"接受报文中判断异常，加密前无分散因子", true);
//		}
//
//		log.debug("分散因子" + HexDump.toHexString(fsyz).toString());
//
//		
//		int len = 0;
//		if(controlType ==1){//充值，开户
//			len = random2.length + fsyz.length 	+ jmBeforeReport.length;
//		}
//		byte[] div = new byte[len];
//		int jmBeforeCount = 0;
//		System.arraycopy(random2, 0, div, jmBeforeCount, random2.length);
//		jmBeforeCount += random2.length;
//		System.arraycopy(fsyz, 0, div, jmBeforeCount, fsyz.length);
//		jmBeforeCount += fsyz.length;
//		
//		System.arraycopy(jmBeforeReport, 0, div, jmBeforeCount,
//				jmBeforeReport.length);
//		jmBeforeCount += jmBeforeReport.length;
//
//		// 密文
//		byte[] jmAfterReport = new byte[2048];
//
//		log.debug("usercontrol ：" + HexDump.toHexString(div).toString()
//				+ "len:" + div.length);
////		int ret = getPwdFromPwdComputer("InCreasePurse", 1, div, jmAfterReport);
//		
//		
//		
//		Integer outLen = 0;
//		ArrayList<Integer> out = new ArrayList<Integer>();
//		int ret = getPwdFromPwdComputer("InCreasePurse", 1, div, null, jmAfterReport,
//				out);
//		outLen = out.get(0);
//		if (ret != 0 || outLen <= 0) {
//			log.error("调用加密机参数更新接口失败");
//			return assFailTaskResultObject(taskObj, retObjList, null,
//					"调用加密机参数更新接口失败", true);
//		}
//		byte[] tmp = new byte[outLen.intValue()];
//		System.arraycopy(jmAfterReport, 0, tmp, 0, outLen.intValue());
//		jmAfterReport = tmp;
//		
//		
//		
////		if (ret != 0) {
////			log.error("调用加密机接口失败");
////			return assFailTaskResultObject(taskObj, retObjList, null,
////					"调用加密机接口失败", true);
////		}
//		log.debug("加密报文 ：" + HexDump.toHexString(jmAfterReport).toString()
//				+ "len:" + jmAfterReport.length);
//
//		byte[] controlReport = null;
//		try {
//			controlReport = buildControlBuffer(meterAddr, jmAfterReport,
//					controlType);
//		} catch (Exception e) {
//			log.error("组装透抄698报文出错", e.toString());
//			return assFailTaskResultObject(taskObj, retObjList, null,
//					"组装透抄698报文出错", true);
//		}
//
//		// 设置透明报文！！
//		shareGlobalObjVar.setTransBuf(terminalAddr, controlReport);
//
//		log.debug("control report: "
//				+ HexDump.toHexString(controlReport).toString() + "len:"
//				+ controlReport.length);
//		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
//		int frameIndexNo = 0;
//
//		ConcurrentLinkedQueue<TaskQueueObject> queue = shareGlobalObjVar
//				.getTmnlTaskQueue(taskObj.getTermTask().getTerminalAddr());
//		TaskQueueObject o = queue.peek();
//		if (o == null || o.getTermTask().getTaskId() <=0) {
//			o = taskObj;
//			log.debug("任务队列头被删");
//		}
//		sendBuff[frameIndexNo++] = 0x10; // afnNo
//		byte terminalPFC = tmpTermObj.getTerminalPFC();
//		tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));
//		tmpTermObj.setTerminalTpV(false);
//		tmpTermObj.setTerminalFIR(true);
//		tmpTermObj.setTerminalFIN(true);
//		tmpTermObj.setTerminalCON(false);
//
//		int seqNum = Fk04commFunction.getSeqNumber(terminalAddr);// SEQ
//		sendBuff[frameIndexNo++] = (byte) seqNum;
//
//		ArrayList<String> retStrList = asduConver
//				.convertDataObjToBuff(/* taskObj */o);
//		if (retStrList == null) {
//			log.error("retStrList == null in Dl698SendPacker Error #8 ");
//			return assFailTaskResultObject(taskObj, retObjList, null,
//					"asdu转换出错", true);
//		} else {
//			if (retStrList.size() > 0) {
//				byte tmpBuff[];
//				try {
//					tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
//					for (int m = 0; m < tmpBuff.length; m++) {
//						sendBuff[frameIndexNo++] = tmpBuff[m];
//					}
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//					return assFailTaskResultObject(taskObj, retObjList, null,
//							"不支持的字符编码转换", true);
//				}
//			}
//			log.debug("--Dl698Protocol透抄报文发送645控制报文，数据长度：" + frameIndexNo);
//
//			o.setStep(Fk04TransParent.STEP_OPERATE);
//			taskObj.setStep(Fk04TransParent.STEP_OPERATE);
//			resultObj = new TaskResultObject();
//			seqNum = Fk04commFunction.getSeqNumber(terminalAddr);// SEQ
//			resultObj.setFramePSEQ((short) (seqNum & 0x0f));
//			resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
//			resultObj.setResultValue(0);
//			resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
//
//			resultObj.setRetMsgLen(frameIndexNo);
//			resultObj.setRetMsgBuf(sendBuff);
//			retObjList = new ArrayList<TaskResultObject>();
//			retObjList.add(resultObj);
//
//			return retObjList;
//		}
//
//	}
//
//	public byte[] assControlData_JmBeforeReport(boolean cfFlag,
//		boolean cfjcFlag,boolean heZaiFlag,boolean fenZaiFlag,boolean bdFlag ,boolean bdjcFlag,
//		boolean hezha1, boolean hezha2, boolean hezha3, int delay) throws Exception {
//		byte[] dataBuf = new byte[8];
//		int counter = 0;
//
//		if (cfFlag) {
//			log.debug("下发催费");
//			dataBuf[counter++] = 0x2A;
//		}
//		else if (cfjcFlag) {
//			log.debug("下发催费解除");
//			dataBuf[counter++] = 0x2B;
//		}
//		else if (bdFlag) {
//			log.debug("下发保电");
//			dataBuf[counter++] = 0x3A;
//		}
//		else if (bdjcFlag) {
//			log.debug("下发保电解除");
//			dataBuf[counter++] = 0x3B;
//		}		
//		else if (heZaiFlag) {
//			log.debug("下发合闸");
//			dataBuf[counter++] = 0x1B;
//		} 
//		else if (fenZaiFlag) {
//			log.debug("下发分闸");
//			dataBuf[counter++] = 0x1A;
//		}else if(hezha1){
//			log.debug("下发直接合闸");
//			dataBuf[counter++] = 0x1C;
//		}else if(hezha2){
//			log.debug("下发延迟直接合闸");
//			dataBuf[counter++] = 0x1D;
//		}else if(hezha3){
//			log.debug("下发延迟合闸允许");
//			dataBuf[counter++] = 0x1E;
//		}
//		else {
//			log.debug("无效命令");
//			throw new Exception("无效命令");
//		}
//
//		// 00 00 00(p0p1p2)
////		dataBuf[counter++] = (byte) 0x00;
//		dataBuf[counter++] = (byte) delay;
//
//		Calendar cl = Calendar.getInstance();
//		cl.add(Calendar.HOUR_OF_DAY, 1);
//		byte sec = (byte) (func.TransBinayToBcd((byte) cl.get(Calendar.SECOND)));
//		byte min = (byte) (func.TransBinayToBcd((byte) cl.get(Calendar.MINUTE)));
//		byte hour = (byte) (func.TransBinayToBcd((byte) cl.get(Calendar.HOUR_OF_DAY)));
//		byte day = (byte) (func.TransBinayToBcd((byte) cl.get(Calendar.DAY_OF_MONTH)));
//		byte month = (byte) (func.TransBinayToBcd((byte) (cl.get(Calendar.MONTH) + 1)));
//		byte year = (byte) (func.TransBinayToBcd((byte) (cl.get(Calendar.YEAR) - 2000)));
//		dataBuf[counter++] = year;
//		dataBuf[counter++] = month;
//		dataBuf[counter++] = day;
//		dataBuf[counter++] = hour;
//		dataBuf[counter++] = min;
//		dataBuf[counter++] = sec;
//
//		return dataBuf;
//	}
//
//	//mafeng add for HUA BEI 645  2011/01/10 begin 
//	
//	public byte[] assParamData_JmAfterReport(byte[] dataFlag, byte[] jmAfterReport,int CLASS_FLAG)
//	throws Exception {
//		byte[] dataBuf = new byte[512];
//		int counter = 0;
//		for(int i = 0; i < dataFlag.length; i++){
//			dataBuf[counter++] = dataFlag[i];
//		}
//		if(1 == CLASS_FLAG)
//		{
//			dataBuf[counter++] = (byte) 0x99;
//		}
//		else 
//		{
//			dataBuf[counter++] = (byte) 0x98;
//		}
//		
//			
//		// 00 00 00(p0p1p2)
//		dataBuf[counter++] = (byte) 0x00;
//		dataBuf[counter++] = (byte) 0x00;
//		dataBuf[counter++] = (byte) 0x00;
//		// 78 56 34 12 (操作者代码)
//		dataBuf[counter++] = (byte) 0x78;
//		dataBuf[counter++] = (byte) 0x56;
//		dataBuf[counter++] = (byte) 0x34;
//		dataBuf[counter++] = (byte) 0x12;
//		
//		int len = jmAfterReport.length;
//		if (len <= 0) {
//			throw new Exception("加密报文长度小于零，len =" + len);
//		}
//		
//		byte[] tmpJmAfterReport = new byte[len];
//		//modi by gaolx 20110309 --start
////		for (int i = 0; i < len; i++) {
////			tmpJmAfterReport[i] = jmAfterReport[len- i-1];
////		}
//		for (int i = 0; i < len-4; i++) {
//			tmpJmAfterReport[i] = jmAfterReport[len -4 - i-1];
//		}
//		tmpJmAfterReport[len-4] = jmAfterReport[len-1];
//		tmpJmAfterReport[len-3] = jmAfterReport[len-2];
//		tmpJmAfterReport[len-2] = jmAfterReport[len-3];
//		tmpJmAfterReport[len-1] = jmAfterReport[len-4];
//		//modi gaolx end
//		System.arraycopy(tmpJmAfterReport, 0, dataBuf, counter, len);
//		counter += len;
//		
//		byte[] returnBuf = new byte[counter];
//		System.arraycopy(dataBuf, 0, returnBuf, 0, counter);
//		
//		return returnBuf;
//}
//
//	public boolean checkNumber(String check){
//		String pattern = "[0-9]+(.[0-9]+)?";
//		Pattern p = Pattern.compile(pattern);
//		Matcher m = p.matcher(check);
//		return m.matches();
//	}
//	
//	
//	public StringBuffer stringTobuffer(int len , String val, int pointNum){
//		if(!checkNumber(val)){
//			//log.error("数字串非法");
//			return null;
//		}
//		if(len %2 != 0 || pointNum %2 != 0){
//			//log.error("输入长度不对");
//			return null;
//		}
//		String[] tmpVal = val.split("\\.");
//		StringBuffer strBuff = new StringBuffer();
//		//构造整数buff
//		while(strBuff.length() < len - pointNum - tmpVal[0].length()){
//			strBuff.append("0");
//		}
//		strBuff.append(tmpVal[0]);
//		
//		//构造小数buff
//		if(tmpVal.length>1){
//			strBuff.append(tmpVal[1]);
//		}
//		while(len - strBuff.length() > 0){
//			strBuff.append("0");
//		}
//		
//		if(strBuff.length() < 0 || strBuff.length() > len){
//			//log.error("下发数据有误！");
//	    	return null;
//		}
//		return strBuff;
//	}
//	
//	public byte[] getBcdBuff(int len , String val){
//		return getBcdBuff(len, val, 0);
//	}
//	/**
//	 * 将字符串转换成合适的BCD码数据
//	 * @param len 格式中需要的BCD码数据长度（去掉小数点）例如：NNNN-->len = 2
//	 * @param val 输入String型数据
//	 * @param pointNum 格式中需要的小数点个数
//	 * @return
//	 */
//	public byte[] getBcdBuff(int len ,String val, int pointNum){
//		try{
//		len *= 2;
//		StringBuffer strBuff = stringTobuffer(len, val, pointNum);
//		byte[] buff = new byte[strBuff.length()/2];
//		int pos = 0;
//		for(int i = 0; i < strBuff.length(); i+=2){
//			buff[pos++] = (byte)((strBuff.charAt(i)-'0') * 16 + (strBuff.charAt(i+1) -'0'));
//		}
//		return buff;
//		}catch(Exception ex){
//			ex.printStackTrace();
//			log.error("数据转换出错：" + ex.toString());
//			return null;
//		}
//		
//	}
//	
//	public  byte[] getHexBuff8(int len, String val, int pointNum){
//		len *= 2;
//		StringBuffer strBuff = stringTobuffer(len, val, pointNum);
//		
//		
//		
//		Integer it = Integer.parseInt(strBuff.toString(),10);
//		byte[] buff = new byte[4];
//		int index = 0;
//		int mod = 1;
//		for(int i = 3; i >= 0; i--){
//			for(int j = 0; j < i; j++){
//				mod *= 256;
//			}
//			buff[index++] = (byte)(it / mod);
//			mod = 1;
//		}
//		return buff;
//	}
//	/*
//	public  byte[] getHexBuff(int len, String val, int pointNum){
//		try{
//			len *= 2;
//			StringBuffer strBuff = stringTobuffer(len, val, pointNum);
//			Integer it = Integer.parseInt(strBuff.toString(),10);
//			strBuff = new StringBuffer();
//			strBuff.append(String.format("%08X",it.intValue()));
//			byte[] buff = new byte[strBuff.length()/2];
//			int pos = 0;
//			for(int i = 0; i < strBuff.length(); i+=2){
//				buff[pos++] = (byte)((strBuff.charAt(i)-'0') * 10 + (strBuff.charAt(i+1) -'0'));
//			}
//			return buff;
//		}catch(Exception ex){
//			ex.printStackTrace();
//			log.error("数据转换出错：" + ex.toString());
//			return null;
//		}
//		
//	}
//	*/
////	public byte [] getBCDbuff( int buflen,String Str) 
////	{
////		//modi by gaolx 20110309 --start
//////		String [] TmpStr = null;
//////		java.util.regex.Pattern p = java.util.regex.Pattern.compile("^\\d+\\.\\d+$");
//////		java.util.regex.Matcher m = p.matcher(Str);
//////		
//////	    if(m.matches())
//////		{
//////			log.debug("数字串含小数，去除小数点");
//////			TmpStr = Str.split(".");
//////			Str = TmpStr[0].concat(TmpStr[1]);
//////		}
//////		else 
//////		{
//////			log.error("数字串非法");
//////		}
////		if(!checkNumber(Str)){
////			log.error("数字串非法");
////			return null;
////		}
////	    //add gaolx 20110309 --end
////	    if(Str.length() <= 0 || Str.length() > buflen * 2){
////	    	log.error("下发数据有误！");
////	    	return null;
////	    }
////	    while(Str.length()<buflen*2){
////	    	Str = "0" + Str;
////	    }
////		//gaolx end
////	    
////		byte[] dataBuf = new byte[buflen];
////		
////		int [] bint = new int [2*buflen];
////		//modi by gaolx 20110307 --start
////		//for(int Bi=0;Bi<10;Bi++)
////		for(int Bi = 0; Bi < bint.length; Bi++)
////		//modi by gaolx 20110307 --end
////		{
////			bint[Bi]=0;
////		}
////		
////		int i=0,j=0,k=0,count=0;
////		int Strlen = Str.length();
////		
////		for(i =0; i<Strlen;i++,j++)
////		{
////			bint[j]=Integer.valueOf(Str.charAt(i));
////		}
////		for(k=0;k<2*buflen;k+=2)
////		{
////			dataBuf[count++] = (byte) ( ((bint[k]<<4) & 0xf0) | (bint[k+1] &0x0f) );
////		}
////		return dataBuf;
////	}
//	public byte[] assUpdateCommand(String note, byte dataFlag) throws Exception{
//		return assUpdateCommand(note, dataFlag, 0);
//	}
//	
//	public byte[] assUpdateCommand(String note, byte dataFlag,int controlType) throws Exception {
//			    String [] Str_tmp = note.split(";");
//				byte[] updateCommand = new byte[3];
//				updateCommand[0] = 0x04;
//				updateCommand[1] = (byte)0xd6;		
//				//modi gaolx 20110310 --start
//				if(-1 != Str_tmp[0].indexOf("CLASS1ONE")){//第1套费率
//					updateCommand[2] = (byte)0x83;
//				}
//				else if(-1 != Str_tmp[0].indexOf("CLASS1TWO")){//第2套费率
//					updateCommand[2] = (byte)0x84;
//				}
//				else if(-1 != Str_tmp[0].indexOf("CLASS2")
//						||-1 != Str_tmp[0].indexOf("21")
//						||-1 != Str_tmp[0].indexOf("20")
//						|| controlType >0){//二类参数
//					if(dataFlag % 5 ==0){
//						updateCommand[2] = (byte)( 0x89);
//					}else{
//						updateCommand[2] = (byte)((dataFlag % 5)-1 + 0x90);
//					}
//					
//				}else {
//					//第1套费率
//					updateCommand[2] = (byte)0x82;
//				}
//				return updateCommand;
//	}
//	
//	public String assSoLibName(String note, int controlType) throws Exception {
//	    String [] Str_tmp = note.split(";");
//	    //modi gaolx start
//	    String name = Str_tmp[0];
//	    if(name.indexOf("CLASS1ONE") != -1){
//	    	return "Parameter1";
//	    }else if(name.indexOf("CLASS1TWO") != -1){
//	    	return "Parameter2";
//	    }
//	    else if(name.indexOf("CLASS2") != -1){
//	    	return "ParameterElseUpdate";
//	    }else if(name.equalsIgnoreCase("FF010007")){
//	    	return "InCreasePurse";
//	    }else if(controlType >0){
//	    	return "ParameterElseUpdate";
//	    }
//	    else{
//	    	int position =Integer.parseInt(name); 
//	    	if(position >= 7 && position <= 12){
//	    		return "ParameterUpdate"; 
//	    	}else if(position >= 201 && position <= 216){
//	    		return "ParameterElseUpdate";
//	    	}
//	    	return null;
//	    }
//	    
////		if(Str_tmp[0].contentEquals("CLASS1ONE")){
////			//第1套费率
////			return "ParameterUpdate1";
////		}
////		else if(Str_tmp[0].contentEquals("CLASS1TWO")){
////			//第2套费率
////			return "ParameterUpdate1";
////		}
////		else {
////			//参数
////			return "ParameterUpdate";
////		}
//	    //modi gaolx end
//		//mafeng add 20110223  在这里指定调用的加密机函数
//		
//}
//	
//
//	public byte[] assSetParamData_JmBeforeReport(String note) throws Exception {
//
//		byte[] dataBuf = null;
//
//		String[] Str_tmp = note.split(";");
//
//		log.debug("解析前数据：" + note);
//		if (Str_tmp[0].equals("07")) {
//			log.debug("下发两套分时费率切换时间:" + Str_tmp[1]);
//			dataBuf = new byte[5];
//			dataBuf = getBcdBuff(5, Str_tmp[1], 0);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("08")) {
//			dataBuf = new byte[3];
//			log.debug("下发电流互感器变比 :" + Str_tmp[1]);
//			dataBuf = getBcdBuff(3, Str_tmp[1], 0);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("09")) {
//			dataBuf = new byte[3];
//			log.debug("下发电压互感器变比:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(3, Str_tmp[1], 0);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("10")) {
//			dataBuf = new byte[6];
//			log.debug("下发客户编号:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(6, Str_tmp[1], 0);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("11")) {
//			dataBuf = new byte[4];
//			log.debug("下发报警金额1:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(4, Str_tmp[1], 2);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("12")) {
//			dataBuf = new byte[4];
//			log.debug("下发报警金额2:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(4, Str_tmp[1], 2);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("CLASS1ONE1")) {
//			dataBuf = new byte[4];
//			log.debug("下发第一套费率1 :" + Str_tmp[1]);
//			dataBuf = getBcdBuff(4, Str_tmp[1], 4);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("CLASS1ONE2")) {
//			dataBuf = new byte[4];
//			log.debug("下发第一套费率2 :" + Str_tmp[1]);
//			dataBuf = getBcdBuff(4, Str_tmp[1], 4);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("CLASS1ONE3")) {
//			dataBuf = new byte[4];
//			log.debug("下发第一套费率3 :" + Str_tmp[1]);
//			dataBuf = getBcdBuff(4, Str_tmp[1], 4);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("CLASS1ONE4")) {
//			dataBuf = new byte[4];
//			log.debug("下发第一套费率4 :" + Str_tmp[1]);
//			dataBuf = getBcdBuff(4, Str_tmp[1], 4);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("CLASS1TWO1")) {
//			dataBuf = new byte[4];
//			log.debug("下发第二套费率1:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(4, Str_tmp[1], 4);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("CLASS1TWO2")) {
//			dataBuf = new byte[4];
//			log.debug("下发第二套费率2:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(4, Str_tmp[1], 4);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("CLASS1TWO3")) {
//			dataBuf = new byte[4];
//			log.debug("下发第二套费率3:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(4, Str_tmp[1], 4);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("CLASS1TWO4")) {
//			dataBuf = new byte[4];
//			log.debug("下发第二套费率4:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(4, Str_tmp[1], 4);
//			return dataBuf;
//		}
//
//		// 以下为第二类数据内容
//		else if (Str_tmp[0].equals("201")) {
//			dataBuf = new byte[5];
//			log.debug("下发2类数据两套时区表切换时间:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(5, Str_tmp[1], 0);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("202")) {
//			dataBuf = new byte[5];
//			log.debug("下发2类数据两套日时段表切换时间:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(5, Str_tmp[1], 0);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("203")) {
//			dataBuf = new byte[5];
//			log.debug("下发2类数据两套梯度切换时间:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(5, Str_tmp[1], 0);
//			return dataBuf;
//		}
//
//		// 04 00 02 XX
//		else if (Str_tmp[0].equals("204")) {
//			dataBuf = new byte[1];
//			log.debug("下发2类数据年时区数（P<=14）:" + Str_tmp[1]);
//			if (Integer.parseInt(Str_tmp[1]) > 14) {
//				log.debug("下发2类数据年时区数P > 14,值非法，下发失败！下发时区数为:" + Str_tmp[1]);
//				return null;
//			} else {
//				dataBuf = getBcdBuff(1, Str_tmp[1], 0);
//				return dataBuf;
//			}
//
//		} else if (Str_tmp[0].equals("205")) {
//			dataBuf = new byte[1];
//			log.debug("下发2类数据日时段表数（q<=8）:" + Str_tmp[1]);
//			if (Integer.parseInt(Str_tmp[1]) > 14) {
//				log.debug("下发2类数据日时段表数q > 8,值非法，下发失败！下发日时段表数:" + Str_tmp[1]);
//				return null;
//			} else {
//				dataBuf = getBcdBuff(1, Str_tmp[1], 0);
//				return dataBuf;
//			}
//
//		} else if (Str_tmp[0].equals("206")) {
//			dataBuf = new byte[1];
//			log.debug("下发2类数据日时段数（m<=14）:" + Str_tmp[1]);
//			if (Integer.parseInt(Str_tmp[1]) > 14) {
//				log.debug("下发2类数据日时段数m > 14,值非法，下发失败！下发日时段数:" + Str_tmp[1]);
//				return null;
//			} else {
//				dataBuf = getBcdBuff(1, Str_tmp[1], 0);
//				return dataBuf;
//			}
//		} else if (Str_tmp[0].equals("207")) {
//			dataBuf = new byte[1];
//			log.debug("下发2类数据费率数（k<=63）:" + Str_tmp[1]);
//			if (Integer.parseInt(Str_tmp[1]) > 14) {
//				log.debug("下发2类数据费率数k > 63,值非法，下发失败！下发费率数:" + Str_tmp[1]);
//				return null;
//			} else {
//				dataBuf = getBcdBuff(1, Str_tmp[1], 0);
//				return dataBuf;
//			}
//		} else if (Str_tmp[0].equals("208")) {
//			dataBuf = new byte[2];
//			log.debug("下发2类数据公共假日数（n<=254）:" + Str_tmp[1]);
//			if (Integer.parseInt(Str_tmp[1]) > 14) {
//				log.debug("下发2类数据公共假日数n > 254,值非法，下发失败！下发公共假日数:" + Str_tmp[1]);
//				return null;
//			} else {
//				dataBuf = getBcdBuff(2, Str_tmp[1], 0);
//				return dataBuf;
//			}
//		} else if (Str_tmp[0].equals("209")) {
//			dataBuf = new byte[1];
//			log.debug("下发2类数据梯度数:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(1, Str_tmp[1], 0);
//			return dataBuf;
//		}
//
//		// 04 00 08 xx
//		else if (Str_tmp[0].equals("210")) {
//			dataBuf = new byte[1];
//			log.debug("下发2类数据周休日特征字:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(1, Str_tmp[1], 0);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("211")) {
//			dataBuf = new byte[1];
//			log.debug("下发2类数据周休日采用的日时段表号:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(1, Str_tmp[1], 0);
//			return dataBuf;
//		}
//
//		// 04 00 0B xx
//		else if (Str_tmp[0].equals("212") || Str_tmp[0].equals("213")
//				|| Str_tmp[0].equals("214")) {
//			dataBuf = new byte[2];
//			log.debug("下发2类数据每月第"
//					+ (Integer.parseInt(Str_tmp[0].substring(2, 3)) - 1)
//					+ "结算日:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(2, Str_tmp[1], 0);
//			return dataBuf;
//		}
//
//		// 04 00 10 04
//		else if (Str_tmp[0].equals("215")) {
//			dataBuf = new byte[4];
//			log.debug("下发2类数据囤积金额限值:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(4, Str_tmp[1], 2);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("216")) {
//			dataBuf = new byte[4];
//			log.debug("下发2类数据合闸允许金额限值:" + Str_tmp[1]);
//			dataBuf = getBcdBuff(4, Str_tmp[1], 2);
//			return dataBuf;
//		}
//
//		// 04 01 00 00 第一套时区表数据 2CLASS-ONE-TZ ;第一套起始日期+日时段表号:第二套起始日期+日时段表号
//		else if (Str_tmp[0].equals("CLASS2ONETZDATA")
//				|| Str_tmp[0].equals("CLASS2TWOTZDATA")) {
//			log.debug("下发2类数据第" + Str_tmp[0].substring(6, 9) + "套时区表数据:"
//					+ Str_tmp[1]);
//			String[] Str_tmp_v = Str_tmp[1].split(":");
//			int num = Str_tmp_v.length;
//
//			dataBuf = new byte[3 * num];
//			byte[] temdatabuf1 = new byte[2];
//			byte[] temdatabuf2 = new byte[1];
//
//			for (int i = 0, j = 0; i < num; i++) {
//				String[] Str_tmp_vx = Str_tmp_v[i].split("\\+");
//
//				temdatabuf1 = getBcdBuff(2, Str_tmp_vx[0]);
//				log.debug("下发2类数据第" + Str_tmp[0].substring(6, 9) + "套时区表数据，第"
//						+ i + "时区起始日期:" + Str_tmp_vx[0]);
//				dataBuf[j++] = temdatabuf1[0];
//				dataBuf[j++] = temdatabuf1[1];
//
//				temdatabuf2 = getBcdBuff(1, Str_tmp_vx[1]);
//				log.debug("下发2类数据第" + Str_tmp[0].substring(6, 9) + "套时区表数据，第"
//						+ i + "时区时段表号:" + Str_tmp_vx[1]);
//				dataBuf[j++] = temdatabuf2[0];
//			}
//			return dataBuf;
//		}
//
//		// 04 01 00 01 第一套时段表数据 CLASS2ONETZTDATA ;第1时段起始时间+费率号:第2时段起始时间+费率号
//		else if (Str_tmp[0].equals("CLASS2ONETZTDATA")
//				|| Str_tmp[0].equals("CLASS2TWOTZTDATA")) {
//			log.debug("下发2类数据第" + Str_tmp[0].substring(6, 9) + "套第1日时段表数据:"
//					+ Str_tmp[1]);
//			String[] Str_tmp_v = Str_tmp[1].split(":");
//			int num = Str_tmp_v.length;
//
//			dataBuf = new byte[3 * num];
//			byte[] temdatabuf1 = new byte[2];
//			byte[] temdatabuf2 = new byte[1];
//
//			for (int i = 0, j = 0; i < num; i++) {
//				String[] Str_tmp_vx = Str_tmp_v[i].split("\\+");
//
//				temdatabuf1 = getBcdBuff(2, Str_tmp_vx[0]);
//				log.debug("下发2类数据第" + Str_tmp[0].substring(6, 9)
//						+ "套第1日时段表数据，第" + i + "时段起始时间:" + Str_tmp_vx[0]);
//				dataBuf[j++] = temdatabuf1[0];
//				dataBuf[j++] = temdatabuf1[1];
//				temdatabuf2 = getBcdBuff(1, Str_tmp_vx[1]);
//				log.debug("下发2类数据第" + Str_tmp[0].substring(6, 9)
//						+ "套第1日时段表数据，第" + i + "时段费率号:" + Str_tmp_vx[1]);
//				dataBuf[j++] = temdatabuf2[0];
//			}
//			return dataBuf;
//		}
//
//		// 第一、二套第2-8日时段表数据
//		else if (Str_tmp[0].equals("CLASS2ONETPRDTDATA2")
//				|| Str_tmp[0].equals("CLASS2ONETPRDTDATA3")
//				|| Str_tmp[0].equals("CLASS2ONETPRDTDATA4")
//				|| Str_tmp[0].equals("CLASS2ONETPRDTDATA5")
//				|| Str_tmp[0].equals("CLASS2ONETPRDTDATA6")
//				|| Str_tmp[0].equals("CLASS2ONETPRDTDATA7")
//				|| Str_tmp[0].equals("CLASS2ONETPRDTDATA8")
//				|| Str_tmp[0].equals("CLASS2TWOTPRDTDATA2")
//				|| Str_tmp[0].equals("CLASS2TWOTPRDTDATA3")
//				|| Str_tmp[0].equals("CLASS2TWOTPRDTDATA4")
//				|| Str_tmp[0].equals("CLASS2TWOTPRDTDATA5")
//				|| Str_tmp[0].equals("CLASS2TWOTPRDTDATA6")
//				|| Str_tmp[0].equals("CLASS2TWOTPRDTDATA7")
//				|| Str_tmp[0].equals("CLASS2TWOTPRDTDATA8")) {
//			String[] Str_tmp_v = Str_tmp[1].split(":");
//			int num = Str_tmp_v.length;
//
//			dataBuf = new byte[3 * num];
//			byte[] temdatabuf1 = new byte[2];
//			byte[] temdatabuf2 = new byte[1];
//			for (int i = 0, j = 0; i < num; i++) {
//				String[] Str_tmp_vx = Str_tmp_v[i].split("\\+");
//
//				temdatabuf1 = getBcdBuff(2, Str_tmp_vx[0]);
//				log.debug("下发2类数据第" + Str_tmp[0].substring(6, 9) + "套第"
//						+ Str_tmp[0].substring(18, 19) + "日时段表数据,第" + i
//						+ "时段起始时间:" + Str_tmp_vx[0]);
//				dataBuf[j++] = temdatabuf1[0];
//				dataBuf[j++] = temdatabuf1[1];
//				temdatabuf2 = getBcdBuff(1, Str_tmp_vx[1]);
//				log.debug("下发2类数据第" + Str_tmp[0].substring(6, 9) + "套第"
//						+ Str_tmp[0].substring(18, 19) + "日时段表数据，第" + i
//						+ "时段费率号:" + Str_tmp_vx[1]);
//				dataBuf[j++] = temdatabuf2[0];
//			}
//			return dataBuf;
//		}
//
//		// 04 03 00 xx 第1公共假日日期及日时段表号、第2公共假日日期及日时段表号
//		else if (Str_tmp[0].equals("CLASS2PHANDTP01")
//				|| Str_tmp[0].equals("CLASS2PHANDTP02")
//				|| Str_tmp[0].equals("CLASS2PHANDTP03")
//				|| Str_tmp[0].equals("CLASS2PHANDTP04")
//				|| Str_tmp[0].equals("CLASS2PHANDTP05")
//				|| Str_tmp[0].equals("CLASS2PHANDTP06")
//				|| Str_tmp[0].equals("CLASS2PHANDTP07")
//				|| Str_tmp[0].equals("CLASS2PHANDTP08")
//				|| Str_tmp[0].equals("CLASS2PHANDTP09")
//				|| Str_tmp[0].equals("CLASS2PHANDTP10")
//				|| Str_tmp[0].equals("CLASS2PHANDTP11")
//				|| Str_tmp[0].equals("CLASS2PHANDTP12")) {
//			dataBuf = new byte[4];
//			byte[] tbuf3 = new byte[3];
//			byte[] tbuf1 = new byte[1];
//			// modi by gaolx 20110307 start
//			// String [] Str_tmp_v = note[1].split("+");
//			String[] Str_tmp_v = Str_tmp[1].split("\\+");
//			// modi by gaolx 20110307 end
//			// add by gaolx 20110307 --start
//			if (Str_tmp_v[0].length() == 0 || Str_tmp_v[0].length() > 6
//					|| Str_tmp_v[1].length() == 0 || Str_tmp_v[1].length() > 2) {
//				log.error("输入数据有误！");
//				return null;
//			}
//			while (Str_tmp_v[0].length() < 6) {
//				Str_tmp_v[0] = "0" + Str_tmp_v[0];
//			}
//			while (Str_tmp_v[1].length() < 2) {
//				Str_tmp_v[1] = "0" + Str_tmp_v[1];
//			}
//			// add by gaolx 20110307 --end
//			tbuf3 = getBcdBuff(3, Str_tmp_v[0]);
//			int index = 0;
//			for (; index < 3; index++) {
//				dataBuf[index] = tbuf3[index];
//			}
//			// gaolx end
//			tbuf1 = getBcdBuff(1, Str_tmp_v[1]);
//			dataBuf[index++] = tbuf1[0];
//			log.debug("下发2类数据第" + Str_tmp[0].substring(13, 15)
//					+ "公共假日日期及日时段表号:" + Str_tmp[1]);
//
//			return dataBuf;
//		}
//
//		// 2类第一套阶梯值 1-8
//		else if (Str_tmp[0].equals("CLASS2ONEV1")
//				|| Str_tmp[0].equals("CLASS2ONEV2")
//				|| Str_tmp[0].equals("CLASS2ONEV3")
//				|| Str_tmp[0].equals("CLASS2ONEV4")
//				|| Str_tmp[0].equals("CLASS2ONEV5")
//				|| Str_tmp[0].equals("CLASS2ONEV6")
//				|| Str_tmp[0].equals("CLASS2ONEV7")
//				|| Str_tmp[0].equals("CLASS2ONEV8")) {
//			dataBuf = new byte[4];
//			log.debug("下发2类数据第一套第" + Str_tmp[0].substring(10, 11) + "价梯值:"
//					+ Str_tmp[1]);
//			// add gaolx start
//			if (Str_tmp[1].length() <= 0 || Str_tmp[1].length() >= 8) {
//				log.error("下发数据有误");
//				return null;
//			}
//			dataBuf = getBcdBuff(4, Str_tmp[1], 2);
//			return dataBuf;
//		}
//
//		// 2类第一套阶梯电价 1-8
//		else if (Str_tmp[0].equals("CLASS2ONEF1")
//				|| Str_tmp[0].equals("CLASS2ONEF2")
//				|| Str_tmp[0].equals("CLASS2ONEF3")
//				|| Str_tmp[0].equals("CLASS2ONEF4")
//				|| Str_tmp[0].equals("CLASS2ONEF5")
//				|| Str_tmp[0].equals("CLASS2ONEF6")
//				|| Str_tmp[0].equals("CLASS2ONEF7")
//				|| Str_tmp[0].equals("CLASS2ONEF8")) {
//			dataBuf = new byte[4];
//			log.debug("下发2类数据第一套第" + Str_tmp[0].substring(10, 11) + "价梯电价:"
//					+ Str_tmp[1]);
//			dataBuf = getBcdBuff(4, Str_tmp[1], 4);
//			return dataBuf;
//		}
//
//		// 2类第二套阶梯值 1-8
//		else if (Str_tmp[0].equals("CLASS2TWOV1")
//				|| Str_tmp[0].equals("CLASS2TWOV2")
//				|| Str_tmp[0].equals("CLASS2TWOV3")
//				|| Str_tmp[0].equals("CLASS2TWOV4")
//				|| Str_tmp[0].equals("CLASS2TWOV5")
//				|| Str_tmp[0].equals("CLASS2TWOV6")
//				|| Str_tmp[0].equals("CLASS2TWOV7")
//				|| Str_tmp[0].equals("CLASS2TWOV8")) {
//			dataBuf = new byte[4];
//			log.debug("下发2类数据第二套第" + Str_tmp[0].substring(10, 11) + "价梯值:"
//					+ Str_tmp[1]);
//			dataBuf = getBcdBuff(4, Str_tmp[1], 2);
//			return dataBuf;
//		}
//
//		// 2类第二套阶梯电价 1-8
//		else if (Str_tmp[0].equals("CLASS2TWOF1")
//				|| Str_tmp[0].equals("CLASS2TWOF2")
//				|| Str_tmp[0].equals("CLASS2TWOF3")
//				|| Str_tmp[0].equals("CLASS2TWOF4")
//				|| Str_tmp[0].equals("CLASS2TWOF5")
//				|| Str_tmp[0].equals("CLASS2TWOF6")
//				|| Str_tmp[0].equals("CLASS2TWOF7")
//				|| Str_tmp[0].equals("CLASS2TWOF8")) {
//			dataBuf = new byte[4];
//			log.debug("下发2类数据第二套第" + Str_tmp[0].substring(10, 11) + "价梯电价:"
//					+ Str_tmp[1]);
//			dataBuf = getBcdBuff(4, Str_tmp[1], 4);
//			return dataBuf;
//		} else if (Str_tmp[0].equals("FF010007")) {
//			log.debug("设置身份认证有效时长：" + Str_tmp[1]);
//			dataBuf = getBcdBuff(2, Str_tmp[1], 0);
//			return dataBuf;
//		} else {
//			log.error("收到未定义的编码标识：" + note);
//		}
//		return null;
//	}
//	
//	public int assLC(int jmBeforeLen, int CLASS_FLAG) {
//		int lc = 0;
//		if (CLASS_FLAG == 1) {
//			lc = jmBeforeLen + 4;
//		} else {
//			lc = (jmBeforeLen + 15) / 16 * 16 + 4;
//		}
//		return lc;
//	}
//
//	public int assStartPos(String note) throws Exception {
//		return assStartPos(note,0);
//	}
//	
//	public int assStartPos(String note, int controlType) throws Exception {
//		if (controlType > 0) {
//			return 0;
//		}
//		String[] Str_tmp = note.split(";");
//		if (Str_tmp[0].equals("07")) {
//			log.debug("下发两套分时费率切换时间");
//			return 10;
//		} else if (Str_tmp[0].equals("08")) {
//			log.debug("下发电流互感器变比 ");
//			return 24;
//		} else if (Str_tmp[0].equals("09")) {
//			log.debug("下发电压互感器变比");
//			return 27;
//		} else if (Str_tmp[0].equals("10")) {
//			log.debug("下发客户编号");
//			return 39;
//		} else if (Str_tmp[0].equals("11")) {
//			log.debug("下发报警金额1");
//			return 16;
//		} else if (Str_tmp[0].equals("12")) {
//			log.debug("下发报警金额2");
//			return 20;
//		} else if (-1 != Str_tmp[0].indexOf("CLASS1ONE1")) {
//			log.debug("下发第一套费率1 ");
//			return 4;
//		} else if (-1 != Str_tmp[0].indexOf("CLASS1ONE2")) {
//			log.debug("下发第一套费率2 ");
//			return 8;
//		} else if (-1 != Str_tmp[0].indexOf("CLASS1ONE3")) {
//			log.debug("下发第一套费率3 ");
//			return 12;
//		} else if (-1 != Str_tmp[0].indexOf("CLASS1ONE4")) {
//			log.debug("下发第一套费率4 ");
//			return 16;
//		} else if (-1 != Str_tmp[0].indexOf("CLASS1TWO1")) {
//			log.debug("下发第二套费率1:");
//			return 4;
//		} else if (-1 != Str_tmp[0].indexOf("CLASS1TWO2")) {
//			log.debug("下发第二套费率2");
//			return 8;
//		} else if (-1 != Str_tmp[0].indexOf("CLASS1TWO3")) {
//			log.debug("下发第二套费率3:");
//			return 12;
//		} else if (-1 != Str_tmp[0].indexOf("CLASS1TWO4")) {
//			log.debug("下发第二套费率4");
//			return 16;
//		} else if (Str_tmp[0].equals("FF010007")) {
//			log.debug("设置身份认证时效,起始地址：" + 41);
//			return 41;
//		}
//
//		else if ((-1 != Str_tmp[0].indexOf("20"))
//				|| (-1 != Str_tmp[0].indexOf("21"))
//				|| (-1 != Str_tmp[0].indexOf("CLASS2"))) {
//			return 0;
//		}
//
//		return -1;
//	}
//
//	public int recControlReport(MeterInfo meterInfo,String terminalStr, byte[] recbuf, int recLen,
//			Response response) throws Exception {
//		
//	  if (meterInfo == null){
//			throw new Exception("meterInfo == null  in Dl698TransParent Error#1");		   
//	    }
//		if (recbuf == null) {
//			throw new Exception("recBuf == null  in Dl698TransParent Error#1");
//		}
//
//		if (recbuf.length <= 0 || recLen <= 0) {
//			throw new Exception(
//					"recbuf <=0 ||recLen<=0 in Dl698TransParent Error#1");
//		}
//		log.debug("××××××××××接收645控制返回报文：" + HexDump.toHexString(recbuf).toString());
//		String meterId = String.valueOf(meterInfo.getMeterID());
//		TerminalObject to = shareGlobalObjVar.getTerminalPara(terminalStr);
//		if (to == null)
//			throw new Exception("终端地址：" + terminalStr + "找不到");
//		
//		TerminalObject tmpTermObj = shareGlobalObjVar
//		.getTerminalPara(terminalStr);
//	    if (tmpTermObj == null) {
//	    	throw new Exception("终端对象空");
//         }
//
// 		for (int i = 0; i < recbuf.length; i++) {
//			if (((recbuf[i] & 0xff) == 0x68)
//					&& ((recbuf[i + 7] & 0xff) == 0x68)) {
//				byte controlFlag = recbuf[i + 8];
//				if (controlFlag == (byte) 0x9c/*拉合闸返回*/ || controlFlag == (byte)0x83/*充值返回*/) {
//					response.setContinue(false);
//					response.setErrorCode(ErrorCode.OK);
//					ResponseItem responseItems = new ResponseItem();
//					responseItems.setValue(meterId);
//					responseItems.setStatus((short)1);
//					responseItems.setPn((short)0);
//					responseItems.setFn((short)1);
//					responseItems.setErrorCode((short)ErrorCode.OK.getId());
//					List<ResponseItem> itemList = new ArrayList<ResponseItem>();
//					itemList.add(responseItems);
//					response.setResponseItems(itemList);
//					response.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
//
//			 		return 1;
//
//				} else if (controlFlag == (byte) 0xdc || controlFlag == (byte)0xc3) {
//					//byte errorCode = recbuf[10];//详细错误码
//					response.setContinue(false);
//
//					response.setErrorCode(ErrorCode.OK);
//					ResponseItem responseItems = new ResponseItem();
//					responseItems.setValue(meterId);
//					responseItems.setStatus((short) 1);
//					if (meterInfo.getNote().equalsIgnoreCase("01"))
//						responseItems.setErrorCode((short) ErrorCode.CuiFei_Err
//								.getId());
//					else if (meterInfo.getNote().equalsIgnoreCase("02"))
//						responseItems
//								.setErrorCode((short) ErrorCode.CuiFeiJieChu_Err
//										.getId());
//					else if (meterInfo.getNote().equalsIgnoreCase("05"))
//						responseItems
//								.setErrorCode((short) ErrorCode.BaoDian_Err
//										.getId());
//					else if (meterInfo.getNote().equalsIgnoreCase("06"))
//						responseItems
//								.setErrorCode((short) ErrorCode.BaoDianJieChu_Err
//										.getId());
//					else if (meterInfo.getNote().equalsIgnoreCase("03")) {
//						responseItems.setErrorCode((short) ErrorCode.HeZha_Err
//								.getId());
//					} else if (meterInfo.getNote().equalsIgnoreCase("04")) {
//						responseItems.setErrorCode((short) ErrorCode.FenZha_Err
//								.getId());
//					}else{
//						responseItems.setErrorCode((short) ErrorCode.NakAnswer
//								.getId());
//					}
//
//					responseItems.setPn((short) 0);
//					responseItems.setFn((short) 1);
//					List<ResponseItem> itemList = new ArrayList<ResponseItem>();
//					itemList.add(responseItems);
//					response.setResponseItems(itemList);
//					response.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
//			 		return 0;
//				}else{
//					log.error("未处理标示：" + controlFlag);
//					response.setContinue(false);
//					response.setErrorCode(ErrorCode.OK);
//					ResponseItem responseItems = new ResponseItem();
//					responseItems.setValue(meterId);
//					responseItems.setStatus((short)1);
//					responseItems.setPn((short)0);
//					responseItems.setFn((short)1);
//					responseItems.setErrorCode((short)ErrorCode.OK.getId());
//					List<ResponseItem> itemList = new ArrayList<ResponseItem>();
//					itemList.add(responseItems);
//					response.setResponseItems(itemList);
//					response.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
//
//			 		return 1;
//				}
//				}
//			}
//		return 1;
//	}
//
//	public int recSetOneTypeReport(MeterInfo meterInfo,String terminalStr, byte[] recbuf, int recLen,
// Response response) throws Exception {
//
//		if (meterInfo == null) {
//			throw new Exception(
//					"meterInfo == null  in Dl698TransParent Error#1");
//		}
//		if (recbuf == null) {
//			throw new Exception("recBuf == null  in Dl698TransParent Error#1");
//		}
//
//		if (recbuf.length <= 0 || recLen <= 0) {
//			throw new Exception(
//					"recbuf <=0 ||recLen<=0 in Dl698TransParent Error#1");
//		}
//		
//		recbuf = DataForward645Report.checkReport(recbuf); //add by wyx 2012-10-25 at anhui
//
//		log.debug("××××××××××接收645写数据返回："
//				+ HexDump.toHexString(recbuf).toString());
//		String meterId = String.valueOf(meterInfo.getMeterID());
//		TerminalObject to = shareGlobalObjVar.getTerminalPara(terminalStr);
//		if (to == null)
//			throw new Exception("终端地址：" + terminalStr + "找不到");
//
//		TerminalObject tmpTermObj = shareGlobalObjVar
//				.getTerminalPara(terminalStr);
//		if (tmpTermObj == null) {
//			log.error("tmpTermObj == null in Dl698TransParent Error #1");
//			return -1;
//		}
//
//		for (int i = 0; i < recbuf.length; i++) {
//			if (((recbuf[i] & 0xff) == 0x68)
//					&& ((recbuf[i + 7] & 0xff) == 0x68)) {
//				byte controlFlag = (byte) (recbuf[i + 8] & 0xff);
//				if (controlFlag == (byte) 0x94) {
//					// 正确应答
//					response.setContinue(false);
//					response.setErrorCode(ErrorCode.OK);
//					ResponseItem responseItems = new ResponseItem();
//					responseItems.setValue(meterId);
//					responseItems.setStatus((short) 1);
//					responseItems.setErrorCode((short) ErrorCode.OK.getId());
//					responseItems.setPn((short) 0);
//					responseItems.setFn((short) 1);
//					responseItems.setCode(meterInfo.getNote());
//					List<ResponseItem> itemList = new ArrayList<ResponseItem>();
//					itemList.add(responseItems);
//					response.setResponseItems(itemList);
//					response.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
//
//					String resItem = String.format(
//							"MeterID=%s, ErrorCode=%s, Value=%s", meterId,
//							response.getErrorCode(), responseItems.getCode());
//					log.debug("resPonseItem=" + resItem);
//					return 1;
//
//				} else if (controlFlag == (byte) 0xd4/* 控制返回 */
//						|| controlFlag == (byte) 0xdc/* 写数据返回 */
//						|| controlFlag == (byte) 0xd1/*读数据返回*/){
//					response.setContinue(false);
//
//					response.setErrorCode(ErrorCode.OK);
//					ResponseItem responseItems = new ResponseItem();
//					responseItems.setValue(meterId);
//					responseItems.setStatus((short) 1);
//					if (meterInfo != null && meterInfo.getNote() != null) {
//						if (meterInfo.getNote().equalsIgnoreCase("07")) {
//							responseItems
//									.setErrorCode((short) ErrorCode.FeiLvSwitch_Err
//											.getId());
//						} else if (meterInfo.getNote().equalsIgnoreCase("08")) {
//							responseItems
//									.setErrorCode((short) ErrorCode.DianLiu_Err
//											.getId());
//						} else if (meterInfo.getNote().equalsIgnoreCase("09")) {
//							responseItems
//									.setErrorCode((short) ErrorCode.DianYa_Err
//											.getId());
//						} else if (meterInfo.getNote().equalsIgnoreCase("10")) {
//							responseItems
//									.setErrorCode((short) ErrorCode.KeHuBianHao_Err
//											.getId());
//						} else if (meterInfo.getNote().equalsIgnoreCase("11")) {
//							responseItems
//									.setErrorCode((short) ErrorCode.BaoJing1_Err
//											.getId());
//						} else if (meterInfo.getNote().equalsIgnoreCase("12")) {
//							responseItems
//									.setErrorCode((short) ErrorCode.BaoJing2_Err
//											.getId());
//						} else if (-1 != meterInfo.getNote().indexOf(
//								"CLASS1ONE")) {
//							responseItems
//									.setErrorCode((short) ErrorCode.OneFeiLv_Err
//											.getId());
//						} else if (-1 != meterInfo.getNote().indexOf(
//								"CLASS2TWO")) {
//							responseItems
//									.setErrorCode((short) ErrorCode.TwoFeiLv_Err
//											.getId());
//						} else {// modi later gaolx
//							responseItems
//									.setErrorCode((short) ErrorCode.NakAnswer
//											.getId());
//						}
//					}
//					responseItems.setPn((short) 0);
//					responseItems.setFn((short) 1);
//					List<ResponseItem> itemList = new ArrayList<ResponseItem>();
//					itemList.add(responseItems);
//					response.setResponseItems(itemList);
//					response.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
//					String resItem = String.format(
//							"MeterID=%s, ErrorCode=%s, Value=%s", meterId,
//							response.getErrorCode(), responseItems.getCode());
//					log.debug("resPonseItem=" + resItem);
//					
//					if(controlFlag == (byte)0xd4){
//						if(((int)(recbuf[i + 10] & 0x04)) == 4){
//							//如果是在下发参数时上送了‘密码错/未授权’，则重新进行身份认证
//							log.debug("密码错/未授权---重新进行身份认证");
//							return -10;
//						}
//						else{
//							log.debug("controlFlag=" + controlFlag);
//						}
//					}
//					return 0;
//				} else if (controlFlag == (byte) 0x91 || controlFlag == (byte) 0x83) {
//					String val = new String();
//					int len = (recbuf[i + 9]&0xff) - 4;//modify by wyx 2012-10-20
//					byte[] data = new byte[len];
//					System.arraycopy(recbuf, i + 14, data, 0, len);
//					byte dl645DataType = (byte) ((recbuf[13]&0xff) - 0x33);
//					ArrayList<DataCode> dataCodes = new ArrayList<DataCode>();
//
//					data = report.dataAddNumber(data, 0, data.length, -0x33);
//					byte[] reverseData = new byte[len];
//					for (int ii = 0; ii < len; ii++) {
//						reverseData[ii] = data[len - ii - 1];
//						reverseData[ii] = (byte) (((reverseData[ii] >> 4) & 0x0f) * 10 + (reverseData[ii] & 0x0f));
//					}
//
//					String[] noteTmp = meterInfo.getNote().split(";");
//					log.debug("noteTmp.length=" + noteTmp.length);
//					log.debug("recbuf = "+ HexDump.toHexString(recbuf).toString());
//					if (noteTmp.length > 1) {
//						// val += noteTmp[1];
//						// val += ";";
//						val += assNews(noteTmp[1], reverseData);
//					} else {
//						// val += noteTmp[0];
//						// val += ";";
//						if(meterInfo != null && meterInfo.getBelongCollector() != null ){
//							//含有采集起地址
//							log.debug("采集器报文：" + HexDump.toHexString(recbuf).toString());
//							byte[] tmpBuff = new byte[recbuf.length-6];
//							int index = 0;
//							tmpBuff[index++] = 0x68;
//							for(int p = 10;p<16;p++){
//								tmpBuff[index++] = (byte) ((0xff & recbuf[p])-0x33);
//							}
//							tmpBuff[index++] = 0x68;
//							tmpBuff[index++] = recbuf[8];
//							tmpBuff[index++] = (byte) ((0xff &recbuf[9])-6);
//							for(int p = 16; p <recbuf.length;p++){
//								tmpBuff[index++] = recbuf[p];
//							}
//							
//							recbuf = tmpBuff;
//						}
//						val += report.parseReport(dataCodes, recbuf);
//					}
//					
//					//增加数据入库功能
//					if( (dl645DataType == 0x03) || ((dl645DataType >= 0x10) && (dl645DataType <= 0x1f)) ){
//						response.setDbDatas(getDbDatas(terminalStr, recbuf, dataCodes, meterInfo.getMeterAddr(), meterInfo.getMeterAddrAddZeroNum()));
//					}
//					
//					response.setContinue(false);
//					response.setErrorCode(ErrorCode.OK);
//					ResponseItem responseItems = new ResponseItem();
//					responseItems.setValue(meterId);
//					responseItems.setStatus((short) 1);
//					responseItems.setPn((short) 0);
//					responseItems.setFn((short) 1);
//					responseItems.setCode(val);
//					//读参数，读实时数据时保存下发的编码标识以便付峰使用
//					if(meterInfo.getNote() != null && meterInfo.getNote().startsWith("0A")){
//						responseItems.setBlockDef(meterInfo.getNote().split(";")[1]);
//					}else{
//						responseItems.setBlockDef(meterInfo.getNote());
//					}
//					responseItems.setErrorCode((short) ErrorCode.OK.getId());
//					List<ResponseItem> itemList = new ArrayList<ResponseItem>();
//					itemList.add(responseItems);
//					// log.debug(responseItems.toFeiKnString());
//					response.setResponseItems(itemList);
//					response.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
//
//					String resItem = String.format(
//							"MeterID=%s, ErrorCode=%s, Value=%s", meterId,
//							response.getErrorCode(), responseItems.getCode());
//					log.debug("resPonseItem=" + resItem);
//					return 1;
//				}else if (controlFlag == (byte) 0x81) {//add by wyx 2011-11-14 for anhui omld(召测97电表断相事件)
//					String val = new String();
//					int len = (recbuf[i + 9]&0xff) - 2;
//					byte[] data = new byte[len];
//					System.arraycopy(recbuf, i + 12, data, 0, len);
//					byte dl645DataType = (byte) ((recbuf[11]&0xff) - 0x33);
//					ArrayList<DataCode> dataCodes = new ArrayList<DataCode>();
//
//					data = report.dataAddNumber(data, 0, data.length, -0x33);
//					byte[] reverseData = new byte[len];
//					for (int ii = 0; ii < len; ii++) {
//						reverseData[ii] = data[len - ii - 1];
//						reverseData[ii] = (byte) (((reverseData[ii] >> 4) & 0x0f) * 10 + (reverseData[ii] & 0x0f));
//					}
//
//					String[] noteTmp = meterInfo.getNote().split(";");
//					log.debug("noteTmp.length=" + noteTmp.length);
//					log.debug("recbuf = "+ HexDump.toHexString(recbuf).toString());
//					if (noteTmp.length > 1) {
//						// val += noteTmp[1];
//						// val += ";";
//						val += assNews(noteTmp[1], reverseData);
//					} else {
//						// val += noteTmp[0];
//						// val += ";";
//						if(meterInfo != null && meterInfo.getBelongCollector() != null ){
//							//含有采集起地址
//							log.debug("采集器报文：" + HexDump.toHexString(recbuf).toString());
//							byte[] tmpBuff = new byte[recbuf.length-6];
//							int index = 0;
//							tmpBuff[index++] = 0x68;
//							for(int p = 10;p<16;p++){
//								tmpBuff[index++] = (byte) ((0xff & recbuf[p])-0x33);
//							}
//							tmpBuff[index++] = 0x68;
//							tmpBuff[index++] = recbuf[8];
//							tmpBuff[index++] = (byte) ((0xff &recbuf[9])-6);
//							for(int p = 16; p <recbuf.length;p++){
//								tmpBuff[index++] = recbuf[p];
//							}
//							
//							recbuf = tmpBuff;
//						}
//						val += report.parseReport_97(dataCodes, recbuf);
//					}
//					
//					//增加数据入库功能
//					if(dl645DataType == (byte)0xB3){
//						response.setDbDatas(getDbDatas(terminalStr, recbuf, dataCodes, meterInfo.getMeterAddr(), meterInfo.getMeterAddrAddZeroNum()));
//					}
//					
//					response.setContinue(false);
//					response.setErrorCode(ErrorCode.OK);
//					ResponseItem responseItems = new ResponseItem();
//					responseItems.setValue(meterId);
//					responseItems.setStatus((short) 1);
//					responseItems.setPn((short) 0);
//					responseItems.setFn((short) 1);
//					responseItems.setCode(val);
//					//读参数，读实时数据时保存下发的编码标识以便付峰使用
//					if(meterInfo.getNote() != null && meterInfo.getNote().startsWith("0A")){
//						responseItems.setBlockDef(meterInfo.getNote().split(";")[1]);
//					}else{
//						responseItems.setBlockDef(meterInfo.getNote());
//					}
//					responseItems.setErrorCode((short) ErrorCode.OK.getId());
//					List<ResponseItem> itemList = new ArrayList<ResponseItem>();
//					itemList.add(responseItems);
//					// log.debug(responseItems.toFeiKnString());
//					response.setResponseItems(itemList);
//					response.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
//
//					String resItem = String.format(
//							"MeterID=%s, ErrorCode=%s, Value=%s", meterId,
//							response.getErrorCode(), responseItems.getCode());
//					log.debug("resPonseItem=" + resItem);
//					return 1;
//				}else {
//					log.error("未处理的控制域：" + controlFlag);
//					return 0;
//				}
//			}
//		}
//		return 1;
//	}
//	
//	public ArrayList<DbData> getDbDatas(String tmnlAddr, byte[] buff, ArrayList<DataCode> dataCodes, String meterInfoMeterAddr, int meterAddrAddZeroNum){
//		ArrayList<DbData> DbDatas = new ArrayList<DbData>();
//		DbData db = new DbData();
//		String meterAddr = report.reverseData(buff, 1, 6);
//		
//		//modify by wyx 2012-12-6
//		if(meterInfoMeterAddr.equals(meterAddr)){
//			meterAddr = meterInfoMeterAddr.substring(meterAddrAddZeroNum, 12);
//		}else{
//			log.error("EdataMp ==null " + ", tmnlAddr:" + tmnlAddr + ", meterInfoMeterAddr:" + meterInfoMeterAddr + ", meterAddr:" + meterAddr + ", meterAddrAddZeroNum" + meterAddrAddZeroNum);
//		}
//		//modify by wyx 2012-12-6
//
//		ShareGlobalObj shareGlobalObj = ShareGlobalObj.getInstance();
//		EDataMp eDataMp = null;
//		try {
//			eDataMp = shareGlobalObj.getMpByCommAddr(tmnlAddr,meterAddr);
//		} catch (Exception e) {
//			log.error("获取EdataMp 终端地址：" + tmnlAddr + "中的表地址：" + meterAddr + "错误", e);
//			e.printStackTrace();
//		}
//		if(eDataMp == null){
//			log.error("EdataMp ==null");
//			return DbDatas;
//		}
//		String orgNo = eDataMp.getOrgNo();
//		
//		TerminalObject tmpTermObj = shareGlobalObj.getTerminalPara(tmnlAddr);
//		if (tmpTermObj != null) {
//			db.setTmnlAssetNo(tmpTermObj.getTmnlAssetNo());
//		}
//
//		OOrg oorg = shareGlobalObj.getOrgPara(orgNo);
//		if (oorg != null) {
//			db.setAreaCode(oorg.getAreaCode());
//		}
//		Integer ct = eDataMp.getCt();
//		Integer pt = eDataMp.getPt();
//		db.setCT(ct == null ? 1 : ct.intValue());
//		db.setPT(pt == null ? 1 : pt.intValue());
//		db.setPn(eDataMp.getMpId().getMpSn());
//		db.setDataId(eDataMp.getId());
//		db.setTime(Calendar.getInstance().getTime());
//		db.setMark(0);
//		
//		db.setDataType(FrontConstant.EVENT_DATA);
//		db.setEventFromType(1);
//		db.setEventIsStart((short)1);
//		db.setDataSource(1);
//		db.setMeterID(eDataMp.getId());
////		db.setMetConsNo(eDataMp.getConsNo());
//		db.setOrigiMessage(HexDump.toHexString(buff).toString());
//
//		db.setDataCodes(dataCodes);
//
////		ArrayList<DataCode> dataCodes = new ArrayList<DataCode>();
////		DataCode dataCode = new DataCode();
////		String code = report.getDataCodeName(dataMark);
////		Object value = report.getDataCodeVals(mark, data);
////		dataCode.setName(code);
////		dataCode.setValue(value);
//
//		DbDatas.add(db);
//		return DbDatas;
//	}
//	
//	public String assNews(String note, byte[] data) {
//		StringBuffer value = new StringBuffer();
//		String tmp = new String();
//		for (int i = 0; i < data.length; i++) {
//			String s = String.format("%02d", data[i]);
//			tmp += s;
//		}
//		value.append(tmp);
//
//		String buffer = new String();
//		log.debug("解析数据：" + value.toString());
//		if (note.equals("07")) {
//			buffer += "切换时间：";
//			value.insert(8, "时");
//			value.insert(6, "日");
//			value.insert(4, "月");
//			value.insert(2, "年");
//			value.append("秒");
//			buffer += value.toString();
//		} else if (note.equals("08")) {
//			buffer += "电流变比 ：";
//			buffer += value.toString();
//		} else if (note.equals("09")) {
//			buffer += "电压变比 ：";
//			buffer += value.toString();
//		} else if (note.equals("10")) {
//			buffer += "电压变比 ：";
//			buffer += value.toString();
//		} else if (note.equals("11")) {
//			buffer += "报警金额1 ：";
//			value.insert(6, '.');
//			value.append("元");
//			buffer += value.toString();
//		} else if (note.equals("12")) {
//			buffer += "报警金额2 ：";
//			value.insert(6, '.');
//			value.append("元");
//			buffer += value.toString();
//		} else if (note.equals("CLASS1ONE1")) {
//			buffer += "第一套费率1：";
//			value.insert(4, '.');
//			value.append("元");
//			buffer += value.toString();
//		} else if (note.equals("CLASS1ONE2")) {
//			buffer += "第一套费率2：";
//			value.insert(4, '.');
//			value.append("元");
//			buffer += value.toString();
//		} else if (note.equals("CLASS1ONE3")) {
//			buffer += "第一套费率3：";
//			value.insert(4, '.');
//			value.append("元");
//			buffer += value.toString();
//		} else if (note.equals("CLASS1ONE4")) {
//			buffer += "第一套费率4：";
//			value.insert(4, '.');
//			value.append("元");
//			buffer += value.toString();
//		} else if (note.equals("CLASS1TWO1")) {
//			buffer += "第二套费率1：";
//			value.insert(4, '.');
//			value.append("元");
//			buffer += value.toString();
//		} else if (note.equals("CLASS1TWO2")) {
//			buffer += "第二套费率2：";
//			value.insert(4, '.');
//			value.append("元");
//			buffer += value.toString();
//		} else if (note.equals("CLASS1TWO3")) {
//			buffer += "第二套费率3：";
//			value.insert(4, '.');
//			value.append("元");
//			buffer += value.toString();
//		} else if (note.equals("CLASS1TWO4")) {
//			buffer += "第二套费率4：";
//			value.insert(4, '.');
//			value.append("元");
//			buffer += value.toString();
//		}
//
//		// 以下为第二类数据内容
//		else if (note.equals("201")) {
//			buffer += "两套时区表切换时间：";
//			value.insert(8, "时");
//			value.insert(6, "日");
//			value.insert(4, "月");
//			value.insert(2, "年");
//			value.append("分");
//			buffer += value.toString();
//		} else if (note.equals("202")) {
//			buffer += "两套日时段表切换时间：";
//			value.insert(8, "时");
//			value.insert(6, "日");
//			value.insert(4, "月");
//			value.insert(2, "年");
//			value.append("分");
//			buffer += value.toString();
//		} else if (note.equals("203")) {
//			buffer += "两套梯度切换时间：";
//			value.insert(8, "时");
//			value.insert(6, "日");
//			value.insert(4, "月");
//			value.insert(2, "年");
//			value.append("分");
//			buffer += value.toString();
//
//		} else if (note.equals("204")) {
//			buffer += "年时区数：";
//			value.append("个");
//			buffer += value.toString();
//
//		} else if (note.equals("205")) {
//			buffer += "日时段表数：";
//			value.append("个");
//			buffer += value.toString();
//		} else if (note.equals("206")) {
//			buffer += "每日切换数：";
//			value.append("个");
//			buffer += value.toString();
//		} else if (note.equals("207")) {
//			buffer += "费率数：";
//			value.append("个");
//			buffer += value.toString();
//
//		} else if (note.equals("208")) {
//			buffer += "公共假日数：";
//			value.append("个");
//			buffer += value.toString();
//
//		} else if (note.equals("209")) {
//			buffer += "梯度数：";
//			value.append("个");
//			buffer += value.toString();
//		} else if (note.equals("210")) {
//			buffer += "周休日特征字：";
//			buffer += value.toString();
//
//		} else if (note.equals("211")) {
//			buffer += "周休日采用的日时段表号：";
//			buffer += value.toString();
//
//		} else if (note.equals("212") || note.equals("213")
//				|| note.equals("214")) {
//			buffer += "第" + (Integer.parseInt(note.substring(2, 3)) - 1)
//					+ "结算日：";
//			value.insert(2, "日");
//			value.append("时");
//			buffer += value.toString();
//		} else if (note.equals("215")) {
//			buffer += "囤积金额限值：";
//			value.insert(6, '.');
//			value.append("元");
//			buffer += value.toString();
//		} else if (note.equals("216")) {
//			buffer += "合闸允许金额限值：";
//			value.insert(6, '.');
//			value.append("元");
//			buffer += value.toString();
//
//		} else if (note.equals("CLASS2ONETZDATA")
//				|| note.equals("CLASS2TWOTZDATA")
//				|| note.equals("CLASS2ONETZTDATA")
//				|| note.equals("CLASS2TWOTZTDATA")) {
//			byte[] tmpData = new byte[data.length];
//			for (int i = 0; i < data.length; i++) {
//				tmpData[i] = data[data.length - 1 - i];
//			}
//			buffer += "  ";
//			int pos = 1;
////			for (int i = 0; i <= data.length - 3; i += 3) {
//			for (int i = data.length-3; i >= 0; i -= 3) {
//				if (note.equals("CLASS2ONETZDATA")
//						|| note.equals("CLASS2TWOTZDATA")) {
////					buffer += "时区起始日期" + (i / 3 + 1) + " ";
//					buffer += "时区起始日期" + pos + " ";
//				} else {
////					buffer += "时段" + (i / 3 + 1) + " ";
//					buffer += "时段" + pos + " ";
//				}
//				buffer += String.format("%02d", (data[i]));
//				if (note.equals("CLASS2ONETZDATA")
//						|| note.equals("CLASS2TWOTZDATA")) {
//					buffer += "月";
//				} else {
//					buffer += "时";
//				}
//				buffer += String.format("%02d", (data[i + 1]));
//				if (note.equals("CLASS2ONETZDATA")
//						|| note.equals("CLASS2TWOTZDATA")) {
//					buffer += "日";
//				} else {
//					buffer += "分";
//				}
//				buffer += " ";
//				if (note.equals("CLASS2ONETZDATA")
//						|| note.equals("CLASS2TWOTZDATA")) {
//					buffer += "日时段表号 ";
//				} else {
//					buffer += "费率号  ";
//				}
//				buffer += String.format("%02d", (data[i + 2]));
//				// buffer += "\n";
//				buffer += "<br/>";
//				++pos;
//			}
//		} else if (note.equals("CLASS2ONETPRDTDATA2")
//				|| note.equals("CLASS2ONETPRDTDATA3")
//				|| note.equals("CLASS2ONETPRDTDATA4")
//				|| note.equals("CLASS2ONETPRDTDATA5")
//				|| note.equals("CLASS2ONETPRDTDATA6")
//				|| note.equals("CLASS2ONETPRDTDATA7")
//				|| note.equals("CLASS2ONETPRDTDATA8")
//				|| note.equals("CLASS2TWOTPRDTDATA2")
//				|| note.equals("CLASS2TWOTPRDTDATA3")
//				|| note.equals("CLASS2TWOTPRDTDATA4")
//				|| note.equals("CLASS2TWOTPRDTDATA5")
//				|| note.equals("CLASS2TWOTPRDTDATA6")
//				|| note.equals("CLASS2TWOTPRDTDATA7")
//				|| note.equals("CLASS2TWOTPRDTDATA8")) {
//
//			buffer += "  ";
//			int pos = 1;
////			for (int i = 0; i <= data.length - 3; i += 3) {
//			for (int i = data.length-3; i >=0; i -= 3) {
////				buffer += "时段" + (i / 3 + 1) + " ";
//				buffer += "时段" + pos + " ";
//				buffer += String.format("%02d", (data[i]));
//				buffer += "：";
//				buffer += String.format("%02d", (data[i + 1]));
//				buffer += "费率号  ";
//				buffer += String.format("%02d", (data[i + 2]));
//				buffer += "<br/>";
//				pos ++;
//			}
//		} else if (note.equals("CLASS2PHANDTP01")
//				|| note.equals("CLASS2PHANDTP02")
//				|| note.equals("CLASS2PHANDTP03")
//				|| note.equals("CLASS2PHANDTP04")
//				|| note.equals("CLASS2PHANDTP05")
//				|| note.equals("CLASS2PHANDTP06")
//				|| note.equals("CLASS2PHANDTP07")
//				|| note.equals("CLASS2PHANDTP08")
//				|| note.equals("CLASS2PHANDTP09")
//				|| note.equals("CLASS2PHANDTP10")
//				|| note.equals("CLASS2PHANDTP11")
//				|| note.equals("CLASS2PHANDTP12")) {
//			buffer += "  ";
//			for (int i = 0; i <= data.length - 4; i += 4) {
//				buffer += "假日日期" + (i / 3 + 1) + " ";
//				buffer += String.format("%02d", (data[i]));
//				buffer += "：";
//				buffer += String.format("%02d", (data[i + 1]));
//				buffer += "：";
//				buffer += String.format("%02d", (data[i + 2]));
//				buffer += "费率号：";
//				buffer += String.format("%02d", (data[i + 3]));
//				buffer += "<br/>";
//			}
//		} else if (note.equals("CLASS2ONEV1") || note.equals("CLASS2ONEV2")
//				|| note.equals("CLASS2ONEV3") || note.equals("CLASS2ONEV4")
//				|| note.equals("CLASS2ONEV5") || note.equals("CLASS2ONEV6")
//				|| note.equals("CLASS2ONEV7") || note.equals("CLASS2ONEV8")) {
//			buffer += "第一套第" + note.substring(10, 11) + "阶梯值：";
//			value.insert(6, '.');
//			value.append("KWH");
//			buffer += value.toString();
//
//		} else if (note.equals("CLASS2ONEF1") || note.equals("CLASS2ONEF2")
//				|| note.equals("CLASS2ONEF3") || note.equals("CLASS2ONEF4")
//				|| note.equals("CLASS2ONEF5") || note.equals("CLASS2ONEF6")
//				|| note.equals("CLASS2ONEF7") || note.equals("CLASS2ONEF8")) {
//			buffer += "第一套第" + note.substring(10, 11) + "阶梯电价：";
//			value.insert(4, '.');
//			value.append("元");
//			buffer += value.toString();
//		} else if (note.equals("CLASS2TWOV1") || note.equals("CLASS2TWOV2")
//				|| note.equals("CLASS2TWOV3") || note.equals("CLASS2TWOV4")
//				|| note.equals("CLASS2TWOV5") || note.equals("CLASS2TWOV6")
//				|| note.equals("CLASS2TWOV7") || note.equals("CLASS2TWOV8")) {
//			buffer += "第二套第" + note.substring(10, 11) + "阶梯值：";
//			value.insert(6, '.');
//			value.append("KWH");
//			buffer += value.toString();
//		} else if (note.equals("CLASS2TWOF1") || note.equals("CLASS2TWOF2")
//				|| note.equals("CLASS2TWOF3") || note.equals("CLASS2TWOF4")
//				|| note.equals("CLASS2TWOF5") || note.equals("CLASS2TWOF6")
//				|| note.equals("CLASS2TWOF7") || note.equals("CLASS2TWOF8")) {
//			buffer += "第二套第" + note.substring(10, 11) + "阶梯电价：";
//			value.insert(4, '.');
//			value.append("元");
//			buffer += value.toString();
//		} else {
//			log.error("未识别的note：" + note);
//			return null;
//		}
//		log.debug("解析数据：" + buffer);
//		return buffer;
//	}
//	
//	public int getPwdFromPwdComputer(String soLibName, int Counter, byte[] div, byte[] RandAndEndata){
//		return getPwdFromPwdComputer(soLibName, Counter, div, null, RandAndEndata ,null);
//	}
//	
//	public int getPwdFromPwdComputer(String soLibName, int Counter, byte[] Div,byte[] esam,
//			byte[] RandAndEndata,ArrayList<Integer> out) {
//		byte[] rand = null;
//		int ret = -1;
//		if (soLibName.equalsIgnoreCase("UserControl")){
//     try {
//				char[] char_rand = new char[40];
//				log.debug("-----------------------------------用户控制参数输入-------------------------------");
//				log.debug(HexDump.toHexString(Div).toString());
//				ret = shareGlobalObjVar.getLoadLib().UserControl(1,HexDump.toHexString(Div).toString(), char_rand);
//				log.debug("-----------------------------------用户控制参数输出-------------------------------");
//				log.debug(char_rand.toString());
//				String tmpStr = new String(char_rand);
//				log.debug("控制输出str：" + tmpStr +"返回值 ： " + ret);
//				
//				rand = HexDump.hexStringToByteArray(tmpStr);
//				if (rand.length != 20){
//					log.error("UserControl 返回的字节长度不是20,len："+ rand.length);
//					return -1;
//				}
//
//			} catch (UnknownHostException e) {
//				// TODO Auto-generated catch block
//				log.error("getPwdFromPwdComputer in Dl698TransParent #1", e);
//				return -1;
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				log.error("getPwdFromPwdComputer in Dl698TransParent #2", e);
//				return -1;
//			}
//			
//		}
//		else if (soLibName.equalsIgnoreCase("IdentityAuthentication")){
//			try {
//				char[] char_rand = new char[32];
//		
//				//用户身份验证Test输入动态库之前的报文和之后的报文
//				log.debug("用户身份认证输入:"+ HexDump.toHexString(Div).toString());
//				ret = shareGlobalObjVar.getLoadLib().IdentityAuthentication(1,HexDump.toHexString(Div).toString(), char_rand);
//				String tmpStr = new String(char_rand).toString();
////				if (tmpStr.length() != 32){
////					log.error("getPwdFromPwdComputer in Dl698TransParent #5,len :" + 
////							tmpStr.length()  + "tmpStr:" + tmpStr);
////					return -1;
////				}
//				tmpStr = tmpStr.substring(0, 32);
//				log.debug("用户身份认证输出str：" + tmpStr +"返回值 ： " + ret);			
//				rand = HexDump.hexStringToByteArray(tmpStr);
//				log.debug("用户身份认证输出：" + HexDump.toHexString(rand).toString());
//				if (rand.length != 16){
//					log.error("UserControl 返回的字节长度不是16,len："+ rand.length);
//					return -1;
//				}
//
//			} catch (UnknownHostException e) {
//				log.error("getPwdFromPwdComputer in Dl698TransParent #3", e);
//				return -1;
//			} catch (IOException e) {
//				log.error("getPwdFromPwdComputer in Dl698TransParent #4", e);
//				return -1;
//			}
//			
//		}
//		else if (soLibName.equalsIgnoreCase("ParameterUpdate") || soLibName.equalsIgnoreCase("Parameter1")
//				|| soLibName.equalsIgnoreCase("Parameter2") || soLibName.equalsIgnoreCase("InCreasePurse")){
//			try {
//				int len = Div.length*10 ;
//				char[] char_rand = new char[len];
//		
//				//用户身份验证Test输入动态库之前的报文和之后的报文
//				log.debug("参数修改输入:"+ HexDump.toHexString(Div).toString());
//				if(soLibName.equalsIgnoreCase("ParameterUpdate")){
//					ret = shareGlobalObjVar.getLoadLib().ParameterUpdate(HexDump.toHexString(Div).toString(), char_rand);
//				}else if(soLibName.equalsIgnoreCase("Parameter1")){
//					ret = shareGlobalObjVar.getLoadLib().ParameterUpdate1(HexDump.toHexString(Div).toString(), char_rand);
//				}else if(soLibName.equalsIgnoreCase("Parameter2")){
//					ret = shareGlobalObjVar.getLoadLib().ParameterUpdate2(HexDump.toHexString(Div).toString(), char_rand);
//				}else if(soLibName.equalsIgnoreCase("InCreasePurse")){
//					ret = shareGlobalObjVar.getLoadLib().InCreasePurse(HexDump.toHexString(Div).toString(), char_rand);
//				}
//				String tmpStr = new String(char_rand).toString();
//				log.debug("参数修改输出str：" + tmpStr +"返回值 ： " + ret);
//				log.debug("输出长度=" + tmpStr.length());
//				
//				int pos = 0;
//				char c;
//				while(pos < tmpStr.length()){
//					c = tmpStr.toUpperCase().charAt(pos);
//					if((c >= '0' && c <= '9') || (c >= 'A') && c <= 'F'){
//						pos ++;
//						continue;
//					}else {
//						tmpStr = tmpStr.substring(0,pos);
//						break;
//					}
//				}
//				
//				rand = HexDump.hexStringToByteArray(tmpStr);
//				log.debug("参数修改输出：" + HexDump.toHexString(rand).toString());
//				out.add(rand.length);
//				
//			} catch (UnknownHostException e) {
//				log.error("getPwdFromPwdComputer in Dl698TransParent #3", e);
//				return -1;
//			} catch (IOException e) {
//				log.error("getPwdFromPwdComputer in Dl698TransParent #4", e);
//				return -1;
//			}
//		}
//		else if (soLibName.equalsIgnoreCase("ParameterElseUpdate")){
//			try {
//				int len = Div.length*10 ;
//				char[] char_rand = new char[len];
//		
//				//用户身份验证Test输入动态库之前的报文和之后的报文
//				log.debug("参数修改输入:"+ HexDump.toHexString(Div).toString());
//				ret = shareGlobalObjVar.getLoadLib().ParameterElseUpdate(1,HexDump.toHexString(Div).toString(), 
//						HexDump.toHexString(esam).toString(), char_rand);
//				String tmpStr = new String(char_rand).toString();
//				log.debug("参数修改输出str：" + tmpStr +"返回值 ： " + ret);	
//				log.debug("输出长度=" + tmpStr.length());
//				int pos = 0;
//				char c;
//				while(pos < tmpStr.length()){
//					c = tmpStr.toUpperCase().charAt(pos);
//					if((c >= '0' && c <= '9') || (c >= 'A') && c <= 'F'){
//						pos ++;
//						continue;
//					}else {
//						tmpStr = tmpStr.substring(0,pos);
//						break;
//					}
//				}
//				rand = HexDump.hexStringToByteArray(tmpStr);
//				log.debug("参数修改输出：" + HexDump.toHexString(rand).toString());
//				out.add(rand.length);
//				
//			} catch (UnknownHostException e) {
//				// TODO Auto-generated catch block
//				log.error("getPwdFromPwdComputer in Dl698TransParent #3", e);
//				return -1;
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				log.error("getPwdFromPwdComputer in Dl698TransParent #4", e);
//				return -1;
//			}
//			
//		}
//		System.arraycopy(rand, 0, RandAndEndata, 0, rand.length);
//		return ret;
//	}
//
//	public ArrayList<TaskResultObject> receiveMeterControl(AsduConverter asduConver,
//			TaskQueueObject taskObj, String meterAddr,
//			byte[] esamSequence, byte[] random2,
//			byte[] jmBeforeReport, byte[] dataFlag) {
//		ArrayList<TaskResultObject> retObjList = null;
//		TaskResultObject resultObj = null;
//		Fk04ReceivePacket recv = new Fk04ReceivePacket();
//		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
//		TerminalObject tmpTermObj = shareGlobalObjVar
//				.getTerminalPara(terminalAddr);
//		if (tmpTermObj == null) {
//			log.error("tmpTermObj == null");
//			return assFailTaskResultObject(taskObj, retObjList, null, "终端对象空",
//					true);
//		}
//		MeterInfo meterInfo = tmpTermObj.getMeterInfos().get(0);
//		int identityType = meterInfo.getIdentityType();
//		String meterNo = meterInfo.getBakString();
//
//		// 下发参数－－－－－－－－－－－分散因子
//		byte[] fsyz = null;
//		if (identityType == MeterInfo.IDENTITY_BY_NO) {
//			fsyz = getfsyz(meterNo);
//		} else {
//			if (tmpTermObj.getMeterNo() != null) {
//				fsyz = getfsyz(tmpTermObj.getMeterNo());
//			} else {
//				fsyz = getfsyz(meterAddr);
//			}
//		}
//		if (fsyz == null || fsyz.length <= 0) {
//			log.error("fsyz == null || fsyz<=0 in receiveDataForwardingData Error #1");
//			return assFailTaskResultObject(taskObj, retObjList, null,
//					"接受报文中判断异常，加密前无分散因子", true);
//		}
//
//		log.debug("分散因子" + HexDump.toHexString(fsyz).toString());
//
//		int len = random2.length + fsyz.length + esamSequence.length
//				+ jmBeforeReport.length;
//		byte[] div = new byte[len];
//		int jmBeforeCount = 0;
//		System.arraycopy(random2, 0, div, jmBeforeCount, random2.length);
//		jmBeforeCount += random2.length;
//		System.arraycopy(fsyz, 0, div, jmBeforeCount, fsyz.length);
//		jmBeforeCount += fsyz.length;
//		System.arraycopy(esamSequence, 0, div, jmBeforeCount,
//				esamSequence.length);
//		jmBeforeCount += esamSequence.length;
//		System.arraycopy(jmBeforeReport, 0, div, jmBeforeCount,
//				jmBeforeReport.length);
//		jmBeforeCount += jmBeforeReport.length;
//
//		// 密文
//		byte[] jmAfterReport = new byte[20];
//
//		log.debug("usercontrol ：" + HexDump.toHexString(div).toString()
//				+ "len:" + div.length);
//		int ret = getPwdFromPwdComputer("UserControl", 1, div, jmAfterReport);
//		if (ret != 0) {
//			log.error("调用加密机接口失败");
//			return assFailTaskResultObject(taskObj, retObjList, null,
//					"调用加密机接口失败", true);
//		}
//		log.debug("加密报文 ：" + HexDump.toHexString(jmAfterReport).toString()
//				+ "len:" + jmAfterReport.length);
//
//		byte[] controlReport = null;
//		try {
//			controlReport = assControlReport(meterAddr, jmAfterReport,
//					dataFlag, 0);
//		} catch (Exception e) {
//			log.error("组装透抄698报文出错", e.toString());
//			return assFailTaskResultObject(taskObj, retObjList, null,
//					"组装透抄698报文出错", true);
//		}
//
//		// 设置透明报文！！
//		shareGlobalObjVar.setTransBuf(terminalAddr, controlReport);
//
//		log.debug("control report: "
//				+ HexDump.toHexString(controlReport).toString() + "len:"
//				+ controlReport.length);
//		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
//		int frameIndexNo = 0;
//
//		ConcurrentLinkedQueue<TaskQueueObject> queue = shareGlobalObjVar
//				.getTmnlTaskQueue(taskObj.getTermTask().getTerminalAddr());
//		TaskQueueObject o = queue.peek();
//		if (o == null || o.getTermTask().getTaskId() <=0) {
//			o = taskObj;
//			log.debug("任务队列头被删");
////			return assFailTaskResultObject(taskObj, retObjList, null,
////					"找不到任务队列首", true);
//		}
//		sendBuff[frameIndexNo++] = 0x10; // afnNo
//		byte terminalPFC = tmpTermObj.getTerminalPFC();
//		tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));
//		tmpTermObj.setTerminalTpV(false);
//		tmpTermObj.setTerminalFIR(true);
//		tmpTermObj.setTerminalFIN(true);
//		tmpTermObj.setTerminalCON(false);
//
//		int seqNum = Fk04commFunction.getSeqNumber(terminalAddr);// SEQ
//		sendBuff[frameIndexNo++] = (byte) seqNum;
//
//		ArrayList<String> retStrList = asduConver
//				.convertDataObjToBuff(/* taskObj */o);
//		if (retStrList == null) {
//			log.error("retStrList == null in Dl698SendPacker Error #8 ");
//			return assFailTaskResultObject(taskObj, retObjList, null,
//					"asdu转换出错", true);
//		} else {
//			if (retStrList.size() > 0) {
//				byte tmpBuff[];
//				try {
//					tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
//					for (int m = 0; m < tmpBuff.length; m++) {
//						sendBuff[frameIndexNo++] = tmpBuff[m];
//					}
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//					return assFailTaskResultObject(taskObj, retObjList, null,
//							"不支持的字符编码转换", true);
//				}
//			}
//			log.debug("--Dl698Protocol透抄报文发送645控制报文，数据长度：" + frameIndexNo);
//
//			o.setStep(Fk04TransParent.STEP_OPERATE);
//
//			resultObj = new TaskResultObject();
//			seqNum = Fk04commFunction.getSeqNumber(terminalAddr);// SEQ
//			resultObj.setFramePSEQ((short) (seqNum & 0x0f));
//			resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
//			resultObj.setResultValue(0);
//			resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
//
//			resultObj.setRetMsgLen(frameIndexNo);
//			resultObj.setRetMsgBuf(sendBuff);
//			retObjList = new ArrayList<TaskResultObject>();
//			retObjList.add(resultObj);
//
//			return retObjList;
//		}
//	}
//	
//	public ArrayList<TaskResultObject> receiveMeterParamSetData(AsduConverter asduConver,
//			TaskQueueObject taskObj, String meterAddr,byte[] esam,
//			byte[] random2,	byte[] jmBeforeReport, byte[] updateCommand, int startPos, int lc,
//			String soLibName, byte[] dataFlag, int CLASS_FLAG) {
//		
//		
//		ArrayList<TaskResultObject> retObjList = null;
//		TaskResultObject resultObj = null;
//		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
//		TerminalObject tmpTermObj = shareGlobalObjVar
//				.getTerminalPara(terminalAddr);
//		if (tmpTermObj == null) {
//			log.error("tmpTermObj == null");
//			return assFailTaskResultObject(taskObj, retObjList, null, "终端对象空",
//					true);
//		}
//
//		MeterInfo meterInfo = tmpTermObj.getMeterInfos().get(0);
//		int identityType = meterInfo.getIdentityType();
//		String meterNo = meterInfo.getBakString();
//		// 下发参数－－－－－－－－－－－分散因子
//		byte[] fsyz = null;
//		if (identityType == MeterInfo.IDENTITY_BY_NO) {
//			fsyz = getfsyz(meterNo);
//		} else {
//			if (tmpTermObj.getMeterNo() != null) {
//				fsyz = getfsyz(tmpTermObj.getMeterNo());
//			} else {
//				fsyz = getfsyz(meterAddr);
//			}
//		}
//		if (fsyz == null || fsyz.length <= 0) {
//			log.error("fsyz == null || fsyz<=0");
//			return assFailTaskResultObject(taskObj, retObjList, null,
//					"加密前无分散因子", true);
//		}
//		log.debug("分散因子：" + HexDump.toHexString(fsyz).toString());
//		int len = 0;
//		if (CLASS_FLAG == 1) {
//			len = 17 + jmBeforeReport.length;
//		} else {
//			len = 17 + jmBeforeReport.length + 4; // 17 = 随机数(4) + 分散因子(8) +
//													// 更新指令(5) +数据标识（4）
//		}
//		byte[] div = new byte[len];
//		// modi gaolx 20110307 --end
//
//		int jmBeforeCount = 0;
//		log.debug("random2=" + HexDump.toHexString(random2).toString());
//		System.arraycopy(random2, 0, div, jmBeforeCount, random2.length);
//		jmBeforeCount += random2.length;
//
//		log.debug("fsyz=" + HexDump.toHexString(fsyz).toString());
//		System.arraycopy(fsyz, 0, div, jmBeforeCount, fsyz.length);
//		jmBeforeCount += fsyz.length;
//
//		log.debug("updateCommand="
//				+ HexDump.toHexString(updateCommand).toString());
//		System.arraycopy(updateCommand, 0, div, jmBeforeCount,
//				updateCommand.length);
//		jmBeforeCount += updateCommand.length;
//
//		// 起始值
//
//		div[jmBeforeCount++] = (byte) startPos;
//
//		div[jmBeforeCount++] = (byte) lc;
//
//		if (CLASS_FLAG == 1) {
//		} else {
//			// add by gaolx --start
//			for (int i = dataFlag.length - 1; i >= 0; i--) {
//				div[jmBeforeCount++] = dataFlag[i];
//			}
//		}
//		// end
//		System.arraycopy(jmBeforeReport, 0, div, jmBeforeCount,
//				jmBeforeReport.length);
//		jmBeforeCount += jmBeforeReport.length;
//
//		// 密文
//		byte[] jmAfterReport = new byte[div.length + 1024];
//
//		log.debug("参数更新输入 ,div=" + HexDump.toHexString(div).toString()
//				+ "esam=" + HexDump.toHexString(esam).toString());
//		log.debug("调用函数名：" + soLibName);
//		Integer outLen = 0;
//		ArrayList<Integer> out = new ArrayList<Integer>();
//		int ret = getPwdFromPwdComputer(soLibName, 1, div, esam, jmAfterReport,
//				out);
//		outLen = out.get(0);
//		if (ret != 0 || outLen <= 0) {
//			log.error("调用加密机参数更新接口失败");
//			return assFailTaskResultObject(taskObj, retObjList, null,
//					"调用加密机参数更新接口失败", true);
//		}
//		byte[] tmp = new byte[outLen.intValue()];
//		System.arraycopy(jmAfterReport, 0, tmp, 0, outLen.intValue());
//		jmAfterReport = tmp;
//		log.debug("参数更新输出密文 ：" + HexDump.toHexString(jmAfterReport).toString());
//
//		byte[] controlReport = null;
//		try {
//			controlReport = assControlReport(meterAddr, jmAfterReport,
//					dataFlag, CLASS_FLAG);
//		} catch (Exception e) {
//			log.error("组装透抄698报文出错", e);
//			return assFailTaskResultObject(taskObj, retObjList, null,
//					"接受报文中判断异常，组装透抄698报文出错", true);
//		}
//
//		// 设置透明报文！！
//		shareGlobalObjVar.setTransBuf(terminalAddr, controlReport);
//
//		log.debug("××××××××××发送参数报文"
//				+ HexDump.toHexString(controlReport).toString());
//
//		byte sendBuff[] = new byte[ConstDef.DL698FRAMELENGTH];
//		int frameIndexNo = 0;
//
//		ConcurrentLinkedQueue<TaskQueueObject> queue = shareGlobalObjVar
//				.getTmnlTaskQueue(taskObj.getTermTask().getTerminalAddr());
//		TaskQueueObject o = queue.peek();
//		if (o == null || o.getTermTask().getTaskId() <=0) {
//			o = taskObj;
//			log.debug("任务队列头被删");
////			log.error("TaskQueueObject 为空");
////			return assFailTaskResultObject(taskObj, retObjList, null, "终端对象空",
////					true);
//		}
//		sendBuff[frameIndexNo++] = 0x10; // afnNo
//		byte terminalPFC = tmpTermObj.getTerminalPFC();
//		tmpTermObj.setTerminalPFC((byte) (terminalPFC + 1));
//		tmpTermObj.setTerminalTpV(false);
//		tmpTermObj.setTerminalFIR(true);
//		tmpTermObj.setTerminalFIN(true);
//		tmpTermObj.setTerminalCON(false);
//
//		int seqNum = Fk04commFunction.getSeqNumber(terminalAddr);// SEQ
//		sendBuff[frameIndexNo++] = (byte) seqNum;
//
//		ArrayList<String> retStrList = asduConver
//				.convertDataObjToBuff(/* taskObj */o);
//		if (retStrList == null) {
//			log.error("retStrList == null");
//			return assFailTaskResultObject(taskObj, retObjList, null,
//					"组装透抄报文失败", true);
//		} else {
//			if (retStrList.size() > 0) {
//				byte tmpBuff[];
//				try {
//					tmpBuff = retStrList.get(0).getBytes("ISO-8859-1");
//					for (int m = 0; m < tmpBuff.length; m++) {
//						sendBuff[frameIndexNo++] = tmpBuff[m];
//					}
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//					return assFailTaskResultObject(taskObj, retObjList, null,
//							"不支持的字符转换", true);
//				}
//			}
//
//			o.setStep(Fk04TransParent.STEP_SET_PARAM);
//
//			resultObj = new TaskResultObject();
//			seqNum = Fk04commFunction.getSeqNumber(terminalAddr);// SEQ
//			resultObj.setFramePSEQ((short) (seqNum & 0x0f));
//			resultObj.setResultType(ConstDef.TASKRESULT_SENDCOMMAND);
//			resultObj.setResultValue(0);
//			resultObj.setTaskProtoType(taskObj.getTermTask().getProtocol());
//
//			byte afnNo = resultObj.getRetMsgBuf()[0];
//			resultObj.setRetMsgLen(frameIndexNo);
//			resultObj.setRetMsgBuf(sendBuff);
//			retObjList = new ArrayList<TaskResultObject>();
//			retObjList.add(resultObj);
//
//			return retObjList;
//		}
//	}
//	
//	public ArrayList<TaskResultObject> assFailTaskResultObject(TaskQueueObject taskObj,
//			ArrayList<TaskResultObject> retObjList,ResponseItem resItem, String note){
//		return assFailTaskResultObject(taskObj, retObjList, resItem, note, false);
//	}
//	
//	/**
//	 * 返回此电表控制失败信息
//	 * @param taskObj
//	 * @param retObjList
//	 * @param resItem
//	 * @param note responsetItem中的内容
//	 * @param chgStep 是否要转换step并删除当前表
//	 * @return
//	 */
//	public ArrayList<TaskResultObject> assFailTaskResultObject(TaskQueueObject taskObj,
//			ArrayList<TaskResultObject> retObjList,ResponseItem resItem, String note,boolean chgStep){
//		List<MeterInfo> meterInfos = null;
//		TaskResultObject resultObj = null;
//		
//		log.error(note);
//		
//		TerminalObject tmpTermObj = shareGlobalObjVar.getTerminalPara(taskObj
//				.getTermTask().getTerminalAddr());
//		Response res = new Response();
//		res.setTaskId(taskObj.getTermTask().getTaskId());
//		res.setTerminalAddr(taskObj.getTermTask().getTerminalAddr());
//		res.setTmnlAssetNo(taskObj.getTermTask().getTmnlAssetNo());
//		res.setDbDatas(null);
//		res.setContinue(false);
//		res.setErrorCode(ErrorCode.OK);
//		res.setFuncCode((short) (taskObj.getTermTask().getFuncCode()));
//		res.setNote(note);
//		res.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
//		if (tmpTermObj == null) {
//			log.error("tmpTermObj == null in Dl698SendPacker Error #1");
//		}else{
//			meterInfos = tmpTermObj.getMeterInfos();
//		}
//		
//		if(meterInfos == null || meterInfos.size() <=0){
//			log.error("终端已无表信息");
//		}else{
//			//将超时的表的所有功能项删除(第一个不删除）
//			removeMeterInfoForFail(meterInfos);
//			MeterInfo meterInfo = meterInfos.get(0);
//			String meterId = String.valueOf(meterInfo.getMeterID());
//			if(resItem == null){
//				resItem = new ResponseItem();
//				resItem.setErrorCode((short)ErrorCode.B_RuntimeErr.getId());
//			}
//			resItem.setValue(meterId);
//			resItem.setPn((short)0);
//			resItem.setFn((short)1);
//			resItem.setStatus((short)1);
//			resItem.setCode(note);
//			if(chgStep){
//				if(tmpTermObj.getMeterInfos().size() >0){
//					Fk04ReceivePacket recv = new Fk04ReceivePacket();
////					recv.changeStep(taskObj, true);   //wyx
////					recv.removeCurrentMeter(tmpTermObj);
//				}
//			}
//		}
//		List<ResponseItem> itemList = new ArrayList<ResponseItem>();
//		itemList.add(resItem);
//		res.setResponseItems(itemList);
//		
//		resultObj = new TaskResultObject();
//		resultObj.setResultParaObj(res);
//		resultObj.setResultType(ConstDef.TASKRESULT_RESPONSECOMMAND);
//		resultObj.setResultValue(0);
//		resultObj.setRetMsgLen(0);
//		resultObj.setRetMsgBuf(null);
//		resultObj.setTaskID(taskObj.getTermTask().getTaskId());
//
//		if (retObjList == null)
//			retObjList = new ArrayList<TaskResultObject>();
//		retObjList.add(resultObj);
//		return retObjList;
//	}
//
////	public ArrayList<TaskResultObject> assResponse(TaskQueueObject taskObj,
////			ArrayList<TaskResultObject> retObjList, Response resPara,
////			String note) {
////		TaskResultObject resultObj = null;
////		resPara.setTaskId(taskObj.getTermTask().getTaskId());
////		resPara.setTerminalAddr(taskObj.getTermTask().getTerminalAddr());
////		resPara.setFuncCode((short) (taskObj.getTermTask().getFuncCode()));
////		resPara.setTmnlAssetNo(taskObj.getTermTask().getTmnlAssetNo());
////		resPara.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
////		resPara.setNote(note);
////
////		TerminalObject tmpTermObj = shareGlobalObjVar
////				.getTerminalPara(taskObj.getTermTask().getTerminalAddr());
////		if (tmpTermObj == null) {
////			log.error("tmpTermObj == null in Dl698SendPacker Error #1");
////			return null;
////		}
////		List<MeterInfo> meterInfos = tmpTermObj.getMeterInfos();
////		MeterInfo meterInfo = meterInfos.get(0);
////		String meterId = String.valueOf(meterInfo.getMeterID());
////		resPara.setContinue(false);
////
////		resPara.setErrorCode(ErrorCode.NakAnswer);
////		ResponseItem responseItems = new ResponseItem();
////		responseItems.setValue(meterId);
////		responseItems.setStatus((short) 1);
////		responseItems.setErrorCode((short)ErrorCode.NakAnswer.getId());
////		responseItems.setPn((short) 0);
////		responseItems.setFn((short) 1);
////		responseItems.setCode(note);
////		List<ResponseItem> itemList = new ArrayList<ResponseItem>();
////		itemList.add(responseItems);
////		resPara.setResponseItems(itemList);
////		resPara.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
////		
/////*		if (resPara.getResponseItems().get(0).getValue() == null){
////			TerminalObject tmpTermObj = shareGlobalObjVar
////			.getTerminalPara(taskObj.getTermTask().getTerminalAddr());
////	if (tmpTermObj == null) {
////		long meterId = tmpTermObj.getMeterInfos().get(0).getMeterID();
////		resPara.getResponseItems().get(0).setValue(new Long(meterId).toString());
////		}
////		}
////		*/
////		resultObj = new TaskResultObject();
////		resultObj.setResultParaObj(resPara);
////		resultObj.setResultType(ConstDef.TASKRESULT_RESPONSECOMMAND);
////		resultObj.setResultValue(0);
////		resultObj.setRetMsgLen(0);
////		resultObj.setRetMsgBuf(null);
////		resultObj.setTaskID(taskObj.getTermTask().getTaskId());
////
////		
////		
////		if (retObjList == null)
////			retObjList = new ArrayList<TaskResultObject>();
////		retObjList.add(resultObj);
////		return retObjList;
////	}
//
//	public ArrayList<TaskResultObject> receiveReadMeterDataResponse(
//			TaskQueueObject taskObj, byte[] packetBuff,int len_645) {
//		ArrayList<TaskResultObject> retObjList = null;
//		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
//		TerminalObject tmpTermObj = shareGlobalObjVar
//				.getTerminalPara(terminalAddr);
//		if (tmpTermObj == null) {
////			log.error("tmpTermObj == null in Dl698SendPacker Error #1");
////			Response resPara = new Response();
////			return assResponse(taskObj, retObjList, resPara,"组装透抄报文失败，retStrList == null");
//			return assFailTaskResultObject(taskObj, retObjList, null, "终端对象空");
//		}
//	Response resPara = new Response();
//	List<MeterInfo> meterInfos = tmpTermObj.getMeterInfos();
//	if (meterInfos == null||meterInfos.size() <= 0) {
//		log
//				.error("meterInfos == null in receiveDataForwardingData Error #1");
////		resPara = new Response();
////		return assResponse(taskObj, retObjList, resPara,
////				"接受报文中判断异常，无存储电表信息");
//		return assFailTaskResultObject(taskObj, retObjList, null, "无存储电表信息");
//	}
////	if (meterInfos.size() <= 0) {
////		log
////				.error("meterInfos.size() <=0 in receiveDataForwardingData Error #1");
////		resPara = new Response();
////		return assResponse(taskObj, retObjList, resPara,
////				"接受报文中判断异常，无存储电表信息，长度为零");
////	}
//
//	//String meterAddr = meterInfos.get(0).getMeterAddr();
//	//String meterId = String.valueOf(meterInfos.get(0).getMeterID());
//
//	//字节个数是固定的
//	int ret = 0;
//	byte[] buf645 = new byte[len_645];
//	  
//	System.arraycopy(packetBuff,19,buf645,0,len_645);
//	try {
//		ret = recSetOneTypeReport(meterInfos.get(0),terminalAddr, buf645, taskObj
//				.getTaskMessage().length, resPara);
//	} catch (Exception e) {
//		log
//				.error(
//						"接受控制命令失败，recControlReport in Dl698RecivePacket Error #1",
//						e);
//		return assFailTaskResultObject(taskObj, retObjList, null, "解析报文异常");
//	}
//	if (ret <= 0) {
//		return assFailTaskResultObject(taskObj, retObjList, null, "否认报文");
//		
//	}
//	else{
//		
//		TaskResultObject resultObj = null;
//		resPara.setTaskId(taskObj.getTermTask().getTaskId());
//		resPara.setTerminalAddr(taskObj.getTermTask().getTerminalAddr());
//		resPara.setFuncCode((short) (taskObj.getTermTask().getFuncCode()));
//		resPara.setTmnlAssetNo(taskObj.getTermTask().getTmnlAssetNo());
//		resPara.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
//		resPara.setNote("读数据成功");
//		
//		resultObj = new TaskResultObject();
//		resultObj.setResultParaObj(resPara);
//		resultObj.setResultType(ConstDef.TASKRESULT_RESPONSECOMMAND);
//		resultObj.setResultValue(0);
//		resultObj.setRetMsgLen(0);
//		resultObj.setRetMsgBuf(null);
//		resultObj.setTaskID(taskObj.getTermTask().getTaskId());
//		if (retObjList == null)
//			retObjList = new ArrayList<TaskResultObject>();
//		retObjList.add(resultObj);
//		return retObjList;
//		
//	}
//	}
////////////////////
//	
//
//	public ArrayList<TaskResultObject> receiveParamSetResponse(
//			TaskQueueObject taskObj, byte[] packetBuff,int len_645) {
//		ArrayList<TaskResultObject> retObjList = null;
//		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
//	TerminalObject tmpTermObj = shareGlobalObjVar
//				.getTerminalPara(terminalAddr);
//		if (tmpTermObj == null) {
//			log.error("tmpTermObj == null in Dl698SendPacker Error #1");
//			return null;
//		}
//	Response resPara = new Response();
//	List<MeterInfo> meterInfos = tmpTermObj.getMeterInfos();
//	if (meterInfos == null || meterInfos.size() <= 0) {
//		log
//				.error("meterInfos == null in receiveDataForwardingData Error #1");
//		
//		retObjList= assFailTaskResultObject(taskObj, retObjList, null, "无电表信息",true);
////		Dl698ReceivePacket recv = new Dl698ReceivePacket();
////		recv.changeStep(taskObj, true);
////		recv.removeCurrentMeter(tmpTermObj);
//		return retObjList;
//	}
//
//	int ret = 0;
//	byte[] buf645 = new byte[len_645];
//	  
//	System.arraycopy(packetBuff,21,buf645,0,len_645);
//	try {
//		ret = recSetOneTypeReport(meterInfos.get(0),terminalAddr, buf645, taskObj
//				.getTaskMessage().length, resPara);
//	} catch (Exception e) {
//		log
//				.error(
//						"接受控制命令失败，recControlReport in Dl698RecivePacket Error #1",
//						e);
//
//		
//		retObjList= assFailTaskResultObject(taskObj, retObjList, null, "解析报文异常",true);
////		Dl698ReceivePacket recv = new Dl698ReceivePacket();
////		recv.changeStep(taskObj, true);
////		recv.removeCurrentMeter(tmpTermObj);
//		return retObjList;
//	}
//	if(ret == -10){
//		ConcurrentLinkedQueue<TaskQueueObject> queue = shareGlobalObjVar
//		.getTmnlTaskQueue(taskObj.getTermTask().getTerminalAddr());
//		TaskQueueObject o = queue.peek();
//		o.setStep(Fk04TransParent.STEP_IDENTITYAUTHENTICATION);
//		taskObj.setStep(Fk04TransParent.STEP_IDENTITYAUTHENTICATION);
////		retObjList= assFailTaskResultObject(taskObj, retObjList, null, "否认报文");
//		return new Fk04SendPacket().sendDataForwarding(o);
////		return retObjList;
//	}
//	if (ret <= 0) {
//		retObjList= assFailTaskResultObject(taskObj, retObjList, null, "否认报文",true);
////		Dl698ReceivePacket recv = new Dl698ReceivePacket();
////		recv.changeStep(taskObj, true);
////		recv.removeCurrentMeter(tmpTermObj);
//		return retObjList;
//		
//	}
//	else{
//		//如果web无法知道是按照表号还是表地址进行身份认证，则返回认证方式
//		if(meterInfos.get(0).getIdentityType() == 0){
//			if(tmpTermObj.getMeterNo() != null){
//				resPara.getResponseItems().get(0).setBlockSN("1;" + meterInfos.get(0).getBakString());
//			}else{
//				resPara.getResponseItems().get(0).setBlockSN("-1;" + meterInfos.get(0).getMeterAddr());
//			}
//			log.debug("返回身份认证类型及表号:" 
//					+ resPara.getResponseItems().get(0).getBlockSN());
//		}else{
//			resPara.getResponseItems().get(0).setBlockSN("-1;" + meterInfos.get(0).getBakString());
//		}
//		
//		TaskResultObject resultObj = null;
//		resPara.setTaskId(taskObj.getTermTask().getTaskId());
//		resPara.setTerminalAddr(taskObj.getTermTask().getTerminalAddr());
//		resPara.setFuncCode((short) (taskObj.getTermTask().getFuncCode()));
//		resPara.setTmnlAssetNo(taskObj.getTermTask().getTmnlAssetNo());
//		resPara.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
//		resPara.setNote("写数据成功");
//		
//		resultObj = new TaskResultObject();
//		resultObj.setResultParaObj(resPara);
//		resultObj.setResultType(ConstDef.TASKRESULT_RESPONSECOMMAND);
//		resultObj.setResultValue(0);
//		resultObj.setRetMsgLen(0);
//		resultObj.setRetMsgBuf(null);
//		resultObj.setTaskID(taskObj.getTermTask().getTaskId());
//		if (retObjList == null)
//			retObjList = new ArrayList<TaskResultObject>();
//		retObjList.add(resultObj);
//		Fk04ReceivePacket recv = new Fk04ReceivePacket();
////		recv.changeStep(taskObj, false);  //wyx
////		recv.removeCurrentMeter(tmpTermObj);
//		return retObjList;
//		
//	}
//	}
//
//
//	public ArrayList<TaskResultObject> receiveControlResponse(
//			TaskQueueObject taskObj, byte[] packetBuff,int len_645) {
//		try{
//		ArrayList<TaskResultObject> retObjList = null;
//		String terminalAddr = taskObj.getTermTask().getTerminalAddr();
//	TerminalObject tmpTermObj = shareGlobalObjVar
//				.getTerminalPara(terminalAddr);
//		if (tmpTermObj == null) {
//			return assFailTaskResultObject(taskObj, retObjList, null, "终端对象为空");
//		}
//	Response resPara = new Response();
//	List<MeterInfo> meterInfos = tmpTermObj.getMeterInfos();
//	if (meterInfos == null || meterInfos.size() <=0) {
//		log.error("电表信息空");
//		return assFailTaskResultObject(taskObj, retObjList, null, "电表信息空");
//	}
//
//	//字节个数是固定的
//	int ret = 0;
//	byte[] buf645 = new byte[len_645];
//	  
//	System.arraycopy(packetBuff,21,buf645,0,len_645);
//	try {
//		ret = recControlReport(meterInfos.get(0),terminalAddr, buf645, taskObj
//				.getTaskMessage().length, resPara);
//	} catch (Exception e) {
//		log.error("解析控制报文异常",e.toString());
//		return assFailTaskResultObject(taskObj, retObjList, null, "解析控制报文失败");
//	}
//	if (ret <= 0) {
//		ResponseItem item = null;
//		if(resPara.getResponseItems() != null && resPara.getResponseItems().size() >0){
//			item = resPara.getResponseItems().get(0);
//		}
//		return assFailTaskResultObject(taskObj,retObjList,item, "否定报文");		
//	}
//	else{
//
//		if(meterInfos.get(0).getIdentityType() == 0){
//			if(tmpTermObj.getMeterNo() != null){
//				resPara.getResponseItems().get(0).setBlockSN("1;" + meterInfos.get(0).getBakString());
//			}else{
//				resPara.getResponseItems().get(0).setBlockSN("-1;" + meterInfos.get(0).getMeterAddr());
//			}
//			log.debug("返回身份认证类型及表号" 
//					+ resPara.getResponseItems().get(0).getBlockSN());
//		}else{
//			resPara.getResponseItems().get(0).setBlockSN("-1;" + meterInfos.get(0).getBakString());
//		}
//		String downType = meterInfos.get(0).getNote();
//		
//		imPack.saveDbRsult(String.valueOf(meterInfos.get(0).getMeterID()), downType);
////		log.debug("-----------");
//		TaskResultObject resultObj = null;
//		resPara.setTaskId(taskObj.getTermTask().getTaskId());
//		resPara.setTerminalAddr(taskObj.getTermTask().getTerminalAddr());
//		resPara.setFuncCode((short) (taskObj.getTermTask().getFuncCode()));
//		resPara.setTmnlAssetNo(taskObj.getTermTask().getTmnlAssetNo());
//		resPara.setTaskStatus(FrontConstant.TASK_STATUS_FINISH);
//		resPara.setNote("控制成功");
//		
//		resultObj = new TaskResultObject();
//		resultObj.setResultParaObj(resPara);
//		resultObj.setResultType(ConstDef.TASKRESULT_RESPONSECOMMAND);
//		resultObj.setResultValue(0);
//		resultObj.setRetMsgLen(0);
//		resultObj.setRetMsgBuf(null);
//		resultObj.setTaskID(taskObj.getTermTask().getTaskId());
//		if (retObjList == null)
//			retObjList = new ArrayList<TaskResultObject>();
//		retObjList.add(resultObj);
////		log.debug("ready return");
//		return retObjList;
//		}
//	}catch(Exception e){
//		log.error("异常：" ,e);
//		return null;
//	}
//	}
//	
////	/**
////	 *  下发费控参数数据的时候，如果当前表的某一个功能项下发失败，则
////	 *  这个表所有的功能项停止下发
////	 * @param meterInfoList 还没有完成的表地址列表
////	 * @return
////	 */
////	public void removeMeterInfoForFail(List<MeterInfo> meterInfoList){
////		if(meterInfoList== null || meterInfoList.size() <=1){
////			return ;
////		}
////		String currentAddr = meterInfoList.get(0).getMeterAddr();
////		log.debug("###removeMeterInfoForFail currentAddr " + currentAddr);
////		for(int i = 1;i < meterInfoList.size(); i++){
////			MeterInfo meterInfo = meterInfoList.get(i);
////			String addr = meterInfo.getMeterAddr();
////			if(addr != null ){
////				if(addr.equals(currentAddr)){
////					meterInfoList.remove(i);
////					log.debug("###removeMeterInfoForFail 获取到地址  " + addr + "清除MeterInfo");
////				}else{
////					log.debug("###removeMeterInfoForFail 获取到地址  " + addr + "不匹配");
////				}
////			}else{
////				log.debug("###removeMeterInfoForFail 获取到地址  " + currentAddr + "MeterInfo中无匹配的地址");
////			}
////		}
////		
////	}
//	
//	
//	/**
//	 *  下发费控参数数据的时候，如果当前表的某一个功能项下发失败，则
//	 *  这个表所有的功能项停止下发
//	 * @param meterInfoList 还没有完成的表地址列表
//	 * @return
//	 */
//	public void removeMeterInfoForFail(List<MeterInfo> meterInfoList){
//		if(meterInfoList== null || meterInfoList.size() <=1){
//			return ;
//		}
//		
//		String currentAddr = meterInfoList.get(0).getMeterAddr().trim();
//		log.debug("###removeMeterInfoForFail currentAddr " + currentAddr);
//		
//		Iterator<MeterInfo> it = meterInfoList.iterator();
//		it.next();
//		for(;it.hasNext();){
//			String addr = null;
//			addr = it.next().getMeterAddr().trim();
//			if(addr != null){
//				if(addr.equalsIgnoreCase(currentAddr)){
//					log.debug("###removeMeterInfoForFail 获取到地址  " + addr + "清除MeterInfo " );
//					it.remove();
//				}else{
//					log.debug("###removeMeterInfoForFail 获取到地址  " + addr + "不匹配");
//				}
//			}
//		}
//	}
//	
}
