package tech.mogami.spring.test.constants;

import lombok.experimental.UtilityClass;

/**
 * Test data.
 */
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class TestData {

    /** Client (buyer) address. */
    public static final String CLIENT_WALLET_ADDRESS_1 = "0x2980bc24bBFB34DE1BBC91479Cb712ffbCE02F73";
    public static final String CLIENT_WALLET_ADDRESS_1_PRIVATE_KEY = "0x9d2675820d55300a05c8991df217a619bcfdc86e2fd91e56443dbbcf159337fd";

    /** Server (seller) address numero 1. */
    public static final String SERVER_WALLET_ADDRESS_1 = "0x7553F6FA4Fb62986b64f79aEFa1fB93ea64A22b1";
    public static final String SERVER_WALLET_ADDRESS_1_PRIVATE_KEY = "0xf4f7e165433421377856179c698aa387bd8f872657977bd8fa6d62604f41773c";

    /** Server (seller) address numero 2. */
    public static final String SERVER_WALLET_ADDRESS_2 = "0x29082D631199d7FD35399378B6522D6042A7Da6C";
    public static final String SERVER_WALLET_ADDRESS_2_PRIVATE_KEY = "0xff47ed3bd116b6040e3469ee54673559745cdeb3c636b869247a5efd68ea1664";

    /** Asset contract address. */
    public static final String ASSET_CONTRACT_ADDRESS = "0x036CbD53842c5426634e7929541eC2318f3dCF7e";

}
