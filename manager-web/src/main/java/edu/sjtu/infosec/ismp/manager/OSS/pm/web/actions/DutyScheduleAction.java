package edu.sjtu.infosec.ismp.manager.OSS.pm.web.actions;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.infosec.ismp.agent.comm.winsensor.model.operation.Duty;
import org.infosec.ismp.agent.comm.winsensor.model.operation.DutyManager;
import org.infosec.ismp.manager.rmi.comm.model.SystemModelInfo;
import org.infosec.ismp.manager.rmi.sensor.operation.service.WinsensorOperationDutyManagerService;

import common.Logger;

import edu.sjtu.infosec.ismp.manager.GOSP.comm.LogUtil;
import edu.sjtu.infosec.ismp.manager.LM.pfLog.service.SystemLogService;
import edu.sjtu.infosec.ismp.manager.OSS.pm.model.Complaint;
import edu.sjtu.infosec.ismp.manager.OSS.pm.model.DutySchedule;
import edu.sjtu.infosec.ismp.manager.OSS.pm.model.Roster;
import edu.sjtu.infosec.ismp.manager.OSS.pm.service.ComplaintService;
import edu.sjtu.infosec.ismp.manager.OSS.pm.service.DutyScheduleService;
import edu.sjtu.infosec.ismp.manager.OSS.pm.service.RosterService;
import edu.sjtu.infosec.ismp.manager.OSS.pm.web.form.DutyScheduleForm;
import edu.sjtu.infosec.ismp.manager.SYSM.user.self.comm.SecurityUserHolder;
import edu.sjtu.infosec.ismp.manager.SYSM.user.self.service.DomainService;
import edu.sjtu.infosec.ismp.manager.VPM.pm.comm.HtmlFactory;
import edu.sjtu.infosec.ismp.manager.VPM.pm.comm.PMPage;
import edu.sjtu.infosec.ismp.security.Domain;
import edu.sjtu.infosec.ismp.security.OperatorDetails;

public class DutyScheduleAction extends DispatchAction{

	Logger logger = Logger.getLogger(DutyScheduleAction.class);
	private DutyScheduleService dutyScheduleService;
	private SystemLogService systemlogservice;
	/**
	 * rosterService
	 *
	 * @return  the rosterService
	 * @since   CodingExample Ver 1.0
	 */
	
	public RosterService getRosterService() {
		return rosterService;
	}
	/**
	 * rosterService
	 *
	 * @param   rosterService    the rosterService to set
	 * @since   CodingExample Ver 1.0
	 */
	
	public void setRosterService(RosterService rosterService) {
		this.rosterService = rosterService;
	}
	private RosterService rosterService;
	
	/**
	 * @param systemlogservice the systemlogservice to set
	 */
	public void setSystemlogservice(SystemLogService systemlogservice) {
		this.systemlogservice = systemlogservice;
	}
	/**
	 * @param winsensorOperationDutyManagerService the winsensorOperationDutyManagerService to set
	 */
	public void setWinsensorOperationDutyManagerService(
			WinsensorOperationDutyManagerService winsensorOperationDutyManagerService) {
		this.winsensorOperationDutyManagerService = winsensorOperationDutyManagerService;
	}
	private WinsensorOperationDutyManagerService  winsensorOperationDutyManagerService;
	private ComplaintService  complaintService;
	
	@SuppressWarnings("unused")
	private DomainService domainService;
	
	/**
	 * @param complaintService the complaintService to set
	 */
	public void setComplaintService(ComplaintService complaintService) {
		this.complaintService = complaintService;
	}

	
	public void setDutyScheduleService(DutyScheduleService dutyScheduleService) {
		this.dutyScheduleService = dutyScheduleService;
	}

	public void setDomainService(DomainService domainService) {
		this.domainService = domainService;
	}

	  /**
	   * 查询所有值班记录
	   * ds (DutySchedule 简写)
	   */
	  public ActionForward dsall(ActionMapping mapping, ActionForm form, 
			  HttpServletRequest request, HttpServletResponse response) throws Exception{
		  OperatorDetails user = SecurityUserHolder.getCurrentUser();
		  DutyScheduleForm dsForm =(DutyScheduleForm)form;
		  String identifier = request.getParameter("identifier");
		  String type=request.getParameter("type");
		  String doMainId=request.getParameter("doid");
		  String seachTime= request.getParameter("seachTime");
		  List<Domain> domainList =null;
			PMPage page = new PMPage();
			String curpage = request.getParameter("currPage") != null
					&& (!request.getParameter("currPage").equals("")) ? request
					.getParameter("currPage") : "1";
			page.setCurrentPage(Integer.parseInt(curpage));
			page.setBeginIndex((page.getCurrentPage() - 1) * page.getEveryPage());
		  if(!(user == null)){
			  if("seach".equals(type)){
				   if(HtmlFactory.isNotEmpty(doMainId)){
					   Domain domain = new Domain();
					   domain.setId(Integer.valueOf(doMainId));
					   List<Domain> list = new ArrayList<Domain>();
					   list.add(domain);
					   domainList=list;
				   }
				   if(HtmlFactory.isNotEmpty(seachTime)){
					   dsForm.setCreateStartDate(Timestamp.valueOf(seachTime));
				   }
			  }else{
				  domainList = user.getDomainList();
			  }
			  List<DutySchedule> list =  dutyScheduleService.findConditionsInfo(dsForm.getDs(), domainList , page, dsForm.getCreateStartDate(), dsForm.getCreateEndDate());
			  if("onduty".equals(identifier)){
				  List<Roster> rosterList = new ArrayList<Roster>();
				  Set<Integer> set = new HashSet<Integer>();
				  for(DutySchedule duty : list){
					  for(Iterator<Roster> iter = duty.getRoster().iterator();iter.hasNext();){
						  Roster rosters = iter.next();
						  if(!set.contains(rosters.getId())){
							  set.add(rosters.getId());
							  rosters.setDomain(duty.getDomain());
							  rosterList.add(rosters);
						  } 
					  } 
				  }  
				  request.setAttribute("rosters", rosterList);
			  }
			  request.setAttribute("list", list);
			  request.setAttribute("page", page.getPageInfo());
			  request.setAttribute("currPage", page.getPageInfo().getCurrentPage());
	    	  request.setAttribute("totalPage", page.getPageInfo().getTotalPage());
	    	  request.setAttribute("ossMenu","pm");
		  	  return mapping.findForward(identifier);
		  }
		  return null;
	  }	
	  /**
	   * 添加值班记录
	   * @param mapping
	   * @param form
	   * @param request
	   * @param response
	   * @return
	   * @throws Exception
	   */
	  public ActionForward dsadd(ActionMapping mapping, ActionForm form, 
			  HttpServletRequest request, HttpServletResponse response) throws Exception{
		     DutySchedule ds = new DutySchedule();
		     Domain domain = new Domain();
		     Set<Roster> set = new HashSet<Roster>();
		     String rosters[]=  request.getParameterValues("rostersList");
		     String txt = request.getParameter("task");
		     String domainId = request.getParameter("doselect");
		     String startTime = request.getParameter("startTime");
		     String endTime = request.getParameter("endTime");
		     if(HtmlFactory.isNotEmpty(domainId)){
		    	 domain.setId(Integer.valueOf(domainId));
		     }
		     if(!(rosters == null)){
		    	 for(String r : rosters) {
		    		 Roster roster = new Roster();
		    		 roster.setId(Integer.valueOf(r));
		    		 set.add(roster);
		    	 }
		    	 if(rosters.length > 0){
		    		 ds.setIsLeader(Integer.valueOf(rosters[0]));
		    	 }
		     }
		      ds.setStartTime(Timestamp.valueOf(startTime));
		      ds.setEndTime(Timestamp.valueOf(endTime));
		      ds.setCreateTime(new Timestamp(System.currentTimeMillis()));
		      ds.setDomain(domain);
		      ds.setTask(txt);
		      ds.setRoster(set);
		      
		       String falg="成功！";
	      	   try {
	      		    dutyScheduleService.add(ds);
	    	   } catch (Exception e) {
	    		 falg = "失败！";
	    	   }finally{
	    		   try {
					  systemlogservice.saveSystemLog(LogUtil.userName, LogUtil.roleName, SystemModelInfo.MOD_OSS_pm, "添加值班记录", new Timestamp(System.currentTimeMillis()), falg);
				  } catch (Exception e) {
					 logger.debug("连接日志出错",e);
				  }
	    	   }
		      
		 return null;
	  }
	  /**
	   * 查询某个值班记录
	   * @param mapping
	   * @param form
	   * @param request
	   * @param response
	   * @return
	   * @throws Exception
	   */
	  public ActionForward dsById(ActionMapping mapping, ActionForm form, 
			  HttpServletRequest request, HttpServletResponse response) throws Exception{
		      String rid= request.getParameter("dsid");
		      Complaint complaint = null;
		      String identifier = request.getParameter("identifier");
		      DutySchedule dutySchedule =  dutyScheduleService.findById(Integer.valueOf(rid));
		      request.setAttribute("dutySchedule",dutySchedule);
		      if(!(dutySchedule == null)){
		    	  List<Complaint> list = complaintService.searchByDomian(dutySchedule.getDomain().getId());
		    	  complaint = list.get(0);
		      }
		      request.setAttribute("phoneComplaint", complaint.getComplaintPhone());
		      return mapping.findForward(identifier);
	  }
	  /**
	   * 更新值班记录
	   * @param mapping
	   * @param form
	   * @param request
	   * @param response
	   * @return
	   * @throws Exception
	   */
	  public ActionForward dsUpdate(ActionMapping mapping, ActionForm form, 
			  HttpServletRequest request, HttpServletResponse response) throws Exception{
		     DutySchedule dutySchedule =null;
		     Domain domain = new Domain();
		     Set<Roster> set = new HashSet<Roster>();
		     String dutyId=request.getParameter("dutyId");
		     if(HtmlFactory.isNotEmpty(dutyId)){
		    	 dutySchedule =  dutyScheduleService.findById(Integer.valueOf(dutyId));
		    	 String rosters[]=  request.getParameterValues("rostersList");
		    	 String txt = request.getParameter("textfield");
		    	 String domainId = request.getParameter("doselect");
		    	 String startTime = request.getParameter("startTime");
		    	 String endTime = request.getParameter("endTime");
		    	 if(HtmlFactory.isNotEmpty(domainId)){
		    		 domain.setId(Integer.valueOf(domainId));
		    	 }
		    	 if(!(rosters == null)){
		    		 for(String r : rosters) {
		    			 Roster roster = new Roster();
		    			 roster.setId(Integer.valueOf(r));
		    			 set.add(roster);
		    		 }
		    		 dutySchedule.setIsLeader(Integer.valueOf(rosters[0]));
		    	 }
		    	 dutySchedule.setStartTime(Timestamp.valueOf(startTime));
		    	 dutySchedule.setEndTime(Timestamp.valueOf(endTime));
		    	 dutySchedule.setLastUpdateTime(new Timestamp(new Date().getTime()));
		    	 dutySchedule.setDomain(domain);
		    	 dutySchedule.setTask(txt);
		    	 dutySchedule.setRoster(set);
		    	 
			       String falg="成功！";
		      	   try {
		      		    dutyScheduleService.update(dutySchedule);
		    	   } catch (Exception e) {
		    		 falg = "失败！";
		    	   }finally{
		    		   try {
						  systemlogservice.saveSystemLog(LogUtil.userName, LogUtil.roleName, SystemModelInfo.MOD_OSS_pm, "更新值班记录", new Timestamp(System.currentTimeMillis()), falg);
					  } catch (Exception e) {
						 logger.debug("连接日志出错",e);
					  }
		    	   }
		     }
			 return null;
	  }
	  /**
	   * 删除值班记录
	   * @param mapping
	   * @param form
	   * @param request
	   * @param response
	   * @return
	   * @throws Exception
	   */
	  public ActionForward dsDel(ActionMapping mapping, ActionForm form, 
			  HttpServletRequest request, HttpServletResponse response) throws Exception{
		  String rid= request.getParameter("dsid");
		  String falg="成功！";
	     	try {
	     	    DutySchedule dutySchedule =  dutyScheduleService.findById(Integer.valueOf(rid));
				winsensorOperationDutyManagerService.deleteDutyManager(rid);
				dutyScheduleService.delete(dutySchedule);
			} catch (Exception e) {
				falg = "失败！";
			} finally {
				try {
					systemlogservice.saveSystemLog(LogUtil.userName,
							LogUtil.roleName, SystemModelInfo.MOD_OSS_pm, "删除值班记录",
							new Timestamp(System.currentTimeMillis()), falg);
				} catch (Exception e) {
					logger.debug("连接日志出错", e);
				}
			}
		  return dsall(mapping,new DutyScheduleForm(),request,response );
	  }
	 /**
	  * 查询域下的投诉电话
	  * @param mapping
	  * @param form
	  * @param request
	  * @param response
	  * @return
	  * @throws Exception
	  */
	  public ActionForward searchDomainToComplaint(ActionMapping mapping, ActionForm form, 
			  HttpServletRequest request, HttpServletResponse response) throws Exception{
		      String cId = request.getParameter("cid");
		      StringBuffer sbf = new StringBuffer();
		      String identifier = "N";
		      Complaint complaint = null;
		      if(HtmlFactory.isNotEmpty(cId)){
				   try {
						  Integer id = Integer.valueOf(cId);
				    	  if(!(id == 0)){
				    		  List<Complaint> list = complaintService.searchByDomian(id);
				    		  if(!(list == null) && list.size() > 0){
				    			  identifier = "Y";
				    			  complaint = list.get(0);
				    		  }
				    	  }
					 } catch (Exception e) {
						  
				   }
		      }
		       Object[][] objs = {{"add","identifier",identifier}};
		       HtmlFactory.getDataArray(objs, sbf);
		       if(!(complaint == null) && identifier.equals("Y")){
		    	   Object[][] obj ={{null,"serialVersionUID"},{null,"domain"}};
		    	   HtmlFactory.getDataArray(complaint, sbf ,obj , "COMPLAINT");
		       }
		       HtmlFactory.flushData(response, sbf);
		  return null;
	  }
	  /**
	   * 更新域下的投诉电话
	   * @param mapping
	   * @param form
	   * @param request
	   * @param response
	   * @return
	   * @throws Exception
	   */
	  public ActionForward addOrUpdateDomainToComplaint(ActionMapping mapping, ActionForm form, 
			  HttpServletRequest request, HttpServletResponse response) throws Exception{
		      DutyScheduleForm dsForm = (DutyScheduleForm) form;
		      Integer cid =dsForm.getComplaint().getId();
		      dsForm.getComplaint().setId(cid == null || cid < 1   ? null : cid);
		      String domainId = request.getParameter("domain");
		      StringBuffer sbf = new StringBuffer();
		      String identifier = "N";
			  String falg="成功！";
	     	    try {
						if (HtmlFactory.isNotEmpty(domainId)) {
							Domain domain = new Domain();
							domain.setId(Integer.valueOf(domainId));
							dsForm.getComplaint().setDomain(domain);
							complaintService.save(dsForm.getComplaint());
							identifier = "Y";
						}
				} catch (Exception e) {
					falg = "失败！";
				} finally {
					try {
						systemlogservice.saveSystemLog(LogUtil.userName,
								LogUtil.roleName, SystemModelInfo.MOD_OSS_pm,
								"更新域下的投诉电话", new Timestamp(System.currentTimeMillis()),
								falg);
					} catch (Exception e) {
						logger.debug("连接日志出错", e);
					}
				}
		Object[][] objs = { { "add", "identifier", identifier } };
		HtmlFactory.getDataArray(objs, sbf);
		HtmlFactory.flushData(response, sbf);
		return null;
	  }
	  /**
	   *  发布人员
	   * @param mapping
	   * @param form
	   * @param request
	   * @param response
	   * @return
	   * @throws Exception
	   */
	  public ActionForward publishDutySchedule(ActionMapping mapping, ActionForm form, 
			  HttpServletRequest request, HttpServletResponse response) throws Exception{
		  OperatorDetails user = SecurityUserHolder.getCurrentUser();
		  List<Domain> domainList =  user.getDomainList();
		  List<DutySchedule> dutyList =  dutyScheduleService.findNotPulishedDutySchedule(domainList);
		  List<DutyManager> dutyManagerList = new ArrayList<DutyManager>();
		  Map<String, String> complaintMap = complaintService.searchDomainById();
	      StringBuffer sbf = new StringBuffer();
	      String identifier = "N";
		  String falg ="成功";
	  	   try {
	  		 for(DutySchedule duty : dutyList){
				  String domainId = duty.getDomain().getId().toString();
				  DutyManager dutyManager = new DutyManager();
				  dutyManager.setId(duty.getId().toString());
				  dutyManager.setBeginDate(this.getFormatDate(duty.getStartTime()));
				  dutyManager.setEndDate(this.getFormatDate(duty.getEndTime()));
				  dutyManager.setComplaintNumber(complaintMap.get(domainId));
				  dutyManager.setCreateTime(duty.getCreateTime());
				  dutyManager.setDomainId(domainId);
				  List<Duty> list  = new ArrayList<Duty>();
				  for(Iterator<Roster> iter = duty.getRoster().iterator();iter.hasNext();){
					  Duty d = new Duty();
					  Roster roster = iter.next();
					  d.setId(roster.getId().toString());
					  d.setName(roster.getName());
					  d.setEmail(roster.getEMail());
					  d.setIsManager(duty.getIsLeader() == roster.getId());
					  d.setMobilePhone(roster.getMobile());
					  d.setPhone(roster.getPhone());
					  d.setSex(roster.getSex()==0 ? "男" : "女" );
					  d.setResponsibility(duty.getTask());
					  d.setCreateTime(roster.getCreateTime());
					  list.add(d);
				  }
				  dutyManager.setDuties(list);
				  dutyManagerList.add(dutyManager);
			  }
			  winsensorOperationDutyManagerService.publishDutyManager(dutyManagerList);
			  dutyScheduleService.publishedDutySchedule(dutyList);
			  identifier = "Y";
			} catch (Exception e) {
				falg = "失败！";
			} finally {
				try {
					systemlogservice.saveSystemLog(LogUtil.userName,
							LogUtil.roleName, SystemModelInfo.MOD_OSS_pm, "发布人员",
							new Timestamp(System.currentTimeMillis()), falg);
				} catch (Exception e) {
					logger.debug("连接日志出错", e);
				}
			}
        Object[][] objs = {{"add","identifier",identifier}};
        HtmlFactory.getDataArray(objs, sbf);
        HtmlFactory.flushData(response, sbf);
	   return null;
	  }
	  
	  /**
	   * 删除值班记录
	   * @param mapping
	   * @param form
	   * @param request
	   * @param response
	   * @return
	   * @throws Exception
	   */
	  public ActionForward findDateByRoster(ActionMapping mapping, ActionForm form, 
			  HttpServletRequest request, HttpServletResponse response) throws Exception{
		      
		      String date  = request.getParameter("date");
		      
		      List<DutySchedule> list  =  dutyScheduleService.findDutyDate(new Timestamp( Date.parse(date) ));
		      
		      
		  return dsall(mapping,new DutyScheduleForm(),request,response );
	  }
	  
	  /**
	   * 格式化时间
	   * @param time
	   * @return
	   */
	 private String getFormatDate(Timestamp time){
		 if(!(time == null)){
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 Date date = new Date();
			 date.setTime(time.getTime());
			 return  sdf.format(date);
		 }
		 return "No Time";
	 }
}
