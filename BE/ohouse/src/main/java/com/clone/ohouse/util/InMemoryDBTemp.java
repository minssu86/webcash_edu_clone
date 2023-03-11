package com.clone.ohouse.util;


import java.util.HashMap;
import java.util.Map;

public class InMemoryDBTemp {

    public static Map<String, String> checkCodeStorage = new HashMap<>();
    public static Map<String, String> signupTokenStorage = new HashMap<>();
    public static Map<Integer, String> refreshTokenStorage= new HashMap<>();

}
