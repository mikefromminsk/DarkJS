package org.pdk.converters;

import org.pdk.storage.model.data.FileData;

public interface ConverterParser {
    Object parse(FileData data);
}
