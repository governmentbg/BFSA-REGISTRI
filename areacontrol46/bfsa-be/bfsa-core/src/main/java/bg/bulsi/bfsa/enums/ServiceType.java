package bg.bulsi.bfsa.enums;

import lombok.Getter;

@Getter
public enum ServiceType {
    S16(16),
    S502(502),
    S503(503),
    S769(769),
    S7691(7691),
    S7692(7692),
    S7693(7693),
    S7694(7694),
    S7695(7695),
    S7696(7696),
    S1199(1199),
    S1366(1366),
    S1590(1590),
    S1811(1811),
    S2170(2170),
    S2272(2272),
    S2274(2274),
    S2695(2695),
    S2697(2697),
    S2698(2698),
    S2699(2699),
    S2700(2700),
    S2701(2701),
    S2702(2702),
    S2711(2711),
    S2869(2869),
    S3125(3125),
    S3180(3180),
    S3181(3181),
    S3182(3182),
    S3201(3201),
    S3362(3362),
    S3363(3363),
    S3365(3365);

    private final int value;

    ServiceType(int value) {
        this.value = value;
    }

    public static ServiceType getByValue(int value) {
        for (ServiceType type : ServiceType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }

    public static String getServiceName(ServiceType serviceType) {
        return "application" + serviceType.name() + "Service";
    }
}
