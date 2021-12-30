package com.hubex.learningsystem.app.models.requests.webex;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateMeetingRequest {
    private String start;
    private String end;
    private String token;
   // private String password;
    //private List<MeetingInvite> invites;
   // private boolean sendEmail = true;
   // private String hostEmail;
}
