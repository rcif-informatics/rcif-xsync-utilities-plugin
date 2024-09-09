package org.rcif.xsync.utilities.entities;

import javax.persistence.*;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;
import org.rcif.xsync.utilities.pojo.DomainDefn;

import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
import lombok.EqualsAndHashCode;

//@Slf4j
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = "domain")
})
public class HbDomainDefn extends AbstractHibernateEntity {

    private static final long serialVersionUID = 1503045308184103488L;

	@Column(name = "domain", length = 30, unique = true, nullable = false)
    private String domain;

    @Column(name = "subject_map_only")
    private Boolean subjectMapOnly;
    
    @Transient
    public DomainDefn getDomainDefn() {
		return new DomainDefn(domain, subjectMapOnly, this.isEnabled());
    }

}
