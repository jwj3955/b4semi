package com.b4.service;

import static common.JDBCTemplate.close;
import static common.JDBCTemplate.commit;
import static common.JDBCTemplate.getConnection;
import static common.JDBCTemplate.rollback;

import java.sql.Connection;

import com.b4.dao.MemberDao;
import com.b4.model.vo.Member;

public class MemberService {
	
	MemberDao dao = new MemberDao();

	//id로 해당 아이디가 존재하는지 여부만 체크
	public boolean checkId(String memberId)
	{
		Connection conn = getConnection();
		boolean result = dao.checkId(conn, memberId);
		close(conn);
		return result;
	}
	
	//id로 회원 찾기 메소드
	public Member selectOne(Member m)
	{
		Connection conn = getConnection();
		Member result = dao.selectOne(conn, m);
		close(conn);
		return result;
	}
	
	//마이페이지 - 회원정보수정
	public int updateMember(Member m)
	{
		Connection conn = getConnection();
		int result = dao.updateMember(conn, m);
		if(result>0) commit(conn);
		else rollback(conn);
		return result;
	}
	
	public int insertOne(Member m)
	{
		Connection conn = getConnection();
		int result = dao.insertOne(conn, m);
		if(result > 0) commit(conn);
		else rollback(conn);
		return result;
	}
	
	//마이페이지 - 회원탈퇴 : 프론트에서 memberQuitDate에 값이 있으면 탈퇴한 회원으로 처리
	public int quitMember(Member m)
	{
		Connection conn = getConnection();
		int result = dao.quitMember(conn, m);
		if(result>0) commit(conn);
		else rollback(conn);
		return result;
	}

}
