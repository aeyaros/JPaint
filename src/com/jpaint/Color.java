package com.jpaint;

//Color: used for storing color information (aside from the image array itself, which is argb-ints)
public class Color {
    private short[] channels;

    /*====== CONSTRUCTORS ======*/

    Color(int a, int r, int g, int b) {
       channels = new short[4];
       channels[0] = (short) (a % 256);
       channels[1] = (short) (r % 256);
       channels[2] = (short) (g % 256);
       channels[3] = (short) (b % 256);
    }

    //deep copy constructor
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

    public static Color alphaBlend(Color src, Color dst) {
        short out_a_s;
        short[] out_rgb = new short[3];
        short[] src_rgb = {src.getChannel(1),src.getChannel(2),src.getChannel(3)};
        short[] dst_rgb = {dst.getChannel(1),dst.getChannel(2),dst.getChannel(3)};
        double src_a = (double)src.getChannel(0) / (double)255;

        if(dst.getChannel(0) == 255) { //destination is opaque
            //https://wikimedia.org/api/rest_v1/media/math/render/svg/c6577b83331abfe6b04b93d1d4c4ccb18b9b8c9a
            out_a_s = 255; //output is opaque

            //loop through rgb values and blend each one
            for(int i = 0; i < out_rgb.length; i++) {
                //out_rgb = (src_rgb * src_a) + (dst_rgb * 1 - src_a)
                out_rgb[i] =(short) ((src_rgb[i] * src_a) + (dst_rgb[i] * ((double) 1 - src_a)));
            }
        } else { //under normal conditions
            //https://wikimedia.org/api/rest_v1/media/math/render/svg/a92cffa85057fafdd90b31202ce44690958b8cb9
            double dst_a = (double)src.getChannel(0) / (double)255; //need dst_a

            //out_a = src_a + dst_a * (1 - src_a)
            double out_a = (src_a + (dst_a * (1 - src_a))); //out alpha
            out_a_s = (short)(out_a * 255); //out alpha as a short btw 1-255
            if(out_a_s == 0) return new Color(0,0,0,0); //if totally transparent, return all 0s

            //loop through RGB values and blend each one
            for(int i = 0; i < out_rgb.length; i++) {
                //out_rgb = (src_rgb * src_a) + (dst_rgb * dst_a * 1 - src_a) / out_a
                out_rgb[i] =(short) (((src_rgb[i] * src_a) + (dst_rgb[i] * dst_a * ((double) 1 - src_a))) / out_a);
            }
        }

        return new Color(out_a_s,out_rgb[0],out_rgb[1],out_rgb[2]);
    }

}
