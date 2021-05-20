package ru.bia.voip.phone.conifg.cucm;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
@Getter
public class JabberConfig {
    //------common jabber settings---------//
    @Value("${cucm.jabber.directory.start}")
    private String cucmNumberStart;
    @Value("${cucm.jabber.directory.end}")
    private String cucmNumberEnd;
    @Value("${cucm.jabber.directory.search-pattern}")
    private String cucmNumberSearchPattern;
    @Value("${cucm.jabber.device-pool}")
    private String devicePoolName;
    @Value("${cucm.jabber.sip-profile}")
    private String sipProfileName;
    @Value("${cucm.jabber.name-max-len}")
    private Integer deviceNameMaxLength;


    //--------desktop settings---------------//
    @Value("${cucm.jabber.desktop.security-profile}")
    private String securityProfileNameDesktop;
    @Value("${cucm.jabber.desktop.product}")
    private String productDesktop;
    @Value("${cucm.jabber.desktop.prefix}")
    private String devicePrefixDesktop;


    //------------line settings------------//
    @Value("${cucm.line.partition}")
    private String partition;
    @Value("${cucm.line.css}")
    private String css;
    @Value("${cucm.line.busy-trigger}")
    private String busyTrigger;
    @Value("${cucm.line.max-num-calls}")
    private String maxNumCalls;
    @Value("${cucm.line.recording-profile-name}")
    private String recordingProfileName;
    @Value("${cucm.line.recording-flag}")
    private String recordingFlag;

    //---------------------------------//


    //--License user
    @Value("${cucm.jabber.use-license-user}")
    private boolean useLicenseUser;
    @Value("${cucm.jabber.license-user}")
    private String licenseUser;
    @Value("${cucm.jabber.pg-user}")
    private String pgUser;

}
