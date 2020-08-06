package net.fxft.ascsareavoice.ltmonitor.service;

import net.fxft.ascsareavoice.ltmonitor.vo.QueryResult;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


public interface IBaseService <K extends Serializable,T extends Serializable>  {

    /**
     * 将实体对象保存到数据库中
     * @param t 待保存的实体对象
     * @return 实体对象的ID
     */
    void save(T t);
    /**
     * 将实体对象【集合】保存到数据库中
     * @param ct 实体对象【集合】
     */
    void saveOrUpdateAll(Collection<T> ct);
    /**
     * 根据Id查询实体对象
     * @param id 表记录中的对应的id字段
     * @return 对应的实体对象
     */
    T load(K id);
    /**
     * 更新一条记录
     * @param t 待更新记录对应的实体对象
     * @return 更新后的实体对象
     */
    T update(T t);
    /**
     * 保存或更新一个实体对象到表记录中
     * @param t 待更新的实体对象
     * @return 更新后的实体对象
     */
    T saveOrUpdate(T t);
    /**
     * 删除一个实体对象对应的表记录
     * @param t 待删除的实体对象
     */
    void delete(T t);
    /**
     * 删除一组记录
     * @param ct 待删除记录集合
     */
    void deleteAll(Collection<T> ct);
    /**
     * 根据id删除一条记录
     * @param id 待删除记录id
     * @return 是否删除成功（id是否有效）
     */
    boolean deleteById(K id);
    /**
     * 加载所有记录集合
     * @return 所有记录集合
     */
    Collection<T> loadAll();
    /**
     * 分页加载记录集合
     * @param page 当前第多少页
     * @param rows 每页最多多少行数据
     * @return 第page页的数据集合
     */
    QueryResult<T> load(int page, int rows);
    /**
     * 获取总记录数
     * @return 总数
     */
    long getTotalCount();
    /******************************HQL******************************/
    /**
     * 根据实体类主键ID进行假删除
     * @param id
     * @return
     */
    boolean deleteFakeById(K id);
    /**
     * 假删除实体类对象
     * @param t
     */
    void deleteFake(T t);

    T find(String hsql, Object[] values) ;

    T find(String hsql, Object value) ;

    T find(String hsql) ;

    List<T> query(final String hql);

    List<T> query(final String hql, final Object value);

    List<T> query(final String hql, final Object[] values);

    void bulkUpdate(String hql, Object[] values);
}
