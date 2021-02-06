package net.fxft.ascsareavoice.service.j808.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 808Gps服务
 * 
 * @author DELL
 * 
 */
public class T808Manager {

	private static Logger log = LoggerFactory.getLogger(T808Manager.class);

	/**
	 * 下发流水号
	 */
	private static int serialNo = 0;

//	@Autowired
//	private IPlatformStateService platformStateService;

	public static short getSerialNo() {
		if (serialNo >= (Short.MAX_VALUE - 1))
			serialNo = 0;
		return (short) serialNo++;
	}


//	@PostConstruct
//	public boolean StartServer() {
//			try {
//				PlatformState ps = platformStateService.getPlatformState();
//				ps.setGpsServerDate(new Date());
//				ps.setGpsServerState(PlatformState.STATE_START);
//				platformStateService.saveOrUpdate(ps);
//			} catch (Exception e) {
//				log.error(e.getMessage(), e);
//			}
//		return true;
//	}

//	@PreDestroy
//	public void StopServer() {
//		log.info("---begin stop T808Manager---");
//		try {
//			PlatformState ps = platformStateService.getPlatformState();
//			ps.setGpsServerDate(new Date());
//			ps.setGpsServerState(PlatformState.STATE_STOP);
//			platformStateService.saveOrUpdate(ps);
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//		}
//		log.info("---end stop T808Manager---");
//	}

}
