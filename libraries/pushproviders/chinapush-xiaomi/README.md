# Xiaomi Push adapter

This optional module integrates the official mainland-China MiPush Android SDK.
It is disabled unless chinapush.xiaomi.enabled=true or
CHINA_PUSH_XIAOMI_ENABLED=true.

Production builds should download the current official MiPush AAR from Xiaomi's
developer portal, verify its SHA-256 out of band, and either place it at
libs/MiPush_SDK_Client_6_0_1-C_3rd.aar or point chinapush.xiaomi.aar /
CHINA_PUSH_XIAOMI_AAR at the verified file.

The chinapush.xiaomi.mavenCoordinate option exists only for reproducible API/CI
compatibility checks. It must not be used as the production SDK source unless
that Maven artifact has been independently reviewed and approved.
