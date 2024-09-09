package org.rcif.xsync.utilities.generators;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.model.XnatExperimentdataI;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFTItem;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.xsync.generator.XsyncLabelGeneratorI;
import org.nrg.xsync.configuration.ProjectSyncConfiguration;
import org.rcif.xsync.utilities.pojo.ExperimentMapping;
import org.rcif.xsync.utilities.pojo.MappingsResponse;
import org.rcif.xsync.utilities.pojo.SubjectMapping;
import org.rcif.xsync.utilities.service.HonestBrokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Component
@Slf4j
public class HonestBrokerLabelGenerator implements XsyncLabelGeneratorI {
	
	@Autowired
	private HonestBrokerService honestBrokerService;
    /**
     * Generate new label for data based on date/time
     * @param user the user
     * @param item the item
     * @param projectSyncConfiguration the project sync configuration
     * @return the new label or null if not able to assign one
     */
    @Nullable
    @Override
    public String generateId(UserI user, XFTItem item, ProjectSyncConfiguration projectSyncConfiguration) {
    	final String project = projectSyncConfiguration.getProject().getProject();
        ItemI target = BaseElement.GetGeneratedItem(item);
        if (target instanceof XnatSubjectdata) {
            return getMappedLabel(((XnatSubjectdata) target).getLabel(),item.getXSIType(),project);
        } else if (target instanceof XnatExperimentdataI) {
            return getMappedLabel(((XnatExperimentdataI) target).getLabel(),item.getXSIType(),project);
        } else {
            log.warn("Unable to generate label for target because it is not a subject or experiment: {}", target);
            return null;
        }
    }

    @Nonnull
    private String getMappedLabel(String label, String xsiType, String project) {
    	
    	String returnLabel = label;
    	final MappingsResponse mappingsResponse = honestBrokerService.getMappingsForProject(project);
    	if (mappingsResponse != null) {
    		if (xsiType.toLowerCase().contains("subjectdata")) {
    			returnLabel = getLabelFromSubjectList(label, mappingsResponse.getSubjectMappings());
    		} else {
    			returnLabel = getLabelFromExperimentList(label, mappingsResponse);
    		}
    	}
        return returnLabel;
    }

	private String getLabelFromSubjectList(String label, List<SubjectMapping> subjectMappings) {
		for (final SubjectMapping smap : subjectMappings) {
			if (smap.getSubjectLabel().equals(label)) {
				return smap.getDomainSubjectLabel();
			}
		}
		return label;
	}

	private String getLabelFromExperimentList(String label, MappingsResponse mappingsResponse) {
		final List<ExperimentMapping> experimentMappings = mappingsResponse.getExperimentMappings();
		for (final ExperimentMapping emap : experimentMappings) {
			if (emap.getExperimentLabel().equals(label)) {
				return emap.getDomainExperimentLabel();
			} else if (emap.getExperimentLabel().equals("$MATCH_ALL")) {
				if (emap.getDomainExperimentLabel().equals("$REPLACE_SUBJECT")) {
					final List<SubjectMapping> subjectMappings = mappingsResponse.getSubjectMappings();
					for (final SubjectMapping subjectMapping : subjectMappings) {
						if (label.contains(subjectMapping.getSubjectLabel())) {
							return label.replace(subjectMapping.getSubjectLabel(), subjectMapping.getDomainSubjectLabel());
						}
					}
				}
			}
		}
		return label;
	}
}
