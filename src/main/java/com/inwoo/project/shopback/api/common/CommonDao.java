package com.inwoo.project.shopback.api.common;

import java.util.List;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository("commonDao")
public class CommonDao {
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	public CommonDao() {
	}

	public <E> List<E> selectList(String queryId, Object parameterObject) {
		return this.sqlSessionTemplate.selectList(queryId, parameterObject);
	}

	public <E> List<E> selectList(String queryId) {
		return this.sqlSessionTemplate.selectList(queryId);
	}

	public <T> T select(String queryId, Object parameterObject) {
		return this.sqlSessionTemplate.selectOne(queryId, parameterObject);
	}

	public <T> T select(String queryId) {
		return this.sqlSessionTemplate.selectOne(queryId);
	}

	public int insert(String queryId, Object parameterObject) {
		return this.sqlSessionTemplate.insert(queryId, parameterObject);
	}

	public int update(String queryId, Object parameterObject) {
		return this.sqlSessionTemplate.update(queryId, parameterObject);
	}

	public int delete(String queryId, Object parameterObject) {
		return this.sqlSessionTemplate.delete(queryId, parameterObject);
	}
}
