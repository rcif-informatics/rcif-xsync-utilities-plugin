package org.rcif.xsync.utilities.config;

import org.nrg.framework.annotations.XnatPlugin;
import org.springframework.context.annotation.ComponentScan;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class RcifXsyncUtilitiesPlugin.
 */
@Slf4j
@XnatPlugin(
				value = "rcifXsyncUtilitiesPlugin",
				entityPackages = "org.rcif.xsync.utilities.entities",
				name = "RCIF XSync Utilities Plugin"
			)
@ComponentScan({
	"org.rcif.xsync.utilities.generators",
	"org.rcif.xsync.utilities.entities",
	"org.rcif.xsync.utilities.dao",
	"org.rcif.xsync.utilities.service",
	"org.rcif.xsync.utilities.xapi"
	})
public class RcifXsyncUtilitiesPlugin {
	
	/**
	 * Instantiates a new RCIF Xsync Utilities plugin.
	 */
	public RcifXsyncUtilitiesPlugin() {
		log.info("Configuring RCIF XSync Utilities plugin");
	}
}
