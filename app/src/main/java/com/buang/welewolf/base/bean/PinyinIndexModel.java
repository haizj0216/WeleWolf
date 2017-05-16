package com.buang.welewolf.base.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class PinyinIndexModel implements Parcelable {

	private String id;
	private String name;
	private String pinyin;
	private String shortPinyin;
	private String firstLetter;

	public PinyinIndexModel(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getShortPinyin() {
		return shortPinyin;
	}

	public void setShortPinyin(String shortPinyin) {
		this.shortPinyin = shortPinyin;
	}

	public String getFirstLetter() {
		return firstLetter;
	}

	public void setFirstLetter(String firstLetter) {
		this.firstLetter = firstLetter;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(pinyin);
		dest.writeString(shortPinyin);
		dest.writeString(firstLetter);
	}

	public static final Parcelable.Creator<PinyinIndexModel> CREATOR = new Parcelable.Creator<PinyinIndexModel>() {

		@Override
		public PinyinIndexModel createFromParcel(Parcel source) {
			return new PinyinIndexModel(source);
		}

		@Override
		public PinyinIndexModel[] newArray(int size) {
			return new PinyinIndexModel[size];
		}

	};

	private PinyinIndexModel(Parcel in) {
		id = in.readString();
		name = in.readString();
		pinyin = in.readString();
		shortPinyin = in.readString();
		firstLetter = in.readString();
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		PinyinIndexModel other = (PinyinIndexModel) obj;
		if (id != null && id.equals(other.id)
				&& name != null && name.equals(other.name)) {
			return true;
		}
		return false;
	}

}
