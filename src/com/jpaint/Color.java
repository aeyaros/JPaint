package com.jpaint;

//Color: used for storing color information (aside from the image array itself, which is argb-ints)
public class Color {
    private short[] channels;
    private static final String[] channelNames = { "Opacity", "Red", "Green", "Blue" };
    private static final int NUMBER_OF_VALUES = 256;
    private static final int NUMBER_OF_CHANNELS = 4;
    static final int MAX_VALUE = 255;
    static final float MAX_VALUE_FLOAT = 255.0f;
    static final int MIN_VALUE = 0;


    /*====== CONSTRUCTORS ======*/

    //create a new color
    Color(int a, int r, int g, int b) {
       channels = new short[NUMBER_OF_CHANNELS];
       channels[0] = (short) (a % NUMBER_OF_VALUES);
       channels[1] = (short) (r % NUMBER_OF_VALUES);
       channels[2] = (short) (g % NUMBER_OF_VALUES);
       channels[3] = (short) (b % NUMBER_OF_VALUES);
    }

    //deep copy from another color
    Color(Color oldColor) {
        channels = new short[NUMBER_OF_CHANNELS];
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
    }

    //get java color class object
    public java.awt.Color getAWT() { //argb -> rgba
        return new java.awt.Color(channels[1], channels[2], channels[3], channels[0]);
    }

    //get the alpha, red, green, or blue
    short getChannel(int i) {
        return channels[i % channels.length];
    }

    static String getChannelString(int i) {
        return channelNames[i % channelNames.length];
    }

    //set the channel
    void setChannel(int channelIndex, int newValue) {
        channels[channelIndex % channels.length] = (short) (newValue % NUMBER_OF_VALUES);
    }

    //print out the values of the color to console
    void print() {
        for (short channel : channels) {
            System.out.print(channel + " ");
        } System.out.print("\n");
    }

    /*====== ALPHA BLENDING ======*/

    //based on alpha blend equations from wikipedia
    static int alphaBlend(int src_int, int dst_int) {
        int[] out = new int[NUMBER_OF_CHANNELS];
        int[] src = gcA(src_int);
        int[] dst = gcA(dst_int);

        float src_a_n = src[0] / MAX_VALUE_FLOAT; //normalized source alpha
        int dst_a_int = dst[0]; //destination alpha

        float var1 = (1.0f - src_a_n);//moved out of loop to reduce calculations

        if(dst_a_int == MAX_VALUE) { //if destination is fully opaque
            //https://wikimedia.org/api/rest_v1/media/math/render/svg/c6577b83331abfe6b04b93d1d4c4ccb18b9b8c9a
            out[0] = MAX_VALUE; //output should be opaque

            //loop through rgb values and blend each one
            for(int i = 1; i < out.length; i++) {
                ///// out_rgb = (src_rgb * src_a) + (dst_rgb * 1 - src_a) /////
                out[i] =(int)((src[i] * src_a_n) + (dst[i] * var1));
            }
        } else { //if destination is not fully opaque
            //https://wikimedia.org/api/rest_v1/media/math/render/svg/a92cffa85057fafdd90b31202ce44690958b8cb9
            float dst_a_n = dst_a_int / MAX_VALUE_FLOAT; //normalized destination alpha

            ///// out_a = src_a + dst_a * (1 - src_a) /////
            float out_a_n = (src_a_n + (dst_a_n * (1 - src_a_n))); //normalized output alpha

            out[0] = (int)(out_a_n * MAX_VALUE); //get alpha as int btw 0-255
            if(out[0] == MIN_VALUE) return 0b00000000000000000000000000000000; //if totally transparent, return all 0s

            //if not totally transparent then loop through RGB values and blend each one

            double var2 = dst_a_n * var1; //moved out of loop to reduce calculations

            for(int i = 1; i < out.length; i++) { //3 = rgb.length
                //out_rgb = (src_rgb * src_a) + (dst_rgb * dst_a * 1 - src_a) / out_a
                out[i] =(int) (((src[i] * src_a_n) + (dst[i] * var2)) / out_a_n);
            }
        } //shift values and add them, return new integer
        return ((out[0] << 24) + (out[1] << 16) + (out[2] << 8) + out[3]);
    }

    //alpha blend helper function: return array of channels
    private static int[] gcA(int input) {
        int[] output = new int[4];
        output[0] = input >> 24 & 0b11111111;
        output[1] = input >> 16 & 0b11111111;
        output[2] = input >> 8 & 0b11111111;
        output[3] = input & 0b11111111;
        return output;
    }


}
