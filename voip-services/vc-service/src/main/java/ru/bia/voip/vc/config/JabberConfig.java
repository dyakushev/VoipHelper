package ru.bia.voip.vc.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.bia.voip.vc.model.jabber.JabberType;

@Component
@Getter
public class JabberConfig {
    //------common jabber settings---------//
    @Value("${cucm.directory.start}")
    private String cucmNumberStart;
    @Value("${cucm.directory.end}")
    private String cucmNumberEnd;
    @Value("${cucm.jabber.devicepool}")
    private String devicePoolName;
    @Value("${cucm.jabber.sipprofile}")
    private String sipProfileName;
    @Value("${cucm.jabber.mrgl}")
    private String mrglName;
    @Value("${cucm.jabber.namemaxlen}")
    private Integer deviceNameMaxLength;


    //--------desktop settings---------------//
    @Value("${cucm.jabber.desktop.securityprofile}")
    private String securityProfileNameDesktop;
    @Value("${cucm.jabber.desktop.product}")
    private String productDesktop;
    @Value("${cucm.jabber.desktop.prefix}")
    private String devicePrefixDesktop;

    //--------android settings---------------//
    @Value("${cucm.jabber.android.securityprofile}")
    private String securityProfileNameAndroid;
    @Value("${cucm.jabber.android.product}")
    private String productAndroid;
    @Value("${cucm.jabber.android.prefix}")
    private String devicePrefixAndroid;

    //--------iphone settings---------------//
    @Value("${cucm.jabber.iphone.securityprofile}")
    private String securityProfileNameIphone;
    @Value("${cucm.jabber.iphone.product}")
    private String productIphone;
    @Value("${cucm.jabber.iphone.prefix}")
    private String devicePrefixIphone;

    //--------tablet settings---------------//
    @Value("${cucm.jabber.tablet.securityprofile}")
    private String securityProfileNameTablet;
    @Value("${cucm.jabber.tablet.product}")
    private String productTablet;
    @Value("${cucm.jabber.tablet.prefix}")
    private String devicePrefixTablet;
    //---------------------------------//

    //------------user settings------------//
    @Value("${cucm.user.ucserviceprofile}")
    private String ucServiceProfile;
    @Value("${cucm.user.groups}")
    private String[] userGroups;
    //---------------------------------//
    //------------line settings------------//
    @Value("${cucm.line.partition}")
    private String partition;
    @Value("${cucm.line.css}")
    private String css;
    //---------------------------------//

    private String product;
    private String devicePrefix;
    private String securityProfileName;

    //--License user
    @Value("${cucm.jabber.useLicenseUser}")
    private boolean useLicenseUser;
    @Value("${cucm.jabber.licenseUser}")
    private String licenseUser;


    public String getProduct(JabberType jabberType) {
        switch (jabberType) {
            case DESKTOP:
                return productDesktop;
            case TABLET:
                return productTablet;
            case IPHONE:
                return productIphone;
            case ANDROID:
                return productAndroid;
        }
        return null;
    }

    public String getDevicePrefix(JabberType jabberType) {
        switch (jabberType) {
            case DESKTOP:
                return devicePrefixDesktop;
            case TABLET:
                return devicePrefixTablet;
            case IPHONE:
                return devicePrefixIphone;
            case ANDROID:
                return devicePrefixAndroid;
        }
        return null;
    }

    public String getSecurityProfileName(JabberType jabberType) {
        switch (jabberType) {
            case DESKTOP:
                return securityProfileNameDesktop;
            case TABLET:
                return securityProfileNameTablet;
            case IPHONE:
                return securityProfileNameIphone;
            case ANDROID:
                return securityProfileNameAndroid;
        }
        return null;
    }
}
