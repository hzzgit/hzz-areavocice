package net.fxft.ascsareavoice;

import net.fxft.ascsareavoice.ltmonitor.entity.MapArea;
import net.fxft.ascsareavoice.ltmonitor.service.IMapAreaService;
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
