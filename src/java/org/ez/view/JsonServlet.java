package org.ez.view;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.ez.model.User;


/**
 * A generic servlet optimized for JSON requests.
 * The request object should minimally have an "action" attribute which is the action to be executed. 
 * @author jmouka
 *
 */
public abstract class JsonServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        if(mustBeLoggedIn() && request.getSession().getAttribute("user")==null) {
                response.getWriter().println("{\"timeout\":\"not logged in\"}");
        }
        else {
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> output = new HashMap<String, Object>();

            String action = request.getParameter("action");
            String dataParameter = request.getParameter("data");

            /*
            HashMap<String, Object> data = null;

            if(dataParameter!=null) {
                    data = mapper.readValue(dataParameter, HashMap.class);
            }
             */

            try {
                    doActionNamed(request, action, dataParameter, output);
            }
            catch (NullPointerException e) {
                    output.put("error", "null pointer error.");
                    e.printStackTrace();
            }
            catch (Throwable e) {
                    output.put("error", e.getMessage() + (e.getCause()!=null? " (Cause: " + e.getCause().getMessage() + ")": ""));
                    e.printStackTrace();
            }
            mapper.writeValue(response.getWriter(), output);
        }


//		setRequest(null);
//		setResponse(null);
	}
	
	protected abstract void doActionNamed(HttpServletRequest request2, String action, String data, HashMap<String, Object> output);
	
	/** a check if the user is logged in before executing any actions. will return an appropriate JSON message back to client. */
	protected abstract boolean mustBeLoggedIn();
	
	/** adds a JSON error to the output */
	protected void error(HashMap output, String s) {
		output.put("error", s);
	}
	
	protected void error(HashMap output, String s, Throwable t) {
		output.put("error", s + ": " + t.getMessage());
	}
	
	public static User getUser(HttpServletRequest request) {
		Object o = request.getSession().getAttribute("user");
		return (o!=null? (User)o : null);
	}
	protected void setUser(HttpServletRequest request, User user) {
		request.getSession().setAttribute("user", user);
	}
	
	protected boolean isAssistant(HttpServletRequest request) {
		Object o = request.getSession().getAttribute("isAssistant");
		if(o==null) {
			return false;
		}
		else {
			return ((Boolean)o).booleanValue(); 
		}
	}
	protected void setIsAssistant(HttpServletRequest request) {
		request.getSession().setAttribute("isAssistant", Boolean.TRUE);
	}
	
}
