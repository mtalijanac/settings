package hr.ispcard.tools.settings.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class StringMapFactoryBeanTest {

    @Test
    public void testMap() {
        String value = "ALB1 Lynx_notification_ALB@mercury-processing.com,lynx.monitoring@mercury-processing.com \n"
                     + "BIH1 Lynx_notification_BIH@mercury-processing.com,lynx.monitoring@mercury-processing.com \n"
                     + "DEU1 Lynx_notification_DEU@mercury-processing.com,lynx.monitoring@mercury-processing.com \n"
                     + "EGY1 Lynx_notification_EGY@mercury-processing.com,lynx.monitoring@mercury-processing.com \n"
                     + "HRV1 Lynx_notification_HRV@mercury-processing.com,lynx.monitoring@mercury-processing.com \n"
                     + "HUN1 Lynx_notification_HUN@mercury-processing.com,lynx.monitoring@mercury-processing.com \n"
                     + "IPB1 Lynx_notification_IPB@mercury-processing.com,lynx.monitoring@mercury-processing.com \n"
                     + "ROM1 Lynx_notification_ROM@mercury-processing.com,lynx.monitoring@mercury-processing.com \n"
                     + "SRB1 Lynx_notification_SRB@mercury-processing.com,lynx.monitoring@mercury-processing.com \n"
                     + "SVK1 Lynx_notification_SVK@mercury-processing.com,lynx.monitoring@mercury-processing.com \n"
                     + "SVN1 Lynx_notification_SVN@mercury-processing.com,lynx.monitoring@mercury-processing.com";

        StringMapFactoryBean fmb = new StringMapFactoryBean();
        fmb.setValue(value);
        fmb.setSeperatorChars(" ");

        Map<String, String> map = fmb.getObject();

        Assert.assertEquals(11, map.size());

        HashSet<String> expectedKeys = new HashSet<String>();
        expectedKeys.addAll(Arrays.asList("ALB1", "BIH1", "DEU1", "EGY1", "HRV1", "HUN1"));
        expectedKeys.addAll(Arrays.asList("IPB1", "ROM1", "SRB1", "SVK1", "SVN1"));

        Assert.assertEquals(expectedKeys, map.keySet());
    }
}
