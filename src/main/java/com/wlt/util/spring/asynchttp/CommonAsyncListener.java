package com.wlt.util.spring.asynchttp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 通用异步监听
 */
public class CommonAsyncListener implements AsyncListener{
	private static final Logger logger = LoggerFactory.getLogger(CommonAsyncListener.class);
	private String waitKey;
	
	public CommonAsyncListener(String waitKey){
		this.waitKey = waitKey;
	}
	
	@Override
	public void onComplete(AsyncEvent event) throws IOException {
		logger.info("删除上下文,key={}",waitKey);
		AsyncContextMgr.remove(waitKey);
	}

	@Override
	public void onTimeout(AsyncEvent event) throws IOException {
		PrintWriter writer = event.getAsyncContext().getResponse().getWriter();
		writer.write("长连接timeout");
		event.getAsyncContext().complete();
	}

	@Override
	public void onError(AsyncEvent event) throws IOException {
		logger.warn("长轮询异常关闭",event.getThrowable());
		PrintWriter writer = event.getAsyncContext().getResponse().getWriter();
		writer.write("长轮询异常关闭");
		event.getAsyncContext().complete();
	}

	@Override
	public void onStartAsync(AsyncEvent event) throws IOException {
	}

	/**
	 * 获取业务数据
	 * @param event
	 * @return
	 */
	private String getBizData(AsyncEvent event){
		return  event.getAsyncContext().getRequest().getParameter("bizData");
	}
	
}
