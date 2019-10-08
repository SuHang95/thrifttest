package crypto;

import io.netty.buffer.ByteBuf;
import sun.misc.Unsafe;

import java.util.Random;

public class Rc4 {

    public static void main(String[] args) {
        Rc4 rc4 = new Rc4();

        byte[] plainText=new byte[0x40000000];
        new Random().nextBytes(plainText);

        byte[] testText=plainText.clone();

        String key = "我喜欢这个字符";


        {
            long start=System.currentTimeMillis();
            for(int i=0;i<10;i++){
                 rc4.encrypt(testText, key.getBytes());
                 rc4.encrypt(testText, key.getBytes());
            }

            long end=System.currentTimeMillis();

            System.out.println("Method1 Use "+(end-start)+" millis!");
        }

        System.out.println(plainText.equals(testText));

    }

    //加密
    public void encrypt(final ByteBuf text, final byte[] key) {
        int[] S = new int[256]; // S盒
        //待加密字节流的长度
        int len=text.readableBytes();
        byte[] keySchedul = new byte[len]; // 生成的密钥流

        ksa(S, key);
        rpga(S, keySchedul, text.readableBytes());

        int readerIndex=text.readerIndex();

        if(text.hasMemoryAddress()){
            Unsafe unsafe=Unsafe.getUnsafe();
            long addr=text.memoryAddress()+readerIndex;

            for (int i=0;i<len;i++){
                byte k=(byte) (unsafe.getByte(addr+i) ^ keySchedul[i]);
                unsafe.putByte(addr+i,k);
            }
        }else if(text.hasArray()){
            byte[] array = text.array();
            for (int i=0;i<len;i++){
                array[i+readerIndex] ^= keySchedul[i];
            }
        }else {
            for (int i = 0; i < len; i++) {
                byte k=(byte) (text.getByte(i+readerIndex) ^ keySchedul[i]);
                text.setByte(i+readerIndex,k);
            }
        }
    }

    public void encrypt(final ByteBuf text, final byte[] key,byte[] keySchedul){
        if(keySchedul.length<text.readableBytes()){
            keySchedul=new byte[text.readableBytes()];
        }

        int[] S = new int[256]; // S盒
        //待加密字节流的长度
        int len=text.readableBytes();

        ksa(S, key);
        rpga(S, keySchedul, text.readableBytes());

        int readerIndex=text.readerIndex();

        if(text.hasMemoryAddress()){
            Unsafe unsafe=Unsafe.getUnsafe();
            long addr=text.memoryAddress()+readerIndex;

            for (int i=0;i<len;i++){
                byte k=(byte) (unsafe.getByte(addr+i) ^ keySchedul[i]);
                unsafe.putByte(addr+i,k);
            }
        }else if(text.hasArray()){
            byte[] array = text.array();
            for (int i=0;i<len;i++){
                array[i+readerIndex] ^= keySchedul[i];
            }
        }else {
            for (int i = 0; i < len; i++) {
                byte k=(byte) (text.getByte(i+readerIndex) ^ keySchedul[i]);
                text.setByte(i+readerIndex,k);
            }
        }
    }

    public void encrypt(final byte[] text, final byte[] key,byte[] keySchedul){
        int[] S = new int[256]; // S盒

        if(keySchedul.length<text.length) {
            keySchedul = new byte[text.length]; // 生成的密钥流
        }

        ksa(S, key);
        rpga(S, keySchedul, text.length);

        for (int i = 0; i < text.length; ++i) {
            text[i]^=keySchedul[i];
        }
    }



    //加密
    public void encrypt(final byte[] text, final byte[] key) {
        int[] S = new int[256]; // S盒
        byte[] keySchedul = new byte[text.length]; // 生成的密钥流

        ksa(S, key);
        rpga(S, keySchedul, text.length);

        for (int i = 0; i < text.length; ++i) {
            text[i]^=keySchedul[i];
        }
    }

    // 1.1 KSA--密钥调度算法--利用key来对S盒做一个置换，也就是对S盒重新排列
    private void ksa(int[] s, byte[] key) {
        for (int i = 0; i < 256; ++i) {
            s[i] = i;
        }

        int j = 0;
        for (int i = 0; i < 256; ++i) {
            j = (j + s[i] + (key[i % key.length]&0xff)) % 256;
            swap(s, i, j);
        }
    }

    // 1.2 RPGA--伪随机生成算法--利用上面重新排列的S盒来产生任意长度的密钥流
    private void rpga(int[] s, byte[] keySchedul, int plaintextLength) {
        int i = 0, j = 0;
        for (int k = 0; k < plaintextLength; ++k) {
            i = (i + 1) % 256;
            j = (j + s[i]) % 256;
            swap(s, i, j);
            keySchedul[k] = (byte) (s[(s[i] + s[j]) % 256]);
        }
    }

    // 1.3 置换
    private void swap(int[] s, int i, int j) {
        int mTemp = s[i];
        s[i] = s[j];
        s[j] = mTemp;
    }
}
