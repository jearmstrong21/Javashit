package p0nki.pesl.api;

import p0nki.pesl.api.object.MapLikeObject;
import p0nki.pesl.api.object.PESLObject;
import p0nki.pesl.api.object.UndefinedObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PESLContext implements MapLikeObject {

    private final PESLContext parent;
    private final Map<String, PESLObject> objects;
    private PESLObject thisValue = UndefinedObject.INSTANCE;

    public PESLContext(@Nullable PESLContext parent, @Nonnull Map<String, PESLObject> objects) {
        this.parent = parent;
        this.objects = objects;
    }

    @Override
    public @Nonnull
    PESLObject get(String key) throws PESLReferenceException {
        if (objects.containsKey(key)) return objects.get(key);
        if (parent != null) return parent.get(key);
        throw new PESLReferenceException("Reference exception `" + key + "`");
    }

    @Nonnull
    public PESLContext push() {
        return new PESLContext(this, new HashMap<>());
    }

    public void let(@Nonnull String key, @Nonnull PESLObject value) {
        objects.put(key, value);
    }

    @Nullable
    public PESLObject getThis() {
        return thisValue;
    }

    public void setThis(@Nullable PESLObject thisValue) {
        this.thisValue = thisValue;
    }

    @Override
    public void set(@Nonnull String key, @Nonnull PESLObject value) {
        PESLContext ctx = this;
        while (ctx != null && !ctx.keys().contains(key)) {
            ctx = ctx.parent;
        }
        if (ctx == null) {
            objects.put(key, value);
        } else {
            ctx.objects.put(key, value);
        }
    }

    @Override
    public @Nonnull
    Set<String> keys() {
        return Collections.unmodifiableSet(objects.keySet());
    }

}
