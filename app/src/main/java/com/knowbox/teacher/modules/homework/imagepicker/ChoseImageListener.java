package com.knowbox.teacher.modules.homework.imagepicker;

public interface ChoseImageListener {
    public boolean onSelected(ImageBean image);

    public boolean onCancelSelect(ImageBean image);
}
