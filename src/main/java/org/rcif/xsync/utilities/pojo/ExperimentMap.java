package org.rcif.xsync.utilities.pojo;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentMap {
	
    private DomainDefn domain;

    private Map<String, String> labelMap;

}
