package org.pdk.files.converters;

import org.pdk.store.model.data.FileData;

public interface ConverterParser {
    Object parse(FileData data);
}
