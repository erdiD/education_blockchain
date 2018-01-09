package de.deutschebahn.ilv.app;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This Servlet should help to prevent 404 errors when reloading the Angular App.
 */
@WebServlet(urlPatterns = {"/app/login", "/app/marketplace", "/app/newDemand", "/app/project/*", "/app/offer/*"})
public class RedirectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public RedirectServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("/ilv/app/"); 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
