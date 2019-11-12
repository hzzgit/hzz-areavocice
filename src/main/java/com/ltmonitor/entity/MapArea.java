package com.ltmonitor.entity;

import com.ltmonitor.service.MapFixService;
import com.ltmonitor.util.StringUtil;
import com.ltmonitor.vo.PointLatLng;
import net.fxft.common.jdbc.DbColumn;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.io.Serializable;
import java.util.Date;

/**
 * 地图电子围栏
 *
 * @author Administrator
 */

//@Entity
//@Table(name = "MapArea")
//@org.hibernate.annotations.Proxy(lazy = false)
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DbTable(value = "MapArea", camelToUnderline = false)
public class MapArea extends TenantEntity implements Serializable {
    public static final String InDriver = "进区域报警给驾驶员";
    public static final String InPlatform = "进区域报警给平台";
    public static final String OutDriver = "出区域报警给驾驶员";
    public static final String OutPlatform = "出区域报警给平台";
    //多边形
    public static String POLYGON = "polygon";
    //矩形
    public static String RECT = "rect";
    //圆形
    public static String CIRCLE = "circle";
    //线路
    public static String ROUTE = "route";
    //地图标记
    public static String MARKER = "marker";
    public static String KEY_POINT = "keyPoint";
    //	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "areaId", unique = true, nullable = false)
    @DbId
    @DbColumn(columnName = "areaId")
    private long entityId;
    /**
     * 地图类型,由于百度地图的坐标类型和其他基于火星坐标的地图不一样， 在判断围栏报警的时候，需要对判断的坐标加偏
     */
    private String mapType;
    private String plateNo;
    //所属部门
    private long depId;
    //地图标记时所用的图标
    private String icon;
    // 围栏名称
    private String name;
    // 围栏坐标点,字符串格式是x1,y1;x2,y2,分别代表左上角和右下角的坐标点值
    private String points;
    // 围栏号
    private int sn;
    // 围栏类型
    private String areaType;
    /**
     * 关键点监控
     */
    private int keyPoint;
    // 报警类型，每个报警使用逗号隔开
    private String alarmType;
    // 围栏有效期
    private Date startDate = new Date(0);
    private Date endDate = new Date(0);
    // 半径，以地图的米为单位
    private double radius;
    // 是否根据时间
    private boolean byTime;
    // 是否限速
    private boolean limitSpeed;
    /**
     * 偏移报警延迟时间
     */
    private int offsetDelay;
    // 超速持续时间
    private int delay;
    // 最大速度限制
    private double maxSpeed;
    private String status;
    // 统一线宽
    private double lineWidth;
    // 围栏的中心
    private double centerLat;
    private double centerLng;
    public MapArea() {
        setCreateDate(new Date());
        setStartDate(new Date());
        setEndDate(new Date());
        this.maxSpeed = 89;
        this.delay = 10;
        this.limitSpeed = true;
        this.setLineWidth(50);
        //this.mapType = Constants.MAP_GOOGLE;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long value) {
        entityId = value;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String value) {
        plateNo = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String value) {
        points = value;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int value) {
        sn = value;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String value) {
        areaType = value;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String value) {
        alarmType = value;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date value) {
        startDate = value;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date value) {
        endDate = value;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double value) {
        radius = value;
    }

    public boolean getByTime() {
        return byTime;
    }

    public void setByTime(boolean value) {
        byTime = value;
    }

    public boolean getLimitSpeed() {
        return limitSpeed;
    }

    public void setLimitSpeed(boolean value) {
        limitSpeed = value;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int value) {
        delay = value;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double value) {
        maxSpeed = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        status = value;
    }

    public double getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(double value) {
        lineWidth = value;
    }

    /**
     * 围栏区域的属性，如果要下发到部标终端，需要按照协议要求，填充到2字节共16位的对应的标示中，然后转换成short数值.
     *
     * @return
     */
    public short CreateAreaAttr() {
        byte[] bytes = new byte[16];
        bytes[0] = (byte) (getByTime() ? 1 : 0); // 1：根据时间
        bytes[1] = (byte) (getLimitSpeed() ? 1 : 0); // 限速
        bytes[2] = (byte) (getAlarmType() != null
                && getAlarmType().indexOf(InDriver) >= 0 ? 1 : 0);
        bytes[3] = (byte) (getAlarmType() != null
                && getAlarmType().indexOf(InPlatform) >= 0 ? 1 : 0);
        bytes[4] = (byte) (getAlarmType() != null
                && getAlarmType().indexOf(OutDriver) >= 0 ? 1 : 0);
        bytes[5] = (byte) (getAlarmType() != null
                && getAlarmType().indexOf(OutPlatform) >= 0 ? 1 : 0);
        bytes[6] = 0;
        bytes[7] = 0;
        bytes[8] = 0; // 0: 关闭 1：启动限速拍照

        bytes[15] = 0; // 15 0：无区域名称 1：有区域名称

        String byteStr = ""; // 二进制字符创
        for (int m = 0; m < 16; m++) {
            byteStr += bytes[15 - m];
            // byteStr += bytes[m];
        }

        // ORIGINAL LINE: ushort t = Convert.ToUInt16(byteStr, 2);
        // short t = Short.parseShort(byteStr, 2); //将二进制字符转换成ushort
        short t = (short) Integer.parseInt(byteStr, 2);
        return t;
    }

    // 区域的每个顶点
    public java.util.ArrayList<EnclosureNode> GetNodes() {
        java.util.ArrayList<EnclosureNode> nodes = new java.util.ArrayList<EnclosureNode>();

        String[] strPts = getPoints().split("[;]", -1);
        for (String strPt : strPts) {
            if (StringUtil.isNullOrEmpty(strPt) == false) {
                String[] strPoint = strPt.split("[,]", -1);
                if (strPoint.length == 2) {
                    EnclosureNode pl = new EnclosureNode();
                    pl.setLng(Double.parseDouble(strPoint[0]));
                    pl.setLat(Double.parseDouble(strPoint[1]));
                    // 下发终端前，坐标要纠偏，转换到wgs84坐标
                    PointLatLng pt = MapFixService.reverse(pl.getLat(),
                            pl.getLng(), this.mapType);
                    pl.setLat(pt.getLat());
                    pl.setLng(pt.getLng());
                    nodes.add(pl);
                }
            }
        }
        if (this.areaType.equals(MapArea.POLYGON) && nodes.size() > 1) {
            EnclosureNode p1 = nodes.get(0);
            EnclosureNode p2 = nodes.get(nodes.size() - 1);
            if (p2.getLat() == p1.getLat() && p2.getLng() == p1.getLng())
                nodes.remove(nodes.size() - 1);
        }
        return nodes;
    }

    public double getCenterLat() {
        return centerLat;
    }

    public void setCenterLat(double centerLat) {
        this.centerLat = centerLat;
    }

    public double getCenterLng() {
        return centerLng;
    }

    public void setCenterLng(double centerLng) {
        this.centerLng = centerLng;
    }

    public int getOffsetDelay() {
        return offsetDelay;
    }

    public void setOffsetDelay(int offsetDelay) {
        this.offsetDelay = offsetDelay;
    }

    public int getKeyPoint() {
        return keyPoint;
    }

    public void setKeyPoint(int keyPoint) {
        this.keyPoint = keyPoint;
    }

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getDepId() {
        return depId;
    }

    public void setDepId(long depId) {
        this.depId = depId;
    }

}