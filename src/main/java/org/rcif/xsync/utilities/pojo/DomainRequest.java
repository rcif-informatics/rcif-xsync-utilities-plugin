package org.rcif.xsync.utilities.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DomainRequest {
	
    private String domain;
    private boolean enabled = false;
    private boolean subjectMapOnly = false;

}
