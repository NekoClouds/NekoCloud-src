package me.nekocloud.core.api.chat.serializer;

import com.google.gson.*;
import me.nekocloud.core.api.chat.component.BaseComponent;
import me.nekocloud.core.api.chat.component.TranslatableComponent;

import java.lang.reflect.Type;
import java.util.Arrays;

public class TranslatableComponentSerializer extends BaseComponentSerializer implements JsonSerializer<TranslatableComponent>, JsonDeserializer<TranslatableComponent>
{

    @Override
    public TranslatableComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        TranslatableComponent component = new TranslatableComponent();
        JsonObject object = json.getAsJsonObject();
        deserialize( object, component, context );
        component.setTranslate( object.get( "translate" ).getAsString() );
        if ( object.has( "with" ) )
        {
            component.setWith( Arrays.asList(context.deserialize( object.get( "with" ), BaseComponent[].class )) );
        }
        return component;
    }

    @Override
    public JsonElement serialize(TranslatableComponent src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject object = new JsonObject();
        serialize( object, src, context );
        object.addProperty( "translate", src.getTranslate() );
        if ( src.getWith() != null )
        {
            object.add( "with", context.serialize( src.getWith() ) );
        }
        return object;
    }
}
