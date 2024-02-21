package com.ib.babhregs.system.filters;

import com.ib.babhregs.rest.common.Secured;
import com.ib.babhregs.utils.JWTUtil;
import com.ib.system.BaseUserData;
import com.ib.system.IBUserPrincipal;
import com.ib.system.auth.TokenCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.credential.Credential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Set;

import static javax.security.enterprise.AuthenticationStatus.SUCCESS;

/**
 * Контролира достъпа до рест услугите
 * Прилага се само за услуги, които са анотирани със @Secured
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);
    @Inject
    private SecurityContext securityContext;
    @Context
    HttpServletRequest httpServletRequest;

    @Context
    HttpServletResponse httpServletResponse;
    private static final String AUTHENTICATION_SCHEME = "Bearer";

//    @Inject
//    private SystemData sd;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        //javax.ws.rs.core.SecurityContext securityContext = requestContext.getSecurityContext();
        LOGGER.info("AuthenticationFilter -- Start");

        // Get the Authorization header from the request
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Validate the Authorization header
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            LOGGER.info("AuthenticationFilter - no token based auhentication:missin {}",AUTHENTICATION_SCHEME);
            abortWithUnauthorized(requestContext);
            return;
        }

        // Extract the token from the Authorization header
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
        LOGGER.info("AuthenticationFilter - token:{}",token);
        try {

            // Validate the token
            validateToken(token);
            LOGGER.info("AuthenticationFilter - We have valid token!!!! Go to load simple  UserData in SecureContext");

            Credential tokenCredential = new TokenCredential(token);
            AuthenticationParameters authParms = AuthenticationParameters.withParams().credential(tokenCredential);

            AuthenticationStatus authenticationStatus = securityContext.authenticate(httpServletRequest, httpServletResponse, authParms);

            if (authenticationStatus==SUCCESS) {
                    LOGGER.info("AuthenticationFilter - authenticationStatus:{}",authenticationStatus.name());
                    Set<IBUserPrincipal> principals = this.securityContext.getPrincipalsByType(IBUserPrincipal.class);
                    BaseUserData userData = principals.iterator().next().getUserData();
                    LOGGER.info("AuthenticationFilter - authenticated userId:{},subject:{},names:{}",userData.getUserId(),userData.getLoginName(),userData.getLiceNames());
            }else {
                LOGGER.info("AuthenticationFilter - authenticationStatus:{}",authenticationStatus.name());
                abortWithUnauthorized(requestContext);
            }


        } catch (Exception e) {
            LOGGER.info("AuthenticationFilter - invalid token:{}",token);
            abortWithUnauthorized(requestContext);
        }

    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {

        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // Authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {

        // Abort the filter chain with a 401 status code
        // The "WWW-Authenticate" is sent along with the response
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME)
                        .build());
    }

    /**
     * Тук единствено проверяваме дали няма да получим EXception. Ако има такъв- значи нещо не е наред и диекно изхвърляме
     * @param token
     * @throws Exception
     */
    private void validateToken(String token) throws Exception {
        new JWTUtil().decodeJWT(token);
    }

}
