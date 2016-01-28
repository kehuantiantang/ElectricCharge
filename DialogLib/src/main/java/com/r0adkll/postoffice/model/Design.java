package com.r0adkll.postoffice.model;

/**
 * Project: PostOffice
 * Package: com.r0adkll.postoffice.model
 * Created by drew.heavner on 8/20/14.
 */
public enum Design {
    /**
     * Holo Light Design
     */
    HOLO_LIGHT,

    /**
     * Holo Dark Design
     */
    HOLO_DARK,

    /**
     * Material Design Light
     */
    MATERIAL_LIGHT,

    /**
     * Material Design Dark
     */
    MATERIAL_DARK,

    /**
     * Allow custom UI
     */
    CUSTOM;


    /**
     * Return whether or not this design is material or not
     *
     * @return      true if material design, false otherwise
     */
    public boolean isMaterial(){
        return this == MATERIAL_LIGHT || this == MATERIAL_DARK;
    }

    /**
     * Return whether or not this design is light or dark
     *
     * @return      true if LIGHT theme, false if DARK
     */
    public boolean isLight(){
        return this == MATERIAL_LIGHT || this == HOLO_LIGHT;
    }

}
