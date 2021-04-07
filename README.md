欢迎使用福信富通GIT代码托管系统!

本仓库名称:ascs-areavoice

项目说明：围栏、语音播报、运单围栏、定时拍照后台计算服务

项目功能：
1、围栏计算服务
涉及的表 maparea、mapareabinding、mapareabydep、mapareauserlim
围栏表、围栏车辆绑定表、围栏机构绑定表、围栏用户授权表
主要计算圆形、矩形、多边形、行政区划（多个多边形）围栏，
只有当进入或者出去的时候才会报警一次
涉及到的接口在subiaoweb

2、计算语音播报服务
autovoice、autovoiceconfig、autovoicebyvehicle、autovoicebydep
语音播报配置表、语音播报配置表附表、车辆绑定表、机构绑定表
涉及到的接口在subiaoweb


3、计算运单围栏
orderareamanage、orderareapoint
运单围栏和simno绑定表、运单围栏所对应的圆形围栏
涉及到的接口在subiaoweb中的对外服务接口，对外服务接口里面
还涉及实时位置、历史轨迹、报警、和视频播放
主要是给对外进行圆形围栏的计算及报警
计算完之后发送到报警服务，报警服务再发送到报警转发服务进行发送

4、定时拍照服务
takingphotosbytime、takingphotosbytimebydep、takingphotosbytimebyvehicle、
takingphotosbytimeuserlim、takingphotosbytimeresult、takingphotosbytimedetail

定时拍照配置表、机构绑定表、车辆绑定表、用户授权绑定表、定时拍照结果表、定时拍照详情表（主要是记录每个通道的拍照结果）

通过定时拍照配置表及其绑定表缓存全量的需要定时拍照的车辆信息，然后进行轮询
并判断是否满足条件，满足则插入到拍照结果表和发送命令并插入详情表，
接收照片在cmd服务里面，接收到之后修改详情表接收状态



请遵守福信富通GIT代码托管系统使用规范。

如有问题请联系运维！
