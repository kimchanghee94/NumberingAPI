

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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GetProcessID")
public class GetProcessID extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
    public GetProcessID() {
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		int contentLength = request.getContentLength();
		byte[] recvByte = new byte[contentLength];
		request.getInputStream().read(recvByte);
		String recvStr = new String(recvByte);
		
		OutputStream os = response.getOutputStream();
		String processID = null;
		if(recvStr.equals("getProcessID")){
			processID = Thread.currentThread().getName();
		}else{
			processID = "";
		}
		byte[] output_data = processID.getBytes("EUC-KR");
		os.write(output_data);
		os.flush();
	}
}
