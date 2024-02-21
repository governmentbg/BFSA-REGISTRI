package bg.bulsi.bfsa.dto.eforms;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.ServiceRequest;

public class ServiceRequestWrapper {
    @JsonProperty("ServiceRequest")
    private ServiceRequest serviceRequest;

    public ServiceRequest getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(ServiceRequest value) {
        this.serviceRequest = value;
    }
}
