package com.b4.dao;

import static common.JDBCTemplate.close;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.b4.model.vo.Review;

public class ReviewDao {
	
	private Properties prop = new Properties();
	
	public ReviewDao() {
		try {
			String fileName = MemberDao.class.getResource("/sql/review/review-query.properties").getPath();
			prop.load(new FileReader(fileName));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	public int selectCountByDP(Connection conn, Review r)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = 0;
		String sql = prop.getProperty("selectCountByDP");
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, r.getDisplayListSeq());
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				result = rs.getInt("cnt");
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			close(rs);
			close(pstmt);
		}
		return result;
	}
	
	public List<Review> selectAllByDP(Connection conn, Review r, int cPage)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Review> list = new ArrayList<>();
		String sql = prop.getProperty("selectAllByDP");
		//numPerPage의 값 고정
		int numPerPage = 15;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, r.getMemberSeq());
			pstmt.setInt(2, (cPage-1)*numPerPage+1);
			pstmt.setInt(3, cPage*numPerPage);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				Review review = new Review();
				review.setReviewSeq(rs.getInt("reviewSeq"));
				review.setMemberSeq(rs.getInt("memberSeq"));
				review.setReviewTitle(rs.getString("reviewTitle"));
				review.setReviewContents(rs.getString("reviewContents"));
				review.setReviewDate(rs.getTimestamp("reviewDate"));
				review.setReviewDeleteDate(rs.getTimestamp("reviewDeleteDate"));
				review.setReviewScore(rs.getInt("reviewScore"));
				review.setProductCode(rs.getString("productCode"));
				review.setDisplayListSeq(rs.getInt("displayListSeq"));
				list.add(review);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public List<Review> selectAllByMember(Connection conn, Review r, int cPage)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Review> list = new ArrayList<>();
		String sql = prop.getProperty("selectAllByMember");
		//numPerPage의 값 고정
		int numPerPage = 15;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, r.getDisplayListSeq());
			pstmt.setInt(2, (cPage-1)*numPerPage+1);
			pstmt.setInt(3, cPage*numPerPage);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				Review review = new Review();
				review.setReviewSeq(rs.getInt("reviewSeq"));
				review.setMemberSeq(rs.getInt("memberSeq"));
				review.setReviewTitle(rs.getString("reviewTitle"));
				review.setReviewContents(rs.getString("reviewContents"));
				review.setReviewDate(rs.getTimestamp("reviewDate"));
				review.setReviewDeleteDate(rs.getTimestamp("reviewDeleteDate"));
				review.setReviewScore(rs.getInt("reviewScore"));
				review.setProductCode(rs.getString("productCode"));
				review.setDisplayListSeq(rs.getInt("displayListSeq"));
				list.add(review);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public int insertReview(Connection conn, Review r)
	{
		PreparedStatement pstmt = null;
		String sql = prop.getProperty("insertReview");
		int result = 0;
		try
		{
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, r.getMemberSeq());
			pstmt.setString(2, r.getReviewTitle());
			pstmt.setString(3, r.getReviewContents());
			pstmt.setTimestamp(4, r.getReviewDate());
			pstmt.setInt(5, r.getReviewScore());
			pstmt.setString(6, r.getProductCode());
			pstmt.setInt(7, r.getDisplayListSeq());
			result = pstmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			close(pstmt);
		}
		return result;
	}
	
	public int updateReview(Connection conn, Review r)
	{
		PreparedStatement pstmt = null;
		String sql = prop.getProperty("updateReview");
		int result = 0;
		try
		{
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, r.getReviewTitle());
			pstmt.setString(2, r.getReviewContents());
			pstmt.setTimestamp(3, r.getReviewDate());
			pstmt.setInt(4, r.getReviewScore());
			pstmt.setInt(5, r.getReviewSeq());
			result = pstmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			close(pstmt);
		}
		return result;
	}
	
	public int deleteReview(Connection conn, Review r)
	{
		PreparedStatement pstmt = null;
		String sql = prop.getProperty("deleteReview");
		int result = 0;
		try
		{
			pstmt = conn.prepareStatement(sql);
			pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			pstmt.setInt(2, r.getReviewSeq());
			result = pstmt.executeUpdate();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			close(pstmt);
		}
		return result;
	}

}
