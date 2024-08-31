package core.mvc;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "dispatcher", urlPatterns = "/", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {
    /*
     * loadOnStartup : 서블릿을 언제 초기화 될 지 지정하는 데 사용
     * 서블릿 컨테이너가 서블릿 인스턴스를 생성하고 초기화하는 시점을 제어
     * 0 또는 음수 값: loadOnStartup 속성이 0이거나 음수일 경우,
     *          해당 서블릿은 첫 번째 요청이 들어올 때 초기화됩니다.
     *          즉, 서블릿이 처음으로 호출되기 전까지는 서블릿이 로드되거나 초기화되지 않습니다.
     * 양수 값: loadOnStartup 속성이 양수 값으로 설정되어 있으면,
     *        해당 서블릿은 서블릿 컨테이너가 시작될 때 즉시 초기화됩니다.
     *        숫자가 클수록 초기화 순서가 뒤로 밀리며, 작은 숫자(예: 1)는 더 먼저 초기화됩니다.
     *        즉, 서블릿 컨테이너가 기동될 때 미리 서블릿 인스턴스를 생성하고 init() 메소드를 호출합니다.
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);
    private static final String DEFAULT_REDIRECT_PREFIX = "redirect:";

    private RequestMapping rm;

    @Override
    public void init() throws ServletException {
        rm = new RequestMapping();
        rm.initMapping();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestUri = req.getRequestURI();
        logger.debug("Method : {}, Request URI : {}", req.getMethod(), requestUri);

        Controller controller = rm.findController(requestUri);
        try {
            String viewName = controller.execute(req, resp);
            move(viewName, req, resp);
        } catch (Throwable e) {
            logger.error("Exception : {}", e);
            throw new ServletException(e.getMessage());
        }
    }

    private void move(String viewName, HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        if (viewName.startsWith(DEFAULT_REDIRECT_PREFIX)) {
            resp.sendRedirect(viewName.substring(DEFAULT_REDIRECT_PREFIX.length()));
            return;
        }

        RequestDispatcher rd = req.getRequestDispatcher(viewName);
        rd.forward(req, resp);
    }
}
