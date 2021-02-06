package net.fxft.ascsareavoice.service.j808.cmd.impl;/**
 * @ClassName CmdParse_8202
 * @Author zwj
 * @Description 位置跟踪
 * @Date 2019/11/22 18:23
 */

import com.ltmonitor.jt808.protocol.JT_8801;
import net.fxft.ascsareavoice.ltmonitor.entity.TerminalCommand;
import net.fxft.ascsareavoice.service.j808.cmd.ICmdParse;
import net.fxft.gateway.protocol.IMsgBody;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: zwj
 * @Date: 2019/11/22 18:23
 */
@Service("CmdParse_8801")
public class CmdParse_8801 implements ICmdParse {
    @Override
    public IMsgBody parse(TerminalCommand tc) throws Exception {
        // 摄像头立即上传命令
        JT_8801 cmdData = new JT_8801();
        String[] fields = tc.getCmdData().split("[;]", -1);
        cmdData.setChannelId(Byte.parseByte(fields[0]));
        short cmdWord = (short) Integer.parseInt(fields[1]);// 录像命令是0xFFFF，超过short值
        cmdData.setPhotoCommand(cmdWord);
        cmdData.setPhotoTimeInterval(Short.parseShort(fields[2]));
        cmdData.setStoreFlag(Byte.parseByte(fields[3]));
        cmdData.setResolution(Byte.parseByte(fields[4]));
        cmdData.setQuality(Byte.parseByte(fields[5]));
        cmdData.setBrightness((byte) (Integer.parseInt(fields[6])));
        cmdData.setContrast((byte) (Integer.parseInt(fields[7])));

        cmdData.setSaturation((byte) (Integer.parseInt(fields[8])));
        cmdData.setChroma((byte) (Integer.parseInt(fields[9])));
        // *
        // * 通道ID BYTE >0
        // 拍摄命令 WORD 0表示停止拍摄；0xFFFF表示录像；其他表示拍照张数
        // 拍照间隔，录像时间 WORD 秒，0表示按最小间隔拍照或一直录像
        // 保持标志 BYTE 1：保存；0：实时上传
        // 分辨率 BYTE 0x01:320*210;
        // 0x02:640*480:
        // 0x03:800*600;
        // 0x04:1024*768;
        // 0x05:176*144;[Qcif];
        // 0x06:352*288;[Cif];
        // 0x07:704*288;[HALF D1];
        // 0x08:701*576;[D1];
        // 图像/视频质量 BYTE 1-10, 1代表质量损失最小，10表示压缩比最大
        // 亮度 BYTE 0-255
        // 对比度 BYTE 0-127
        // 饱和度 BYTE 0-127
        // 色度 BYTE 0-255
        //
        return cmdData;
    }
}
