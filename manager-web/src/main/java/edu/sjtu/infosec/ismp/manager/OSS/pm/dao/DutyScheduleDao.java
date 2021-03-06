package edu.sjtu.infosec.ismp.manager.OSS.pm.dao;
import java.sql.Timestamp;
import java.util.List;

import edu.sjtu.infosec.ismp.manager.OSS.pm.model.DutySchedule;
import edu.sjtu.infosec.ismp.manager.VPM.pm.comm.PMPage;
import edu.sjtu.infosec.ismp.security.Domain;
/**
 * 
 * @author Wu Guojie
 * @date 2010-5-14
 * @version 1.0
 */
public interface DutyScheduleDao {

	/**
	 * 增
	 */
	public void add(DutySchedule dutySchedule)throws Exception;
	/**
	 * 删
	 */
	public void delete(DutySchedule dutySchedule)throws Exception;
	/**
	 * 改
	 */
	public void update(DutySchedule dutySchedule)throws Exception;
	/**
	 * 查询为 id 的对象
	 */
	public DutySchedule findById(int id);
	/**
	 * 查询所有
	 */
	public List<DutySchedule> findAll();
	/**
	 * 条件查询
	 */
	public List<DutySchedule> findConditionsInfo(DutySchedule dutySchedule,List<Domain> domainList,PMPage page,Timestamp startRecordTime, Timestamp endRecordTime);
	/**
	 * 条件查询总数
	 */
	public long  findRosterByCount(DutySchedule dutySchedule,List<Domain> domainList,PMPage page,Timestamp startRecordTime, Timestamp endRecordTime);
	
	/**
	 *发布值班人员
	 * @param dutyScheduleList
	 */
	public void publishedDutySchedule(List<DutySchedule> dutyScheduleList);
	/**
	 * 获取未发布的人员
	 * @return
	 */
	public List<DutySchedule> findNotPulishedDutySchedule(DutySchedule dutySchedule,List<Domain> domainList);
	/**
	 * 获取未值班的人员
	 * @return
	 */
	public List<DutySchedule> findDutyDate(Timestamp startRecordTime);
}
