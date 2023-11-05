package com.comp90018.uninooks.models.location.resource;

public enum ResourceType {

    TOILET,
    ATM,
    CAR_PARK,
    KITCHEN,
    VENDING_MACHINE,
    STOP1,

    EMERGENCY_PHONES,
    AED,
    MICROWAVE_OVEN;


    public static ResourceType toType(String resourceTypeFromSQL) {
        String returnString = "";
        String[] words = resourceTypeFromSQL.split(" ");

        for (int i=0 ; i < words.length ; i++) {
            returnString += words[i].toUpperCase();
            if (i != words.length - 1) {
                returnString = returnString + "_";
            }
        }

        for (ResourceType type : ResourceType.values()) {
            if (type.toString().equals(returnString)) {
                return type;
            }
        }
        return null;
    }
}
