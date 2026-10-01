# vivo Push adapter

This optional module integrates the official vivo Android Push SDK and is
disabled unless chinapush.vivo.enabled=true or CHINA_PUSH_VIVO_ENABLED=true.

Production builds must use the vendor SDK downloaded from the official vivo
developer portal. Verify its SHA-256 before use and either place it at
libs/vpush_clientSDK_v4.0.6.0_506.aar or point chinapush.vivo.aar /
CHINA_PUSH_VIVO_AAR at the verified AAR.
