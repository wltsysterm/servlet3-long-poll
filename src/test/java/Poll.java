import com.wlt.util.spring.AsynServletController;
import com.wlt.util.spring.asynchttp.AsyncContextMgr;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import java.io.IOException;
import java.util.Date;

/**
 * @author 魏霖涛
 * @since 2018/2/2 0002
 */
public class Poll {
    private static Logger logger = LoggerFactory.getLogger(Poll.class);
    @Test
    public void testSpringPoll() throws Exception {
        logger.info("当前长轮询监听={}", AsyncContextMgr.showList());
        AsyncContext ctx = AsyncContextMgr.get("wlt");
        if (ctx != null) {
            try {
                ctx.getResponse().getWriter().write("<script>window.parent.execAsyncCallback(" + new Date().getSeconds() + ");</script>");
                ctx.complete();
            } catch (IOException e) {
                logger.warn("通知异常", e);
            }
        } else {
            logger.info("消息消费失败，未获取到结果对应异步上下文");
        }
    }

    private void testOriginPoll() throws Exception {
        logger.info("当前长轮询监听={}", AsyncContextMgr.showList());
        AsyncContext ctx = AsyncContextMgr.get("wlt");
    }
}
