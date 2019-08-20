package com.template.schema;

import javax.persistence.Converter;


public class AES {


    private static final String ALGO = "AES";
    private byte[] keyValue;

    public AES(String key){
        keyValue = key.getBytes();
    }



}

