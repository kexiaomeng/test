package kafkaListen.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafkaListen.interFace.AbstractListen;
import kafkaListen.object.Archives;

public class ArchiveHandler extends AbstractListen<Archives>{
	private Logger logger = LoggerFactory.getLogger(ArchiveHandler.class);
	@Override
	public void handler(Archives obj) {
		// TODO Auto-generated method stub
		logger.debug("test Message");
		logger.info(obj.getTerminalAddr());
		logger.info(obj.getOptType()+"");
		logger.info(obj.getList().get(0));
	}

}
