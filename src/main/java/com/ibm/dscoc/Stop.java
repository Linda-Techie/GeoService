package com.ibm.dscoc;

public class Stop {

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            StartGeoServer.stop(Integer.valueOf(args[0]));
        } else {
            StartGeoServer.stop();
        }

    }
}
