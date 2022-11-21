package engine;

/**
 * 채번 서버구동
 * doPost로 처리하여 Json, Stream타입에 따라 처리방식 구분
 * 
 * @version 1.00
 * @since 2022/11/13
 * @author Changhee Kim
 */

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.logging.Logger;
import engine.service.JsonProcess;
import engine.service.StreamProcess;

@WebServlet("/NumberingServlet")
public class NumberingServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
    public NumberingServlet() {
    	Logger.initLogger();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logger.info("<=================Sub Engine Call=================>");
				
		//content-type 구분
		String reqContentType = request.getContentType();
		int cIdx = reqContentType.lastIndexOf('/');
		reqContentType = reqContentType.substring(cIdx + 1);	
		
		//json 전문 처리
		if(reqContentType.equalsIgnoreCase("json") == true) {
			try {
				JsonProcess jsonProcess = new JsonProcess(request, response);
				jsonProcess.method();
			} catch(InterruptedException e) {
				Logger.error(e);
			}
		}
		//stream 전문 처리
		else {
			try {
				StreamProcess streamProcess = new StreamProcess(request, response);
				streamProcess.method();
			} catch (InterruptedException e) {
				Logger.error(e);
			}	
		}
	}
}
