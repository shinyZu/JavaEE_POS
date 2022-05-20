package filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebFilter(urlPatterns = "/*")
@WebFilter(urlPatterns = {"/customer", "/item", "/orders"})
public class CORSFilter implements Filter {

    public CORSFilter() {
//        System.out.println("Object created from CORSFilter");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
//        System.out.println("CORS Filter initialized");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        servletResponse.setContentType("application/json");
        resp.addHeader("Access-Control-Allow-Origin", "*");

        if (req.getMethod().equals("OPTIONS")) {
            resp.addHeader("Access-Control-Allow-Methods", "DELETE,PUT");
            resp.addHeader("Access-Control-Allow-Headers", "Content-Type");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
//        System.out.println("CORS Filter destroyed");
    }
}
