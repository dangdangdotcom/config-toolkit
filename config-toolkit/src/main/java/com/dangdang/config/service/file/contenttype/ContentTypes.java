package com.dangdang.config.service.file.contenttype;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Content type handler class factory
 *
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 */
public final class ContentTypes {

    private Map<String, Class<ContentType>> contentTypes;

    private static ContentTypes INSTANCE = new ContentTypes();

    private static final String REGISTER_FILE = "META-INF/toolkit/com.dangdang.config.service.file.contenttype.ContentType";

    public static ContentTypes getInstance() {
        return INSTANCE;
    }

    private ContentTypes() {
        try {
            Properties props = new Properties();

            // Load register file contents
            Enumeration<URL> registerFiles = this.getClass().getClassLoader().getResources(REGISTER_FILE);
            URL registerFile = null;
            while (registerFiles.hasMoreElements()) {
                registerFile = registerFiles.nextElement();
                try (InputStream in = registerFile.openStream()) {
                    props.load(in);
                }
            }

            // Initialize protocol beans
            contentTypes = new HashMap<>();
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                final String contentTypeName = ((String) entry.getKey()).toLowerCase();
                @SuppressWarnings("unchecked") final Class<ContentType> contentTypeBeanClazz = (Class<ContentType>) Class.forName((String) entry.getValue());
                contentTypes.put(contentTypeName, contentTypeBeanClazz);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Class<ContentType> get(String contentTypeName) {
        final Class<ContentType> contentTypeClass = contentTypes.get(contentTypeName);
        if (contentTypeClass == null) {
            throw new RuntimeException("Content type with name " + contentTypeName + " not registered.");
        }
        return contentTypeClass;
    }

}
