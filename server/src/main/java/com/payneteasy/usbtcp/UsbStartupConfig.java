package com.payneteasy.usbtcp;

public class UsbStartupConfig {

    public UsbStartupConfig(IStartupConfig config) {
        this.config = config;
    }

    private final IStartupConfig config;

    int getVendorId         () { return parseHex("USB_VENDOR_ID"    , config.getVendorIdHex        ()); }
    int getProductId        () { return parseHex("USB_PRODUCT_ID"   , config.getProductIdHex       ()); }
    int getInterfaceNumber  () { return parseHex("USB_INTERFACE"    , config.getInterfaceNumberHex ()); }
    int getEndpointOut      () { return parseHex("USB_ENDPOINT_OUT" , config.getEndpointOutHex     ()); }
    int getEndpointIn       () { return parseHex("USB_ENDPOINT_IN"  , config.getEndpointInHex      ()); }

    int parseHex(String aName, String aHex) {
        if(!aHex.startsWith("0x")) {
            throw new IllegalStateException(aName + ": hex value should start with 0x prefix or with 'h' suffix");
        }
        return Integer.parseInt(aHex.replace("0x", "").replace("h", ""), 16);
    }

}
