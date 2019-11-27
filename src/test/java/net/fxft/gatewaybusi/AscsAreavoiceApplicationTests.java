package net.fxft.gatewaybusi;

import com.ltmonitor.entity.MapArea;
import com.ltmonitor.service.IMapAreaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AscsAreavoiceApplicationTests {

    @Autowired
    private IMapAreaService mapAreaService;

    @Test
   public void contextLoads() {
        String hql = "select * from MapArea where areaId = ? ";
        MapArea ec = (MapArea) this.mapAreaService.find(hql, 5);
        System.out.println(ec);
    }

}
