package org.rcif.xsync.utilities.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DomainDefn {

    private String domain;

    private Boolean subjectMapOnly;

    private Boolean enabled;

}
