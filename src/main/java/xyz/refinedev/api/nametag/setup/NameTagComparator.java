package xyz.refinedev.api.nametag.setup;

import com.google.common.primitives.Ints;
import xyz.refinedev.api.nametag.adapter.NameTagAdapter;

import java.util.Comparator;

public class NameTagComparator implements Comparator<NameTagAdapter> {

    public int compare(NameTagAdapter a, NameTagAdapter b) {
        return Ints.compare(b.getWeight(), a.getWeight());
    }

}
