package com.example.whosin.model;

import com.example.whosin.model.Objects.ActiveMeeting;
import com.example.whosin.model.Objects.Group;

public class MeetingToGroup {
    private ActiveMeeting meeting;
    private Group group;
    public MeetingToGroup() {
    }

    public MeetingToGroup(ActiveMeeting meeting, Group group) {
        this.meeting = meeting;
        this.group = group;
    }

    public ActiveMeeting getMeeting() {
        return meeting;
    }

    public void setMeeting(ActiveMeeting meeting) {
        this.meeting = meeting;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "MeetingToGroup{" +
                "meeting=" + meeting.toString() +
                ", group=" + group.toString() +
                '}';
    }
}
