# OPPO Push adapter

This optional module integrates the official HeyTap/OPPO Android Push SDK and
is disabled unless chinapush.oppo.enabled=true or CHINA_PUSH_OPPO_ENABLED=true.

Production builds must use the vendor SDK downloaded from the official OPPO
developer portal. Verify its SHA-256 before use and either place it at
libs/oppo_com.heytap.msp_V3.5.3.aar or point chinapush.oppo.aar /
CHINA_PUSH_OPPO_AAR at the verified AAR.

Matrix notifications use OPPO data/passthrough delivery so Element X can sync
and decrypt locally. OPPO passthrough delivery is permission-gated; do not
enable this provider in production until the application has that capability
and the gateway is configured to send data messages.
