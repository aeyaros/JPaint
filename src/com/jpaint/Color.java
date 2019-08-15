package com.jpaint;

//Color: used for storing color information (aside from the image array itself, which is argb-ints)
public class Color {
    private short[] _channels;

    /*====== CONSTRUCTORS ======*/

    Color(int a, int r, int g, int b) {
       _channels = new short[4];
       _channels[0] = (short) (a % 256);
       _channels[1] = (short) (r % 256);
       _channels[2] = (short) (g % 256);
       _channels[3] = (short) (b % 256);
    }

    //deep copy constructor
    Color(Color oldColor) {
        _channels = new short[4];
        _channels[0] = oldColor.getChannel(0);
        _channels[1] = oldColor.getChannel(1);
        _channels[2] = oldColor.getChannel(2);
        _channels[3] = oldColor.getChannel(3);
    }

    /* These functions that use .SIZE will work as long as integers
    are 32 bits i.e. 4 bytes, each byte containing 8 bits
    This should never change unless Oracle devs decide
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
        _channels = new short[Integer.BYTES];

        //need to access channels in reverse order
        // for example, "a" channel appears last in binArray but should be first in _channels
        int maxIndex = Integer.BYTES - 1;
        //for each channel of 8 bits
        for(int i = 0; i < Integer.BYTES; i++) {
            //construct a number from the bits
            _channels[maxIndex - i] = 0;

            //for each bit in current channel
            for(int j = 0; j < Byte.SIZE; j++) {
                _channels[maxIndex - i] += (binArray[i * Byte.SIZE + j] << j);
            }
        }
    }

    /*====== ACCESSORS ======*/

    //get the ARGB 32-bit integer for the current color
    public int getARGB() {
        int argb = 0; //4 bytes
        for(int i = 0; i < Integer.BYTES; i++) {
            //ith item is shifted left 32 - (8 * 1|2|3|4)
            argb += _channels[i] << (Integer.SIZE - (Byte.SIZE * (i+1)));
        } return argb;
        /*return (_channels[0] << 24) + (_channels[1] << 16) + (_channels[2] << 8) + (_channels[3]);*/
    }

    //get java color class object
    public java.awt.Color getAWT() { //argb -> rgba
        return new java.awt.Color(_channels[1], _channels[2], _channels[3], _channels[0]);
    }

    //get the alpha, red, green, or blue
    public short getChannel(int i) {
        return _channels[i % 4];
    }

    //print out the values of the color to console
    public void print() {
        for (short channel : _channels) {
            System.out.print(channel + " ");
        } System.out.print("\n");
    }

}
