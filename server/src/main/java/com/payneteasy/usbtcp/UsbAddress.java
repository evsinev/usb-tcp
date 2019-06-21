package com.payneteasy.usbtcp;

public class UsbAddress {

    private final short vendorId;
    private final short productId;
    private final int interfaceNumber;
    private final byte  endpointIn;
    private final byte endpointOut;

    private UsbAddress(Builder aBuilder){
        vendorId = aBuilder.theVendorId;
        productId = aBuilder.theProductId;
        interfaceNumber = aBuilder.theInterfaceNumber;
        endpointIn = aBuilder.theEndpointIn;
        endpointOut = aBuilder.theEndpointOut;
    }

    public short getVendorId() {
        return vendorId;
    }

    public short getProductId() {
        return productId;
    }

    public int getInterfaceNumber() {
        return interfaceNumber;
    }

    public byte getEndpointIn() {
        return endpointIn;
    }

    public byte getEndpointOut() {
        return endpointOut;
    }

    public static class Builder {

        public Builder vendorId(int aVendorId) {
            theVendorId = (short) aVendorId;
            return this;
        }

        public Builder productId(int aProductId) {
            theProductId = (short) aProductId;
            return this;
        }

        public Builder interfaceNumber(int aInterfaceNumber) {
            theInterfaceNumber = aInterfaceNumber;
            return this;
        }

        public Builder endpointIn(int aEndpointIn) {
            theEndpointIn = (byte) aEndpointIn;
            return this;
        }

        public Builder endpointOut(int aEndpointOut) {
            theEndpointOut = (byte) aEndpointOut;
            return this;
        }

        public UsbAddress build() {
            return new UsbAddress(this);
        }

        private short theVendorId;
        private short theProductId;
        private int   theInterfaceNumber;
        private byte  theEndpointIn;
        private byte  theEndpointOut;

    }
}
