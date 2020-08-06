package net.fxft.ascsareavoice.ltmonitor.entity;

import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.util.Date;

/**
 * 行车记录仪的速度记录
 *
 * @author Administrator
 */
//@Entity
//@Table(name="SpeedRecorder")
//@org.hibernate.annotations.Proxy(lazy = false)
@DbTable(value = "SpeedRecorder", camelToUnderline = false)
public class SpeedRecorder {
    //主记录Id
//	@javax.persistence.Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "Id", unique = true, nullable = false)
    @DbId
    private int Id;

//    @ManyToOne
//    @JoinColumn(name = "recorderId")
//	private VehicleRecorder recorder;

    private double speed;
    //开关量
    private int signal;
    //序号
    private int sn;
    //记录时间
    private Date recorderDate;
    //创建时间
    private Date createDate;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getRecorderDate() {
        return recorderDate;
    }

    public void setRecorderDate(Date recorderDate) {
        this.recorderDate = recorderDate;
    }
//	public VehicleRecorder getRecorder() {
//		return recorder;
//	}
//	public void setRecorder(VehicleRecorder recorder) {
//		this.recorder = recorder;
//	}

}
