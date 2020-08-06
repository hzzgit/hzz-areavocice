package net.fxft.ascsareavoice.ltmonitor.service.impl;

import net.fxft.ascsareavoice.ltmonitor.vo.QueryResult;
import net.fxft.ascsareavoice.ltmonitor.service.IBaseService;
import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.common.jdbc.dao.DefaultDao;
import net.fxft.web.component.table.TableResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//@Transactional(propagation = Propagation.REQUIRED)
public abstract class BaseService<K extends Serializable,T extends Serializable> implements IBaseService<K,T> {

	@Autowired
	protected JdbcUtil jdbc;
	protected Class<T> clatt;

	public BaseService(Class<T> clatt) {
		this.clatt = clatt;
	}

	@Override
	public void save(T t) {
		jdbc.defaultDao(clatt).insert(t);
	}

	@Override
	public void saveOrUpdateAll(Collection<T> ct) {
		DefaultDao<T> dao = jdbc.defaultDao(clatt);
		for (T t : ct) {
			dao.saveOrUpdate(t);
		}
	}

	@Override
	public T load(K id) {
		return jdbc.defaultDao(clatt).findById(id);
	}

	@Override
	public T update(T t) {
		jdbc.defaultDao(clatt).update(t);
		return t;
	}

	@Override
	public T saveOrUpdate(T t) {
		jdbc.defaultDao(clatt).saveOrUpdate(t);
		return t;
	}

	@Override
	public void delete(T t) {
		jdbc.defaultDao(clatt).delete(t);
	}

	@Override
	public void deleteAll(Collection<T> ct) {
		jdbc.defaultDao(clatt).deleteAll(ct);
	}

	@Override
	public boolean deleteById(K id) {
		int k = jdbc.defaultDao(clatt).deleteById(id);
		return true;
	}

	@Override
	public Collection<T> loadAll() {
		return jdbc.select(clatt).query();
	}

	@Override
	public QueryResult<T> load(int page, int rows) {
		QueryResult<T> result = new QueryResult<>();
		int startIndex = (page - 1) * rows;
		TableResult<T> tableResult = jdbc.select(clatt)
				.limit(startIndex, rows)
				.queryTableResult();
		result.setTotalCount(tableResult.getTotalCount());
		List<T> data = new ArrayList<>();
		data.addAll(tableResult.getRows());
		result.setDatas(data);
		return result;
	}

	@Override
	public long getTotalCount() {
		return jdbc.select(clatt).queryCount();
	}

	@Override
	public boolean deleteFakeById(K id) {
		jdbc.update(clatt)
				.updateSetDeleted()
				.whereIdEQ(id)
				.execute();
		return true;
	}

	@Override
	public void deleteFake(T t) {
		jdbc.update(t)
				.updateSetDeleted()
				.whereIdRefValueEQ()
				.execute();
	}

	@Override
	public T find(String hsql, Object[] values) {
		return jdbc.sql(hsql).addIndexParam(values).queryFirst(clatt);
	}

	@Override
	public T find(String hsql, Object value) {
		return jdbc.sql(hsql).addIndexParam(value).queryFirst(clatt);
	}

	@Override
	public T find(String hsql) {
		return jdbc.sql(hsql).queryFirst(clatt);
	}


	@Override
	public List<T> query(final String hql) {
		return jdbc.sql(hql).query(clatt);
	}

	@Override
	public List<T> query(final String hql, final Object value) {
		return jdbc.sql(hql).addIndexParam(value).query(clatt);
	}

	@Override
	public List<T> query(final String hql, final Object[] values) {
		return jdbc.sql(hql).addIndexParam(values).query(clatt);
	}

	public void bulkUpdate(String hql, Object[] values){
		jdbc.sql(hql).addIndexParam(values).executeUpdate();
	}
}
