package net.querz.mcaselector.filter.filters;

import net.querz.mcaselector.filter.Comparator;
import net.querz.mcaselector.filter.FilterType;
import net.querz.mcaselector.filter.Operator;
import net.querz.mcaselector.filter.TextFilter;
import net.querz.mcaselector.io.mca.ChunkData;
import net.querz.mcaselector.io.registry.StructureRegistry;
import net.querz.mcaselector.validation.ValidationHelper;
import net.querz.mcaselector.version.ChunkFilter;
import net.querz.mcaselector.version.VersionController;
import net.querz.nbt.CompoundTag;
import net.querz.nbt.NBTUtil;
import net.querz.nbt.Tag;
import java.util.*;

public class StructureFilter extends TextFilter<List<String>> {

	public StructureFilter() {
		this(Operator.AND, net.querz.mcaselector.filter.Comparator.CONTAINS, null);
	}

	private StructureFilter(Operator operator, Comparator comparator, List<String> value) {
		super(FilterType.STRUCTURES, operator, comparator, value);
		setRawValue(String.join(",", value == null ? new ArrayList<>(0) : value));
	}

	@Override
	public boolean contains(List<String> value, ChunkData data) {
		if (data.region() == null || data.region().getData() == null) {
			return false;
		}
		ChunkFilter chunkFilter = VersionController.getChunkFilter(data.region().getData().getInt("DataVersion"));
		CompoundTag references = chunkFilter.getStructureReferences(data.region().getData());
		if (references == null) {
			return false;
		}
		for (String name : value) {
			Tag structure = references.get(name);
			if (structure == null || NBTUtil.toSNBT(structure).equals("[]")) {
				structure = references.get(StructureRegistry.getAltName(name));
				if (structure == null || NBTUtil.toSNBT(structure).equals("[]")) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean containsNot(List<String> value, ChunkData data) {
		return !contains(value, data);
	}

	@Override
	public boolean intersects(List<String> value, ChunkData data) {
		if (data.region() == null || data.region().getData() == null) {
			return false;
		}
		ChunkFilter chunkFilter = VersionController.getChunkFilter(data.region().getData().getInt("DataVersion"));
		CompoundTag references = chunkFilter.getStructureReferences(data.region().getData());
		if (references == null) {
			return false;
		}
		for (String name : getFilterValue()) {
			long[] refs = ValidationHelper.silent(() -> references.getLongArray(name), null);
			if (refs != null && refs.length > 0) {
				return true;
			}
			refs = ValidationHelper.silent(() -> references.getLongArray(StructureRegistry.getAltName(name)), null);
			if (refs != null && refs.length > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setFilterValue(String raw) {
		String[] rawStructureNames = raw.replace(" ", "").split(",");
		if (raw.isEmpty() || rawStructureNames.length == 0) {
			setValid(false);
			setValue(null);
		} else {
			for (int i = 0; i < rawStructureNames.length; i++) {
				String name = rawStructureNames[i].toLowerCase();
				if (!StructureRegistry.isValidName(rawStructureNames[i])) {
					if (name.startsWith("'") && name.endsWith("'") && name.length() >= 2 && !name.contains("\"")) {
						rawStructureNames[i] = name.substring(1, name.length() - 1);
						continue;
					}
					setValue(null);
					setValid(false);
					return;
				}
				rawStructureNames[i] = name;
			}
			setValid(true);
			setValue(Arrays.asList(rawStructureNames));
			setRawValue(raw);
		}
	}

	@Override
	public String getFormatText() {
		return "<structure>[,<structure>,...]";
	}

	@Override
	public String toString() {
		return "Structures " + getComparator().getQueryString() + " \"" + getRawValue() + "\"";
	}

	@Override
	public StructureFilter clone() {
	    return new StructureFilter(getOperator(), getComparator(), new ArrayList<>(value));
	}
}