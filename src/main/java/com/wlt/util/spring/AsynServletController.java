package com.wlt.util.spring;

import com.wlt.core.common.MsgResult;
import com.wlt.util.spring.asynchttp.AsyncContextMgr;
import com.wlt.util.spring.asynchttp.CommonAsyncListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RequestMapping("/asynServlet")
@Controller
public class AsynServletController {
    private static Logger logger = LoggerFactory.getLogger(AsynServletController.class);
	/** 异步请求Servlet关键字参数名 */
	public static final String ASYNC_SUPPORTED = "org.apache.catalina.ASYNC_SUPPORTED";
	public static final long WAIT_TIMEOUT = 600000L;
	/**
	 * @param req
	 * @return
	 */
	@RequestMapping("/connect")
	public void connect(HttpServletRequest req, String uid) throws Exception {
		req.setAttribute(ASYNC_SUPPORTED, true);
		final AsyncContext ctx = req.startAsync();
		ctx.setTimeout(WAIT_TIMEOUT);
		AsyncContextMgr.add(uid, ctx);
		logger.info("当前监听={}",String.valueOf(AsyncContextMgr.showList()));
		ctx.addListener(new CommonAsyncListener(uid));
		logger.info("接收到异步请求 uid = {}",uid);
	}
	@ResponseBody
	@RequestMapping("/testSpringPoll")
	public MsgResult testSpringPoll() throws Exception {
		logger.info("当前长轮询监听={}", AsyncContextMgr.showList());
		AsyncContext ctx = AsyncContextMgr.get("wlt");
		if (ctx != null) {
			try {
				ctx.getResponse().getWriter().write(""+new Date().getSeconds());
//				ctx.complete();
			} catch (IOException e) {
				logger.warn("通知异常", e);
			}
		} else {
			logger.info("消息消费失败，未获取到结果对应异步上下文");
		}
		return new MsgResult();
	}
}
