package dataGen;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import common.JDBCTemplate;

//더미 데이터 자동 생성을 위한 클래스 - DB 데이터 삭제 후 이용해주세요!
public class DataGen {
	
	private final int memberCount = 100; //생성 회원수
	private final int noticeCount = 100; //공지사항 게시수
	private final int qnaCount = 500; //일대일 문의 게시수
	private final long orderInterval = 31536000000L/12; //회원당 평균 주문 간격
	
	private final long howOld = 31536000000L; //마켓 오픈시점과 현재의 밀리초 차이
	private final double priceInc = 0.2; //마켓 오픈시점부터 현시점까지의 물가상승률
	
	public static void main(String[] args) {
		new DataGen();
	}
	
	private DataGen () {
		int memberSeqStart = 1;
		int dpListSeqStart = 1;
	 
		Connection cn = JDBCTemplate.getConnection();
		
		memberSeqStart = getMemberSeq(cn);
		
		//기존 데이터가 존재할 경우 초기화 로직
		if(getProductCount(cn)>0)
		{
			System.out.println("상품 데이터 초기화중 - 에러 발생시 오라클에서 계정 삭제 후 다시 생성하여 초기화를 해주세요");
			delete(cn);
			System.out.println("상품 데이터 초기화 완료 - 재시작 필요");
			JDBCTemplate.commit(cn);
			JDBCTemplate.close(cn);
			return;
		}
		
		System.out.println("회원 생성중");
		createMember(memberSeqStart, cn); //매개변수 : 생성 인원수, 시퀀스 시작 번호, 커넥션
		System.out.println("회원 생성 완료");
		System.out.println("배송지 생성중");
		createAddressList(memberSeqStart, cn);
		System.out.println("배송지 생성 완료");
		System.out.println("공급사 생성중");
		createSupplier(cn);
		System.out.println("공급사 생성 완료");
		System.out.println("상품마스터 생성중");
		ArrayList<ProductPrice> plist = ProductGen.createProduct(cn);
		System.out.println("상품마스터 생성 완료");
		dpListSeqStart = getDPListSeq(cn);
		System.out.println("판매상품리스트, 이미지DB 생성중");
		createDPList(plist, dpListSeqStart, cn);
		System.out.println("판매상품리스트, 이미지DB 생성 완료");
		System.out.println("판매세부옵션 생성중");
		createDPOption(plist, dpListSeqStart, cn);
		System.out.println("판매세부옵션 생성완료");
		System.out.println("장바구니 생성중");
		createBasket(plist, dpListSeqStart, memberSeqStart, cn);
		System.out.println("장바구니 생성완료");
		System.out.println("공지사항 생성중");
		createNotice(plist, memberSeqStart, cn);
		System.out.println("공지사항 생성 완료");
		System.out.println("일대일문의, 답변 생성중");
		createQNA(plist, memberSeqStart, cn);
		System.out.println("일대일문의, 답변 생성완료");
		System.out.println("입고, 주문결제내역, 주문상품내역, 취소환불, 상품리뷰, 마일리지변경log, 회원변경log 생성중일까??");
		createOrder(plist, dpListSeqStart, memberSeqStart, cn);
		System.out.println("입고, 주문결제내역, 주문상품내역, 취소환불, 상품리뷰, 마일리지변경log, 회원변경log 생성했을까??");
		System.out.println("쿠폰마스터, 발행된 쿠폰 생성 아직 불가");
		
		JDBCTemplate.commit(cn);
		JDBCTemplate.close(cn);
	}
	

	
	public static int[] chooseIndex(int indexSize, int chooseCount)
	{
		if(indexSize<chooseCount)
		{
			return null;
		}
		
		int[] nums = new int[chooseCount];
		for(int i=0; i<chooseCount;i++)
		{
			nums[i] = (int)(Math.random()*indexSize);
			for(int j=0; j<i;j++)
			{
				if(nums[i]==nums[j])
				{
					i--;
					break;
				}
			}
		}
		return nums;
		
	}
	

	
	public int getMemberSeq(Connection cn)
	{
		PreparedStatement ps =null;
		ResultSet rs = null;
		int result = 1;
		try {
			ps=cn.prepareStatement("SELECT MEMBER_SEQ.NEXTVAL FROM DUAL");
			rs=ps.executeQuery();
			if(rs.next())
			{
				result = rs.getInt(1);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				rs.close();
				ps.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		return result+1;
	}
	
	
	public int getQnaSeq(Connection cn)
	{
		PreparedStatement ps =null;
		ResultSet rs = null;
		int result = 1;
		try {
			ps=cn.prepareStatement("SELECT QUERY_SEQ.NEXTVAL FROM DUAL");
			rs=ps.executeQuery();
			if(rs.next())
			{
				result = rs.getInt(1);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				rs.close();
				ps.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		return result+1;
	}
	
	public int getOrderSeq(Connection cn)
	{
		PreparedStatement ps =null;
		ResultSet rs = null;
		int result = 1;
		try {
			ps=cn.prepareStatement("SELECT ORDER_SEQ.NEXTVAL FROM DUAL");
			rs=ps.executeQuery();
			if(rs.next())
			{
				result = rs.getInt(1);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				rs.close();
				ps.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		return result+1;
	}
	
	
	public void createMember(int memberSeqStart, Connection cn)
	{
		
		String[] firstNames = {"k강","k권","k김","n나","m문","p박","s손","s신","w왕","y유","y윤","l이","i임","j정","h한"};
		String[] lastNames = {"bs병승","yw영우","wj우진","js지섭","sh성희","ig일교","jh장현","jm재민","mr미리","jw진우","sh성화",
				"hj혜진","ys윤석","ma민아","dh동현","sg순규","cw찬웅","ja지안","ss성식","jb정복","dw도원","sl솔","jh준혁"};
		PreparedStatement ps =null;
		try {
			

		ps=cn.prepareStatement("INSERT INTO MEMBER VALUES (MEMBER_SEQ.NEXTVAL,?,'NEW',?,?,?,?,null,?,null,0,'N')");
		
			//관리자 추가 : id = admin, pw = 1234
			ps.setString(1, "admin");
			ps.setString(2, "1ARVn2Auq2/WAqx2gNrL+q3RNjAzXpUfCXrzkA6d4Xa22yhRLy4AC50E+6UTPoscbo31nbOoq51gvkuXzJ6B2w==");
			ps.setString(3, "관리자");
			ps.setString(4, "admin@admin.co.kr");
			ps.setString(5, "01012345678");
			ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()-howOld));
			ps.executeUpdate();
		
			for(int i=memberSeqStart+1; i<memberSeqStart+memberCount;i++)
			{
				String str1 = firstNames[(int)(Math.random()*firstNames.length)];
				String str2 = lastNames[(int)(Math.random()*lastNames.length)];
				String str3 = String.valueOf((int)(Math.random()*10))+String.valueOf(i);
				if(Math.random()>0.5 && str2.length()>3)
				{
					str2=str2.substring(1, 2) + str2.substring(0, 1)+  str2.substring(3) + str2.substring(2,3);
				}
				//id설정
				ps.setString(1,str1.substring(0,1)+str2.substring(0,2)+str3);
		
				//비밀번호 설정 - 아이디 뒤에 붙은 숫자를 비밀번호로 설정
				MessageDigest md = null;
					try
					{
						md = MessageDigest.getInstance("SHA-512");
					}
					catch(NoSuchAlgorithmException e)
					{
						e.printStackTrace();
					}
				byte[] bytes = str3.getBytes(Charset.forName("UTF-8"));
				md.update(bytes);
				ps.setString(2, Base64.getEncoder().encodeToString(md.digest()));
				
				//이름설정
				ps.setString(3,str1.substring(1)+str2.substring(2));
				
				//이메일설정
				ps.setString(4, str1.substring(0,1)+str2.substring(0,2)+str3+(Math.random()<0.7?"@naver.com":(Math.random()<0.7?"@google.com":"@daum.net")));
				
				//폰번호설정
				ps.setString(5, "010"+String.format("%08d",(int)(Math.random()*100000000)));
				
				//가입일설정
				ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()-(long)(Math.random()*howOld)));
				
				ps.executeUpdate();
			}
		
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				ps.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void createAddressList(int memberSeqStart, Connection cn)
	{
		String[] address = AddressData.getAddress();
		int addressSize = address.length;
		
		String sql = "INSERT INTO ADDRESSLIST VALUES (ADDRESS_SEQ.NEXTVAL,?,?,?,?)";
		PreparedStatement ps =null;
		try {
			
		ps=cn.prepareStatement(sql);
		
			for(int i=memberSeqStart; i<memberSeqStart+memberCount;i++)
			{
				int addressCount = 3;
				
				for(int j=1; j<=addressCount; j++)
				{
					//회원 번호 참조값
					ps.setInt(1,i);
					
					//태그명
					ps.setString(2, "주소"+j);
					
					//주소설정
					ps.setString(3, address[(int)(Math.random()*addressSize)]);
					
					//폰번호설정
					ps.setString(4, "010"+String.format("%08d",(int)(Math.random()*100000000)));
					
					ps.executeUpdate();
					
				}
			}
		
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				ps.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public void createNotice(ArrayList<ProductPrice> plist, int memberSeqStart, Connection cn)
	{
		
		String sql = "INSERT INTO NOTICE VALUES (NOTICE_SEQ.NEXTVAL,?,?,?,?,null,0)";
		PreparedStatement ps =null;
		try {
			
		ps=cn.prepareStatement(sql);
		long time;
		
			for(int i=0; i<noticeCount;i++)
			{
				time = System.currentTimeMillis()-howOld+(long)((Math.random()+i)*(howOld/noticeCount));
				
				
					//작성자 회원 번호
					ps.setInt(1,memberSeqStart);
					
					if(Math.random()>0.5)
					{
						String date = new SimpleDateFormat("d일 HH시").format(new Date(time));
						//공지사항 제목
						ps.setString(2, date + " 서버 점검 안내");
						//공지사항 내용
						ps.setString(3, "<img class='NoticeImages' src='...imgpath...no.jpg'><br>안녕하세요. The Food Forum 입니다.<br><br><br>보다 나은 서비스 제공을 위한 시스템 작업으로 서비스가 중단될 예정입니다.<br><br><br>최대한 빠른 시간 내에 작업을 마칠 수 있도록 최선을 다하겠습니다.<br><br><br>감사합니다.");
					}
					else
					{
						if(Math.random()>0.5)
						{
							//공지사항 제목
							ps.setString(2, plist.get((int)(plist.size()*Math.random())).getProductName() + " 상품 일시 품절 안내");
							//공지사항 내용
							ps.setString(3, "<img class='NoticeImages' src='...imgpath...no.jpg'> <br>안녕하세요. The Food Forum 입니다.<br><br>최대한 빠른 시일 내에 상품 판매를 재개할 수 있도록 하겠습니다.<br><br><br>감사합니다.");
						}
						else
						{
							String date = new SimpleDateFormat("M월 d일").format(new Date(time));
							ProductPrice p = plist.get((int)(plist.size()*Math.random()));
							//공지사항 제목
							ps.setString(2, date + " " +p.getProductName() + " 할인 이벤트!");
							//공지사항 내용
							ps.setString(3, "<img class='NoticeImages' src='...imgpath...no.jpg'> <br>안녕하세요. The Food Forum 입니다.<br><br>오늘 단 하루! "+p.getProductName()+"를 "+(p.getOutPrice()-10)+"원에 구매하실 수 있습니다.<br><br><br>감사합니다.");
						}
					}

					
					//작성일시
					ps.setTimestamp(4, new Timestamp(time));
					
					ps.executeUpdate();
			}
		
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				ps.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public void createQNA(ArrayList<ProductPrice> plist, int memberSeqStart, Connection cn)
	{
		long time;
		int qnaWriter;
		int replyWriter;
		int qnaSeqStart = getQnaSeq(cn);
		
		String sql1 = "INSERT INTO QUERYBOARD VALUES (QUERY_SEQ.NEXTVAL,?,?,?,?,null)";
		PreparedStatement ps1 =null;


		for(int i=0; i<qnaCount;i++)
		{
			time = System.currentTimeMillis()-howOld+(long)((Math.random()+i)*(howOld/qnaCount));
			qnaWriter = memberSeqStart+1 + (int)(Math.random()*(memberCount-1));
			

			try {

				ps1=cn.prepareStatement(sql1);

				//작성자 회원 번호
				ps1.setInt(1,qnaWriter);
				
				//게시글 제목
				if(Math.random()>0.5)
				{
					String date = new SimpleDateFormat("d일 H시").format(new Date(time+(long)(Math.random()*(howOld/qnaCount))));
					//게시글 제목
					ps1.setString(2, date + "에 배송 가능할까요?");
					//게시글 내용
					ps1.setString(3, "<img class='QNAImages' src='...imgpath...no.jpg'><br>꼭 부탁합니다");
				}
				else
				{
					if(Math.random()>0.5)
					{
						//게시글 제목
						ps1.setString(2, plist.get((int)(plist.size()*Math.random())).getProductName() + " 상품에 대해 문의 드립니다");
						//게시글 내용
						ps1.setString(3, "<img class='NoticeImages' src='...imgpath...no.jpg'><br>깨끗하고 좋은 물건으로 보내주실 수 있으신가요");
					}
					else
					{
						String date = new SimpleDateFormat("M월 d일").format(new Date(time+(long)(Math.random()*(howOld/qnaCount))));
						ProductPrice p = plist.get((int)(plist.size()*Math.random()));
						//게시글 제목
						ps1.setString(2, date + "에 구매한 " +p.getProductName() + " 환불에 대해 문의 드립니다");
						//게시글 내용
						ps1.setString(3, "<img class='NoticeImages' src='...imgpath...no.jpg'><br>받은 물건이 마음에 안 드는데 환불 가능한가요");
					}
				}

				//작성일시
				ps1.setTimestamp(4, new Timestamp(time));
				ps1.executeUpdate();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try{
					ps1.close();
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
			}
			
			
			if(Math.random()<0.05)
			{
				// 관리자 답변 없는 게시글
				continue;
			}
			
			String sql2 = "INSERT INTO REPLY VALUES (COMMENT_SEQ.NEXTVAL,?,?,null,?,?,1,null)";
			PreparedStatement ps2=null;
			
			replyWriter=memberSeqStart;
			
			try {
				ps2=cn.prepareStatement(sql2);
				
				for(;;)
				{
					time = time+(long)(Math.random()*Math.random()*(howOld/qnaCount));

					//게시글 번호
					ps2.setInt(1, qnaSeqStart);
					
					//작성자 회원 번호
					ps2.setInt(2,replyWriter);
					
					//작성일시
					ps2.setTimestamp(3, new Timestamp(time));

					//댓글 내용
					if(replyWriter==memberSeqStart)
					{
						String[] replyText = {"안녕하세요. The Food Forum 입니다.<br>좋은 하루 되세요.","안녕하세요. The Food Forum 입니다.<br>이용에 불편을 드려 죄송합니다.",
								"안녕하세요. The Food Forum 입니다.<br>문의하신 내용 잘 확인하였습니다.","안녕하세요. The Food Forum 입니다.<br>문의해주신 대로 처리할 수 있도록 노력하겠습니다.",
								"안녕하세요. The Food Forum 입니다.<br>해당 상품에 많은 관심 갖아주셔서 감사합니다."};
						ps2.setString(4, replyText[(int)(Math.random()*replyText.length)]);
					}
					else
					{
						String[] replyText = {"이런","헐","알겠습니다","...","뭔말인지 잘 모르겠네요"};
						ps2.setString(4, replyText[(int)(Math.random()*replyText.length)]);
					}

		
					ps2.executeUpdate();


					if(Math.random()>0.5)
					{
						if(Math.random()>0.75)
						{
							replyWriter=memberSeqStart;
						}
						else
						{
							replyWriter=qnaWriter;
						}
					}
					else
					{
						break;
					}
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try{
					ps2.close();
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
			}
		}

	}

	public void createSupplier(Connection cn) {
		String[] address = AddressData.getAddress();
		int addressSize = address.length;
		
		String sql = "INSERT INTO SUPPLIER VALUES (?,?,?,?,?)";
		PreparedStatement ps =null;
		try {
			
		ps=cn.prepareStatement(sql);
		
		//공급사코드, 공급사이름, 공급사폰, 공급사주소, 공급사이메일
		ps.setString(1,"b4farm");
		ps.setString(2,"비포농산");
		ps.setString(3,"010"+String.format("%08d",(int)(Math.random()*100000000)));
		ps.setString(4,address[(int)(Math.random()*addressSize)]);
		ps.setString(5,"b4farm@naver.com");
		ps.executeUpdate();
		
		ps.setString(1,"ajaxFish");
		ps.setString(2,"에이작스수산");
		ps.setString(3,"010"+String.format("%08d",(int)(Math.random()*100000000)));
		ps.setString(4,address[(int)(Math.random()*addressSize)]);
		ps.setString(5,"ajaxFish@naver.com");
		ps.executeUpdate();
		
		ps.setString(1,"rainbowMeat");
		ps.setString(2,"무지개축산");
		ps.setString(3,"010"+String.format("%08d",(int)(Math.random()*100000000)));
		ps.setString(4,address[(int)(Math.random()*addressSize)]);
		ps.setString(5,"rainbowMeat@naver.com");
		ps.executeUpdate();
		
		ps.setString(1,"haerim");
		ps.setString(2,"해림");
		ps.setString(3,"010"+String.format("%08d",(int)(Math.random()*100000000)));
		ps.setString(4,address[(int)(Math.random()*addressSize)]);
		ps.setString(5,"haerim@naver.com");
		ps.executeUpdate();
		
		ps.setString(1,"dalmont");
		ps.setString(2,"달몬트");
		ps.setString(3,"010"+String.format("%08d",(int)(Math.random()*100000000)));
		ps.setString(4,address[(int)(Math.random()*addressSize)]);
		ps.setString(5,"dalmont@naver.com");
		ps.executeUpdate();
		
		ps.setString(1,"maedalMilk");
		ps.setString(2,"매달유업");
		ps.setString(3,"010"+String.format("%08d",(int)(Math.random()*100000000)));
		ps.setString(4,address[(int)(Math.random()*addressSize)]);
		ps.setString(5,"maedalMilk@naver.com");
		ps.executeUpdate();
		
		ps.setString(1,"darkSnow");
		ps.setString(2,"흑설");
		ps.setString(3,"010"+String.format("%08d",(int)(Math.random()*100000000)));
		ps.setString(4,address[(int)(Math.random()*addressSize)]);
		ps.setString(5,"darkSnow@naver.com");
		ps.executeUpdate();
		
		ps.setString(1,"maeddugi");
		ps.setString(2,"메뚜기");
		ps.setString(3,"010"+String.format("%08d",(int)(Math.random()*100000000)));
		ps.setString(4,address[(int)(Math.random()*addressSize)]);
		ps.setString(5,"maeddugi@naver.com");
		ps.executeUpdate();
		
		ps.setString(1,"sandeulchan");
		ps.setString(2,"산들반찬");
		ps.setString(3,"010"+String.format("%08d",(int)(Math.random()*100000000)));
		ps.setString(4,address[(int)(Math.random()*addressSize)]);
		ps.setString(5,"sandeulchan@naver.com");
		ps.executeUpdate();
		
		ps.setString(1,"bonovo");
		ps.setString(2,"보노보유통");
		ps.setString(3,"010"+String.format("%08d",(int)(Math.random()*100000000)));
		ps.setString(4,address[(int)(Math.random()*addressSize)]);
		ps.setString(5,"bonovo@naver.com");
		ps.executeUpdate();
		
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				ps.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public void delete(Connection cn)
	{
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		
		try {
			ps1=cn.prepareStatement("DELETE FROM CART CASCADE");
			ps1.executeUpdate();
			ps2=cn.prepareStatement("DELETE FROM ORDERPDETAIL CASCADE");
			ps2.executeUpdate();
			
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				ps1.close();
				ps2.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		
	
		try {
			ps1=cn.prepareStatement("DELETE FROM DPOPTION CASCADE");
			ps1.executeUpdate();
			ps2=cn.prepareStatement("DELETE FROM ORDERLIST CASCADE");
			ps2.executeUpdate();

		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				ps1.close();
				ps2.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		try {
			ps1=cn.prepareStatement("DELETE FROM PRODUCT CASCADE");
			ps1.executeUpdate();
			ps2=cn.prepareStatement("DELETE FROM IMAGES CASCADE");
			ps2.executeUpdate();
			ps3=cn.prepareStatement("DELETE FROM DPLIST CASCADE");
			ps3.executeUpdate();

		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				ps1.close();
				ps2.close();
				ps3.close();

			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		try {
			ps=cn.prepareStatement("DELETE FROM SUPPLIER CASCADE");
			ps.executeUpdate();

		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				ps.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		

	}
	
	public int getProductCount(Connection cn)
	{
		String sql = "SELECT COUNT(*) FROM PRODUCT";
		PreparedStatement ps = null;
		ResultSet rs = null;
		int result =0;
		try {
			
			ps=cn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next())
			{
				result = rs.getInt(1);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				rs.close();
				ps.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public int getDPListSeq(Connection cn)
	{
		PreparedStatement ps =null;
		ResultSet rs = null;
		int result = 1;
		try {
			ps=cn.prepareStatement("SELECT DISPLAY_LIST_SEQ.NEXTVAL FROM DUAL");
			rs=ps.executeQuery();
			if(rs.next())
			{
				result = rs.getInt(1);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				rs.close();
				ps.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public void createDPList(ArrayList<ProductPrice> plist, int dpListSeqStart, Connection cn)
	{
		PreparedStatement ps1 =null;
		PreparedStatement ps2 =null;
		
		String sql1 = "INSERT INTO DPLIST VALUES (DISPLAY_LIST_SEQ.NEXTVAL,?,?,'Y',?)";
		String sql2 = "INSERT INTO IMAGES VALUES (?,?,'BL',?)";
		try {
		String[] prefixs = {"","신선한 ","맛있는 ","깨끗한 ","고급 ","실속있는 ","오리지널 ","친환경 ","부드러운 ","찰진 ","푸짐한 ", "향긋한 "};
		String prefix = prefixs[(int)(Math.random()*prefixs.length)];
		String title = prefix;
		String html = "";
		ProductPrice p;
		
		int listSize = plist.size();
		
		//판매글 추가
		ps1=cn.prepareStatement(sql1);
		//이미지 추가
		ps2=cn.prepareStatement(sql2);
		
		
			for(int i=0; i<listSize; i++)
			{
				p = plist.get(i);
				
				
				title +=p.getProductName();
				
				
				if("default".equals(p.getUrl()))
				{
					html +="<div class='DPListImages'><p>"+ prefix + p.getProductName() +
					"</p><img src='...imgpath..."+p.getProductName()+".jpg'></div>";

					//저장파일명
					ps2.setString(1, p.getProductName()+".jpg");
					//원본파일명
					ps2.setString(2, p.getProductName()+".jpg");
					//게시번호
					ps2.setInt(3, dpListSeqStart+p.getDpNo());
					ps2.executeUpdate();
				}
				else if(p.getUrl() != null)
				{
					html +="<div class='DPListImages'><p>"+ prefix + p.getProductName() +
					"</p><img src='...imgpath..."+p.getUrl()+"'></div>";
					
					//저장파일명
					ps2.setString(1, p.getUrl());
					//원본파일명
					ps2.setString(2, p.getUrl());
					//게시번호
					ps2.setInt(3, dpListSeqStart+p.getDpNo());
					ps2.executeUpdate();
				}

				
				if(i+1 == listSize || p.getDpNo() != plist.get(i+1).getDpNo())
				{

					//타이틀
					ps1.setString(1, title);
					//내용
					ps1.setString(2, html);
					//작성일
					ps1.setTimestamp(3, new Timestamp(System.currentTimeMillis()-howOld));
					ps1.executeUpdate();
					
					
					prefix = prefixs[(int)(Math.random()*prefixs.length)];
					title = prefix;
					html ="";
				}
				else
				{
					title +=", ";
				}
				
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				ps1.close();
				ps2.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void createDPOption(ArrayList<ProductPrice> plist, int dpListSeqStart, Connection cn)
	{
		PreparedStatement ps3 =null;
		String sql3 = "INSERT INTO DPOPTION VALUES (?,?,null,?,'Y')";
		
		try {
		ProductPrice p;
		
		int listSize = plist.size();
		
		//세부옵션 추가
		ps3=cn.prepareStatement(sql3);
		
			for(int i=0; i<listSize; i++)
			{
				p = plist.get(i);
				
				ps3.setString(1, p.getProductCode());
				ps3.setInt(2, dpListSeqStart+p.getDpNo());
				ps3.setInt(3, (int)(p.getOutPrice()*(0.01*Math.random()+0.1))*10   );
				ps3.executeUpdate();
				
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				ps3.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void createBasket(ArrayList<ProductPrice> plist, int dpListSeqStart, int memberSeqStart, Connection cn)
	{
		PreparedStatement ps = null;
		String sql = "INSERT INTO CART VALUES (?,?,?,?)";
		
		try {
		ProductPrice p;
		
		int listSize = plist.size();
		
		ps=cn.prepareStatement(sql);
		
		int contain;
		int[] pIndex;
		
			for(int i=memberSeqStart; i<memberSeqStart + memberCount; i++)
			{
				contain = 2;
				pIndex = chooseIndex(listSize, contain);
				for(int j=0; j<contain; j++)
				{
					p = plist.get(pIndex[j]);
					ps.setInt(1, i);
					ps.setString(2, p.getProductCode());
					ps.setInt(3, dpListSeqStart+p.getDpNo());
					ps.setInt(4, 1+(int)(Math.random()*20));
					ps.executeUpdate();
				}
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				ps.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public MemberAddress getMemberAddress(int memberNo, Connection cn)
	{
		PreparedStatement ps =null;
		ResultSet rs = null;
		MemberAddress result = null;
		try {
			ps=cn.prepareStatement("SELECT * FROM MEMBER JOIN ADDRESSLIST USING(MEMBERSEQ) WHERE ADDRESSTAG = '주소1' AND MEMBERSEQ = ?");
			ps.setInt(1, memberNo);
			rs=ps.executeQuery();
			if(rs.next())
			{
				result = new MemberAddress();
				result.setMemberSeq(rs.getInt("MEMBERSEQ"));
				result.setMemberId(rs.getString("MEMBERID"));
				result.setMemberName(rs.getString("MEMBERNAME"));
				result.setMemberPhone(rs.getString("MEMBERPHONE"));
				result.setMemberEnrollDate(rs.getTimestamp("MEMBERENROLLDATE"));
				result.setMemberMileage(rs.getInt("MEMBERMILEAGE"));
				result.setAddress(rs.getString("ADDRESS"));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{
				rs.close();
				ps.close();
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public void createOrder(ArrayList<ProductPrice> plist,int dpListSeqStart,int memberSeqStart,Connection cn)
	{
		Order order;
		MemberAddress member;
		int orderSeq = getOrderSeq(cn);
		int orderCount;
		int productKind;
		int[] productIndex;
		double maxKind = Math.sqrt(plist.size());
		long currtime;

		ArrayList<OrderProduct> orderList;
		OrderProduct orderProduct;
		
		
		//회원 반복
		for(int i=memberSeqStart; i < memberSeqStart + memberCount ; i++)
		{
			member = getMemberAddress(i, cn);
			currtime=System.currentTimeMillis();
			orderCount=2+(int)(2*Math.random()*(currtime-member.getMemberEnrollDate().getTime())/orderInterval);
			
			//주문 반복
			for(int j=0; j<orderCount ; j++)
			{
				//DB에 입력하기 전 주문정보 세팅
				order = new Order();
				order.setMemberSeq(member.getMemberSeq());
				order.setOrderTime((long)(Math.random()*(currtime-member.getMemberEnrollDate().getTime())) + member.getMemberEnrollDate().getTime());
				if(j<2)
				{
					productKind = 2;
				}
				else
				{
					productKind = 1+(int)(maxKind*Math.random()*Math.random()); 
				}
				productIndex = chooseIndex(plist.size(), productKind);
				orderList = new ArrayList<OrderProduct> ();
				//상품 반복
				for(int z=0; z<productKind; z++)
				{
					orderProduct = new OrderProduct();
					orderProduct.setpp(plist.get(productIndex[z]));
					orderProduct.setProductCount((int)(Math.random()*Math.random()*Math.random()*Math.random()*50000/orderProduct.getpp().getOutPrice()+Math.pow(Math.random(),4)*4+1));
					if(j==0)
					{
						orderProduct.setCancelCount(orderProduct.getProductCount());
						orderProduct.setReview(false);
						order.addTotalPrice(orderProduct.getProductCount()*orderProduct.getpp().getOutPrice());
						order.addCancelPrice(orderProduct.getProductCount()*orderProduct.getpp().getOutPrice());
					}
					else if(j>=2 && Math.random()>0.95)
					{
						orderProduct.setCancelCount(orderProduct.getProductCount());
						orderProduct.setReview(false);
						order.addTotalPrice(orderProduct.getProductCount()*orderProduct.getpp().getOutPrice());
						order.addCancelPrice(orderProduct.getProductCount()*orderProduct.getpp().getOutPrice());
					}
					else
					{
						orderProduct.setCancelCount(0);
						if(Math.random()>0.5)
						{
							orderProduct.setReview(true);
						}
						else
						{
							orderProduct.setReview(false);
						}
						order.addTotalPrice(orderProduct.getProductCount()*orderProduct.getpp().getOutPrice());
					}
					orderList.add(orderProduct);
					
					
				}
				
				
				//DB 주문 입력 로직
				PreparedStatement ps1 = null;
				String sql1 = "INSERT INTO ORDERLIST VALUES (ORDER_SEQ.NEXTVAL,null,?,'OS05',0,?,?,?,?,?,?,?,?)";
				
				try {
				
					ps1=cn.prepareStatement(sql1);
					ps1.setInt(1, i);
					ps1.setString(2, member.getMemberName());
					ps1.setString(3, member.getAddress());
					ps1.setString(4, member.getMemberPhone());
					ps1.setString(5, Math.random()>0.5?"빠른 배송 부탁드립니다":((int)(1+Math.random()*12)+"시쯤에 배송해주세요"));
					ps1.setString(6, String.format("%12d",(long)(Math.random()*1000000000000L)));
					ps1.setInt(7, order.getTotalPrice());
					ps1.setInt(8, order.getCancelPrice());
					ps1.setTimestamp(9, new Timestamp(order.getOrderTime()));
					ps1.executeUpdate();
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				finally
				{
					try{
						ps1.close();
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}
				}
				
				//DB 주문상세, 입고, 주문취소, 리뷰, 마일리지변경로그 입력 로직
				
				sql1 = "INSERT INTO ORDERPDETAIL VALUES (?,?,?,?)";
				
				try {
					
					ps1=cn.prepareStatement(sql1);
					
					for(int z=0; z<productKind ;z++)
					{
						ps1.setInt(1, orderSeq);
						ps1.setString(2, orderList.get(z).getpp().getProductCode());
						ps1.setInt(3, dpListSeqStart + orderList.get(z).getpp().getDpNo());
						ps1.setInt(4, orderList.get(z).getProductCount());
						ps1.executeUpdate();
					}

				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				finally
				{
					try{
						ps1.close();
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}
				}
				
				orderSeq++;
				
			}
			
			//DB 회원정보 마일리지값 설정 로직

		}
	}
	
}
