package com.jpaint;

//Color: used for storing color information (aside from the image array itself, which is argb-ints)
public class Color {
    private short[] channels;

    /*====== CONSTRUCTORS ======*/

    //create a new color
    Color(int a, int r, int g, int b) {
       channels = new short[4];
       channels[0] = (short) (a % 256);
       channels[1] = (short) (r % 256);
       channels[2] = (short) (g % 256);
       channels[3] = (short) (b % 256);
    }

    //deep copy from another color
    Color(Color oldColor) {
        channels = new short[4];
        channels[0] = oldColor.getChannel(0);
        channels[1] = oldColor.getChannel(1);
        channels[2] = oldColor.getChannel(2);
        channels[3] = oldColor.getChannel(3);
    }

    /* These functions that use .SIZE will work as long as integers
    are 32 bits i.e. 4 bytes, each byte containing 8 bits
    This should never change unless Oracle developers decide
    to redefine this in a future version of Java, which
    would probably break way more than this one program
    so I'll assume it's not an issue */

    //create a color object based on an ARGB integer
    Color(int argb) {
        //create array to extract bits from input int
        int[] binArray = new int[Integer.SIZE];
        //array will be structured like this, in reverse of the order of channels
            // bbbbbbbbggggggggrrrrrrrraaaaaaaa
        //basically, bits of integer are reversed
        for(int i = 0; i < Integer.SIZE; i++) {
            binArray[i] = (argb >> i) & 1;
        }

        //4 shorts <-> 4 channels <-> 4 bytes in int
        channels = new short[Integer.BYTES];

        //need to access channels in reverse order
        // for example, "a" channel appears last in binArray but should be first in _channels
        int maxIndex = Integer.BYTES - 1;
        //for each channel of 8 bits
        for(int i = 0; i < Integer.BYTES; i++) {
            //construct a number from the bits
            channels[maxIndex - i] = 0;

            //for each bit in current channel
            for(int j = 0; j < Byte.SIZE; j++) {
                channels[maxIndex - i] += (binArray[i * Byte.SIZE + j] << j);
            }
        }
    }

    /*====== ACCESSORS ======*/

    //get the ARGB 32-bit integer for the current color
    int getARGB() {
        int argb = 0; //4 bytes
        for(int i = 0; i < Integer.BYTES; i++) {
            //ith item is shifted left 32 - (8 * 1|2|3|4)
            argb += channels[i] << (Integer.SIZE - (Byte.SIZE * (i+1)));
        } return argb;
        /*return (_channels[0] << 24) + (_channels[1] << 16) + (_channels[2] << 8) + (_channels[3]);*/
    }

    //get java color class object
    public java.awt.Color getAWT() { //argb -> rgba
        return new java.awt.Color(channels[1], channels[2], channels[3], channels[0]);
    }

    //get the alpha, red, green, or blue
    short getChannel(int i) {
        return channels[i % 4];
    }

    //print out the values of the color to console
    void print() {
        for (short channel : channels) {
            System.out.print(channel + " ");
        } System.out.print("\n");
    }

    private static int gc(int input, int i) {
        switch (i) {
            case 0: return (input >> 24 & 0b11111111);
            case 1: return (input >> 16 & 0b11111111);
            case 2: return (input >>  8 & 0b11111111);
            default: return input & 0b11111111;
        }
    }

    static int alphaBlend(int src_int, int dst_int) {
        int out_a_int; //output alpha
        int src_a_int = gc(src_int,0); //source alpha
        int dst_a_int = gc(dst_int,0); //destination alpha
        int[] out_rgb = new int[3]; //output RGB
        //arrays for source/destination RGB
        int[] src_rgb = {gc(src_int,1), gc(src_int,2), gc(src_int,3) };
        int[] dst_rgb = {gc(dst_int,1), gc(dst_int,2), gc(dst_int,3) };

        //normalized source alpha
        double src_a_n = src_a_int / 255.0d;

        if(dst_a_int == 255) { //if destination is fully opaque
            //https://wikimedia.org/api/rest_v1/media/math/render/svg/c6577b83331abfe6b04b93d1d4c4ccb18b9b8c9a
            out_a_int = 255; //output should be opaque

            //loop through rgb values and blend each one
            for(int i = 0; i < out_rgb.length; i++) {
                ///// out_rgb = (src_rgb * src_a) + (dst_rgb * 1 - src_a) /////
                out_rgb[i] =(int)((src_rgb[i] * src_a_n) + (dst_rgb[i] * (1.0d - src_a_n)));
            }
        } else { //if destination is not fully opaque
            //https://wikimedia.org/api/rest_v1/media/math/render/svg/a92cffa85057fafdd90b31202ce44690958b8cb9
            double dst_a_n = dst_a_int / 255.0d; //normalized destination alpha

            ///// out_a = src_a + dst_a * (1 - src_a) /////
            double out_a_n = (src_a_n + (dst_a_n * (1 - src_a_n))); //normalized output alpha

            out_a_int = (int)(out_a_n * 255); //get alpha as int btw 0-255
            if(out_a_int == 0) return 0b00000000000000000000000000000000; //if totally transparent, return all 0s

            //if not totally transparent then loop through RGB values and blend each one
            for(int i = 0; i < out_rgb.length; i++) {
                //out_rgb = (src_rgb * src_a) + (dst_rgb * dst_a * 1 - src_a) / out_a
                out_rgb[i] =(int) (((src_rgb[i] * src_a_n) + (dst_rgb[i] * dst_a_n * (1.0d - src_a_n))) / out_a_n);
            }
        } //shift values and add them
        return ((out_a_int << 24) + (out_rgb[0] << 16) + (out_rgb[1] << 8) + (out_rgb[2]));
    }

}
