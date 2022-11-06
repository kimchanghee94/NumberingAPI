package engine;

import java.io.IOException;
import java.io.OutputStream;

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
        System.out.println("****** doGet Call ******");
        System.out.println("****** request : " + request);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("****** doPost Call ******");
		Logger.debug("Test for Debug log");
		Logger.info("Test for Info log");
		try {
			StreamProcess(request, response);
		} catch (InterruptedException e) {
			e.printStackTrace();
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
