package com.ib.babhregs.system.filters;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  За да логваме REST Цлиент.
 * За да се ползва трябва:
 --------------------------------------------
  		Client client = ClientBuilder.newClient();
		client.register(new LogClientRequestFilter());
------------------------------------------------
		
 * @author krasig
 *
 */
public class LogClientRequestFilter implements ClientRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogClientRequestFilter.class);

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		LOGGER.info("Rest client invoke:------------------------------");
		if (requestContext.getEntity()!=null) {
			LOGGER.info(requestContext.getEntity().toString());
		}
		LOGGER.info("Url={}",requestContext.getUri().toString());
		
		
	}

}
