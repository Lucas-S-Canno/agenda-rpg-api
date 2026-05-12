package com.agendarpgadmin.api.services.utils;

import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;

/**
 * Utility class for generating UUIDs.
 * Uses UUID v7 (time-ordered) for better database performance and global
 * uniqueness.
 */
public final class UuidUtils {

    private UuidUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Generates a new UUID v7.
     * 
     * @return a time-ordered UUID v7
     */
    public static UUID generateV7() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
