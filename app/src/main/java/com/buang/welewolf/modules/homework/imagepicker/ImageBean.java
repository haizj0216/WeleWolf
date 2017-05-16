package com.buang.welewolf.modules.homework.imagepicker;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageBean implements Parcelable {

	private String path = null;
	private boolean isSelected = false;

	public ImageBean(String path, boolean selected) {
		this.path = path;
		this.isSelected = selected;
	}

	public ImageBean() {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(path);
		dest.writeInt(isSelected ? 1 : 0);
	}

	public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {

		@Override
		public ImageBean createFromParcel(Parcel source) {
			return new ImageBean(source.readString(), source.readInt() == 1 ? true : false);
		}

		@Override
		public ImageBean[] newArray(int size) {
			return new ImageBean[size];
		}
	};

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param isSeleted the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
