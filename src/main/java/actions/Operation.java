package actions;

import java.lang.reflect.InvocationTargetException;

public interface Operation{
    float[] changeRGB (float[] rgb) throws InvocationTargetException, IllegalAccessException;
}
