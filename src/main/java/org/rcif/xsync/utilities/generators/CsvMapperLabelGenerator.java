package org.rcif.xsync.utilities.generators;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.model.XnatExperimentdataI;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFTItem;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.xsync.generator.XsyncLabelGeneratorI;
import org.nrg.xsync.configuration.ProjectSyncConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Component
@Slf4j
public class CsvMapperLabelGenerator implements XsyncLabelGeneratorI {
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
        ItemI target = BaseElement.GetGeneratedItem(item);
        if (target instanceof XnatSubjectdata) {
            return getMappedLabel(((XnatSubjectdata) target).getLabel(),item.getXSIType());
        } else if (target instanceof XnatExperimentdataI) {
            return getMappedLabel(((XnatExperimentdataI) target).getLabel(),item.getXSIType());
        } else {
            log.warn("Unable to generate label for target because it is not a subject or experiment: {}", target);
            return null;
        }
    }

    @Nonnull
    private String getMappedLabel(String label, String xsiType) {
         return "MAPPED_" + label;
    }
}
