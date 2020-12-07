package com.cet.eem.common.util;

import com.cet.eem.annotation.ModelLabel;
import com.cet.eem.metadata.TableInfoHelper;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeBindings;
import org.apache.commons.lang.StringUtils;

import java.lang.annotation.Annotation;
import java.util.List;

public class ModelFieldNamer extends JacksonAnnotationIntrospector implements Versioned {

    @Override
    public Version version() {
        return VersionUtil.versionFor(getClass());
    }

    @Override
    public boolean isAnnotationBundle(Annotation ann) {
        Class<? extends Annotation> aClass = ann.annotationType();
        if (ModelLabel.class == aClass) {
            return true;
        }
        return super.isAnnotationBundle(ann);
    }

    @Override
    public PropertyName findNameForSerialization(Annotated a) {
        PropertyName nameForSerialization = super.findNameForSerialization(a);
        return getPropertyName(a, nameForSerialization);
    }

    @Override
    public PropertyName findNameForDeserialization(Annotated a) {
        PropertyName nameForSerialization = super.findNameForDeserialization(a);
        return getPropertyName(a, nameForSerialization);
    }

    private PropertyName getPropertyName(Annotated a, PropertyName nameForSerialization) {
        if (nameForSerialization == null) {
            JavaType type = a.getType();
            Class<?> rawClass = type.getRawClass();
            if (List.class.isAssignableFrom(rawClass)) {
                TypeBindings bindings = type.getBindings();
                JavaType boundType = bindings.getBoundType(0);
                if (boundType != null) {
                    rawClass = boundType.getRawClass();
                }
            }
            String modelLabel1 = TableInfoHelper.getModelLabel(rawClass);
            if (StringUtils.isNotEmpty(modelLabel1)) {
                return PropertyName.construct(modelLabel1 + "_model");
            }
        }
        return nameForSerialization;
    }

}
