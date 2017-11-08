package com.mygdx.polyplot;

public interface MapObject
{
    boolean isMouseOvered();
    boolean isSelected();
    void setSelected(boolean selected);
    Properties getProperties();
}
