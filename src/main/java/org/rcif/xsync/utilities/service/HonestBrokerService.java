package org.rcif.xsync.utilities.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import org.rcif.xsync.utilities.pojo.*;
import org.rcif.xsync.utilities.dao.*;
import org.rcif.xsync.utilities.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import lombok.extern.slf4j.Slf4j;

//@Slf4j
@Service
public class HonestBrokerService {
	
	@Autowired
	private HbDomainDefnDAO hbDomainDefnDAO;
	@Autowired
	private HbDomainProjectMapDAO hbDomainProjectMapDAO;
	@Autowired
	private HbExperimentMapDAO hbExperimentMapDAO;
	@Autowired
	private HbSubjectMapDAO hbSubjectMapDAO;

	@Transactional(readOnly = true)
	public boolean domainExists(String domain) {
		return hbDomainDefnDAO.exists("domain", domain);
	}

	@Transactional
	public DomainDefn createDomain(DomainRequest domainRequest) {
        HbDomainDefn domain = new HbDomainDefn();
        domain.setDomain(domainRequest.getDomain());
        domain.setEnabled(domainRequest.isEnabled());
        domain.setSubjectMapOnly(domainRequest.isSubjectMapOnly());
        hbDomainDefnDAO.create(domain);
        return domain.getDomainDefn();
	}

	@Transactional(readOnly = true)
	public List<DomainDefn> getAllDomains() {
		final List<DomainDefn> returnList = new ArrayList<DomainDefn>();
		for (final HbDomainDefn hbDomain : hbDomainDefnDAO.findAllEnabled()) {
			returnList.add(hbDomain.getDomainDefn());
		}
		return returnList;
	}

	@Transactional(readOnly = true)
	public DomainDefn getDomain(String domain) {
		final HbDomainDefn hbDomainDefn = hbDomainDefnDAO.findByUniqueProperty("domain", domain);
		return (hbDomainDefn != null) ? hbDomainDefn.getDomainDefn() : null;
	}

	@Transactional(readOnly = true)
	public HbDomainDefn getHbDomain(String domain) {
		final HbDomainDefn hbDomainDefn = hbDomainDefnDAO.findByUniqueProperty("domain", domain);
		return hbDomainDefn;
	}

	@Transactional(readOnly = true)
	public List<String> getDomainProjects(String domain) {
		final List<String> returnList = new ArrayList<String>();
		final HbDomainDefn hbDomainDefn = getHbDomain(domain);
		for (final HbDomainProjectMap hbMap : hbDomainProjectMapDAO.findByProperty("domain", hbDomainDefn)) {
			if (hbMap.getDomain().isEnabled()) {
				returnList.add(hbMap.getDomainProjectMap().getProject());
			}
		}
        return returnList;
	}

	@Transactional(readOnly = true)
	public String getDomainForProject(String project) {
		final Map<String, String> propertiesMap = new HashMap<>();
		propertiesMap.put("project", project);
		final HbDomainProjectMap hbMap = hbDomainProjectMapDAO.findByUniqueProperty("project", project);
		return (hbMap!=null) ? hbMap.getDomain().getDomain() : null;
	}

	@Transactional(readOnly = true)
	public HbDomainProjectMap getHbDomainForProject(String project) {
		final Map<String, String> propertiesMap = new HashMap<>();
		propertiesMap.put("project", project);
		final HbDomainProjectMap hbMap = hbDomainProjectMapDAO.findByUniqueProperty("project", project);
		return hbMap;
	}

	@Transactional
	public boolean pairProjectWithDomain(String domain, String proj) {
		final HbDomainDefn hbDomainDefn = hbDomainDefnDAO.findByUniqueProperty("domain", domain);
		if (hbDomainDefn != null) {
			HbDomainProjectMap dpMap = new HbDomainProjectMap();
			dpMap.setDomain(hbDomainDefn);
			dpMap.setProject(proj);
			hbDomainProjectMapDAO.create(dpMap);
			return true;
		}
		return false;
	}

	@Transactional
	public boolean unpairProjectFromDomain(String domain, String proj) {
		final HbDomainProjectMap dpMap = hbDomainProjectMapDAO.findByUniqueProperty("project", proj);
		if (dpMap != null) {
			hbDomainProjectMapDAO.delete(dpMap);
			return true;
		}
		return false;
	}

	@Transactional
	public boolean uploadMappings(String domain, MappingsRequest mappingsRequest) {
		final HbDomainDefn hbDomainDefn = hbDomainDefnDAO.findByUniqueProperty("domain", domain);
		if (hbDomainDefn != null) {
			List<HbSubjectMap> subjMapList = hbSubjectMapDAO.findByProperty("domain", hbDomainDefn);
			HbSubjectMap subjMap = (subjMapList != null && subjMapList.size()>0) ? subjMapList.get(0) : null;
			final Map<String, String> subjValMap = new HashMap<>();
			for (final SubjectMapping sMapping : mappingsRequest.getSubjectMappings()) {
				subjValMap.put(sMapping.getSubjectLabel(), sMapping.getDomainSubjectLabel());
			}
			if (subjMap == null) {
				subjMap = new HbSubjectMap();
			} 
			subjMap.setDomain(hbDomainDefn);
			subjMap.setLabelMap(subjValMap);
			hbSubjectMapDAO.saveOrUpdate(subjMap);

			List<HbExperimentMap> expMapList = hbExperimentMapDAO.findByProperty("domain", hbDomainDefn);
			HbExperimentMap expMap = (expMapList != null && expMapList.size()>0) ? expMapList.get(0) : null;
			final Map<String, String> expValMap = new HashMap<>();
			for (final ExperimentMapping eMapping : mappingsRequest.getExperimentMappings()) {
				expValMap.put(eMapping.getExperimentLabel(), eMapping.getDomainExperimentLabel());
			}
			if (expMap == null) {
				expMap = new HbExperimentMap();
			} 
			expMap.setDomain(hbDomainDefn);
			expMap.setLabelMap(expValMap);
			hbExperimentMapDAO.saveOrUpdate(expMap);
			return true;
		}
		return false;
	}

	@Transactional(readOnly = true)
	public MappingsResponse getMappings(String domain) {
		MappingsResponse response = new MappingsResponse();
		final HbDomainDefn hbDomainDefn = hbDomainDefnDAO.findByUniqueProperty("domain", domain);
		if (hbDomainDefn != null) {
			List<HbSubjectMap> subjMapList = hbSubjectMapDAO.findByProperty("domain", hbDomainDefn);
			final List<SubjectMapping> subjectMappings = new ArrayList<>();
			if (subjMapList !=null && subjMapList.size()>0) {
				for (final Entry<String, String> subjEntry : subjMapList.get(0).getLabelMap().entrySet()) {
					subjectMappings.add(new SubjectMapping(subjEntry.getKey(), subjEntry.getValue()));
				}
			}
			response.setSubjectMappings(subjectMappings);
			List<HbExperimentMap> expMapList = hbExperimentMapDAO.findByProperty("domain", hbDomainDefn);
			final List<ExperimentMapping> experimentMappings = new ArrayList<>();
			if (expMapList !=null && expMapList.size()>0) {
				for (final Entry<String, String> expEntry : expMapList.get(0).getLabelMap().entrySet()) {
					experimentMappings.add(new ExperimentMapping(expEntry.getKey(), expEntry.getValue()));
				}
			}
			response.setExperimentMappings(experimentMappings);
			return response;
		}
		return null;
	}

	@Transactional(readOnly = true)
	public MappingsResponse getMappingsForProject(String project) {
		MappingsResponse response = new MappingsResponse();
		final HbDomainProjectMap hbDomainProjectMap = getHbDomainForProject(project);
		if (hbDomainProjectMap == null) {
			return null;
		}
		final HbDomainDefn hbDomainDefn = hbDomainProjectMap.getDomain();
		if (hbDomainDefn != null) {
			List<HbSubjectMap> subjMapList = hbSubjectMapDAO.findByProperty("domain", hbDomainDefn);
			final List<SubjectMapping> subjectMappings = new ArrayList<>();
			if (subjMapList !=null && subjMapList.size()>0) {
				for (final Entry<String, String> subjEntry : subjMapList.get(0).getLabelMap().entrySet()) {
					subjectMappings.add(new SubjectMapping(subjEntry.getKey(), subjEntry.getValue()));
				}
			}
			response.setSubjectMappings(subjectMappings);
			List<HbExperimentMap> expMapList = hbExperimentMapDAO.findByProperty("domain", hbDomainDefn);
			final List<ExperimentMapping> experimentMappings = new ArrayList<>();
			if (expMapList !=null && expMapList.size()>0) {
				for (final Entry<String, String> expEntry : expMapList.get(0).getLabelMap().entrySet()) {
					experimentMappings.add(new ExperimentMapping(expEntry.getKey(), expEntry.getValue()));
				}
			}
			response.setExperimentMappings(experimentMappings);
			return response;
		}
		return null;
	}

	@Transactional
	public DomainDefn updateDomainEnabled(String domain, boolean enabled) {
		final HbDomainDefn hbDomainDefn = hbDomainDefnDAO.findByUniqueProperty("domain", domain);
		if (hbDomainDefn != null) {
			hbDomainDefn.setEnabled(enabled);
		    hbDomainDefnDAO.update(hbDomainDefn);
		    return hbDomainDefn.getDomainDefn();
		}
		return null;
	}

	@Transactional
	public DomainDefn updateSubjectMappingOnly(String domain, boolean subjectMapOnly) {
		final HbDomainDefn hbDomainDefn = hbDomainDefnDAO.findByUniqueProperty("domain", domain);
		if (hbDomainDefn != null) {
			hbDomainDefn.setSubjectMapOnly(subjectMapOnly);
		    hbDomainDefnDAO.update(hbDomainDefn);
		    return hbDomainDefn.getDomainDefn();
		}
		return null;
	}

	@Transactional
	public boolean deleteDomain(String domain) {
		final HbDomainDefn hbDomainDefn = hbDomainDefnDAO.findByUniqueProperty("domain", domain);
		if (hbDomainDefn != null) {
			hbDomainDefnDAO.delete(hbDomainDefn);
			return true;
		}
		return false;
	}

}
