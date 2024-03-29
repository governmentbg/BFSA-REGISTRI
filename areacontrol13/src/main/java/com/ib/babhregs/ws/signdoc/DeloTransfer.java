package com.ib.babhregs.ws.signdoc;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.3.3
 * 2020-07-17T13:04:22.189+03:00
 * Generated source version: 3.3.3
 *
 */
@WebService(targetNamespace = "http://wstransfer.delo.indexbg.com/", name = "DeloTransfer")

public interface DeloTransfer {

    @WebMethod
    @RequestWrapper(localName = "getServiceVersion", targetNamespace = "http://wstransfer.delo.indexbg.com/", className = "com.ib.babhregs.ws.signdoc.GetServiceVersion")
    @ResponseWrapper(localName = "getServiceVersionResponse", targetNamespace = "http://wstransfer.delo.indexbg.com/", className = "com.ib.babhregs.ws.signdoc.GetServiceVersionResponse")
    @WebResult(name = "return", targetNamespace = "")
    public int getServiceVersion()
;

    @WebMethod
    @RequestWrapper(localName = "uploadFile", targetNamespace = "http://wstransfer.delo.indexbg.com/", className = "com.ib.babhregs.ws.signdoc.UploadFile")
    @ResponseWrapper(localName = "uploadFileResponse", targetNamespace = "http://wstransfer.delo.indexbg.com/", className = "com.ib.babhregs.ws.signdoc.UploadFileResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.lang.String uploadFile(

        @WebParam(name = "fileContent", targetNamespace = "")
        byte[] fileContent,
        @WebParam(name = "fileName", targetNamespace = "")
        java.lang.String fileName
    ) throws WSFault;
}
