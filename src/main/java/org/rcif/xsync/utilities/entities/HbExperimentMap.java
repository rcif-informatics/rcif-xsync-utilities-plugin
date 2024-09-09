package org.rcif.xsync.utilities.entities;

import java.util.Map;

import javax.persistence.*;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;
import org.rcif.xsync.utilities.pojo.DomainProjectMap;
import org.rcif.xsync.utilities.pojo.ExperimentMap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;

//@Slf4j
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints = {
	    @UniqueConstraint(columnNames = "domain")
	})
public class HbExperimentMap extends AbstractHibernateEntity {
	
	private static final long serialVersionUID = 8461910643969579499L;

	@ManyToOne
    @JoinColumn(name = "domain", referencedColumnName = "domain", nullable = false)
    private HbDomainDefn domain;
	
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "xhbm_hb_experiment_map_labels",
		//uniqueConstraints = @UniqueConstraint(columnNames = {"domain", "exp_label"}),
    	joinColumns = @JoinColumn(name = "experiment_map_id", referencedColumnName = "id"))
    @MapKeyColumn(name = "exp_label")  // The subject label as the key
    @Column(name = "domain_exp_label")  // The domain-specific subject label as the value
    @Access(AccessType.FIELD)
    private Map<String, String> labelMap;  // subjLabel -> domainSubjLabel

    @Transient
    public ExperimentMap geExperimentMap() {
    	return new ExperimentMap(domain.getDomainDefn(), labelMap);
    }

}
