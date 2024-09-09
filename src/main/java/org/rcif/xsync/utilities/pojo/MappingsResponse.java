package org.rcif.xsync.utilities.pojo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MappingsResponse {

    private List<SubjectMapping> subjectMappings;
    private List<ExperimentMapping> experimentMappings;

}
