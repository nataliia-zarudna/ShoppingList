package com.nzarudna.shoppinglist.model.user;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * App user
 */
@Entity(tableName = "users")
public class User implements Parcelable {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "user_id")
    private UUID userID;

    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @NonNull
    private String name;

    @ColumnInfo(name = "invitor_name")
    private String invitorName;

    @ColumnInfo(name = "invitation_link")
    private String invitationLink;

    private String token;

    public User() {
        this.userID = UUID.randomUUID();
    }

    protected User(Parcel in) {
        phoneNumber = in.readString();
        name = in.readString();
        invitorName = in.readString();
        invitationLink = in.readString();
        token = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phoneNumber);
        dest.writeString(name);
        dest.writeString(invitorName);
        dest.writeString(invitationLink);
        dest.writeString(token);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @NonNull
    public UUID getUserID() {
        return userID;
    }

    public void setUserID(@NonNull UUID userID) {
        this.userID = userID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getInvitorName() {
        return invitorName;
    }

    public void setInvitorName(String invitorName) {
        this.invitorName = invitorName;
    }

    public String getInvitationLink() {
        return invitationLink;
    }

    public void setInvitationLink(String invitationLink) {
        this.invitationLink = invitationLink;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!userID.equals(user.userID)) return false;
        if (phoneNumber != null ? !phoneNumber.equals(user.phoneNumber) : user.phoneNumber != null)
            return false;
        if (!name.equals(user.name)) return false;
        if (invitorName != null ? !invitorName.equals(user.invitorName) : user.invitorName != null)
            return false;
        if (invitationLink != null ? !invitationLink.equals(user.invitationLink) : user.invitationLink != null)
            return false;
        return token != null ? token.equals(user.token) : user.token == null;
    }

    @Override
    public int hashCode() {
        int result = userID.hashCode();
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + name.hashCode();
        result = 31 * result + (invitorName != null ? invitorName.hashCode() : 0);
        result = 31 * result + (invitationLink != null ? invitationLink.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", name='" + name + '\'' +
                ", invitorName='" + invitorName + '\'' +
                ", invitationLink='" + invitationLink + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
