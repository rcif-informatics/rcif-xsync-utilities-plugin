package org.rcif.xsync.utilities.xapi;

import io.swagger.annotations.*;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.rcif.xsync.utilities.service.HonestBrokerService;
import org.rcif.xsync.utilities.pojo.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import java.util.List;

@Lazy
@XapiRestController
@RequestMapping("/honestbroker")
@Api(value = "Honest Broker API", description = "Operations pertaining to domains and projects in the Honest Broker system.")
public class HonestBrokerApi extends AbstractXapiRestController {

    private HonestBrokerService honestBrokerService;  // Assume a service class handling domain-related logic

	@Autowired
    protected HonestBrokerApi(UserManagementServiceI userManagementService, RoleHolder roleHolder, HonestBrokerService honestBrokerService) {
		super(userManagementService, roleHolder);
		this.honestBrokerService=honestBrokerService;
	}

    @ApiOperation(value = "Create a new domain", response = DomainDefn.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Domain successfully created"),
        @ApiResponse(code = 409, message = "Domain already exists")
    })
    @PostMapping("/domains")
    @Transactional
    public ResponseEntity<?> createDomain(@RequestBody DomainRequest domainRequest) {
        if (honestBrokerService.domainExists(domainRequest.getDomain())) {
            return ResponseEntity.status(409).body("Domain already exists");
        }
        DomainDefn domain = honestBrokerService.createDomain(domainRequest);
        return ResponseEntity.ok(domain);
    }

    @ApiOperation(value = "List all domains", response = List.class)
    @GetMapping("/domains")
    public ResponseEntity<List<DomainDefn>> listDomains() {
        List<DomainDefn> domains = honestBrokerService.getAllDomains();
        return ResponseEntity.ok(domains);
    }

    @ApiOperation(value = "Get a domain by its name", response = DomainDefn.class)
    @GetMapping("/domains/{domain}")
    public ResponseEntity<?> getDomain(@PathVariable String domain) {
        DomainDefn domainObj = honestBrokerService.getDomain(domain);
        if (domainObj == null) {
            return ResponseEntity.status(404).body("Domain not found");
        }
        return ResponseEntity.ok(domainObj);
    }

    @ApiOperation(value = "Enable or disable a domain")
    @PutMapping("/domains/{domain}/enabled={enabled}")
    public ResponseEntity<?> setDomainEnabled(@PathVariable String domain, @PathVariable boolean enabled) {
        DomainDefn domainObj = honestBrokerService.updateDomainEnabled(domain, enabled);
        if (domainObj == null) {
            return ResponseEntity.status(404).body("Domain not found");
        }
        return ResponseEntity.ok(domainObj);
    }

    @ApiOperation(value = "Toggle Subject Mapping Only for a domain")
    @PutMapping("/domains/{domain}/subjectMappingOnly={subjectMapOnly}")
    public ResponseEntity<?> toggleSubjectMappingOnly(@PathVariable String domain, @PathVariable boolean subjectMapOnly) {
        DomainDefn domainObj = honestBrokerService.updateSubjectMappingOnly(domain, subjectMapOnly);
        if (domainObj == null) {
            return ResponseEntity.status(404).body("Domain not found");
        }
        return ResponseEntity.ok(domainObj);
    }

    @ApiOperation(value = "Delete a domain")
    @DeleteMapping("/domains/{domain}")
    public ResponseEntity<?> deleteDomain(@PathVariable String domain) {
        boolean deleted = honestBrokerService.deleteDomain(domain);
        if (!deleted) {
            return ResponseEntity.status(404).body("Domain not found");
        }
        return ResponseEntity.ok("Domain deleted");
    }

    @ApiOperation(value = "List all domain-project pairings", response = List.class)
    @GetMapping("/domains/{domain}/projects")
    public ResponseEntity<List<String>> listDomainProjects(@PathVariable String domain) {
        List<String> projects = honestBrokerService.getDomainProjects(domain);
        if (projects == null) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(projects);
    }

    @ApiOperation(value = "Pair a project with a domain")
    @PutMapping("/domains/{domain}/projects/{proj}")
    public ResponseEntity<?> pairProjectWithDomain(@PathVariable String domain, @PathVariable String proj) {
        boolean success = honestBrokerService.pairProjectWithDomain(domain, proj);
        if (!success) {
            return ResponseEntity.status(404).body("Domain or project not found");
        }
        return ResponseEntity.ok("Project paired with domain");
    }

    @ApiOperation(value = "Unpair a project from a domain")
    @DeleteMapping("/domains/{domain}/projects/{proj}")
    public ResponseEntity<?> unpairProjectFromDomain(@PathVariable String domain, @PathVariable String proj) {
        boolean success = honestBrokerService.unpairProjectFromDomain(domain, proj);
        if (!success) {
            return ResponseEntity.status(404).body("Domain or project not found");
        }
        return ResponseEntity.ok("Project unpaired from domain");
    }

    @ApiOperation(value = "Upload mappings to a specific domain")
    @PostMapping("/domains/{domain}/mappings")
    public ResponseEntity<?> uploadMappings(@PathVariable String domain, @RequestBody MappingsRequest mappingsRequest) {
        boolean success = honestBrokerService.uploadMappings(domain, mappingsRequest);
        if (!success) {
            return ResponseEntity.status(404).body("Domain not found or invalid mappings");
        }
        return ResponseEntity.ok("Mappings uploaded");
    }

    @ApiOperation(value = "Download mappings from a specific domain")
    @GetMapping("/domains/{domain}/mappings")
    public ResponseEntity<?> downloadMappings(@PathVariable String domain) {
        MappingsResponse mappings = honestBrokerService.getMappings(domain);
        if (mappings == null) {
            return ResponseEntity.status(404).body("Domain not found");
        }
        return ResponseEntity.ok(mappings);
    }	

    @ApiOperation(value = "Download mappings from a specific project")
    @GetMapping("/project/{project}/mappings")
    public ResponseEntity<?> downloadProjectMappings(@PathVariable String project) {
        MappingsResponse mappings = honestBrokerService.getMappingsForProject(project);
        if (mappings == null) {
            return ResponseEntity.status(404).body("Domain mapping not found for project");
        }
        return ResponseEntity.ok(mappings);
    }	
	
}