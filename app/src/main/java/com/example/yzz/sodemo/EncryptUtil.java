package com.example.yzz.sodemo;


public class EncryptUtil {


//    public static byte[] encrypt(byte[] datas,int random){
//        byte[] xorData = new byte[datas.length];
//        for(int i = 0;i<datas.length;i++){
//            xorData[i] = (byte) (datas[i]^random);
//            xorData[i] = bitReverse(xorData[i]);
//        }
////        return xorData;
//        return datas;
//    }
//
//    public static  byte[] decrypt(byte[] decryptDatas,int random){
//        for(int i = 0;i<decryptDatas.length;i++){
//            decryptDatas[i] = bitReverse(decryptDatas[i]);
//            decryptDatas[i] = (byte) (decryptDatas[i]^random);
//        }
//        return decryptDatas;
//    }


    public static byte bitReverse(byte data){
        byte mask = 1;
        byte reverse = 0;
        for (int j = 0; j < 8; j++) {
            if ((data & mask) != 0)
                reverse |= 1 << (7 - j);
            mask <<= 1;
        }
        return (byte) (reverse&0xff);
    }


    public static byte[] bitReverseInByte(byte[] in_data, int start, int length) {
        byte[] out_data = new byte[in_data.length];
        for (int i = 0; i < in_data.length; i++) {
            int x = 0;
            for (int j = 0; j < 8; j++) {
                x += (((in_data[i] & 255) >>> (7 - j)) & 1) << j;
            }
            out_data[i] = (byte) x;
        }
        return out_data;
    }

    public static byte[] bleWhiteningForRfPacket(byte[] rf_packet, int channel) {
        int i;
        byte[] b = new byte[(rf_packet.length + 13)];
        for (i = 0; i < rf_packet.length; i++) {
            b[i + 13] = rf_packet[i];
        }
        b = bleWhitening(b, channel);
        byte[] out_data = new byte[rf_packet.length];
        for (i = 0; i < rf_packet.length; i++) {
            out_data[i] = b[i + 13];
        }
        return out_data;
    }




    public static byte[] bleWhitening(byte[] paramArrayOfByte, int paramInt){
        int i = 0xFF & (0x1 | (paramInt & 0x20) >>> 4 | (paramInt & 0x10) >>> 2 | (paramInt & 0x8) << 0
                | (paramInt & 0x4) << 2 | (paramInt & 0x2) << 4 | (paramInt & 0x1) << 6);
        byte[] arrayOfByte = new byte[paramArrayOfByte.length];
        for (int j = 0; j < paramArrayOfByte.length; j++)
        {
            int k = 0;
            for (int m = 0; m < 8; m++)
            {
                int n = i & 0xFF;
                int i1 = (n & 0x40) >>> 6;
                k |= (0xFF & paramArrayOfByte[j] ^ i1 << m) & 1 << m;
                int i2 = n << 1;
                int i3 = 0x1 & i2 >>> 7;
                int i4 = i3 | i2 & 0xFFFFFFFE;
                i = i4 & 0xFFFFFFEF | 0x10 & (i4 ^ i3 << 4);
            }
            arrayOfByte[j] = ((byte)k);
        }
        return arrayOfByte;
    }


}
