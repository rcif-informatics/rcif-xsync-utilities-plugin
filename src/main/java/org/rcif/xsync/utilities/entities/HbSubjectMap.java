package org.rcif.xsync.utilities.entities;

import java.util.Map;

import javax.persistence.*;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;
import org.rcif.xsync.utilities.pojo.SubjectMap;

import lombok.Data;
import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;

//@Slf4j
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints = {
	    @UniqueConstraint(columnNames = "domain")
})
public class HbSubjectMap extends AbstractHibernateEntity {
	
    private static final long serialVersionUID = 8766665940125260394L;

	@ManyToOne
    @JoinColumn(name = "domain", referencedColumnName = "domain", nullable = false)
    private HbDomainDefn domain;
	
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "xhbm_hb_subject_map_labels", 
    	//uniqueConstraints = @UniqueConstraint(columnNames = {"domain", "subj_label"}),
    	joinColumns = @JoinColumn(name = "subject_map_id", referencedColumnName = "id")
    )
    @MapKeyColumn(name = "subj_label")  // The subject label as the key
    @Column(name = "domain_subj_label")  // The domain-specific subject label as the value
    @Access(AccessType.FIELD)
    private Map<String, String> labelMap;  // subjLabel -> domainSubjLabel

    @Transient
    public SubjectMap getSubjectMap() {
    	return new SubjectMap(domain.getDomainDefn(), labelMap);
    }

}
