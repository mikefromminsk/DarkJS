package org.pdk.converters;

import org.pdk.store.model.data.FileData;

public interface ConverterParser {
    Object parse(FileData data);
}
