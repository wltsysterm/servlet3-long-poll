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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RequestMapping("/asynServlet")
@Controller
public class AsynServletController {
    private static Logger logger = LoggerFactory.getLogger(AsynServletController.class);
	/** 异步请求Servlet关键字参数名 */
	public static final String ASYNC_SUPPORTED = "org.apache.catalina.ASYNC_SUPPORTED";
	public static final long WAIT_TIMEOUT = 200000L;
	/**
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/connect",method = {RequestMethod.GET})
	public void connect(HttpServletRequest req, String uid) throws Exception {
		req.setAttribute(ASYNC_SUPPORTED, true);
		final AsyncContext ctx = req.startAsync();
		ctx.setTimeout(WAIT_TIMEOUT);
		AsyncContextMgr.add(uid, ctx);
		logger.info("当前监听={}",String.valueOf(AsyncContextMgr.showList()));
		CommonAsyncListener commonAsyncListener = new CommonAsyncListener(uid);
		//用来监控当前的AsyncContext
		ctx.addListener(commonAsyncListener);
		logger.info("接收到异步请求 uid = {}",uid);
	}
	@ResponseBody
	@RequestMapping("/testSpringPoll")
	public MsgResult testSpringPoll(tmpObj content) throws Exception {
		logger.info("当前长轮询监听={}", AsyncContextMgr.showList());
		for (String key:AsyncContextMgr.showList()) {
			AsyncContext ctx = AsyncContextMgr.get(key);
			if (ctx != null) {
				try {
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
					PrintWriter writer = response.getWriter();
					response.setHeader("Content-type", "text/html;charset=UTF-8");
					writer.println("<script>window.parent.doalert('"+format.format(new Date()) + ",receive:姓名=" + content.getName()+";年龄="+ content.getAge() + "')</script>");
					writer.flush();
				} catch (IOException e) {
					logger.warn("通知异常", e);
					ctx.complete();
				}
			} else {
				logger.info("消息消费失败，未获取到结果对应异步上下文");
			}
		}
		return new MsgResult("发送成功");
	}
}
class tmpObj{
	private int age;
	private String name;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
