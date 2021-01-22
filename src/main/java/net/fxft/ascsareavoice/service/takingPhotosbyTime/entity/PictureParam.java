package net.fxft.ascsareavoice.service.takingPhotosbyTime.entity;

/**
 * 拍照命令参数
 * vehicleId: 445697
 * alarmId: 0
 * action: 1
 * channel: 1
 * photoNum: 1
 * interval: 600
 * saveType: 0
 * picSize: 1
 * quality: 5
 * light: 128
 * compare: 71
 * stature: 71
 * grade: 128
 * startTime: 2021-01-21 00:00:00
 * endTime: 2021-01-21 23:59:59
 * @author admin
 * 
 */
public class PictureParam {

	// 通道
	private int channel;
	// 拍摄方式
	private int action=1;
	// 拍摄张数
	private int photoNum=1;
	// 拍摄时间
	private int interval=1;
	// 保存方式
	private int saveType=0;
	// 图片尺寸
	private int picSize=1;
	// 质量
	private int quality=5;
	// 亮度
	private int light=128;
	// 对比度
	private int compare=71;
	// 饱和度
	private int stature=71;
	// 色度
	private int grade=128;

	public PictureParam()
	{
	}
	
	public String getCommandString()
	{
		if (action == 3)
			action = 0xFFFF;
		if (action == 2)
			action = photoNum;

		StringBuilder sb = new StringBuilder();
		sb.append(channel).append(";").append(action).append(";")
				.append(interval).append(";").append(saveType).append(";")
				.append(picSize).append(";").append(quality).append(";")
				.append(light).append(";").append(compare).append(";")
				.append(stature).append(";").append(grade);
		return sb.toString();
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public int getPhotoNum() {
		return photoNum;
	}

	public void setPhotoNum(int photoNum) {
		this.photoNum = photoNum;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getSaveType() {
		return saveType;
	}

	public void setSaveType(int saveType) {
		this.saveType = saveType;
	}

	public int getPicSize() {
		return picSize;
	}

	public void setPicSize(int picSize) {
		this.picSize = picSize;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public int getLight() {
		return light;
	}

	public void setLight(int light) {
		this.light = light;
	}

	public int getCompare() {
		return compare;
	}

	public void setCompare(int compare) {
		this.compare = compare;
	}

	public int getStature() {
		return stature;
	}

	public void setStature(int stature) {
		this.stature = stature;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

}
