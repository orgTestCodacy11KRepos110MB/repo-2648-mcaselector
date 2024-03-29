package net.querz.mcaselector.version.anvil112;

import net.querz.mcaselector.io.mca.ChunkData;
import net.querz.mcaselector.range.Range;
import net.querz.mcaselector.version.EntityFilter;
import net.querz.mcaselector.version.Helper;
import net.querz.nbt.CompoundTag;
import net.querz.nbt.ListTag;
import java.util.List;

public class Anvil112EntityFilter implements EntityFilter {

	@Override
	public void deleteEntities(ChunkData data, List<Range> ranges) {
		ListTag entities = Helper.tagFromLevelFromRoot(data.region().getData(), "Entities", null);
		if (ranges == null) {
			if (entities != null) {
				entities.clear();
			}
		} else {
			for (int i = 0; i < entities.size(); i++) {
				CompoundTag entity = entities.getCompound(i);
				for (Range range : ranges) {
					ListTag entityPos = Helper.tagFromCompound(entity, "Pos");
					if (entityPos != null && entityPos.size() == 3) {
						if (range.contains(entityPos.getInt(1) >> 4)) {
							entities.remove(i);
							i--;
						}
					}
				}
			}
		}
	}

	@Override
	public ListTag getEntities(ChunkData data) {
		return Helper.tagFromLevelFromRoot(data.region().getData(), "Entities", null);
	}
}
