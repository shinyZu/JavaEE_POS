package filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class CORSFilter implements Filter {

    public CORSFilter() {
        System.out.println("Object created from CORSFilter");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("CORS Filter initialized");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)servletRequest;

        filterChain.doFilter(servletRequest,servletResponse);

        HttpServletResponse resp = (HttpServletResponse) servletResponse;
//        servletResponse.setContentType("application/json");
        resp.addHeader("Access-Control-Allow-Origin","*");

        if (req.getMethod().equals("OPTIONS")){
            resp.addHeader("Access-Control-Allow-Methods","DELETE,PUT");
            resp.addHeader("Access-Control-Allow-Headers","Content-Type");
        }
    }

    @Override
    public void destroy() {
        System.out.println("CORS Filter destroyed");
    }
}
