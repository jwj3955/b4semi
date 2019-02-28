package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.TimeZone;

// 시간 경과에 따른 회원 등급 조정, 쿠폰 만료, 유통기한 만료 처리를 위한 클래스 
public class TimeSystem extends Thread {
	
	
	private static final int OFFSET = TimeZone.getDefault().getRawOffset(); // 실행국가에 따른 시간 차이를 밀리초단위로 환산한 값 
	private static boolean isInitialized = false;
	private long day=0; //날짜단위의 변화를 감지하기 위한 값
	
	private TimeSystem() { // 외부 생성 불가능
		setDaemon(true);
	} 
	
	public synchronized static void initialize() {
		if(!isInitialized)
		{
			isInitialized = true;
			new TimeSystem().start();
			
		}
	}
	
	private Socket socket = null;
	
	//시간 경과에 따른 쓰레드 로직
	public void run()
	{
		// socket tcp connection
		String ip = "127.0.0.1";
		int port = 9080;
		int number = 123;
		try {
		socketConnect(ip, port);
		
	    	while(true){
	    		
	    		// writes and receives the message
	    		String message = "message"+number;
	    		number++;
	    		
	    		System.out.println("Sending: " + message);
	    		String returnStr = echo(message);
	    		System.out.println("Receiving: " + returnStr);
	    		
	    		Thread.sleep(1000);
	    	}
	    	
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		
		try {
			for(;;)
			{
				if(dayChangeCheck())
				{
					System.out.println("타임시스템 - 시스템 날짜 변경 및 데이터 갱신");
					// 데이터 갱신 로직
				}
				sleep(60000);
			}
		}catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	 // make the connection with the socket
 	private void socketConnect(String ip, int port) throws UnknownHostException, IOException {
 		System.out.println("[Connecting to socket...]");
 		this.socket = new Socket(ip, port);
 		
 	}
 	
 	// writes and receives the full message int the socket (String)
 	public String echo(String message) {
 		try {
 			// out & in 
 			PrintWriter out = new PrintWriter(getSocket().getOutputStream(), true);
 			BufferedReader in = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
 			
 			// writes str in the socket and read
 			out.println(message);
 			String returnStr = in .readLine();
 			return returnStr;
 			} catch (IOException e) {
 				e.printStackTrace();
 				}
 		
 		return null;
 		}
 	
 	// get the socket instance
 	
 	private Socket getSocket() {
 		return socket;
 		}




	
	//날짜가 바뀌면 true 리턴
	public boolean dayChangeCheck () {
		if(day != day(System.currentTimeMillis()))
		{
			day = day(System.currentTimeMillis());
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static long day(long date) // UMT 밀리초 시간을 한국시간으로 환산 후 날짜 단위값으로 리턴하는 메소드
	{
		return (date + OFFSET)/86400000L;
	}
	
	public static long dayInv(long day) //위 메소드의 역함수 - 이때 시간 단위는 버려진다
	{
		return (day*86400000L) - OFFSET;
	}
	

}
