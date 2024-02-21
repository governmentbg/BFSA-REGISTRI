package com.ib.babhregs.rest;


import com.ib.babhregs.rest.common.Logged;
import com.ib.babhregs.rest.common.Secured;
import com.ib.babhregs.utils.JWTUtil;
import com.ib.indexui.db.dao.AdmUserDAO;
import com.ib.indexui.system.Constants;
import com.ib.indexui.system.IndexHttpSessionListener;
import com.ib.indexui.utils.ClientInfo;
import com.ib.system.ActiveUser;
import com.ib.system.BaseUserData;
import com.ib.system.IBUserPrincipal;
import com.ib.system.auth.EAuthCredential;
import com.ib.system.auth.RestCredential;
import com.ib.system.db.JPA;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.AuthenticationException;
import com.ib.system.exceptions.BaseException;
import com.ib.system.useractivity.UserActivityData;
import com.ib.system.utils.HTTPUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.credential.Credential;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Set;

import static com.ib.system.SysConstants.CODE_DEIN_LOGIN;
import static com.ib.system.SysConstants.CODE_DEIN_LOGIN_FAILED;

/**
 * Този клас трябва да осигури необходимите услуги за логване с токен
 */
@Path("/auth")
public class LoginByToken {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginByToken.class);

    @Inject
    private SecurityContext securityContext;

//    @Inject
//    SystemData sd;


    /**Тази услуга трябва да предостави логване с токен, като работи по следния начин:
     * <p>
     * Очаква се да има хедър "Authorization" в който да има "Bearer SDFSFsdfsdflkjlkjsdfmsdfsldjflsdkjfsdf..."
     *</p>
     * Ако има и е валиден , се взема от него "sub", където очакваме да стои username
     * По този усернаме намираме потребителя и ако всичко е наред се извлич userData-та му и се зарежда в секюрити контекста
     * 1.Ако всичко е наред, в респонса се връща линк към /pages/index.xml
     * 2.Ако има проблем с усера се хвърля към логин страницата
     * 3.Ако има грешка или изобщо няма токен, не е вълиден или друго, се връща респонс с еррор 500 като се подава IBRestException ot kydeto move da se iz`ete цоррелатионИД + мессаге
     *
     *
     * @param httpRequest
     * @param httpResponse
     * @param headers
     * @param servletContext
     * @return
     */
    @GET
    @Path("/loginByToken")
    @Operation(tags = "Auth",summary = "Логване в системата с токен",description = "Очаква се в \"Authorization\" хедъра да има \"Bearer:...\"")
    public Response loginByToken(@Context HttpServletRequest httpRequest,
                                 @Context HttpServletResponse httpResponse,
                                 @Context HttpHeaders headers,
                                 @Context ServletContext servletContext){

        LOGGER.debug("loginByToken ========== start");
        String successPage ="/pages/index.xhtml";
        String loginPage ="/login.xhtml";
        String userIP = ClientInfo.getClientIpAddr(httpRequest);
        String applicationBaseUrl = HTTPUtils.getApplicationBaseUrl(httpRequest);
        String contextPath = servletContext.getContextPath();
        Response restResponse = null;
        long correlationId=new Date().getTime();
        EntityManager entityManager=null;
        try {
            String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);

            LOGGER.debug("authorizationHeader:{}", authorizationHeader);
            LOGGER.debug("AppBaseURL:{}", applicationBaseUrl);
            LOGGER.debug("Path:{}",contextPath);
            LOGGER.debug("userIp:{}",userIP);
            String authToken = HTTPUtils.getBearerToken(authorizationHeader);


            if (authToken==null){

                throw new AuthenticationException("There is no token when someone trying to login",-1);
            }

            LOGGER.debug("token:{}",authToken);
            Jws<Claims> claimsJws;
            try {
                claimsJws = new JWTUtil().decodeJWT(authToken);
            }catch (Exception e){
                //   LOGGER.error(e.getMessage(),e);
                throw new AuthenticationException(e.getMessage(),-1);
            }

            String userName = claimsJws.getPayload().getSubject();
            LOGGER.info("loginName:{}", userName);

            entityManager = JPA.getUtil().getEntityManager();
//            entityManager.
            Query query = entityManager.createNativeQuery("select user_id from adm_users where USERNAME=:loginName");
            query.setParameter("loginName", userName);
            Number userId = (Number) query.getSingleResult();
            if (userId==null){
                LOGGER.error("User with name \"{}\" not found",userName);
                throw new AuthenticationException(AuthenticationException.CODE_USER_UNKNOWN,-1);
            }
            LOGGER.info("iserId:{}",userId);
            Credential userCredentials= new EAuthCredential(userId.intValue());
            AuthenticationParameters authParms = AuthenticationParameters.withParams().credential(userCredentials);
            AuthenticationStatus authenticationStatus = securityContext.authenticate(httpRequest, httpResponse, authParms);

            switch (authenticationStatus) {
                case SEND_CONTINUE:
                    LOGGER.info("SEND_CONTINUE: GoTo:{}",applicationBaseUrl + successPage);
                    restResponse = redirect(applicationBaseUrl + successPage);
                    break;
                case SEND_FAILURE:
                    LOGGER.info("SEND_FAILURE: GoTo:{}",applicationBaseUrl + loginPage);
//                    logOperation(CODE_EAUTH_FAIL, 8L, -1L,  Long.valueOf( userCredentialsEAuth.getUserId() == null ? -1L :  userCredentialsEAuth.getUserId()), null, "еАвтентикация2.0, e-mail:" + userInfo.getEmail());
                    restResponse = redirect(applicationBaseUrl + loginPage);
                    break;
                case SUCCESS:
                    LOGGER.info("SUCCESS: GoTo:{}",applicationBaseUrl + successPage);
                    Set<IBUserPrincipal> principals = this.securityContext.getPrincipalsByType(IBUserPrincipal.class);
                    BaseUserData userData = principals.iterator().next().getUserData();
                    journalLoginSuccess(httpRequest,userIP,userData);
                    restResponse = redirect(applicationBaseUrl + successPage);
                    //  return Response.seeOther(new URL("http://localhost:8080/BABHRegs/pages/dashboard.xhtml").toURI()).build();
//                    setUActiveUsersList();
                    break;
                case NOT_DONE:
                    LOGGER.debug("NOT_DONE: GoTo:{}",applicationBaseUrl + loginPage);
                    restResponse = redirect(applicationBaseUrl + loginPage);
                    break;

            }




//           return Response.status(Response.Status.SEE_OTHER).entity("http://dir.bg").build();
//           return Response.status(Response.Status.SEE_OTHER).location(new URL("http://google.com").toURI()).build();
            //return Response.seeOther(new URL("http://localhost:8080/BABHRegs/pages/dashboard.xhtml").toURI()).build();
//            return Response.temporaryRedirect(new URL("http://localhost:8080").toURI()).build();
        } catch (URISyntaxException | MalformedURLException | AuthenticationException e) {
            LOGGER.error("CorrelationID:{}",correlationId,e);
            journalLoginFail("Error login with token", httpRequest, userIP, new AuthenticationException("",-1));
            throw new IBRestException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),correlationId,"Error logging site content!",e);
        } catch(Exception e){
            LOGGER.error("CorrelationID:{}",correlationId,e);
            journalLoginFail("Error login with token", httpRequest, userIP, new AuthenticationException("",-1));
            throw new IBRestException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),correlationId,"Error logging site content!",e);
//            e.printStackTrace();
        }finally {
            if (entityManager != null) {
                if (entityManager.isOpen()) {
                    entityManager.close();
                }
            }
        }


        return restResponse;
    }


    /**
     * @return Логване на потребител . Връща токен!!!
     */
    @GET
//    @Secured
    @Logged
    @Path("/loginMeIn")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(tags = "Auth", summary = "Логване в системата с izdawane na token", description = "Очаква се в \"Authorization\" хедъра да има \"Bearer:...\"")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content =
                    {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid parameter supplied"),
            @ApiResponse(responseCode = "401", description = "Invalid user/pass supplied (User Not found"),
            @ApiResponse(responseCode = "500", description = "Some kind of internal error", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = com.ib.babhregs.rest.IBRestException.class))})

    })
    public Response loginMeIn(@Context HttpServletRequest httpRequest,
                              @Context HttpServletResponse httpResponse,
                              @Context HttpHeaders headers,
                              @Context ServletContext servletContext,
                              @NotNull(message="Missing \"username\" parameter") @QueryParam("username") String username,@NotNull @QueryParam("password") String password){
        LOGGER.debug("loginMeIn: username={}, password={}",username,password);

        String AUTHENTICATION_SCHEME = "Bearer";
        Response restResponse = null;
        String userIP = ClientInfo.getClientIpAddr(httpRequest);
        long correlationId=new Date().getTime();

        try {

            Credential userCredentials = new RestCredential(username, password);
            AuthenticationParameters authParms = AuthenticationParameters.withParams().credential(userCredentials);
            AuthenticationStatus authenticationStatus = securityContext.authenticate(httpRequest, httpResponse, authParms);

            switch (authenticationStatus) {
                case SEND_CONTINUE:
                    LOGGER.info("SEND_CONTINUE: GoTo:");
                    restResponse = Response.status(Response.Status.UNAUTHORIZED)
                            .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME)
                            .build();
                    break;
                case SEND_FAILURE:
                    LOGGER.info("SEND_FAILURE: GoTo:");
//                    logOperation(CODE_EAUTH_FAIL, 8L, -1L,  Long.valueOf( userCredentialsEAuth.getUserId() == null ? -1L :  userCredentialsEAuth.getUserId()), null, "еАвтентикация2.0, e-mail:" + userInfo.getEmail());
                    restResponse = Response.status(Response.Status.UNAUTHORIZED)
                            .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME)
                            .build();
                    break;
                case SUCCESS:
                    LOGGER.info("SUCCESS: GoTo:");
                    Set<IBUserPrincipal> principals = this.securityContext.getPrincipalsByType(IBUserPrincipal.class);
                    BaseUserData userData = principals.iterator().next().getUserData();
                    journalLoginSuccess(httpRequest, userIP, userData);
                    String token;
                    JWTUtil jwtUtil = new JWTUtil(
                            "babhregs",
                            username,
                            "babhregs.com",
                            new Date(),
                            new Date(),
                            null,
                            30,
                            null

                    );
                    jwtUtil.getClaims().put("userId",userData.getUserId());
                    jwtUtil.getClaims().put("names",userData.getLiceNames());
                    token=jwtUtil.generateJWT();
                    restResponse = Response.ok(token).build();
                    //  return Response.seeOther(new URL("http://localhost:8080/BABHRegs/pages/dashboard.xhtml").toURI()).build();
//                    setUActiveUsersList();
                    break;
                case NOT_DONE:
                    LOGGER.debug("NOT_DONE: GoTo:");
                    restResponse = Response.status(Response.Status.UNAUTHORIZED)
                            .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME)
                            .build();
                    break;

            }
        }catch (AuthenticationException e){
            restResponse = Response.status(Response.Status.UNAUTHORIZED)
                    .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME)
                    .build();
        } catch (Exception e) {
            LOGGER.error("CorrelationID:{}", correlationId, e);
            journalLoginFail("Error login with token", httpRequest, userIP, new AuthenticationException("", -1));
            throw new IBRestException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), correlationId, "Error logging site content!", e);
        }
        return restResponse;

    }


    @GET
    @Path("/testUnsecuredService")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(tags = "Auth",summary = "Тостова услуга за достъп")
    public Response myUnsecuredMethod() {
        return Response.ok("Success with no valid token").build();
    }

    @Secured
    @GET
    @Path("/testSecuredService")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(tags = "Auth",summary = "Тостова услуга за достъп")
    public Response mySecuredMethod() {
        Set<IBUserPrincipal> principals = securityContext.getPrincipalsByType(IBUserPrincipal.class);
        BaseUserData userData = principals.iterator().next().getUserData();

        return Response.ok("Success with only with valid token.UserID:"+userData.getUserId()+",username:"+userData.getLoginName()).build();
    }




    private Response redirect(String url) throws URISyntaxException, MalformedURLException {
        return Response.seeOther(new URL(url).toURI()).build();
    }

    private Response abortWithUnauthorized() throws MalformedURLException, URISyntaxException {

        return Response.seeOther(new URL("http://localhost:8080/BABHRegs/login.xhtml?tokenLoginError=1").toURI()).build();
        // Abort the filter chain with a 401 status code
        // The "WWW-Authenticate" is sent along with the response
//        requestContext.abortWith(
//                Response.status(Response.Status.UNAUTHORIZED)
//                        .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME)
//                        .build());
    }

    /**
     * Запис на успешно логване с токен в журнала
     * Метода е копиран с дребна промяна от IndexLoginBean
     *
     * @param request
     * @param userIP
     * @param userData
     */
    private void journalLoginSuccess(HttpServletRequest request, String userIP, BaseUserData userData) {
        String sessionId = "Token XXXXX";//((HttpSession) JSFUtils.getExternalContext().getSession(false)).getId();
        String clientBrowser = ClientInfo.getClientBrowser(request);
        String clientOS = ClientInfo.getClientOS(request);

        if (IndexHttpSessionListener.isActive()) { // за да се запази за екрана активни потребители
            UserActivityData u = new UserActivityData(userData.getUserId(), userIP, sessionId, clientBrowser, clientOS);

//            sd.getActiveUsers().add(u);
        }

        // журналирането на логина
        String identObject = "Username=" + userData.getLoginName() + "; IP=" + userIP + "; Browser=" + clientBrowser + "; OS=" + clientOS + ";\r\nSESSID=" + sessionId;
        SystemJournal journal = new SystemJournal(userData.getUserId(), CODE_DEIN_LOGIN, Constants.CODE_ZNACHENIE_JOURNAL_USER, userData.getUserId(), identObject, null);

        AdmUserDAO dao = new AdmUserDAO(userData);
        try{
            JPA.getUtil().runInTransaction(() -> dao.saveAudit(journal));

        } catch (BaseException e) {
            LOGGER.error("Грешка при журналиране на Вход в системата", e);
        }
    }


    /**Запис в журнал-а на неуспешно логване
     * @param errorMessage
     * @param request
     * @param userIP
     * @param ae
     */
    private void journalLoginFail(String errorMessage, HttpServletRequest request, String userIP, AuthenticationException ae) {
        String sessionId = "Token YYYYY";//((HttpSession) JSFUtils.getExternalContext().getSession(false)).getId();
        String clientBrowser = ClientInfo.getClientBrowser(request);
        String clientOS = ClientInfo.getClientOS(request);

        String identObject = errorMessage + "\r\nIP=" + userIP + "; Browser=" + clientBrowser + "; OS=" + clientOS + "; SESSID=" + sessionId;

        final SystemJournal journal;
        if (ae.getUserId() != null) { // знае се кой не е успял да влезне

            journal = new SystemJournal(ae.getUserId(), CODE_DEIN_LOGIN_FAILED, Constants.CODE_ZNACHENIE_JOURNAL_USER, ae.getUserId(), identObject, null);

        } else { // не е намерен потребител по потребителското име

            journal = new SystemJournal(ActiveUser.DEFAULT.getUserId(), CODE_DEIN_LOGIN_FAILED, Constants.CODE_ZNACHENIE_JOURNAL_USER, null, identObject, null);
        }

        AdmUserDAO dao = new AdmUserDAO(ActiveUser.of(journal.getIdUser()));
        try {

            JPA.getUtil().runInTransaction(() -> dao.saveAudit(journal));


        } catch (BaseException e) {
            LOGGER.error("Грешка при журналиране на Неуспешен вход в системата", e);
        }
    }
}
