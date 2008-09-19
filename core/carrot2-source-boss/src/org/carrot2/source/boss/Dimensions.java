package org.carrot2.source.boss;


/**
 * Preferred image size for {@link BossImageSearchService#dimensions}.
 */
public enum Dimensions
{
    ALL("all"), 
    SMALL("small"), 
    MEDIUM("medium"), 
    LARGE("large"), 
    WALLPAPER("wallpaper"), 
    WIDE_WALLPAPER("widewallpaper");

    String parameterValue;

    Dimensions(String value)
    {
        this.parameterValue = value;
    }
}