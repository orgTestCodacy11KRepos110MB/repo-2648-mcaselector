package net.querz.mcaselector.version.anvil112;

import net.querz.mcaselector.range.Range;
import net.querz.mcaselector.version.ChunkMerger;
import net.querz.nbt.tag.CompoundTag;
import java.util.List;

public class Anvil112PoiMerger implements ChunkMerger {

	@Override
	public void mergeChunks(CompoundTag source, CompoundTag destination, List<Range> ranges, int yOffset) {
		// nothing to do until 1.14
	}
}