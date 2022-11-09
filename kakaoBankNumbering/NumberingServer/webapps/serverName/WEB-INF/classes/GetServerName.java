

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
import java.net.InetAddress;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GetServerName")
public class GetServerName extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
    public GetServerName() {
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int contentLength = request.getContentLength();
		byte[] recvByte = new byte[contentLength];
		request.getInputStream().read(recvByte);
		String recvStr = new String(recvByte);
		
		OutputStream os = response.getOutputStream();
		String serverName = null;

		if(recvStr.equals("getServerName")){
			serverName = InetAddress.getLocalHost().getHostName();
		}else{
			serverName = "";
		}

		byte[] output_data = serverName.getBytes("EUC-KR");
		os.write(output_data);
		os.flush();
	}
}
