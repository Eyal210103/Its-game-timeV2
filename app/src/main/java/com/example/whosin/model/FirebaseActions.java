package com.example.whosin.model;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.example.whosin.model.Listeners.ArrivalUserListener;
import com.example.whosin.model.Listeners.DataLoadListener;
import com.example.whosin.model.Listeners.GroupParticipantsLoadListener;
import com.example.whosin.model.Listeners.MeetingsLoadListener;
import com.example.whosin.model.Listeners.MessagesLoadListener;
import com.example.whosin.model.Listeners.UserMeetingsLoadListener;
import com.example.whosin.model.Objects.ActiveMeeting;
import com.example.whosin.model.Objects.Group;
import com.example.whosin.model.Objects.MessageChat;
import com.example.whosin.model.Objects.SingleMeeting;
import com.example.whosin.model.Objects.User;
import com.example.whosin.model.Singleton.CurrentUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class FirebaseActions {

    private static String TAG = "Firebase";
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static User thisUser = CurrentUser.getInstance();

    public static MutableLiveData<ArrayList<MutableLiveData<Group>>> loadUserGroups(Activity context) {
        final ArrayList<MutableLiveData<Group>> groupsObj = new ArrayList<>();
        final DataLoadListener dataLoadListener = (DataLoadListener) context;
        final MutableLiveData<ArrayList<MutableLiveData<Group>>> g = new MutableLiveData<>();
        final int before = 0;
        database.getReference().child("Users").child(thisUser.getId()).child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<String> groups = new ArrayList<String>();
                try {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String group = postSnapshot.getValue(String.class);
                            groups.add(group);
                    }
                    for (String id : groups) {
                        database.getReference().child("Groups").child(id).child("details").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (before > groups.size()){
                                    groups.clear();
                                    dataLoadListener.onGroupsLoaded();
                                }
                                Log.d(TAG, "onDataChange: "  +dataSnapshot +  dataSnapshot.getValue());
                                Group group = dataSnapshot.getValue(Group.class);
                                boolean isThere = false;
                                int i = 0;
                                for (MutableLiveData<Group> g : groupsObj) {
                                    try {
                                        if (g.getValue().getId().equals(group.getId())) {
                                            MutableLiveData<Group> mlvg = new MutableLiveData<>();
                                            mlvg.setValue(group);
                                            groupsObj.set(i, mlvg);
                                            dataLoadListener.onGroupsLoaded();
                                            isThere = true;
                                            break;
                                        }
                                        i++;
                                    }catch (Exception ignored){}
                                }
                                if (!isThere) {
                                    MutableLiveData<Group> mlvg = new MutableLiveData<>();
                                    mlvg.setValue(group);
                                    groupsObj.add(mlvg);
                                }
                                dataLoadListener.onGroupsLoaded();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        dataLoadListener.onGroupsLoaded();
                    }
                    g.setValue(groupsObj);
                    dataLoadListener.onGroupsLoaded();
                } catch (Exception ignored) {
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        g.setValue(groupsObj);
        return g;
    }

    public static MutableLiveData<ArrayList<MessageChat>> loadMessages(final Fragment context, Group group) {
        final ArrayList<MessageChat> messages = new ArrayList<MessageChat>();
        final MessagesLoadListener messagesLoadListener = (MessagesLoadListener) context;

        database.getReference().child("Groups").child(group.getId()).child("Chat").child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    messages.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        messages.add(ds.getValue(MessageChat.class));
                        messagesLoadListener.onMessagesLoaded();
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        MutableLiveData<ArrayList<MessageChat>> g = new MutableLiveData<>();
        g.setValue(messages);
        return g;
    }

    public static MutableLiveData<ArrayList<ActiveMeeting>> loadGroupMeeting(final Fragment context, String id) {
        final ArrayList<ActiveMeeting> meetings = new ArrayList<ActiveMeeting>();
        final MeetingsLoadListener meetingsLoadListener = (MeetingsLoadListener) context;
        MutableLiveData<ArrayList<ActiveMeeting>> m = new MutableLiveData<>();
        database.getReference().child("Groups").child(id).child("ActiveMeeting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        meetings.add(ds.getValue(ActiveMeeting.class));
                        meetingsLoadListener.onMeetingsLoaded();
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        m.setValue(meetings);
        return m;
    }

    public static void confirmUserArrival(Group group, ActiveMeeting meeting) {
        database.getReference().child("Groups").child(group.getId()).child("ActiveMeeting").child(meeting.getId()).child("HowComing").child(thisUser.getId()).setValue(thisUser.getId());
        database.getReference().child("Users").child(thisUser.getId()).child("ActiveMeeting").child(meeting.getId()).child("MeetingId").setValue(meeting.getId());
        database.getReference().child("Users").child(thisUser.getId()).child("ActiveMeeting").child(meeting.getId()).child("GroupId").setValue(group.getId());
    }

    public static MutableLiveData<ArrayList<MeetingToGroup>> loadUserMeetings(final Activity context) {
        final ArrayList<MeetingToGroup> meetings = new ArrayList<MeetingToGroup>();
        final UserMeetingsLoadListener userMeetingsLoadListener = (UserMeetingsLoadListener) context;
        MutableLiveData<ArrayList<MeetingToGroup>> m = new MutableLiveData<>();

        database.getReference().child("Users").child(thisUser.getId()).child("ActiveMeeting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    meetings.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        String groupId = ds.child("GroupId").getValue(String.class);
                        final String meetingId = ds.child("MeetingId").getValue(String.class);
                        database.getReference().child("Groups").child(groupId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final ActiveMeeting meeting = dataSnapshot.child("ActiveMeeting").child(meetingId).getValue(ActiveMeeting.class);
                                final Group group = dataSnapshot.child("details").getValue(Group.class);
                                boolean isThere = false;
                                for (MeetingToGroup am : meetings) {
                                    if (am.getMeeting().getId().equals(meeting.getId())){
                                        isThere = true;
                                    }
                                }
                                if (!isThere) {
                                    meetings.add(new MeetingToGroup(meeting, group));
                                    userMeetingsLoadListener.onUserMeetingsLoaded();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                    userMeetingsLoadListener.onUserMeetingsLoaded();
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        m.setValue(meetings);
        return m;
    }

    public static MutableLiveData<ArrayList<User>> loadConfirmUsers(Fragment context, Group group, ActiveMeeting meeting) {
        final ArrayList<String> usersId = new ArrayList<>();
        final ArrayList<User> users = new ArrayList<>();
        final ArrivalUserListener arrivalUserListener = (ArrivalUserListener) context;
        database.getReference().child("Groups").child(group.getId()).child("ActiveMeeting").child(meeting.getId()).child("HowComing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    usersId.clear();
                    users.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        usersId.add(ds.getValue(String.class));
                    }
                    for (String s : usersId) {
                        database.getReference().child("Users").child(s).child("details").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    users.add(dataSnapshot.getValue(User.class));
                                    arrivalUserListener.onUserLoaded();
                                } catch (Exception ignored) {
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        MutableLiveData<ArrayList<User>> u = new MutableLiveData<>();
        u.setValue(users);
        return u;
    }

    public static void sendMessage(Group group, String context) {
        MessageChat message = new MessageChat();
        message.setContext(context);
        message.setSender(thisUser.getFullName());
        message.setSenderID(thisUser.getEmail());
        message.setImageUrl(thisUser.getImageUri());
        Calendar calendar = Calendar.getInstance();
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        if (minutes < 10) {
            message.setHour("" + hour24hrs + ":0" + minutes);
        } else {
            message.setHour("" + hour24hrs + ":" + minutes);
        }
        database.getReference().child("Groups").child(group.getId()).child("Chat").child("Messages").push().setValue(message);
    }

    public static boolean leaveGroup(Group group) {
        return database.getReference().child("Users").child(thisUser.getId()).child("Groups").child(group.getId()).removeValue().isSuccessful() &&
                database.getReference().child("Groups").child(group.getId()).child("Members").child(thisUser.getId()).removeValue().isSuccessful();
    }

    public static MutableLiveData<ArrayList<User>> loadGroupsParticipants(Group group , final Fragment context){
        final ArrayList<User> users = new ArrayList<>();
        final GroupParticipantsLoadListener groupParticipantsLoadListener = (GroupParticipantsLoadListener)context;
        final MutableLiveData<ArrayList<User>> u = new MutableLiveData<>();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> ids = new ArrayList<String>();
                try {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        ids.add(postSnapshot.getValue(String.class));
                    }
                    for (String id : ids) {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("details").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                users.add(dataSnapshot.getValue(User.class));
                                Log.d("GET DATA", "+++++++++++++++++++++++++++++++++++++++++++++" + users.toString());
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    groupParticipantsLoadListener.onGroupParticipantsLoaded();
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        FirebaseDatabase.getInstance().getReference().child("Groups").child(group.getId()).child("Members").addValueEventListener(valueEventListener);
        u.setValue(users);
        Log.d("GET DATA", "+++++++++++++++++++++++++++++++++++++++++++++" + users.toString());
        return u;
    }

    public static MutableLiveData<ArrayList<SingleMeeting>>loadAllSingleMeetings(Fragment context){
        final ArrayList<SingleMeeting> meetings = new ArrayList<>();
        final MutableLiveData<ArrayList<SingleMeeting>> u = new MutableLiveData<>();
        final DataLoadListener dataLoadListener = (DataLoadListener)context;
        database.getReference().child("Meetings").child("SingleMeetings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    meetings.add(ds.getValue(SingleMeeting.class));
                }
                dataLoadListener.onGroupsLoaded();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        u.setValue(meetings);
        return  u;
    }

    public  static MutableLiveData<Group> getGroupById(String id, Fragment context){
        final MutableLiveData<Group> g = new MutableLiveData<>();
        final DataLoadListener dataLoadListener = (DataLoadListener)context;
        database.getReference().child("Groups").child(id).child("details").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                g.setValue(dataSnapshot.getValue(Group.class));
                dataLoadListener.onGroupsLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return g;
    }
}
