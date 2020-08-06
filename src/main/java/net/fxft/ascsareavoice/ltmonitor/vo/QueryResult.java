package net.fxft.ascsareavoice.ltmonitor.vo;

import java.util.List;

public class QueryResult<T> {

	private long totalCount;//总的记录数
	private List<T> datas;//一次查询出来的数据集
	
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public List<T> getDatas() {
		return datas;
	}
	public void setDatas(List<T> datas) {
		this.datas = datas;
	}

}
