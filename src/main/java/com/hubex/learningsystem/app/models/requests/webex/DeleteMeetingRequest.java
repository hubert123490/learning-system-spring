package com.hubex.learningsystem.app.models.requests.webex;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteMeetingRequest {
    private String token;
    private String meetingId;
}
