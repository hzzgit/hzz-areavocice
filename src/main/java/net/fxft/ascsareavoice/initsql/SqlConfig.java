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

    public List<String> allsql = new ArrayList<>();

    @Autowired
    private JdbcUtil jdbcUtil;

    String sql1 = " CREATE TABLE IF NOT EXISTS subiaodb.`orderareamanage` (\n" +
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
    String sql2 = "CREATE TABLE IF NOT EXISTS subiaodb.`orderareapoint` (\n" +
            "  `id` varchar(255) NOT NULL COMMENT '主键,uuid',\n" +
            "  `createDate` datetime DEFAULT NULL COMMENT '创建时间',\n" +
            "  `pointtype` int(11) DEFAULT NULL COMMENT '点位类型,1，开始点，2，途经点，3，结束点',\n" +
            "  `longitude` varchar(255) DEFAULT NULL COMMENT '经度',\n" +
            "  `latitude` varchar(255) DEFAULT NULL COMMENT '纬度',\n" +
            "  `maptype` varchar(255) DEFAULT NULL COMMENT '地图类型 gps:天地图坐标84坐标系，baidu:百度坐标，google:谷歌地图',\n" +
            "  `orderid` int(11) DEFAULT NULL COMMENT '和orderareamanage表主键绑定',\n" +
            "  PRIMARY KEY (`id`)\n" +
            ") ENGINE=InnoDB COMMENT='物流订单围栏点位信息' ";

    String sql3 = "  INSERT INTO `subiaodb`.`alarmconfig`(`id`, `depId`, `alarmSource`, `alarmType`, `enabled`, `name`, `popupEnabled`, `soundEnabled`, `parent`, `statisticEnabled`, `alarmOnce`, `takePictureChannels`, `videoMonitorChannels`, `riskLevel`, `autoProcess`, `points`, `textForDriver`, `remark`) select 0, 0, 'platform_alarm', 'InAreawaybill', b'1', '进入运单围栏报警', b'1', b'1', 'AlarmType', b'0', 0, NULL, NULL, 'none', 0, 0, NULL, NULL from dual \n" +
            "where NOT EXISTS (\n" +
            "    SELECT\n" +
            "      1\n" +
            "    FROM\n" +
            "      subiaodb.alarmconfig\n" +
            "    WHERE\n" +
            "      alarmSource='platform_alarm' and alarmType='InAreawaybill'\n" +
            "  ) ";

    String sql4 = "   INSERT INTO `subiaodb`.`alarmconfig`(`id`, `depId`, `alarmSource`, `alarmType`, `enabled`, `name`, `popupEnabled`, `soundEnabled`, `parent`, `statisticEnabled`, `alarmOnce`, `takePictureChannels`, `videoMonitorChannels`, `riskLevel`, `autoProcess`, `points`, `textForDriver`, `remark`) select 0, 0, 'platform_alarm', 'Crosswaybill', b'1', '离开运单围栏报警', b'1', b'1', 'AlarmType', b'0', 0, NULL, NULL, 'none', 0, 0, NULL, NULL from dual \n" +
            "where NOT EXISTS (\n" +
            "    SELECT\n" +
            "      1\n" +
            "    FROM\n" +
            "      subiaodb.alarmconfig\n" +
            "    WHERE\n" +
            "      alarmSource='platform_alarm' and alarmType='Crosswaybill'\n" +
            "  )";

    //定位围栏增加每个点判断的有效半径
    String sql5 = " ALTER TABLE `subiaodb`.`orderareapoint` \n" +
            "ADD COLUMN `validradius` int(11) NULL COMMENT '判断进出点位的有效半径，单位米，不得小于50，小于50会按照五十来算，不设置会触发默认值200米' ";


    String sql6 = "CREATE TABLE `keypoint_area` (\n" +
            "  `id` bigint(20) NOT NULL COMMENT '主键，雪花算法',\n" +
            "  `name` varchar(255) DEFAULT NULL COMMENT '区域名称',\n" +
            "  `createdate` datetime DEFAULT NULL COMMENT '创建时间',\n" +
            "  `updatedate` datetime DEFAULT NULL COMMENT '修改时间',\n" +
            "  `state` int(11) DEFAULT NULL COMMENT '状态，0、停用，1、启用',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  KEY `index_state` (`state`)\n" +
            ") ENGINE=InnoDB COMMENT='关键点停车订单区域配置' ";
    String sql7 = "CREATE TABLE `keypoint_areapoint` (\n" +
            "  `id` bigint(20) NOT NULL COMMENT '主键',\n" +
            "  `longitude` varchar(255) DEFAULT NULL COMMENT '经度',\n" +
            "  `latitude` varchar(255) DEFAULT NULL COMMENT '纬度',\n" +
            "  `maptype` varchar(255) DEFAULT NULL COMMENT '地图类型 gps:天地图坐标84坐标系，baidu:百度坐标，google:谷歌地图',\n" +
            "  `areaid` bigint(20) DEFAULT NULL COMMENT '订单区域id',\n" +
            "  `createDate` datetime DEFAULT NULL COMMENT '创建时间',\n" +
            "  `deleted` int(11) DEFAULT NULL,\n" +
            "  `updatedate` datetime DEFAULT NULL,\n" +
            "  `cfgradius` int(11) DEFAULT NULL COMMENT '配置该点位生效的半径',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  KEY `index_areaid` (`areaid`),\n" +
            "  KEY `index_deleted` (`deleted`)\n" +
            ") ENGINE=InnoDB  COMMENT='关键点停车订单区域点位信息' ";
    String sql8 = "CREATE TABLE `keypoint_orderbyarea` (\n" +
            "  `id` bigint(20) NOT NULL COMMENT '主键',\n" +
            "  `orderid` bigint(20) DEFAULT NULL COMMENT '订单id',\n" +
            "  `areaid` bigint(20) DEFAULT NULL COMMENT '区域id',\n" +
            "  `deleted` int(11) DEFAULT NULL COMMENT '删除标志',\n" +
            "  `cfgparktime` int(11) DEFAULT NULL COMMENT '停车时长，单位秒',\n" +
            "  `cfgparkdisplacedistance` int(11) DEFAULT NULL COMMENT '停车解除的位移大小，单位米',\n" +
            "  `cfgparkdisplacetime` int(11) DEFAULT NULL COMMENT '停车超时报警的时长，单位秒',\n" +
            "  `createdate` datetime DEFAULT NULL COMMENT '创建时间',\n" +
            "  `updatedate` datetime DEFAULT NULL,\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `unique_orderidandareaid` (`orderid`,`areaid`,`deleted`) USING BTREE,\n" +
            "  KEY `index_orderid` (`orderid`),\n" +
            "  KEY `index_areaid` (`areaid`),\n" +
            "  KEY `index_deleted` (`deleted`)\n" +
            ") ENGINE=InnoDB DEFAULT COMMENT='关键点停车-订单和区域绑定表'";
    String sql9 = "CREATE TABLE `keypoint_orderbysimno` (\n" +
            "  `id` bigint(20) NOT NULL COMMENT '主键，飘雪算法',\n" +
            "  `createDate` datetime DEFAULT NULL COMMENT '创建时间',\n" +
            "  `orderid` bigint(20) DEFAULT NULL COMMENT '订单主键',\n" +
            "  `plateNo` varchar(255) DEFAULT NULL COMMENT '车牌号',\n" +
            "  `simNo` varchar(255) DEFAULT NULL COMMENT 'simNo卡号',\n" +
            "  `deleted` int(11) DEFAULT NULL COMMENT '删除标志',\n" +
            "  `updatedate` datetime DEFAULT NULL COMMENT '修改时间',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `uni_orderidandsimno` (`orderid`,`simNo`,`deleted`) USING BTREE,\n" +
            "  KEY `index_orderid` (`orderid`),\n" +
            "  KEY `index_deleted` (`deleted`),\n" +
            "  KEY `index_simno` (`simNo`)\n" +
            ") ENGINE=InnoDB  COMMENT='(停车)关键点订单围栏订单车辆关系表（多对多）'";
    String sql10 = "CREATE TABLE `keypoint_ordermanage` (\n" +
            "  `id` bigint(20) NOT NULL COMMENT '主键，雪花算法',\n" +
            "  `createDate` datetime DEFAULT NULL COMMENT '创建时间',\n" +
            "  `name` varchar(255) DEFAULT NULL COMMENT '订单名称',\n" +
            "  `byTime` int(11) DEFAULT NULL COMMENT '是否根据时间,0、不根据，1根据',\n" +
            "  `startTime` datetime NOT NULL COMMENT '根据时间的订单开始时间',\n" +
            "  `endTime` datetime DEFAULT NULL COMMENT '根据时间的订单结束时间',\n" +
            "  `state` int(11) DEFAULT NULL COMMENT '状态，0、停用，1、启用',\n" +
            "  `userid` int(11) DEFAULT NULL COMMENT '对外接口管理里面开发者管理表里面绑定的用户id',\n" +
            "  `updateDate` datetime DEFAULT NULL COMMENT '修改时间',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  KEY `index_state` (`state`)\n" +
            ") ENGINE=InnoDB COMMENT='(停车)关键点订单围栏订单表'";

    @PostConstruct
    public void init() {
        Collections.addAll(allsql, sql1, sql2, sql3, sql4, sql5,sql6,sql7,sql8,sql9,sql10);
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
