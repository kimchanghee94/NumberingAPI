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
import java.io.OutputStream;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.logging.Logger;

@WebServlet("/NumberingServlet")
public class NumberingServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

    public NumberingServlet() {
    	Logger.initLogger();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logger.debug("<=================doGet Call=================>");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logger.debug("<=================doPost Call=================>");
		
		//content-type 구분
		String reqContentType = request.getContentType();
		Logger.debug("request ContentType : " + reqContentType);
		int cIdx = reqContentType.lastIndexOf('/');
		reqContentType = reqContentType.substring(cIdx + 1);
		
		//json타입 처리
		if(reqContentType.equalsIgnoreCase("json")) {
			try {
				JsonProcess.method(request, response);
			} catch(InterruptedException e) {
				Logger.error(e);
			}
		}
		//stream전문 처리
		else {
			try {
				StreamProcess.method(request, response);
			} catch (InterruptedException e) {
				Logger.error(e);
			}	
		}
	}
	
	public void StreamProcess(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, InterruptedException {

            req.setCharacterEncoding("EUC-KR");
            res.setCharacterEncoding("UTF-8");

        	  int i = req.getContentLength();
        	  byte[] arrayOfByte = new byte[i];

            req.getInputStream().read(arrayOfByte);
            String str1 = new String(arrayOfByte);

            System.out.println("****** doPost request(str1) : " + str1);

            ServletOutputStream out1 = res.getOutputStream();
            res.setStatus(200);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            OutputStream os = res.getOutputStream();

			str1 = "javaServer";
			byte[] output_data = str1.getBytes("EUC-KR");

			os.write(output_data);
			os.flush();
	    }

}
