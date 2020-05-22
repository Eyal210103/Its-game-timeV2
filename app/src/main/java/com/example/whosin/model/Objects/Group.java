package com.example.whosin.model.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Group implements Serializable, Parcelable {

    private String groupName;
    private String sports;
    private String image;
    private String id;

    public Group(String groupName, String sports) {
        this.groupName = groupName;
        this.sports = sports;
    }

    public Group() {
    }



    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSports() {
        return sports;
    }

    public void setSports(String sports) {
        this.sports = sports;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return groupName + "\n" +
                sports + "\n";
    }


    //Parcelable
    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.groupName);
        dest.writeString(this.id);
        dest.writeString(this.image);
        dest.writeString(this.image);
    }

    protected Group(Parcel in) {
        groupName = in.readString();
        sports = in.readString();
        image = in.readString();
        id = in.readString();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}
