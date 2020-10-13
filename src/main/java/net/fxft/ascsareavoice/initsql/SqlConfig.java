package net.fxft.ascsareavoice.initsql;

import net.fxft.ascsareavoice.ltmonitor.util.ConverterUtils;
import lombok.extern.slf4j.Slf4j;
import net.fxft.common.jdbc.JdbcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author hzz
 * @version 1.0
 * @date 2020/4/16 16:07
 */
@Service
@Slf4j
public class SqlConfig implements BeanPostProcessor {

    public List<String > allsql=new ArrayList<>();

    @Autowired
    private JdbcUtil jdbcUtil;

    String sql1=" CREATE TABLE IF NOT EXISTS subiaodb.`orderareamanage` (\n" +
            "  `id` varchar(255) NOT NULL COMMENT '主键,uuid',\n" +
            "  `createDate` datetime DEFAULT NULL COMMENT '创建时间',\n" +
            "  `plateNo` varchar(255) DEFAULT NULL COMMENT '车牌号',\n" +
            "  `simNo` varchar(255) DEFAULT NULL COMMENT 'simNo,终端卡号',\n" +
            "  `name` varchar(255) DEFAULT NULL COMMENT '订单名称',\n" +
            "  `byTime` int(11) DEFAULT NULL COMMENT '是否根据时间，0：否。1：是',\n" +
            "  `startTime` datetime DEFAULT NULL COMMENT '订单开始时间',\n" +
            "  `endTime` datetime DEFAULT NULL COMMENT '订单结束时间',\n" +
            "  `state` int(11) DEFAULT NULL COMMENT '状态，0、停用，1、启用',\n" +
            "  `userid` int(11) DEFAULT NULL COMMENT '对外接口管理里面开发者管理表里面绑定的用户id',\n" +
            "  PRIMARY KEY (`id`)\n" +
            ") ENGINE=InnoDB  COMMENT='物流订单围栏管理' ";
String sql2="CREATE TABLE IF NOT EXISTS subiaodb.`orderareapoint` (\n" +
        "  `id` varchar(255) NOT NULL COMMENT '主键,uuid',\n" +
        "  `createDate` datetime DEFAULT NULL COMMENT '创建时间',\n" +
        "  `pointtype` int(11) DEFAULT NULL COMMENT '点位类型,1，开始点，2，途经点，3，结束点',\n" +
        "  `longitude` varchar(255) DEFAULT NULL COMMENT '经度',\n" +
        "  `latitude` varchar(255) DEFAULT NULL COMMENT '纬度',\n" +
        "  `maptype` varchar(255) DEFAULT NULL COMMENT '地图类型 gps:天地图坐标84坐标系，baidu:百度坐标，google:谷歌地图',\n" +
        "  `orderid` int(11) DEFAULT NULL COMMENT '和orderareamanage表主键绑定',\n" +
        "  PRIMARY KEY (`id`)\n" +
        ") ENGINE=InnoDB COMMENT='物流订单围栏点位信息' ";

String sql3="  INSERT INTO `subiaodb`.`alarmconfig`(`id`, `depId`, `alarmSource`, `alarmType`, `enabled`, `name`, `popupEnabled`, `soundEnabled`, `parent`, `statisticEnabled`, `alarmOnce`, `takePictureChannels`, `videoMonitorChannels`, `riskLevel`, `autoProcess`, `points`, `textForDriver`, `remark`) select 0, 0, 'platform_alarm', 'InAreawaybill', b'1', '进入运单围栏报警', b'1', b'1', 'AlarmType', b'0', 0, NULL, NULL, 'none', 0, 0, NULL, NULL from dual \n" +
        "where NOT EXISTS (\n" +
        "    SELECT\n" +
        "      1\n" +
        "    FROM\n" +
        "      subiaodb.alarmconfig\n" +
        "    WHERE\n" +
        "      alarmSource='platform_alarm' and alarmType='InAreawaybill'\n" +
        "  ) ";

String sql4="   INSERT INTO `subiaodb`.`alarmconfig`(`id`, `depId`, `alarmSource`, `alarmType`, `enabled`, `name`, `popupEnabled`, `soundEnabled`, `parent`, `statisticEnabled`, `alarmOnce`, `takePictureChannels`, `videoMonitorChannels`, `riskLevel`, `autoProcess`, `points`, `textForDriver`, `remark`) select 0, 0, 'platform_alarm', 'Crosswaybill', b'1', '离开运单围栏报警', b'1', b'1', 'AlarmType', b'0', 0, NULL, NULL, 'none', 0, 0, NULL, NULL from dual \n" +
        "where NOT EXISTS (\n" +
        "    SELECT\n" +
        "      1\n" +
        "    FROM\n" +
        "      subiaodb.alarmconfig\n" +
        "    WHERE\n" +
        "      alarmSource='platform_alarm' and alarmType='Crosswaybill'\n" +
        "  )";

//定位围栏增加每个点判断的有效半径
String sql5=" ALTER TABLE `subiaodb`.`orderareapoint` \n" +
        "ADD COLUMN `validradius` int(11) NULL COMMENT '判断进出点位的有效半径，单位米，不得小于50，小于50会按照五十来算，不设置会触发默认值200米' ";

    @PostConstruct
    public void init(){
        Collections.addAll(allsql,sql1,sql2,sql3,sql4,sql5);
        if (ConverterUtils.isList(allsql)) {
            for (String s : allsql) {
                try {
                    jdbcUtil.sql(s).executeUpdate();
                } catch (Exception e) {
                }
            }
        }
    }



}
