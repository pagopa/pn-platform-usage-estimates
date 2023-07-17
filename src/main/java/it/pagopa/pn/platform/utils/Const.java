package it.pagopa.pn.platform.utils;

public class Const {
    public static final String PN_PLATFORM = "PN_PLATFORM";
    public static final String MONTHLY = "monthlypreorder";
    public static final String SNAPSHOT = "snapshot";
    public static final String LAST = "last";




    public enum OriginType {
        PN_PLATFORM("PN-PLATFORM-NOTIFICATION-FE"),
        PN_HELP_DESK("PN-HELP-DESK-FE");

        private final String value;

        OriginType(String value){
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
