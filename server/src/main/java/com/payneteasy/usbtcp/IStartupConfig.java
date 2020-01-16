package com.payneteasy.usbtcp;

import com.payneteasy.startup.parameters.AStartupParameter;

public interface IStartupConfig {

    @AStartupParameter(name = "TCP_PORT", value = "8090")
    int getTcpPort();

    @AStartupParameter(name = "TCP_READ_TIMEOUT_MS", value = "20000")
    int getTcpReadTimeoutMs();

    @AStartupParameter(name = "USB_READ_TIMEOUT_MS", value = "10000")
    int getUsbReadTimeoutMs();

    @AStartupParameter(name = "USB_WRITE_TIMEOUT_MS", value = "10000")
    int getUsWriteTimeoutMs();

    @AStartupParameter(name = "USB_VENDOR_ID", value = "0x27DD")
    String getVendorIdHex();

    @AStartupParameter(name = "USB_PRODUCT_ID", value = "0x0201")
    String getProductIdHex();

    @AStartupParameter(name = "USB_INTERFACE", value = "0x00")
    String getInterfaceNumberHex();

    @AStartupParameter(name = "USB_ENDPOINT_OUT", value = "0x02")
    String getEndpointOutHex();

    @AStartupParameter(name = "USB_ENDPOINT_IN", value = "0x81")
    String getEndpointInHex();

}
