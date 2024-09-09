package org.rcif.xsync.utilities.entities;

import javax.persistence.*;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;
import org.rcif.xsync.utilities.pojo.DomainProjectMap;

import lombok.Data;
import lombok.EqualsAndHashCode;
//import lombok.extern.slf4j.Slf4j;

//@Slf4j
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = "project")
})
public class HbDomainProjectMap extends AbstractHibernateEntity {
	
	private static final long serialVersionUID = 8568706111284276387L;

	@ManyToOne
    @JoinColumn(name = "domain", referencedColumnName = "domain", nullable = false)
    private HbDomainDefn domain;

    @Column(name = "project", length = 255, nullable = false)
    private String project;
    
    @Transient
    public DomainProjectMap getDomainProjectMap() {
    	return new DomainProjectMap(domain.getDomainDefn(), project);
    }

}
