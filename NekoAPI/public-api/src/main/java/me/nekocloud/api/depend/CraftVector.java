package me.nekocloud.api.depend;

import com.comphenix.protocol.wrappers.Vector3F;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CraftVector {

    private final float x;
    private final float y;
    private final float z;

    public Vector3F toVector3f() {
        return new Vector3F(x, y, z);
    }

    public static CraftVector fromVector3f(Vector3F vector3F) {
        return new CraftVector(vector3F.getX(), vector3F.getY(), vector3F.getZ());
    }
}
