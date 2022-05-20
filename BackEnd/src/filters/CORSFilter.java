package filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

@WebFilter(urlPatterns = {"/customer", "/item", "/orders"})
//@WebFilter(urlPatterns = "/*")
public class CORSFilter implements Filter {

    public CORSFilter() {
        System.out.println("Object created from CORSFilter");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
//        System.out.println("CORS Filter initialized");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
       /* HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        servletResponse.setContentType("application/json");
        resp.addHeader("Access-Control-Allow-Origin", "*");

        if (req.getMethod().equals("OPTIONS")) {
            resp.addHeader("Access-Control-Allow-Methods", "DELETE,PUT");
            resp.addHeader("Access-Control-Allow-Headers", "Content-Type");
        }

        String servletPath = req.getServletPath();
        System.out.println(servletPath);

        if (servletPath.equals("/user")) {
            System.out.println("Context Path is User...");

            String userEmail = "^[a-z|0-9]{2,}@(gmail)(.com|.lk)$"; //admin@gmail.com, cashier1@gmail.lk
            String userPwdRegEx = "^[a-z]*[0-9]{3,}$"; // admin123, cashier123

            String email = servletRequest.getParameter("email");
            String pwd = servletRequest.getParameter("pwd");
            System.out.println("inside Filter : " + email);
            System.out.println("inside Filter : " + pwd);

            boolean matches = Pattern.compile(userEmail).matcher(email).matches();

            if (matches) {
                System.out.println("Already Exist...");
                resp.sendRedirect(req.getRequestURI());
                return;
            } else {
                System.out.println("Can POST...");
            }

        } else {
            System.out.println("Context Path is NOT User...");
        }

        System.out.println("Awe naaaaa");*/

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        servletResponse.setContentType("application/json");
        resp.addHeader("Access-Control-Allow-Origin", "*");

        if (req.getMethod().equals("OPTIONS")) {
            resp.addHeader("Access-Control-Allow-Methods", "DELETE,PUT");
            resp.addHeader("Access-Control-Allow-Headers", "Content-Type");
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
//        System.out.println("CORS Filter destroyed");
    }
}
