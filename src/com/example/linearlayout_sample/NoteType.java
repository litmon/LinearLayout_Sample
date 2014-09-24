package com.example.linearlayout_sample;

public enum NoteType {
	WHOLE("全音符") {
		@Override
		float getWeight() {
			return 16;
		}

		@Override
		int getResourceId() {
			// TODO
			return 0;
		}
	},
	HALF("二分音符") {
		@Override
		float getWeight() {
			return 8;
		}

		@Override
		int getResourceId() {
			// TODO
			return 0;
		}
	},
	QUARTER("四分音符") {
		@Override
		float getWeight() {
			return 4;
		}

		@Override
		int getResourceId() {
			return R.drawable.note_quarter;
		}
	},
	EIGHTH("八分音符") {
		@Override
		float getWeight() {
			return 2;
		}

		@Override
		int getResourceId() {
			return R.drawable.note_eighth;
		}
	},
	SIXTEENTH("一六分音符") {
		@Override
		float getWeight() {
			return 1;
		}

		@Override
		int getResourceId() {
			// TODO
			return 0;
		}
	};

	String typeString;

	NoteType(String typeString) {
		this.typeString = typeString;
	}

	abstract float getWeight();

	abstract int getResourceId();

	@Override
	public String toString() {
		return typeString;
	}

	public static NoteType valueOf(float weight) {
		for (NoteType type : values()) {
			if (type.getWeight() == weight) {
				return type;
			}
		}
		return null;
	}
}
