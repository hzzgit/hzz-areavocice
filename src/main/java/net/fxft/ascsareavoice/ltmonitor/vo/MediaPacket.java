package net.fxft.ascsareavoice.ltmonitor.vo;

import com.ltmonitor.jt808.protocol.JT_0200;
import com.ltmonitor.jt808.protocol.JT_0801;
import com.ltmonitor.jt808.protocol.T808Message;
import com.ltmonitor.jt808.protocol.T808MessageHeader;
import com.ltmonitor.util.DateUtil;

import java.util.*;

/**
 * 多媒体数据包
 * 用于记录一个完整的拍照数据包，并提供检测包的完整性方法，用于检测是否收到所有的分包
 * @author admin
 *
 */
public class MediaPacket {
	/**
	 * 数据包的key
	 */
	private String key;
	
	private int totalNum;
	
	private Date updateDate = new Date();
	/**
	 * 收到的所有分包
	 */
	private Map<Integer, byte[]> packets = new HashMap<Integer, byte[]>();
	
	private int mediaId;
	
	private T808Message t808Message;
	
	private JT_0200 position;
	/**
	 * 重传次数
	 */
	private int retransCount;
	
	private Date createDate = new Date();
	
	public MediaPacket(T808Message msg)
	{
		T808MessageHeader header = msg.getHeader();
		this.totalNum = header.getMessageTotalPacketsCount();
		
		// 多媒体数据包
		JT_0801 mediaData = new JT_0801();
		mediaData.ReadFromBytes(msg.getHeader().getProtocolVersion(), msg.getChildPacket());
		msg.setMessageContents(mediaData);
		this.mediaId = mediaData.getMultimediaDataId();
		packets.put((int)header.getMessagePacketNo(), mediaData.getMultimediaData());
		this.position = mediaData.getPosition();
		this.t808Message = msg;
	}
	/**
	 * 判断所有的分包是否完整的收到
	 * @return
	 */
	public boolean isComplete()
	{
		return packets.size() == this.totalNum; 
	}
	
	public List<byte[]> getWholePacket()
	{
		if(isComplete() == false)
			return null;
		List<byte[]> result = new ArrayList<byte[]>();
		for(int m = 1; m <= this.totalNum; m++)
		{
			byte[] data = packets.get(m);
			result.add(data);
		}
		return result;
	}
	/**
	 * 返回需要重新上传的数据分包号
	 * @return
	 */
	public ArrayList<Short> getNeedReTransPacketNo()
	{
		ArrayList<Short> result = new ArrayList<Short>();
		if(this.totalNum > this.packets.size())
		{
			for(int m = 1; m <= this.totalNum;m++)
			{
				if(packets.containsKey(m) == false)
					result.add((short)m);
			}
		}
		return result;
	}
	
	public boolean containPacket(int packetNo)
	{
		return packets.containsKey(packetNo);
	}
	
	public void addPacket(int packetNo, byte[] packetData)
	{
		packets.put(packetNo, packetData);
		this.updateDate = new Date();
	}
	
	
	public String toString()
	{
		double seconds = DateUtil.getSeconds(createDate, updateDate);
		StringBuilder sb = new StringBuilder();
		sb.append(this.t808Message.getPlateNo()).append(",").append(this.t808Message.getSimNo())
		.append(",总包数:").append(this.totalNum)
		.append(",已收到:").append(this.packets.size())
		.append(",重传次数:").append(this.retransCount)
		.append(",耗时:").append((int)seconds).append("秒")
		.append(",创建时间:").append(DateUtil.datetimeToString(createDate))
		.append(",结束时间:").append(DateUtil.datetimeToString(updateDate));
		return sb.toString();
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Map<Integer, byte[]> getPackets() {
		return packets;
	}

	public void setPackets(Map<Integer, byte[]> packets) {
		this.packets = packets;
	}

	public int getMediaId() {
		return mediaId;
	}

	public void setMediaId(int mediaId) {
		this.mediaId = mediaId;
	}

	public JT_0200 getPosition() {
		return position;
	}

	public void setPosition(JT_0200 position) {
		this.position = position;
	}
	public T808Message getT808Message() {
		return t808Message;
	}
	public void setT808Message(T808Message t808Message) {
		this.t808Message = t808Message;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getRetransCount() {
		return retransCount;
	}
	public void setRetransCount(int retransCount) {
		this.retransCount = retransCount;
	}
	
	

}
