package com.deepdoc.describe;

import com.deepdoc.model.FileInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Descriptions for every file that was asked for — one entry per file, order preserved. */
public final class DescriptionResult {
    private final List<FileDescription> descriptions;
    private final Map<FileInfo, FileDescription> byFile;

    public DescriptionResult(List<FileDescription> descriptions) {
        this.descriptions = Collections.unmodifiableList(new ArrayList<>(descriptions));
        Map<FileInfo, FileDescription> index = new LinkedHashMap<>();
        for (FileDescription d : this.descriptions) {
            index.put(d.getFile(), d);
        }
        this.byFile = Collections.unmodifiableMap(index);
    }

    public List<FileDescription> getDescriptions() {
        return descriptions;
    }

    public FileDescription of(FileInfo file) {
        return byFile.get(file);
    }

    public int size() {
        return descriptions.size();
    }

    public Map<DescriptionOrigin, Long> countsByOrigin() {
        Map<DescriptionOrigin, Long> counts = new EnumMap<>(DescriptionOrigin.class);
        for (DescriptionOrigin origin : DescriptionOrigin.values()) {
            counts.put(origin, 0L);
        }
        for (FileDescription d : descriptions) {
            counts.merge(d.getOrigin(), 1L, Long::sum);
        }
        return counts;
    }
}
