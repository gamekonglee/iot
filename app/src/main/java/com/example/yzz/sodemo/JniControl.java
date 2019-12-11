package com.example.yzz.sodemo;

public class JniControl {

    public static native String buildpayload( int opcode,            //1Byte opcode
                                                     int group_addr,        //2Byte group_addr(format:0xXXX0)
                                                     int group_index,         //12bit group_index,range[0x0,0xFFF]
                                                     int white,             //1Byte white, range[0x0,0xFF]
                                                     int yellow,            //1Byte yellow,range[0x0,0xFF]
                                                     int count,                 //2Byte count,increase
                                                     int unk1,                 //1Byte
                                                     int unk2,  int round, byte[] a);

   static  {
        System.loadLibrary("JniDemo");//加载lib库文件
    }

}
